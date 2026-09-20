package com.example.glaucoscan.presentation.screens.camera

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.glaucoscan.R
import com.example.glaucoscan.presentation.screens.ModelRow
import com.example.glaucoscan.presentation.screens.ModelSheet
import com.example.glaucoscan.presentation.screens.ResultCard
import com.example.glaucoscan.presentation.theme.Blue
import java.util.concurrent.Executors


@Composable
fun CameraScreen(onBack: () -> Unit, viewModel: CameraViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showSheet by remember { mutableStateOf(false) }

    var granted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED,
        )
    }
    val requestPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted = it }
    LaunchedEffect(Unit) { if (!granted) requestPermission.launch(Manifest.permission.CAMERA) }

    Box(Modifier.fillMaxSize().background(Color.Black)) {
        if (granted) {
            CameraPreview(onFrame = viewModel::onFrame, modifier = Modifier.fillMaxSize())
        } else {
            PermissionNotice(
                onRequest = { requestPermission.launch(Manifest.permission.CAMERA) },
                modifier = Modifier.align(Alignment.Center),
            )
        }

        Box(
            Modifier.align(Alignment.TopStart).statusBarsPadding().padding(12.dp)
                .clip(CircleShape).background(Color.White.copy(alpha = 0.92f)),
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.action_back), tint = Blue.Navy)
            }
        }

        Column(
            Modifier.align(Alignment.BottomCenter).fillMaxWidth()
                .background(
                    MaterialTheme.colorScheme.background,
                    RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                )
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 20.dp),
        ) {
            when (val a = state.status) {
                is CameraState.Status.Success -> ResultCard(a.result, Modifier.fillMaxWidth())
                is CameraState.Status.Error -> Text(stringResource(a.message), color = MaterialTheme.colorScheme.error)
                else -> Text(
                    stringResource(R.string.camera_hint),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                ModelRow(state.model, onClick = { showSheet = true }, modifier = Modifier.weight(1f))
                Spacer(Modifier.width(12.dp))
                FilledTonalIconButton(
                    onClick = viewModel::onToggleLive,
                    modifier = Modifier.size(52.dp),
                    shape = MaterialTheme.shapes.small,
                ) {
                    Icon(
                        if (state.isLive) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = stringResource(if (state.isLive) R.string.camera_pause else R.string.camera_resume),
                    )
                }
            }
        }
    }

    if (showSheet) ModelSheet(state.model, viewModel::onModelSelected) { showSheet = false }
}


@Composable
private fun PermissionNotice(onRequest: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier.padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(stringResource(R.string.camera_permission_title), style = MaterialTheme.typography.headlineMedium, color = Blue.Primary)
        Spacer(Modifier.height(8.dp))
        Text(
            stringResource(R.string.camera_permission_body),
            style = MaterialTheme.typography.bodyMedium,
            color = Blue.Primary.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(20.dp))
        Button(
            onClick = onRequest,
            shape = MaterialTheme.shapes.medium,
            colors = ButtonDefaults.buttonColors(containerColor = Blue.Primary, contentColor = Blue.Tint),
        ) { Text(stringResource(R.string.camera_permission_button)) }
    }
}

@Composable
private fun CameraPreview(onFrame: (capture: () -> Bitmap) -> Unit, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context).apply { scaleType = PreviewView.ScaleType.FILL_CENTER } }
    val executor = remember { Executors.newSingleThreadExecutor() }
    val currentOnFrame by rememberUpdatedState(onFrame)

    DisposableEffect(lifecycleOwner) {
        val providerFuture = ProcessCameraProvider.getInstance(context)
        providerFuture.addListener({
            val provider = providerFuture.get()

            val preview = Preview.Builder().build().also {
                it.surfaceProvider = previewView.surfaceProvider
            }
            val analysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                .build()
                .also { useCase ->
                    useCase.setAnalyzer(executor) { proxy ->
                        proxy.use { proxy ->
                            currentOnFrame { proxy.toRotatedBitmap() }
                        }
                    }
                }

            provider.unbindAll()
            provider.bindToLifecycle(lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, preview, analysis)
        }, ContextCompat.getMainExecutor(context))

        onDispose {
            runCatching { providerFuture.get().unbindAll() }
            executor.shutdown()
        }
    }

    AndroidView(factory = { previewView }, modifier = modifier)
}

private fun ImageProxy.toRotatedBitmap(): Bitmap {
    val raw = toBitmap()
    val degrees = imageInfo.rotationDegrees
    if (degrees == 0) return raw
    val matrix = Matrix().apply { postRotate(degrees.toFloat()) }
    return Bitmap.createBitmap(raw, 0, 0, raw.width, raw.height, matrix, true).also { raw.recycle() }
}