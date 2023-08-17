package com.coroutinedispatcher.newsspeaker.ui.camera

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.MediaStoreOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.PreviewView
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.core.graphics.toColorInt
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.coroutinedispatcher.newsspeaker.MainViewModel
import com.coroutinedispatcher.newsspeaker.R
import com.coroutinedispatcher.newsspeaker.databinding.FragmentCameraBinding
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.common.util.concurrent.ListenableFuture
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

// TODO Remove Back button
@AndroidEntryPoint
class CameraFragment : Fragment() {

    private var cameraBinding: FragmentCameraBinding? = null
    private val binding
        get() = checkNotNull(cameraBinding)

    private var videoCapture: VideoCapture<Recorder>? = null
    private var recording: Recording? = null
    private lateinit var cameraExecutor: ExecutorService
    private val cameraViewModel by viewModels<CameraViewModel>()
    private val mainViewModel by activityViewModels<MainViewModel>()

    override fun onResume() {
        super.onResume()
        mainViewModel.currentProject?.pId ?: finish()
        cameraViewModel.loadCurrentProject(checkNotNull(mainViewModel.currentProject).pId)
    }

    @SuppressLint("CoroutineCreationDuringComposition")
    @Composable
    private fun TextContentComponent(state: CameraViewModel.State.ContentReady) {
        val listState = rememberLazyListState()
        val coroutineScope = rememberCoroutineScope()
        val data = state.project.content.split(" ").toList() + listOf(" ", " ")
        val scrollEvent = cameraViewModel.scrollingState.collectAsStateWithLifecycle(
            initialValue = null
        )

        when (val scrollingStateValue = scrollEvent.value) {
            null -> Unit
            else -> coroutineScope.launch {
                if (scrollingStateValue >= data.size) return@launch
                listState.animateScrollToItem(scrollingStateValue)
            }
        }

        LazyColumn(
            content = {
                items(data.size) { position ->
                    Text(
                        text = data[position],
                        modifier = Modifier.fillMaxSize(),
                        fontSize = 32.sp,
                        textAlign = TextAlign.Center,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            modifier = Modifier
                .height(300.dp)
                .fillMaxWidth()
                .background(
                    Color("#CC0F0F0F".toColorInt())
                ),
            state = listState
        )
    }

    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        cameraBinding = FragmentCameraBinding.inflate(inflater, container, false)
        cameraExecutor = Executors.newSingleThreadExecutor()

        binding.cameraComposeView.setContent {
            val lifecycleOwner = LocalLifecycleOwner.current
            val context = LocalContext.current
            val cameraProviderFuture = remember {
                ProcessCameraProvider.getInstance(context)
            }
            val cameraPermissionState = rememberMultiplePermissionsState(
                mutableListOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO
                ).apply {
                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                        add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    }
                }
            )
            val state = cameraViewModel.state.collectAsStateWithLifecycle()

            if (cameraPermissionState.allPermissionsGranted) {
                Box(modifier = Modifier.fillMaxSize()) {
                    CameraComponent(lifecycleOwner, cameraProviderFuture)
                    CameraToolsComponent()
                    when (state.value) {
                        CameraViewModel.State.Idle -> Unit
                        is CameraViewModel.State.ContentReady ->
                            TextContentComponent(state.value as CameraViewModel.State.ContentReady)
                    }
                }
            } else {
                if (cameraPermissionState.shouldShowRationale) {
                    // TODO: Give the user a message if he denies the permission
                    requireActivity().supportFragmentManager.popBackStack()
                }
                LaunchedEffect(key1 = Unit, block = {
                    cameraPermissionState.launchMultiplePermissionRequest()
                })
            }
        }

        return binding.root
    }

    @Composable
    fun BoxScope.CameraToolsComponent(modifier: Modifier = Modifier) {
        val isRecording = rememberSaveable { mutableStateOf(false) }

        Box(
            modifier = modifier
                .align(Alignment.BottomCenter)
                .wrapContentHeight()
                .fillMaxWidth()
                .background(
                    Color("#CC0F0F0F".toColorInt())
                )
        ) {
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .wrapContentHeight(),
                contentAlignment = Alignment.Center
            ) {
                Crossfade(
                    modifier = Modifier.wrapContentSize(),
                    targetState = isRecording.value,
                    animationSpec = tween(1000)
                ) { targetState ->
                    Image(
                        modifier = Modifier
                            .clickable {
                                captureVideo(
                                    onRecordingStarted = {
                                        isRecording.value = true
                                        cameraViewModel.startLoopingSubtitles()
                                    },
                                    onRecordingStopped = { recordEvent ->
                                        isRecording.value = false
                                        tryConclude(recordEvent)
                                    }
                                )
                            }
                            .width(100.dp)
                            .height(100.dp),
                        painter = painterResource(
                            id = if (targetState) {
                                R.drawable.stop_recording_light
                            } else {
                                R.drawable.record_light_mode_button
                            }
                        ),
                        contentDescription = stringResource(id = R.string.capture_video)
                    )
                }
            }
        }
    }

    private fun tryConclude(recordEvent: VideoRecordEvent.Finalize) {
        cameraViewModel.stopLoopingSubtitles()
        if (!recordEvent.hasError()) {
            val msg = "Video capture succeeded: " +
                "${recordEvent.outputResults.outputUri}"
            Toast
                .makeText(requireContext(), msg, Toast.LENGTH_SHORT)
                .show()

            mainViewModel.updatePath(recordEvent.outputResults.outputUri)
        } else {
            recording?.close()
            recording = null
            somethingWentWrong()
        }
        finish()
    }

    @Composable
    fun CameraComponent(
        lifecycleOwner: LifecycleOwner,
        cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                PreviewView(context).apply {
                    cameraProviderFuture.addListener({
                        // Used to bind the lifecycle of cameras to the lifecycle owner
                        val cameraProvider: ProcessCameraProvider =
                            cameraProviderFuture.get()

                        // Preview
                        val preview = Preview.Builder()
                            .build()
                            .also {
                                it.setSurfaceProvider(surfaceProvider)
                            }

                        val recorder = Recorder.Builder()
                            .setQualitySelector(QualitySelector.from(Quality.HIGHEST))
                            .build()
                        videoCapture = VideoCapture.withOutput(recorder)

                        // Select back camera as a default
                        val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

                        try {
                            // Unbind use cases before rebinding
                            cameraProvider.unbindAll()

                            // Bind use cases to camera
                            cameraProvider
                                .bindToLifecycle(
                                    lifecycleOwner,
                                    cameraSelector,
                                    preview,
                                    videoCapture
                                )
                        } catch (exc: Exception) {
                            Log.e(TAG, "Use case binding failed", exc)
                        }
                    }, ContextCompat.getMainExecutor(requireActivity()))
                }
            }
        )
    }

    private fun captureVideo(
        onRecordingStarted: () -> Unit,
        onRecordingStopped: (VideoRecordEvent.Finalize) -> Unit
    ) {
        val videoCapture = this.videoCapture ?: return

        val curRecording = recording

        if (curRecording != null) {
            // Stop the current recording session.
            curRecording.stop()
            recording = null
            return
        }

        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/CameraX-Video")
            }
        }

        val mediaStoreOutputOptions = MediaStoreOutputOptions
            .Builder(requireActivity().contentResolver, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
            .setContentValues(contentValues)
            .build()

        recording = videoCapture.output
            .prepareRecording(requireActivity(), mediaStoreOutputOptions)
            .apply {
                if (PermissionChecker.checkSelfPermission(
                        requireActivity(),
                        Manifest.permission.RECORD_AUDIO
                    ) ==
                    PermissionChecker.PERMISSION_GRANTED
                ) {
                    withAudioEnabled()
                }
            }
            .start(ContextCompat.getMainExecutor(requireActivity())) { recordEvent ->
                when (recordEvent) {
                    is VideoRecordEvent.Start -> onRecordingStarted()
                    is VideoRecordEvent.Finalize -> onRecordingStopped(recordEvent)
                }
            }
    }

    private fun somethingWentWrong() {
        // TODO. Don't forget to handle me 👀
    }

    private fun finish() {
        val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
        val backStackEntryCount: Int = fragmentManager.backStackEntryCount
        for (i in 0 until backStackEntryCount) {
            fragmentManager.popBackStack()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraBinding = null
        cameraExecutor.shutdown()
    }

    companion object {
        const val TAG = "CameraXFragment"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"

        fun newInstance(): CameraFragment = CameraFragment()
    }
}
