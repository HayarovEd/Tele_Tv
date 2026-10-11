package com.edurda77.impuls.tele_tv.player

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import com.edurda77.impuls.tele_tv.resources.theme.Tele_TvTheme

@Composable
fun TvVolumeProgress(
    modifier: Modifier = Modifier,
    progressHeight: Dp,
    progressWidth: Dp = 6.dp,
    volume: Float,
    lineColor: Color = MaterialTheme.colorScheme.primary,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant,
) {
    Canvas(
        modifier = modifier
            .clip(RoundedCornerShape(5.dp))
            .size(progressWidth, progressHeight)
    ) {
        drawLine(
            start = Offset(0f, 0f),
            end = Offset(0f, size.height),
            color = backgroundColor,
            strokeWidth = size.width*2,
        )
        drawLine(
            start = Offset(0f, size.height * (1 - volume)),
            end = Offset(0f, size.height),
            color = lineColor,
            strokeWidth = size.width * 2,
        )
    }
}

@Preview()
@Composable
private fun TvVolumeProgressView() {
    Tele_TvTheme {
        TvVolumeProgress(
            volume = 0.2f,
            progressHeight = 300.dp
        )
    }
}