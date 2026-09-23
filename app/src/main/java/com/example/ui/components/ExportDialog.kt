package com.example.ui.components

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.ExportConfig
import com.example.model.ExportResolution
import com.example.ui.theme.CapCutBorder
import com.example.ui.theme.CapCutCyan
import com.example.ui.theme.CapCutDarkBg
import com.example.ui.theme.CapCutGreen
import com.example.ui.theme.CapCutPanelBg
import com.example.ui.theme.CapCutSurface
import com.example.ui.theme.CapCutSurfaceVariant
import com.example.ui.theme.CapCutTextMuted
import com.example.ui.theme.CapCutTextPrimary
import com.example.ui.theme.CapCutTextSecondary
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportDialog(
  exportConfig: ExportConfig,
  isExporting: Boolean,
  exportProgress: Float,
  exportDone: Boolean,
  exportedFilePath: String?,
  onResolutionChange: (ExportResolution) -> Unit,
  onFpsChange: (Int) -> Unit,
  onToggleHdr: () -> Unit,
  onToggleHardwareAcceleration: () -> Unit,
  onStartExport: () -> Unit,
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current

  BasicAlertDialog(
    onDismissRequest = { if (!isExporting) onDismiss() },
    modifier = modifier.testTag("export_dialog")
  ) {
    Surface(
      shape = RoundedCornerShape(16.dp),
      color = CapCutPanelBg,
      border = androidx.compose.foundation.BorderStroke(1.dp, CapCutBorder),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(
        modifier = Modifier.padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Top Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(
            text = if (isExporting) "Rendering Video..." else if (exportDone) "Export Complete!" else "Export Video (Hardware Accel)",
            color = CapCutTextPrimary,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
          )
          if (!isExporting) {
            IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
              Icon(Icons.Default.Close, contentDescription = "Close", tint = CapCutTextSecondary)
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (isExporting) {
          // Live rendering view
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(140.dp)
              .clip(RoundedCornerShape(10.dp))
              .background(Color.Black),
            contentAlignment = Alignment.Center
          ) {
            Image(
              painter = painterResource(id = R.drawable.clip_forest),
              contentDescription = "Rendering frame",
              modifier = Modifier.fillMaxSize(),
              contentScale = ContentScale.Crop,
              alpha = 0.8f
            )
            Box(
              modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f))
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = "${(exportProgress * 100).toInt()}%",
                color = CapCutCyan,
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace
              )
              Text(
                text = "Rendering at 60 FPS • MediaCodec H.264",
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
              )
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          LinearProgressIndicator(
            progress = { exportProgress },
            color = CapCutCyan,
            trackColor = CapCutSurfaceVariant,
            modifier = Modifier
              .fillMaxWidth()
              .height(8.dp)
              .clip(RoundedCornerShape(4.dp))
          )

          Spacer(modifier = Modifier.height(10.dp))

          Text(
            text = "Hardware acceleration active • GPU Shader Engine",
            color = CapCutTextSecondary,
            fontSize = 11.sp
          )
        } else if (exportDone) {
          // Export Done view
          Box(
            modifier = Modifier
              .size(64.dp)
              .clip(CircleShape)
              .background(CapCutGreen.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.CheckCircle,
              contentDescription = "Done",
              tint = CapCutGreen,
              modifier = Modifier.size(40.dp)
            )
          }

          Spacer(modifier = Modifier.height(12.dp))

          Text(
            text = "Video Saved Successfully!",
            color = CapCutTextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
          )

          Text(
            text = exportedFilePath ?: "Saved to Video Gallery",
            color = CapCutTextMuted,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(top = 4.dp)
          )

          Spacer(modifier = Modifier.height(18.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            // Share Button
            Surface(
              color = CapCutSurfaceVariant,
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier
                .weight(1f)
                .height(44.dp)
                .clip(RoundedCornerShape(10.dp))
                .clickable {
                  val sendIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, "Exported from CapCut Video Editor: 60 FPS 4K Video!")
                    type = "text/plain"
                  }
                  context.startActivity(Intent.createChooser(sendIntent, "Share Video"))
                }
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
              ) {
                Icon(Icons.Default.Share, contentDescription = "Share", tint = CapCutTextPrimary, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Share", color = CapCutTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
              }
            }

            // Done Button
            Surface(
              color = CapCutCyan,
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier
                .weight(1f)
                .height(44.dp)
                .clip(RoundedCornerShape(10.dp))
                .clickable { onDismiss() }
            ) {
              Box(contentAlignment = Alignment.Center) {
                Text("Done", color = Color.Black, fontSize = 13.sp, fontWeight = FontWeight.Bold)
              }
            }
          }
        } else {
          // Export Settings view
          Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            // Resolution Selection
            Text("Resolution:", color = CapCutTextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              ExportResolution.values().forEach { res ->
                val isSelected = exportConfig.resolution == res
                Surface(
                  color = if (isSelected) CapCutCyan else CapCutSurface,
                  shape = RoundedCornerShape(8.dp),
                  modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onResolutionChange(res) }
                ) {
                  Box(
                    modifier = Modifier.padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                  ) {
                    Text(
                      text = res.label.split(" ").first(),
                      color = if (isSelected) Color.Black else CapCutTextPrimary,
                      fontSize = 11.sp,
                      fontWeight = FontWeight.Bold
                    )
                  }
                }
              }
            }

            // Frame Rate (FPS)
            Text("Frame Rate:", color = CapCutTextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              listOf(24, 30, 50, 60).forEach { fps ->
                val isSelected = exportConfig.fps == fps
                Surface(
                  color = if (isSelected) CapCutCyan else CapCutSurface,
                  shape = RoundedCornerShape(8.dp),
                  modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onFpsChange(fps) }
                ) {
                  Box(
                    modifier = Modifier.padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                  ) {
                    Text(
                      text = "${fps} FPS",
                      color = if (isSelected) Color.Black else CapCutTextPrimary,
                      fontSize = 11.sp,
                      fontWeight = FontWeight.Bold
                    )
                  }
                }
              }
            }

            // Hardware Acceleration Toggle
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .background(CapCutSurface, RoundedCornerShape(8.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Column {
                Text("Hardware Acceleration", color = CapCutTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text("60 FPS GPU Rendering", color = CapCutTextMuted, fontSize = 10.sp)
              }
              Switch(
                checked = exportConfig.hardwareAcceleration,
                onCheckedChange = { onToggleHardwareAcceleration() },
                colors = SwitchDefaults.colors(
                  checkedThumbColor = CapCutCyan,
                  checkedTrackColor = CapCutCyan.copy(alpha = 0.5f)
                )
              )
            }

            // Smart HDR Toggle
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .background(CapCutSurface, RoundedCornerShape(8.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Column {
                Text("Smart HDR Vivid Colors", color = CapCutTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text("Enhanced dynamic range", color = CapCutTextMuted, fontSize = 10.sp)
              }
              Switch(
                checked = exportConfig.smartHdr,
                onCheckedChange = { onToggleHdr() },
                colors = SwitchDefaults.colors(
                  checkedThumbColor = CapCutCyan,
                  checkedTrackColor = CapCutCyan.copy(alpha = 0.5f)
                )
              )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Start Export Button
            Surface(
              color = CapCutCyan,
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .clickable { onStartExport() }
                .testTag("dialog_start_export_button")
            ) {
              Box(contentAlignment = Alignment.Center) {
                Text(
                  text = "Start High-End 60 FPS Export",
                  color = Color.Black,
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }
        }
      }
    }
  }
}
