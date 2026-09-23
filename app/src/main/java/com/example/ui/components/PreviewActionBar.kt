package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CapCutCyan
import com.example.ui.theme.CapCutDarkBg
import com.example.ui.theme.CapCutSurfaceVariant
import com.example.ui.theme.CapCutTextMuted
import com.example.ui.theme.CapCutTextPrimary
import com.example.ui.theme.CapCutTextSecondary

@Composable
fun PreviewActionBar(
  isPlaying: Boolean,
  isFullscreen: Boolean,
  compareMode: Boolean,
  canUndo: Boolean,
  canRedo: Boolean,
  onPlayPauseToggle: () -> Unit,
  onFullscreenToggle: () -> Unit,
  onCompareToggle: () -> Unit,
  onUndo: () -> Unit,
  onRedo: () -> Unit,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .height(44.dp)
      .background(CapCutDarkBg)
      .padding(horizontal = 16.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    // Left: Fullscreen expand button
    IconButton(
      onClick = onFullscreenToggle,
      modifier = Modifier
        .size(36.dp)
        .testTag("preview_fullscreen_button")
    ) {
      Icon(
        imageVector = if (isFullscreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
        contentDescription = "Expand Fullscreen",
        tint = CapCutTextPrimary,
        modifier = Modifier.size(24.dp)
      )
    }

    // Center: Play / Pause button
    IconButton(
      onClick = onPlayPauseToggle,
      modifier = Modifier
        .size(40.dp)
        .testTag("preview_play_pause_button")
    ) {
      Icon(
        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
        contentDescription = if (isPlaying) "Pause" else "Play",
        tint = CapCutTextPrimary,
        modifier = Modifier.size(28.dp)
      )
    }

    // Right: Compare toggle + Undo + Redo
    Row(
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Compare Mode Toggle Button (e.g. ⧉ OFF / ON)
      Surface(
        color = if (compareMode) CapCutCyan.copy(alpha = 0.2f) else CapCutSurfaceVariant.copy(alpha = 0.6f),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
          .height(26.dp)
          .clip(RoundedCornerShape(12.dp))
          .clickable { onCompareToggle() }
          .testTag("preview_compare_toggle")
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.padding(horizontal = 8.dp)
        ) {
          Text(
            text = "⧉",
            color = if (compareMode) CapCutCyan else CapCutTextSecondary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = if (compareMode) "ON" else "OFF",
            color = if (compareMode) CapCutCyan else CapCutTextSecondary,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }

      Spacer(modifier = Modifier.width(12.dp))

      // Undo button
      IconButton(
        onClick = onUndo,
        enabled = canUndo,
        modifier = Modifier
          .size(32.dp)
          .testTag("preview_undo_button")
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.Undo,
          contentDescription = "Undo",
          tint = if (canUndo) CapCutTextPrimary else CapCutTextMuted,
          modifier = Modifier.size(20.dp)
        )
      }

      Spacer(modifier = Modifier.width(4.dp))

      // Redo button
      IconButton(
        onClick = onRedo,
        enabled = canRedo,
        modifier = Modifier
          .size(32.dp)
          .testTag("preview_redo_button")
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.Redo,
          contentDescription = "Redo",
          tint = if (canRedo) CapCutTextPrimary else CapCutTextMuted,
          modifier = Modifier.size(20.dp)
        )
      }
    }
  }
}
