package com.edurda77.impuls.tele_tv.player

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Border
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.SurfaceDefaults
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import com.edurda77.impuls.tele_tv.domain.model.TvChannel
import com.edurda77.impuls.tele_tv.resources.theme.Tele_TvTheme


@Composable
fun DrumMenu(
    modifier: Modifier = Modifier,
    channels: List<TvChannel>,
    selectedIndex: Int,
    baseTextSize: TextUnit = 30.sp,
) {
    val scrollState = rememberLazyListState()
    var countVisible by remember { mutableIntStateOf(0) }
    var firstVisibleItemIndex by remember { mutableIntStateOf(0) }
    val configuration = LocalWindowInfo.current.containerSize
    val screenWidth = configuration.height.dp

    LaunchedEffect(selectedIndex) {
        if (selectedIndex >= 0) {
            firstVisibleItemIndex = scrollState.firstVisibleItemIndex
            countVisible = scrollState.layoutInfo.visibleItemsInfo.size
            val scrolledIndex = when (selectedIndex) {
                in 0..<countVisible / 2 -> 0
                else -> selectedIndex - countVisible / 2
            }
            scrollState.scrollToItem(scrolledIndex)
        }
    }

    LazyRow (
        //state = scrollState,
        modifier = modifier
          //  .background(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
            .fillMaxWidth()
            .padding(16.dp),
        state = scrollState,
        horizontalArrangement = Arrangement.spacedBy(15.dp)
    ) {

        itemsIndexed(channels) { index, channel ->
            Surface(
                colors = SurfaceDefaults.colors(
                    containerColor = if (index == selectedIndex) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(
                        alpha = 0.5f
                    ),
                ),
                shape = RoundedCornerShape(16.dp),
                border = Border(
                    border = BorderStroke(
                        width = 3.dp,
                        color = if (index == selectedIndex) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent
                    ),
                    inset = 4.dp
                )
            ) {
                Row(
                    modifier = modifier.width(screenWidth/5),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    channel.tvgLogo?.let {
                        AsyncImage(
                            modifier = modifier.size(30.dp),
                            model = channel.tvgLogo,
                            contentDescription = "",
                            contentScale = ContentScale.Fit
                        )
                        Spacer(modifier = modifier.width(15.dp))
                    }
                    Text(
                        modifier = modifier
                            .padding(vertical = 10.dp)
                            .basicMarquee(),
                        text = channel.tvgChno,
                        fontSize = baseTextSize,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Preview(
  //  showBackground = true
)
@Composable
private fun DrumMenuView() {
    val channels = (0..15).map {
        TvChannel(
            tvgId = "www",
            tvgLogo = "",
            tvgChno = "$it channel",
            name = "channel",
            url = ""
        )
    }
    Tele_TvTheme {
        DrumMenu(
            channels = channels,
            selectedIndex = 4,
        )
    }
}