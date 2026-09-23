package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AspectRatio
import com.example.ui.theme.CapCutCyan
import com.example.ui.theme.CapCutPanelBg
import com.example.ui.theme.CapCutSurface
import com.example.ui.theme.CapCutSurfaceVariant
import com.example.ui.theme.CapCutTextPrimary
import com.example.ui.theme.CapCutTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CanvasRatioSheet(
  currentRatio: AspectRatio,
  onSelectRatio: (AspectRatio) -> Unit,
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = CapCutPanelBg,
    dragHandle = null,
    modifier = modifier.testTag("canvas_ratio_sheet")
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
          Icon(Icons.Default.AspectRatio, contentDescription = null, tint = CapCutCyan, modifier = Modifier.size(22.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text("Canvas Aspect Ratio", color = CapCutTextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
        IconButton(onClick = onDismiss) {
          Icon(Icons.Default.Close, contentDescription = "Close", tint = CapCutTextSecondary)
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        AspectRatio.values().forEach { ratio ->
          val isSelected = ratio == currentRatio
          Surface(
            color = if (isSelected) CapCutCyan else CapCutSurface,
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
              .weight(1f)
              .padding(horizontal = 3.dp)
              .clip(RoundedCornerShape(10.dp))
              .clickable {
                onSelectRatio(ratio)
                onDismiss()
              }
          ) {
            Column(
              modifier = Modifier.padding(vertical = 12.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Box(
                modifier = Modifier
                  .size(34.dp)
                  .background(CapCutSurfaceVariant, RoundedCornerShape(6.dp)),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = "▭",
                  color = if (isSelected) Color.Black else CapCutCyan,
                  fontSize = 18.sp,
                  fontWeight = FontWeight.Bold
                )
              }
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = ratio.label,
                color = if (isSelected) Color.Black else CapCutTextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(28.dp))
    }
  }
}
