package com.example.ui.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.CapCutBorder
import com.example.ui.theme.CapCutCyan
import com.example.ui.theme.CapCutPanelBg
import com.example.ui.theme.CapCutSurface
import com.example.ui.theme.CapCutSurfaceVariant
import com.example.ui.theme.CapCutTextPrimary
import com.example.ui.theme.CapCutTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaPickerDialog(
  onAddPresetSunset: () -> Unit,
  onAddUserMedia: (String, String) -> Unit,
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier
) {
  // Safe modern Android Photo Picker
  val photoPickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia()
  ) { uri ->
    if (uri != null) {
      onAddUserMedia(uri.toString(), "Imported Clip")
      onDismiss()
    }
  }

  BasicAlertDialog(
    onDismissRequest = onDismiss,
    modifier = modifier.testTag("media_picker_dialog")
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
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(
            text = "Add Media to Timeline",
            color = CapCutTextPrimary,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
          )
          IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = CapCutTextSecondary)
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Option 1: Sunset Stickman Cliff (Preset Scene)
        Surface(
          color = CapCutSurface,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable {
              onAddPresetSunset()
              onDismiss()
            }
        ) {
          Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(8.dp))
            ) {
              Image(
                painter = painterResource(id = R.drawable.clip_sunset),
                contentDescription = "Sunset scene",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text("Sunset Cliff Adventure", color = CapCutTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
              Text("Stickman cinematic cartoon scene • 25s", color = CapCutTextSecondary, fontSize = 12.sp)
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Option 2: Choose from Device Photos/Videos
        Surface(
          color = CapCutSurface,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable {
              photoPickerLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)
              )
            }
        ) {
          Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(60.dp)
                .background(CapCutSurfaceVariant, RoundedCornerShape(8.dp)),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.AddPhotoAlternate, contentDescription = "Gallery", tint = CapCutCyan, modifier = Modifier.size(28.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text("Import from Device", color = CapCutTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
              Text("Select video or photo from gallery", color = CapCutTextSecondary, fontSize = 12.sp)
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))
      }
    }
  }
}
