package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0

    private val valueAnimator = ValueAnimator.ofFloat(0f, 100f).apply {
        duration = 1000
        repeatCount = 3600
        repeatMode = ValueAnimator.REVERSE
    }

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { _, _, new ->
        when (new) {
            ButtonState.Loading -> {
                valueAnimator.start()
            }
            ButtonState.Completed -> {
                valueAnimator.end()
            }
        }
    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
    }

    private var textRect = RectF()
    private var circleRect = RectF()

    private val downloadText = context.getString(R.string.button_name)
    private val loadingText = context.getString(R.string.button_loading)

    private var mainColor = 0
    private var loadingColor = 0
    private var textColor = 0
    private var circleColor = 0

    init {
        isClickable = true


        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            mainColor = getColor(R.styleable.LoadingButton_mainColor, 0)
            loadingColor = getColor(R.styleable.LoadingButton_loadingColor, 0)
            textColor = getColor(R.styleable.LoadingButton_textColor, 0)
            circleColor = getColor(R.styleable.LoadingButton_circleColor, 0)
        }

        valueAnimator.addUpdateListener {
            invalidate()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        when (buttonState) {
            ButtonState.Completed -> {
                drawMainState(canvas)
            }
            ButtonState.Loading -> {
                drawLoadingState(canvas)
            }
        }
    }

    private fun drawMainState(canvas: Canvas) {
        canvas.drawButton(mainColor)
        canvas.drawText(textColor, downloadText)
    }

    private fun drawLoadingState(canvas: Canvas) {
        canvas.drawButton(loadingColor)
        canvas.drawLoadingButton(mainColor)
        canvas.drawText(textColor, loadingText)
        canvas.drawCircle(circleColor)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        val marginLeft = 80f
        val marginTop = 40f

        val bounds = Rect()
        paint.getTextBounds(loadingText, 0, loadingText.length, bounds);
        val startX = widthSize.toFloat() / 2f + bounds.width() / 2f + marginLeft
        val startY = heightSize.toFloat() / 2f
        circleRect = RectF(startX, marginTop, startX + marginLeft, startY + marginTop)

        textRect = RectF(0f, marginTop, widthSize.toFloat(), heightSize.toFloat())

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

    fun onDownloadingEvent() {
        isEnabled = false
        buttonState = ButtonState.Loading
    }

    fun onCompletedEvent() {
        isEnabled = true
        buttonState = ButtonState.Completed
    }

    private fun Canvas.drawText(color: Int, text: String) {
        paint.color = color
        paint.textAlign = Paint.Align.CENTER
        drawText(text, textRect.centerX(), textRect.centerY(), paint)
    }

    private fun Canvas.drawButton(color: Int) {
        paint.color = color
        drawRect(0f, 0f, widthSize.toFloat(), heightSize.toFloat(), paint)
    }

    private fun Canvas.drawLoadingButton(color: Int) {
        val progress = valueAnimator.animatedFraction * widthSize.toFloat()
        paint.color = color
        drawRect(0f, 0f, progress, heightSize.toFloat(), paint)
    }

    private fun Canvas.drawCircle(color: Int) {
        val progress = valueAnimator.animatedFraction * 360f
        paint.color = color
        drawArc(circleRect, 0f, progress, true, paint)
    }

}