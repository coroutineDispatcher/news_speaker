package com.coroutinedispatcher.newsspeaker.ui.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class RecordButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var isRecording = false
    private val circlePaint: Paint = Paint()
    private val wrappingTransparentPaint: Paint = Paint()
    private val wrappingPeripheralCirclePaint: Paint = Paint()

    private var normalColor = Color.WHITE
    private var recordingColor = Color.RED

    init {
        circlePaint.isAntiAlias = true
        circlePaint.style = Paint.Style.FILL
        circlePaint.color = normalColor

        wrappingTransparentPaint.color = Color.TRANSPARENT
        wrappingTransparentPaint.style = Paint.Style.STROKE

        wrappingPeripheralCirclePaint.color = if (isRecording) recordingColor else normalColor
        wrappingPeripheralCirclePaint.style = Paint.Style.FILL

        isFocusable = true
        isClickable = true
    }

    override fun onDraw(canvas: Canvas) {
        val centerX = width / 2
        val centerY = height / 2
        val radius = Math.min(centerX, centerY).toFloat()

        canvas.drawCircle(centerX.toFloat(), centerY.toFloat(), radius, circlePaint)
    }

    fun setRecording(recording: Boolean) {
        isRecording = recording
        circlePaint.color = if (recording) recordingColor else normalColor
        invalidate() // Redraw the view to update the color
    }
}
