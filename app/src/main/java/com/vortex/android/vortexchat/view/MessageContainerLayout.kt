package com.vortex.android.vortexchat.view

import android.app.usage.UsageEvents.Event.NONE
import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.TextView

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

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        var widthSize = MeasureSpec.getSize(widthMeasureSpec)

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
        var heightSize = paddingTop + paddingBottom

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
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
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
    }
}