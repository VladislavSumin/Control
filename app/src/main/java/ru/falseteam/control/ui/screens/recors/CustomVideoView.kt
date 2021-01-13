package ru.falseteam.control.ui.screens.recors

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.VideoView

class CustomVideoView : VideoView {

    private var mVideoWidth: Int = 0
    private var mVideoHeight: Int = 0

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)
    constructor(context: Context) : super(context)

    fun setVideoSize(width: Int, height: Int) {
        mVideoWidth = width
        mVideoHeight = height
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var width: Int
        var height: Int
        if (mVideoWidth > 0 && mVideoHeight > 0) {
            width = View.getDefaultSize(mVideoWidth, widthMeasureSpec)
            height = View.getDefaultSize(mVideoHeight, heightMeasureSpec)

            if (mVideoWidth * height > width * mVideoHeight) {
                height = width * mVideoHeight / mVideoWidth
            } else if (mVideoWidth * height < width * mVideoHeight) {
                width = height * mVideoWidth / mVideoHeight
            }

        } else {
            width = View.getDefaultSize(mVideoWidth, widthMeasureSpec)
            height = width * 9 / 16
        }

        setMeasuredDimension(width, height)
        Log.v(TAG, "set view size w: $width, h: $height")
    }

    companion object {
        private val TAG = CustomVideoView::class.java.simpleName
    }
}