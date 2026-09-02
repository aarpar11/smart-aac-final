package com.example.data.vision

import android.webkit.JavascriptInterface
import com.example.ui.viewmodel.AACViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

class FacialVisionBridge(
    private val viewModel: AACViewModel,
    private val scope: CoroutineScope
) {
    @JavascriptInterface
    fun postFacialEvent(payload: String) {
        scope.launch(Dispatchers.Main) {
            runCatching {
                val json = JSONObject(payload)
                val trigger = json.optString("trigger", "NONE")
                val progress = json.optDouble("progress", 0.0).toFloat()
                val isFired = json.optBoolean("isFired", false)
                val dominantEmotion = json.optString("dominantEmotion", "neutral")
                val emotionConfidence = json.optDouble("confidence", 0.0).toFloat()
                
                val eyeOpenness = json.optDouble("eyeOpenness", 1.0).toFloat()
                val mouthOpenness = json.optDouble("mouthOpenness", 1.0).toFloat()
                val browFurrow = json.optDouble("browFurrow", 0.0).toFloat()
                val browRaise = json.optDouble("browRaise", 0.0).toFloat()
                val smileScore = json.optDouble("smileScore", 0.0).toFloat()

                viewModel.onFacialEvent(
                    trigger = trigger,
                    progress = progress,
                    isFired = isFired,
                    emotion = dominantEmotion,
                    confidence = emotionConfidence,
                    eyeOpenness = eyeOpenness,
                    mouthOpenness = mouthOpenness,
                    browFurrow = browFurrow,
                    browRaise = browRaise,
                    smileScore = smileScore
                )
            }.onFailure {
                it.printStackTrace()
            }
        }
    }

    @JavascriptInterface
    fun onBaselineCalibrated(baselineJson: String) {
        scope.launch(Dispatchers.Main) {
            viewModel.onBaselineCalibrated(baselineJson)
        }
    }

    @JavascriptInterface
    fun onVisionStatus(status: String) {
        scope.launch(Dispatchers.Main) {
            viewModel.onVisionStatus(status)
        }
    }
}
