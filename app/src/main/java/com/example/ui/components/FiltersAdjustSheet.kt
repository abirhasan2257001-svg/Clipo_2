package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Filter
import androidx.compose.material.icons.filled.Tune
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
import com.example.model.FilterType
import com.example.model.VideoClip
import com.example.ui.theme.CapCutCyan
import com.example.ui.theme.CapCutPanelBg
import com.example.ui.theme.CapCutSurface
import com.example.ui.theme.CapCutSurfaceVariant
import com.example.ui.theme.CapCutTextPrimary
import com.example.ui.theme.CapCutTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FiltersAdjustSheet(
  isFilterMode: Boolean,
  clip: VideoClip?,
  onFilterChange: (FilterType) -> Unit,
  onAdjustmentsChange: (Float, Float, Float, Float) -> Unit,
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  var brightness by remember(clip) { mutableFloatStateOf(clip?.brightness ?: 0f) }
  var contrast by remember(clip) { mutableFloatStateOf(clip?.contrast ?: 1f) }
  var saturation by remember(clip) { mutableFloatStateOf(clip?.saturation ?: 1f) }
  var vignette by remember(clip) { mutableFloatStateOf(clip?.vignette ?: 0f) }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = CapCutPanelBg,
    dragHandle = null,
    modifier = modifier.testTag("filters_adjust_sheet")
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
          Icon(
            if (isFilterMode) Icons.Default.Filter else Icons.Default.Tune,
            contentDescription = null,
            tint = CapCutCyan,
            modifier = Modifier.size(22.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = if (isFilterMode) "Color Filters & LUTs" else "Pro Color Grading",
            color = CapCutTextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
          )
        }
        IconButton(onClick = onDismiss) {
          Icon(Icons.Default.Close, contentDescription = "Close", tint = CapCutTextSecondary)
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      if (isFilterMode) {
        // Horizontal Filter Presets
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          FilterType.values().forEach { filter ->
            val isSelected = clip?.filter == filter
            Surface(
              color = if (isSelected) CapCutCyan else CapCutSurface,
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier
                .width(80.dp)
                .clip(RoundedCornerShape(10.dp))
                .clickable { onFilterChange(filter) }
            ) {
              Column(
                modifier = Modifier.padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Box(
                  modifier = Modifier
                    .size(38.dp)
                    .background(CapCutSurfaceVariant, RoundedCornerShape(8.dp)),
                  contentAlignment = Alignment.Center
                ) {
                  Text(
                    text = filter.displayName.take(1),
                    color = if (isSelected) Color.Black else CapCutCyan,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                  )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                  text = filter.displayName,
                  color = if (isSelected) Color.Black else CapCutTextPrimary,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.SemiBold
                )
              }
            }
          }
        }
      } else {
        // Adjust Sliders
        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          AdjustSliderRow(
            label = "Brightness",
            value = brightness,
            valueRange = -0.5f..0.5f,
            onValueChange = {
              brightness = it
              onAdjustmentsChange(brightness, contrast, saturation, vignette)
            }
          )
          AdjustSliderRow(
            label = "Contrast",
            value = contrast,
            valueRange = 0.5f..1.8f,
            onValueChange = {
              contrast = it
              onAdjustmentsChange(brightness, contrast, saturation, vignette)
            }
          )
          AdjustSliderRow(
            label = "Saturation",
            value = saturation,
            valueRange = 0f..2.0f,
            onValueChange = {
              saturation = it
              onAdjustmentsChange(brightness, contrast, saturation, vignette)
            }
          )
          AdjustSliderRow(
            label = "Vignette",
            value = vignette,
            valueRange = 0f..1.0f,
            onValueChange = {
              vignette = it
              onAdjustmentsChange(brightness, contrast, saturation, vignette)
            }
          )
        }
      }

      Spacer(modifier = Modifier.height(28.dp))
    }
  }
}

@Composable
private fun AdjustSliderRow(
  label: String,
  value: Float,
  valueRange: ClosedFloatingPointRange<Float>,
  onValueChange: (Float) -> Unit
) {
  Column(modifier = Modifier.fillMaxWidth()) {
    Text(label, color = CapCutTextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    Slider(
      value = value,
      onValueChange = onValueChange,
      valueRange = valueRange,
      colors = SliderDefaults.colors(
        thumbColor = CapCutCyan,
        activeTrackColor = CapCutCyan,
        inactiveTrackColor = CapCutSurfaceVariant
      )
    )
  }
}
