package com.example.glaucoscan.presentation.screens.gallery

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.glaucoscan.R
import com.example.glaucoscan.presentation.screens.BackBar
import com.example.glaucoscan.presentation.screens.ModelRow
import com.example.glaucoscan.presentation.screens.ModelSheet
import com.example.glaucoscan.presentation.screens.NotFundusCard
import com.example.glaucoscan.presentation.screens.ResultCard
import com.example.glaucoscan.presentation.theme.Blue

@Composable
fun GalleryScreen(onBack: () -> Unit, viewModel: GalleryViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showSheet by remember { mutableStateOf(false) }

    val picker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) viewModel.onImagePicked(uri)
    }
    val pick = { picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }

    Column(Modifier.fillMaxSize().systemBarsPadding()) {
        BackBar(stringResource(R.string.nav_gallery), onBack)

        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(20.dp)) {
            ImageFrame(state.preview, onClick = pick)
            Spacer(Modifier.height(16.dp))
            ModelRow(state.model, onClick = { showSheet = true })
            Spacer(Modifier.height(16.dp))

            when (val a = state.status) {
                GalleryState.Status.Initial -> Unit
                GalleryState.Status.Loading -> LinearProgressIndicator(
                    Modifier.fillMaxWidth().clip(CircleShape),
                    color = Blue.Primary, trackColor = MaterialTheme.colorScheme.outline,
                )
                GalleryState.Status.NotFundus -> NotFundusCard(Modifier.fillMaxWidth())
                is GalleryState.Status.Success -> ResultCard(a.result, Modifier.fillMaxWidth())
                is GalleryState.Status.Error -> Text(stringResource(a.message), color = MaterialTheme.colorScheme.error)
            }
        }

        Button(
            onClick = pick,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth().padding(20.dp).height(52.dp),
        ) {
            Text(
                stringResource(if (state.preview == null) R.string.gallery_pick_image else R.string.gallery_change_image),
                style = MaterialTheme.typography.labelLarge,
            )
        }
    }

    if (showSheet) ModelSheet(state.model, viewModel::onModelSelected) { showSheet = false }
}

@Composable
private fun ImageFrame(preview: ImageBitmap?, onClick: () -> Unit) {
    Box(
        Modifier.fillMaxWidth().aspectRatio(1f)
            .clip(MaterialTheme.shapes.medium)
            .background(if (preview == null) MaterialTheme.colorScheme.surface else Color(0xFF0D2137))
            .border(1.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.medium)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (preview == null) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Outlined.Image, contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(32.dp),
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    stringResource((R.string.gallery_empty_hint)),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        } else {
            Image(preview, stringResource((R.string.gallery_image_desc)), Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        }
    }
}