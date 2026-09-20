package com.example.glaucoscan.presentation.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.glaucoscan.R
import com.example.glaucoscan.presentation.screens.AppCard
import com.example.glaucoscan.presentation.screens.ModelRow
import com.example.glaucoscan.presentation.screens.ModelSheet
import com.example.glaucoscan.presentation.theme.Blue

@Composable
fun HomeScreen(
    onOpenGallery: () -> Unit,
    onOpenCamera: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showSheet by remember { mutableStateOf(false) }

    Column(
        Modifier.fillMaxSize().systemBarsPadding().padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.weight(1f))
        Text(
            text = "Glauco",
            style = MaterialTheme.typography.displayLarge,
            modifier = Modifier.offset(x = (-40).dp)
        )

        Text(
            text = "Scan",
            style = MaterialTheme.typography.displayLarge,
            modifier = Modifier.offset(x = 25.dp)
        )
        Spacer(Modifier.height(48.dp))

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            MenuTile(
                modifier = Modifier.weight(1f),
                icon = Icons.Outlined.Image,
                label = stringResource((R.string.nav_gallery)),
                onClick = onOpenGallery
            )

            Spacer(Modifier.width(24.dp))

            MenuTile(
                modifier = Modifier.weight(1f),
                icon = Icons.Outlined.PhotoCamera,
                label = stringResource((R.string.nav_camera)),
                onClick = onOpenCamera
            )
        }

        Spacer(Modifier.height(24.dp))
        ModelRow(state, onClick = { showSheet = true })

        Spacer(Modifier.weight(1f))
        Spacer(Modifier.height(24.dp))
    }

    if (showSheet) ModelSheet(state, viewModel::onModelSelected) { showSheet = false }
}

@Composable
fun MenuTile(
    icon: ImageVector, label: String, onClick: () -> Unit, modifier: Modifier = Modifier
){
   AppCard(modifier.height(180.dp), onClick=onClick) {
       Column(Modifier.padding(18.dp).fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
           Box(
               modifier = Modifier
                   .size(96.dp)
                   .clip(RoundedCornerShape(12.dp))
                   .background(MaterialTheme.colorScheme.primaryContainer),
               contentAlignment = Alignment.Center
           ){
               Icon(
                   icon,
                   contentDescription = null,
                   modifier = Modifier.size(48.dp),
                   tint = Blue.Primary,
               )
           }
           Spacer(Modifier.weight(1f))
           Text(label, style = MaterialTheme.typography.titleMedium)
       }
   }
}