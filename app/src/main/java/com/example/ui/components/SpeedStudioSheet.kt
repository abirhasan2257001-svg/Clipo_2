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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CapCutCyan
import com.example.ui.theme.CapCutPanelBg
import com.example.ui.theme.CapCutSurface
import com.example.ui.theme.CapCutSurfaceVariant
import com.example.ui.theme.CapCutTextPrimary
import com.example.ui.theme.CapCutTextSecondary
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpeedStudioSheet(
  currentSpeed: Float,
  onSpeedChange: (Float) -> Unit,
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  var speedValue by remember { mutableFloatStateOf(currentSpeed) }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = CapCutPanelBg,
    dragHandle = null,
    modifier = modifier.testTag("speed_studio_sheet")
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
          Icon(Icons.Default.Speed, contentDescription = null, tint = CapCutCyan, modifier = Modifier.size(22.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text("Speed Control", color = CapCutTextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
        IconButton(onClick = onDismiss) {
          Icon(Icons.Default.Close, contentDescription = "Close", tint = CapCutTextSecondary)
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Current Speed Display
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .background(CapCutSurface, RoundedCornerShape(10.dp))
          .padding(16.dp),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = String.format(Locale.US, "%.1fx", speedValue),
          color = CapCutCyan,
          fontSize = 32.sp,
          fontWeight = FontWeight.Black,
          fontFamily = FontFamily.Monospace
        )
      }

      Spacer(modifier = Modifier.height(14.dp))

      Slider(
        value = speedValue,
        onValueChange = {
          speedValue = it
          onSpeedChange(it)
        },
        valueRange = 0.2f..5.0f,
        colors = SliderDefaults.colors(
          thumbColor = CapCutCyan,
          activeTrackColor = CapCutCyan,
          inactiveTrackColor = CapCutSurfaceVariant
        )
      )

      Spacer(modifier = Modifier.height(12.dp))

      // Preset speed buttons (0.5x, 1.0x, 1.5x, 2.0x, 3.0x)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        listOf(0.5f, 1.0f, 1.5f, 2.0f, 3.0f).forEach { speedPreset ->
          val isSelected = kotlin.math.abs(speedValue - speedPreset) < 0.05f
          Surface(
            color = if (isSelected) CapCutCyan else CapCutSurfaceVariant,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .clickable {
                speedValue = speedPreset
                onSpeedChange(speedPreset)
              }
          ) {
            Text(
              text = "${speedPreset}x",
              color = if (isSelected) Color.Black else CapCutTextPrimary,
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(28.dp))
    }
  }
}
