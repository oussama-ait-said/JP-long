const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_kotoba_123";
const BOB_UID = "bob_kotoba_456";

const [emulatorHost, emulatorPortStr] = (
  process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085"
).split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

function pastDate(offsetMs = 2000) {
  return new Date(Date.now() - offsetMs);
}

test("Unauthenticated user cannot read or write any Kotoba collection", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  const now = pastDate();

  await assertFails(unauthDb.collection("users").doc(ALICE_UID).get());
  await assertFails(unauthDb.collection("users").get());
  await assertFails(
    unauthDb.collection("users").doc(ALICE_UID).set({
      userId: ALICE_UID,
      displayName: "Unauth",
      targetLevel: "N5",
      dailyGoal: 10,
      xp: 0,
      streakDays: 0,
      lastStudyDay: "",
      completedLessonIds: [],
      createdAt: now,
      updatedAt: now,
    })
  );
  await assertFails(
    unauthDb.collection("users").doc(ALICE_UID).collection("flashcards").get()
  );
  await assertFails(
    unauthDb.collection("users").doc(ALICE_UID).collection("quiz_results").get()
  );
});

test("Authenticated owner: valid UserProfile create & partial update succeed; tampering fails", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const created = pastDate(10000);
  const profileRef = aliceDb.collection("users").doc(ALICE_UID);

  await assertSucceeds(
    profileRef.set({
      userId: ALICE_UID,
      displayName: "أسامة",
      targetLevel: "N5",
      dailyGoal: 10,
      xp: 0,
      streakDays: 1,
      lastStudyDay: "2026-11-18",
      completedLessonIds: [],
      createdAt: created,
      updatedAt: created,
    })
  );

  await assertSucceeds(profileRef.get());

  // Valid partial update (lesson completion + XP + streak)
  await assertSucceeds(
    profileRef.update({
      xp: 50,
      completedLessonIds: ["kana_hiragana_1"],
      updatedAt: pastDate(500),
    })
  );

  // Reject invalid targetLevel enum
  await assertFails(
    profileRef.update({
      targetLevel: "N1",
      updatedAt: pastDate(300),
    })
  );

  // Reject immutable createdAt modification
  await assertFails(
    profileRef.update({
      createdAt: pastDate(100),
      updatedAt: pastDate(100),
    })
  );

  // Reject future timestamp
  await assertFails(
    profileRef.update({
      xp: 999,
      updatedAt: new Date(Date.now() + 60000),
    })
  );

  // Reject ghost field on update
  await assertFails(
    profileRef.update({
      isAdmin: true,
      updatedAt: pastDate(100),
    })
  );

  // Reject listing /users root collection
  await assertFails(aliceDb.collection("users").get());
});

test("Cross-user isolation: Bob cannot read or mutate Alice's profile, flashcards, or quiz results", async () => {
  const created = pastDate(10000);
  await testEnv.withSecurityRulesDisabled(async (ctx) => {
    const adminDb = ctx.firestore();
    await adminDb.collection("users").doc(ALICE_UID).set({
      userId: ALICE_UID,
      displayName: "Alice",
      targetLevel: "N5",
      dailyGoal: 10,
      xp: 120,
      streakDays: 3,
      lastStudyDay: "2026-11-18",
      completedLessonIds: ["kana_hiragana_1"],
      createdAt: created,
      updatedAt: created,
    });
    await adminDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("flashcards")
      .doc("vocab_konnichiwa")
      .set({
        userId: ALICE_UID,
        itemId: "vocab_konnichiwa",
        japanese: "こんにちは",
        reading: "こんにちは",
        romaji: "konnichiwa",
        arabicMeaning: "مرحباً (تحية النهار)",
        level: "N5",
        box: 1,
        intervalDays: 1,
        easeFactor: 2.5,
        nextReviewAtMs: Date.now() + 86400000,
        reviewCount: 1,
        createdAt: created,
        updatedAt: created,
      });
    await adminDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("quiz_results")
      .doc("quiz_1")
      .set({
        userId: ALICE_UID,
        level: "N5",
        score: 8,
        totalQuestions: 10,
        xpEarned: 80,
        createdAt: created,
        updatedAt: created,
      });
  });

  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  await assertFails(bobDb.collection("users").doc(ALICE_UID).get());
  await assertFails(
    bobDb.collection("users").doc(ALICE_UID).update({ xp: 9999 })
  );
  await assertFails(
    bobDb.collection("users").doc(ALICE_UID).collection("flashcards").get()
  );
  await assertFails(
    bobDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("flashcards")
      .doc("vocab_konnichiwa")
      .get()
  );
  await assertFails(
    bobDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("flashcards")
      .doc("vocab_konnichiwa")
      .delete()
  );
  await assertFails(
    bobDb.collection("users").doc(ALICE_UID).collection("quiz_results").get()
  );
});

test("Flashcards SRS: owner CRUD, list on empty & populated, partial SRS update, and validation bounds", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const cardsCol = aliceDb
    .collection("users")
    .doc(ALICE_UID)
    .collection("flashcards");

  // List on empty subcollection must succeed
  await assertSucceeds(cardsCol.get());

  const created = pastDate(8000);
  const cardRef = cardsCol.doc("n5_taberu");

  await assertSucceeds(
    cardRef.set({
      userId: ALICE_UID,
      itemId: "n5_taberu",
      japanese: "食べる",
      reading: "たべる",
      romaji: "taberu",
      arabicMeaning: "يأكل",
      level: "N5",
      box: 0,
      intervalDays: 0,
      easeFactor: 2.5,
      nextReviewAtMs: Date.now(),
      reviewCount: 0,
      createdAt: created,
      updatedAt: created,
    })
  );

  // List populated subcollection
  await assertSucceeds(cardsCol.get());

  // Valid SRS review update
  await assertSucceeds(
    cardRef.update({
      box: 2,
      intervalDays: 6,
      easeFactor: 2.6,
      nextReviewAtMs: Date.now() + 6 * 86400000,
      reviewCount: 2,
      updatedAt: pastDate(500),
    })
  );

  // Reject including `@DocumentId` 'id' key on create
  await assertFails(
    cardsCol.doc("n5_nomu").set({
      id: "n5_nomu",
      userId: ALICE_UID,
      itemId: "n5_nomu",
      japanese: "飲む",
      reading: "のむ",
      romaji: "nomu",
      arabicMeaning: "يشرب",
      level: "N5",
      box: 0,
      intervalDays: 0,
      easeFactor: 2.5,
      nextReviewAtMs: Date.now(),
      reviewCount: 0,
      createdAt: created,
      updatedAt: created,
    })
  );

  // Reject mutating itemId on update
  await assertFails(
    cardRef.update({
      itemId: "other_id",
      updatedAt: pastDate(200),
    })
  );

  // Owner delete succeeds
  await assertSucceeds(cardRef.delete());
});

test("QuizResults: append-only create & list succeed; update fails; invalid score fails", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const quizCol = aliceDb
    .collection("users")
    .doc(ALICE_UID)
    .collection("quiz_results");
  const created = pastDate(5000);

  await assertSucceeds(quizCol.get());

  const quizRef = quizCol.doc("attempt_001");
  await assertSucceeds(
    quizRef.set({
      userId: ALICE_UID,
      level: "N3",
      score: 9,
      totalQuestions: 10,
      xpEarned: 110,
      createdAt: created,
      updatedAt: created,
    })
  );

  await assertSucceeds(quizCol.get());

  // Quiz attempts are immutable
  await assertFails(
    quizRef.update({
      score: 10,
      updatedAt: pastDate(200),
    })
  );

  // Score cannot exceed totalQuestions
  await assertFails(
    quizCol.doc("attempt_bad").set({
      userId: ALICE_UID,
      level: "N3",
      score: 12,
      totalQuestions: 10,
      xpEarned: 110,
      createdAt: created,
      updatedAt: created,
    })
  );
});
