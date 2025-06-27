package com.edurda77.impuls.tele_tv.player

import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Border
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import com.edurda77.impuls.tele_tv.domain.model.TvEpg
import com.edurda77.impuls.tele_tv.domain.utils.calculateHoursMins
import com.edurda77.impuls.tele_tv.resources.theme.Tele_TvTheme

@Composable
fun ItemFullTvEpg(
    modifier: Modifier = Modifier,
    tvEpg: TvEpg,
) {
    Surface(
        onClick = {},
        modifier = modifier
            .fillMaxWidth()
            .onFocusChanged {
                if (it.hasFocus) {
                    Log.d("REST TELE TV", "tvEpg ${it.isFocused}")
                }
            },
        colors = ClickableSurfaceDefaults.colors(
            containerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
        ),
        border = ClickableSurfaceDefaults.border(
            focusedBorder = Border(
                border = BorderStroke(
                    width = 3.dp,
                    color = MaterialTheme.colorScheme.secondaryContainer
                ),
                inset = 2.dp
            )
        ),
        scale = ClickableSurfaceDefaults.scale(
            focusedScale = 1.05f
        )
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = modifier.weight(2f),
                text = "(${tvEpg.start.calculateHoursMins()}-${tvEpg.stop.calculateHoursMins()})",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Column(
                modifier = modifier.weight(5f),
            ) {
                Text(
                    modifier = modifier
                        .fillMaxWidth(),
                    text = tvEpg.title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )
                tvEpg.description?.let {
                    Spacer(modifier = modifier.height(5.dp))
                    Text(
                        modifier = modifier
                            .fillMaxWidth(),
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                }
            }
            Box(
                modifier = modifier
                    .weight(1f)
            ) {
                Text(
                    modifier = modifier
                        .align(Alignment.Center)
                        .background(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = CircleShape
                        )
                        .padding(10.dp),
                    text = "${tvEpg.ageRating}+",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                )
            }
        }
    }
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun ItemTvEpgView() {
    Tele_TvTheme {
        ItemFullTvEpg(
            tvEpg = TvEpg(
                channelName = "Рифей (22)",
                channelNumber = "22",
                channelUuid = "bb96aabb80ee7139983ed081a341d148",
                ageRating = 16,
                description = "Девочка Маша и Медведь — неразлучные друзья. В голову озорной Маши всегда приходят самые невероятные идеи, и поэтому каждый день героев наполнен весельем, приключениями и новыми открытиями.",
                eventId = 464049,
                start = 1750806000,
                stop = 1750845600,
                title = "Новости на Рифее."
            ),
        )
    }
}