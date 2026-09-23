package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Animation
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Filter
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.EditorMode
import com.example.ui.theme.CapCutCyan
import com.example.ui.theme.CapCutDarkBg
import com.example.ui.theme.CapCutRed
import com.example.ui.theme.CapCutTextPrimary
import com.example.ui.theme.CapCutTextSecondary
import com.example.ui.theme.CapCutYellow

@Composable
fun BottomToolbar(
  currentMode: EditorMode,
  onModeSelect: (EditorMode) -> Unit,
  onSplitClip: () -> Unit,
  onDeleteClip: () -> Unit,
  onDuplicateClip: () -> Unit,
  onOpenKeyframeStudio: () -> Unit,
  onOpenSpeedStudio: () -> Unit,
  modifier: Modifier = Modifier
) {
  AnimatedContent(
    targetState = currentMode,
    transitionSpec = { fadeIn() togetherWith fadeOut() },
    label = "BottomToolbarTransition",
    modifier = modifier
      .fillMaxWidth()
      .height(64.dp)
      .background(CapCutDarkBg)
  ) { mode ->
    when (mode) {
      EditorMode.EDIT_CLIP -> {
        // Clip Edit Sub-mode Toolbar (CapCut style)
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 8.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
          // Back to Main
          ToolbarItem(
            icon = Icons.AutoMirrored.Filled.ArrowBack,
            label = "Back",
            onClick = { onModeSelect(EditorMode.MAIN) },
            testTag = "toolbar_back"
          )

          // Split
          ToolbarItem(
            icon = Icons.Default.ContentCut,
            label = "Split",
            onClick = onSplitClip,
            tint = CapCutCyan,
            testTag = "toolbar_split"
          )

          // Speed
          ToolbarItem(
            icon = Icons.Default.Speed,
            label = "Speed",
            onClick = onOpenSpeedStudio,
            testTag = "toolbar_speed"
          )

          // Keyframe Studio
          ToolbarItem(
            icon = Icons.Default.Diamond,
            label = "Keyframe",
            onClick = onOpenKeyframeStudio,
            tint = CapCutYellow,
            testTag = "toolbar_keyframe_studio"
          )

          // Animation
          ToolbarItem(
            icon = Icons.Default.Animation,
            label = "Animation",
            onClick = { onModeSelect(EditorMode.EFFECTS) },
            testTag = "toolbar_animation"
          )

          // Filters
          ToolbarItem(
            icon = Icons.Default.Filter,
            label = "Filters",
            onClick = { onModeSelect(EditorMode.FILTERS) },
            testTag = "toolbar_filters"
          )

          // Adjust
          ToolbarItem(
            icon = Icons.Default.Tune,
            label = "Adjust",
            onClick = { onModeSelect(EditorMode.ADJUST) },
            testTag = "toolbar_adjust"
          )

          // Duplicate
          ToolbarItem(
            icon = Icons.Default.ContentCopy,
            label = "Duplicate",
            onClick = onDuplicateClip,
            testTag = "toolbar_duplicate"
          )

          // Delete
          ToolbarItem(
            icon = Icons.Default.Delete,
            label = "Delete",
            onClick = onDeleteClip,
            tint = CapCutRed,
            testTag = "toolbar_delete"
          )
        }
      }

      else -> {
        // Main CapCut Bottom Navigation Bar (Edit, Audio, Text, Effects, Overlay, Captions, etc.)
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 8.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
          ToolbarItem(
            icon = Icons.Default.ContentCut,
            label = "Edit",
            selected = mode == EditorMode.EDIT_CLIP,
            onClick = { onModeSelect(EditorMode.EDIT_CLIP) },
            testTag = "main_nav_edit"
          )

          ToolbarItem(
            icon = Icons.Default.MusicNote,
            label = "Audio",
            selected = mode == EditorMode.AUDIO,
            onClick = { onModeSelect(EditorMode.AUDIO) },
            testTag = "main_nav_audio"
          )

          ToolbarItem(
            icon = Icons.Default.TextFields,
            label = "Text",
            selected = mode == EditorMode.TEXT,
            onClick = { onModeSelect(EditorMode.TEXT) },
            testTag = "main_nav_text"
          )

          ToolbarItem(
            icon = Icons.Default.AutoAwesome,
            label = "Effects",
            selected = mode == EditorMode.EFFECTS,
            onClick = { onModeSelect(EditorMode.EFFECTS) },
            testTag = "main_nav_effects"
          )

          ToolbarItem(
            icon = Icons.Default.PhotoLibrary,
            label = "Overlay",
            selected = mode == EditorMode.OVERLAY,
            onClick = { onModeSelect(EditorMode.OVERLAY) },
            testTag = "main_nav_overlay"
          )

          ToolbarItem(
            icon = Icons.Default.Subtitles,
            label = "Captions",
            selected = mode == EditorMode.CAPTIONS,
            onClick = { onModeSelect(EditorMode.CAPTIONS) },
            testTag = "main_nav_captions"
          )

          ToolbarItem(
            icon = Icons.Default.Filter,
            label = "Filters",
            selected = mode == EditorMode.FILTERS,
            onClick = { onModeSelect(EditorMode.FILTERS) },
            testTag = "main_nav_filters"
          )

          ToolbarItem(
            icon = Icons.Default.Tune,
            label = "Adjust",
            selected = mode == EditorMode.ADJUST,
            onClick = { onModeSelect(EditorMode.ADJUST) },
            testTag = "main_nav_adjust"
          )
        }
      }
    }
  }
}

@Composable
private fun ToolbarItem(
  icon: ImageVector,
  label: String,
  onClick: () -> Unit,
  selected: Boolean = false,
  tint: Color? = null,
  testTag: String = ""
) {
  val itemTint = tint ?: if (selected) CapCutCyan else CapCutTextPrimary

  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center,
    modifier = Modifier
      .clip(RoundedCornerShape(8.dp))
      .clickable { onClick() }
      .padding(horizontal = 8.dp, vertical = 6.dp)
      .testTag(testTag)
  ) {
    Icon(
      imageVector = icon,
      contentDescription = label,
      tint = itemTint,
      modifier = Modifier.size(24.dp)
    )
    Spacer(modifier = Modifier.height(3.dp))
    Text(
      text = label,
      color = if (selected) CapCutCyan else CapCutTextSecondary,
      fontSize = 11.sp,
      fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
    )
  }
}
