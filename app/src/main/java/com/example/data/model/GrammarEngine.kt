package com.example.data.model

/**
 * Scalable, metadata-driven Grammar Engine for AAC (Augmentative and Alternative Communication).
 *
 * Implements dynamic button filtering (smart key inhibition & predictive highlighting) using
 * structured grammatical metadata (Parts of Speech and Semantic Tags) rather than brittle raw string matching.
 *
 * Designed following AAC language acquisition frameworks (Fitzgerald Key, WordPower, Crescent Grammar).
 */
object GrammarEngine {

    /**
     * Start-of-sentence permitted grammatical parts of speech.
     */
    val START_OF_SENTENCE_ALLOWED: Set<PartOfSpeech> = setOf(
        PartOfSpeech.SUBJECT_PRONOUN,
        PartOfSpeech.NOUN,
        PartOfSpeech.PROPER_NOUN,
        PartOfSpeech.VERB_ACTION,
        PartOfSpeech.VERB_MOTION,
        PartOfSpeech.VERB_LINKING,
        PartOfSpeech.VERB_MODAL,
        PartOfSpeech.ADJECTIVE,
        PartOfSpeech.DETERMINER,
        PartOfSpeech.QUANTIFIER,
        PartOfSpeech.QUESTION_WORD,
        PartOfSpeech.SOCIAL,
        PartOfSpeech.ADVERB,
        PartOfSpeech.PREPOSITION,
        PartOfSpeech.UNKNOWN
    )

    /**
     * Declarative Grammar Transition Matrix:
     * Defines the standard grammatically valid next parts of speech given the current PartOfSpeech.
     */
    val ALLOWED_TRANSITIONS: Map<PartOfSpeech, Set<PartOfSpeech>> = mapOf(
        PartOfSpeech.SUBJECT_PRONOUN to setOf(
            PartOfSpeech.VERB_ACTION,
            PartOfSpeech.VERB_LINKING,
            PartOfSpeech.VERB_MOTION,
            PartOfSpeech.VERB_MODAL,
            PartOfSpeech.ADJECTIVE,      // Predicate adjective AAC telegraphic shorthand (e.g. "I happy", "She tired")
            PartOfSpeech.ADVERB,         // Negation / adverb modifier (e.g. "I not", "We always")
            PartOfSpeech.PREPOSITION,    // Locative / prepositional state (e.g. "I in", "He at")
            PartOfSpeech.SOCIAL,         // Polite additions (e.g. "I please")
            PartOfSpeech.UNKNOWN
        ),
        PartOfSpeech.OBJECT_PRONOUN to setOf(
            PartOfSpeech.CONJUNCTION,
            PartOfSpeech.PREPOSITION,
            PartOfSpeech.ADVERB,
            PartOfSpeech.SOCIAL,
            PartOfSpeech.VERB_ACTION,    // AAC colloquial/expressive (e.g. "me want")
            PartOfSpeech.UNKNOWN
        ),
        PartOfSpeech.POSSESSIVE_PRONOUN to setOf(
            PartOfSpeech.NOUN,
            PartOfSpeech.PROPER_NOUN,
            PartOfSpeech.ADJECTIVE,
            PartOfSpeech.UNKNOWN
        ),
        PartOfSpeech.NOUN to setOf(
            PartOfSpeech.VERB_ACTION,
            PartOfSpeech.VERB_LINKING,
            PartOfSpeech.VERB_MOTION,
            PartOfSpeech.VERB_MODAL,
            PartOfSpeech.PREPOSITION,
            PartOfSpeech.CONJUNCTION,
            PartOfSpeech.ADVERB,
            PartOfSpeech.ADJECTIVE,
            PartOfSpeech.SOCIAL,
            PartOfSpeech.SUBJECT_PRONOUN, // Clause transition (e.g. "Mom I want")
            PartOfSpeech.UNKNOWN
        ),
        PartOfSpeech.PROPER_NOUN to setOf(
            PartOfSpeech.VERB_ACTION,
            PartOfSpeech.VERB_LINKING,
            PartOfSpeech.VERB_MOTION,
            PartOfSpeech.VERB_MODAL,
            PartOfSpeech.PREPOSITION,
            PartOfSpeech.CONJUNCTION,
            PartOfSpeech.ADVERB,
            PartOfSpeech.ADJECTIVE,
            PartOfSpeech.SOCIAL,
            PartOfSpeech.UNKNOWN
        ),
        PartOfSpeech.VERB_ACTION to setOf(
            PartOfSpeech.NOUN,
            PartOfSpeech.PROPER_NOUN,
            PartOfSpeech.OBJECT_PRONOUN,
            PartOfSpeech.SUBJECT_PRONOUN, // Pronoun complement (e.g. "see you", "help me")
            PartOfSpeech.DETERMINER,
            PartOfSpeech.QUANTIFIER,
            PartOfSpeech.ADJECTIVE,
            PartOfSpeech.PREPOSITION,
            PartOfSpeech.ADVERB,
            PartOfSpeech.VERB_ACTION,     // Verb chaining / infinitive shortcut (e.g. "want eat", "stop play")
            PartOfSpeech.VERB_MOTION,     // (e.g. "want go")
            PartOfSpeech.SOCIAL,
            PartOfSpeech.UNKNOWN
        ),
        PartOfSpeech.VERB_LINKING to setOf(
            PartOfSpeech.ADJECTIVE,       // Predicate adjective (e.g. "is happy", "am hungry")
            PartOfSpeech.NOUN,            // Predicate nominative (e.g. "is teacher", "am friend")
            PartOfSpeech.PROPER_NOUN,
            PartOfSpeech.DETERMINER,
            PartOfSpeech.QUANTIFIER,
            PartOfSpeech.PREPOSITION,     // Prepositional phrase (e.g. "is in", "are at")
            PartOfSpeech.ADVERB,          // (e.g. "is not", "am here")
            PartOfSpeech.SOCIAL,
            PartOfSpeech.UNKNOWN
        ),
        PartOfSpeech.VERB_MOTION to setOf(
            PartOfSpeech.PREPOSITION,     // (e.g. "go to", "walk in", "come with")
            PartOfSpeech.NOUN,            // Direct destination noun (e.g. "go home", "go school")
            PartOfSpeech.PROPER_NOUN,
            PartOfSpeech.ADVERB,          // (e.g. "go out", "come back", "walk here")
            PartOfSpeech.CONJUNCTION,
            PartOfSpeech.SOCIAL,
            PartOfSpeech.UNKNOWN
        ),
        PartOfSpeech.VERB_MODAL to setOf(
            PartOfSpeech.VERB_ACTION,     // (e.g. "can eat", "will help")
            PartOfSpeech.VERB_MOTION,     // (e.g. "can go", "will come")
            PartOfSpeech.VERB_LINKING,    // (e.g. "can be")
            PartOfSpeech.ADVERB,          // (e.g. "can not", "will always")
            PartOfSpeech.SOCIAL,
            PartOfSpeech.UNKNOWN
        ),
        PartOfSpeech.ADJECTIVE to setOf(
            PartOfSpeech.NOUN,            // (e.g. "big ball", "cold water")
            PartOfSpeech.PROPER_NOUN,
            PartOfSpeech.ADJECTIVE,       // Adjective stacking (e.g. "big red", "hot delicious")
            PartOfSpeech.CONJUNCTION,     // (e.g. "happy and")
            PartOfSpeech.ADVERB,          // (e.g. "happy now")
            PartOfSpeech.PREPOSITION,     // (e.g. "ready for", "good at")
            PartOfSpeech.SOCIAL,          // (e.g. "happy please")
            PartOfSpeech.UNKNOWN
        ),
        PartOfSpeech.DETERMINER to setOf(
            PartOfSpeech.NOUN,            // (e.g. "the dog", "a cookie")
            PartOfSpeech.PROPER_NOUN,
            PartOfSpeech.ADJECTIVE,       // (e.g. "the big", "a good")
            PartOfSpeech.QUANTIFIER,
            PartOfSpeech.UNKNOWN
        ),
        PartOfSpeech.QUANTIFIER to setOf(
            PartOfSpeech.NOUN,            // (e.g. "more water", "some cookies")
            PartOfSpeech.PROPER_NOUN,
            PartOfSpeech.ADJECTIVE,       // (e.g. "more hungry")
            PartOfSpeech.PREPOSITION,     // (e.g. "more of", "all in")
            PartOfSpeech.SOCIAL,          // (e.g. "more please")
            PartOfSpeech.UNKNOWN
        ),
        PartOfSpeech.PREPOSITION to setOf(
            PartOfSpeech.NOUN,            // (e.g. "in room", "to park", "with friend")
            PartOfSpeech.PROPER_NOUN,     // (e.g. "with Mom")
            PartOfSpeech.OBJECT_PRONOUN,  // (e.g. "with me", "to you", "for us")
            PartOfSpeech.SUBJECT_PRONOUN,
            PartOfSpeech.DETERMINER,      // (e.g. "in the", "to a")
            PartOfSpeech.QUANTIFIER,      // (e.g. "for all", "in some")
            PartOfSpeech.ADJECTIVE,       // (e.g. "in big", "with cold")
            PartOfSpeech.VERB_ACTION,     // Infinitive construction after "to" (e.g. "to eat", "to play")
            PartOfSpeech.VERB_MOTION,     // Infinitive construction after "to" (e.g. "to go")
            PartOfSpeech.ADVERB,          // (e.g. "to here", "in there")
            PartOfSpeech.SOCIAL,
            PartOfSpeech.UNKNOWN
        ),
        PartOfSpeech.CONJUNCTION to setOf(
            PartOfSpeech.SUBJECT_PRONOUN,
            PartOfSpeech.NOUN,
            PartOfSpeech.PROPER_NOUN,
            PartOfSpeech.OBJECT_PRONOUN,
            PartOfSpeech.VERB_ACTION,
            PartOfSpeech.VERB_MOTION,
            PartOfSpeech.VERB_LINKING,
            PartOfSpeech.VERB_MODAL,
            PartOfSpeech.ADJECTIVE,
            PartOfSpeech.DETERMINER,
            PartOfSpeech.QUANTIFIER,
            PartOfSpeech.PREPOSITION,
            PartOfSpeech.ADVERB,
            PartOfSpeech.SOCIAL,
            PartOfSpeech.UNKNOWN
        ),
        PartOfSpeech.ADVERB to setOf(
            PartOfSpeech.VERB_ACTION,     // (e.g. "not want", "now eat")
            PartOfSpeech.VERB_MOTION,     // (e.g. "not go", "now walk")
            PartOfSpeech.VERB_LINKING,    // (e.g. "not is", "really feel")
            PartOfSpeech.VERB_MODAL,      // (e.g. "not can")
            PartOfSpeech.ADJECTIVE,       // (e.g. "not happy", "very good", "really cold")
            PartOfSpeech.NOUN,            // (e.g. "here water", "there school")
            PartOfSpeech.PREPOSITION,     // (e.g. "away from", "out in")
            PartOfSpeech.ADVERB,          // (e.g. "not now", "very really")
            PartOfSpeech.SOCIAL,
            PartOfSpeech.UNKNOWN
        ),
        PartOfSpeech.SOCIAL to setOf(
            PartOfSpeech.SUBJECT_PRONOUN, // (e.g. "Hello I", "Please I")
            PartOfSpeech.VERB_ACTION,     // (e.g. "Please want", "Please help", "Please eat")
            PartOfSpeech.VERB_MOTION,     // (e.g. "Please go")
            PartOfSpeech.NOUN,            // (e.g. "Please water", "Hello friend")
            PartOfSpeech.PROPER_NOUN,
            PartOfSpeech.ADJECTIVE,
            PartOfSpeech.DETERMINER,
            PartOfSpeech.QUANTIFIER,      // (e.g. "Please more")
            PartOfSpeech.SOCIAL,          // (e.g. "Hello thanks", "Yes please", "No thanks")
            PartOfSpeech.UNKNOWN
        ),
        PartOfSpeech.QUESTION_WORD to setOf(
            PartOfSpeech.VERB_LINKING,    // (e.g. "What is", "Who is", "Where is")
            PartOfSpeech.VERB_MODAL,      // (e.g. "What can", "Where will")
            PartOfSpeech.VERB_ACTION,     // (e.g. "What want", "Who eat")
            PartOfSpeech.VERB_MOTION,     // (e.g. "Where go")
            PartOfSpeech.SUBJECT_PRONOUN, // (e.g. "What you", "Where we")
            PartOfSpeech.NOUN,            // (e.g. "What time", "Which room")
            PartOfSpeech.UNKNOWN
        ),
        PartOfSpeech.UNKNOWN to PartOfSpeech.values().toSet()
    )

    /**
     * Determines whether [button] is valid to be selected given the [currentSentence]
     * under the active grammar rules.
     *
     * Core AAC Usability Guardrails:
     * 1. Navigation, folder, and control buttons ALWAYS remain enabled (`isAlwaysClickable`).
     * 2. When filter is disabled, all buttons are enabled.
     * 3. At start of sentence, permitted sentence starters are allowed.
     * 4. Fails open on UNKNOWN parts of speech to prevent locking the user out.
     */
    fun isButtonValid(
        button: AACButton,
        currentSentence: List<AACButton>,
        isFilterEnabled: Boolean
    ): Boolean {
        if (!isFilterEnabled) return true

        // 1. Guardrail: Folders, navigation, and control buttons are ALWAYS clickable
        if (button.isAlwaysClickable ||
            button.type == ButtonType.FOLDER ||
            button.type == ButtonType.CONTROL ||
            button.category == FitzgeraldCategory.NAVIGATION
        ) {
            return true
        }

        val effectiveCandidate = resolveEffectiveButton(button)

        // 2. Start of Sentence check
        if (currentSentence.isEmpty()) {
            return effectiveCandidate.partOfSpeech in START_OF_SENTENCE_ALLOWED
        }

        val lastButton = resolveEffectiveButton(currentSentence.last())

        // 3. Fail-open if either word is UNKNOWN
        if (effectiveCandidate.partOfSpeech == PartOfSpeech.UNKNOWN ||
            lastButton.partOfSpeech == PartOfSpeech.UNKNOWN
        ) {
            return true
        }

        // 4. Check base POS transition matrix
        val allowedNextPos = ALLOWED_TRANSITIONS[lastButton.partOfSpeech]
            ?: return true // Fail open if transition not explicitly defined

        if (effectiveCandidate.partOfSpeech !in allowedNextPos) {
            return false
        }

        // 5. Contextual semantic & construction refinements
        return evaluateContextualRules(lastButton, effectiveCandidate)
    }

    /**
     * Evaluates fine-grained semantic and contextual rules beyond broad PartOfSpeech categories.
     */
    private fun evaluateContextualRules(
        lastButton: AACButton,
        candidate: AACButton
    ): Boolean {
        val lastWord = lastButton.spokenText.lowercase().trim()

        // Context 1: Motion Verb destination constraint
        // E.g. "Go" + Place Noun ("park", "school", "home") is VALID, but "Go" + Food Noun ("pizza", "water") is INVALID
        if (lastButton.partOfSpeech == PartOfSpeech.VERB_MOTION &&
            (candidate.partOfSpeech == PartOfSpeech.NOUN || candidate.partOfSpeech == PartOfSpeech.PROPER_NOUN)
        ) {
            val isPlace = candidate.semanticTags.contains(SemanticTag.PLACE) ||
                    candidate.spokenText.lowercase().trim() in PLACE_NOUNS_FALLBACK
            return isPlace
        }

        // Context 2: Preposition "to" + Infinitive Verb vs other prepositions
        // "to eat", "to play", "to go" is valid infinitive construction.
        // "in eat", "under sleep" is invalid.
        if (lastButton.partOfSpeech == PartOfSpeech.PREPOSITION &&
            (candidate.partOfSpeech == PartOfSpeech.VERB_ACTION || candidate.partOfSpeech == PartOfSpeech.VERB_MOTION)
        ) {
            return lastWord == "to" || lastWord == "for"
        }

        // Context 3: Prevent direct consecutive identical prepositions ("in on", "to at")
        if (lastButton.partOfSpeech == PartOfSpeech.PREPOSITION &&
            candidate.partOfSpeech == PartOfSpeech.PREPOSITION
        ) {
            return false
        }

        // Context 4: Prevent direct consecutive subject pronouns ("I you", "he she")
        if (lastButton.partOfSpeech == PartOfSpeech.SUBJECT_PRONOUN &&
            candidate.partOfSpeech == PartOfSpeech.SUBJECT_PRONOUN
        ) {
            return false
        }

        return true
    }

    /**
     * Resolves effective grammatical metadata for a button, using its explicit tags or
     * inferring them automatically for backwards compatibility / dynamic user buttons.
     */
    fun resolveEffectiveButton(button: AACButton): AACButton {
        if (button.partOfSpeech != PartOfSpeech.UNKNOWN) {
            return button
        }
        return inferMetadata(button)
    }

    /**
     * Infers PartOfSpeech and SemanticTags from label/category when explicit metadata is absent.
     */
    fun inferMetadata(button: AACButton): AACButton {
        val word = button.spokenText.lowercase().trim()

        val (inferredPos, inferredTags) = when {
            // Pronouns / People
            word in SUBJECT_PRONOUNS -> PartOfSpeech.SUBJECT_PRONOUN to setOf(SemanticTag.PERSON)
            word in OBJECT_PRONOUNS -> PartOfSpeech.OBJECT_PRONOUN to setOf(SemanticTag.PERSON)
            word in POSSESSIVE_PRONOUNS -> PartOfSpeech.POSSESSIVE_PRONOUN to setOf(SemanticTag.GENERAL)
            button.category == FitzgeraldCategory.PEOPLE -> PartOfSpeech.NOUN to setOf(SemanticTag.PERSON)

            // Motion Verbs
            word in MOTION_VERBS -> PartOfSpeech.VERB_MOTION to setOf(SemanticTag.ACTION_CORE)

            // Linking Verbs
            word in LINKING_VERBS -> PartOfSpeech.VERB_LINKING to setOf(SemanticTag.ACTION_CORE)

            // Modal Verbs
            word in MODAL_VERBS -> PartOfSpeech.VERB_MODAL to setOf(SemanticTag.ACTION_CORE)

            // General Action Verbs
            word in ACTION_VERBS -> {
                val tags = mutableSetOf(SemanticTag.ACTION_CORE)
                if (word in setOf("eat", "drink", "bite", "cook")) tags.add(SemanticTag.FOOD)
                if (word in setOf("play", "draw", "sing")) tags.add(SemanticTag.TOY_PLAY)
                if (word in setOf("want", "help", "need", "stop")) tags.add(SemanticTag.REQUEST)
                PartOfSpeech.VERB_ACTION to tags
            }

            // Prepositions
            word in PREPOSITIONS -> PartOfSpeech.PREPOSITION to setOf(SemanticTag.GENERAL)

            // Conjunctions
            word in CONJUNCTIONS -> PartOfSpeech.CONJUNCTION to setOf(SemanticTag.GENERAL)

            // Adverbs & Modifiers
            word in ADVERBS -> PartOfSpeech.ADVERB to setOf(SemanticTag.GENERAL)

            // Determiners
            word in DETERMINERS -> PartOfSpeech.DETERMINER to setOf(SemanticTag.GENERAL)

            // Quantifiers
            word in QUANTIFIERS -> PartOfSpeech.QUANTIFIER to setOf(SemanticTag.REQUEST)

            // Social
            word in SOCIAL_WORDS -> {
                val tags = mutableSetOf(SemanticTag.GENERAL)
                if (word in setOf("please", "help", "more")) tags.add(SemanticTag.REQUEST)
                PartOfSpeech.SOCIAL to tags
            }

            // Adjectives / Feelings / Descriptors
            word in ADJECTIVES || word in EMOTION_ADJECTIVES || word in SENSORY_ADJECTIVES -> {
                val tags = mutableSetOf(SemanticTag.GENERAL)
                if (word in EMOTION_ADJECTIVES) tags.add(SemanticTag.EMOTION)
                if (word in SENSORY_ADJECTIVES) tags.add(SemanticTag.SENSORY)
                PartOfSpeech.ADJECTIVE to tags
            }

            // People Nouns
            word in PEOPLE_NOUNS -> PartOfSpeech.NOUN to setOf(SemanticTag.PERSON)

            // Nouns / Things / Places
            word in PLACE_NOUNS_FALLBACK || word in FOOD_NOUNS_FALLBACK || word in DRINK_NOUNS_FALLBACK || word in TOY_NOUNS_FALLBACK || word in GENERAL_NOUNS -> {
                val tags = mutableSetOf<SemanticTag>()
                if (word in PLACE_NOUNS_FALLBACK) tags.add(SemanticTag.PLACE)
                if (word in FOOD_NOUNS_FALLBACK) tags.add(SemanticTag.FOOD)
                if (word in DRINK_NOUNS_FALLBACK) tags.add(SemanticTag.DRINK)
                if (word in TOY_NOUNS_FALLBACK) tags.add(SemanticTag.TOY_PLAY)
                if (tags.isEmpty()) tags.add(SemanticTag.GENERAL)
                PartOfSpeech.NOUN to tags
            }

            else -> PartOfSpeech.UNKNOWN to emptySet()
        }

        return button.copy(
            partOfSpeech = inferredPos,
            semanticTags = if (button.semanticTags.isNotEmpty()) button.semanticTags else inferredTags
        )
    }

    /**
     * Provides smart next-word single concept suggestions based on current AAC sentence context.
     * Decoupled from the validation engine.
     */
    fun getNextWordSuggestions(
        currentSentence: List<AACButton>,
        maxSuggestions: Int = 3
    ): List<String> {
        if (currentSentence.isEmpty()) {
            return listOf("I", "Want", "Go").take(maxSuggestions)
        }

        val lastButton = resolveEffectiveButton(currentSentence.last())
        val lastWord = lastButton.spokenText.lowercase().trim()

        val candidates = when (lastButton.partOfSpeech) {
            PartOfSpeech.SUBJECT_PRONOUN -> listOf("want", "like", "go", "feel", "need", "am", "can")
            PartOfSpeech.VERB_LINKING -> listOf("happy", "tired", "hungry", "cold", "good", "ready")
            PartOfSpeech.VERB_MOTION -> listOf("to", "home", "bathroom", "outside", "school", "park")
            PartOfSpeech.VERB_ACTION -> {
                if (lastWord in setOf("eat", "drink")) {
                    listOf("water", "pizza", "apple", "cookie", "more")
                } else if (lastWord in setOf("play")) {
                    listOf("blocks", "bubbles", "ball", "puzzle", "with")
                } else {
                    listOf("water", "pizza", "help", "more", "to", "now")
                }
            }
            PartOfSpeech.PREPOSITION -> {
                if (lastWord == "to") {
                    listOf("park", "school", "home", "play", "eat", "bed")
                } else {
                    listOf("school", "park", "bed", "bathroom", "home", "me")
                }
            }
            PartOfSpeech.ADJECTIVE -> listOf("water", "pizza", "toy", "more", "please", "and")
            PartOfSpeech.QUANTIFIER -> listOf("water", "pizza", "cookie", "bubbles", "please")
            PartOfSpeech.NOUN, PartOfSpeech.PROPER_NOUN -> listOf("please", "more", "is", "thanks", "now", "and")
            PartOfSpeech.SOCIAL -> listOf("I", "want", "more", "water", "please")
            PartOfSpeech.ADVERB -> listOf("want", "go", "eat", "happy", "cold")
            else -> listOf("want", "more", "please")
        }

        return candidates.take(maxSuggestions)
    }

    // --- Fallback & Dictionary Vocabularies for Auto-Inference ---

    private val SUBJECT_PRONOUNS = setOf("i", "you", "we", "he", "she", "they", "it", "who")
    private val OBJECT_PRONOUNS = setOf("me", "him", "her", "us", "them")
    private val POSSESSIVE_PRONOUNS = setOf("my", "mine", "your", "yours", "our", "his", "her", "their")

    private val MOTION_VERBS = setOf(
        "go", "come", "drive", "walk", "run", "move", "fall", "travel", "fly", "ride"
    )

    private val LINKING_VERBS = setOf(
        "am", "is", "are", "was", "were", "be", "been", "being", "feel", "look", "sound", "seem", "taste", "smell"
    )

    private val MODAL_VERBS = setOf(
        "can", "will", "would", "could", "should", "must", "might", "may", "do", "have"
    )

    private val ACTION_VERBS = setOf(
        "bite", "break", "bring", "change", "choose", "clean", "cut", "drink", "eat", "find",
        "finish", "fix", "get", "give", "grow", "guess", "hear", "help", "hurt", "keep",
        "know", "learn", "like", "live", "love", "make", "mean", "measure", "need", "open",
        "play", "push", "put", "put on", "read", "see", "share", "show", "sing", "start",
        "stop", "take", "talk", "tell", "try", "use", "wait", "wear", "work"
    )

    private val PREPOSITIONS = setOf(
        "about", "away", "before", "down", "for", "from", "in", "of", "off",
        "on", "out", "over", "to", "under", "up", "with", "at", "by", "behind"
    )

    private val CONJUNCTIONS = setOf("and", "but", "or", "because", "so", "if", "then")

    private val ADVERBS = setOf(
        "not", "always", "never", "now", "later", "again", "here", "there", "very", "really",
        "just", "almost", "also", "less", "more", "most"
    )

    private val DETERMINERS = setOf("a", "an", "the", "this", "that", "these", "those")

    private val QUANTIFIERS = setOf("more", "less", "some", "any", "all", "none", "many", "few")

    private val SOCIAL_WORDS = setOf(
        "hello", "hi", "goodbye", "bye", "yes", "no", "please", "thanks", "thank you", "sorry", "okay"
    )

    private val EMOTION_ADJECTIVES = setOf(
        "happy", "sad", "mad", "angry", "scared", "excited", "proud", "silly", "nervous", "calm"
    )

    private val SENSORY_ADJECTIVES = setOf(
        "cold", "hot", "hungry", "tired", "sick", "hurt", "loud", "quiet", "soft", "hard"
    )

    private val ADJECTIVES = setOf(
        "bad", "big", "cold", "dark", "different", "difficult", "done", "early",
        "easy", "empty", "far", "fast", "favorite", "few", "first", "full", "fun",
        "gone", "good", "great", "happy", "hard", "high", "hot", "last", "late",
        "light", "little", "long", "loud", "new", "next", "nice", "old", "past",
        "pretty", "quiet", "ready", "right", "sad", "same", "second", "silly",
        "slow", "soft", "top", "very", "hungry", "tired", "sick"
    )

    private val PEOPLE_NOUNS = setOf(
        "friend", "mom", "dad", "sister", "brother", "doctor", "teacher", "grandma",
        "grandpa", "family", "baby", "boy", "girl", "person", "people"
    )

    private val GENERAL_NOUNS = setOf(
        "body", "day", "face", "hour", "job", "minute", "month", "morning",
        "name", "party", "problem", "question", "sound", "story", "time",
        "today", "tomorrow", "week", "year", "yesterday"
    )

    private val PLACE_NOUNS_FALLBACK = setOf(
        "home", "school", "park", "bed", "bathroom", "store", "library", "pool",
        "hospital", "outside", "room", "sensory room", "place", "middle", "top"
    )

    private val FOOD_NOUNS_FALLBACK = setOf(
        "apple", "banana", "pizza", "bread", "rice", "soup", "cheese", "chicken",
        "cookie", "ice cream", "food", "snack"
    )

    private val DRINK_NOUNS_FALLBACK = setOf(
        "water", "milk", "juice", "drink"
    )

    private val TOY_NOUNS_FALLBACK = setOf(
        "blocks", "bubbles", "swing", "puzzle", "book", "drawing", "train", "ball",
        "music", "car", "tablet", "toy", "game"
    )
}
