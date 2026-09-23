package com.example.model

import androidx.annotation.DrawableRes
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

enum class CurveType(val displayName: String, val description: String) {
  LINEAR("Linear", "Constant speed"),
  EASE_IN("Ease In", "Starts slow, ends fast"),
  EASE_OUT("Ease Out", "Starts fast, ends slow"),
  EASE_IN_OUT("Ease In-Out", "Smooth acceleration & deceleration"),
  BOUNCE("Bounce", "Bounces smoothly at destination"),
  FLOW("Flow", "Dynamic cinematic wave"),
  JUMP("Jump", "Snappy step jump");

  fun evaluate(t: Float): Float {
    val clamped = t.coerceIn(0f, 1f)
    return when (this) {
      LINEAR -> clamped
      EASE_IN -> clamped * clamped * clamped
      EASE_OUT -> 1f - (1f - clamped).pow(3)
      EASE_IN_OUT -> {
        if (clamped < 0.5f) 4f * clamped * clamped * clamped
        else 1f - (-2f * clamped + 2f).pow(3) / 2f
      }
      BOUNCE -> {
        val n1 = 7.5625f
        val d1 = 2.75f
        var x = clamped
        when {
          x < 1f / d1 -> n1 * x * x
          x < 2f / d1 -> {
            x -= 1.5f / d1
            n1 * x * x + 0.75f
          }
          x < 2.5f / d1 -> {
            x -= 2.25f / d1
            n1 * x * x + 0.9375f
          }
          else -> {
            x -= 2.625f / d1
            n1 * x * x + 0.984375f
          }
        }
      }
      FLOW -> sin(clamped * PI.toFloat() / 2f)
      JUMP -> if (clamped < 0.5f) 0f else 1f
    }
  }
}

data class Keyframe(
  val id: String = java.util.UUID.randomUUID().toString(),
  val timeMs: Long,
  val posX: Float = 0f,
  val posY: Float = 0f,
  val scale: Float = 1f,
  val rotation: Float = 0f,
  val opacity: Float = 1f,
  val curveType: CurveType = CurveType.EASE_IN_OUT
)

data class TransformState(
  val posX: Float = 0f,
  val posY: Float = 0f,
  val scale: Float = 1f,
  val rotation: Float = 0f,
  val opacity: Float = 1f
)

enum class FilterType(val displayName: String) {
  NONE("Normal"),
  VIBRANT("Vibrant"),
  CINEMATIC("Cinematic"),
  VINTAGE("Vintage"),
  CYBERPUNK("Cyberpunk"),
  MONO("B & W"),
  WARM_SUN("Warm Sun"),
  COLD_ICE("Cold Ice")
}

enum class AnimationType(val displayName: String) {
  NONE("None"),
  FADE_IN("Fade In"),
  FADE_OUT("Fade Out"),
  ZOOM_IN("Zoom In"),
  ZOOM_OUT("Zoom Out"),
  SPIN_CLOCKWISE("Spin In"),
  BOUNCE_IN("Bounce In"),
  SHAKE("Camera Shake")
}

data class VideoClip(
  val id: String = java.util.UUID.randomUUID().toString(),
  val name: String,
  @DrawableRes val resId: Int? = null,
  val imageUri: String? = null,
  val startMs: Long,
  val durationMs: Long,
  val sourceDurationMs: Long = 60000L,
  val trimInMs: Long = 0L,
  val trimOutMs: Long = 0L,
  val speed: Float = 1.0f,
  val volume: Float = 1.0f,
  val keyframes: List<Keyframe> = emptyList(),
  val filter: FilterType = FilterType.NONE,
  val animationIn: AnimationType = AnimationType.NONE,
  val animationOut: AnimationType = AnimationType.NONE,
  val brightness: Float = 0f,
  val contrast: Float = 1f,
  val saturation: Float = 1f,
  val vignette: Float = 0f,
  val defaultTransform: TransformState = TransformState()
) {
  fun getTransformAt(clipRelativeTimeMs: Long): TransformState {
    if (keyframes.isEmpty()) {
      return defaultTransform
    }
    if (keyframes.size == 1) {
      val kf = keyframes.first()
      return TransformState(kf.posX, kf.posY, kf.scale, kf.rotation, kf.opacity)
    }

    val sorted = keyframes.sortedBy { it.timeMs }
    if (clipRelativeTimeMs <= sorted.first().timeMs) {
      val first = sorted.first()
      return TransformState(first.posX, first.posY, first.scale, first.rotation, first.opacity)
    }
    if (clipRelativeTimeMs >= sorted.last().timeMs) {
      val last = sorted.last()
      return TransformState(last.posX, last.posY, last.scale, last.rotation, last.opacity)
    }

    // Find the pair between which clipRelativeTimeMs falls
    for (i in 0 until sorted.size - 1) {
      val kf1 = sorted[i]
      val kf2 = sorted[i + 1]
      if (clipRelativeTimeMs in kf1.timeMs..kf2.timeMs) {
        val totalDelta = (kf2.timeMs - kf1.timeMs).coerceAtLeast(1L).toFloat()
        val progress = (clipRelativeTimeMs - kf1.timeMs) / totalDelta
        val eased = kf1.curveType.evaluate(progress)

        return TransformState(
          posX = kf1.posX + (kf2.posX - kf1.posX) * eased,
          posY = kf1.posY + (kf2.posY - kf1.posY) * eased,
          scale = kf1.scale + (kf2.scale - kf1.scale) * eased,
          rotation = kf1.rotation + (kf2.rotation - kf1.rotation) * eased,
          opacity = (kf1.opacity + (kf2.opacity - kf1.opacity) * eased).coerceIn(0f, 1f)
        )
      }
    }

    val fallback = sorted.first()
    return TransformState(fallback.posX, fallback.posY, fallback.scale, fallback.rotation, fallback.opacity)
  }
}

data class AudioClip(
  val id: String = java.util.UUID.randomUUID().toString(),
  val title: String,
  val startMs: Long,
  val durationMs: Long,
  val volume: Float = 1.0f,
  val isBgm: Boolean = false,
  val soundType: String = "FX"
)

data class TextOverlay(
  val id: String = java.util.UUID.randomUUID().toString(),
  val text: String,
  val startMs: Long,
  val durationMs: Long,
  val posX: Float = 0f,
  val posY: Float = 0f,
  val scale: Float = 1f,
  val rotation: Float = 0f,
  val colorHex: Long = 0xFFFFFFFF,
  val fontSizeSp: Float = 22f,
  val styleName: String = "Classic",
  val animation: AnimationType = AnimationType.NONE
)

enum class EffectType(val displayName: String, val icon: String) {
  SHAKE("Shake", "⚡"),
  GLITCH("Glitch", "▞"),
  RGB_SPLIT("RGB Split", "◈"),
  CINEMA_BARS("Cinema 2.35:1", "▬"),
  NEON_EDGES("Neon Glow", "✧"),
  FILM_GRAIN("Film Grain", "∿"),
  VIGNETTE_PULSE("Vignette", "◐")
}

data class VideoEffect(
  val id: String = java.util.UUID.randomUUID().toString(),
  val type: EffectType,
  val name: String,
  val intensity: Float = 0.5f,
  val startMs: Long,
  val durationMs: Long
)

enum class AspectRatio(val label: String, val ratio: Float) {
  RATIO_16_9("16:9", 16f / 9f),
  RATIO_9_16("9:16", 9f / 16f),
  RATIO_1_1("1:1", 1f),
  RATIO_4_3("4:3", 4f / 3f),
  RATIO_21_9("21:9", 21f / 9f)
}

enum class EditorMode(val title: String) {
  MAIN("Overview"),
  EDIT_CLIP("Edit Clip"),
  KEYFRAME_STUDIO("Keyframe Studio"),
  SPEED_STUDIO("Speed Control"),
  AUDIO("Audio & FX"),
  TEXT("Text & Titles"),
  EFFECTS("Video Effects"),
  OVERLAY("Overlay (PIP)"),
  CAPTIONS("Captions"),
  FILTERS("Filters & Presets"),
  ADJUST("Color Grading"),
  CANVAS("Canvas Ratio")
}

enum class ExportResolution(val label: String, val width: Int, val height: Int) {
  RES_720P("720P HD", 1280, 720),
  RES_1080P("1080P FHD", 1920, 1080),
  RES_2K("2K QHD", 2560, 1440),
  RES_AI_UHD("AI UHD (4K)", 3840, 2160)
}

data class ExportConfig(
  val resolution: ExportResolution = ExportResolution.RES_AI_UHD,
  val fps: Int = 60,
  val smartHdr: Boolean = true,
  val aiSuperResolution: Boolean = true,
  val hardwareAcceleration: Boolean = true,
  val bitrateMbps: Float = 25f
)
