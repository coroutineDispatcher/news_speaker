package com.coroutinedispatcher.newsspeaker.ui.camera

import android.Manifest
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
import androidx.camera.video.FallbackStrategy
import androidx.camera.video.MediaStoreOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import com.coroutinedispatcher.newsspeaker.databinding.FragmentCameraBinding
import dagger.hilt.android.AndroidEntryPoint
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
        val qualitySelector = QualitySelector.fromOrderedList(
            listOf(Quality.HD),
            FallbackStrategy.lowerQualityOrHigherThan(Quality.SD)
        )
        val recorder = Recorder.Builder()
            .setExecutor(cameraExecutor).setQualitySelector(qualitySelector)
            .build()
        videoCapture = VideoCapture.withOutput(recorder)
        return checkNotNull(cameraBinding).root
    }

    private fun requestPermissions() {
        activityResultLauncher.launch(REQUIRED_PERMISSIONS)
    }

    private fun captureVideo() {
        val videoCapture = this.videoCapture ?: return

        binding.ivBackArrow.isVisible = false
        binding.btnRecord.setRecording(true)

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
                    is VideoRecordEvent.Start -> Unit
                    is VideoRecordEvent.Finalize -> {
                        if (!recordEvent.hasError()) {
                            val msg = "Video capture succeeded: " +
                                "${recordEvent.outputResults.outputUri}"
                            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT)
                                .show()
                            Log.d(TAG, msg)
                            binding.ivBackArrow.isVisible = true
                            binding.btnRecord.setRecording(false)
                            finish()
                        } else {
                            recording?.close()
                            recording = null
                            Log.e(
                                TAG,
                                "Video capture ends with error: " +
                                    "${recordEvent.error}"
                            )
                        }
                    }
                }
            }
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
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(requireNotNull(cameraBinding).viewFinder.surfaceProvider)
                }

            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            try {
                cameraProvider.unbindAll()

                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview
                )
            } catch (exc: Exception) {
                Log.d(TAG, "startCamera: ${exc.printStackTrace()}")
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
