package com.example.ui.components

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.view.ViewGroup
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.vision.FacialAACState
import com.example.data.vision.FacialVisionBridge
import com.example.ui.viewmodel.AACViewModel

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun FacialVisionOverlay(
    viewModel: AACViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val facialState by viewModel.facialState.collectAsStateWithLifecycle()
    val isEnabled by viewModel.isFacialTrackingEnabled.collectAsStateWithLifecycle()
    val activeEmotion by viewModel.activeEmotion.collectAsStateWithLifecycle()
    val manualEmotion by viewModel.manualEmotionOverride.collectAsStateWithLifecycle()
    val isEmotionSortingEnabled by viewModel.isEmotionSortingEnabled.collectAsStateWithLifecycle()

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasCameraPermission = granted
        }
    )

    var isExpanded by remember { mutableStateOf(true) }
    var webViewRef by remember { mutableStateOf<WebView?>(null) }

    LaunchedEffect(isEnabled) {
        if (isEnabled && !hasCameraPermission) {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }

    if (!isEnabled) return

    val emotionsList = listOf(
        null to "🔄 Auto",
        "happy" to "😊 Happy",
        "sad" to "😢 Sad",
        "angry" to "😠 Frustrated",
        "fearful" to "😨 Fearful",
        "disgusted" to "😣 Discomfort",
        "surprised" to "😲 Surprised",
        "neutral" to "😐 Neutral"
    )

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .testTag("facial_vision_hud")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    facialState.isFired -> Color(0xFFF59E0B)
                                    facialState.trigger != "NONE" -> Color(0xFF10B981)
                                    else -> Color(0xFF38BDF8)
                                }
                            )
                    )
                    Text(
                        text = "Facial Vision AAC",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color.White
                    )

                    val emotionEmoji = when (activeEmotion) {
                        "happy" -> "😊 Happy"
                        "sad" -> "😢 Sad"
                        "angry" -> "😠 Frustrated"
                        "surprised" -> "😲 Surprised"
                        "fearful" -> "😨 Fearful"
                        "disgusted" -> "😣 Discomfort"
                        else -> "😐 Neutral"
                    }
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (manualEmotion != null) Color(0xFF3B82F6) else Color(0xFF1E293B),
                        modifier = Modifier.padding(start = 4.dp)
                    ) {
                        Text(
                            text = if (manualEmotion != null) "$emotionEmoji (Manual)" else emotionEmoji,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFFE2E8F0),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }

                IconButton(
                    onClick = { isExpanded = !isExpanded },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = "Expand Vision Controls",
                        tint = Color(0xFF94A3B8)
                    )
                }
            }

            // Emotion Relevance Test & Selector Bar
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Emotion:",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF94A3B8),
                    modifier = Modifier.padding(end = 2.dp)
                )
                emotionsList.forEach { (emoKey, label) ->
                    val isSelected = (emoKey == null && manualEmotion == null) || (emoKey != null && manualEmotion == emoKey)
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) Color(0xFF2563EB) else Color(0xFF1E293B),
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                viewModel.setManualEmotion(emoKey)
                            }
                            .border(
                                width = if (isSelected) 1.5.dp else 0.5.dp,
                                color = if (isSelected) Color(0xFF60A5FA) else Color(0xFF334155),
                                shape = RoundedCornerShape(8.dp)
                            )
                    ) {
                        Text(
                            text = label,
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color.White else Color(0xFFCBD5E1),
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            if (facialState.trigger != "NONE") {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val triggerTitle = when (facialState.trigger) {
                        "BROW_RAISE_CONFIRM" -> "⬆️ Brow Raise (Speak / Confirm)"
                        "MOUTH_OPEN_SELECT" -> "👄 Mouth Open (Select Word)"
                        "SUSTAINED_SMILE" -> "😊 Smile (Feelings Board)"
                        "SUSTAINED_FROWN" -> "😟 Discomfort (Needs Board)"
                        "LONG_BLINK_CANCEL" -> "👁️ Long Blink (Cancel / Back)"
                        else -> facialState.trigger
                    }
                    Text(
                        text = triggerTitle,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (facialState.isFired) Color(0xFFFBBF24) else Color(0xFF34D399)
                    )
                    Text(
                        text = "${(facialState.progress * 100).toInt()}%",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { facialState.progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = if (facialState.isFired) Color(0xFFF59E0B) else Color(0xFF10B981),
                    trackColor = Color(0xFF334155)
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (isExpanded) 140.dp else 1.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.Black)
                    .padding(top = if (isExpanded) 8.dp else 0.dp)
            ) {
                AndroidView(
                    factory = { ctx ->
                        WebView(ctx).apply {
                            layoutParams = ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                            settings.apply {
                                javaScriptEnabled = true
                                domStorageEnabled = true
                                mediaPlaybackRequiresUserGesture = false
                                allowFileAccess = true
                                cacheMode = WebSettings.LOAD_DEFAULT
                            }
                            webChromeClient = object : WebChromeClient() {
                                override fun onPermissionRequest(request: PermissionRequest?) {
                                    request?.grant(request.resources)
                                }
                            }
                            webViewClient = WebViewClient()
                            addJavascriptInterface(
                                FacialVisionBridge(viewModel, coroutineScope),
                                "AndroidVisionBridge"
                            )
                            loadUrl("file:///android_asset/facial_vision/index.html")
                            webViewRef = this
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }

            if (isExpanded) {
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            webViewRef?.evaluateJavascript("startCalibration();", null)
                        },
                        modifier = Modifier.weight(1.1f),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("Calibrate", fontSize = 11.sp)
                    }

                    OutlinedButton(
                        onClick = {
                            webViewRef?.evaluateJavascript("simulateTrigger('BROW_RAISE_CONFIRM', 'neutral');", null)
                        },
                        modifier = Modifier.weight(1.2f),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Text("Brow Raise", fontSize = 11.sp)
                    }

                    OutlinedButton(
                        onClick = {
                            webViewRef?.evaluateJavascript("simulateTrigger('SUSTAINED_SMILE', 'happy');", null)
                        },
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Text("Smile", fontSize = 11.sp)
                    }

                    OutlinedButton(
                        onClick = {
                            webViewRef?.evaluateJavascript("simulateTrigger('SUSTAINED_FROWN', 'sad');", null)
                        },
                        modifier = Modifier.weight(1.2f),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Text("Discomfort", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}
