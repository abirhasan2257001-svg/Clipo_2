package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import com.example.model.ExportResolution
import com.example.ui.theme.CapCutCyan
import com.example.ui.theme.CapCutDarkBg
import com.example.ui.theme.CapCutSurfaceVariant
import com.example.ui.theme.CapCutTextPrimary

@Composable
fun TopEditorBar(
  currentResolution: ExportResolution,
  showQualityMenu: Boolean,
  onQualityMenuToggle: (Boolean) -> Unit,
  onSelectResolution: (ExportResolution) -> Unit,
  onExportClick: () -> Unit,
  onCloseClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .height(56.dp)
      .background(CapCutDarkBg)
      .padding(horizontal = 12.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    // Left: Close & Search
    Row(verticalAlignment = Alignment.CenterVertically) {
      IconButton(
        onClick = onCloseClick,
        modifier = Modifier
          .size(40.dp)
          .testTag("top_close_button")
      ) {
        Icon(
          imageVector = Icons.Default.Close,
          contentDescription = "Close",
          tint = CapCutTextPrimary,
          modifier = Modifier.size(24.dp)
        )
      }

      Spacer(modifier = Modifier.width(6.dp))

      IconButton(
        onClick = {},
        modifier = Modifier
          .size(40.dp)
          .testTag("top_search_button")
      ) {
        Icon(
          imageVector = Icons.Default.Search,
          contentDescription = "Search",
          tint = CapCutTextPrimary,
          modifier = Modifier.size(24.dp)
        )
      }
    }

    // Right: AI UHD Badge and Export Button
    Row(verticalAlignment = Alignment.CenterVertically) {
      // Quality Badge (e.g. "AI UHD ▾")
      Box {
        Surface(
          color = CapCutSurfaceVariant,
          shape = RoundedCornerShape(16.dp),
          modifier = Modifier
            .height(34.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onQualityMenuToggle(true) }
            .testTag("quality_selector_badge")
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp)
          ) {
            Text(
              text = currentResolution.label.replace(" (4K)", "").replace(" FHD", "").replace(" HD", "").replace(" QHD", ""),
              color = CapCutTextPrimary,
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold
            )
            Icon(
              imageVector = Icons.Default.ArrowDropDown,
              contentDescription = "Dropdown",
              tint = CapCutTextPrimary,
              modifier = Modifier.size(18.dp)
            )
          }
        }

        DropdownMenu(
          expanded = showQualityMenu,
          onDismissRequest = { onQualityMenuToggle(false) },
          modifier = Modifier.background(CapCutSurfaceVariant)
        ) {
          ExportResolution.values().forEach { res ->
            DropdownMenuItem(
              text = {
                Text(
                  text = res.label,
                  color = if (res == currentResolution) CapCutCyan else CapCutTextPrimary,
                  fontWeight = if (res == currentResolution) FontWeight.Bold else FontWeight.Normal,
                  fontSize = 13.sp
                )
              },
              onClick = {
                onSelectResolution(res)
                onQualityMenuToggle(false)
              }
            )
          }
        }
      }

      Spacer(modifier = Modifier.width(10.dp))

      // Export Button (Cyan Pill with Play Icon / "Export" text exactly like reference)
      Surface(
        color = CapCutCyan,
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier
          .height(34.dp)
          .clip(RoundedCornerShape(18.dp))
          .clickable { onExportClick() }
          .testTag("top_export_button")
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.padding(horizontal = 14.dp)
        ) {
          // Play badge icon with AD badge
          Box(
            modifier = Modifier
              .size(16.dp)
              .clip(RoundedCornerShape(3.dp))
              .background(Color.Black.copy(alpha = 0.25f)),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "▶",
              color = Color.Black,
              fontSize = 9.sp,
              fontWeight = FontWeight.Black
            )
          }
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Export",
            color = Color.Black,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }
  }
}
