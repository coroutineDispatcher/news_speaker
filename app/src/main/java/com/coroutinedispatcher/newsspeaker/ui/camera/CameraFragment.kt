package com.coroutinedispatcher.newsspeaker.ui.camera

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.core.graphics.toColorInt
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.coroutinedispatcher.newsspeaker.R
import com.coroutinedispatcher.newsspeaker.databinding.FragmentCameraBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@AndroidEntryPoint
class CameraFragment : Fragment() {

    private var cameraBinding: FragmentCameraBinding? = null
    private val binding
        get() = checkNotNull(cameraBinding)

    private var videoCapture: VideoCapture<Recorder>? = null
    private var recording: Recording? = null
    private lateinit var cameraExecutor: ExecutorService
    private val cameraViewModel by viewModels<CameraViewModel>()

    private val activityResultLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        var permissionGranted = true
        permissions.entries.forEach {
            if (it.key in REQUIRED_PERMISSIONS && !it.value) {
                permissionGranted = false
            }
        }
        if (!permissionGranted) {
            requireActivity().supportFragmentManager.popBackStack()
        } else {
            startCamera()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissions()
        }

        val projectId = arguments?.getLong(PROJECT_ID_FRAGMENT_TAG_CAMERA)
        if (projectId == null) {
            Toast.makeText(requireActivity(), "Something went wrong", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        cameraViewModel.loadCurrentProject(projectId)
    }

    override fun onResume() {
        super.onResume()
        viewLifecycleOwner.lifecycleScope.launch {
            cameraViewModel.state.collect {
                when (it) {
                    CameraViewModel.State.Finished -> finish()
                    CameraViewModel.State.Idle -> Unit
                    is CameraViewModel.State.ContentReady -> drawComposeListView(it)
                }
            }
        }
    }

    @SuppressLint("CoroutineCreationDuringComposition")
    private fun drawComposeListView(state: CameraViewModel.State.ContentReady) {
        binding.cvSubtitles.setContent {
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
                    .fillMaxSize()
                    .background(
                        Color("#CC0F0F0F".toColorInt())
                    ),
                state = listState
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        cameraBinding = FragmentCameraBinding.inflate(inflater, container, false)
        cameraExecutor = Executors.newSingleThreadExecutor()
        binding.ivBackArrow.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        binding.btnRecord.setOnClickListener {
            captureVideo()
        }
        return checkNotNull(cameraBinding).root
    }

    private fun requestPermissions() {
        activityResultLauncher.launch(REQUIRED_PERMISSIONS)
    }

    private fun captureVideo() {
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

    private fun onRecordingStarted() {
        binding.ivBackArrow.isVisible = false
        binding.btnRecord.setImageDrawable(
            ContextCompat.getDrawable(
                requireActivity(),
                R.drawable.stop_recording_light
            )
        )
        cameraViewModel.startLoopingSubtitles()
    }

    private fun onRecordingStopped(recordEvent: VideoRecordEvent.Finalize) {
        cameraViewModel.stopLoopingSubtitles()
        if (!recordEvent.hasError()) {
            val msg = "Video capture succeeded: " +
                "${recordEvent.outputResults.outputUri}"
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT)
                .show()

            cameraViewModel.saveProjectToDatabase(
                recordEvent.outputResults.outputUri
            )
        } else {
            recording?.close()
            recording = null
            somethingWentWrong()
        }
        binding.ivBackArrow.isVisible = true
        binding.btnRecord.setImageDrawable(
            ContextCompat.getDrawable(requireActivity(), R.drawable.record_light_mode_button)
        )
    }

    private fun somethingWentWrong() {
        // TODO. Don't forget to handle me 👀
    }

    private fun finish() {
        val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
        val backStackEntryCount: Int = fragmentManager.backStackEntryCount
        for (i in 0 until backStackEntryCount) {
            fragmentManager.popBackStackImmediate()
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireActivity())

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
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
                    .bindToLifecycle(this, cameraSelector, preview, videoCapture)
            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(requireActivity()))
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            requireActivity().baseContext,
            it
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraBinding = null
        cameraExecutor.shutdown()
    }

    companion object {
        private const val PROJECT_ID_FRAGMENT_TAG_CAMERA = "project_id_fragment_tag_camera"
        const val TAG = "CameraXFragment"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private val REQUIRED_PERMISSIONS =
            mutableListOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()

        fun newInstance(projectId: Long): CameraFragment {
            val fragment = CameraFragment()
            val arguments = Bundle()
            arguments.putLong(PROJECT_ID_FRAGMENT_TAG_CAMERA, projectId)
            fragment.arguments = arguments
            return fragment
        }
    }
}
