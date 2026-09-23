package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Keyframe
import com.example.model.VideoClip
import com.example.ui.theme.CapCutCyan
import com.example.ui.theme.CapCutDarkBg
import com.example.ui.theme.CapCutSurfaceVariant
import com.example.ui.theme.CapCutTextMuted
import com.example.ui.theme.CapCutTextPrimary
import com.example.ui.theme.CapCutTextSecondary
import com.example.ui.theme.CapCutYellow
import java.util.Locale

@Composable
fun TimelineRulerBar(
  currentTimeMs: Long,
  totalDurationMs: Long,
  selectedClip: VideoClip?,
  activeKeyframeAtPlayhead: Keyframe?,
  onToggleKeyframe: () -> Unit,
  onJumpPrevKeyframe: () -> Unit,
  onJumpNextKeyframe: () -> Unit,
  onOpenCurves: () -> Unit,
  modifier: Modifier = Modifier
) {
  val formattedCurrent = formatTimeCode(currentTimeMs)
  val formattedTotal = formatTimeCode(totalDurationMs)

  Column(
    modifier = modifier
      .fillMaxWidth()
      .background(CapCutDarkBg)
  ) {
    // Top Row: Time display on left & Keyframe controls on right (or ruler)
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .height(38.dp)
        .padding(horizontal = 16.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      // Timecode display: e.g. "00:00 / 01:00"
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.testTag("timeline_timecode_display")
      ) {
        Text(
          text = formattedCurrent,
          color = CapCutTextPrimary,
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold,
          fontFamily = FontFamily.Monospace
        )
        Text(
          text = " / ",
          color = CapCutTextMuted,
          fontSize = 13.sp,
          fontFamily = FontFamily.Monospace
        )
        Text(
          text = formattedTotal,
          color = CapCutTextSecondary,
          fontSize = 13.sp,
          fontFamily = FontFamily.Monospace
        )
      }

      // Keyframe Action Bar (visible when clip selected or always available)
      if (selectedClip != null) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.testTag("keyframe_action_bar")
        ) {
          // Jump Previous Keyframe
          Text(
            text = "◁◇",
            color = CapCutTextSecondary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .clickable { onJumpPrevKeyframe() }
              .padding(horizontal = 4.dp, vertical = 2.dp)
              .testTag("keyframe_jump_prev")
          )

          // Main Diamond Keyframe Add/Remove Button!
          val isAtKeyframe = activeKeyframeAtPlayhead != null
          Surface(
            color = if (isAtKeyframe) CapCutYellow else CapCutSurfaceVariant,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
              .height(28.dp)
              .clip(RoundedCornerShape(8.dp))
              .clickable { onToggleKeyframe() }
              .testTag("keyframe_toggle_button")
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.padding(horizontal = 10.dp)
            ) {
              Text(
                text = "◇",
                color = if (isAtKeyframe) Color.Black else CapCutYellow,
                fontSize = 14.sp,
                fontWeight = FontWeight.Black
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = if (isAtKeyframe) "Remove" else "Keyframe",
                color = if (isAtKeyframe) Color.Black else CapCutTextPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }

          // Jump Next Keyframe
          Text(
            text = "◇▷",
            color = CapCutTextSecondary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .clickable { onJumpNextKeyframe() }
              .padding(horizontal = 4.dp, vertical = 2.dp)
              .testTag("keyframe_jump_next")
          )

          // Velocity Curves button (∿ Curves)
          Surface(
            color = CapCutSurfaceVariant.copy(alpha = 0.8f),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
              .height(28.dp)
              .clip(RoundedCornerShape(8.dp))
              .clickable { onOpenCurves() }
              .testTag("keyframe_curves_button")
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.padding(horizontal = 8.dp)
            ) {
              Text(
                text = "∿",
                color = CapCutCyan,
                fontSize = 14.sp,
                fontWeight = FontWeight.Black
              )
              Spacer(modifier = Modifier.width(3.dp))
              Text(
                text = "Curves",
                color = CapCutTextPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
              )
            }
          }
        }
      }
    }
  }
}

fun formatTimeCode(ms: Long): String {
  val totalSeconds = ms / 1000L
  val minutes = totalSeconds / 60L
  val seconds = totalSeconds % 60L
  return String.format(Locale.US, "%02d:%02d", minutes, seconds)
}
