package com.example

import com.example.data.model.AACButton
import com.example.data.model.ButtonType
import com.example.data.model.FitzgeraldCategory
import com.example.data.model.GrammarEngine
import com.example.data.model.PartOfSpeech
import com.example.data.model.PresetBoards
import com.example.data.model.SemanticTag
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Production test suite verifying the metadata-driven AAC Grammar Engine and State Machine.
 */
class GrammarEngineTest {

    // --- Sample AAC Vocabulary Fixtures ---
    private val btnI = AACButton("c_i", "I", "I", FitzgeraldCategory.PEOPLE, partOfSpeech = PartOfSpeech.SUBJECT_PRONOUN, semanticTags = setOf(SemanticTag.PERSON), isCoreWord = true)
    private val btnYou = AACButton("c_you", "You", "you", FitzgeraldCategory.PEOPLE, partOfSpeech = PartOfSpeech.SUBJECT_PRONOUN, semanticTags = setOf(SemanticTag.PERSON), isCoreWord = true)
    private val btnShe = AACButton("c_she", "She", "she", FitzgeraldCategory.PEOPLE, partOfSpeech = PartOfSpeech.SUBJECT_PRONOUN, semanticTags = setOf(SemanticTag.PERSON), isCoreWord = true)

    private val btnWant = AACButton("c_want", "Want", "want", FitzgeraldCategory.ACTION, partOfSpeech = PartOfSpeech.VERB_ACTION, semanticTags = setOf(SemanticTag.REQUEST, SemanticTag.ACTION_CORE), isCoreWord = true)
    private val btnEat = AACButton("c_eat", "Eat", "eat", FitzgeraldCategory.ACTION, partOfSpeech = PartOfSpeech.VERB_ACTION, semanticTags = setOf(SemanticTag.FOOD, SemanticTag.ACTION_CORE), isCoreWord = true)
    private val btnGo = AACButton("c_go", "Go", "go", FitzgeraldCategory.ACTION, partOfSpeech = PartOfSpeech.VERB_MOTION, semanticTags = setOf(SemanticTag.ACTION_CORE), isCoreWord = true)
    private val btnIs = AACButton("c_is", "Is", "is", FitzgeraldCategory.ACTION, partOfSpeech = PartOfSpeech.VERB_LINKING, semanticTags = setOf(SemanticTag.ACTION_CORE), isCoreWord = true)
    private val btnPlay = AACButton("c_play", "Play", "play", FitzgeraldCategory.ACTION, partOfSpeech = PartOfSpeech.VERB_ACTION, semanticTags = setOf(SemanticTag.TOY_PLAY, SemanticTag.ACTION_CORE), isCoreWord = true)

    private val btnTo = AACButton("c_to", "To", "to", FitzgeraldCategory.FUNCTION, partOfSpeech = PartOfSpeech.PREPOSITION, semanticTags = setOf(SemanticTag.GENERAL))
    private val btnIn = AACButton("c_in", "In", "in", FitzgeraldCategory.FUNCTION, partOfSpeech = PartOfSpeech.PREPOSITION, semanticTags = setOf(SemanticTag.GENERAL))

    private val btnHappy = AACButton("c_happy", "Happy", "happy", FitzgeraldCategory.ADJECTIVE, partOfSpeech = PartOfSpeech.ADJECTIVE, semanticTags = setOf(SemanticTag.EMOTION), isCoreWord = true)
    private val btnMore = AACButton("c_more", "More", "more", FitzgeraldCategory.ADJECTIVE, partOfSpeech = PartOfSpeech.QUANTIFIER, semanticTags = setOf(SemanticTag.REQUEST), isCoreWord = true)
    private val btnCold = AACButton("c_cold", "Cold", "cold", FitzgeraldCategory.ADJECTIVE, partOfSpeech = PartOfSpeech.ADJECTIVE, semanticTags = setOf(SemanticTag.SENSORY), isCoreWord = true)

    private val btnPizza = AACButton("c_pizza", "Pizza", "pizza", FitzgeraldCategory.NOUN, partOfSpeech = PartOfSpeech.NOUN, semanticTags = setOf(SemanticTag.FOOD))
    private val btnWater = AACButton("c_water", "Water", "water", FitzgeraldCategory.NOUN, partOfSpeech = PartOfSpeech.NOUN, semanticTags = setOf(SemanticTag.DRINK, SemanticTag.FOOD), isCoreWord = true)
    private val btnMilk = AACButton("c_milk", "Milk", "milk", FitzgeraldCategory.NOUN, partOfSpeech = PartOfSpeech.NOUN, semanticTags = setOf(SemanticTag.DRINK, SemanticTag.FOOD))
    private val btnPark = AACButton("c_park", "Park", "park", FitzgeraldCategory.NOUN, partOfSpeech = PartOfSpeech.NOUN, semanticTags = setOf(SemanticTag.PLACE))
    private val btnSchool = AACButton("c_school", "School", "school", FitzgeraldCategory.NOUN, partOfSpeech = PartOfSpeech.NOUN, semanticTags = setOf(SemanticTag.PLACE))

    private val btnPlease = AACButton("c_please", "Please", "please", FitzgeraldCategory.SOCIAL, partOfSpeech = PartOfSpeech.SOCIAL, semanticTags = setOf(SemanticTag.REQUEST), isCoreWord = true)
    private val btnThanks = AACButton("c_thanks", "Thanks", "thank you", FitzgeraldCategory.SOCIAL, partOfSpeech = PartOfSpeech.SOCIAL, semanticTags = setOf(SemanticTag.GENERAL), isCoreWord = true)

    private val folderActions = AACButton("f_actions", "Actions 📂", "Actions folder", FitzgeraldCategory.NAVIGATION, ButtonType.FOLDER, targetBoardId = "sub_actions", isAlwaysClickable = true)
    private val btnControlClear = AACButton("ctrl_clear", "Clear", "clear sentence", FitzgeraldCategory.NAVIGATION, ButtonType.CONTROL, isAlwaysClickable = true)

    @Test
    fun testStartOfSentence_allowsValidStarters() {
        val emptySentence = emptyList<AACButton>()

        assertTrue("Subject pronoun allowed at start", GrammarEngine.isButtonValid(btnI, emptySentence, true))
        assertTrue("Verb allowed at start (e.g. Stop, Go, Eat)", GrammarEngine.isButtonValid(btnWant, emptySentence, true))
        assertTrue("Motion verb allowed at start", GrammarEngine.isButtonValid(btnGo, emptySentence, true))
        assertTrue("Social word allowed at start (e.g. Please, Hello)", GrammarEngine.isButtonValid(btnPlease, emptySentence, true))
        assertTrue("Quantifier allowed at start (e.g. More)", GrammarEngine.isButtonValid(btnMore, emptySentence, true))
        assertTrue("Noun allowed at start", GrammarEngine.isButtonValid(btnWater, emptySentence, true))
    }

    @Test
    fun testGuardrails_alwaysClickableButtons() {
        val sentence = listOf(btnI) // Normally restricts direct food nouns

        // Folders and control buttons MUST remain clickable at all times
        assertTrue("Folder button is always clickable", GrammarEngine.isButtonValid(folderActions, sentence, true))
        assertTrue("Control button is always clickable", GrammarEngine.isButtonValid(btnControlClear, sentence, true))

        // When grammar filter is disabled, all buttons are allowed
        assertTrue("Filter disabled allows any button", GrammarEngine.isButtonValid(btnPizza, sentence, false))
    }

    @Test
    fun testSentenceFormulation_IWantGoToPark() {
        // Step 1: Start -> Select "I"
        var sentence = listOf(btnI)

        // After "I", Actions/Verbs are valid
        assertTrue("I -> Want is valid", GrammarEngine.isButtonValid(btnWant, sentence, true))
        assertTrue("I -> Go is valid", GrammarEngine.isButtonValid(btnGo, sentence, true))
        // Direct noun "I Pizza" is invalid in standard AAC grammar flow
        assertFalse("I -> Pizza is invalid", GrammarEngine.isButtonValid(btnPizza, sentence, true))
        // Consecutive subject pronoun "I You" is invalid
        assertFalse("I -> You is invalid", GrammarEngine.isButtonValid(btnYou, sentence, true))

        // Step 2: "I" + "Want"
        sentence = listOf(btnI, btnWant)
        assertTrue("I Want -> Go is valid (verb chaining)", GrammarEngine.isButtonValid(btnGo, sentence, true))
        assertTrue("I Want -> Pizza is valid (direct object)", GrammarEngine.isButtonValid(btnPizza, sentence, true))
        assertTrue("I Want -> To is valid (infinitive marker)", GrammarEngine.isButtonValid(btnTo, sentence, true))

        // Step 3: "I" + "Want" + "Go"
        sentence = listOf(btnI, btnWant, btnGo)
        assertTrue("Go -> To is valid (preposition)", GrammarEngine.isButtonValid(btnTo, sentence, true))
        assertTrue("Go -> Park is valid (direct place noun)", GrammarEngine.isButtonValid(btnPark, sentence, true))
        assertFalse("Go -> Pizza is invalid (non-place noun)", GrammarEngine.isButtonValid(btnPizza, sentence, true))

        // Step 4: "I" + "Want" + "Go" + "To"
        sentence = listOf(btnI, btnWant, btnGo, btnTo)
        assertTrue("To -> Park is valid (destination noun)", GrammarEngine.isButtonValid(btnPark, sentence, true))
        assertTrue("To -> School is valid (destination noun)", GrammarEngine.isButtonValid(btnSchool, sentence, true))
        assertTrue("To -> Eat is valid (infinitive verb)", GrammarEngine.isButtonValid(btnEat, sentence, true))
        assertFalse("To -> In is invalid (stacked preposition)", GrammarEngine.isButtonValid(btnIn, sentence, true))

        // Step 5: "I" + "Want" + "Go" + "To" + "Park"
        sentence = listOf(btnI, btnWant, btnGo, btnTo, btnPark)
        assertTrue("Park -> Please is valid (social addition)", GrammarEngine.isButtonValid(btnPlease, sentence, true))
        assertTrue("Park -> Thanks is valid", GrammarEngine.isButtonValid(btnThanks, sentence, true))
    }

    @Test
    fun testSentenceFormulation_SheIsHappy() {
        // Step 1: "She"
        var sentence = listOf(btnShe)
        assertTrue("She -> Is is valid (linking verb)", GrammarEngine.isButtonValid(btnIs, sentence, true))
        assertTrue("She -> Happy is valid (AAC predicate adjective shortcut)", GrammarEngine.isButtonValid(btnHappy, sentence, true))
        assertFalse("She -> Water is invalid (direct noun)", GrammarEngine.isButtonValid(btnWater, sentence, true))

        // Step 2: "She" + "Is"
        sentence = listOf(btnShe, btnIs)
        assertTrue("She Is -> Happy is valid (predicate adjective)", GrammarEngine.isButtonValid(btnHappy, sentence, true))
        assertTrue("She Is -> Cold is valid (sensory adjective)", GrammarEngine.isButtonValid(btnCold, sentence, true))
        assertTrue("She Is -> In is valid (locative preposition)", GrammarEngine.isButtonValid(btnIn, sentence, true))

        // Step 3: "She" + "Is" + "Happy"
        sentence = listOf(btnShe, btnIs, btnHappy)
        assertTrue("Happy -> Please is valid", GrammarEngine.isButtonValid(btnPlease, sentence, true))
        assertTrue("Happy -> Thanks is valid", GrammarEngine.isButtonValid(btnThanks, sentence, true))
    }

    @Test
    fun testSentenceFormulation_MoreMilkPlease() {
        // Step 1: "More" (Quantifier)
        var sentence = listOf(btnMore)
        assertTrue("More -> Milk is valid", GrammarEngine.isButtonValid(btnMilk, sentence, true))
        assertTrue("More -> Water is valid", GrammarEngine.isButtonValid(btnWater, sentence, true))
        assertTrue("More -> Pizza is valid", GrammarEngine.isButtonValid(btnPizza, sentence, true))
        assertTrue("More -> Please is valid", GrammarEngine.isButtonValid(btnPlease, sentence, true))

        // Step 2: "More" + "Milk"
        sentence = listOf(btnMore, btnMilk)
        assertTrue("Milk -> Please is valid", GrammarEngine.isButtonValid(btnPlease, sentence, true))
        assertTrue("Milk -> Thanks is valid", GrammarEngine.isButtonValid(btnThanks, sentence, true))
        assertTrue("Milk -> Is is valid", GrammarEngine.isButtonValid(btnIs, sentence, true))
    }

    @Test
    fun testFailOpen_onUnclassifiedCustomWord() {
        val unknownCustomButton = AACButton(
            id = "custom_x",
            label = "CustomGizmo",
            category = FitzgeraldCategory.NOUN,
            partOfSpeech = PartOfSpeech.UNKNOWN
        )

        val sentence = listOf(btnI)
        // Even if noun after I is normally blocked, UNKNOWN word fails open so user is never locked out
        assertTrue("Unknown custom word fails open", GrammarEngine.isButtonValid(unknownCustomButton, sentence, true))
    }

    @Test
    fun testMetadataAutoInference() {
        val legacyRawButton = AACButton(
            id = "raw_eat",
            label = "Eat",
            spokenText = "eat",
            category = FitzgeraldCategory.ACTION
        )

        val resolved = GrammarEngine.resolveEffectiveButton(legacyRawButton)
        assertEquals(PartOfSpeech.VERB_ACTION, resolved.partOfSpeech)
        assertTrue(resolved.semanticTags.contains(SemanticTag.ACTION_CORE))
        assertTrue(resolved.semanticTags.contains(SemanticTag.FOOD))
    }

    @Test
    fun testSuggestionsProvider_decoupled() {
        val sentence = listOf(btnI)
        val suggestions = GrammarEngine.getNextWordSuggestions(sentence, maxSuggestions = 3)

        assertEquals(3, suggestions.size)
        assertTrue("Suggestions contain common verbs", suggestions.contains("want") || suggestions.contains("like") || suggestions.contains("go"))
    }
}
