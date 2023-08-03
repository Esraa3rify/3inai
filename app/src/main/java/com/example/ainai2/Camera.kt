package com.example.ainai2


import android.bluetooth.BluetoothAdapter.ERROR
import android.content.Context
import android.content.pm.PackageManager
import android.opengl.Visibility
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.LayoutInflater
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.style.TextOverflow.Companion.Visible
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import kotlinx.coroutines.delay
import java.util.Locale
import java.util.concurrent.Executors


class Camera : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            // A surface container using the 'background' color from the theme
            Surface {
                TextRecognizer()
            }
        }
    }
}


@Composable
fun TextRecognizer() {
    var isVisible by remember { mutableStateOf(true) }
    val extractedText = remember { mutableStateOf("") }

    Box() {

        CameraPreview(extractedText)


        LaunchedEffect(key1 = true) {
            delay(30000L)
        }

        text_to_speech(extractedText)


        if (!isVisible) {
            Text(
                text = extractedText.value,


                )
        }
    }
}


@Composable
fun isCameraPermissionGranted(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context, android.Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED
}

@Composable
fun requestCameraPermission(cameraPermissionLauncher: ActivityResultLauncher<String>) {
    SideEffect {
        cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
    }

}


@Composable
fun CameraPreview(extractedText: MutableState<String>) {


    val lifeCycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current


    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    val textRecognizer = remember { TextRecognition.getClient() }

    val previewView = remember {
        PreviewView(context).apply {
            id = R.id.previewView
        }
    }

    // Remember the launcher for requesting the camera permission
    val cameraPermissionLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission(),
            onResult = { isGranted ->
                if (isGranted) {
                    // Camera permission was granted. Access the camera here.
                } else {
                    // Camera permission was denied. Handle the case when the user denies the permission.
                }
            })

    if (isCameraPermissionGranted(context)) {
        // Camera permission is already granted. Show the camera preview.
        AndroidView(
            factory = { context ->
                // Inflate the XML layout

                LayoutInflater.from(context).inflate(R.layout.camera, previewView)
            }, modifier = Modifier
                .fillMaxSize()
                .wrapContentSize()
        ) {


            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val preview = androidx.camera.core.Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.createSurfaceProvider())
                }

                val imageAnalyzer = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build().also {
                        it.setAnalyzer(
                            cameraExecutor,
                            ObjectDetectorImageAnalyzer(textRecognizer, extractedText)
                        )
                    }

                // Select back camera as a default
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                try {
                    // Unbind use cases before rebinding
                    cameraProvider.unbindAll()

                    // Bind use cases to camera
                    cameraProvider.bindToLifecycle(
                        lifeCycleOwner, cameraSelector, preview, imageAnalyzer
                    )
                } catch (exc: Exception) {
                    Log.e("CAMERA PREVIEW", "Use case binding failed", exc)
                }

            }, ContextCompat.getMainExecutor(context))
        }
    } else {
        // Camera permission is not granted. Request it.
        requestCameraPermission(cameraPermissionLauncher)
    }
}


class ObjectDetectorImageAnalyzer(
    private val textRecognizer: TextRecognizer, private val extractedText: MutableState<String>
) : ImageAnalysis.Analyzer {

    override fun analyze(imageProxy: ImageProxy) {

        val mediaImage = imageProxy.image

        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            textRecognizer.process(image).addOnCompleteListener {
                if (it.isSuccessful) {
                    extractedText.value = it.result?.text ?: ""

                }

                //TO AVOID: com.google.mlkit.common.MlKitException: Internal error has occurred when executing ML Kit tasks
                imageProxy.close()
            }
        }
    }
}


private lateinit var textToSpeech: TextToSpeech

@Composable
fun text_to_speech(extractedText: MutableState<String>) {


    val applicationContext = LocalContext.current.applicationContext

    textToSpeech = remember {

        TextToSpeech(applicationContext) { status ->
            // if No error is found then only it will run
            if (status != ERROR) {
                // To Choose language of speech
                textToSpeech.language = Locale.UK
                //textToSpeech.speak(extractedText.toString(), TextToSpeech.QUEUE_FLUSH, null, null)

                // Use LaunchedEffect to trigger speech when extractedText changes


            }
        }
    }



    textToSpeech.speak(extractedText.value, TextToSpeech.QUEUE_FLUSH, null, null)


    // Don't forget to handle cleanup when the composable is no longer needed.
    DisposableEffect(Unit) {
        onDispose {
            textToSpeech.shutdown()
        }
    }

}





