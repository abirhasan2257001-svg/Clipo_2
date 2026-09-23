package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.model.AudioClip
import com.example.model.TextOverlay
import com.example.model.VideoClip
import com.example.ui.theme.CapCutAudioTrack
import com.example.ui.theme.CapCutBorder
import com.example.ui.theme.CapCutCyan
import com.example.ui.theme.CapCutDarkBg
import com.example.ui.theme.CapCutPanelBg
import com.example.ui.theme.CapCutPlayheadWhite
import com.example.ui.theme.CapCutSurface
import com.example.ui.theme.CapCutSurfaceVariant
import com.example.ui.theme.CapCutTextMuted
import com.example.ui.theme.CapCutTextPrimary
import com.example.ui.theme.CapCutTextSecondary
import com.example.ui.theme.CapCutTextTrack
import com.example.ui.theme.CapCutYellow
import kotlin.math.roundToInt

private const val PIXELS_PER_SECOND = 60f // 60 dp per second for smooth timeline scrubbing

@Composable
fun TimelineView(
  currentTimeMs: Long,
  totalDurationMs: Long,
  clips: List<VideoClip>,
  selectedClipId: String?,
  audioTracks: List<AudioClip>,
  textTracks: List<TextOverlay>,
  isMuted: Boolean,
  onSeekTo: (Long) -> Unit,
  onSelectClip: (String?) -> Unit,
  onToggleMute: () -> Unit,
  onSetCover: () -> Unit,
  onAddMediaClick: () -> Unit,
  onAddAudioClick: () -> Unit,
  onAddTextClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val density = LocalDensity.current

  Row(
    modifier = modifier
      .fillMaxWidth()
      .background(CapCutDarkBg)
  ) {
    // Left Control Column (Fixed Rail)
    LeftTimelineRail(
      isMuted = isMuted,
      onToggleMute = onToggleMute,
      onSetCover = onSetCover,
      modifier = Modifier
        .width(100.dp)
        .fillMaxHeight()
    )

    // Center Vertical Playhead Divider Line
    Box(
      modifier = Modifier
        .width(1.5.dp)
        .fillMaxHeight()
        .background(CapCutBorder)
    )

    // Right Scrolling Tracks Area
    BoxWithConstraints(
      modifier = Modifier
        .weight(1f)
        .fillMaxHeight()
    ) {
      val trackAreaWidth = constraints.maxWidth.toFloat()
      val centerOffsetPx = trackAreaWidth / 2f
      val pixelsPerMs = (PIXELS_PER_SECOND / 1000f) * density.density

      // Track scroll position calculation: center playhead
      val scrollOffsetPx = (currentTimeMs * pixelsPerMs)

      // Outer drag listener for buttery smooth timeline scrubbing
      Box(
        modifier = Modifier
          .fillMaxSize()
          .testTag("timeline_tracks_container")
          .pointerInput(Unit) {
            detectDragGestures { change, dragAmount ->
              change.consume()
              val deltaMs = (-dragAmount.x / pixelsPerMs).toLong()
              onSeekTo(currentTimeMs + deltaMs)
            }
          }
      ) {
        // Horizontally offset tracks canvas
        Box(
          modifier = Modifier
            .fillMaxHeight()
            .offset { IntOffset((centerOffsetPx - scrollOffsetPx).roundToInt(), 0) }
        ) {
          Column(modifier = Modifier.fillMaxHeight()) {
            // Time Ruler Row
            TimelineRulerTicks(
              totalDurationMs = totalDurationMs,
              pixelsPerMs = pixelsPerMs,
              modifier = Modifier
                .height(26.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Main Video Track Row
            VideoFilmstripTrack(
              clips = clips,
              selectedClipId = selectedClipId,
              pixelsPerMs = pixelsPerMs,
              onSelectClip = onSelectClip,
              onAddMediaClick = onAddMediaClick,
              modifier = Modifier.height(64.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Audio Track Row
            AudioTrackStrip(
              audioTracks = audioTracks,
              pixelsPerMs = pixelsPerMs,
              onAddAudioClick = onAddAudioClick,
              modifier = Modifier.height(34.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Text Track Row
            TextTrackStrip(
              textTracks = textTracks,
              pixelsPerMs = pixelsPerMs,
              onAddTextClick = onAddTextClick,
              modifier = Modifier.height(34.dp)
            )
          }
        }

        // Center stationary Playhead Needle
        Box(
          modifier = Modifier
            .offset { IntOffset(centerOffsetPx.roundToInt(), 0) }
            .width(2.5.dp)
            .fillMaxHeight()
            .background(CapCutPlayheadWhite)
            .testTag("timeline_playhead_needle")
        )
      }
    }
  }
}

@Composable
private fun LeftTimelineRail(
  isMuted: Boolean,
  onToggleMute: () -> Unit,
  onSetCover: () -> Unit,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .background(CapCutDarkBg)
      .padding(horizontal = 8.dp, vertical = 6.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    // Spacer matching ruler height
    Spacer(modifier = Modifier.height(26.dp))

    // Top: Mute Clip & Cover side-by-side or stacked
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Mute clip button
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
          .clip(RoundedCornerShape(6.dp))
          .clickable { onToggleMute() }
          .padding(4.dp)
          .testTag("timeline_mute_clip_button")
      ) {
        Icon(
          imageVector = if (isMuted) Icons.Default.VolumeMute else Icons.Default.VolumeUp,
          contentDescription = "Mute clip",
          tint = if (isMuted) CapCutCyan else CapCutTextSecondary,
          modifier = Modifier.size(20.dp)
        )
        Text(
          text = if (isMuted) "Muted" else "Mute\nclip",
          color = CapCutTextSecondary,
          fontSize = 9.sp,
          lineHeight = 10.sp,
          fontWeight = FontWeight.Normal
        )
      }

      // Cover thumbnail box with pencil
      Box(
        modifier = Modifier
          .size(44.dp)
          .clip(RoundedCornerShape(8.dp))
          .background(CapCutSurfaceVariant)
          .clickable { onSetCover() }
          .testTag("timeline_cover_button"),
        contentAlignment = Alignment.Center
      ) {
        Image(
          painter = painterResource(id = R.drawable.clip_forest),
          contentDescription = "Cover thumbnail",
          modifier = Modifier.fillMaxSize(),
          contentScale = ContentScale.Crop,
          alpha = 0.5f
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = "Edit cover",
            tint = Color.White,
            modifier = Modifier.size(14.dp)
          )
          Text(
            text = "Cover",
            color = Color.White,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(18.dp))

    // Audio Track header icon (♪)
    Box(
      modifier = Modifier
        .size(28.dp)
        .clip(RoundedCornerShape(6.dp))
        .background(CapCutSurfaceVariant),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = "♪",
        color = CapCutCyan,
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold
      )
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Text Track header icon (T)
    Box(
      modifier = Modifier
        .size(28.dp)
        .clip(RoundedCornerShape(6.dp))
        .background(CapCutSurfaceVariant),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = "T",
        color = CapCutYellow,
        fontSize = 14.sp,
        fontWeight = FontWeight.Black
      )
    }
  }
}

@Composable
private fun TimelineRulerTicks(
  totalDurationMs: Long,
  pixelsPerMs: Float,
  modifier: Modifier = Modifier
) {
  val density = LocalDensity.current
  val stepSeconds = 2L
  val totalSeconds = (totalDurationMs / 1000L) + 10L

  Row(
    modifier = modifier,
    verticalAlignment = Alignment.CenterVertically
  ) {
    for (sec in 0..totalSeconds step stepSeconds) {
      val timeMs = sec * 1000L
      val widthPx = (stepSeconds * 1000f * pixelsPerMs)
      val widthDp = with(density) { widthPx.toDp() }

      Row(
        modifier = Modifier.width(widthDp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        val minutes = sec / 60
        val seconds = sec % 60
        Text(
          text = String.format("%02d:%02d", minutes, seconds),
          color = CapCutTextMuted,
          fontSize = 10.sp,
          fontFamily = FontFamily.Monospace
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
          text = "•",
          color = CapCutTextMuted,
          fontSize = 12.sp
        )
        Spacer(modifier = Modifier.weight(1f))
      }
    }
  }
}

@Composable
private fun VideoFilmstripTrack(
  clips: List<VideoClip>,
  selectedClipId: String?,
  pixelsPerMs: Float,
  onSelectClip: (String?) -> Unit,
  onAddMediaClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val density = LocalDensity.current

  Row(
    modifier = modifier,
    verticalAlignment = Alignment.CenterVertically
  ) {
    clips.forEach { clip ->
      val isSelected = clip.id == selectedClipId
      val clipWidthPx = clip.durationMs * pixelsPerMs
      val clipWidthDp = with(density) { clipWidthPx.toDp() }

      Box(
        modifier = Modifier
          .width(clipWidthDp)
          .fillMaxHeight()
          .clip(RoundedCornerShape(6.dp))
          .background(CapCutSurface)
          .border(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) CapCutPlayheadWhite else CapCutBorder,
            shape = RoundedCornerShape(6.dp)
          )
          .clickable { onSelectClip(clip.id) }
          .testTag("timeline_clip_${clip.id}")
      ) {
        // Filmstrip repeated frame preview
        Row(modifier = Modifier.fillMaxSize()) {
          val frameCount = (clipWidthPx / 70f).toInt().coerceAtLeast(1)
          for (i in 0 until frameCount) {
            Image(
              painter = painterResource(id = clip.resId ?: R.drawable.clip_forest),
              contentDescription = null,
              modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
              contentScale = ContentScale.Crop
            )
          }
        }

        // Subtle gradient overlay for readability
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(
              Brush.horizontalGradient(
                colors = listOf(Color.Black.copy(alpha = 0.35f), Color.Transparent, Color.Black.copy(alpha = 0.35f))
              )
            )
        )

        // Clip label
        Text(
          text = clip.name,
          color = Color.White,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          modifier = Modifier
            .align(Alignment.TopStart)
            .padding(4.dp)
            .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(3.dp))
            .padding(horizontal = 4.dp, vertical = 1.dp)
        )

        // Keyframe Diamonds along the clip!
        clip.keyframes.forEach { kf ->
          val kfOffsetPx = (kf.timeMs * pixelsPerMs)
          val kfOffsetDp = with(density) { kfOffsetPx.toDp() }

          Box(
            modifier = Modifier
              .offset(x = kfOffsetDp - 8.dp)
              .align(Alignment.CenterStart)
              .size(16.dp)
              .clip(CircleShape)
              .background(CapCutYellow)
              .border(1.5.dp, Color.Black, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "◇",
              color = Color.Black,
              fontSize = 11.sp,
              fontWeight = FontWeight.Black
            )
          }
        }

        // Selected clip trim handles at left & right
        if (isSelected) {
          Box(
            modifier = Modifier
              .width(8.dp)
              .fillMaxHeight()
              .background(CapCutPlayheadWhite)
              .align(Alignment.CenterStart)
          )
          Box(
            modifier = Modifier
              .width(8.dp)
              .fillMaxHeight()
              .background(CapCutPlayheadWhite)
              .align(Alignment.CenterEnd)
          )
        }
      }

      Spacer(modifier = Modifier.width(4.dp))
    }

    // Plus (+) Button to Add Media (White square like reference)
    Surface(
      color = Color.White,
      shape = RoundedCornerShape(8.dp),
      modifier = Modifier
        .size(42.dp)
        .clip(RoundedCornerShape(8.dp))
        .clickable { onAddMediaClick() }
        .testTag("timeline_add_media_button")
    ) {
      Box(contentAlignment = Alignment.Center) {
        Icon(
          imageVector = Icons.Default.Add,
          contentDescription = "Add Media",
          tint = Color.Black,
          modifier = Modifier.size(26.dp)
        )
      }
    }
  }
}

@Composable
private fun AudioTrackStrip(
  audioTracks: List<AudioClip>,
  pixelsPerMs: Float,
  onAddAudioClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val density = LocalDensity.current

  Row(
    modifier = modifier,
    verticalAlignment = Alignment.CenterVertically
  ) {
    if (audioTracks.isEmpty()) {
      // "+ Add audio" placeholder row
      Surface(
        color = CapCutSurfaceVariant.copy(alpha = 0.5f),
        shape = RoundedCornerShape(6.dp),
        modifier = Modifier
          .fillMaxHeight()
          .width(180.dp)
          .clip(RoundedCornerShape(6.dp))
          .clickable { onAddAudioClick() }
          .testTag("timeline_add_audio_strip")
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.padding(horizontal = 12.dp)
        ) {
          Text("+", color = CapCutCyan, fontSize = 16.sp, fontWeight = FontWeight.Bold)
          Spacer(modifier = Modifier.width(8.dp))
          Text("Add audio", color = CapCutTextSecondary, fontSize = 12.sp)
        }
      }
    } else {
      audioTracks.forEach { audio ->
        val widthPx = audio.durationMs * pixelsPerMs
        val widthDp = with(density) { widthPx.toDp() }

        Surface(
          color = CapCutAudioTrack,
          shape = RoundedCornerShape(6.dp),
          modifier = Modifier
            .width(widthDp.coerceAtLeast(100.dp))
            .fillMaxHeight()
            .clip(RoundedCornerShape(6.dp))
            .border(1.dp, CapCutCyan.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp)
          ) {
            Text("♪", color = CapCutCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = audio.title,
              color = CapCutTextPrimary,
              fontSize = 11.sp,
              fontWeight = FontWeight.Medium,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }
        }

        Spacer(modifier = Modifier.width(4.dp))
      }

      // Add more audio button
      Surface(
        color = CapCutSurfaceVariant.copy(alpha = 0.6f),
        shape = RoundedCornerShape(6.dp),
        modifier = Modifier
          .height(30.dp)
          .clip(RoundedCornerShape(6.dp))
          .clickable { onAddAudioClick() }
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.padding(horizontal = 8.dp)
        ) {
          Text("+ Add audio", color = CapCutCyan, fontSize = 11.sp)
        }
      }
    }
  }
}

@Composable
private fun TextTrackStrip(
  textTracks: List<TextOverlay>,
  pixelsPerMs: Float,
  onAddTextClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val density = LocalDensity.current

  Row(
    modifier = modifier,
    verticalAlignment = Alignment.CenterVertically
  ) {
    if (textTracks.isEmpty()) {
      // "+ Add text" placeholder row
      Surface(
        color = CapCutSurfaceVariant.copy(alpha = 0.5f),
        shape = RoundedCornerShape(6.dp),
        modifier = Modifier
          .fillMaxHeight()
          .width(180.dp)
          .clip(RoundedCornerShape(6.dp))
          .clickable { onAddTextClick() }
          .testTag("timeline_add_text_strip")
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.padding(horizontal = 12.dp)
        ) {
          Text("+", color = CapCutYellow, fontSize = 16.sp, fontWeight = FontWeight.Bold)
          Spacer(modifier = Modifier.width(8.dp))
          Text("Add text", color = CapCutTextSecondary, fontSize = 12.sp)
        }
      }
    } else {
      textTracks.forEach { textOverlay ->
        val widthPx = textOverlay.durationMs * pixelsPerMs
        val widthDp = with(density) { widthPx.toDp() }

        Surface(
          color = CapCutTextTrack,
          shape = RoundedCornerShape(6.dp),
          modifier = Modifier
            .width(widthDp.coerceAtLeast(90.dp))
            .fillMaxHeight()
            .clip(RoundedCornerShape(6.dp))
            .border(1.dp, CapCutYellow.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp)
          ) {
            Text("T", color = CapCutYellow, fontSize = 11.sp, fontWeight = FontWeight.Black)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = textOverlay.text,
              color = CapCutTextPrimary,
              fontSize = 11.sp,
              fontWeight = FontWeight.Medium,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }
        }

        Spacer(modifier = Modifier.width(4.dp))
      }

      // Add more text button
      Surface(
        color = CapCutSurfaceVariant.copy(alpha = 0.6f),
        shape = RoundedCornerShape(6.dp),
        modifier = Modifier
          .height(30.dp)
          .clip(RoundedCornerShape(6.dp))
          .clickable { onAddTextClick() }
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.padding(horizontal = 8.dp)
        ) {
          Text("+ Add text", color = CapCutYellow, fontSize = 11.sp)
        }
      }
    }
  }
}
