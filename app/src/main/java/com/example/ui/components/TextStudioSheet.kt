package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
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
import com.example.ui.theme.CapCutCyan
import com.example.ui.theme.CapCutPanelBg
import com.example.ui.theme.CapCutSurface
import com.example.ui.theme.CapCutSurfaceVariant
import com.example.ui.theme.CapCutTextMuted
import com.example.ui.theme.CapCutTextPrimary
import com.example.ui.theme.CapCutTextSecondary
import com.example.ui.theme.CapCutYellow

private val StylePresets = listOf("Bold Pop", "Neon Glow", "Retro Box", "Cinema Subtitle", "Cyber Tag")
private val PaletteColors = listOf(0xFFFFFFFF, 0xFFFFD166, 0xFF00E5FF, 0xFFFF4757, 0xFF06D6A0, 0xFF8338EC)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextStudioSheet(
  onAddText: (String, String, Long) -> Unit,
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  var textInput by remember { mutableStateOf("STICKMAN ADVENTURE") }
  var selectedStyle by remember { mutableStateOf("Bold Pop") }
  var selectedColor by remember { mutableLongStateOf(0xFFFFD166) }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = CapCutPanelBg,
    dragHandle = null,
    modifier = modifier.testTag("text_studio_sheet")
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
          Icon(Icons.Default.TextFields, contentDescription = null, tint = CapCutYellow, modifier = Modifier.size(22.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text("Add Text & Titles", color = CapCutTextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
        IconButton(onClick = onDismiss) {
          Icon(Icons.Default.Close, contentDescription = "Close", tint = CapCutTextSecondary)
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Input Field
      OutlinedTextField(
        value = textInput,
        onValueChange = { textInput = it },
        placeholder = { Text("Enter text...", color = CapCutTextMuted) },
        colors = TextFieldDefaults.colors(
          focusedContainerColor = CapCutSurface,
          unfocusedContainerColor = CapCutSurface,
          focusedTextColor = Color(selectedColor),
          unfocusedTextColor = Color(selectedColor),
          focusedIndicatorColor = CapCutCyan,
          unfocusedIndicatorColor = CapCutSurfaceVariant
        ),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("text_input_field")
      )

      Spacer(modifier = Modifier.height(14.dp))

      // Color Palette
      Text("Color:", color = CapCutTextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
      Spacer(modifier = Modifier.height(8.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        PaletteColors.forEach { colorHex ->
          val isSelected = selectedColor == colorHex
          Box(
            modifier = Modifier
              .size(32.dp)
              .clip(CircleShape)
              .background(Color(colorHex))
              .border(
                width = if (isSelected) 2.5.dp else 1.dp,
                color = if (isSelected) CapCutCyan else Color.Transparent,
                shape = CircleShape
              )
              .clickable { selectedColor = colorHex },
            contentAlignment = Alignment.Center
          ) {
            if (isSelected) {
              Icon(Icons.Default.Check, contentDescription = null, tint = if (colorHex == 0xFFFFFFFF) Color.Black else Color.White, modifier = Modifier.size(16.dp))
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Style presets
      Text("Style Preset:", color = CapCutTextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
      Spacer(modifier = Modifier.height(8.dp))
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        StylePresets.forEach { style ->
          val isSelected = style == selectedStyle
          Surface(
            color = if (isSelected) CapCutYellow else CapCutSurfaceVariant,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .clickable { selectedStyle = style }
          ) {
            Text(
              text = style,
              color = if (isSelected) Color.Black else CapCutTextPrimary,
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Confirm Add Button
      Surface(
        color = CapCutCyan,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(46.dp)
          .clip(RoundedCornerShape(12.dp))
          .clickable {
            if (textInput.isNotBlank()) {
              onAddText(textInput, selectedStyle, selectedColor)
              onDismiss()
            }
          }
          .testTag("confirm_add_text_button")
      ) {
        Box(contentAlignment = Alignment.Center) {
          Text("Apply Text to Video", color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
      }

      Spacer(modifier = Modifier.height(20.dp))
    }
  }
}
