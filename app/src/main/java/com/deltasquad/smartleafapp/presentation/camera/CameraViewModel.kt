package com.deltasquad.smartleafapp.presentation.camera

import android.app.Application
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.camera.core.ImageCapture
import androidx.lifecycle.AndroidViewModel
import androidx.exifinterface.media.ExifInterface
import com.deltasquad.smartleafapp.data.api.RetrofitClient
import com.deltasquad.smartleafapp.data.model.FlowerResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import androidx.core.graphics.scale

class CameraViewModel(application: Application) : AndroidViewModel(application) {

    var imageCapture: ImageCapture? = null

    private val _photoUri = MutableStateFlow<Uri?>(null)
    val photoUri: StateFlow<Uri?> = _photoUri

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun onPhotoCaptured(uri: Uri) {
        _photoUri.value = uri
    }

    /** Guarda el bitmap en la galería y devuelve su URI */
    fun saveBitmapToGallery(context: Context, bitmap: Bitmap, name: String): Uri? {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/SmartLeafApp")
            }
        }

        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        uri?.let {
            resolver.openOutputStream(it)?.use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            }
        }

        return uri
    }

    /** Envía la imagen al servidor y guarda el resultado en Firestore */
    fun sendImageToServerAndSaveRecord(context: Context, imageUri: Uri) {
        CoroutineScope(Dispatchers.IO).launch {
            val contentResolver = context.contentResolver
            var tempFile: File? = null

            try {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "📤 Enviando imagen al servidor...", Toast.LENGTH_SHORT).show()
                }

                // 1️⃣ Crear un archivo temporal para trabajar
                tempFile = File.createTempFile("flower_", ".jpg", context.cacheDir)

                // 2️⃣ Copiar la imagen de CameraX al archivo temporal
                contentResolver.openInputStream(imageUri)?.use { input ->
                    tempFile!!.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }

                // 3️⃣ Leer bitmap desde el archivo temporal
                var bitmap = BitmapFactory.decodeFile(tempFile!!.absolutePath)
                if (bitmap == null) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "❌ No se pudo leer la imagen", Toast.LENGTH_LONG).show()
                    }
                    return@launch
                }

                // 4️⃣ Rotar bitmap según EXIF
                bitmap = rotateBitmapIfRequired(tempFile, bitmap)

                // 5️⃣ Guardar la foto original en la galería
                val savedUri = saveBitmapToGallery(context, bitmap, "flower_${System.currentTimeMillis()}")

                // 6️⃣ Redimensionar para enviar al servidor
                val resizedBitmap = bitmap.scale(224, 224)

                // 7️⃣ Guardar temporalmente el bitmap redimensionado
                val serverFile = File.createTempFile("flower_resized_", ".jpg", context.cacheDir)
                serverFile.outputStream().use { out ->
                    resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
                }

                // 8️⃣ Preparar cuerpo para Retrofit
                val requestFile = serverFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("image", serverFile.name, requestFile)

                // 9️⃣ Llamada al servidor
                val response = RetrofitClient.api.classifyFlower(body)

                if (response.isSuccessful) {
                    val result = response.body()
                    val predictedLabel = result?.class_supervised ?: "Desconocido"
                    val confidence = result?.confidence ?: 0.0f
                    val clusterId = result?.cluster_id ?: -1
                    val classCluster = result?.class_cluster ?: "Desconocido"

                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "🌸 Flor detectada: $predictedLabel (Confianza: ${(confidence * 100).toInt()}%)",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    // 🔹 Guardar en Firestore
                    val user = auth.currentUser
                    val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
                    if (user != null) {
                        val record = FlowerResponse(
                            success = true,
                            class_supervised = predictedLabel,
                            confidence = confidence,
                            cluster_id = clusterId,
                            class_cluster = classCluster,
                            userId = user.uid,
                            timestamp = timestamp,
                            imageUrl = savedUri?.toString()
                        )

                        db.collection("users")
                            .document(user.uid)
                            .collection("flowers")
                            .add(record)
                            .addOnSuccessListener {
                                Log.d("Firestore", "Registro guardado correctamente")
                            }
                            .addOnFailureListener {
                                Log.e("Firestore", "Error al guardar registro", it)
                            }
                    }

                } else {
                    val errorMsg = response.errorBody()?.string()
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "❌ Error servidor: $errorMsg", Toast.LENGTH_LONG).show()
                    }
                }

            } catch (e: Exception) {
                Log.e("CameraViewModel", "Error al enviar imagen", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "❌ Error al enviar imagen: ${e.message}", Toast.LENGTH_LONG).show()
                }
            } finally {
                tempFile?.delete()
            }
        }
    }

    /** Rota el bitmap según los datos EXIF del archivo */
    fun rotateBitmapIfRequired(file: File, bitmap: Bitmap): Bitmap {
        val exif = ExifInterface(file.absolutePath)
        val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        val matrix = Matrix()

        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
            else -> return bitmap
        }

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
}
