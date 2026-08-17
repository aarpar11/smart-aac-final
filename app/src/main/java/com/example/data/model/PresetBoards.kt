package com.example.data.model

object PresetBoards {

    val coreHomeBoard: Board by lazy {
        val buttons = listOf(
            // --- 1. SUBJECTS & PRONOUNS (Yellow / Fitzgerald PEOPLE) ---
            AACButton("c_i", "I", "I", FitzgeraldCategory.PEOPLE, partOfSpeech = PartOfSpeech.SUBJECT_PRONOUN, semanticTags = setOf(SemanticTag.PERSON), isCoreWord = true),
            AACButton("c_you", "You", "you", FitzgeraldCategory.PEOPLE, partOfSpeech = PartOfSpeech.SUBJECT_PRONOUN, semanticTags = setOf(SemanticTag.PERSON), isCoreWord = true),
            AACButton("c_we", "We", "we", FitzgeraldCategory.PEOPLE, partOfSpeech = PartOfSpeech.SUBJECT_PRONOUN, semanticTags = setOf(SemanticTag.PERSON), isCoreWord = true),
            AACButton("c_it", "It", "it", FitzgeraldCategory.PEOPLE, partOfSpeech = PartOfSpeech.SUBJECT_PRONOUN, semanticTags = setOf(SemanticTag.GENERAL), isCoreWord = true),
            AACButton("f_people", "People 📂", "People folder", FitzgeraldCategory.NAVIGATION, ButtonType.FOLDER, targetBoardId = "sub_people", isAlwaysClickable = true),

            // --- 2. CORE ACTIONS & VERBS (Green / Fitzgerald ACTION) ---
            AACButton("c_want", "Want", "want", FitzgeraldCategory.ACTION, partOfSpeech = PartOfSpeech.VERB_ACTION, semanticTags = setOf(SemanticTag.REQUEST, SemanticTag.ACTION_CORE), isCoreWord = true),
            AACButton("c_like", "Like", "like", FitzgeraldCategory.ACTION, partOfSpeech = PartOfSpeech.VERB_ACTION, semanticTags = setOf(SemanticTag.ACTION_CORE), isCoreWord = true),
            AACButton("c_go", "Go", "go", FitzgeraldCategory.ACTION, partOfSpeech = PartOfSpeech.VERB_MOTION, semanticTags = setOf(SemanticTag.ACTION_CORE), isCoreWord = true),
            AACButton("c_eat", "Eat", "eat", FitzgeraldCategory.ACTION, partOfSpeech = PartOfSpeech.VERB_ACTION, semanticTags = setOf(SemanticTag.FOOD, SemanticTag.ACTION_CORE), isCoreWord = true),
            AACButton("c_help", "Help", "help", FitzgeraldCategory.ACTION, partOfSpeech = PartOfSpeech.VERB_ACTION, semanticTags = setOf(SemanticTag.REQUEST, SemanticTag.ACTION_CORE), isCoreWord = true),
            AACButton("c_play", "Play", "play", FitzgeraldCategory.ACTION, partOfSpeech = PartOfSpeech.VERB_ACTION, semanticTags = setOf(SemanticTag.TOY_PLAY, SemanticTag.ACTION_CORE), isCoreWord = true),
            AACButton("c_stop", "Stop", "stop", FitzgeraldCategory.ACTION, partOfSpeech = PartOfSpeech.VERB_ACTION, semanticTags = setOf(SemanticTag.REQUEST, SemanticTag.ACTION_CORE), isCoreWord = true),
            AACButton("f_actions", "Actions 📂", "Actions folder", FitzgeraldCategory.NAVIGATION, ButtonType.FOLDER, targetBoardId = "sub_actions", isAlwaysClickable = true),

            // --- 3. DESCRIPTORS & ADJECTIVES (Blue / Fitzgerald ADJECTIVE) ---
            AACButton("c_good", "Good", "good", FitzgeraldCategory.ADJECTIVE, partOfSpeech = PartOfSpeech.ADJECTIVE, semanticTags = setOf(SemanticTag.GENERAL), isCoreWord = true),
            AACButton("c_bad", "Bad", "bad", FitzgeraldCategory.ADJECTIVE, partOfSpeech = PartOfSpeech.ADJECTIVE, semanticTags = setOf(SemanticTag.GENERAL), isCoreWord = true),
            AACButton("c_more", "More", "more", FitzgeraldCategory.ADJECTIVE, partOfSpeech = PartOfSpeech.QUANTIFIER, semanticTags = setOf(SemanticTag.REQUEST), isCoreWord = true),
            AACButton("c_happy", "Happy", "happy", FitzgeraldCategory.ADJECTIVE, partOfSpeech = PartOfSpeech.ADJECTIVE, semanticTags = setOf(SemanticTag.EMOTION), isCoreWord = true),
            AACButton("c_tired", "Tired", "tired", FitzgeraldCategory.ADJECTIVE, partOfSpeech = PartOfSpeech.ADJECTIVE, semanticTags = setOf(SemanticTag.EMOTION, SemanticTag.SENSORY), isCoreWord = true),
            AACButton("f_feelings", "Feelings 📂", "Feelings folder", FitzgeraldCategory.NAVIGATION, ButtonType.FOLDER, targetBoardId = "sub_feelings", isAlwaysClickable = true),

            // --- 4. CORE OBJECTS & NOUNS (Orange / Fitzgerald NOUN) ---
            AACButton("c_water", "Water", "water", FitzgeraldCategory.NOUN, partOfSpeech = PartOfSpeech.NOUN, semanticTags = setOf(SemanticTag.DRINK, SemanticTag.FOOD), isCoreWord = true),
            AACButton("c_bathroom", "Bathroom", "bathroom", FitzgeraldCategory.NOUN, partOfSpeech = PartOfSpeech.NOUN, semanticTags = setOf(SemanticTag.PLACE, SemanticTag.REQUEST), isCoreWord = true),
            AACButton("c_home", "Home", "home", FitzgeraldCategory.NOUN, partOfSpeech = PartOfSpeech.NOUN, semanticTags = setOf(SemanticTag.PLACE), isCoreWord = true),
            AACButton("c_school", "School", "school", FitzgeraldCategory.NOUN, partOfSpeech = PartOfSpeech.NOUN, semanticTags = setOf(SemanticTag.PLACE), isCoreWord = true),
            AACButton("f_places", "Places 📂", "Places folder", FitzgeraldCategory.NAVIGATION, ButtonType.FOLDER, targetBoardId = "sub_places", isAlwaysClickable = true),
            AACButton("f_time_nouns", "Time & Nouns 📂", "Time and Nouns folder", FitzgeraldCategory.NAVIGATION, ButtonType.FOLDER, targetBoardId = "sub_time_nouns", isAlwaysClickable = true),

            // --- 5. FUNCTION & SMALL WORDS ---
            AACButton("f_small_words", "Small Words 📂", "Small words folder", FitzgeraldCategory.NAVIGATION, ButtonType.FOLDER, targetBoardId = "sub_small_words", isAlwaysClickable = true),

            // --- 6. SOCIAL & OTHER FOLDERS ---
            AACButton("c_yes", "Yes", "yes", FitzgeraldCategory.SOCIAL, partOfSpeech = PartOfSpeech.SOCIAL, semanticTags = setOf(SemanticTag.GENERAL), isCoreWord = true),
            AACButton("c_no", "No", "no", FitzgeraldCategory.SOCIAL, partOfSpeech = PartOfSpeech.SOCIAL, semanticTags = setOf(SemanticTag.GENERAL), isCoreWord = true),
            AACButton("c_please", "Please", "please", FitzgeraldCategory.SOCIAL, partOfSpeech = PartOfSpeech.SOCIAL, semanticTags = setOf(SemanticTag.REQUEST), isCoreWord = true),
            AACButton("c_thanks", "Thanks", "thank you", FitzgeraldCategory.SOCIAL, partOfSpeech = PartOfSpeech.SOCIAL, semanticTags = setOf(SemanticTag.GENERAL), isCoreWord = true),
            AACButton("c_hello", "Hello", "hello", FitzgeraldCategory.SOCIAL, partOfSpeech = PartOfSpeech.SOCIAL, semanticTags = setOf(SemanticTag.GENERAL), isCoreWord = true),
            AACButton("f_food", "Food 📂", "Food folder", FitzgeraldCategory.NAVIGATION, ButtonType.FOLDER, targetBoardId = "sub_food", isAlwaysClickable = true),
            AACButton("f_toys", "Toys 📂", "Toys folder", FitzgeraldCategory.NAVIGATION, ButtonType.FOLDER, targetBoardId = "sub_toys", isAlwaysClickable = true),
            AACButton("f_saved_phrases", "Saved Phrases 📁", "Saved phrases folder", FitzgeraldCategory.NAVIGATION, ButtonType.FOLDER, targetBoardId = "saved_phrases", isAlwaysClickable = true)
        )
        Board(
            id = "core_home",
            name = "Smart AAC Board",
            isCustom = false,
            columns = 4,
            rows = 8,
            buttonsJson = serializeButtons(buttons)
        )
    }

    val actionsBoard: Board by lazy {
        val words = listOf(
            "am", "are", "is", "bite", "break", "bring", "can", "change", "choose",
            "clean", "come", "cut", "do", "drink", "drive", "eat", "fall", "feel",
            "find", "finish", "fix", "get", "give", "go", "grow", "guess", "have",
            "hear", "help", "hurt", "keep", "know", "learn", "like", "live", "look",
            "love", "make", "mean", "measure", "move", "need", "open", "play", "push",
            "put", "put on", "read", "see", "share", "show", "sing", "start", "stop",
            "take", "talk", "tell", "try", "use", "wait", "wear", "work"
        )
        val buttons = words.mapIndexed { idx, w ->
            val pos = when (w) {
                "am", "are", "is", "feel", "look" -> PartOfSpeech.VERB_LINKING
                "go", "come", "drive", "move", "fall" -> PartOfSpeech.VERB_MOTION
                "can", "do", "have" -> PartOfSpeech.VERB_MODAL
                else -> PartOfSpeech.VERB_ACTION
            }
            val tags = mutableSetOf(SemanticTag.ACTION_CORE)
            if (w in setOf("eat", "drink", "bite")) tags.add(SemanticTag.FOOD)
            if (w in setOf("play", "read", "sing")) tags.add(SemanticTag.TOY_PLAY)
            AACButton("ac_$idx", w.capitalizeWord(), w, FitzgeraldCategory.ACTION, partOfSpeech = pos, semanticTags = tags)
        }
        Board("sub_actions", "Actions & Verbs 📂", false, 4, 15, serializeButtons(buttons))
    }

    val feelingsBoard: Board by lazy {
        val words = listOf(
            "bad", "big", "cold", "dark", "different", "difficult", "done", "early",
            "easy", "empty", "far", "fast", "favorite", "few", "first", "full", "fun",
            "gone", "good", "great", "happy", "hard", "high", "hot", "last", "late",
            "light", "little", "long", "loud", "new", "next", "nice", "old", "past",
            "pretty", "quiet", "ready", "right", "sad", "same", "second", "silly",
            "slow", "soft", "top", "very", "hungry", "tired", "sick"
        )
        val buttons = words.mapIndexed { idx, w ->
            val tags = mutableSetOf<SemanticTag>()
            if (w in setOf("happy", "sad", "silly", "good", "bad", "great")) tags.add(SemanticTag.EMOTION)
            if (w in setOf("cold", "hot", "hungry", "tired", "sick", "loud", "quiet", "soft", "hard")) tags.add(SemanticTag.SENSORY)
            AACButton("fl_$idx", w.capitalizeWord(), w, FitzgeraldCategory.ADJECTIVE, partOfSpeech = PartOfSpeech.ADJECTIVE, semanticTags = tags)
        }
        Board("sub_feelings", "Descriptors & Feelings 📂", false, 4, 12, serializeButtons(buttons))
    }

    val peopleBoard: Board by lazy {
        val words = listOf(
            "I", "you", "we", "he", "she", "they", "it", "me", "mine", "friend",
            "mom", "dad", "sister", "brother", "doctor", "teacher", "grandma"
        )
        val buttons = words.mapIndexed { idx, w ->
            val pos = when (w.lowercase()) {
                "me" -> PartOfSpeech.OBJECT_PRONOUN
                "mine" -> PartOfSpeech.POSSESSIVE_PRONOUN
                "i", "you", "we", "he", "she", "they", "it" -> PartOfSpeech.SUBJECT_PRONOUN
                else -> PartOfSpeech.NOUN
            }
            AACButton("pp_$idx", w.capitalizeWord(), w, FitzgeraldCategory.PEOPLE, partOfSpeech = pos, semanticTags = setOf(SemanticTag.PERSON))
        }
        Board("sub_people", "People & Pronouns 📂", false, 3, 6, serializeButtons(buttons))
    }

    val smallWordsBoard: Board by lazy {
        val words = listOf(
            "about", "again", "all", "almost", "always", "and", "any", "away", "because",
            "before", "down", "for", "from", "here", "how", "if", "in", "later", "less",
            "many", "more", "most", "never", "no", "none", "not", "nothing", "now", "of",
            "off", "on", "other", "out", "over", "some", "that", "then", "these", "this",
            "those", "under", "up", "what", "when", "where", "who", "why", "with"
        )
        val buttons = words.mapIndexed { idx, w ->
            val pos = when (w) {
                "in", "on", "under", "to", "for", "with", "from", "about", "at", "down", "up", "off", "of", "out", "over", "before", "behind" -> PartOfSpeech.PREPOSITION
                "and", "because", "if", "then", "or", "but" -> PartOfSpeech.CONJUNCTION
                "more", "less", "many", "most", "some", "any", "all", "none", "few" -> PartOfSpeech.QUANTIFIER
                "this", "that", "these", "those" -> PartOfSpeech.DETERMINER
                "what", "when", "where", "who", "why", "how" -> PartOfSpeech.QUESTION_WORD
                "no", "yes" -> PartOfSpeech.SOCIAL
                else -> PartOfSpeech.ADVERB
            }
            AACButton("sw_$idx", w.capitalizeWord(), w, FitzgeraldCategory.FUNCTION, partOfSpeech = pos)
        }
        Board("sub_small_words", "Small Words & Functions 📂", false, 4, 12, serializeButtons(buttons))
    }

    val timeNounsBoard: Board by lazy {
        val words = listOf(
            "body", "day", "face", "hour", "job", "middle", "minute", "month", "morning",
            "name", "party", "place", "problem", "question", "room", "sound", "story",
            "time", "today", "tomorrow", "week", "year", "yesterday"
        )
        val buttons = words.mapIndexed { idx, w ->
            val tags = if (w in setOf("day", "hour", "minute", "month", "morning", "time", "today", "tomorrow", "week", "year", "yesterday")) {
                setOf(SemanticTag.TIME)
            } else setOf(SemanticTag.GENERAL)
            AACButton("tn_$idx", w.capitalizeWord(), w, FitzgeraldCategory.NOUN, partOfSpeech = PartOfSpeech.NOUN, semanticTags = tags)
        }
        Board("sub_time_nouns", "Time & Concepts 📂", false, 4, 6, serializeButtons(buttons))
    }

    val placesBoard: Board by lazy {
        val buttons = listOf(
            AACButton("pl_home", "Home", "home", FitzgeraldCategory.NOUN, partOfSpeech = PartOfSpeech.NOUN, semanticTags = setOf(SemanticTag.PLACE)),
            AACButton("pl_park", "Park", "park", FitzgeraldCategory.NOUN, partOfSpeech = PartOfSpeech.NOUN, semanticTags = setOf(SemanticTag.PLACE)),
            AACButton("pl_school", "School", "school", FitzgeraldCategory.NOUN, partOfSpeech = PartOfSpeech.NOUN, semanticTags = setOf(SemanticTag.PLACE)),
            AACButton("pl_outside", "Outside", "outside", FitzgeraldCategory.NOUN, partOfSpeech = PartOfSpeech.NOUN, semanticTags = setOf(SemanticTag.PLACE)),
            AACButton("pl_sensory", "Sensory Room", "sensory room", FitzgeraldCategory.NOUN, partOfSpeech = PartOfSpeech.NOUN, semanticTags = setOf(SemanticTag.PLACE)),
            AACButton("pl_store", "Store", "store", FitzgeraldCategory.NOUN, partOfSpeech = PartOfSpeech.NOUN, semanticTags = setOf(SemanticTag.PLACE)),
            AACButton("pl_library", "Library", "library", FitzgeraldCategory.NOUN, partOfSpeech = PartOfSpeech.NOUN, semanticTags = setOf(SemanticTag.PLACE)),
            AACButton("pl_bed", "Bed", "bed", FitzgeraldCategory.NOUN, partOfSpeech = PartOfSpeech.NOUN, semanticTags = setOf(SemanticTag.PLACE)),
            AACButton("pl_pool", "Pool", "swimming pool", FitzgeraldCategory.NOUN, partOfSpeech = PartOfSpeech.NOUN, semanticTags = setOf(SemanticTag.PLACE)),
            AACButton("pl_hospital", "Hospital", "hospital", FitzgeraldCategory.NOUN, partOfSpeech = PartOfSpeech.NOUN, semanticTags = setOf(SemanticTag.PLACE)),
            AACButton("pl_bathroom", "Bathroom", "bathroom", FitzgeraldCategory.NOUN, partOfSpeech = PartOfSpeech.NOUN, semanticTags = setOf(SemanticTag.PLACE, SemanticTag.REQUEST))
        )
        Board("sub_places", "Places & Environments 📂", false, 3, 4, serializeButtons(buttons))
    }

    val foodBoard: Board by lazy {
        val buttons = listOf(
            AACButton("fd_water", "Water", "water", FitzgeraldCategory.NOUN, partOfSpeech = PartOfSpeech.NOUN, semanticTags = setOf(SemanticTag.DRINK, SemanticTag.FOOD)),
            AACButton("fd_apple", "Apple", "apple", FitzgeraldCategory.NOUN, imageUrl = "https://s3.amazonaws.com/opensymbols/production/uploads/images/7542/apple.png", partOfSpeech = PartOfSpeech.NOUN, semanticTags = setOf(SemanticTag.FOOD)),
            AACButton("fd_banana", "Banana", "banana", FitzgeraldCategory.NOUN, imageUrl = "https://s3.amazonaws.com/opensymbols/production/uploads/images/2231/banana.png", partOfSpeech = PartOfSpeech.NOUN, semanticTags = setOf(SemanticTag.FOOD)),
            AACButton("fd_pizza", "Pizza", "pizza", FitzgeraldCategory.NOUN, imageUrl = "https://s3.amazonaws.com/opensymbols/production/uploads/images/3034/pizza.png", partOfSpeech = PartOfSpeech.NOUN, semanticTags = setOf(SemanticTag.FOOD)),
            AACButton("fd_milk", "Milk", "milk", FitzgeraldCategory.NOUN, partOfSpeech = PartOfSpeech.NOUN, semanticTags = setOf(SemanticTag.DRINK, SemanticTag.FOOD)),
            AACButton("fd_juice", "Juice", "juice", FitzgeraldCategory.NOUN, partOfSpeech = PartOfSpeech.NOUN, semanticTags = setOf(SemanticTag.DRINK, SemanticTag.FOOD)),
            AACButton("fd_cookie", "Cookie", "cookie", FitzgeraldCategory.NOUN, partOfSpeech = PartOfSpeech.NOUN, semanticTags = setOf(SemanticTag.FOOD)),
            AACButton("fd_bread", "Bread", "bread", FitzgeraldCategory.NOUN, partOfSpeech = PartOfSpeech.NOUN, semanticTags = setOf(SemanticTag.FOOD)),
            AACButton("fd_rice", "Rice", "rice", FitzgeraldCategory.NOUN, partOfSpeech = PartOfSpeech.NOUN, semanticTags = setOf(SemanticTag.FOOD)),
            AACButton("fd_soup", "Soup", "soup", FitzgeraldCategory.NOUN, partOfSpeech = PartOfSpeech.NOUN, semanticTags = setOf(SemanticTag.FOOD)),
            AACButton("fd_cheese", "Cheese", "cheese", FitzgeraldCategory.NOUN, partOfSpeech = PartOfSpeech.NOUN, semanticTags = setOf(SemanticTag.FOOD)),
            AACButton("fd_chicken", "Chicken", "chicken", FitzgeraldCategory.NOUN, partOfSpeech = PartOfSpeech.NOUN, semanticTags = setOf(SemanticTag.FOOD)),
            AACButton("fd_icecream", "Ice Cream", "ice cream", FitzgeraldCategory.NOUN, partOfSpeech = PartOfSpeech.NOUN, semanticTags = setOf(SemanticTag.FOOD))
        )
        Board("sub_food", "Food & Drinks 📂", false, 3, 5, serializeButtons(buttons))
    }

    val toysBoard: Board by lazy {
        val buttons = listOf(
            AACButton("ty_blocks", "Blocks", "blocks", FitzgeraldCategory.NOUN, partOfSpeech = PartOfSpeech.NOUN, semanticTags = setOf(SemanticTag.TOY_PLAY)),
            AACButton("ty_bubbles", "Bubbles", "bubbles", FitzgeraldCategory.NOUN, partOfSpeech = PartOfSpeech.NOUN, semanticTags = setOf(SemanticTag.TOY_PLAY)),
            AACButton("ty_swing", "Swing", "swing", FitzgeraldCategory.NOUN, partOfSpeech = PartOfSpeech.NOUN, semanticTags = setOf(SemanticTag.TOY_PLAY)),
            AACButton("ty_puzzle", "Puzzle", "puzzle", FitzgeraldCategory.NOUN, partOfSpeech = PartOfSpeech.NOUN, semanticTags = setOf(SemanticTag.TOY_PLAY)),
            AACButton("ty_book", "Book", "book", FitzgeraldCategory.NOUN, partOfSpeech = PartOfSpeech.NOUN, semanticTags = setOf(SemanticTag.TOY_PLAY)),
            AACButton("ty_drawing", "Drawing", "drawing", FitzgeraldCategory.NOUN, partOfSpeech = PartOfSpeech.NOUN, semanticTags = setOf(SemanticTag.TOY_PLAY)),
            AACButton("ty_train", "Train", "train set", FitzgeraldCategory.NOUN, partOfSpeech = PartOfSpeech.NOUN, semanticTags = setOf(SemanticTag.TOY_PLAY, SemanticTag.VEHICLE)),
            AACButton("ty_ball", "Ball", "ball", FitzgeraldCategory.NOUN, partOfSpeech = PartOfSpeech.NOUN, semanticTags = setOf(SemanticTag.TOY_PLAY)),
            AACButton("ty_music", "Music", "music", FitzgeraldCategory.NOUN, partOfSpeech = PartOfSpeech.NOUN, semanticTags = setOf(SemanticTag.TOY_PLAY, SemanticTag.SENSORY)),
            AACButton("ty_car", "Car", "car", FitzgeraldCategory.NOUN, partOfSpeech = PartOfSpeech.NOUN, semanticTags = setOf(SemanticTag.TOY_PLAY, SemanticTag.VEHICLE)),
            AACButton("ty_tablet", "Tablet", "tablet", FitzgeraldCategory.NOUN, partOfSpeech = PartOfSpeech.NOUN, semanticTags = setOf(SemanticTag.TOY_PLAY))
        )
        Board("sub_toys", "Toys & Fun 📂", false, 3, 4, serializeButtons(buttons))
    }

    val allPresetBoards: Map<String, Board> = mapOf(
        "core_home" to coreHomeBoard,
        "sub_food" to foodBoard,
        "sub_feelings" to feelingsBoard,
        "sub_actions" to actionsBoard,
        "sub_places" to placesBoard,
        "sub_people" to peopleBoard,
        "sub_toys" to toysBoard,
        "sub_small_words" to smallWordsBoard,
        "sub_time_nouns" to timeNounsBoard
    )

    private fun String.capitalizeWord(): String {
        if (isEmpty()) return this
        if (this.equals("i", ignoreCase = true)) return "I"
        return this.substring(0, 1).uppercase() + this.substring(1)
    }

    fun serializeButtons(buttons: List<AACButton>): String {
        val sb = StringBuilder()
        sb.append("[")
        buttons.forEachIndexed { idx, btn ->
            val tagsStr = btn.semanticTags.joinToString(",") { it.name }
            sb.append("{")
            sb.append("\"id\":\"${btn.id}\",")
            sb.append("\"label\":\"${escapeJson(btn.label)}\",")
            sb.append("\"spokenText\":\"${escapeJson(btn.spokenText)}\",")
            sb.append("\"category\":\"${btn.category.name}\",")
            sb.append("\"type\":\"${btn.type.name}\",")
            sb.append("\"partOfSpeech\":\"${btn.partOfSpeech.name}\",")
            sb.append("\"semanticTags\":\"${tagsStr}\",")
            sb.append("\"isCoreWord\":${btn.isCoreWord},")
            sb.append("\"isAlwaysClickable\":${btn.isAlwaysClickable},")
            if (btn.imageUrl != null) sb.append("\"imageUrl\":\"${btn.imageUrl}\",")
            if (btn.localIconId != null) sb.append("\"localIconId\":\"${btn.localIconId}\",")
            if (btn.targetBoardId != null) sb.append("\"targetBoardId\":\"${btn.targetBoardId}\",")
            sb.append("\"isHidden\":${btn.isHidden}")
            sb.append("}")
            if (idx < buttons.size - 1) sb.append(",")
        }
        sb.append("]")
        return sb.toString()
    }

    fun deserializeButtons(json: String): List<AACButton> {
        val result = mutableListOf<AACButton>()
        try {
            val cleaner = json.trim()
            if (cleaner == "[]" || cleaner.isEmpty()) return emptyList()

            val items = cleaner.substring(1, cleaner.length - 1).split("},{", "}, {")
            for (item in items) {
                var normalized = item
                if (!normalized.startsWith("{")) normalized = "{$normalized"
                if (!normalized.endsWith("}")) normalized = "$normalized}"

                val id = extractJsonField(normalized, "id") ?: "unknown"
                val label = extractJsonField(normalized, "label") ?: ""
                val spokenText = extractJsonField(normalized, "spokenText") ?: label
                val categoryStr = extractJsonField(normalized, "category") ?: "NOUN"
                val typeStr = extractJsonField(normalized, "type") ?: "WORD"
                val posStr = extractJsonField(normalized, "partOfSpeech")
                val tagsStr = extractJsonField(normalized, "semanticTags")
                val isCoreStr = extractJsonValueNoQuotes(normalized, "isCoreWord") ?: "false"
                val isAlwaysClickableStr = extractJsonValueNoQuotes(normalized, "isAlwaysClickable")
                val imageUrl = extractJsonField(normalized, "imageUrl")
                val localIconId = extractJsonField(normalized, "localIconId")
                val targetBoardId = extractJsonField(normalized, "targetBoardId")
                val isHiddenStr = extractJsonValueNoQuotes(normalized, "isHidden") ?: "false"
                val isHidden = isHiddenStr.trim() == "true"

                val category = try { FitzgeraldCategory.valueOf(categoryStr) } catch(e: Exception) { FitzgeraldCategory.NOUN }
                val type = try { ButtonType.valueOf(typeStr) } catch(e: Exception) { ButtonType.WORD }
                val pos = if (posStr != null) {
                    try { PartOfSpeech.valueOf(posStr) } catch(e: Exception) { PartOfSpeech.UNKNOWN }
                } else PartOfSpeech.UNKNOWN

                val semanticTags = if (!tagsStr.isNullOrBlank()) {
                    tagsStr.split(",").mapNotNull { tag ->
                        try { SemanticTag.valueOf(tag.trim()) } catch(e: Exception) { null }
                    }.toSet()
                } else emptySet()

                val isCoreWord = isCoreStr.trim() == "true"
                val isAlwaysClickable = if (isAlwaysClickableStr != null) {
                    isAlwaysClickableStr.trim() == "true"
                } else (type == ButtonType.FOLDER || type == ButtonType.CONTROL || category == FitzgeraldCategory.NAVIGATION)

                val rawButton = AACButton(
                    id = id,
                    label = label,
                    spokenText = spokenText,
                    category = category,
                    type = type,
                    imageUrl = imageUrl,
                    localIconId = localIconId,
                    targetBoardId = targetBoardId,
                    isHidden = isHidden,
                    partOfSpeech = pos,
                    semanticTags = semanticTags,
                    isCoreWord = isCoreWord,
                    isAlwaysClickable = isAlwaysClickable
                )

                // If pos was UNKNOWN (legacy data), infer dynamically
                result.add(GrammarEngine.resolveEffectiveButton(rawButton))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }

    private fun extractJsonField(json: String, field: String): String? {
        val target = "\"$field\":\""
        val idx = json.indexOf(target)
        if (idx == -1) return null
        val start = idx + target.length
        val end = json.indexOf("\"", start)
        if (end == -1) return null
        return json.substring(start, end)
    }

    private fun extractJsonValueNoQuotes(json: String, field: String): String? {
        val target = "\"$field\":"
        val idx = json.indexOf(target)
        if (idx == -1) return null
        val start = idx + target.length
        var end = json.indexOf(",", start)
        if (end == -1) end = json.indexOf("}", start)
        if (end == -1) return null
        return json.substring(start, end).trim()
    }

    private fun escapeJson(s: String): String {
        return s.replace("\"", "\\\"").replace("\n", "\\n")
    }
}
