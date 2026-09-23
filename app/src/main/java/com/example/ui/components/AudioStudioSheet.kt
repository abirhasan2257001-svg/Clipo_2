package com.example.ui.components

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AudioClip
import com.example.ui.theme.CapCutCyan
import com.example.ui.theme.CapCutPanelBg
import com.example.ui.theme.CapCutSurface
import com.example.ui.theme.CapCutSurfaceVariant
import com.example.ui.theme.CapCutTextMuted
import com.example.ui.theme.CapCutTextPrimary
import com.example.ui.theme.CapCutTextSecondary

private val SoundEffectsList = listOf(
  Triple("Whoosh Cinematic", "00:03", "FX"),
  Triple("Camera Shutter Click", "00:01", "FX"),
  Triple("Glitch Hit & Swish", "00:02", "FX"),
  Triple("Pop Notification", "00:01", "FX"),
  Triple("Cinematic Boom", "00:04", "FX")
)

private val BgmTracksList = listOf(
  Triple("Chill Lo-Fi Summer Beat", "01:20", "BGM"),
  Triple("Cyberpunk Dynamic Synth", "02:10", "BGM"),
  Triple("Acoustic Travel Melody", "01:45", "BGM"),
  Triple("Epic Orchestral Drop", "02:30", "BGM")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioStudioSheet(
  onAddAudio: (String, Long, Boolean, String) -> Unit,
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  var selectedTab by remember { mutableStateOf(0) } // 0: Sound FX, 1: Music (BGM)

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = CapCutPanelBg,
    dragHandle = null,
    modifier = modifier.testTag("audio_studio_sheet")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.MusicNote, contentDescription = null, tint = CapCutCyan, modifier = Modifier.size(22.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text("Audio & Sound Studio", color = CapCutTextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
        IconButton(onClick = onDismiss) {
          Icon(Icons.Default.Close, contentDescription = "Close", tint = CapCutTextSecondary)
        }
      }

      // Tabs: Sound Effects vs BGM
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(CapCutSurfaceVariant, RoundedCornerShape(8.dp))
          .padding(4.dp)
      ) {
        Box(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(6.dp))
            .background(if (selectedTab == 0) CapCutCyan else Color.Transparent)
            .clickable { selectedTab = 0 }
            .padding(vertical = 8.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "Sound Effects",
            color = if (selectedTab == 0) Color.Black else CapCutTextSecondary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
          )
        }

        Box(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(6.dp))
            .background(if (selectedTab == 1) CapCutCyan else Color.Transparent)
            .clickable { selectedTab = 1 }
            .padding(vertical = 8.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "Music Tracks (BGM)",
            color = if (selectedTab == 1) Color.Black else CapCutTextSecondary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      val activeList = if (selectedTab == 0) SoundEffectsList else BgmTracksList

      LazyColumn(
        modifier = Modifier
          .fillMaxWidth()
          .height(280.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        items(activeList) { (title, duration, type) ->
          Surface(
            color = CapCutSurface,
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(36.dp)
                    .background(CapCutSurfaceVariant, RoundedCornerShape(8.dp)),
                  contentAlignment = Alignment.Center
                ) {
                  Text(
                    text = if (type == "BGM") "♪" else "⚡",
                    color = CapCutCyan,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                  )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                  Text(title, color = CapCutTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                  Text(duration, color = CapCutTextMuted, fontSize = 11.sp)
                }
              }

              Surface(
                color = CapCutCyan,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                  .clip(RoundedCornerShape(16.dp))
                  .clickable {
                    val durationMs = if (type == "BGM") 60000L else 4000L
                    onAddAudio(title, durationMs, type == "BGM", type)
                    onDismiss()
                  }
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                  Icon(Icons.Default.Add, contentDescription = "Use", tint = Color.Black, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("Use", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))
    }
  }
}
