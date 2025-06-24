package com.edurda77.impuls.tele_tv.player

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Border
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import com.edurda77.impuls.tele_tv.domain.model.TvChannel
import com.edurda77.impuls.tele_tv.resources.theme.Tele_TvTheme

@Composable
fun ChannelItem(
    channel: TvChannel,
    isCurrent: Boolean,
    isFocused: Boolean,
    onSelected: () -> Unit,
    modifier: Modifier = Modifier
) {

    Card(
        modifier = modifier
            .focusable()
            .onKeyEvent {
                if (it.nativeKeyEvent.action == android.view.KeyEvent.ACTION_UP) {
                    when (it.nativeKeyEvent.keyCode) {
                        android.view.KeyEvent.KEYCODE_ENTER -> {
                            onSelected()
                        }
                    }
                }
                true
            },
        border = CardDefaults.border(
            border = Border(
                border = BorderStroke(
                    color = if (isFocused) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent,
                    width = 3.dp
                )
            )
        ),
        colors = CardDefaults.colors(
            containerColor = if (isCurrent) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surfaceVariant
        ),
        onClick = onSelected
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            channel.tvgLogo?.let {
                AsyncImage(
                    modifier = modifier.size(50.dp),
                    model = channel.tvgLogo,
                    contentDescription = "",
                    contentScale = ContentScale.Fit
                )
            }
            Spacer(modifier = modifier.width(15.dp))
            Text(
                modifier = modifier,
                text = "${channel.tvgChannelNumber}, ${channel.name}",
                style = MaterialTheme.typography.headlineMedium,
                color = if (isCurrent) MaterialTheme.colorScheme.onPrimaryContainer
                else MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Preview
@Composable
private fun ChannelItemView() {
    Tele_TvTheme {
        ChannelItem(
            channel = TvChannel(
                tvgId = "111",
                tvgLogo = "",
                tvgChannelNumber = 1,
                name = "First",
                url = ""
            ),
            isCurrent = true,
            onSelected = {},
            isFocused = false
        )
    }
}

@Preview
@Composable
private fun ChannelItemView2() {
    Tele_TvTheme {
        ChannelItem(
            channel = TvChannel(
                tvgId = "111",
                tvgLogo = "",
                tvgChannelNumber = 1,
                name = "First",
                url = ""
            ),
            isCurrent = false,
            onSelected = {},
            isFocused = true
        )
    }
}