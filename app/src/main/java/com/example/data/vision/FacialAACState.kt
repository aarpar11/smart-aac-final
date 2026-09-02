package com.example.data.vision

data class FacialAACState(
    val trigger: String = "NONE",
    val progress: Float = 0f,
    val isFired: Boolean = false,
    val dominantEmotion: String = "neutral",
    val emotionConfidence: Float = 0f,
    val eyeOpenness: Float = 1f,
    val mouthOpenness: Float = 1f,
    val browFurrow: Float = 0f,
    val browRaise: Float = 0f,
    val smileScore: Float = 0f,
    val isTrackingActive: Boolean = false
)

enum class FacialTriggerType(val displayName: String, val description: String) {
    NONE("Resting", "Tracking facial movements"),
    BROW_RAISE_CONFIRM("Brow Raise", "Hold to speak or confirm selection"),
    MOUTH_OPEN_SELECT("Mouth Open", "Hold to select next tile"),
    SUSTAINED_SMILE("Sustained Smile", "Hold to open Feelings & Social board"),
    SUSTAINED_FROWN("Discomfort / Furrow", "Hold to open Needs & Help board"),
    LONG_BLINK_CANCEL("Long Blink", "Hold to delete last word / navigate back")
}
