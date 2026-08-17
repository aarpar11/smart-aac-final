package com.example.ui.viewmodel

import android.app.Application
import android.media.MediaPlayer
import android.speech.tts.TextToSpeech
import android.util.Base64
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.data.api.GeminiClient
import com.example.data.api.GeminiContent
import com.example.data.api.GeminiGenerationConfig
import com.example.data.api.GeminiInlineData
import com.example.data.api.GeminiPart
import com.example.data.api.GeminiPrebuiltVoiceConfig
import com.example.data.api.GeminiRequest
import com.example.data.api.GeminiSpeechConfig
import com.example.data.api.GeminiThinkingConfig
import com.example.data.api.GeminiVoiceConfig
import com.example.data.api.OpenSymbolsClient
import com.example.data.api.OpenSymbolItem
import com.example.data.db.AppDatabase
import com.example.data.model.AACButton
import com.example.data.model.Board
import com.example.data.model.ButtonType
import com.example.data.model.FitzgeraldCategory
import com.example.data.model.PartOfSpeech
import com.example.data.model.PhraseHistory
import com.example.data.model.PresetBoards
import com.example.data.model.SemanticTag
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.Locale

class AACViewModel(application: Application) : AndroidViewModel(application), TextToSpeech.OnInitListener {

    private val db = AppDatabase.getDatabase(application)
    private val boardDao = db.boardDao()
    private val phraseHistoryDao = db.phraseHistoryDao()

    // Screen navigation stack of Boards
    private val _boardStack = MutableStateFlow<List<Board>>(listOf(PresetBoards.coreHomeBoard))
    val currentBoard: StateFlow<Board> = MutableStateFlow(PresetBoards.coreHomeBoard).apply {
        viewModelScope.launch {
            _boardStack.collect { stack ->
                value = stack.lastOrNull() ?: PresetBoards.coreHomeBoard
            }
        }
    }
    val previousBoardAvailable: StateFlow<Boolean> = MutableStateFlow(false).apply {
        viewModelScope.launch {
            _boardStack.collect { stack ->
                value = stack.size > 1
            }
        }
    }

    // Sentence bar sequence
    private val _sentence = MutableStateFlow<List<AACButton>>(emptyList())
    val sentence: StateFlow<List<AACButton>> = _sentence.asStateFlow()

    // Audio / TTS settings
    private var textToSpeech: TextToSpeech? = null
    private val _ttsInitialized = MutableStateFlow(false)

    private val _useGeminiTts = MutableStateFlow(false)
    val useGeminiTts: StateFlow<Boolean> = _useGeminiTts.asStateFlow()

    private val _selectedVoice = MutableStateFlow("Kore") // "Kore", "Puck", "Fenrir", "Aoede", "Charon"
    val selectedVoice: StateFlow<String> = _selectedVoice.asStateFlow()

    private val _ttsLoading = MutableStateFlow(false)
    val ttsLoading: StateFlow<Boolean> = _ttsLoading.asStateFlow()

    private val _aiIconLoading = MutableStateFlow(false)
    val aiIconLoading: StateFlow<Boolean> = _aiIconLoading.asStateFlow()

    private var mediaPlayer: MediaPlayer? = null

    // OpenSymbols Search
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<List<OpenSymbolItem>>(emptyList())
    val searchResults: StateFlow<List<OpenSymbolItem>> = _searchResults.asStateFlow()

    private val _searchLoading = MutableStateFlow(false)
    val searchLoading: StateFlow<Boolean> = _searchLoading.asStateFlow()

    // Smart AI Assist states (High Thinking model output)
    private val _aiElaboratedText = MutableStateFlow("")
    val aiElaboratedText: StateFlow<String> = _aiElaboratedText.asStateFlow()

    private val _aiLoading = MutableStateFlow(false)
    val aiLoading: StateFlow<Boolean> = _aiLoading.asStateFlow()

    private val _suggestedNextWords = MutableStateFlow<List<String>>(emptyList())
    val suggestedNextWords: StateFlow<List<String>> = _suggestedNextWords.asStateFlow()

    private val _aiDialogResponse = MutableStateFlow("")
    val aiDialogResponse: StateFlow<String> = _aiDialogResponse.asStateFlow()

    // List of custom boards created by users
    private val _userCustomBoards = MutableStateFlow<List<Board>>(emptyList())
    val userCustomBoards: StateFlow<List<Board>> = _userCustomBoards.asStateFlow()

    // Board density mode (True = Standard Simple 12-button grid, False = Compact 32-button core Vocal Flair board)
    private val _boardDensitySimple = MutableStateFlow(true)
    val boardDensitySimple: StateFlow<Boolean> = _boardDensitySimple.asStateFlow()

    // New Display & Customization Settings
    private val _cardDisplayMode = MutableStateFlow(CardDisplayMode.BOTH)
    val cardDisplayMode: StateFlow<CardDisplayMode> = _cardDisplayMode.asStateFlow()

    private val _textPosition = MutableStateFlow(TextPosition.BELOW_SYMBOL)
    val textPosition: StateFlow<TextPosition> = _textPosition.asStateFlow()

    private val _isHighContrast = MutableStateFlow(false)
    val isHighContrast: StateFlow<Boolean> = _isHighContrast.asStateFlow()

    private val _selectedSkinTone = MutableStateFlow(SkinTone.DEFAULT)
    val selectedSkinTone: StateFlow<SkinTone> = _selectedSkinTone.asStateFlow()

    private val _speakOnTileTap = MutableStateFlow(true)
    val speakOnTileTap: StateFlow<Boolean> = _speakOnTileTap.asStateFlow()

    private val _isGrammarFilterEnabled = MutableStateFlow(true)
    val isGrammarFilterEnabled: StateFlow<Boolean> = _isGrammarFilterEnabled.asStateFlow()

    fun setCardDisplayMode(mode: CardDisplayMode) {
        _cardDisplayMode.value = mode
    }

    fun setTextPosition(position: TextPosition) {
        _textPosition.value = position
    }

    fun setHighContrast(enabled: Boolean) {
        _isHighContrast.value = enabled
    }

    fun setSkinTone(tone: SkinTone) {
        _selectedSkinTone.value = tone
    }

    fun setSpeakOnTileTap(enabled: Boolean) {
        _speakOnTileTap.value = enabled
    }

    fun setGrammarFilterEnabled(enabled: Boolean) {
        _isGrammarFilterEnabled.value = enabled
    }

    fun isButtonGrammaticallyValid(button: AACButton): Boolean {
        return com.example.data.model.GrammarEngine.isButtonValid(
            button = button,
            currentSentence = _sentence.value,
            isFilterEnabled = _isGrammarFilterEnabled.value
        )
    }

    init {
        textToSpeech = TextToSpeech(application, this)
        loadUserBoards()
        
        // Initialize default saved phrases board and pre-set home override
        viewModelScope.launch {
            val existing = boardDao.getBoardById("saved_phrases")
            if (existing == null) {
                val preloads = listOf(
                    AACButton("sp_1", "I need help", "I need help", FitzgeraldCategory.SOCIAL, partOfSpeech = PartOfSpeech.SOCIAL, semanticTags = setOf(SemanticTag.REQUEST)),
                    AACButton("sp_2", "Please give me water", "Please give me water", FitzgeraldCategory.SOCIAL, partOfSpeech = PartOfSpeech.SOCIAL, semanticTags = setOf(SemanticTag.REQUEST)),
                    AACButton("sp_3", "Go outside", "Go outside", FitzgeraldCategory.SOCIAL, partOfSpeech = PartOfSpeech.SOCIAL, semanticTags = setOf(SemanticTag.GENERAL))
                )
                val board = Board(
                    id = "saved_phrases",
                    name = "Saved Phrases 📁",
                    isCustom = true,
                    columns = 3,
                    rows = 4,
                    buttonsJson = PresetBoards.serializeButtons(preloads)
                )
                boardDao.insertBoard(board)
            }
            
            // Preload home board override on start if it exists in DB
            val dbHome = boardDao.getBoardById("core_home")
            if (dbHome != null) {
                _boardStack.value = listOf(dbHome)
            }
        }
    }

    private fun loadUserBoards() {
        viewModelScope.launch {
            boardDao.getAllBoards().collect { boards ->
                _userCustomBoards.value = boards
                
                // Update active board stack with any database overrides
                val currentStack = _boardStack.value
                val updatedStack = currentStack.map { board ->
                    boards.find { it.id == board.id } ?: board
                }
                _boardStack.value = updatedStack
            }
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            textToSpeech?.language = Locale.US
            _ttsInitialized.value = true
        } else {
            Log.e("AACViewModel", "Local TextToSpeech initialization failed.")
        }
    }

    fun setBoardDensity(simple: Boolean) {
        _boardDensitySimple.value = simple
    }

    // Handles direct button tap
    fun onButtonTapped(button: AACButton) {
        when (button.type) {
            ButtonType.WORD -> {
                // Speak word instantly if enabled in settings
                if (_speakOnTileTap.value) {
                    speakInstant(button.spokenText)
                }
                // Add to active sentence
                _sentence.value = _sentence.value + button
                // Query next suggested words reactively based on sentence
                triggerNextWordSuggestions()
            }
            ButtonType.FOLDER -> {
                if (_speakOnTileTap.value) {
                    speakInstant(button.spokenText)
                }
                navigateToFolder(button.targetBoardId)
            }
            ButtonType.CONTROL -> {
                // Generic control actions is handled elsewhere
            }
        }
    }

    private fun navigateToFolder(boardId: String?) {
        if (boardId == null) return
        viewModelScope.launch {
            // Check Database version first
            val dbBoard = boardDao.getBoardById(boardId)
            if (dbBoard != null) {
                _boardStack.value = _boardStack.value + dbBoard
                return@launch
            }
            // Check presets
            val preset = PresetBoards.allPresetBoards[boardId]
            if (preset != null) {
                _boardStack.value = _boardStack.value + preset
                return@launch
            }
            // Check user custom boards
            val custom = _userCustomBoards.value.find { it.id == boardId }
            if (custom != null) {
                _boardStack.value = _boardStack.value + custom
            }
        }
    }

    fun navigateBack() {
        val currentStack = _boardStack.value
        if (currentStack.size > 1) {
            _boardStack.value = currentStack.dropLast(1)
        }
    }

    fun navigateToHome() {
        viewModelScope.launch {
            val dbHome = boardDao.getBoardById("core_home")
            _boardStack.value = listOf(dbHome ?: PresetBoards.coreHomeBoard)
        }
    }

    fun clearSentence() {
        _sentence.value = emptyList()
        _aiElaboratedText.value = ""
        _aiDialogResponse.value = ""
        _suggestedNextWords.value = emptyList()
    }

    fun removeLastFromSentence() {
        val current = _sentence.value
        if (current.isNotEmpty()) {
            _sentence.value = current.dropLast(1)
            triggerNextWordSuggestions()
        }
    }

    fun setSpeechMode(geminiSpeech: Boolean) {
        _useGeminiTts.value = geminiSpeech
    }

    fun setVoice(voice: String) {
        _selectedVoice.value = voice
    }

    // Instant offline speak for tactile click guidance
    fun speakInstant(text: String) {
        if (_ttsInitialized.value) {
            textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "instant_id")
        }
    }

    // Speak entire sentence using chosen TTS (local vs Gemini High Quality)
    fun speakSentence(text: String) {
        if (text.isBlank()) return

        // Log sentence into Room History
        viewModelScope.launch {
            phraseHistoryDao.insertPhrase(PhraseHistory(phraseText = text))
        }

        if (_useGeminiTts.value && BuildConfig.GEMINI_API_KEY.isNotEmpty() && BuildConfig.GEMINI_API_KEY != "MY_GEMINI_API_KEY") {
            speakWithGeminiTTS(text)
        } else {
            // Local TTS fallback
            speakInstant(text)
        }
    }

    private fun speakWithGeminiTTS(text: String) {
        _ttsLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val apiKey = BuildConfig.GEMINI_API_KEY
                val request = GeminiRequest(
                    contents = listOf(
                        GeminiContent(
                            parts = listOf(GeminiPart(text = "Say clearly and warmly: $text"))
                        )
                    ),
                    generationConfig = GeminiGenerationConfig(
                        responseModalities = listOf("AUDIO"),
                        speechConfig = GeminiSpeechConfig(
                            voiceConfig = GeminiVoiceConfig(
                                prebuiltVoiceConfig = GeminiPrebuiltVoiceConfig(voiceName = _selectedVoice.value.lowercase())
                            )
                        )
                    )
                )

                val response = GeminiClient.apiService.generateTTS(apiKey, request)
                val base64Audio = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.inlineData?.data

                if (base64Audio != null) {
                    val audioBytes = Base64.decode(base64Audio, Base64.DEFAULT)
                    withContext(Dispatchers.Main) {
                        playAudioBytes(audioBytes)
                    }
                } else {
                    Log.e("AACViewModel", "Gemini TTS returned empty audio payload.")
                    withContext(Dispatchers.Main) {
                        speakInstant(text) // fallback
                    }
                }
            } catch (e: Exception) {
                Log.e("AACViewModel", "Failed Gemini TTS: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    speakInstant(text) // fallback
                }
            } finally {
                withContext(Dispatchers.Main) {
                    _ttsLoading.value = false
                }
            }
        }
    }

    private fun playAudioBytes(audioBytes: ByteArray) {
        try {
            mediaPlayer?.release()
            val tempFile = File(getApplication<Application>().cacheDir, "speak_temp.wav")
            FileOutputStream(tempFile).use { fos ->
                fos.write(audioBytes)
            }
            mediaPlayer = MediaPlayer().apply {
                setDataSource(tempFile.absolutePath)
                prepare()
                start()
            }
        } catch (e: Exception) {
            Log.e("AACViewModel", "MediaPlayer playback error: ${e.message}")
        }
    }

    // Searches OpenSymbols API
    fun seekSymbol(query: String) {
        _searchQuery.value = query
        if (query.isBlank()) {
            _searchResults.value = emptyList()
            return
        }
        _searchLoading.value = true
        viewModelScope.launch {
            try {
                val results = OpenSymbolsClient.apiService.searchSymbols(query)
                _searchResults.value = results.take(15)
            } catch (e: Exception) {
                Log.e("AACViewModel", "OpenSymbols search failed: ${e.message}")
                _searchResults.value = emptyList()
            } finally {
                _searchLoading.value = false
            }
        }
    }

    // AI Smart Elaborator (uses High Thinking gemini-3.1-pro-preview)
    fun elaborateSentence() {
        val currentWords = _sentence.value.joinToString(" ") { it.label }
        if (currentWords.isBlank()) return

        _aiLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val apiKey = BuildConfig.GEMINI_API_KEY
                val request = GeminiRequest(
                    contents = listOf(
                        GeminiContent(
                            parts = listOf(
                                GeminiPart(text = "The raw AAC icons clicked are: '$currentWords'. Please translate these communication icons into an articulate, natural first-person conversational sentence. Keep it expressive yet direct, suitable for an autistic user communicating their primary need or state.")
                            )
                        )
                    ),
                    generationConfig = GeminiGenerationConfig(
                        thinkingConfig = GeminiThinkingConfig(thinkingLevel = "high"),
                        temperature = 0.5
                    )
                )

                val response = GeminiClient.apiService.generateContentHighThinking(apiKey, request)
                val responseText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: ""
                withContext(Dispatchers.Main) {
                    _aiElaboratedText.value = responseText.trim()
                }
            } catch (e: Exception) {
                Log.e("AACViewModel", "Sentence elaboration failed: ${e.message}")
                withContext(Dispatchers.Main) {
                    _aiElaboratedText.value = "Fallback: " + _sentence.value.joinToString(" ") { it.spokenText }
                }
            } finally {
                withContext(Dispatchers.Main) {
                    _aiLoading.value = false
                }
            }
        }
    }

    // AI Dialog (uses High Thinking gemini-3.1-pro-preview)
    fun chatWithAI() {
        val currentWords = _sentence.value.joinToString(" ") { it.label }
        if (currentWords.isBlank()) return

        _aiLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val apiKey = BuildConfig.GEMINI_API_KEY
                val request = GeminiRequest(
                    contents = listOf(
                        GeminiContent(
                            parts = listOf(
                                GeminiPart(text = "User communicated using AAC icons: '$currentWords'. Respond as a kind, therapeutic companion assisting the autistic individual. Keep your reply simple (1-2 sentences), affirming, and clear.")
                            )
                        )
                    ),
                    generationConfig = GeminiGenerationConfig(
                        thinkingConfig = GeminiThinkingConfig(thinkingLevel = "high"),
                        temperature = 0.7
                    )
                )

                val response = GeminiClient.apiService.generateContentHighThinking(apiKey, request)
                val responseText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: ""
                withContext(Dispatchers.Main) {
                    _aiDialogResponse.value = responseText.trim()
                }
            } catch (e: Exception) {
                Log.e("AACViewModel", "AI Dialog failed: ${e.message}")
                withContext(Dispatchers.Main) {
                    _aiDialogResponse.value = "I hear you! Let's communicate together."
                }
            } finally {
                withContext(Dispatchers.Main) {
                    _aiLoading.value = false
                }
            }
        }
    }

    private var suggestionsJob: Job? = null

    // Suggests 3 logical single-phrase concept next clicks based on recent words (uses fast model + local fallback on rate-limit/offline)
    private fun triggerNextWordSuggestions() {
        suggestionsJob?.cancel()

        val currentWords = _sentence.value.joinToString(" ") { it.label }
        if (currentWords.isBlank()) {
            _suggestedNextWords.value = emptyList()
            return
        }

        val localFallbacks = com.example.data.model.GrammarEngine.getNextWordSuggestions(_sentence.value)

        suggestionsJob = viewModelScope.launch(Dispatchers.IO) {
            delay(450) // Debounce rapid button taps to avoid API rate limiting
            try {
                val apiKey = BuildConfig.GEMINI_API_KEY
                if (apiKey.isBlank()) {
                    withContext(Dispatchers.Main) {
                        _suggestedNextWords.value = localFallbacks
                    }
                    return@launch
                }

                val request = GeminiRequest(
                    contents = listOf(
                        GeminiContent(
                            parts = listOf(
                                GeminiPart(text = "Based on these active AAC symbols: '$currentWords', what are 3 single logical words or short terms that the user is most likely to tap next? List only the 3 words separated by commas, with no other text.")
                            )
                        )
                    ),
                    generationConfig = GeminiGenerationConfig(
                        temperature = 0.2
                    )
                )
                val response = GeminiClient.apiService.generateContentFast(apiKey, request)
                val responseText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: ""
                val suggestions = responseText.split(",")
                    .map { it.replace(".", "").replace("`", "").trim() }
                    .filter { it.isNotEmpty() }
                    .take(3)

                withContext(Dispatchers.Main) {
                    _suggestedNextWords.value = if (suggestions.isNotEmpty()) suggestions else localFallbacks
                }
            } catch (e: Exception) {
                // HTTP 429 rate limits or network issues: fall back seamlessly to local grammar engine
                withContext(Dispatchers.Main) {
                    _suggestedNextWords.value = localFallbacks
                }
            }
        }
    }

    // Uses High Thinking model to design a brand new 12-button communication board for any custom topic
    fun generateCustomBoard(topic: String, onFinished: (Boolean) -> Unit) {
        if (topic.isBlank()) return
        _aiLoading.value = true

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val apiKey = BuildConfig.GEMINI_API_KEY
                val schemaPrompt = """
                    Design a custom 3x4 (12 cards) AAC communication board on the theme: '$topic'.
                    Respond ONLY with a valid JSON array of exactly 12 items. Each item must have:
                    - "id": String (unique e.g. 'c_dentist_chair')
                    - "label": String (1-2 clear words)
                    - "spokenText": String (label text spoken aloud)
                    - "category": String, MUST be one of: PEOPLE, ACTION, NOUN, ADJECTIVE, SOCIAL, or FUNCTION.
                    - "type": "WORD"
                    - "imageUrl": String of a public schematic svg or png icon or leave null.
                    - "targetBoardId": null
                    Ensure no formatting other than pure raw JSON.
                """.trimIndent()

                val request = GeminiRequest(
                    contents = listOf(
                        GeminiContent(parts = listOf(GeminiPart(text = schemaPrompt)))
                    ),
                    generationConfig = GeminiGenerationConfig(
                        thinkingConfig = GeminiThinkingConfig(thinkingLevel = "high"),
                        temperature = 0.5
                    )
                )

                val response = GeminiClient.apiService.generateContentHighThinking(apiKey, request)
                val jsonStringRaw = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: ""
                
                // Clean markdown code blocks from model response
                val cleanJson = jsonStringRaw
                    .replace("```json", "")
                    .replace("```", "")
                    .trim()

                val buttons = PresetBoards.deserializeButtons(cleanJson)
                if (buttons.isNotEmpty()) {
                    val newBoardId = "custom_${System.currentTimeMillis()}"
                    val serialized = PresetBoards.serializeButtons(buttons)
                    val generatedBoard = Board(
                        id = newBoardId,
                        name = "$topic Board ✨",
                        isCustom = true,
                        columns = 3,
                        rows = 4,
                        buttonsJson = serialized
                    )
                    boardDao.insertBoard(generatedBoard)
                    withContext(Dispatchers.Main) {
                        _boardStack.value = _boardStack.value + generatedBoard
                        onFinished(true)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        onFinished(false)
                    }
                }
            } catch (e: Exception) {
                Log.e("AACViewModel", "Custom board compilation failed: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    onFinished(false)
                }
            } finally {
                withContext(Dispatchers.Main) {
                    _aiLoading.value = false
                }
            }
        }
    }

    fun deleteCustomBoard(boardId: String) {
        viewModelScope.launch {
            boardDao.deleteBoardById(boardId)
            // If currently viewing deleted board, pop it
            if (currentBoard.value.id == boardId) {
                navigateBack()
            }
        }
    }

    fun addCustomButtonToBoard(label: String, category: FitzgeraldCategory, imageUrl: String?) {
        viewModelScope.launch {
            val board = currentBoard.value
            val buttons = PresetBoards.deserializeButtons(board.buttonsJson).toMutableList()
            val rawBtn = AACButton(
                id = "cb_${System.currentTimeMillis()}",
                label = label,
                spokenText = label,
                category = category,
                type = ButtonType.WORD,
                imageUrl = imageUrl
            )
            val newBtn = com.example.data.model.GrammarEngine.resolveEffectiveButton(rawBtn)
            
            // Insert adjacent to existing buttons of the same category if present
            val lastMatchingIdx = buttons.indexOfLast { it.category == category }
            if (lastMatchingIdx != -1) {
                buttons.add(lastMatchingIdx + 1, newBtn)
            } else {
                buttons.add(newBtn)
            }

            val updatedBoard = board.copy(
                buttonsJson = PresetBoards.serializeButtons(buttons)
            )
            // If preset, we can make it custom to save modifications or just insert in database
            boardDao.insertBoard(updatedBoard)
            
            // Sync with stack
            val currentStack = _boardStack.value.toMutableList()
            val idx = currentStack.indexOfFirst { it.id == board.id }
            if (idx != -1) {
                currentStack[idx] = updatedBoard
                _boardStack.value = currentStack
            }
        }
    }

    // AI Icon generator for custom cards
    fun generateIconForCard(label: String, onResult: (String?) -> Unit) {
        if (label.isBlank()) {
            onResult(null)
            return
        }
        _aiIconLoading.value = true
        viewModelScope.launch {
            try {
                // 1. Try querying OpenSymbols API first for high precision AAC vector icon
                val symbols = OpenSymbolsClient.apiService.searchSymbols(label)
                if (symbols.isNotEmpty() && !symbols.first().imageUrl.isNullOrBlank()) {
                    onResult(symbols.first().imageUrl)
                    _aiIconLoading.value = false
                    return@launch
                }
            } catch (e: Exception) {
                Log.e("AACViewModel", "OpenSymbols search failed: ${e.message}")
            }

            // 2. Fallback: Search OpenSymbols with simplified single word or return smart vector placeholder
            try {
                val cleanWord = label.split(" ").firstOrNull() ?: label
                val fallbackSymbols = OpenSymbolsClient.apiService.searchSymbols(cleanWord)
                if (fallbackSymbols.isNotEmpty() && !fallbackSymbols.first().imageUrl.isNullOrBlank()) {
                    onResult(fallbackSymbols.first().imageUrl)
                } else {
                    onResult(null)
                }
            } catch (e: Exception) {
                onResult(null)
            } finally {
                _aiIconLoading.value = false
            }
        }
    }

    private fun boostSystemMediaVolume() {
        try {
            val audioManager = getApplication<Application>().getSystemService(android.content.Context.AUDIO_SERVICE) as? android.media.AudioManager
            audioManager?.let { am ->
                val maxVolume = am.getStreamMaxVolume(android.media.AudioManager.STREAM_MUSIC)
                am.setStreamVolume(android.media.AudioManager.STREAM_MUSIC, maxVolume, 0)
            }
        } catch (e: Exception) {
            Log.e("AACViewModel", "Failed to set max volume: ${e.message}")
        }
    }

    fun speakLouder(text: String) {
        if (text.isBlank()) return

        // Maximize device media volume stream
        boostSystemMediaVolume()

        // Log sentence into Room History with (Loud) suffix
        viewModelScope.launch {
            phraseHistoryDao.insertPhrase(PhraseHistory(phraseText = "$text (Loud)"))
        }

        if (_useGeminiTts.value && BuildConfig.GEMINI_API_KEY.isNotEmpty() && BuildConfig.GEMINI_API_KEY != "MY_GEMINI_API_KEY") {
            speakWithGeminiTTSLouder(text)
        } else {
            // Local TTS fallback with elevated pitch, slower rate for maximum impact & volume
            if (_ttsInitialized.value) {
                textToSpeech?.let { tts ->
                    tts.setPitch(1.35f)
                    tts.setSpeechRate(0.85f)
                    val params = android.os.Bundle()
                    params.putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, 1.0f)
                    tts.speak(text.uppercase() + "!", TextToSpeech.QUEUE_FLUSH, params, "loud_instant_id")
                    
                    // Reset pitch & rate back after speak
                    viewModelScope.launch {
                        kotlinx.coroutines.delay(1200)
                        tts.setPitch(1.0f)
                        tts.setSpeechRate(1.0f)
                    }
                }
            }
        }
    }

    private fun speakWithGeminiTTSLouder(text: String) {
        _ttsLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val apiKey = BuildConfig.GEMINI_API_KEY
                val request = GeminiRequest(
                    contents = listOf(
                        GeminiContent(
                            parts = listOf(GeminiPart(text = "Say this name or phrase with great volume, intensity, and high-energy emphasis: $text"))
                        )
                    ),
                    generationConfig = GeminiGenerationConfig(
                        responseModalities = listOf("AUDIO"),
                        speechConfig = GeminiSpeechConfig(
                            voiceConfig = GeminiVoiceConfig(
                                prebuiltVoiceConfig = GeminiPrebuiltVoiceConfig(voiceName = _selectedVoice.value.lowercase())
                            )
                        )
                    )
                )

                val response = GeminiClient.apiService.generateTTS(apiKey, request)
                val base64Audio = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.inlineData?.data

                if (base64Audio != null) {
                    val audioBytes = Base64.decode(base64Audio, Base64.DEFAULT)
                    withContext(Dispatchers.Main) {
                        playAudioBytes(audioBytes)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        speakInstant(text.uppercase() + "!") // fallback
                    }
                }
            } catch (e: Exception) {
                Log.e("AACViewModel", "Failed Louder Gemini TTS: ${e.message}")
                withContext(Dispatchers.Main) {
                    speakInstant(text.uppercase() + "!") // fallback
                }
            } finally {
                withContext(Dispatchers.Main) {
                    _ttsLoading.value = false
                }
            }
        }
    }

    fun toggleButtonVisibility(boardId: String, buttonId: String) {
        viewModelScope.launch {
            // Find board in memory or db
            val board = _boardStack.value.find { it.id == boardId } 
                ?: boardDao.getBoardById(boardId)
                ?: PresetBoards.allPresetBoards[boardId]
                ?: return@launch
                
            val buttons = PresetBoards.deserializeButtons(board.buttonsJson).map { btn ->
                if (btn.id == buttonId) {
                    btn.copy(isHidden = !btn.isHidden)
                } else {
                    btn
                }
            }
            
            val updatedBoard = board.copy(
                buttonsJson = PresetBoards.serializeButtons(buttons)
            )
            boardDao.insertBoard(updatedBoard)
            
            // Sync with stack
            val currentStack = _boardStack.value.toMutableList()
            val idx = currentStack.indexOfFirst { it.id == board.id }
            if (idx != -1) {
                currentStack[idx] = updatedBoard
                _boardStack.value = currentStack
            }
        }
    }

    fun saveRearrangedButtons(boardId: String, buttons: List<AACButton>) {
        viewModelScope.launch {
            val board = _boardStack.value.find { it.id == boardId } 
                ?: boardDao.getBoardById(boardId)
                ?: PresetBoards.allPresetBoards[boardId]
                ?: return@launch
                
            val updatedBoard = board.copy(
                buttonsJson = PresetBoards.serializeButtons(buttons)
            )
            boardDao.insertBoard(updatedBoard)
            
            // Sync with stack
            val currentStack = _boardStack.value.toMutableList()
            val idx = currentStack.indexOfFirst { it.id == board.id }
            if (idx != -1) {
                currentStack[idx] = updatedBoard
                _boardStack.value = currentStack
            }
        }
    }

    fun saveCurrentPhraseToSavedPhrases() {
        val combinedText = _sentence.value.joinToString(" ") { it.spokenText }
        val labelText = _sentence.value.joinToString(" ") { it.label }
        if (combinedText.isBlank()) return

        viewModelScope.launch {
            val existingBoard = boardDao.getBoardById("saved_phrases") ?: Board(
                id = "saved_phrases",
                name = "Saved Phrases 📁",
                isCustom = true,
                columns = 3,
                rows = 4,
                buttonsJson = PresetBoards.serializeButtons(emptyList())
            )
            
            val buttons = PresetBoards.deserializeButtons(existingBoard.buttonsJson).toMutableList()
            val newBtn = AACButton(
                id = "sp_${System.currentTimeMillis()}",
                label = labelText,
                spokenText = combinedText,
                category = FitzgeraldCategory.SOCIAL,
                type = ButtonType.WORD,
                partOfSpeech = PartOfSpeech.SOCIAL,
                semanticTags = setOf(SemanticTag.GENERAL)
            )
            buttons.add(newBtn)
            
            val updatedBoard = existingBoard.copy(
                buttonsJson = PresetBoards.serializeButtons(buttons)
            )
            boardDao.insertBoard(updatedBoard)
            
            // Sync with stack
            val currentStack = _boardStack.value.toMutableList()
            val idx = currentStack.indexOfFirst { it.id == "saved_phrases" }
            if (idx != -1) {
                currentStack[idx] = updatedBoard
                _boardStack.value = currentStack
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        textToSpeech?.shutdown()
        mediaPlayer?.release()
    }
}

enum class CardDisplayMode { BOTH, SYMBOLS_ONLY, WORDS_ONLY }
enum class TextPosition { BELOW_SYMBOL, ABOVE_SYMBOL }
enum class SkinTone(val label: String, val hexColor: String) {
    DEFAULT("Original", "#FFC107"),
    LIGHT("Light", "#FFDFC4"),
    MEDIUM_LIGHT("Medium-Light", "#F0C7A1"),
    MEDIUM("Medium", "#D09B6A"),
    MEDIUM_DARK("Medium-Dark", "#8D5524"),
    DARK("Dark", "#4C2F19")
}

