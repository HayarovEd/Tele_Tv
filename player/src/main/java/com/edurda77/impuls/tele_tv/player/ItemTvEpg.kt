package com.edurda77.impuls.tele_tv.player

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.edurda77.impuls.tele_tv.domain.model.TvEpg
import com.edurda77.impuls.tele_tv.resources.theme.Tele_TvTheme

@Composable
fun ItemTvEpg(
    modifier: Modifier = Modifier,
    tvEpg: TvEpg,
    currentTime: Long,
) {
    val localDensity = LocalDensity.current
    var columnHeightDp by remember {
        mutableStateOf(0.dp)
    }

    val percent =
        if (currentTime > tvEpg.start) (currentTime - tvEpg.start) / tvEpg.duration.toFloat() else 0f

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(5.dp))
    ) {
        Box(
            modifier = modifier
                .height(columnHeightDp)
                .fillMaxWidth(percent)
                .background(color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f))
        )
        Column(
            modifier = modifier
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    columnHeightDp = with(localDensity) { coordinates.size.height.toDp() }
                }
                .padding(horizontal = 10.dp, vertical = 2.dp)
        ) {
            Text(
                modifier = modifier
                    .basicMarquee(),
                text = tvEpg.title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            /*Spacer(
                modifier = modifier.height(5.dp)
            )
            tvEpg.description?.let {
                Text(
                    modifier = modifier
                        .fillMaxWidth(),
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(
                    modifier = modifier.height(5.dp)
                )
            }
            Text(
                modifier = modifier,
                text = "${stringResource(R.string.duration)}: ${duration.first} ${
                    stringResource(
                        R.string.hour_unit
                    )
                } ${duration.second} ${stringResource(R.string.minute_unit)}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimary,
            )
            tvEpg.ageRating?.let {
                Spacer(
                    modifier = modifier.height(5.dp)
                )
                Text(
                    modifier = modifier,
                    text = "${stringResource(R.string.age_rating)}: $it",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            }*/
        }
    }
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ItemTvEpgView() {
    Tele_TvTheme {
        ItemTvEpg(
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
            currentTime = 1750827721
        )
    }
}