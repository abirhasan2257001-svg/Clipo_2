package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.model.AspectRatio
import com.example.model.EffectType
import com.example.model.FilterType
import com.example.model.TextOverlay
import com.example.model.TransformState
import com.example.model.VideoClip
import com.example.model.VideoEffect
import com.example.ui.theme.CapCutCyan
import com.example.ui.theme.CapCutDarkBg
import com.example.ui.theme.CapCutYellow
import kotlin.math.roundToInt
import kotlin.random.Random

@Composable
fun PreviewCanvas(
  clip: VideoClip?,
  currentTimeMs: Long,
  transform: TransformState,
  aspectRatio: AspectRatio,
  compareMode: Boolean,
  isPlaying: Boolean,
  effects: List<VideoEffect>,
  textTracks: List<TextOverlay>,
  onTransformChange: (Float, Float, Float, Float) -> Unit,
  modifier: Modifier = Modifier
) {
  // Shake & Glitch dynamic offsets during playback
  val shakeOffset = remember(isPlaying, currentTimeMs) {
    val shakeEffect = effects.find { it.type == EffectType.SHAKE && currentTimeMs in it.startMs..(it.startMs + it.durationMs) }
    if (shakeEffect != null && isPlaying) {
      Offset(
        (Random.nextFloat() - 0.5f) * 24f * shakeEffect.intensity,
        (Random.nextFloat() - 0.5f) * 24f * shakeEffect.intensity
      )
    } else Offset.Zero
  }

  val hasCinemaBars = effects.any { it.type == EffectType.CINEMA_BARS && currentTimeMs in it.startMs..(it.startMs + it.durationMs) }
  val hasGlitch = effects.any { it.type == EffectType.GLITCH && currentTimeMs in it.startMs..(it.startMs + it.durationMs) }

  Box(
    modifier = modifier
      .fillMaxWidth()
      .background(CapCutDarkBg),
    contentAlignment = Alignment.Center
  ) {
    // Screen container keeping the aspect ratio
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .aspectRatio(aspectRatio.ratio)
        .background(Color.Black)
        .clip(RoundedCornerShape(0.dp))
        .testTag("video_preview_canvas")
        .pointerInput(clip?.id) {
          detectTransformGestures { _, pan, zoom, rotation ->
            val newX = transform.posX + pan.x
            val newY = transform.posY + pan.y
            val newScale = (transform.scale * zoom).coerceIn(0.2f, 5.0f)
            val newRotation = (transform.rotation + rotation)
            onTransformChange(newX, newY, newScale, newRotation)
          }
        },
      contentAlignment = Alignment.Center
    ) {
      // Content layer
      if (clip != null) {
        val colorMatrix = remember(clip.filter, clip.brightness, clip.contrast, clip.saturation) {
          createFilterMatrix(clip.filter, clip.brightness, clip.contrast, clip.saturation)
        }

        Box(
          modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
              translationX = transform.posX + shakeOffset.x + if (hasGlitch && isPlaying) (Random.nextFloat() - 0.5f) * 12f else 0f
              translationY = transform.posY + shakeOffset.y
              scaleX = transform.scale
              scaleY = transform.scale
              rotationZ = transform.rotation
              alpha = transform.opacity.coerceIn(0f, 1f)
            },
          contentAlignment = Alignment.Center
        ) {
          // Video Clip image render (either resource or coil uri)
          if (clip.imageUri != null) {
            AsyncImage(
              model = ImageRequest.Builder(LocalContext.current)
                .data(clip.imageUri)
                .crossfade(true)
                .build(),
              contentDescription = "Clip Video Frame",
              modifier = Modifier.fillMaxSize(),
              contentScale = ContentScale.Crop,
              colorFilter = ColorFilter.colorMatrix(colorMatrix)
            )
          } else {
            Image(
              painter = painterResource(id = clip.resId ?: R.drawable.clip_forest),
              contentDescription = "Stickman Forest Video Frame",
              modifier = Modifier.fillMaxSize(),
              contentScale = ContentScale.Crop,
              colorFilter = ColorFilter.colorMatrix(colorMatrix)
            )
          }

          // Subtle vignette overlay if adjusted or effect present
          if (clip.vignette > 0f) {
            Box(
              modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = (clip.vignette * 0.7f).coerceIn(0f, 0.8f)))
            )
          }

          // Bounding Box with transform handles when paused/selected
          if (!isPlaying) {
            TransformBoundingBox()
          }
        }

        // Compare Mode Split Screen: Left is original raw, right is graded
        if (compareMode) {
          Box(
            modifier = Modifier
              .fillMaxWidth(0.5f)
              .fillMaxHeight()
              .align(Alignment.CenterStart)
              .clip(RoundedCornerShape(0.dp))
              .background(Color.Black)
          ) {
            Image(
              painter = painterResource(id = clip.resId ?: R.drawable.clip_forest),
              contentDescription = "Raw Original",
              modifier = Modifier.fillMaxSize(),
              contentScale = ContentScale.Crop
            )
            // Left Label
            Box(
              modifier = Modifier
                .padding(8.dp)
                .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Text("ORIGINAL", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
          }

          // Center Divider
          Box(
            modifier = Modifier
              .fillMaxHeight()
              .width(2.dp)
              .background(Color.White)
              .align(Alignment.Center)
          )
        }
      }

      // Cinema Bars Effect
      if (hasCinemaBars) {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
          Box(modifier = Modifier.fillMaxWidth().height(28.dp).background(Color.Black))
          Box(modifier = Modifier.fillMaxWidth().height(28.dp).background(Color.Black))
        }
      }

      // Text Overlays on Canvas
      textTracks.filter { currentTimeMs in it.startMs..(it.startMs + it.durationMs) }.forEach { textItem ->
        TextOverlayItem(textItem = textItem)
      }

      // Keyframe Active Badge on top-right of canvas when keyframed
      if (clip?.keyframes?.isNotEmpty() == true) {
        Box(
          modifier = Modifier
            .align(Alignment.TopEnd)
            .padding(10.dp)
            .background(Color.Black.copy(alpha = 0.65f), RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text("◇", color = CapCutYellow, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              "Keyframe Active",
              color = Color.White,
              fontSize = 11.sp,
              fontWeight = FontWeight.Medium
            )
          }
        }
      }
    }
  }
}

@Composable
private fun TransformBoundingBox() {
  Box(
    modifier = Modifier
      .fillMaxSize()
      .padding(12.dp)
      .border(1.5.dp, CapCutCyan.copy(alpha = 0.85f), RoundedCornerShape(4.dp))
  ) {
    // 4 Corner circle handles (CapCut style)
    Box(
      modifier = Modifier
        .size(10.dp)
        .align(Alignment.TopStart)
        .offset((-5).dp, (-5).dp)
        .background(Color.White, CircleShape)
        .border(1.dp, CapCutCyan, CircleShape)
    )
    Box(
      modifier = Modifier
        .size(10.dp)
        .align(Alignment.TopEnd)
        .offset(5.dp, (-5).dp)
        .background(Color.White, CircleShape)
        .border(1.dp, CapCutCyan, CircleShape)
    )
    Box(
      modifier = Modifier
        .size(10.dp)
        .align(Alignment.BottomStart)
        .offset((-5).dp, 5.dp)
        .background(Color.White, CircleShape)
        .border(1.dp, CapCutCyan, CircleShape)
    )
    Box(
      modifier = Modifier
        .size(10.dp)
        .align(Alignment.BottomEnd)
        .offset(5.dp, 5.dp)
        .background(Color.White, CircleShape)
        .border(1.dp, CapCutCyan, CircleShape)
    )
  }
}

@Composable
private fun TextOverlayItem(textItem: TextOverlay) {
  Box(
    modifier = Modifier
      .offset { IntOffset(textItem.posX.roundToInt(), textItem.posY.roundToInt()) }
      .graphicsLayer {
        scaleX = textItem.scale
        scaleY = textItem.scale
        rotationZ = textItem.rotation
      }
      .background(
        if (textItem.styleName.contains("Box")) Color.Black.copy(alpha = 0.6f) else Color.Transparent,
        RoundedCornerShape(6.dp)
      )
      .padding(horizontal = 8.dp, vertical = 4.dp),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = textItem.text,
      color = Color(textItem.colorHex),
      fontSize = textItem.fontSizeSp.sp,
      fontWeight = FontWeight.Black,
      textAlign = TextAlign.Center
    )
  }
}

private fun createFilterMatrix(
  filter: FilterType,
  brightness: Float,
  contrast: Float,
  saturation: Float
): ColorMatrix {
  val matrix = ColorMatrix()

  // Base preset
  when (filter) {
    FilterType.NONE -> {}
    FilterType.VIBRANT -> {
      matrix.setToSaturation(1.4f * saturation)
    }
    FilterType.CINEMATIC -> {
      // Teal and orange feel
      val m = floatArrayOf(
        1.15f * contrast, 0.05f, 0.0f, 0f, 15f + brightness * 50f,
        0.0f, 1.05f * contrast, 0.05f, 0f, 5f + brightness * 50f,
        0.05f, 0.0f, 1.25f * contrast, 0f, 25f + brightness * 50f,
        0f, 0f, 0f, 1f, 0f
      )
      return ColorMatrix(m)
    }
    FilterType.VINTAGE -> {
      val m = floatArrayOf(
        0.9f * contrast, 0.2f, 0.1f, 0f, 20f + brightness * 50f,
        0.1f, 0.8f * contrast, 0.1f, 0f, 15f + brightness * 50f,
        0.1f, 0.1f, 0.6f * contrast, 0f, 0f + brightness * 50f,
        0f, 0f, 0f, 1f, 0f
      )
      return ColorMatrix(m)
    }
    FilterType.CYBERPUNK -> {
      val m = floatArrayOf(
        1.3f * contrast, 0.0f, 0.2f, 0f, 30f + brightness * 50f,
        0.0f, 1.1f * contrast, 0.3f, 0f, 0f + brightness * 50f,
        0.3f, 0.0f, 1.4f * contrast, 0f, 40f + brightness * 50f,
        0f, 0f, 0f, 1f, 0f
      )
      return ColorMatrix(m)
    }
    FilterType.MONO -> {
      matrix.setToSaturation(0f)
    }
    FilterType.WARM_SUN -> {
      val m = floatArrayOf(
        1.2f * contrast, 0.1f, 0.0f, 0f, 25f + brightness * 50f,
        0.0f, 1.1f * contrast, 0.0f, 0f, 15f + brightness * 50f,
        0.0f, 0.0f, 0.8f * contrast, 0f, 0f + brightness * 50f,
        0f, 0f, 0f, 1f, 0f
      )
      return ColorMatrix(m)
    }
    FilterType.COLD_ICE -> {
      val m = floatArrayOf(
        0.85f * contrast, 0.0f, 0.1f, 0f, 0f + brightness * 50f,
        0.0f, 1.0f * contrast, 0.1f, 0f, 10f + brightness * 50f,
        0.1f, 0.2f, 1.3f * contrast, 0f, 35f + brightness * 50f,
        0f, 0f, 0f, 1f, 0f
      )
      return ColorMatrix(m)
    }
  }

  // Multiply saturation if not already custom matrix
  if (filter == FilterType.NONE || filter == FilterType.VIBRANT) {
    matrix.setToSaturation(saturation)
  }

  return matrix
}
