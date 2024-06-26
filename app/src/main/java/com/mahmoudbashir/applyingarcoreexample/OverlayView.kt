package com.mahmoudbashir.applyingarcoreexample

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View

class OverlayView(context:Context,attrs:AttributeSet?) : View(context,attrs){

    private var arrowX: Float = 0f
    private var arrowY: Float = 0f

    private val paint = Paint().apply {
        color = Color.RED
        style = Paint.Style.FILL
    }

    fun updateArrowPosition(x: Float, y: Float) {
        arrowX = x
        arrowY = y
        invalidate() // This will trigger onDraw
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawArrow(canvas, arrowX, arrowY)
    }

    private fun drawArrow(canvas: Canvas, x: Float, y: Float) {
        val path = Path()
        path.moveTo(x, y - 30) // Top
        path.lineTo(x - 20, y + 30) // Bottom left
        path.lineTo(x + 20, y + 30) // Bottom right
        path.close()

        canvas.drawPath(path, paint)
    }
}