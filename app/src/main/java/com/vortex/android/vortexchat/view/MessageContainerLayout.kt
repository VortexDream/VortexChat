package com.vortex.android.vortexchat.view

import android.app.usage.UsageEvents.Event.NONE
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.Log
import android.widget.FrameLayout
import android.widget.TextView
import com.vortex.android.vortexchat.R

//CustomView для сообщения с адекватным отображением времения отправки сообщения
class MessageContainerLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {
    private val textView by lazy(NONE) { getChildAt(0) as TextView }
    private val containerView by lazy(NONE) { getChildAt(1) }

    private val viewPartMainLayoutParams by lazy(NONE) { textView.layoutParams as LayoutParams }
    private val viewPartSlaveLayoutParams by lazy(NONE) { containerView.layoutParams as LayoutParams }
    private var containerWidth = 0
    private var containerHeight = 0

    var widthSize = 0
    var heightSize = 0

    private var backgroundCorneredColor: Int

    private var path = Path()
    private val cornerRadius : Float = 48f
    private val cornerPaint = Paint()

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.MessageContainerLayout,
            0, 0).apply {

            try {
                backgroundCorneredColor = getColor(R.styleable.MessageContainerLayout_backgroundCorneredColor,0x22ff0000)
                cornerPaint.color = backgroundCorneredColor
            } finally {
                recycle()
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        Log.d("CustomView", "OnMeasure() Started")
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        widthSize = MeasureSpec.getSize(widthMeasureSpec)

        if (widthSize <= 0) {
            return
        }

        val availableWidth = widthSize - paddingLeft - paddingRight
        val textViewWidth = textView.measuredWidth + viewPartMainLayoutParams.leftMargin + viewPartMainLayoutParams.rightMargin
        val textViewHeight = textView.measuredHeight + viewPartMainLayoutParams.topMargin + viewPartMainLayoutParams.bottomMargin

        containerWidth = containerView.measuredWidth + viewPartSlaveLayoutParams.leftMargin + viewPartSlaveLayoutParams.rightMargin
        containerHeight = containerView.measuredHeight + viewPartSlaveLayoutParams.topMargin + viewPartSlaveLayoutParams.bottomMargin

        val viewPartMainLineCount = textView.lineCount
        val viewPartMainLastLineWidth = if (viewPartMainLineCount > 0) textView.layout.getLineWidth(viewPartMainLineCount - 1) else 0.0f

        widthSize = paddingLeft + paddingRight
        heightSize = paddingTop + paddingBottom

        if (viewPartMainLineCount > 1 && viewPartMainLastLineWidth + containerWidth < textView.measuredWidth) {
            widthSize += textViewWidth
            heightSize += textViewHeight
        } else if (viewPartMainLineCount > 1 && viewPartMainLastLineWidth + containerWidth >= availableWidth) {
            widthSize += textViewWidth
            heightSize += textViewHeight + containerHeight
        } else if (viewPartMainLineCount == 1 && textViewWidth + containerWidth >= availableWidth) {
            widthSize += textView.measuredWidth
            heightSize += textViewHeight + containerHeight
        } else {
            widthSize += textViewWidth + containerWidth
            heightSize += textViewHeight
        }

        setMeasuredDimension(widthSize, heightSize)

        super.onMeasure(
            MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY)
        )
        Log.d("CustomView", "OnMeasure() Finished")
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        Log.d("CustomView", "OnLayout() Started")
        super.onLayout(changed, left, top, right, bottom)

        textView.layout(
            paddingLeft,
            paddingTop,
            textView.width + paddingLeft,
            textView.height + paddingTop
        )

        containerView.layout(
            right - left - containerWidth - paddingRight,
            bottom - top - paddingBottom - containerHeight,
            right - left - paddingRight,
            bottom - top - paddingBottom
        )
        Log.d("CustomView", "OnLayout() Finished")
    }

    /*override fun onDraw(canvas: Canvas) {
        Log.d("CustomView", "OnDraw() Started")
        super.onDraw(canvas)

        Log.d("MEASUREMENTS", "$widthSize, $heightSize")
        path.apply {
            moveTo(cornerRadius, 0F)
            lineTo(widthSize - cornerRadius, 0F)
            arcTo(widthSize - 2 * cornerRadius, 0F, widthSize.toFloat(), 2 * cornerRadius, -90F, 90F, false)
            lineTo(widthSize.toFloat(), cornerRadius)
            arcTo(widthSize.toFloat() - 2 * cornerRadius, heightSize - 2 * cornerRadius, widthSize.toFloat(), heightSize.toFloat(), 0F, 90F, false)
            lineTo(cornerRadius, heightSize.toFloat())
            arcTo(0F, heightSize.toFloat() - 2 * cornerRadius, 2 * cornerRadius, heightSize.toFloat(), 90F, 90F, false)
            lineTo(0F, cornerRadius)
            arcTo(0F, 0F, 2 * cornerRadius, 2 * cornerRadius, 180F, 90F, false)
        }

        canvas.drawPath(path, cornerPaint)
        Log.d("CustomView", "OnDraw() Finished")
    }*/
}