package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

enum class FitzgeraldCategory(val hexColor: String, val textColor: String) {
    PEOPLE("#FFF3CD", "#6B5100"),      // Pronouns / People (Yellow)
    ACTION("#D4EDDA", "#155724"),      // Verbs / Actions (Green)
    NOUN("#FFE8D6", "#8F3B00"),        // Nouns / Things (Orange)
    ADJECTIVE("#D1ECF1", "#0C5460"),   // Adjectives / Descriptions (Blue)
    SOCIAL("#F8D7DA", "#721C24"),      // Social / Greetings (Pink/Purple)
    FUNCTION("#E2E3E5", "#383D41"),    // Function / Little Words (Gray)
    NAVIGATION("#E8F0FE", "#1A73E8")   // Navigational folder cells (Light Blue)
}

enum class ButtonType {
    WORD,         // Normal word button (speaks & adds to sentence bar)
    FOLDER,       // Navigation button (navigates to another board/folder)
    CONTROL       // Active action (e.g. Back, Home, Speak, Clear)
}

enum class PartOfSpeech {
    SUBJECT_PRONOUN,    // I, you, we, he, she, they, it, who
    OBJECT_PRONOUN,     // me, you, him, her, us, them, it
    POSSESSIVE_PRONOUN, // my, mine, your, yours, our, his, her, their
    NOUN,               // General concrete and abstract nouns (water, pizza, school, toy)
    PROPER_NOUN,        // Specific names (Mom, Dad, Dr. Smith)
    VERB_ACTION,        // Transitive / Intransitive action verbs (eat, drink, play, help, want, like, stop)
    VERB_LINKING,       // Copula / linking verbs (am, is, are, was, were, be, feel, look, sound, seem)
    VERB_MOTION,        // Motion verbs (go, come, walk, run, drive, ride, move)
    VERB_MODAL,         // Auxiliary & modal verbs (can, will, would, could, should, do, have)
    ADJECTIVE,          // Descriptors and state adjectives (big, small, good, bad, happy, cold, hungry)
    DETERMINER,         // Articles & demonstratives (a, the, this, that, these, those)
    QUANTIFIER,         // Quantities and numbers (more, less, some, all, many, few, 1, 2)
    PREPOSITION,        // Spatial and relational prepositions (to, in, on, under, with, for, at, from)
    CONJUNCTION,        // Connecting words (and, but, or, because, so)
    ADVERB,             // Modifiers, time & negation (not, always, never, now, later, here, there, really, very)
    SOCIAL,             // Pragmatic / social phrases (hello, bye, please, thanks, yes, no, sorry)
    QUESTION_WORD,      // Wh-interrogatives (what, where, when, why, who, how)
    UNKNOWN             // Fallback for unclassified custom words
}

enum class SemanticTag {
    PERSON,
    FOOD,
    DRINK,
    PLACE,
    TIME,
    EMOTION,
    SENSORY,
    BODY_PART,
    TOY_PLAY,
    CLOTHING,
    ANIMAL,
    VEHICLE,
    ACTION_CORE,
    REQUEST,
    GENERAL
}

@JsonClass(generateAdapter = true)
data class AACButton(
    val id: String,
    val label: String,
    val spokenText: String = label,
    val category: FitzgeraldCategory,
    val type: ButtonType = ButtonType.WORD,
    val imageUrl: String? = null,          // Remote URL from OpenSymbols
    val localIconId: String? = null,       // Local drawables if any
    val targetBoardId: String? = null,     // Id of sub-board if FOLDER
    val isHidden: Boolean = false,         // Allow users to hide/show certain tiles
    val partOfSpeech: PartOfSpeech = PartOfSpeech.UNKNOWN,
    val semanticTags: Set<SemanticTag> = emptySet(),
    val isCoreWord: Boolean = false,
    val isAlwaysClickable: Boolean = (type == ButtonType.FOLDER || type == ButtonType.CONTROL || category == FitzgeraldCategory.NAVIGATION)
)

@Entity(tableName = "custom_boards")
@JsonClass(generateAdapter = true)
data class Board(
    @PrimaryKey val id: String,
    val name: String,
    val isCustom: Boolean = false,
    val columns: Int = 4,
    val rows: Int = 3,
    val buttonsJson: String // Serialized List<AACButton> to easily store flexible layouts
)

@Entity(tableName = "phrase_history")
data class PhraseHistory(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val phraseText: String,
    val timestamp: Long = System.currentTimeMillis()
)
