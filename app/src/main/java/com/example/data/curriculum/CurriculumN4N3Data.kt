package com.example.data.curriculum

import com.example.data.model.JlptLevel

object CurriculumN4N3Data {

  val n4Lessons: List<Lesson> = listOf(
    Lesson(
      id = "n4_te_form",
      order = 1,
      level = JlptLevel.N4,
      titleArabic = "صيغة الـ て والطلبات المهذبة",
      subtitleArabic = "أساس ربط الأفعال وطلب المساعدة (〜てください)",
      xpReward = 65L,
      grammarPoints = listOf(
        GrammarPoint(
          id = "g_n4_te_kudasai",
          pattern = "الفعل (て形) + ください",
          romaji = "...te kudasai",
          arabicTitle = "الطلب المهذب: «من فضلك افعل...»",
          explanationArabic = "صيغة الـ て (Te-form) هي حجر الزاوية في قواعد N4؛ عند دمجها مع «ください» تعني «من فضلك افعل كذا»، ومع «います» تعبّر عن الفعل المستمر الآن.",
          formulaArabic = "الفعل بصيغة て + ください",
          examples = listOf(
            GrammarExample("ここに 名前を 書いて ください。", "ここに なまえを かいて ください。", "Koko ni namae o kaite kudasai.", "من فضلك اكتب اسمك هنا."),
            GrammarExample("今 日本語を 勉強して います。", "いま にほんごを べんきょうして います。", "Ima nihongo o benkyō shite imasu.", "أنا أدرس اليابانية الآن."),
          ),
          level = JlptLevel.N4,
        ),
        GrammarPoint(
          id = "g_n4_te_wa_ikemasen",
          pattern = "الفعل (て形) + は いけません",
          romaji = "...te wa ikemasen",
          arabicTitle = "المنع والتحريم: «ممنوع / لا يجوز»",
          explanationArabic = "للتعبير عن قاعدة تمنع القيام بفعل ما، نلحق «は いけません» بصيغة الـ て للفعل.",
          formulaArabic = "الفعل بصيغة て + は いけません",
          examples = listOf(
            GrammarExample("ここで しゃしんを とっては いけません。", "ここで しゃしんを とっては いけません。", "Koko de shashin o totte wa ikemasen.", "ممنوع التقاط الصور هنا."),
          ),
          level = JlptLevel.N4,
        ),
      ),
      vocabulary = listOf(
        VocabItem("v_n4_tetsudau", "手伝う", "てつだう", "tetsudau", "يساعد", "しごとを 手伝って ください。", "しごとを てつだって ください。", "من فضلك ساعدني في العمل.", JlptLevel.N4),
        VocabItem("v_n4_oshieru", "教える", "おしえる", "oshieru", "يعلّم / يخبر", "駅への 道を 教えて ください。", "えきへの みちを おしえて ください。", "من فضلك أخبرني بالطريق إلى المحطة.", JlptLevel.N4),
        VocabItem("v_n4_tsukau", "使う", "つかう", "tsukau", "يستخدم", "はしを 使って 食べます。", "はしを つかって たべます。", "آكل باستخدام عيدان الطعام.", JlptLevel.N4),
        VocabItem("v_n4_oboeru", "覚える", "おぼえる", "oboeru", "يحفظ / يتذكّر", "毎日 漢字を 覚えます。", "まいにち かんじを おぼえます。", "أحفظ الكانجي كل يوم.", JlptLevel.N4),
      ),
      kanji = listOf(
        KanjiItem("k_n4_kaisha", "社", "シャ", "やしろ", "شركة / معبد", 7, "会社", "かいしゃ", "شركة", JlptLevel.N4),
        KanjiItem("k_n4_den", "電", "デン", "—", "كهرباء", 13, "電車", "でんしゃ", "قطار كهربائي", JlptLevel.N4),
        KanjiItem("k_n4_eki", "駅", "エキ", "—", "محطة", 14, "東京駅", "とうきょうえき", "محطة طوكيو", JlptLevel.N4),
      ),
    ),
    Lesson(
      id = "n4_potential_desire",
      order = 2,
      level = JlptLevel.N4,
      titleArabic = "الإمكانية، الرغبة، والتجربة",
      subtitleArabic = "التعبير عمّا تستطيع فعله وما تتمنّى تجربته",
      xpReward = 70L,
      grammarPoints = listOf(
        GrammarPoint(
          id = "g_n4_koto_ga_dekiru",
          pattern = "الفعل (قاموس) + ことが できます",
          romaji = "...koto ga dekimasu",
          arabicTitle = "التعبير عن الاستطاعة والقدرة",
          explanationArabic = "بإضافة «ことが できます» بعد صيغة القاموس للفعل، نعبر عن القدرة أو المهارة في فعل شيء (مثل التحدث بلغة أو السباحة).",
          formulaArabic = "الفعل (المصدر) + ことが できます",
          examples = listOf(
            GrammarExample("私は 漢字を 読む ことが できます。", "わたしは かんじを よむ ことが できます。", "Watashi wa kanji o yomu koto ga dekimasu.", "أستطيع قراءة الكانجي."),
          ),
          level = JlptLevel.N4,
        ),
        GrammarPoint(
          id = "g_n4_ta_koto_ga_aru",
          pattern = "الفعل (た形) + ことが あります",
          romaji = "...ta koto ga arimasu",
          arabicTitle = "الحديث عن تجربة سابقة (سبق لي أن...)",
          explanationArabic = "تُستخدم الصيغة الماضية البسيطة (た形) متبوعة بـ «ことが あります» للقول إنك جرّبت هذا الشيء ولو لمرة واحدة في حياتك.",
          formulaArabic = "الفعل (الماضي البسيط た) + ことが あります",
          examples = listOf(
            GrammarExample("日本へ 行った ことが あります。", "にほんへ いった ことが あります。", "Nihon e itta koto ga arimasu.", "سبق لي أن سافرت إلى اليابان."),
          ),
          level = JlptLevel.N4,
        ),
      ),
      vocabulary = listOf(
        VocabItem("v_n4_ryokou", "旅行", "りょこう", "ryokō", "سفر / رحلة", "来年 日本へ 旅行したいです。", "らいねん にほんへ りょこうしたいです。", "أرغب في السفر إلى اليابان العام القادم.", JlptLevel.N4),
        VocabItem("v_n4_keiken", "経験", "けいけん", "keiken", "خبرة / تجربة", "いい 経験に なりました。", "いい けいけんに なりました。", "كانت تجربة رائعة.", JlptLevel.N4),
        VocabItem("v_n4_yume", "夢", "ゆめ", "yume", "حلم", "私の 夢は 日本で 働く ことです。", "わたしの ゆめは にほんで はたらく ことです。", "حلمي هو العمل في اليابان.", JlptLevel.N4),
        VocabItem("v_n4_isogashii", "忙しい", "いそがしい", "isogashii", "مشغول", "今週は とても 忙しいです。", "こんしゅうは とても いそがしいです。", "أنا مشغول جداً هذا الأسبوع.", JlptLevel.N4),
      ),
      kanji = listOf(
        KanjiItem("k_n4_tabi", "旅", "リョ", "たび", "سفر / رحلة", 10, "旅行", "りょこう", "سفر", JlptLevel.N4),
        KanjiItem("k_n4_ben", "勉", "ベン", "つと(める)", "اجتهاد", 10, "勉強", "べんきょう", "دراسة", JlptLevel.N4),
        KanjiItem("k_n4_omou", "思", "シ", "おも(う)", "يفكّر / يظن", 9, "思い出す", "おもいだす", "يتذكّر", JlptLevel.N4),
      ),
    ),
  )

  val n3Lessons: List<Lesson> = listOf(
    Lesson(
      id = "n3_reason_purpose",
      order = 1,
      level = JlptLevel.N3,
      titleArabic = "الغاية والسبب في مستوى N3",
      subtitleArabic = "استخدام 〜ために و 〜ように و 〜おかげで",
      xpReward = 85L,
      grammarPoints = listOf(
        GrammarPoint(
          id = "g_n3_tame_ni",
          pattern = "الفعل (قاموس) / الاسم + の + ために",
          romaji = "...tame ni",
          arabicTitle = "التعبير عن الهدف المباشر «من أجل»",
          explanationArabic = "تُستخدم «ために» لربط فعل إرادي بهدف واضح ومقصود، وتُستعمل مع صيغة القاموس للأفعال الإرادية أو بعد الاسم مع «の».",
          formulaArabic = "فعل إرادي (قاموس) + ために  |  الاسم + の + ために",
          examples = listOf(
            GrammarExample("日本語が 上手に なる ために、毎日 話します。", "にほんごが じょうずに なる ために、まいにち はなします。", "Nihongo ga jōzu ni naru tame ni, mainichi hanashimasu.", "من أجل أن أتقن اليابانية، أتحدث بها كل يوم."),
            GrammarExample("健康の ために 運動して います。", "けんこうの ために うんどうして います。", "Kenkō no tame ni undō shite imasu.", "أمارس الرياضة من أجل صحتي."),
          ),
          level = JlptLevel.N3,
        ),
        GrammarPoint(
          id = "g_n3_okagede",
          pattern = "〜おかげで / 〜せいで",
          romaji = "...okage de / ...sei de",
          arabicTitle = "الشكر على السبب (おかげで) واللوم (せいで)",
          explanationArabic = "كلاهما يعني «بسبب»، لكن «おかげで» تُستخدم حصراً للنتائج الإيجابية بنبرة امتنان («بفضل...»)، بينما «せいで» تُستخدم للنتائج السلبية أو اللوم.",
          formulaArabic = "الفعل (عادي) / الصفة + おかげで (إيجابي) أو せいで (سلبي)",
          examples = listOf(
            GrammarExample("先生の おかげで N3に 合格しました。", "せんせいの おかげで えぬさんに ごうかくしました。", "Sensei no okage de N3 ni gōkaku shimashita.", "بفضل الأستاذ، نجحتُ في اختبار N3."),
          ),
          level = JlptLevel.N3,
        ),
      ),
      vocabulary = listOf(
        VocabItem("v_n3_goukaku", "合格", "ごうかく", "gōkaku", "نجاح في اختبار", "JLPTに 合格したいです。", "じぇーえるぴーてぃーに ごうかくしたいです。", "أريد النجاح في اختبار JLPT.", JlptLevel.N3),
        VocabItem("v_n3_doryoku", "努力", "どりょく", "doryoku", "بذل الجهد", "毎日の 努力が 大切です。", "まいにちの どりょくが たいせつです。", "الجهد اليومي مهم للغاية.", JlptLevel.N3),
        VocabItem("v_n3_hitsuyou", "必要", "ひつよう", "hitsuyō", "ضروري / لازم", "新しい 計画が 必要です。", "あたらしい けいかくが ひつようです。", "نحتاج إلى خطة جديدة.", JlptLevel.N3),
        VocabItem("v_n3_kankei", "関係", "かんけい", "kankei", "علاقة / صلة", "二つの 言葉には 深い 関係が あります。", "ふたつの ことばには ふかい かんけいが あります。", "توجد علاقة عميقة بين الكلمتين.", JlptLevel.N3),
      ),
      kanji = listOf(
        KanjiItem("k_n3_doryoku", "努", "ド", "つと(める)", "يبذل جهداً", 7, "努力", "どりょく", "جهد / مثابرة", JlptLevel.N3),
        KanjiItem("k_n3_hitsuyou", "必", "ヒツ", "かなら(ず)", "حتمي / ضروري", 5, "必要", "ひつよう", "ضروري", JlptLevel.N3),
        KanjiItem("k_n3_kimeru", "決", "ケツ", "き(める)", "يقرّر", 7, "決定", "けってい", "قرار", JlptLevel.N3),
      ),
    ),
    Lesson(
      id = "n3_conditionals_passive",
      order = 2,
      level = JlptLevel.N3,
      titleArabic = "الشرط المتقدم والمبني للمجهول",
      subtitleArabic = "التفريق بين 〜ば و 〜たら وفهم صيغة المبني للمجهول",
      xpReward = 90L,
      grammarPoints = listOf(
        GrammarPoint(
          id = "g_n3_ba_tara",
          pattern = "〜ば vs 〜たら",
          romaji = "...ba vs ...tara",
          arabicTitle = "أسلوب الشرط (إذا / لو)",
          explanationArabic = "صيغة «ば» تركز على الشرط العام والمنطقي (إذا تحقّق الشرط الفلاني تحدُث النتيجة تلقائياً)، بينما «たら» مرنة جداً وتصلح للمواقف اليومية والزمنية («عندما / إذا»).",
          formulaArabic = "فعل (جذر + えば)  |  فعل ماضي (た) + ら",
          examples = listOf(
            GrammarExample("毎日 練習すれば、上手に なります。", "まいにち れんしゅうすれば、じょうずに なります。", "Mainichi renshū sureba, jōzu ni narimasu.", "إذا تدرّبت يومياً، فستصبح بارعاً."),
            GrammarExample("家に 帰ったら、電話します。", "いえに かえったら、でんわします。", "Ie ni kaettara, denwa shimasu.", "عندما أعود إلى البيت، سأتصل بك."),
          ),
          level = JlptLevel.N3,
        ),
        GrammarPoint(
          id = "g_n3_passive",
          pattern = "〜られる / 〜される (المبني للمجهول)",
          romaji = "...rareru / ...sareru",
          arabicTitle = "صيغة المبني للمجهول (受身形)",
          explanationArabic = "تُستعمل لبيان أن الفاعل تلقّى الفعل (مثل: يُمدح، يُدعى، أو يُنشر)، وتُستخدم كثيراً في الأخبار والكتب الرسمية اليابانية.",
          formulaArabic = "مجموعة ١: (a + れる)  |  مجموعة ٢: (られる)  |  する ← される",
          examples = listOf(
            GrammarExample("この 本は 世界中で 読まれて います。", "この ほんは せかいじゅうで よまれて います。", "Kono hon wa sekaijū de yomarete imasu.", "هذا الكتاب يُقرأ في جميع أنحاء العالم."),
          ),
          level = JlptLevel.N3,
        ),
      ),
      vocabulary = listOf(
        VocabItem("v_n3_keiken_houkoku", "報告", "ほうこく", "hōkoku", "تقرير / إبلاغ", "結果を 部長に 報告しました。", "けっかを ぶちょうに ほうこくしました。", "أبلغتُ المدير بالنتائج.", JlptLevel.N3),
        VocabItem("v_n3_iken", "意見", "いけん", "iken", "رأي", "あなたの 意見を 聞かせて ください。", "あなたの いけんを きかせて ください。", "من فضلك أسمعني رأيك.", JlptLevel.N3),
        VocabItem("v_n3_keizai", "経済", "けいざい", "keizai", "اقتصاد", "日本の 経済について 調べて います。", "にほんの けいざいについて しらべて います。", "أبحث عن الاقتصاد الياباني.", JlptLevel.N3),
        VocabItem("v_n3_kanjou", "感情", "かんじょう", "kanjō", "مشاعر / عواطف", "言葉で 感情を 伝えるのは 難しいです。", "ことばで かんじょうを つたえるのは むずかしいです。", "من الصعب التعبير عن المشاعر بالكلمات.", JlptLevel.N3),
      ),
      kanji = listOf(
        KanjiItem("k_n3_kan", "感", "カン", "かん(じる)", "شعور / إحساس", 13, "感情", "かんじょう", "مشاعر", JlptLevel.N3),
        KanjiItem("k_n3_hou", "報", "ホウ", "むく(いる)", "خبر / بلاغ", 12, "情報", "じょうほう", "معلومات", JlptLevel.N3),
        KanjiItem("k_n3_i", "意", "イ", "—", "معنى / نية", 13, "意味", "いみ", "معنى", JlptLevel.N3),
      ),
    ),
  )

  val allLessons: List<Lesson> =
    CurriculumN5Data.lessons + n4Lessons + n3Lessons

  val quizQuestions: List<QuizQuestion> = listOf(
    // KANA
    QuizQuestion(
      id = "q_kana_1",
      level = JlptLevel.KANA,
      promptArabic = "ما النطق الصحيح لحرف الهيراغانا التالي؟",
      promptJapanese = "し",
      options = listOf("shi (شي)", "tsu (تسو)", "ki (كي)", "ri (ري)"),
      correctIndex = 0,
      explanationArabic = "الحرف «し» ينتمي إلى صف ساكو في الهيراغانا ويُنطق «شي» (shi).",
    ),
    QuizQuestion(
      id = "q_kana_2",
      level = JlptLevel.KANA,
      promptArabic = "اختر حرف الكاتاكانا الذي ينطق «تسو» (tsu):",
      promptJapanese = "tsu",
      options = listOf("シ", "ソ", "ツ", "ン"),
      correctIndex = 2,
      explanationArabic = "في «ツ» (تسو) تكون النقطتان في الأعلى والشرطة نازلة للأسفل، بخلاف «シ» (شي).",
    ),
    QuizQuestion(
      id = "q_kana_3",
      level = JlptLevel.KANA,
      promptArabic = "ما قراءة هذه الكلمة المكتوبة بالهيراغانا؟",
      promptJapanese = "みず",
      options = listOf("mizu (ماء)", "michi (طريق)", "kaze (ريح)", "hana (زهرة)"),
      correctIndex = 0,
      explanationArabic = "み = mi و ず = zu (حرف す مع علامة الداكتين)، فتُقرأ mizu ومعناها ماء.",
    ),
    QuizQuestion(
      id = "q_kana_4",
      level = JlptLevel.KANA,
      promptArabic = "ما نطق حرف الهيراغانا التالي؟",
      promptJapanese = "ぬ",
      options = listOf("ne (نيه)", "nu (نو)", "me (ميه)", "no (نو مدورة)"),
      correctIndex = 1,
      explanationArabic = "الحرف «ぬ» هو (nu)، ويتميّز بوجود حلقة إضافية صغيرة في نهايته مقارنةً بـ «め» (me).",
    ),
    // N5
    QuizQuestion(
      id = "q_n5_1",
      level = JlptLevel.N5,
      promptArabic = "اختر الأداة الصحيحة لإكمال الجملة: «أشرب الماء»",
      promptJapanese = "水（　）飲みます。",
      options = listOf("を", "は", "で", "に"),
      correctIndex = 0,
      explanationArabic = "تُستخدم الأداة «を» للدلالة على المفعول به المباشر الذي يقع عليه الفعل.",
    ),
    QuizQuestion(
      id = "q_n5_2",
      level = JlptLevel.N5,
      promptArabic = "ما معنى الكانجي «山»؟",
      promptJapanese = "富士山 (ふじさん)",
      options = listOf("نهر", "شمس", "جبل", "شجرة"),
      correctIndex = 2,
      explanationArabic = "الكانجي «山» (ياما / سان) يعني «جبل»، كما في جبل فوجي 富士山.",
    ),
    QuizQuestion(
      id = "q_n5_3",
      level = JlptLevel.N5,
      promptArabic = "أي فعل يُستخدم لوجود الحيوانات والأشخاص؟",
      promptJapanese = "へやに ねこが（　）。",
      options = listOf("あります", "います", "します", "です"),
      correctIndex = 1,
      explanationArabic = "يُستخدم الفعل «います» للكائنات الحية (البشر والحيوانات)، بينما «あります» للجماد.",
    ),
    QuizQuestion(
      id = "q_n5_4",
      level = JlptLevel.N5,
      promptArabic = "كيف ننفي صفة الـ い «さむい» (بارد) بأدب؟",
      promptJapanese = "きょうは（　）。",
      options = listOf("さむい じゃありません", "さむくないです", "さむじゃないです", "さむくあります"),
      correctIndex = 1,
      explanationArabic = "لـنفي صفة い نحذف «い» ونضيف «くないです»، فتصبح さむくないです.",
    ),
    // N4
    QuizQuestion(
      id = "q_n4_1",
      level = JlptLevel.N4,
      promptArabic = "ما الصيغة الصحيحة لقول «من فضلك اكتب اسمك»؟",
      promptJapanese = "お名前を（　）ください。",
      options = listOf("書く", "書いた", "書いて", "書きます"),
      correctIndex = 2,
      explanationArabic = "الطلب المهذب يتكون من صيغة الـ て للفعل (書いて) متبوعة بـ ください.",
    ),
    QuizQuestion(
      id = "q_n4_2",
      level = JlptLevel.N4,
      promptArabic = "ما معنى التعبير «日本へ 行った ことが あります»؟",
      promptJapanese = "日本へ 行った ことが あります。",
      options = listOf(
        "أريد الذهاب إلى اليابان",
        "سبق لي أن زرتُ اليابان",
        "يجب أن أذهب إلى اليابان",
        "أستطيع الذهاب إلى اليابان",
      ),
      correctIndex = 1,
      explanationArabic = "التركيب (صيغة الماضي た + ことが あります) يعبّر عن تجربة خاضها المتحدث سابقاً.",
    ),
    QuizQuestion(
      id = "q_n4_3",
      level = JlptLevel.N4,
      promptArabic = "ما معنى كلمة «会社» (かいしゃ)؟",
      promptJapanese = "会社",
      options = listOf("محطة قطار", "مكتبة", "شركة", "مستشفى"),
      correctIndex = 2,
      explanationArabic = "الكلمة «会社» (كايشا) تعني شركة أو مؤسسة عمل.",
    ),
    // N3
    QuizQuestion(
      id = "q_n3_1",
      level = JlptLevel.N3,
      promptArabic = "اختر التعبير المناسب للنتيجة الإيجابية: «بفضل نصيحتك نجحتُ»",
      promptJapanese = "アドバイス（　）合格しました。",
      options = listOf("のせいで", "のおかげで", "のために", "によって"),
      correctIndex = 1,
      explanationArabic = "نستخدم «のおかげで» (بفضل) للنتائج الإيجابية، بينما «のせいで» تُستعمل للنتائج السلبية.",
    ),
    QuizQuestion(
      id = "q_n3_2",
      level = JlptLevel.N3,
      promptArabic = "ما المعنى الدقيق لجملة «世界中で 読まれて います»؟",
      promptJapanese = "この 本は 世界中で 読まれて います。",
      options = listOf(
        "أجبرتُه على قراءة هذا الكتاب",
        "هذا الكتاب يُقرأ في أنحاء العالم",
        "لم يقرأ أحد هذا الكتاب",
        "أستطيع قراءة هذا الكتاب",
      ),
      correctIndex = 1,
      explanationArabic = "الفعل «読まれる» هو صيغة المبني للمجهول من 読む، و«〜て います» تدل على الاستمرار.",
    ),
    QuizQuestion(
      id = "q_n3_3",
      level = JlptLevel.N3,
      promptArabic = "ما قراءة ومعنى كلمة «努力»؟",
      promptJapanese = "努力",
      options = listOf("どりょく — جهد ومثابرة", "けってい — قرار", "ほうこく — تقرير", "かんじょう — مشاعر"),
      correctIndex = 0,
      explanationArabic = "تُقرأ «努力» (doryoku) وتعني بذل الجهد والمثابرة.",
    ),
  )
}
