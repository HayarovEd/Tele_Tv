package com.edurda77.impuls.radio

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ComposeShader
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.RadialGradient
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.SweepGradient
import com.chibde.BaseVisualizer
import kotlin.math.abs
import kotlin.math.ceil


class CustomVisualisation(
    context: Context
): BaseVisualizer(context) {

    override fun init() {}

    override fun onDraw(canvas: Canvas) {
        if (bytes != null) {
            val colors = (0..50).map {
                val x = ceil(it * 8.5).toInt()
                val sr = -abs(bytes[x].toInt()) + 128
                //val color = Color.rgb(sr*1.1.toFloat(), sr*1.3.toFloat(), sr*1.7.toFloat())
                blueVioletColorsInt[sr%10]
            }
            val width = width.toFloat()
            val height = height.toFloat()
            val rect = RectF(0f, 0f, width, height)
            val centerX = width / 2f
            val centerY = height / 2f
            val radius = width.coerceAtLeast(height) / 2f
            val radialGradient = RadialGradient(
                centerX,
                centerY,
                radius,
                colors.toIntArray(), // Массив цветов
                null, // Позиции цветов (равномерное распределение)
                Shader.TileMode.CLAMP // Режим заполнения
            )
            val linearGradient =
                LinearGradient(0f, 0f, width, 0f, colors.toIntArray(), null, Shader.TileMode.CLAMP)
            val sweepGradient = SweepGradient(
                centerX, centerY,  // центр
                colors.toIntArray(),  // цвета
                null  // позиции
            )

            val shader1 = radialGradient
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

val blueVioletColorsInt = listOf(
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