package com.example.glaucoscan.presentation.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.glaucoscan.R
import com.example.glaucoscan.domain.models.ClassificationResult
import com.example.glaucoscan.domain.models.Dataset
import com.example.glaucoscan.domain.models.GlaucomaLabel
import com.example.glaucoscan.domain.models.ModelConfig
import com.example.glaucoscan.domain.models.Variant
import com.example.glaucoscan.presentation.theme.Blue
import java.util.Locale

@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val base = modifier
        .clip(MaterialTheme.shapes.medium)
        .background(MaterialTheme.colorScheme.surface)
        .border(1.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.medium)
    Column(
        modifier = if (onClick != null) base.clickable(onClick = onClick) else base,
        content = content,
    )
}

@Composable
fun ResultCard(result: ClassificationResult, modifier: Modifier = Modifier) {
    val locale = LocalConfiguration.current.locales[0]
    val positive = result.label == GlaucomaLabel.GLAUCOMA
    val accent = if (positive) Blue.Alert else Blue.Primary
//    val id = Locale.forLanguageTag("id-ID")
    val pct = result.glaucomaProbability * 100
    val animated by animateFloatAsState(
        targetValue = result.glaucomaProbability.coerceIn(0f, 1f),
        animationSpec = tween(600, easing = FastOutSlowInEasing),
        label = "prob",
    )

    AppCard(modifier) {
        Column(Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.clip(RoundedCornerShape(8.dp))
                        .background(accent.copy(alpha = 0.10f))
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                ) {
                    Text(
                        stringResource(if (positive) R.string.result_glaucoma else R.string.result_normal),
                        style = MaterialTheme.typography.labelMedium, color = accent,
                    )
                }
                Spacer(Modifier.weight(1f))
                Text(
                    "%.0f ms".format(locale, result.latencyMs),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Spacer(Modifier.height(14.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text("%.1f".format(locale, pct), style = MaterialTheme.typography.displayLarge, color = accent)
                Text(
                    "%", style = MaterialTheme.typography.headlineSmall, color = accent,
                    modifier = Modifier.padding(start = 2.dp, bottom = 7.dp),
                )
            }
            Text(
                stringResource(R.string.result_probability),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(Modifier.height(16.dp))
            Box(
                Modifier.fillMaxWidth().height(6.dp)
                    .clip(CircleShape).background(MaterialTheme.colorScheme.outline),
            ) {
                Box(
                    Modifier.fillMaxWidth(animated).fillMaxHeight()
                        .clip(CircleShape).background(accent),
                )
            }
        }
    }
}

@Composable
fun ModelRow(model: ModelConfig, onClick: () -> Unit, modifier: Modifier = Modifier) {
    AppCard(modifier.fillMaxWidth(), onClick = onClick) {
        Row(
            Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    stringResource(R.string.model_label), style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(2.dp))
                Text(model.displayName, style = MaterialTheme.typography.titleMedium)
            }
            Icon(
                Icons.Default.KeyboardArrowDown, contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModelSheet(selected: ModelConfig, onSelect: (ModelConfig) -> Unit, onDismiss: () -> Unit) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.background,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
    ) {
        Column(Modifier.padding(start = 20.dp, end = 20.dp, bottom = 28.dp)) {
            Text(stringResource(R.string.model_pick_title), style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(16.dp))

            Row(
                Modifier.fillMaxWidth().clip(MaterialTheme.shapes.small)
                    .background(MaterialTheme.colorScheme.surfaceVariant).padding(4.dp),
            ) {
                Dataset.entries.forEach { ds ->
                    val active = ds == selected.dataset
                    Box(
                        Modifier.weight(1f).clip(RoundedCornerShape(9.dp))
                            .background(if (active) MaterialTheme.colorScheme.surface else Color.Transparent)
                            .clickable { onSelect(selected.copy(dataset = ds)) }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            ds.label, style = MaterialTheme.typography.labelMedium,
                            color = if (active) Blue.Primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
            Variant.entries.forEach { v ->
                val active = v == selected.variant
                Row(
                    Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        .clip(MaterialTheme.shapes.small)
                        .background(if (active) MaterialTheme.colorScheme.surfaceVariant else Color.Transparent)
                        .clickable { onSelect(selected.copy(variant = v)) }
                        .padding(horizontal = 14.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(v.label, Modifier.weight(1f), style = MaterialTheme.typography.titleMedium)
                    if (active) Icon(Icons.Default.Check, null, tint = Blue.Primary)
                }
            }
        }
    }
}

@Composable
fun BackBar(title: String, onBack: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(start = 4.dp, end = 20.dp, top = 4.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.action_back))
        }
        Text(title, style = MaterialTheme.typography.headlineSmall)
    }
}

@Composable
fun NotFundusCard(modifier: Modifier = Modifier) {
    AppCard(modifier) {
        Column(Modifier.padding(20.dp)) {
            Text(stringResource(R.string.not_fundus_title), style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(4.dp))
            Text(
                stringResource(R.string.not_fundus_body),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}