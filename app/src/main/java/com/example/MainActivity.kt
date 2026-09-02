package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AACViewModel
import com.example.ui.viewmodel.CardDisplayMode
import com.example.ui.viewmodel.TextPosition
import com.example.ui.viewmodel.SkinTone
import com.example.data.model.AACButton
import com.example.data.model.Board
import com.example.data.model.ButtonType
import com.example.data.model.FitzgeraldCategory
import com.example.data.model.PresetBoards
import com.example.ui.components.FacialVisionOverlay

class MainActivity : ComponentActivity() {
    private val viewModel: AACViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    AACMainScreen(
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun AACMainScreen(
    viewModel: AACViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentBoard by viewModel.currentBoard.collectAsStateWithLifecycle()
    val canGoBack by viewModel.previousBoardAvailable.collectAsStateWithLifecycle()
    val sentence by viewModel.sentence.collectAsStateWithLifecycle()
    val suggestedWords by viewModel.suggestedNextWords.collectAsStateWithLifecycle()
    val useGeminiTts by viewModel.useGeminiTts.collectAsStateWithLifecycle()
    val selectedVoice by viewModel.selectedVoice.collectAsStateWithLifecycle()
    val isTtsLoading by viewModel.ttsLoading.collectAsStateWithLifecycle()

    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()
    val isSearchLoading by viewModel.searchLoading.collectAsStateWithLifecycle()

    val aiElaboratedText by viewModel.aiElaboratedText.collectAsStateWithLifecycle()
    val isAiLoading by viewModel.aiLoading.collectAsStateWithLifecycle()
    val isAiIconLoading by viewModel.aiIconLoading.collectAsStateWithLifecycle()
    val aiResponseText by viewModel.aiDialogResponse.collectAsStateWithLifecycle()

    val userCustomBoards by viewModel.userCustomBoards.collectAsStateWithLifecycle()
    val isSimpleDensity by viewModel.boardDensitySimple.collectAsStateWithLifecycle()

    val cardDisplayMode by viewModel.cardDisplayMode.collectAsStateWithLifecycle()
    val textPosition by viewModel.textPosition.collectAsStateWithLifecycle()
    val isHighContrast by viewModel.isHighContrast.collectAsStateWithLifecycle()
    val selectedSkinTone by viewModel.selectedSkinTone.collectAsStateWithLifecycle()
    val speakOnTileTap by viewModel.speakOnTileTap.collectAsStateWithLifecycle()
    val isGrammarFilterEnabled by viewModel.isGrammarFilterEnabled.collectAsStateWithLifecycle()
    val isFacialTrackingEnabled by viewModel.isFacialTrackingEnabled.collectAsStateWithLifecycle()

    // Dialog state controllers
    var isEditMode by remember { mutableStateOf(false) }
    var selectedButtonForSwap by remember { mutableStateOf<AACButton?>(null) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showBoardCustomizerDialog by remember { mutableStateOf(false) }
    var customTopicText by remember { mutableStateOf("") }
    var customButtonName by remember { mutableStateOf("") }
    var customButtonCategory by remember { mutableStateOf(FitzgeraldCategory.NOUN) }
    var customButtonImageUrl by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(if (isHighContrast) Color(0xFF121212) else Color(0xFFF7F9FC))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Smart AAC",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 22.sp,
                    color = Color(0xFF1E293B)
                )
                if (useGeminiTts) {
                    Text(
                        text = "Gemini HD Voice ($selectedVoice)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF6366F1)
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(
                        onClick = { viewModel.setBoardDensity(!isSimpleDensity) },
                        modifier = Modifier
                            .background(Color.White, CircleShape)
                            .border(1.dp, Color(0xFFE2E8F0), CircleShape)
                            .size(40.dp)
                    ) {
                        Icon(
                            imageVector = if (isSimpleDensity) Icons.Default.GridOn else Icons.Default.GridView,
                            contentDescription = "Toggle Grid Layout Density",
                            tint = Color(0xFF475569)
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text("Grid Size", fontSize = 9.sp, fontWeight = FontWeight.Medium, color = Color(0xFF475569))
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(
                        onClick = { showBoardCustomizerDialog = true },
                        modifier = Modifier
                            .background(Color.White, CircleShape)
                            .border(1.dp, Color(0xFFE2E8F0), CircleShape)
                            .size(40.dp)
                            .testTag("customizer_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddCircleOutline,
                            contentDescription = "Search Symbols & Customize",
                            tint = Color(0xFF475569)
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text("Add Card", fontSize = 9.sp, fontWeight = FontWeight.Medium, color = Color(0xFF475569))
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(
                        onClick = { showSettingsDialog = true },
                        modifier = Modifier
                            .background(Color.White, CircleShape)
                            .border(1.dp, Color(0xFFE2E8F0), CircleShape)
                            .size(40.dp)
                            .testTag("settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Speech Voice Settings",
                            tint = Color(0xFF475569)
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text("Settings", fontSize = 9.sp, fontWeight = FontWeight.Medium, color = Color(0xFF475569))
                }
            }
        }

        FacialVisionOverlay(viewModel = viewModel)

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .testTag("sentence_bar"),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (sentence.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(64.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = "Tap cards below to compose a message...",
                            color = Color(0xFF94A3B8),
                            fontSize = 15.sp,
                            modifier = Modifier.padding(start = 12.dp)
                        )
                    }
                } else {
                    LazyRow(
                        modifier = Modifier
                            .weight(1f)
                            .height(64.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        items(sentence) { wordBtn ->
                            SentenceButtonComponent(button = wordBtn, onRemove = {})
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(
                            onClick = { viewModel.removeLastFromSentence() },
                            modifier = Modifier
                                .background(Color(0xFFFEF2F2), CircleShape)
                                .size(38.dp)
                                .testTag("backspace_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Backspace,
                                contentDescription = "Backspace last word",
                                tint = Color(0xFFEF4444)
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("Delete", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFFEF4444))
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(
                            onClick = { viewModel.clearSentence() },
                            modifier = Modifier
                                .background(Color(0xFFF1F5F9), CircleShape)
                                .size(38.dp)
                                .testTag("clear_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteSweep,
                                contentDescription = "Clear entire sentence",
                                tint = Color(0xFF64748B)
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("Clear", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(
                            onClick = {
                                viewModel.saveCurrentPhraseToSavedPhrases()
                                Toast.makeText(context, "Phrase added to Saved Phrases!", Toast.LENGTH_SHORT).show()
                            },
                            enabled = sentence.isNotEmpty(),
                            modifier = Modifier
                                .background(
                                    if (sentence.isNotEmpty()) Color(0xFF10B981) else Color(0xFFF1F5F9),
                                    CircleShape
                                )
                                .size(38.dp)
                                .testTag("save_phrase_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Save,
                                contentDescription = "Save phrase",
                                tint = if (sentence.isNotEmpty()) Color.White else Color(0xFF94A3B8)
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("Save", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = if (sentence.isNotEmpty()) Color(0xFF10B981) else Color(0xFF94A3B8))
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(
                            onClick = {
                                val combinedText = sentence.joinToString(" ") { it.spokenText }
                                viewModel.speakSentence(combinedText)
                            },
                            enabled = sentence.isNotEmpty() && !isTtsLoading,
                            modifier = Modifier
                                .background(
                                    if (sentence.isNotEmpty()) Color(0xFF4F46E5) else Color(0xFFE2E8F0),
                                    CircleShape
                                )
                                .size(40.dp)
                                .testTag("speak_sentence_button")
                        ) {
                            if (isTtsLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.VolumeUp,
                                    contentDescription = "Vocalize Message",
                                    tint = if (sentence.isNotEmpty()) Color.White else Color(0xFF94A3B8)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("Speak", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = if (sentence.isNotEmpty()) Color(0xFF4F46E5) else Color(0xFF94A3B8))
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(
                            onClick = {
                                val combinedText = sentence.joinToString(" ") { it.spokenText }
                                viewModel.speakLouder(combinedText)
                            },
                            enabled = sentence.isNotEmpty() && !isTtsLoading,
                            modifier = Modifier
                                .background(
                                    if (sentence.isNotEmpty()) Color(0xFFEA580C) else Color(0xFFE2E8F0),
                                    CircleShape
                                )
                                .size(40.dp)
                                .testTag("speak_louder_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Campaign,
                                contentDescription = "Vocalize Louder",
                                tint = if (sentence.isNotEmpty()) Color.White else Color(0xFF94A3B8)
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("Louder", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = if (sentence.isNotEmpty()) Color(0xFFEA580C) else Color(0xFF94A3B8))
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = suggestedWords.isNotEmpty(),
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        modifier = Modifier.size(13.dp),
                        tint = Color(0xFF818CF8)
                    )
                    Text(
                        text = "Suggested Next:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6366F1)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(suggestedWords) { sug ->
                        SuggestionCard(word = sug, onClick = {
                            val targetBtn = AACButton(
                                id = "sug_${System.currentTimeMillis()}",
                                label = sug,
                                spokenText = sug,
                                category = FitzgeraldCategory.NOUN
                            )
                            viewModel.onButtonTapped(targetBtn)
                        })
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (canGoBack) {
                    ElevatedButton(
                        onClick = { viewModel.navigateBack() },
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = Color.White,
                            contentColor = Color(0xFF334155)
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go back folder",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Back", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }

                Text(
                    text = currentBoard.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF334155)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(
                        onClick = { 
                            isEditMode = !isEditMode 
                            selectedButtonForSwap = null
                        },
                        modifier = Modifier
                            .background(if (isEditMode) Color(0xFF4F46E5) else Color.White, RoundedCornerShape(10.dp))
                            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(10.dp))
                            .size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Toggle Edit Board Mode",
                            tint = if (isEditMode) Color.White else Color(0xFF334155),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text("Edit", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = if (isEditMode) Color(0xFF4F46E5) else Color(0xFF334155))
                }

                if (canGoBack) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(
                            onClick = { viewModel.navigateToHome() },
                            modifier = Modifier
                                .background(Color.White, RoundedCornerShape(10.dp))
                                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(10.dp))
                                .size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = "Navigate to Home Core Board",
                                tint = Color(0xFF334155),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("Home", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF334155))
                    }
                }
            }
        }

        if (isEditMode) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEEF2FF)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = Color(0xFF4F46E5),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Edit Mode: Tap top-right eye to hide/show, use arrows to nudge, tap two tiles to swap, or select skin tone below!",
                            fontSize = 11.sp,
                            color = Color(0xFF312E81),
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Skin Tone for People:",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4338CA)
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            for (tone in SkinTone.values()) {
                                val isToneSelected = selectedSkinTone == tone
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(Color(android.graphics.Color.parseColor(tone.hexColor)))
                                        .border(
                                            width = if (isToneSelected) 2.dp else 1.dp,
                                            color = if (isToneSelected) Color(0xFF4F46E5) else Color.Gray.copy(alpha = 0.4f),
                                            shape = CircleShape
                                        )
                                        .clickable { viewModel.setSkinTone(tone) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isToneSelected) {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .clip(CircleShape)
                                                .background(Color.White)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- 5. Fluid Grid Layout ---
        val boardButtons = PresetBoards.deserializeButtons(currentBoard.buttonsJson)
        val visibleButtons = if (isEditMode) boardButtons else boardButtons.filter { !it.isHidden }
        val finalColumns = if (isSimpleDensity) 3 else 4

        if (visibleButtons.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.FolderOpen,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = Color(0xFFCBD5E1)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (isEditMode) "This folder page is empty." else "All tiles on this page are currently hidden.",
                        color = Color(0xFF64748B),
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = if (isEditMode) "Use customizer '+' to add tiles!" else "Toggle Edit Mode above to re-show hidden tiles.",
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(finalColumns),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(visibleButtons) { btn ->
                    val isGrammarValid = viewModel.isButtonGrammaticallyValid(btn)
                    AACButtonCard(
                        button = btn,
                        isSimpleDensity = isSimpleDensity,
                        cardDisplayMode = cardDisplayMode,
                        textPosition = textPosition,
                        isHighContrast = isHighContrast,
                        selectedSkinTone = selectedSkinTone,
                        isEditMode = isEditMode,
                        isSelectedForSwap = selectedButtonForSwap?.id == btn.id,
                        isGrammaticallyValid = isGrammarValid,
                        onTap = {
                            if (isEditMode) {
                                val currentSwap = selectedButtonForSwap
                                if (currentSwap == null) {
                                    // Start swap operation
                                    selectedButtonForSwap = btn
                                    Toast.makeText(context, "Selected '${btn.label}'. Tap another tile to swap positions.", Toast.LENGTH_SHORT).show()
                                } else if (currentSwap.id == btn.id) {
                                    // Cancel swap
                                    selectedButtonForSwap = null
                                } else {
                                    // Complete swap!
                                    val list = boardButtons.toMutableList()
                                    val idx1 = list.indexOfFirst { it.id == currentSwap.id }
                                    val idx2 = list.indexOfFirst { it.id == btn.id }
                                    if (idx1 != -1 && idx2 != -1) {
                                        list[idx1] = btn
                                        list[idx2] = currentSwap
                                        viewModel.saveRearrangedButtons(currentBoard.id, list)
                                        Toast.makeText(context, "Swapped successfully!", Toast.LENGTH_SHORT).show()
                                    }
                                    selectedButtonForSwap = null
                                }
                            } else {
                                viewModel.onButtonTapped(btn)
                            }
                        },
                        onToggleVisibility = {
                            viewModel.toggleButtonVisibility(currentBoard.id, btn.id)
                        },
                        onMoveLeft = {
                            val idx = boardButtons.indexOfFirst { it.id == btn.id }
                            if (idx > 0) {
                                val list = boardButtons.toMutableList()
                                val target = list.removeAt(idx)
                                list.add(idx - 1, target)
                                viewModel.saveRearrangedButtons(currentBoard.id, list)
                            }
                        },
                        onMoveRight = {
                            val idx = boardButtons.indexOfFirst { it.id == btn.id }
                            if (idx >= 0 && idx < boardButtons.size - 1) {
                                val list = boardButtons.toMutableList()
                                val target = list.removeAt(idx)
                                list.add(idx + 1, target)
                                viewModel.saveRearrangedButtons(currentBoard.id, list)
                            }
                        }
                    )
                }
            }
        }
    }

    if (showSettingsDialog) {
        Dialog(onDismissRequest = { showSettingsDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(imageVector = Icons.Default.Settings, contentDescription = null, tint = Color(0xFF4F46E5))
                            Text(
                                text = "Settings & Preferences",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = Color(0xFF1E293B)
                            )
                        }
                        IconButton(onClick = { showSettingsDialog = false }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close settings")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Tile Content Mode",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color(0xFF334155),
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF1F5F9))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Button(
                            onClick = { viewModel.setCardDisplayMode(CardDisplayMode.BOTH) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (cardDisplayMode == CardDisplayMode.BOTH) Color.White else Color.Transparent,
                                contentColor = if (cardDisplayMode == CardDisplayMode.BOTH) Color(0xFF1E293B) else Color(0xFF64748B)
                            ),
                            elevation = if (cardDisplayMode == CardDisplayMode.BOTH) ButtonDefaults.buttonElevation(2.dp) else null,
                            contentPadding = PaddingValues(vertical = 6.dp)
                        ) {
                            Text("Both", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { viewModel.setCardDisplayMode(CardDisplayMode.SYMBOLS_ONLY) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (cardDisplayMode == CardDisplayMode.SYMBOLS_ONLY) Color.White else Color.Transparent,
                                contentColor = if (cardDisplayMode == CardDisplayMode.SYMBOLS_ONLY) Color(0xFF1E293B) else Color(0xFF64748B)
                            ),
                            elevation = if (cardDisplayMode == CardDisplayMode.SYMBOLS_ONLY) ButtonDefaults.buttonElevation(2.dp) else null,
                            contentPadding = PaddingValues(vertical = 6.dp)
                        ) {
                            Text("Symbols Only", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { viewModel.setCardDisplayMode(CardDisplayMode.WORDS_ONLY) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (cardDisplayMode == CardDisplayMode.WORDS_ONLY) Color.White else Color.Transparent,
                                contentColor = if (cardDisplayMode == CardDisplayMode.WORDS_ONLY) Color(0xFF1E293B) else Color(0xFF64748B)
                            ),
                            elevation = if (cardDisplayMode == CardDisplayMode.WORDS_ONLY) ButtonDefaults.buttonElevation(2.dp) else null,
                            contentPadding = PaddingValues(vertical = 6.dp)
                        ) {
                            Text("Words Only", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (cardDisplayMode == CardDisplayMode.BOTH) {
                        Text(
                            text = "Text Label Placement",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color(0xFF334155),
                            modifier = Modifier.align(Alignment.Start)
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFF1F5F9))
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Button(
                                onClick = { viewModel.setTextPosition(TextPosition.BELOW_SYMBOL) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (textPosition == TextPosition.BELOW_SYMBOL) Color.White else Color.Transparent,
                                    contentColor = if (textPosition == TextPosition.BELOW_SYMBOL) Color(0xFF1E293B) else Color(0xFF64748B)
                                ),
                                elevation = if (textPosition == TextPosition.BELOW_SYMBOL) ButtonDefaults.buttonElevation(2.dp) else null,
                                contentPadding = PaddingValues(vertical = 6.dp)
                            ) {
                                Text("Below Symbol", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = { viewModel.setTextPosition(TextPosition.ABOVE_SYMBOL) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (textPosition == TextPosition.ABOVE_SYMBOL) Color.White else Color.Transparent,
                                    contentColor = if (textPosition == TextPosition.ABOVE_SYMBOL) Color(0xFF1E293B) else Color(0xFF64748B)
                                ),
                                elevation = if (textPosition == TextPosition.ABOVE_SYMBOL) ButtonDefaults.buttonElevation(2.dp) else null,
                                contentPadding = PaddingValues(vertical = 6.dp)
                            ) {
                                Text("Above Symbol", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isHighContrast) Color(0xFF000000) else Color(0xFFF8FAFC))
                            .border(
                                2.dp,
                                if (isHighContrast) Color(0xFFFFFF00) else Color(0xFFE2E8F0),
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { viewModel.setSpeakOnTileTap(!speakOnTileTap) }
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Speak Word on Card or Folder Tap",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = if (isHighContrast) Color.White else Color(0xFF1E293B)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Play speech audio immediately when tapping a folder or vocabulary tile.",
                                    fontSize = 11.sp,
                                    color = if (isHighContrast) Color(0xFFE2E8F0) else Color(0xFF64748B)
                                )
                            }
                            Switch(
                                checked = speakOnTileTap,
                                onCheckedChange = { viewModel.setSpeakOnTileTap(it) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isHighContrast) Color(0xFF000000) else Color(0xFFF8FAFC))
                            .border(
                                1.dp,
                                if (isHighContrast) Color(0xFFFFFF00) else Color(0xFFE2E8F0),
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { viewModel.setGrammarFilterEnabled(!isGrammarFilterEnabled) }
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Smart Grammar Rule Filter",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = if (isHighContrast) Color.White else Color(0xFF1E293B)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Prevents clicking grammatically invalid next words after selecting a card.",
                                    fontSize = 11.sp,
                                    color = if (isHighContrast) Color(0xFFE2E8F0) else Color(0xFF64748B)
                                )
                            }
                            Switch(
                                checked = isGrammarFilterEnabled,
                                onCheckedChange = { viewModel.setGrammarFilterEnabled(it) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // SECTION 3: High Contrast Theme Option
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isHighContrast) Color(0xFF000000) else Color(0xFFF8FAFC))
                            .border(
                                2.dp,
                                if (isHighContrast) Color(0xFFFFFF00) else Color(0xFFE2E8F0),
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { viewModel.setHighContrast(!isHighContrast) }
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "High Contrast Mode",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = if (isHighContrast) Color.White else Color(0xFF1E293B)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Black backgrounds with thick, high-saturation category borders for maximal visual clarity.",
                                    fontSize = 11.sp,
                                    color = if (isHighContrast) Color(0xFFE2E8F0) else Color(0xFF64748B)
                                )
                            }
                            Switch(
                                checked = isHighContrast,
                                onCheckedChange = { viewModel.setHighContrast(it) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Skin Tone for People Symbols (5 Tones)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color(0xFF334155),
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Customize skin tone for person tiles across all boards:",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B),
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        for (tone in SkinTone.values()) {
                            val isToneSelected = selectedSkinTone == tone
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clickable { viewModel.setSkinTone(tone) }
                                    .padding(2.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(Color(android.graphics.Color.parseColor(tone.hexColor)))
                                        .border(
                                            width = if (isToneSelected) 3.dp else 1.dp,
                                            color = if (isToneSelected) Color(0xFF4F46E5) else Color.Gray.copy(alpha = 0.4f),
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isToneSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = if (tone == SkinTone.LIGHT || tone == SkinTone.DEFAULT || tone == SkinTone.MEDIUM_LIGHT) Color.Black else Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = tone.label,
                                    fontSize = 9.sp,
                                    fontWeight = if (isToneSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isToneSelected) Color(0xFF4F46E5) else Color(0xFF64748B)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Speech Synthesis Engine",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color(0xFF334155),
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF1F5F9))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Button(
                            onClick = { viewModel.setSpeechMode(false) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (!useGeminiTts) Color.White else Color.Transparent,
                                contentColor = if (!useGeminiTts) Color(0xFF1E293B) else Color(0xFF64748B)
                            ),
                            elevation = if (!useGeminiTts) ButtonDefaults.buttonElevation(2.dp) else null,
                            contentPadding = PaddingValues(vertical = 6.dp)
                        ) {
                            Text("Offline Voice", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = { viewModel.setSpeechMode(true) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (useGeminiTts) Color.White else Color.Transparent,
                                contentColor = if (useGeminiTts) Color(0xFF1E293B) else Color(0xFF64748B)
                            ),
                            elevation = if (useGeminiTts) ButtonDefaults.buttonElevation(2.dp) else null,
                            contentPadding = PaddingValues(vertical = 6.dp)
                        ) {
                            Text("Gemini HD Voice", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (useGeminiTts) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Choose Natural HD Voice:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color(0xFF475569),
                            modifier = Modifier.align(Alignment.Start)
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        val voices = listOf("Kore", "Puck", "Fenrir", "Aoede", "Charon")
                        voices.forEach { voice ->
                            val isSelected = selectedVoice == voice
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) Color(0xFFEEF2FF) else Color(0xFFF8FAFC))
                                    .border(
                                        1.dp,
                                        if (isSelected) Color(0xFF6366F1) else Color(0xFFE2E8F0),
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable { viewModel.setVoice(voice) }
                                    .padding(horizontal = 14.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = voice,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = if (isSelected) Color(0xFF4F46E5) else Color(0xFF334155)
                                )
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Selected",
                                        tint = Color(0xFF4F46E5),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Facial Vision & Expression Switch",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color(0xFF334155),
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF8FAFC))
                            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Enable Facial Action Tracking",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                                color = Color(0xFF1E293B)
                            )
                            Text(
                                text = "Use brow raise, smile, mouth open & blink as hands-free AAC triggers",
                                fontSize = 10.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                        Switch(
                            checked = isFacialTrackingEnabled,
                            onCheckedChange = { viewModel.toggleFacialTracking(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF4F46E5)
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { showSettingsDialog = false },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5))
                    ) {
                        Text("Save & Close Preferences", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    if (showBoardCustomizerDialog) {
        Dialog(onDismissRequest = { showBoardCustomizerDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Search & Customizer",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color(0xFF1E293B)
                        )
                        IconButton(onClick = { showBoardCustomizerDialog = false }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close dialog")
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFFF8FAFC))
                            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(14.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "Search OpenSymbols Repository",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color(0xFF334155)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Search thousands of open AAC symbols to insert or speak.",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { viewModel.seekSymbol(it) },
                                placeholder = { Text("Search apple, house, run...", fontSize = 13.sp) },
                                singleLine = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .background(Color.White, RoundedCornerShape(8.dp)),
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF4F46E5),
                                    unfocusedBorderColor = Color(0xFFE2E8F0)
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        if (isSearchLoading) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = Color(0xFF4F46E5))
                            }
                        } else if (searchResults.isNotEmpty()) {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp)
                            ) {
                                items(searchResults) { item ->
                                    val name = item.name ?: "icon"
                                    Column(
                                        modifier = Modifier
                                            .width(72.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color.White)
                                            .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(8.dp))
                                            .clickable {
                                                val btn = AACButton(
                                                    id = "search_${System.currentTimeMillis()}",
                                                    label = name,
                                                    spokenText = name,
                                                    category = FitzgeraldCategory.NOUN,
                                                    imageUrl = item.imageUrl
                                                )
                                                viewModel.onButtonTapped(btn)
                                                showBoardCustomizerDialog = false
                                                Toast.makeText(context, "Added $name to sentence bar!", Toast.LENGTH_SHORT).show()
                                            }
                                            .padding(6.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        AsyncImage(
                                            model = item.imageUrl,
                                            contentDescription = name,
                                            modifier = Modifier
                                                .size(44.dp)
                                                .clip(RoundedCornerShape(4.dp)),
                                            contentScale = ContentScale.Fit
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = name,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            textAlign = TextAlign.Center,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            color = Color(0xFF334155)
                                        )
                                    }
                                }
                            }
                        } else if (searchQuery.isNotEmpty()) {
                            Text(
                                text = "No search results found.",
                                fontSize = 12.sp,
                                color = Color(0xFF94A3B8),
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFFF1F5F9))
                            .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(14.dp))
                            .padding(14.dp)
                    ) {
                        Text(
                            text = "Add tile to current: ${currentBoard.name}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color(0xFF1E293B)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = customButtonName,
                            onValueChange = { customButtonName = it },
                            label = { Text("Word/Phrase Label", fontSize = 12.sp) },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White, RoundedCornerShape(8.dp)),
                            shape = RoundedCornerShape(8.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = customButtonImageUrl,
                            onValueChange = { customButtonImageUrl = it },
                            label = { Text("Image URL (optional)", fontSize = 12.sp) },
                            placeholder = { Text("https://...", fontSize = 11.sp) },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White, RoundedCornerShape(8.dp)),
                            shape = RoundedCornerShape(8.dp)
                        )
                        
                        Button(
                            onClick = {
                                if (customButtonName.isNotBlank()) {
                                    viewModel.generateIconForCard(customButtonName) { url ->
                                        if (!url.isNullOrBlank()) {
                                            customButtonImageUrl = url
                                            Toast.makeText(context, "AI Icon generated! ✨", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(context, "No icon found for '$customButtonName'. Try another word.", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            },
                            enabled = customButtonName.isNotBlank() && !isAiIconLoading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            if (isAiIconLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Generating AI Icon...", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            } else {
                                Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Generate AI Icon for Card ✨", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Choose Fitzgerald Color Category:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color(0xFF475569)
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            val categories = listOf(
                                FitzgeraldCategory.PEOPLE,
                                FitzgeraldCategory.ACTION,
                                FitzgeraldCategory.NOUN,
                                FitzgeraldCategory.ADJECTIVE,
                                FitzgeraldCategory.SOCIAL,
                                FitzgeraldCategory.FUNCTION
                            )
                            // Display them as circular or small color bubbles select
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                items(categories) { cat ->
                                    val isSelected = cat == customButtonCategory
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(android.graphics.Color.parseColor(cat.hexColor)))
                                            .border(
                                                2.dp,
                                                if (isSelected) Color.Black else Color.Transparent,
                                                RoundedCornerShape(8.dp)
                                            )
                                            .clickable { customButtonCategory = cat }
                                            .padding(horizontal = 8.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = cat.name,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(android.graphics.Color.parseColor(cat.textColor))
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                if (customButtonName.isNotBlank()) {
                                    val url = if (customButtonImageUrl.isBlank()) null else customButtonImageUrl
                                    viewModel.addCustomButtonToBoard(
                                        label = customButtonName,
                                        category = customButtonCategory,
                                        imageUrl = url
                                    )
                                    customButtonName = ""
                                    customButtonImageUrl = ""
                                    showBoardCustomizerDialog = false
                                    Toast.makeText(context, "Added new card successfully!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            enabled = customButtonName.isNotBlank(),
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Insert Card into Board", fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (userCustomBoards.isNotEmpty()) {
                        Text(
                            text = "Created Custom Boards",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color(0xFF334155),
                            modifier = Modifier.align(Alignment.Start)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        userCustomBoards.forEach { board ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFFF8FAFC))
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(board.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Row {
                                    IconButton(
                                        onClick = {
                                            viewModel.onButtonTapped(
                                                AACButton(
                                                    id = "nav_custom_${board.id}",
                                                    label = board.name,
                                                    category = FitzgeraldCategory.NAVIGATION,
                                                    type = ButtonType.FOLDER,
                                                    targetBoardId = board.id
                                                )
                                            )
                                            showBoardCustomizerDialog = false
                                        }
                                    ) {
                                        Icon(imageVector = Icons.Default.Folder, contentDescription = "Open Board", tint = Color(0xFF1E293B))
                                    }

                                    IconButton(
                                        onClick = { viewModel.deleteCustomBoard(board.id) }
                                    ) {
                                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Board", tint = Color(0xFFEF4444))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SentenceButtonComponent(
    button: AACButton,
    onRemove: () -> Unit
) {
    val bgColor = Color(android.graphics.Color.parseColor(button.category.hexColor))
    val textColor = Color(android.graphics.Color.parseColor(button.category.textColor))

    Card(
        modifier = Modifier
            .fillMaxHeight()
            .width(64.dp)
            .border(1.dp, textColor.copy(alpha = 0.25f), RoundedCornerShape(10.dp)),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (button.imageUrl != null) {
                AsyncImage(
                    model = button.imageUrl,
                    contentDescription = button.label,
                    modifier = Modifier.size(24.dp),
                    contentScale = ContentScale.Fit
                )
            } else {
                Icon(
                    imageVector = when (button.category) {
                        FitzgeraldCategory.PEOPLE -> Icons.Default.Face
                        FitzgeraldCategory.ACTION -> Icons.Default.PlayArrow
                        FitzgeraldCategory.NOUN -> Icons.Default.Category
                        FitzgeraldCategory.ADJECTIVE -> Icons.Default.Palette
                        FitzgeraldCategory.SOCIAL -> Icons.Default.EmojiEmotions
                        FitzgeraldCategory.FUNCTION -> Icons.Default.Link
                        FitzgeraldCategory.NAVIGATION -> Icons.Default.Folder
                    },
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = textColor
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = button.label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = textColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun SuggestionCard(
    word: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .clickable { onClick() }
            .border(1.dp, Color(0xFFC7D2FE), RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = word,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = Color(0xFF4F46E5)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = Color(0xFF4F46E5),
                modifier = Modifier.size(12.dp)
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AACButtonCard(
    button: AACButton,
    isSimpleDensity: Boolean,
    cardDisplayMode: CardDisplayMode = CardDisplayMode.BOTH,
    textPosition: TextPosition = TextPosition.BELOW_SYMBOL,
    isHighContrast: Boolean = false,
    selectedSkinTone: SkinTone = SkinTone.DEFAULT,
    isEditMode: Boolean = false,
    isSelectedForSwap: Boolean = false,
    isGrammaticallyValid: Boolean = true,
    onTap: () -> Unit,
    onToggleVisibility: () -> Unit,
    onMoveLeft: () -> Unit,
    onMoveRight: () -> Unit
) {
    val categoryHighContrastBorder = when (button.category) {
        FitzgeraldCategory.PEOPLE -> Color(0xFFFFFF00)    // Bright Yellow
        FitzgeraldCategory.ACTION -> Color(0xFFFF00FF)    // Bright Magenta
        FitzgeraldCategory.NOUN -> Color(0xFF00FF00)      // Bright Lime Green
        FitzgeraldCategory.ADJECTIVE -> Color(0xFF00FFFF) // Bright Cyan
        FitzgeraldCategory.SOCIAL -> Color(0xFFFF6600)    // Bright Orange
        FitzgeraldCategory.NAVIGATION -> Color(0xFFA855F7)// Bright Electric Purple
        FitzgeraldCategory.FUNCTION -> Color(0xFFFFFFFF)  // Bright White
    }

    val defaultBgColor = Color(android.graphics.Color.parseColor(button.category.hexColor))
    val defaultTextColor = Color(android.graphics.Color.parseColor(button.category.textColor))

    val bgColor = if (isHighContrast) Color(0xFF000000) else defaultBgColor
    val textColor = if (isHighContrast) Color(0xFFFFFFFF) else defaultTextColor
    val borderColor = if (isSelectedForSwap) Color(0xFF4F46E5)
                      else if (isHighContrast) categoryHighContrastBorder
                      else defaultTextColor.copy(alpha = 0.2f)
    val borderWidth = if (isSelectedForSwap) 3.5.dp else if (isHighContrast) 3.dp else 2.dp

    val cardHeight = if (isSimpleDensity) (if (isEditMode) 130.dp else 100.dp) else (if (isEditMode) 115.dp else 84.dp)
    val cardAlpha = if (button.isHidden) 0.45f else if (!isGrammaticallyValid) 0.28f else 1.0f

    val isPeopleButton = button.category == FitzgeraldCategory.PEOPLE ||
            button.spokenText.contains("I", ignoreCase = true) ||
            button.spokenText.contains("me", ignoreCase = true) ||
            button.spokenText.contains("you", ignoreCase = true)

    val skinToneColor = try {
        Color(android.graphics.Color.parseColor(selectedSkinTone.hexColor))
    } catch (e: Exception) {
        Color(0xFFFFC107)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(cardHeight)
            .testTag("aac_button_${button.id}")
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = borderWidth,
                color = borderColor,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(enabled = isGrammaticallyValid) { onTap() },
        colors = CardDefaults.cardColors(containerColor = bgColor.copy(alpha = cardAlpha)),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelectedForSwap) 6.dp else 2.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                val symbolComposable: @Composable () -> Unit = {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        val iconTint = if (isPeopleButton && selectedSkinTone != SkinTone.DEFAULT) skinToneColor else textColor
                        if (button.imageUrl != null) {
                            AsyncImage(
                                model = button.imageUrl,
                                contentDescription = button.label,
                                modifier = Modifier
                                    .size(if (isSimpleDensity) 34.dp else 26.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Fit
                            )
                        } else {
                            Icon(
                                imageVector = getUniqueIconForButton(button),
                                contentDescription = null,
                                modifier = Modifier.size(if (isSimpleDensity) 26.dp else 20.dp),
                                tint = iconTint
                            )
                        }
                    }
                }

                val textComposable: @Composable () -> Unit = {
                    Text(
                        text = button.label,
                        fontWeight = FontWeight.Black,
                        fontSize = if (cardDisplayMode == CardDisplayMode.WORDS_ONLY) {
                            if (isSimpleDensity) 15.sp else 12.sp
                        } else {
                            if (isSimpleDensity) 12.sp else 10.sp
                        },
                        color = textColor,
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Render content based on CardDisplayMode & TextPosition
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    when (cardDisplayMode) {
                        CardDisplayMode.WORDS_ONLY -> {
                            textComposable()
                        }
                        CardDisplayMode.SYMBOLS_ONLY -> {
                            symbolComposable()
                        }
                        CardDisplayMode.BOTH -> {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                if (textPosition == TextPosition.ABOVE_SYMBOL) {
                                    textComposable()
                                    Spacer(modifier = Modifier.height(2.dp))
                                    symbolComposable()
                                } else {
                                    symbolComposable()
                                    Spacer(modifier = Modifier.height(2.dp))
                                    textComposable()
                                }
                            }
                        }
                    }
                }

                // Reorder nudge buttons at the bottom if isEditMode is active
                if (isEditMode) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 2.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { onMoveLeft() },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Move Left",
                                tint = textColor,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Text(
                            text = if (button.isHidden) "Hidden" else "Move",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = textColor.copy(alpha = 0.8f)
                        )

                        IconButton(
                            onClick = { onMoveRight() },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = "Move Right",
                                tint = textColor,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // Top-right Eye Toggle overlay for Visibility control in Edit Mode
            if (isEditMode) {
                IconButton(
                    onClick = { onToggleVisibility() },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(2.dp)
                        .background(Color.White.copy(alpha = 0.8f), CircleShape)
                        .size(24.dp)
                ) {
                    Icon(
                        imageVector = if (button.isHidden) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = "Toggle hidden state",
                        tint = if (button.isHidden) Color.Red else Color(0xFF1E293B),
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

fun getUniqueIconForButton(button: AACButton): ImageVector {
    val clean = button.label.lowercase().replace("📂", "").replace("📁", "").trim()
    
    return when {
        clean == "stop" -> Icons.Default.PanTool
        clean == "go" -> Icons.Default.DirectionsRun
        clean == "want" -> Icons.Default.TouchApp
        clean == "like" -> Icons.Default.Star
        clean == "eat" -> Icons.Default.Restaurant
        clean == "drink" -> Icons.Default.LocalCafe
        clean == "play" -> Icons.Default.SportsEsports
        clean == "help" -> Icons.Default.MedicalServices
        clean == "sleep" || clean == "tired" -> Icons.Default.Bedtime
        clean == "see" -> Icons.Default.Visibility
        clean == "hear" -> Icons.Default.Hearing
        
        // Folders & Categories
        clean.contains("food") -> Icons.Default.Fastfood
        clean.contains("places") || clean.contains("place") -> Icons.Default.Place
        clean.contains("toys") || clean.contains("toy") -> Icons.Default.SportsEsports
        clean.contains("people") || clean.contains("person") -> Icons.Default.Groups
        clean.contains("actions") || clean.contains("action") -> Icons.Default.FlashOn
        clean.contains("feelings") || clean.contains("feeling") -> Icons.Default.SentimentVerySatisfied
        clean.contains("saved") || clean.contains("phrases") -> Icons.Default.Bookmark
        
        // Pronouns / People
        clean == "i" || clean == "me" -> Icons.Default.Person
        clean == "you" -> Icons.Default.Person
        clean == "we" -> Icons.Default.People
        clean == "mom" || clean == "dad" || clean == "sister" || clean == "brother" || clean == "grandma" -> Icons.Default.Face
        clean == "doctor" -> Icons.Default.LocalHospital
        clean == "teacher" -> Icons.Default.School
        clean == "friend" -> Icons.Default.Handshake
        
        // Nouns & Places
        clean == "water" -> Icons.Default.WaterDrop
        clean == "bathroom" -> Icons.Default.Wc
        clean == "home" -> Icons.Default.Home
        clean == "school" -> Icons.Default.School
        clean == "bed" -> Icons.Default.Bed
        clean == "park" -> Icons.Default.Park
        clean == "outside" -> Icons.Default.WbSunny
        clean == "sensory room" -> Icons.Default.Spa
        clean == "store" -> Icons.Default.ShoppingCart
        clean == "library" -> Icons.Default.MenuBook
        clean == "pool" -> Icons.Default.Pool
        clean == "hospital" -> Icons.Default.LocalHospital
        
        // Food items
        clean == "pizza" -> Icons.Default.LocalPizza
        clean == "apple" || clean == "banana" -> Icons.Default.LocalDining
        clean == "sandwich" -> Icons.Default.LunchDining
        clean == "chicken" -> Icons.Default.Restaurant
        clean == "snack" -> Icons.Default.Cookie
        clean == "milk" || clean == "juice" -> Icons.Default.LocalBar
        
        // Toys & Objects
        clean == "blocks" -> Icons.Default.GridView
        clean == "ball" -> Icons.Default.SportsBaseball
        clean == "doll" -> Icons.Default.Face
        clean == "puzzle" -> Icons.Default.Extension
        clean == "car" -> Icons.Default.DirectionsCar
        clean == "tablet" -> Icons.Default.Tablet
        clean == "bubbles" -> Icons.Default.BubbleChart
        clean == "music" -> Icons.Default.MusicNote
        
        // Actions
        clean == "run" -> Icons.Default.DirectionsRun
        clean == "jump" -> Icons.Default.AccessibilityNew
        clean == "walk" -> Icons.Default.DirectionsWalk
        clean == "sit" -> Icons.Default.EventSeat
        clean == "stand" -> Icons.Default.Accessibility
        clean == "read" -> Icons.Default.MenuBook
        clean == "draw" -> Icons.Default.Create
        clean == "listen" -> Icons.Default.Headphones
        
        // Emotions & Adjectives
        clean == "happy" -> Icons.Default.SentimentVerySatisfied
        clean == "sad" || clean == "angry" || clean == "scared" -> Icons.Default.SentimentVeryDissatisfied
        clean == "silly" -> Icons.Default.EmojiEmotions
        clean == "excited" -> Icons.Default.Celebration
        clean == "calm" -> Icons.Default.Spa
        clean == "sick" -> Icons.Default.Sick
        clean == "good" -> Icons.Default.ThumbUp
        clean == "bad" -> Icons.Default.ThumbDown
        clean == "more" -> Icons.Default.Add
        clean == "hungry" -> Icons.Default.Restaurant
        
        // Social
        clean == "yes" -> Icons.Default.CheckCircle
        clean == "no" -> Icons.Default.Cancel
        clean == "please" -> Icons.Default.FavoriteBorder
        clean == "thanks" -> Icons.Default.StarOutline
        clean == "sorry" -> Icons.Default.SentimentSatisfied
        
        else -> when (button.category) {
            FitzgeraldCategory.PEOPLE -> Icons.Default.Face
            FitzgeraldCategory.ACTION -> Icons.Default.Bolt
            FitzgeraldCategory.NOUN -> Icons.Default.Category
            FitzgeraldCategory.ADJECTIVE -> Icons.Default.FavoriteBorder
            FitzgeraldCategory.SOCIAL -> Icons.Default.EmojiEmotions
            FitzgeraldCategory.FUNCTION -> Icons.Default.StarOutline
            FitzgeraldCategory.NAVIGATION -> Icons.Default.FolderOpen
        }
    }
}
