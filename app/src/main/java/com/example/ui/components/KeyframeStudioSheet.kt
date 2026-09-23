package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CurveType
import com.example.model.Keyframe
import com.example.model.TransformState
import com.example.model.VideoClip
import com.example.ui.theme.CapCutBorder
import com.example.ui.theme.CapCutCyan
import com.example.ui.theme.CapCutDarkBg
import com.example.ui.theme.CapCutPanelBg
import com.example.ui.theme.CapCutSurface
import com.example.ui.theme.CapCutSurfaceVariant
import com.example.ui.theme.CapCutTextMuted
import com.example.ui.theme.CapCutTextPrimary
import com.example.ui.theme.CapCutTextSecondary
import com.example.ui.theme.CapCutYellow
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KeyframeStudioSheet(
  selectedClip: VideoClip?,
  currentTransform: TransformState,
  activeKeyframe: Keyframe?,
  onDismiss: () -> Unit,
  onUpdateTransform: (Float?, Float?, Float?, Float?, Float?, CurveType?) -> Unit,
  onToggleKeyframe: () -> Unit,
  onJumpPrevKeyframe: () -> Unit,
  onJumpNextKeyframe: () -> Unit,
  modifier: Modifier = Modifier
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  var selectedTab by remember { mutableStateOf(0) } // 0: Transform Sliders, 1: Easing Curves

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = CapCutPanelBg,
    dragHandle = null,
    modifier = modifier.testTag("keyframe_studio_sheet")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
      // Header: Title, Active Keyframe badge, and Close
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text("◇", color = CapCutYellow, fontSize = 20.sp, fontWeight = FontWeight.Black)
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Keyframe Animation Studio",
            color = CapCutTextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
          )
        }

        IconButton(onClick = onDismiss) {
          Icon(Icons.Default.Close, contentDescription = "Close", tint = CapCutTextSecondary)
        }
      }

      // Quick Keyframe Bar: Prev, Add/Remove, Next
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(CapCutSurface, RoundedCornerShape(10.dp))
          .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
      ) {
        // Prev Keyframe
        Surface(
          color = CapCutSurfaceVariant,
          shape = RoundedCornerShape(6.dp),
          modifier = Modifier
            .clickable { onJumpPrevKeyframe() }
            .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
          Text("◁◇ Prev", color = CapCutTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }

        // Add/Remove Keyframe
        Surface(
          color = if (activeKeyframe != null) CapCutYellow else CapCutCyan,
          shape = RoundedCornerShape(6.dp),
          modifier = Modifier
            .clickable { onToggleKeyframe() }
            .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
          Text(
            text = if (activeKeyframe != null) "◇ Delete Keyframe" else "◇ Add Keyframe",
            color = Color.Black,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
          )
        }

        // Next Keyframe
        Surface(
          color = CapCutSurfaceVariant,
          shape = RoundedCornerShape(6.dp),
          modifier = Modifier
            .clickable { onJumpNextKeyframe() }
            .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
          Text("Next ◇▷", color = CapCutTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Tab selector: Controls vs Easing Curves
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
            text = "Transform Controls",
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
            text = "Velocity Curves (∿)",
            color = if (selectedTab == 1) Color.Black else CapCutTextSecondary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      if (selectedTab == 0) {
        // Transform Sliders
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          // Position X
          KeyframeSliderRow(
            label = "Position X",
            value = currentTransform.posX,
            valueRange = -300f..300f,
            format = "%.0f px",
            onValueChange = { onUpdateTransform(it, null, null, null, null, null) },
            onReset = { onUpdateTransform(0f, null, null, null, null, null) }
          )

          // Position Y
          KeyframeSliderRow(
            label = "Position Y",
            value = currentTransform.posY,
            valueRange = -300f..300f,
            format = "%.0f px",
            onValueChange = { onUpdateTransform(null, it, null, null, null, null) },
            onReset = { onUpdateTransform(null, 0f, null, null, null, null) }
          )

          // Scale / Zoom
          KeyframeSliderRow(
            label = "Scale (Zoom)",
            value = currentTransform.scale,
            valueRange = 0.2f..3.0f,
            format = "%.2fx",
            onValueChange = { onUpdateTransform(null, null, it, null, null, null) },
            onReset = { onUpdateTransform(null, null, 1.0f, null, null, null) }
          )

          // Rotation
          KeyframeSliderRow(
            label = "Rotation",
            value = currentTransform.rotation,
            valueRange = -180f..180f,
            format = "%.0f°",
            onValueChange = { onUpdateTransform(null, null, null, it, null, null) },
            onReset = { onUpdateTransform(null, null, null, 0f, null, null) }
          )

          // Opacity
          KeyframeSliderRow(
            label = "Opacity",
            value = currentTransform.opacity,
            valueRange = 0f..1f,
            format = "%.0f%%",
            displayMultiplier = 100f,
            onValueChange = { onUpdateTransform(null, null, null, null, it, null) },
            onReset = { onUpdateTransform(null, null, null, null, 1.0f, null) }
          )
        }
      } else {
        // Easing Curves Studio
        val currentCurve = activeKeyframe?.curveType ?: CurveType.EASE_IN_OUT

        Column(modifier = Modifier.fillMaxWidth()) {
          // Interactive Curve Preview Canvas
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(130.dp)
              .background(CapCutDarkBg, RoundedCornerShape(10.dp))
              .border(1.dp, CapCutBorder, RoundedCornerShape(10.dp))
              .padding(12.dp)
          ) {
            CurveVisualizerCanvas(curveType = currentCurve)
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Curve Preset Pills
          Text(
            text = "Select Motion Curve:",
            color = CapCutTextSecondary,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
          )

          Spacer(modifier = Modifier.height(8.dp))

          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            CurveType.values().forEach { curve ->
              val isSelected = curve == currentCurve
              Surface(
                color = if (isSelected) CapCutCyan else CapCutSurfaceVariant,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .clickable { onUpdateTransform(null, null, null, null, null, curve) }
              ) {
                Column(
                  modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                  horizontalAlignment = Alignment.CenterHorizontally
                ) {
                  Text(
                    text = curve.displayName,
                    color = if (isSelected) Color.Black else CapCutTextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                  )
                  Text(
                    text = curve.description,
                    color = if (isSelected) Color.Black.copy(alpha = 0.7f) else CapCutTextMuted,
                    fontSize = 10.sp
                  )
                }
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}

@Composable
private fun KeyframeSliderRow(
  label: String,
  value: Float,
  valueRange: ClosedFloatingPointRange<Float>,
  format: String,
  displayMultiplier: Float = 1f,
  onValueChange: (Float) -> Unit,
  onReset: () -> Unit
) {
  Column(modifier = Modifier.fillMaxWidth()) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = label,
        color = CapCutTextPrimary,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold
      )

      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
          text = String.format(Locale.US, format, value * displayMultiplier),
          color = CapCutCyan,
          fontSize = 13.sp,
          fontFamily = FontFamily.Monospace,
          fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.width(8.dp))
        Icon(
          imageVector = Icons.Default.Refresh,
          contentDescription = "Reset",
          tint = CapCutTextMuted,
          modifier = Modifier
            .size(16.dp)
            .clickable { onReset() }
        )
      }
    }

    Slider(
      value = value,
      onValueChange = onValueChange,
      valueRange = valueRange,
      colors = SliderDefaults.colors(
        thumbColor = CapCutCyan,
        activeTrackColor = CapCutCyan,
        inactiveTrackColor = CapCutSurfaceVariant
      ),
      modifier = Modifier.height(30.dp)
    )
  }
}

@Composable
private fun CurveVisualizerCanvas(curveType: CurveType) {
  Canvas(modifier = Modifier.fillMaxSize()) {
    val w = size.width
    val h = size.height

    // Draw coordinate grid lines
    val gridColor = Color(0xFF262835)
    drawLine(gridColor, Offset(0f, 0f), Offset(w, 0f), strokeWidth = 1f)
    drawLine(gridColor, Offset(0f, h / 2f), Offset(w, h / 2f), strokeWidth = 1f)
    drawLine(gridColor, Offset(0f, h), Offset(w, h), strokeWidth = 1f)
    drawLine(gridColor, Offset(w / 2f, 0f), Offset(w / 2f, h), strokeWidth = 1f)

    // Plot easing curve
    val path = Path()
    val steps = 60
    for (i in 0..steps) {
      val t = i.toFloat() / steps
      val eased = curveType.evaluate(t)
      val x = t * w
      val y = h - (eased * (h - 20f) + 10f)

      if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }

    drawPath(
      path = path,
      color = CapCutCyan,
      style = Stroke(width = 3.5f, cap = StrokeCap.Round)
    )

    // Start and end control points
    drawCircle(CapCutYellow, radius = 5f, center = Offset(0f, h - 10f))
    val endY = h - (curveType.evaluate(1f) * (h - 20f) + 10f)
    drawCircle(CapCutYellow, radius = 5f, center = Offset(w, endY))
  }
}
