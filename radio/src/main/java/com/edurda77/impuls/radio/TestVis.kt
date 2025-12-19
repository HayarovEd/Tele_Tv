package com.edurda77.impuls.radio

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.RectF
import android.graphics.Shader
import com.chibde.BaseVisualizer
import kotlin.math.abs
import kotlin.math.ceil


class CustomVis(
    context: Context
): BaseVisualizer(context) {

    override fun init() {}

    override fun onDraw(canvas: Canvas) {
        if (bytes != null) {
            val colors = (0..50).map {
                val x = ceil(it * 8.5).toInt()
                val sr = -abs(bytes[x].toInt()) + 128
                val color = Color.rgb(127f, sr*1.3.toFloat(), 127f)
                color
            }
            val width = width.toFloat()
            val height = height.toFloat()
            val rect = RectF(0f, 0f, width, height)
            val centerX = width / 2f
            val centerY = height / 2f
            val radius = width.coerceAtLeast(height) / 2f
            val shader1 = RadialGradient(
                centerX,
                centerY,
                radius,
                colors.toIntArray(), // Массив цветов
                null, // Позиции цветов (равномерное распределение)
                Shader.TileMode.CLAMP // Режим заполнения
            )

            val paint = Paint().apply {
                shader = shader1
                isAntiAlias = true
            }

            // Рисуем прямоугольник
            canvas.drawRect(rect, paint)
        }
        super.onDraw(canvas)
    }
}