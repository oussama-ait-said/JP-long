package com.example.data.curriculum

import com.example.data.model.JlptLevel

object CurriculumN5Data {

  val lessons: List<Lesson> = listOf(
    Lesson(
      id = "n5_greetings",
      order = 1,
      level = JlptLevel.N5,
      titleArabic = "التحيات والتعارف الأول",
      subtitleArabic = "كيف تحيّي باليابانية وتقدّم نفسك بلباقة",
      xpReward = 40L,
      grammarPoints = listOf(
        GrammarPoint(
          id = "g_n5_desu",
          pattern = "A は B です",
          romaji = "A wa B desu",
          arabicTitle = "الجملة الاسمية (A هو B)",
          explanationArabic = "تُستخدم أداة الموضوع «は» (تُنطق wa رغم كتابتها ha) لتحديد موضوع الحديث، وتنتهي الجملة بـ «です» لإضافة معنى الكينونة والأدب.",
          formulaArabic = "الاسم ١ + は + الاسم ٢ + です",
          examples = listOf(
            GrammarExample("わたしは アフマド です。", "わたしは あふまど です。", "Watashi wa Afumado desu.", "أنا أحمد."),
            GrammarExample("これは ほん です。", "これは ほん です。", "Kore wa hon desu.", "هذا كتاب."),
          ),
          level = JlptLevel.N5,
        ),
        GrammarPoint(
          id = "g_n5_ka",
          pattern = "〜ですか",
          romaji = "...desu ka",
          arabicTitle = "تحويل الجملة إلى سؤال",
          explanationArabic = "ببساطة أضف «か» في نهاية الجملة المذيلة بـ «です» لتتحول إلى سؤال رسمي مهذّب دون تغيير ترتيب الكلمات.",
          formulaArabic = "الجملة + です + か",
          examples = listOf(
            GrammarExample("おげんき ですか。", "おげんき ですか。", "O-genki desu ka.", "هل أنت بخير؟"),
            GrammarExample("にほんじん ですか。", "にほんじん ですか。", "Nihonjin desu ka.", "هل أنت ياباني؟"),
          ),
          level = JlptLevel.N5,
        ),
      ),
      vocabulary = listOf(
        VocabItem("v_n5_ohayou", "おはようございます", "おはようございます", "ohayō gozaimasu", "صباح الخير (رسمي)", "おはようございます、先生。", "おはようございます、せんせい。", "صباح الخير يا أستاذ.", JlptLevel.N5),
        VocabItem("v_n5_konnichiwa", "こんにちは", "こんにちは", "konnichiwa", "مرحباً (تحية النهار)", "こんにちは、お元気ですか。", "こんにちは、おげんきですか。", "مرحباً، كيف حالك؟", JlptLevel.N5),
        VocabItem("v_n5_arigatou", "ありがとうございます", "ありがとうございます", "arigatō gozaimasu", "شكراً جزيلاً", "てつだってくれて ありがとうございます。", "てつだってくれて ありがとうございます。", "شكراً جزيلاً لمساعدتك.", JlptLevel.N5),
        VocabItem("v_n5_sumimasen", "すみません", "すみません", "sumimasen", "عذراً / لو سمحت", "すみません、えきは どこですか。", "すみません、えきは どこですか。", "عفواً، أين المحطة؟", JlptLevel.N5),
        VocabItem("v_n5_watashi", "私", "わたし", "watashi", "أنا", "私は 学生 です。", "わたしは がくせい です。", "أنا طالب.", JlptLevel.N5),
      ),
      kanji = listOf(
        KanjiItem("k_n5_nichi", "日", "ニチ・ジツ", "ひ・か", "يوم / شمس", 4, "日本", "にほん", "اليابان", JlptLevel.N5),
        KanjiItem("k_n5_hon", "本", "ホン", "もと", "كتاب / أصل", 5, "本", "ほん", "كتاب", JlptLevel.N5),
        KanjiItem("k_n5_hito", "人", "ジン・ニン", "ひと", "شخص / إنسان", 2, "日本人", "にほんじん", "شخص ياباني", JlptLevel.N5),
      ),
    ),
    Lesson(
      id = "n5_daily_verbs",
      order = 2,
      level = JlptLevel.N5,
      titleArabic = "الأفعال اليومية وأداة المفعول を",
      subtitleArabic = "التعبير عن الأكل والشرب والدراسة بصيغة ます",
      xpReward = 50L,
      grammarPoints = listOf(
        GrammarPoint(
          id = "g_n5_wo",
          pattern = "الاسم + を + الفعل ます",
          romaji = "...o ...masu",
          arabicTitle = "أداة المفعول به المباشر «を»",
          explanationArabic = "تُوضع الأداة «を» (تُنطق o) بعد الاسم الذي يقع عليه الفعل مباشرةً، ويأتي الفعل دائماً في نهاية الجملة اليابانية بصيغة «ます» المهذّبة.",
          formulaArabic = "المفعول به + を + الفعل + ます / ません",
          examples = listOf(
            GrammarExample("みずを のみます。", "みずを のみます。", "Mizu o nomimasu.", "أشرب الماء."),
            GrammarExample("にほんごを べんきょうします。", "にほんごを べんきょうします。", "Nihongo o benkyō shimasu.", "أدرس اللغة اليابانية."),
          ),
          level = JlptLevel.N5,
        ),
        GrammarPoint(
          id = "g_n5_de",
          pattern = "المكان + で + الفعل",
          romaji = "...de ...",
          arabicTitle = "تحديد مكان وقوع الحدث «で»",
          explanationArabic = "عندما تصف مكاناً يتمّ فيه نشاط أو فعل معيّن (مثل الأكل في المطعم أو الدراسة في المكتبة)، نستخدم الأداة «で» بعد اسم المكان.",
          formulaArabic = "المكان + で + المفعول + を + الفعل",
          examples = listOf(
            GrammarExample("としょかんで 本を よみます。", "としょかんで ほんを よみます。", "Toshokan de hon o yomimasu.", "أقرأ كتاباً في المكتبة."),
          ),
          level = JlptLevel.N5,
        ),
      ),
      vocabulary = listOf(
        VocabItem("v_n5_taberu", "食べる", "たべる", "taberu", "يأكل", "朝ごはんを 食べます。", "あさごはんを たべます。", "أتناول وجبة الإفطار.", JlptLevel.N5),
        VocabItem("v_n5_nomu", "飲む", "のむ", "nomu", "يشرب", "コーヒーを 飲みます。", "こーひーを のみます。", "أشرب القهوة.", JlptLevel.N5),
        VocabItem("v_n5_yomu", "読む", "よむ", "yomu", "يقرأ", "まいにち 新聞を 読みます。", "まいにち しんぶんを よみます。", "أقرأ الجريدة كل يوم.", JlptLevel.N5),
        VocabItem("v_n5_kaku", "書く", "かく", "kaku", "يكتب", "ひらがなを 書きます。", "ひらがなを かきます。", "أكتب الهيراغانا.", JlptLevel.N5),
        VocabItem("v_n5_iku", "行く", "いく", "iku", "يذهب", "あした 学校へ 行きます。", "あした がっこうへ いきます。", "سأذهب غداً إلى المدرسة.", JlptLevel.N5),
      ),
      kanji = listOf(
        KanjiItem("k_n5_shoku", "食", "ショク", "た(べる)", "طعام / يأكل", 9, "食べ物", "たべもの", "طعام", JlptLevel.N5),
        KanjiItem("k_n5_mizu", "水", "スイ", "みず", "ماء", 4, "水曜日", "すいようび", "الأربعاء", JlptLevel.N5),
        KanjiItem("k_n5_gaku", "学", "ガク", "まな(ぶ)", "تعلّم / علم", 8, "学生", "がくせい", "طالب", JlptLevel.N5),
      ),
    ),
    Lesson(
      id = "n5_adjectives",
      order = 3,
      level = JlptLevel.N5,
      titleArabic = "الصفات (い و な) ووصف الأشياء",
      subtitleArabic = "الفرق بين نوعَي الصفات وكيفية نفيهما",
      xpReward = 50L,
      grammarPoints = listOf(
        GrammarPoint(
          id = "g_n5_i_adj",
          pattern = "صفة い (〜いです / 〜くないです)",
          romaji = "...i desu / ...kunai desu",
          arabicTitle = "صفات الـ い ونفيها",
          explanationArabic = "الصفات التي تنتهي بحرف «い» تُثبت بـ «です»، وعند النفي نحذف «い» الأخيرة ونضع «くないです» (استثناء: いい تصبح よくないです).",
          formulaArabic = "الإثبات: صفة い + です  |  النفي: الجذر + くないです",
          examples = listOf(
            GrammarExample("この ラーメンは おいしいです。", "この らーめんは おいしいです。", "Kono rāmen wa oishii desu.", "هذا الرامن لذيذ."),
            GrammarExample("きょうは さむくないです。", "きょうは さむくないです。", "Kyō wa samukunai desu.", "اليوم ليس بارداً."),
          ),
          level = JlptLevel.N5,
        ),
        GrammarPoint(
          id = "g_n5_na_adj",
          pattern = "صفة な + な + الاسم",
          romaji = "...na + Noun",
          arabicTitle = "صفات الـ な عند وصف الاسم",
          explanationArabic = "صفات الـ な (مثل しずか هادئ، きれい جميل) لا تأتي بحرف «な» في نهاية الجملة مع «です»، لكنها تحتاج إلى «な» عندما تصف اسماً بعدها مباشرة.",
          formulaArabic = "صفة な + な + الاسم",
          examples = listOf(
            GrammarExample("きょうとは きれいな まちです。", "きょうとは きれいな まちです。", "Kyōto wa kirei na machi desu.", "كيوتو مدينة جميلة."),
          ),
          level = JlptLevel.N5,
        ),
      ),
      vocabulary = listOf(
        VocabItem("v_n5_ookii", "大きい", "おおきい", "ōkii", "كبير", "大きい 家に すんでいます。", "おおきい いえに すんでいます。", "أعيش في بيت كبير.", JlptLevel.N5),
        VocabItem("v_n5_chiisai", "小さい", "ちいさい", "chiisai", "صغير", "小さい ねこが います。", "ちいさい ねこが います。", "يوجد قط صغير.", JlptLevel.N5),
        VocabItem("v_n5_atarashii", "新しい", "あたらしい", "atarashii", "جديد", "新しい 言葉を おぼえます。", "新しい ことばを おぼえます。", "أحفظ كلمات جديدة.", JlptLevel.N5),
        VocabItem("v_n5_shizuka", "静か", "しずか", "shizuka", "هادئ (صفة な)", "この 公園は 静かです。", "この こうえんは しずかです。", "هذه الحديقة هادئة.", JlptLevel.N5),
        VocabItem("v_n5_kirei", "綺麗", "きれい", "kirei", "جميل / نظيف (صفة な)", "富士山は とても きれいです。", "ふじさんは とても きれいです。", "جبل فوجي جميل جداً.", JlptLevel.N5),
      ),
      kanji = listOf(
        KanjiItem("k_n5_dai", "大", "ダイ・タイ", "おお(きい)", "كبير", 3, "大学", "だいがく", "جامعة", JlptLevel.N5),
        KanjiItem("k_n5_shou", "小", "ショウ", "ちい(さい)", "صغير", 3, "小学校", "しょうがっこう", "مدرسة ابتدائية", JlptLevel.N5),
        KanjiItem("k_n5_yama", "山", "サン", "やま", "جبل", 3, "富士山", "ふじさん", "جبل فوجي", JlptLevel.N5),
      ),
    ),
    Lesson(
      id = "n5_time_existence",
      order = 4,
      level = JlptLevel.N5,
      titleArabic = "الوقت والوجود (あります و います)",
      subtitleArabic = "التفريق بين وجود الأشياء والكائنات الحية",
      xpReward = 55L,
      grammarPoints = listOf(
        GrammarPoint(
          id = "g_n5_arimasu_imasu",
          pattern = "المكان + に + الشيء/الشخص + が + あります / います",
          romaji = "...ni ...ga arimasu / imasu",
          arabicTitle = "أفعال الوجود: あります للجماد و います للأحياء",
          explanationArabic = "نستخدم «います» مع البشر والحيوانات (الكائنات التي تتحرك بإرادتها)، بينما نستخدم «あります» مع الجمادات والنباتات والأفكار والمواعيد.",
          formulaArabic = "المكان + に + الفاعل + が + あります / います",
          examples = listOf(
            GrammarExample("つくえの うえに 本が あります。", "つくえの うえに ほんが あります。", "Tsukue no ue ni hon ga arimasu.", "يوجد كتاب فوق الطاولة."),
            GrammarExample("へやに いぬが います。", "へやに いぬが います。", "Heya ni inu ga imasu.", "يوجد كلب في الغرفة."),
          ),
          level = JlptLevel.N5,
        ),
      ),
      vocabulary = listOf(
        VocabItem("v_n5_ima", "今", "いま", "ima", "الآن", "今 何時ですか。", "いま なんじですか。", "كم الساعة الآن؟", JlptLevel.N5),
        VocabItem("v_n5_kyou", "今日", "きょう", "kyō", "اليوم", "今日は いい 天気ですね。", "きょうは いい てんきですね。", "الطقس جميل اليوم، أليس كذلك؟", JlptLevel.N5),
        VocabItem("v_n5_ashita", "明日", "あした", "ashita", "غداً", "明日 テストが あります。", "あした てすとが あります。", "يوجد اختبار غداً.", JlptLevel.N5),
        VocabItem("v_n5_eki", "駅", "えき", "eki", "محطة قطار", "駅の 前に 店が あります。", "えきの まえに みせが あります。", "يوجد متجر أمام المحطة.", JlptLevel.N5),
      ),
      kanji = listOf(
        KanjiItem("k_n5_ji", "時", "ジ", "とき", "وقت / ساعة", 10, "時間", "じかん", "وقت / مدة", JlptLevel.N5),
        KanjiItem("k_n5_tsuki", "月", "ゲツ・ガツ", "つき", "قمر / شهر", 4, "一月", "いちがつ", "يناير", JlptLevel.N5),
        KanjiItem("k_n5_kawa", "川", "セン", "かわ", "نهر", 3, "川", "かわ", "نهر", JlptLevel.N5),
      ),
    ),
  )
}
