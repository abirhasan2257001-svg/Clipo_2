package com.example.viewmodel

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.R
import com.example.model.AnimationType
import com.example.model.AspectRatio
import com.example.model.AudioClip
import com.example.model.CurveType
import com.example.model.EditorMode
import com.example.model.EffectType
import com.example.model.ExportConfig
import com.example.model.ExportResolution
import com.example.model.FilterType
import com.example.model.Keyframe
import com.example.model.TextOverlay
import com.example.model.TransformState
import com.example.model.VideoClip
import com.example.model.VideoEffect
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

data class ProjectHistorySnapshot(
  val clips: List<VideoClip>,
  val audioTracks: List<AudioClip>,
  val textTracks: List<TextOverlay>,
  val effects: List<VideoEffect>
)

data class VideoEditorUiState(
  val currentTimeMs: Long = 0L,
  val isPlaying: Boolean = false,
  val clips: List<VideoClip> = emptyList(),
  val selectedClipId: String? = null,
  val audioTracks: List<AudioClip> = emptyList(),
  val textTracks: List<TextOverlay> = emptyList(),
  val effects: List<VideoEffect> = emptyList(),
  val editorMode: EditorMode = EditorMode.MAIN,
  val aspectRatio: AspectRatio = AspectRatio.RATIO_16_9,
  val compareMode: Boolean = false,
  val isFullscreen: Boolean = false,
  val isMuted: Boolean = false,
  val coverTimestampMs: Long = 0L,
  val exportConfig: ExportConfig = ExportConfig(),
  val isExporting: Boolean = false,
  val exportProgress: Float = 0f,
  val exportDone: Boolean = false,
  val exportedFilePath: String? = null,
  val showExportDialog: Boolean = false,
  val showQualityMenu: Boolean = false,
  val showMediaPicker: Boolean = false,
  val canUndo: Boolean = false,
  val canRedo: Boolean = false
) {
  val totalDurationMs: Long
    get() = clips.maxOfOrNull { it.startMs + it.durationMs }?.coerceAtLeast(10000L) ?: 10000L

  val selectedClip: VideoClip?
    get() = clips.find { it.id == selectedClipId }

  val activeClipAtPlayhead: VideoClip?
    get() = clips.find { currentTimeMs in it.startMs until (it.startMs + it.durationMs) } ?: selectedClip

  val currentTransform: TransformState
    get() {
      val clip = selectedClip ?: activeClipAtPlayhead ?: return TransformState()
      val clipTime = (currentTimeMs - clip.startMs).coerceIn(0L, clip.durationMs)
      return clip.getTransformAt(clipTime)
    }

  fun isPlayheadAtKeyframe(thresholdMs: Long = 100L): Keyframe? {
    val clip = selectedClip ?: return null
    val clipTime = currentTimeMs - clip.startMs
    return clip.keyframes.find { kotlin.math.abs(it.timeMs - clipTime) <= thresholdMs }
  }
}

class VideoEditorViewModel(application: Application) : AndroidViewModel(application) {

  private val _uiState = MutableStateFlow(VideoEditorUiState())
  val uiState: StateFlow<VideoEditorUiState> = _uiState.asStateFlow()

  private val undoStack = mutableListOf<ProjectHistorySnapshot>()
  private val redoStack = mutableListOf<ProjectHistorySnapshot>()
  private var playbackJob: Job? = null

  private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
    val vibratorManager = application.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
    vibratorManager?.defaultVibrator
  } else {
    @Suppress("DEPRECATION")
    application.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
  }

  init {
    loadInitialProject()
  }

  private fun loadInitialProject() {
    // Initial clip matching the user's reference: Cartoon stickman caveman in forest
    val initialClips = listOf(
      VideoClip(
        id = "clip_forest_1",
        name = "Stickman Forest",
        resId = R.drawable.clip_forest,
        startMs = 0L,
        durationMs = 60000L,
        sourceDurationMs = 60000L,
        keyframes = listOf(
          Keyframe(timeMs = 0L, posX = 0f, posY = 0f, scale = 1.0f, rotation = 0f, opacity = 1f, curveType = CurveType.EASE_IN_OUT),
          Keyframe(timeMs = 3000L, posX = -40f, posY = -20f, scale = 1.25f, rotation = 2f, opacity = 1f, curveType = CurveType.BOUNCE),
          Keyframe(timeMs = 6000L, posX = 50f, posY = 15f, scale = 1.15f, rotation = -3f, opacity = 0.95f, curveType = CurveType.FLOW),
          Keyframe(timeMs = 9000L, posX = 0f, posY = 0f, scale = 1.0f, rotation = 0f, opacity = 1f, curveType = CurveType.EASE_IN_OUT)
        )
      )
    )

    val initialAudio = listOf(
      AudioClip(
        id = "audio_bgm_1",
        title = "Forest Adventure BGM",
        startMs = 0L,
        durationMs = 60000L,
        volume = 0.85f,
        isBgm = true,
        soundType = "BGM"
      )
    )

    val initialText = listOf(
      TextOverlay(
        id = "text_intro",
        text = "STICKMAN ADVENTURE",
        startMs = 500L,
        durationMs = 3500L,
        posX = 0f,
        posY = -280f,
        scale = 1.1f,
        colorHex = 0xFFFFD166,
        fontSizeSp = 24f,
        styleName = "Bold Pop"
      )
    )

    _uiState.value = _uiState.value.copy(
      clips = initialClips,
      selectedClipId = initialClips.first().id,
      audioTracks = initialAudio,
      textTracks = initialText,
      currentTimeMs = 0L
    )
  }

  private fun saveSnapshot() {
    val state = _uiState.value
    undoStack.add(
      ProjectHistorySnapshot(
        clips = state.clips,
        audioTracks = state.audioTracks,
        textTracks = state.textTracks,
        effects = state.effects
      )
    )
    if (undoStack.size > 30) undoStack.removeAt(0)
    redoStack.clear()
    updateUndoRedoAvailability()
  }

  private fun updateUndoRedoAvailability() {
    _uiState.value = _uiState.value.copy(
      canUndo = undoStack.isNotEmpty(),
      canRedo = redoStack.isNotEmpty()
    )
  }

  fun undo() {
    if (undoStack.isEmpty()) return
    val currentSnapshot = ProjectHistorySnapshot(
      clips = _uiState.value.clips,
      audioTracks = _uiState.value.audioTracks,
      textTracks = _uiState.value.textTracks,
      effects = _uiState.value.effects
    )
    redoStack.add(currentSnapshot)
    val prev = undoStack.removeAt(undoStack.lastIndex)
    _uiState.value = _uiState.value.copy(
      clips = prev.clips,
      audioTracks = prev.audioTracks,
      textTracks = prev.textTracks,
      effects = prev.effects
    )
    hapticFeedback()
    updateUndoRedoAvailability()
  }

  fun redo() {
    if (redoStack.isEmpty()) return
    val currentSnapshot = ProjectHistorySnapshot(
      clips = _uiState.value.clips,
      audioTracks = _uiState.value.audioTracks,
      textTracks = _uiState.value.textTracks,
      effects = _uiState.value.effects
    )
    undoStack.add(currentSnapshot)
    val next = redoStack.removeAt(redoStack.lastIndex)
    _uiState.value = _uiState.value.copy(
      clips = next.clips,
      audioTracks = next.audioTracks,
      textTracks = next.textTracks,
      effects = next.effects
    )
    hapticFeedback()
    updateUndoRedoAvailability()
  }

  fun togglePlayPause() {
    if (_uiState.value.isPlaying) {
      pause()
    } else {
      play()
    }
  }

  fun play() {
    playbackJob?.cancel()
    _uiState.value = _uiState.value.copy(isPlaying = true)
    playbackJob = viewModelScope.launch {
      var lastNanos = System.nanoTime()
      while (isActive && _uiState.value.isPlaying) {
        delay(16) // Smooth ~60 fps tick
        val now = System.nanoTime()
        val deltaMs = (now - lastNanos) / 1_000_000L
        lastNanos = now

        val total = _uiState.value.totalDurationMs
        val nextTime = _uiState.value.currentTimeMs + deltaMs
        if (nextTime >= total) {
          _uiState.value = _uiState.value.copy(currentTimeMs = 0L, isPlaying = false)
          break
        } else {
          _uiState.value = _uiState.value.copy(currentTimeMs = nextTime)
        }
      }
    }
  }

  fun pause() {
    playbackJob?.cancel()
    playbackJob = null
    _uiState.value = _uiState.value.copy(isPlaying = false)
  }

  fun seekTo(timeMs: Long) {
    val total = _uiState.value.totalDurationMs
    val clamped = timeMs.coerceIn(0L, total)

    // Check keyframe snap
    val selected = _uiState.value.selectedClip
    var target = clamped
    if (selected != null) {
      val clipTime = clamped - selected.startMs
      val snapKf = selected.keyframes.find { kotlin.math.abs(it.timeMs - clipTime) < 120L }
      if (snapKf != null) {
        target = selected.startMs + snapKf.timeMs
        hapticFeedback(true)
      }
    }

    _uiState.value = _uiState.value.copy(currentTimeMs = target)
  }

  fun selectClip(clipId: String?) {
    _uiState.value = _uiState.value.copy(
      selectedClipId = clipId,
      editorMode = if (clipId != null && _uiState.value.editorMode == EditorMode.MAIN) EditorMode.EDIT_CLIP else _uiState.value.editorMode
    )
    hapticFeedback()
  }

  fun setEditorMode(mode: EditorMode) {
    _uiState.value = _uiState.value.copy(editorMode = mode)
    hapticFeedback()
  }

  fun toggleCompareMode() {
    _uiState.value = _uiState.value.copy(compareMode = !_uiState.value.compareMode)
    hapticFeedback()
  }

  fun toggleFullscreen() {
    _uiState.value = _uiState.value.copy(isFullscreen = !_uiState.value.isFullscreen)
  }

  fun toggleMute() {
    _uiState.value = _uiState.value.copy(isMuted = !_uiState.value.isMuted)
    hapticFeedback()
  }

  fun setAspectRatio(ratio: AspectRatio) {
    _uiState.value = _uiState.value.copy(aspectRatio = ratio)
    hapticFeedback()
  }

  // --- Advanced Keyframe Tools ---

  fun toggleKeyframeAtPlayhead() {
    val clip = _uiState.value.selectedClip ?: return
    saveSnapshot()

    val clipTime = (_uiState.value.currentTimeMs - clip.startMs).coerceIn(0L, clip.durationMs)
    val existingKf = clip.keyframes.find { kotlin.math.abs(it.timeMs - clipTime) <= 150L }

    val updatedKeyframes = if (existingKf != null) {
      // Remove keyframe
      clip.keyframes.filterNot { it.id == existingKf.id }
    } else {
      // Add keyframe with current interpolated transform
      val currentTransform = clip.getTransformAt(clipTime)
      val newKf = Keyframe(
        timeMs = clipTime,
        posX = currentTransform.posX,
        posY = currentTransform.posY,
        scale = currentTransform.scale,
        rotation = currentTransform.rotation,
        opacity = currentTransform.opacity,
        curveType = CurveType.EASE_IN_OUT
      )
      (clip.keyframes + newKf).sortedBy { it.timeMs }
    }

    updateClipInList(clip.copy(keyframes = updatedKeyframes))
    hapticFeedback(true)
  }

  fun jumpToPrevKeyframe() {
    val clip = _uiState.value.selectedClip ?: return
    val clipTime = _uiState.value.currentTimeMs - clip.startMs
    val prevKf = clip.keyframes.filter { it.timeMs < clipTime - 50L }.maxByOrNull { it.timeMs }
    if (prevKf != null) {
      seekTo(clip.startMs + prevKf.timeMs)
      hapticFeedback()
    }
  }

  fun jumpToNextKeyframe() {
    val clip = _uiState.value.selectedClip ?: return
    val clipTime = _uiState.value.currentTimeMs - clip.startMs
    val nextKf = clip.keyframes.filter { it.timeMs > clipTime + 50L }.minByOrNull { it.timeMs }
    if (nextKf != null) {
      seekTo(clip.startMs + nextKf.timeMs)
      hapticFeedback()
    }
  }

  fun updateKeyframeTransform(
    posX: Float? = null,
    posY: Float? = null,
    scale: Float? = null,
    rotation: Float? = null,
    opacity: Float? = null,
    curveType: CurveType? = null
  ) {
    val clip = _uiState.value.selectedClip ?: return
    val clipTime = (_uiState.value.currentTimeMs - clip.startMs).coerceIn(0L, clip.durationMs)
    val existingKf = clip.keyframes.find { kotlin.math.abs(it.timeMs - clipTime) <= 200L }

    val updatedKeyframes = if (existingKf != null) {
      clip.keyframes.map { kf ->
        if (kf.id == existingKf.id) {
          kf.copy(
            posX = posX ?: kf.posX,
            posY = posY ?: kf.posY,
            scale = scale ?: kf.scale,
            rotation = rotation ?: kf.rotation,
            opacity = opacity ?: kf.opacity,
            curveType = curveType ?: kf.curveType
          )
        } else kf
      }
    } else {
      // Auto-create keyframe at current position
      val currentTransform = clip.getTransformAt(clipTime)
      val newKf = Keyframe(
        timeMs = clipTime,
        posX = posX ?: currentTransform.posX,
        posY = posY ?: currentTransform.posY,
        scale = scale ?: currentTransform.scale,
        rotation = rotation ?: currentTransform.rotation,
        opacity = opacity ?: currentTransform.opacity,
        curveType = curveType ?: CurveType.EASE_IN_OUT
      )
      (clip.keyframes + newKf).sortedBy { it.timeMs }
    }

    updateClipInList(clip.copy(keyframes = updatedKeyframes))
  }

  fun setKeyframeCurveForCurrent(curveType: CurveType) {
    val clip = _uiState.value.selectedClip ?: return
    val clipTime = _uiState.value.currentTimeMs - clip.startMs
    val existingKf = clip.keyframes.find { kotlin.math.abs(it.timeMs - clipTime) <= 300L }
      ?: clip.keyframes.minByOrNull { kotlin.math.abs(it.timeMs - clipTime) }
      ?: return

    val updated = clip.keyframes.map {
      if (it.id == existingKf.id) it.copy(curveType = curveType) else it
    }
    updateClipInList(clip.copy(keyframes = updated))
    hapticFeedback()
  }

  // --- Clip Editing Actions ---

  fun splitSelectedClip() {
    val clip = _uiState.value.selectedClip ?: return
    val playhead = _uiState.value.currentTimeMs
    if (playhead <= clip.startMs + 500L || playhead >= clip.startMs + clip.durationMs - 500L) {
      return // Too close to edge
    }

    saveSnapshot()
    val firstDuration = playhead - clip.startMs
    val secondDuration = clip.durationMs - firstDuration

    val keyframes1 = clip.keyframes.filter { it.timeMs <= firstDuration }
    val keyframes2 = clip.keyframes.filter { it.timeMs > firstDuration }.map {
      it.copy(timeMs = it.timeMs - firstDuration)
    }

    val clip1 = clip.copy(
      id = java.util.UUID.randomUUID().toString(),
      durationMs = firstDuration,
      keyframes = keyframes1
    )
    val clip2 = clip.copy(
      id = java.util.UUID.randomUUID().toString(),
      startMs = playhead,
      durationMs = secondDuration,
      keyframes = keyframes2
    )

    val currentList = _uiState.value.clips
    val index = currentList.indexOfFirst { it.id == clip.id }
    if (index >= 0) {
      val mutable = currentList.toMutableList()
      mutable.removeAt(index)
      mutable.add(index, clip1)
      mutable.add(index + 1, clip2)
      _uiState.value = _uiState.value.copy(clips = mutable, selectedClipId = clip2.id)
    }
    hapticFeedback(true)
  }

  fun deleteSelectedClip() {
    val clip = _uiState.value.selectedClip ?: return
    if (_uiState.value.clips.size <= 1) return // Keep at least one clip
    saveSnapshot()

    val currentList = _uiState.value.clips.filterNot { it.id == clip.id }
    // Recalculate start times for ripple edit
    var cursor = 0L
    val reordered = currentList.map {
      val updated = it.copy(startMs = cursor)
      cursor += it.durationMs
      updated
    }

    _uiState.value = _uiState.value.copy(
      clips = reordered,
      selectedClipId = reordered.firstOrNull()?.id,
      editorMode = EditorMode.MAIN
    )
    hapticFeedback()
  }

  fun duplicateSelectedClip() {
    val clip = _uiState.value.selectedClip ?: return
    saveSnapshot()

    val cloned = clip.copy(
      id = java.util.UUID.randomUUID().toString(),
      name = "${clip.name} (Copy)",
      startMs = clip.startMs + clip.durationMs
    )

    val currentList = _uiState.value.clips.toMutableList()
    val index = currentList.indexOfFirst { it.id == clip.id }
    if (index >= 0) {
      currentList.add(index + 1, cloned)
      // shift remaining
      var cursor = 0L
      val shifted = currentList.map {
        val updated = it.copy(startMs = cursor)
        cursor += it.durationMs
        updated
      }
      _uiState.value = _uiState.value.copy(clips = shifted, selectedClipId = cloned.id)
    }
    hapticFeedback()
  }

  fun updateSpeed(speed: Float) {
    val clip = _uiState.value.selectedClip ?: return
    saveSnapshot()
    val newDuration = (clip.sourceDurationMs / speed).toLong().coerceAtLeast(500L)
    val updated = clip.copy(speed = speed, durationMs = newDuration)
    updateClipInList(updated)
    recalculateTimeline()
  }

  fun updateVolume(volume: Float) {
    val clip = _uiState.value.selectedClip ?: return
    val updated = clip.copy(volume = volume)
    updateClipInList(updated)
  }

  fun updateFilter(filter: FilterType) {
    val clip = _uiState.value.selectedClip ?: return
    saveSnapshot()
    val updated = clip.copy(filter = filter)
    updateClipInList(updated)
    hapticFeedback()
  }

  fun updateAnimation(animIn: AnimationType, animOut: AnimationType) {
    val clip = _uiState.value.selectedClip ?: return
    saveSnapshot()
    val updated = clip.copy(animationIn = animIn, animationOut = animOut)
    updateClipInList(updated)
    hapticFeedback()
  }

  fun updateAdjustments(brightness: Float, contrast: Float, saturation: Float, vignette: Float) {
    val clip = _uiState.value.selectedClip ?: return
    val updated = clip.copy(
      brightness = brightness,
      contrast = contrast,
      saturation = saturation,
      vignette = vignette
    )
    updateClipInList(updated)
  }

  fun addPresetSunsetClip() {
    saveSnapshot()
    val currentDuration = _uiState.value.totalDurationMs
    val newClip = VideoClip(
      id = "clip_sunset_${System.currentTimeMillis()}",
      name = "Sunset Cliff",
      resId = R.drawable.clip_sunset,
      startMs = currentDuration,
      durationMs = 25000L,
      sourceDurationMs = 25000L,
      keyframes = listOf(
        Keyframe(timeMs = 0L, posX = 0f, posY = 0f, scale = 1.0f, curveType = CurveType.EASE_IN_OUT),
        Keyframe(timeMs = 5000L, posX = 20f, posY = -10f, scale = 1.1f, curveType = CurveType.EASE_OUT)
      )
    )
    _uiState.value = _uiState.value.copy(
      clips = _uiState.value.clips + newClip,
      selectedClipId = newClip.id
    )
    hapticFeedback()
  }

  fun addClipFromUri(uriString: String, name: String) {
    saveSnapshot()
    val currentDuration = _uiState.value.totalDurationMs
    val newClip = VideoClip(
      id = "clip_user_${System.currentTimeMillis()}",
      name = name,
      imageUri = uriString,
      startMs = currentDuration,
      durationMs = 15000L,
      sourceDurationMs = 15000L
    )
    _uiState.value = _uiState.value.copy(
      clips = _uiState.value.clips + newClip,
      selectedClipId = newClip.id
    )
    hapticFeedback()
  }

  // --- Audio & Sound Effects ---

  fun addAudioTrack(title: String, durationMs: Long = 10000L, isBgm: Boolean = false, soundType: String = "FX") {
    saveSnapshot()
    val newAudio = AudioClip(
      title = title,
      startMs = _uiState.value.currentTimeMs,
      durationMs = durationMs,
      volume = if (isBgm) 0.8f else 1.0f,
      isBgm = isBgm,
      soundType = soundType
    )
    _uiState.value = _uiState.value.copy(audioTracks = _uiState.value.audioTracks + newAudio)
    hapticFeedback()
  }

  fun deleteAudioTrack(id: String) {
    saveSnapshot()
    _uiState.value = _uiState.value.copy(
      audioTracks = _uiState.value.audioTracks.filterNot { it.id == id }
    )
  }

  // --- Text & Titles ---

  fun addTextOverlay(text: String, styleName: String = "Bold Pop", colorHex: Long = 0xFFFFFFFF) {
    saveSnapshot()
    val newText = TextOverlay(
      text = text,
      startMs = _uiState.value.currentTimeMs,
      durationMs = 4000L,
      posX = 0f,
      posY = 0f,
      colorHex = colorHex,
      styleName = styleName
    )
    _uiState.value = _uiState.value.copy(textTracks = _uiState.value.textTracks + newText)
    hapticFeedback()
  }

  fun updateTextOverlay(id: String, text: String, colorHex: Long, scale: Float) {
    val updated = _uiState.value.textTracks.map {
      if (it.id == id) it.copy(text = text, colorHex = colorHex, scale = scale) else it
    }
    _uiState.value = _uiState.value.copy(textTracks = updated)
  }

  fun deleteTextOverlay(id: String) {
    saveSnapshot()
    _uiState.value = _uiState.value.copy(
      textTracks = _uiState.value.textTracks.filterNot { it.id == id }
    )
  }

  // --- Video Effects ---

  fun addEffect(type: EffectType) {
    saveSnapshot()
    val newEffect = VideoEffect(
      type = type,
      name = type.displayName,
      intensity = 0.7f,
      startMs = _uiState.value.currentTimeMs,
      durationMs = 6000L
    )
    _uiState.value = _uiState.value.copy(effects = _uiState.value.effects + newEffect)
    hapticFeedback()
  }

  fun deleteEffect(id: String) {
    saveSnapshot()
    _uiState.value = _uiState.value.copy(
      effects = _uiState.value.effects.filterNot { it.id == id }
    )
  }

  // --- Export Studio ---

  fun showExportDialog(show: Boolean) {
    _uiState.value = _uiState.value.copy(showExportDialog = show)
  }

  fun toggleQualityMenu(show: Boolean) {
    _uiState.value = _uiState.value.copy(showQualityMenu = show)
  }

  fun setExportResolution(resolution: ExportResolution) {
    _uiState.value = _uiState.value.copy(
      exportConfig = _uiState.value.exportConfig.copy(resolution = resolution)
    )
    hapticFeedback()
  }

  fun setExportFps(fps: Int) {
    _uiState.value = _uiState.value.copy(
      exportConfig = _uiState.value.exportConfig.copy(fps = fps)
    )
    hapticFeedback()
  }

  fun toggleSmartHdr() {
    _uiState.value = _uiState.value.copy(
      exportConfig = _uiState.value.exportConfig.copy(
        smartHdr = !_uiState.value.exportConfig.smartHdr
      )
    )
  }

  fun toggleHardwareAcceleration() {
    _uiState.value = _uiState.value.copy(
      exportConfig = _uiState.value.exportConfig.copy(
        hardwareAcceleration = !_uiState.value.exportConfig.hardwareAcceleration
      )
    )
  }

  fun startExport() {
    _uiState.value = _uiState.value.copy(
      isExporting = true,
      exportProgress = 0f,
      exportDone = false,
      exportedFilePath = null
    )

    viewModelScope.launch {
      // Simulate real hardware-accelerated video rendering synthesis
      val totalSteps = 100
      for (step in 1..totalSteps) {
        delay(28) // realistic render cadence
        _uiState.value = _uiState.value.copy(exportProgress = step / 100f)
      }

      // Generate actual exported MP4 marker file on device storage
      val context = getApplication<Application>()
      val exportDir = File(context.cacheDir, "exports")
      if (!exportDir.exists()) exportDir.mkdirs()
      val outputFile = File(exportDir, "CapCut_Export_${System.currentTimeMillis()}.mp4")
      try {
        FileOutputStream(outputFile).use { fos ->
          fos.write("CapCut Video Rendered File".toByteArray())
        }
      } catch (e: Exception) {
        e.printStackTrace()
      }

      _uiState.value = _uiState.value.copy(
        isExporting = false,
        exportDone = true,
        exportProgress = 1.0f,
        exportedFilePath = outputFile.absolutePath
      )
      hapticFeedback(true)
    }
  }

  fun setCoverTimestamp() {
    _uiState.value = _uiState.value.copy(coverTimestampMs = _uiState.value.currentTimeMs)
    hapticFeedback()
  }

  fun showMediaPicker(show: Boolean) {
    _uiState.value = _uiState.value.copy(showMediaPicker = show)
  }

  private fun updateClipInList(updatedClip: VideoClip) {
    val list = _uiState.value.clips.map {
      if (it.id == updatedClip.id) updatedClip else it
    }
    _uiState.value = _uiState.value.copy(clips = list)
  }

  private fun recalculateTimeline() {
    var cursor = 0L
    val shifted = _uiState.value.clips.map {
      val updated = it.copy(startMs = cursor)
      cursor += it.durationMs
      updated
    }
    _uiState.value = _uiState.value.copy(clips = shifted)
  }

  private fun hapticFeedback(strong: Boolean = false) {
    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val effect = if (strong) {
          VibrationEffect.createOneShot(28, VibrationEffect.DEFAULT_AMPLITUDE)
        } else {
          VibrationEffect.createOneShot(12, VibrationEffect.EFFECT_TICK)
        }
        vibrator?.vibrate(effect)
      } else {
        @Suppress("DEPRECATION")
        vibrator?.vibrate(if (strong) 25L else 10L)
      }
    } catch (_: Exception) {}
  }
}
