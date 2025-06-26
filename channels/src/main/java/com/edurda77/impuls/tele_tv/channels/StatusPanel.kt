package com.edurda77.impuls.tele_tv.channels

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.edurda77.impuls.tele_tv.resources.uikit.UiIconButton
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import com.edurda77.impuls.tele_tv.resources.R
import com.edurda77.impuls.tele_tv.resources.theme.Tele_TvTheme


@Composable
fun StatusPanel(
    modifier: Modifier = Modifier,
    currentTime: LocalTime,
    isEnableUpdate: Boolean,
    onUpdateClick: () -> Unit,
    onExitClick: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isEnableUpdate) {
            UiIconButton(
                icon = ImageVector.vectorResource(R.drawable.baseline_update_24),
                onClick = onUpdateClick,
                color = MaterialTheme.colorScheme.tertiary
            )
            Spacer(modifier = modifier.width(10.dp))
        }
        UiIconButton(
            icon = ImageVector.vectorResource(R.drawable.outline_logout_24),
            onClick = onExitClick,
            color = MaterialTheme.colorScheme.tertiary
        )
        Spacer(modifier = modifier.width(10.dp))
        Text(
            modifier = modifier,
            text = "${currentTime.hour.toString().padStart(2, '0')}:${
                currentTime.minute.toString().padStart(2, '0')
            }",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.tertiary,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun StatusPanelView() {
    Tele_TvTheme {
        StatusPanel(
            currentTime = Clock.System.now()
                .toLocalDateTime(TimeZone.currentSystemDefault()).time,
            isEnableUpdate = true,
            onUpdateClick = {},
            onExitClick = {}
        )
    }
}