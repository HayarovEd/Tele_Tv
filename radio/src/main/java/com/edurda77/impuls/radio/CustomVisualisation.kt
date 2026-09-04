package com.edurda77.impuls.radio

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.RectF
import android.graphics.Shader
import com.chibde.BaseVisualizer
import kotlin.math.abs
import kotlin.math.ceil


class CustomVisualisation(
    context: Context
) : BaseVisualizer(context) {

    private val rect = RectF()
    private val visualizerPaint = Paint().apply {
        isAntiAlias = true
    }
    private val colorBuffer = IntArray(51)

    override fun init() {}

    override fun onDraw(canvas: Canvas) {
        val currentBytes = bytes
        if (currentBytes != null && currentBytes.isNotEmpty()) {
            val byteSize = currentBytes.size
            val colorPaletteSize = blueVioletColorsIntArray.size
            
            for (i in 0 until 51) {
                val x = ceil(i * 8.5).toInt()
                colorBuffer[i] = if (x < byteSize) {
                    val sr = -abs(currentBytes[x].toInt()) + 128
                    blueVioletColorsIntArray[sr % colorPaletteSize]
                } else {
                    blueVioletColorsIntArray[0]
                }
            }

            val widthF = width.toFloat()
            val heightF = height.toFloat()
            rect.set(0f, 0f, widthF, heightF)
            val centerX = widthF / 2f
            val centerY = heightF / 2f
            val radius = widthF.coerceAtLeast(heightF) / 2f

            // RadialGradient is immutable, so we have to create a new one when colors change.
            // But we reuse the colorBuffer array to minimize allocations.
            visualizerPaint.shader = RadialGradient(
                centerX,
                centerY,
                radius,
                colorBuffer,
                null,
                Shader.TileMode.CLAMP
            )

            canvas.drawRect(rect, visualizerPaint)
        }
        super.onDraw(canvas)
    }
}

private val blueVioletColorsIntArray = intArrayOf(
    -0xE5DC82, // 0xFF1A237E
    -0xD7C6AD, // 0xFF283593
    -0xCFC061, // 0xFF303F9F
    -0xC6B655, // 0xFF3949AB
    -0xC0AE4B, // 0xFF3F51B5
    -0xA39440, // 0xFF5C6BC0
    -0x867935, // 0xFF7986CB
    -0x605727, // 0xFF9FA8DA
    -0x3A351A, // 0xFFC5CAE9
    -0x17150A, // 0xFFE8EAF6
    -0xCEE46E, // 0xFF311B92
    -0xBAD860, // 0xFF4527A0
    -0xADD258, // 0xFF512DA8
    -0xA1C94F, // 0xFF5E35B1
    -0x98C549, // 0xFF673AB7
    -0x81A83E, // 0xFF7E57C2
    -0x6A8A37, // 0xFF9575CD
    -0x4C6225, // 0xFFB39DDB
    -0x2E3B17, // 0xFFD1C4E9
    -0x12180A  // 0xFFEDE7F6
)