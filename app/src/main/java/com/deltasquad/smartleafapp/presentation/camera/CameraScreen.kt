package com.deltasquad.smartleafapp.presentation.camera

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import java.io.File
import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.provider.MediaStore
import androidx.compose.ui.graphics.Color
import android.graphics.Matrix
import android.view.Surface
import android.view.WindowManager
import androidx.compose.foundation.background
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import com.deltasquad.smartleafapp.presentation.theme.primaryPurple
import com.deltasquad.smartleafapp.presentation.theme.primaryWhite

@Composable
fun CameraScreen(viewModel: CameraViewModel = viewModel()) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val previewView = remember { PreviewView(context) }

    var photoUri by remember { mutableStateOf<Uri?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { previewView },
            modifier = Modifier.fillMaxSize()
        )

        // Botón de captura
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Button(
                onClick = {
                    takePhoto(context, previewView, viewModel) { uri ->
                        photoUri = uri
                        Toast.makeText(context, "📸 Foto capturada", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth(0.6f),
                colors = ButtonDefaults.buttonColors(primaryPurple)
            ) {
                Text("Clasificar flor", color = primaryWhite, fontWeight = FontWeight.Bold)
            }
        }
    }

    // Inicializar cámara
    LaunchedEffect(true) {
        val cameraProvider = cameraProviderFuture.get()
        val preview = Preview.Builder().build().apply {
            setSurfaceProvider(previewView.surfaceProvider)
        }

        val imageCapture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
            .build()

        viewModel.imageCapture = imageCapture

        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageCapture
            )
        } catch (e: Exception) {
            Log.e("CameraScreen", "Error al iniciar cámara", e)
        }
    }
}

fun takePhoto(
    context: Context,
    previewView: PreviewView,
    viewModel: CameraViewModel,
    onPhotoCaptured: (Uri) -> Unit
) {
    val imageCapture = viewModel.imageCapture ?: return
    val tempFile = File(context.cacheDir, "IMG_${System.currentTimeMillis()}.jpg")
    val outputOptions = ImageCapture.OutputFileOptions.Builder(tempFile).build()

    imageCapture.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                val uri = Uri.fromFile(tempFile)
                viewModel.onPhotoCaptured(uri)
                onPhotoCaptured(uri)

                // 🚀 Enviar imagen al servidor
                viewModel.sendImageToServerAndSaveRecord(context, uri)
            }

            override fun onError(exception: ImageCaptureException) {
                Log.e("CameraCapture", "Error al capturar", exception)
                Toast.makeText(context, "❌ Error al capturar imagen", Toast.LENGTH_SHORT).show()
            }
        }
    )
}

@Composable
fun CameraScreenEntryPoint() {
    RequestPermissions {
        CameraScreen()
    }
}
