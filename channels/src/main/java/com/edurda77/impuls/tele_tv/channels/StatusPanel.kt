package com.edurda77.impuls.tele_tv.channels

import androidx.compose.foundation.layout.Column
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
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import com.edurda77.impuls.tele_tv.resources.R
import com.edurda77.impuls.tele_tv.resources.theme.Tele_TvTheme
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.number
import kotlin.time.Clock
import kotlin.time.ExperimentalTime


@Composable
fun StatusPanel(
    modifier: Modifier = Modifier,
    currentTime: LocalDateTime,
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
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = modifier.width(10.dp))
        }
        UiIconButton(
            icon = ImageVector.vectorResource(R.drawable.outline_logout_24),
            onClick = onExitClick,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = modifier.width(10.dp))
        Text(
            modifier = modifier,
            text = "${currentTime.hour.toString().padStart(2, '0')}:${
                currentTime.minute.toString().padStart(2, '0')
            }",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Spacer(modifier = modifier.width(5.dp))
        Column {
            Text(
                modifier = modifier,
                text = "${currentTime.day.toString().padStart(2, '0')}/${
                    currentTime.month.number.toString().padStart(2, '0')
                }/${
                    currentTime.year.toString().padStart(2, '0')
                }",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                modifier = modifier,
                text = localeDayOfWeek(currentTime.dayOfWeek),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }
    }
}

@OptIn(ExperimentalTime::class)
@Preview()
@Composable
fun StatusPanelView() {
    Tele_TvTheme {
        StatusPanel(
            currentTime = Clock.System.now()
                .toLocalDateTime(TimeZone.currentSystemDefault()),
            isEnableUpdate = true,
            onUpdateClick = {},
            onExitClick = {}
        )
    }
}

private fun localeDayOfWeek(day: DayOfWeek): String {
    return when (day) {
        DayOfWeek.MONDAY -> "Понедельник"
        DayOfWeek.TUESDAY -> "Вторник"
        DayOfWeek.WEDNESDAY -> "Среда"
        DayOfWeek.THURSDAY -> "Четверг"
        DayOfWeek.FRIDAY -> "Пятница"
        DayOfWeek.SATURDAY -> "Суббота"
        DayOfWeek.SUNDAY -> "Воскресенье"
    }
}

