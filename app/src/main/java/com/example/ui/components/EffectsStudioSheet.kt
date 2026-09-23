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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
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
import com.example.model.EffectType
import com.example.ui.theme.CapCutCyan
import com.example.ui.theme.CapCutPanelBg
import com.example.ui.theme.CapCutSurface
import com.example.ui.theme.CapCutSurfaceVariant
import com.example.ui.theme.CapCutTextPrimary
import com.example.ui.theme.CapCutTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EffectsStudioSheet(
  onAddEffect: (EffectType) -> Unit,
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = CapCutPanelBg,
    dragHandle = null,
    modifier = modifier.testTag("effects_studio_sheet")
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
          Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = CapCutCyan, modifier = Modifier.size(22.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text("Video Effects Library", color = CapCutTextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
        IconButton(onClick = onDismiss) {
          Icon(Icons.Default.Close, contentDescription = "Close", tint = CapCutTextSecondary)
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(260.dp)
      ) {
        items(EffectType.values()) { effectType ->
          Surface(
            color = CapCutSurface,
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
              .clip(RoundedCornerShape(10.dp))
              .clickable {
                onAddEffect(effectType)
                onDismiss()
              }
          ) {
            Column(
              modifier = Modifier.padding(14.dp),
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.Center
            ) {
              Box(
                modifier = Modifier
                  .size(44.dp)
                  .background(CapCutSurfaceVariant, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
              ) {
                Text(effectType.icon, color = CapCutCyan, fontSize = 20.sp, fontWeight = FontWeight.Black)
              }
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = effectType.displayName,
                color = CapCutTextPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))
    }
  }
}
