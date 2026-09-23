package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.EditorMode
import com.example.ui.components.AudioStudioSheet
import com.example.ui.components.BottomToolbar
import com.example.ui.components.CanvasRatioSheet
import com.example.ui.components.EffectsStudioSheet
import com.example.ui.components.ExportDialog
import com.example.ui.components.FiltersAdjustSheet
import com.example.ui.components.KeyframeStudioSheet
import com.example.ui.components.MediaPickerDialog
import com.example.ui.components.PreviewActionBar
import com.example.ui.components.PreviewCanvas
import com.example.ui.components.SpeedStudioSheet
import com.example.ui.components.TextStudioSheet
import com.example.ui.components.TimelineRulerBar
import com.example.ui.components.TimelineView
import com.example.ui.components.TopEditorBar
import com.example.ui.theme.CapCutDarkBg
import com.example.viewmodel.VideoEditorViewModel
import kotlinx.coroutines.launch

@Composable
fun VideoEditorScreen(
  viewModel: VideoEditorViewModel = viewModel()
) {
  val state by viewModel.uiState.collectAsState()
  val scope = rememberCoroutineScope()
  val snackbarHostState = remember { SnackbarHostState() }

  // Sub-panel modals
  var showKeyframeStudio by remember { mutableStateOf(false) }
  var showSpeedStudio by remember { mutableStateOf(false) }
  var showMediaPicker by remember { mutableStateOf(false) }

  Scaffold(
    snackbarHost = { SnackbarHost(snackbarHostState) },
    containerColor = CapCutDarkBg,
    modifier = Modifier
      .fillMaxSize()
      .systemBarsPadding()
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .background(CapCutDarkBg)
    ) {
      // 1. Top Bar (Close, Search, AI UHD Badge, Export Button)
      TopEditorBar(
        currentResolution = state.exportConfig.resolution,
        showQualityMenu = state.showQualityMenu,
        onQualityMenuToggle = { viewModel.toggleQualityMenu(it) },
        onSelectResolution = { viewModel.setExportResolution(it) },
        onExportClick = { viewModel.showExportDialog(true) },
        onCloseClick = {
          scope.launch {
            snackbarHostState.showSnackbar("Project Auto-Saved")
          }
        }
      )

      // 2. Preview Canvas (60 FPS, Stickman Forest, Keyframe Transforms, Gestures, Compare Mode)
      PreviewCanvas(
        clip = state.activeClipAtPlayhead,
        currentTimeMs = state.currentTimeMs,
        transform = state.currentTransform,
        aspectRatio = state.aspectRatio,
        compareMode = state.compareMode,
        isPlaying = state.isPlaying,
        effects = state.effects,
        textTracks = state.textTracks,
        onTransformChange = { posX, posY, scale, rotation ->
          viewModel.updateKeyframeTransform(posX, posY, scale, rotation, null, null)
        },
        modifier = Modifier.weight(if (state.isFullscreen) 1f else 0.85f)
      )

      // 3. Preview Action Bar (Fullscreen, Play/Pause, Compare ⧉ OFF/ON, Undo, Redo)
      PreviewActionBar(
        isPlaying = state.isPlaying,
        isFullscreen = state.isFullscreen,
        compareMode = state.compareMode,
        canUndo = state.canUndo,
        canRedo = state.canRedo,
        onPlayPauseToggle = { viewModel.togglePlayPause() },
        onFullscreenToggle = { viewModel.toggleFullscreen() },
        onCompareToggle = { viewModel.toggleCompareMode() },
        onUndo = { viewModel.undo() },
        onRedo = { viewModel.redo() }
      )

      if (!state.isFullscreen) {
        // 4. Timeline Ruler & Keyframe Bar (Timecode "00:00 / 01:00", Keyframe diamond buttons ◁◇, ◇+, ◇▷, ∿ Curves)
        TimelineRulerBar(
          currentTimeMs = state.currentTimeMs,
          totalDurationMs = state.totalDurationMs,
          selectedClip = state.selectedClip,
          activeKeyframeAtPlayhead = state.isPlayheadAtKeyframe(),
          onToggleKeyframe = { viewModel.toggleKeyframeAtPlayhead() },
          onJumpPrevKeyframe = { viewModel.jumpToPrevKeyframe() },
          onJumpNextKeyframe = { viewModel.jumpToNextKeyframe() },
          onOpenCurves = { showKeyframeStudio = true }
        )

        // 5. Timeline Tracks Area (Left Rail: Mute Clip, Cover; Center White Needle; Tracks: Filmstrip, Keyframes, Audio, Text)
        TimelineView(
          currentTimeMs = state.currentTimeMs,
          totalDurationMs = state.totalDurationMs,
          clips = state.clips,
          selectedClipId = state.selectedClipId,
          audioTracks = state.audioTracks,
          textTracks = state.textTracks,
          isMuted = state.isMuted,
          onSeekTo = { viewModel.seekTo(it) },
          onSelectClip = { viewModel.selectClip(it) },
          onToggleMute = { viewModel.toggleMute() },
          onSetCover = {
            viewModel.setCoverTimestamp()
            scope.launch { snackbarHostState.showSnackbar("Cover frame updated!") }
          },
          onAddMediaClick = { showMediaPicker = true },
          onAddAudioClick = { viewModel.setEditorMode(EditorMode.AUDIO) },
          onAddTextClick = { viewModel.setEditorMode(EditorMode.TEXT) },
          modifier = Modifier
            .weight(0.9f)
            .fillMaxWidth()
        )

        // 6. Bottom Navigation Toolbar (Edit, Audio, Text, Effects, Overlay, Captions, Filters, Adjust, Split, Speed)
        BottomToolbar(
          currentMode = state.editorMode,
          onModeSelect = { viewModel.setEditorMode(it) },
          onSplitClip = { viewModel.splitSelectedClip() },
          onDeleteClip = { viewModel.deleteSelectedClip() },
          onDuplicateClip = { viewModel.duplicateSelectedClip() },
          onOpenKeyframeStudio = { showKeyframeStudio = true },
          onOpenSpeedStudio = { showSpeedStudio = true }
        )
      }
    }
  }

  // --- Modals & Sheets ---

  // Keyframe Studio Sheet
  if (showKeyframeStudio) {
    KeyframeStudioSheet(
      selectedClip = state.selectedClip,
      currentTransform = state.currentTransform,
      activeKeyframe = state.isPlayheadAtKeyframe(),
      onDismiss = { showKeyframeStudio = false },
      onUpdateTransform = { x, y, s, r, o, curve ->
        viewModel.updateKeyframeTransform(x, y, s, r, o, curve)
      },
      onToggleKeyframe = { viewModel.toggleKeyframeAtPlayhead() },
      onJumpPrevKeyframe = { viewModel.jumpToPrevKeyframe() },
      onJumpNextKeyframe = { viewModel.jumpToNextKeyframe() }
    )
  }

  // Speed Studio Sheet
  if (showSpeedStudio) {
    SpeedStudioSheet(
      currentSpeed = state.selectedClip?.speed ?: 1.0f,
      onSpeedChange = { viewModel.updateSpeed(it) },
      onDismiss = { showSpeedStudio = false }
    )
  }

  // Audio Studio Sheet
  if (state.editorMode == EditorMode.AUDIO) {
    AudioStudioSheet(
      onAddAudio = { title, durationMs, isBgm, type ->
        viewModel.addAudioTrack(title, durationMs, isBgm, type)
      },
      onDismiss = { viewModel.setEditorMode(EditorMode.MAIN) }
    )
  }

  // Text Studio Sheet
  if (state.editorMode == EditorMode.TEXT) {
    TextStudioSheet(
      onAddText = { text, style, color ->
        viewModel.addTextOverlay(text, style, color)
      },
      onDismiss = { viewModel.setEditorMode(EditorMode.MAIN) }
    )
  }

  // Effects Studio Sheet
  if (state.editorMode == EditorMode.EFFECTS) {
    EffectsStudioSheet(
      onAddEffect = { effectType ->
        viewModel.addEffect(effectType)
      },
      onDismiss = { viewModel.setEditorMode(EditorMode.MAIN) }
    )
  }

  // Filters & Adjust Sheet
  if (state.editorMode == EditorMode.FILTERS || state.editorMode == EditorMode.ADJUST) {
    FiltersAdjustSheet(
      isFilterMode = state.editorMode == EditorMode.FILTERS,
      clip = state.selectedClip,
      onFilterChange = { viewModel.updateFilter(it) },
      onAdjustmentsChange = { b, c, s, v ->
        viewModel.updateAdjustments(b, c, s, v)
      },
      onDismiss = { viewModel.setEditorMode(EditorMode.MAIN) }
    )
  }

  // Canvas Aspect Ratio Sheet
  if (state.editorMode == EditorMode.CANVAS) {
    CanvasRatioSheet(
      currentRatio = state.aspectRatio,
      onSelectRatio = { viewModel.setAspectRatio(it) },
      onDismiss = { viewModel.setEditorMode(EditorMode.MAIN) }
    )
  }

  // Media Picker Dialog
  if (showMediaPicker) {
    MediaPickerDialog(
      onAddPresetSunset = { viewModel.addPresetSunsetClip() },
      onAddUserMedia = { uri, name -> viewModel.addClipFromUri(uri, name) },
      onDismiss = { showMediaPicker = false }
    )
  }

  // Export Dialog (AI UHD, 60 FPS, Hardware acceleration, real rendering progress)
  if (state.showExportDialog) {
    ExportDialog(
      exportConfig = state.exportConfig,
      isExporting = state.isExporting,
      exportProgress = state.exportProgress,
      exportDone = state.exportDone,
      exportedFilePath = state.exportedFilePath,
      onResolutionChange = { viewModel.setExportResolution(it) },
      onFpsChange = { viewModel.setExportFps(it) },
      onToggleHdr = { viewModel.toggleSmartHdr() },
      onToggleHardwareAcceleration = { viewModel.toggleHardwareAcceleration() },
      onStartExport = { viewModel.startExport() },
      onDismiss = { viewModel.showExportDialog(false) }
    )
  }
}
