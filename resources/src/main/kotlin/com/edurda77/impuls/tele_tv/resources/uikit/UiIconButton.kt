package com.edurda77.impuls.tele_tv.resources.uikit

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.tv.material3.Icon
import androidx.tv.material3.IconButton
import androidx.tv.material3.IconButtonDefaults
import androidx.tv.material3.MaterialTheme

@Composable
fun UiIconButton(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    enabled: Boolean = true,
    color: Color = MaterialTheme.colorScheme.secondary,
    buttonColor: Color = Color.Transparent,
    onClick: () -> Unit
) {
    IconButton (
        modifier = modifier,
        enabled = enabled,
        colors = IconButtonDefaults.colors(
            containerColor = buttonColor
        ),
        onClick = onClick
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "",
            tint = color
        )
    }
}