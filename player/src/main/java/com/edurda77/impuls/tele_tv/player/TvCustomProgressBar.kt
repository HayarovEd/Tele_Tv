package com.edurda77.impuls.tele_tv.player

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateValue
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.progressSemantics
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import com.edurda77.impuls.tele_tv.resources.theme.Tele_TvTheme
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.max


internal val trackThickness = 4.0.dp
internal val CircularIndicatorDiameter =
    48.dp - trackThickness * 2
private const val BaseRotationAngle = 286f

private const val JumpRotationAngle = 290f
private const val RotationDuration =3000
private const val RotationAngleOffset = (BaseRotationAngle + JumpRotationAngle) % 360f
private const val HeadAndTailAnimationDuration = (RotationDuration * 0.5).toInt()
private const val HeadAndTailDelayDuration = HeadAndTailAnimationDuration
private val CircularEasing = CubicBezierEasing(0.4f, 0f, 0.2f, 1f)
private const val RotationsPerCycle = 5
private const val StartAngleOffset = -90f


@Composable
fun TvCustomCircularProgressIndicator(
    modifier: Modifier = Modifier,
    strokeWidth: Dp = 6.dp,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    progressColor: Color = MaterialTheme.colorScheme.primary,
    strokeCap: StrokeCap = StrokeCap.Round
) {

    val stroke = with(LocalDensity.current) { Stroke(width = strokeWidth.toPx(), cap = strokeCap) }
    val transition = rememberInfiniteTransition()
    val currentRotation =
        transition.animateValue(
            0,
            RotationsPerCycle,
            Int.VectorConverter,
            infiniteRepeatable(
                animation =
                    tween(
                        durationMillis = RotationDuration * RotationsPerCycle,
                        easing = LinearEasing
                    )
            )
        )
    val baseRotation =
        transition.animateFloat(
            0f,
            BaseRotationAngle,
            infiniteRepeatable(
                animation = tween(durationMillis = RotationDuration, easing = LinearEasing)
            )
        )
    val endAngle =
        transition.animateFloat(
            0f,
            JumpRotationAngle,
            infiniteRepeatable(
                animation =
                    keyframes {
                        durationMillis = HeadAndTailAnimationDuration + HeadAndTailDelayDuration
                        0f at 0 using CircularEasing
                        JumpRotationAngle at HeadAndTailAnimationDuration
                    }
            )
        )
    val startAngle =
        transition.animateFloat(
            0f,
            JumpRotationAngle,
            infiniteRepeatable(
                animation =
                    keyframes {
                        durationMillis = HeadAndTailAnimationDuration + HeadAndTailDelayDuration
                        0f at HeadAndTailDelayDuration using CircularEasing
                        JumpRotationAngle at durationMillis
                    }
            )
        )
    val currentRotationAngleOffset = (currentRotation.value * RotationAngleOffset) % 360f
    val sweep = abs(endAngle.value - startAngle.value)
    val offset = StartAngleOffset + currentRotationAngleOffset + baseRotation.value
    val strokeCapOffset =
        if (stroke.cap == StrokeCap.Butt) {
            0f
        } else {
            (180.0 / PI).toFloat() * (strokeWidth / (CircularIndicatorDiameter / 2)) / 2f
        }
    val adjustedStartAngle = startAngle.value + offset + strokeCapOffset

    val adjustedSweep = max(sweep, 0.1f)


    Canvas(
        modifier = modifier
            .progressSemantics()
            .size(CircularIndicatorDiameter)
    ) {
        val diameterOffset = stroke.width / 2
        val arcDimen = size.width - 2 * diameterOffset
        drawArc(
            color = backgroundColor,
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = Offset(diameterOffset, diameterOffset),
            size = Size(arcDimen, arcDimen),
            style = stroke
        )
        drawArc(
            color = progressColor,
            startAngle = adjustedStartAngle,
            sweepAngle = adjustedSweep,
            useCenter = false,
            topLeft = Offset(diameterOffset, diameterOffset),
            size = Size(arcDimen, arcDimen),
            style = stroke
        )
    }
}

@Preview
@Composable
private  fun TvCustomCircularProgressIndicatorView() {
    Tele_TvTheme {
        TvCustomCircularProgressIndicator()
    }
}