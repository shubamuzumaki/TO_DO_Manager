package com.example.taskmanager.database.util

import android.content.Context
import android.graphics.LinearGradient
import android.graphics.Shader
import android.util.AttributeSet
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.taskmanager.R

class MyGradientTextView:TextView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet):super(context, attributeSet)

    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr:Int):super(context,attributeSet,defStyleAttr)

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        paint.shader = LinearGradient(0f,0f, width.toFloat(), height.toFloat(),
            ContextCompat.getColor(context, R.color.gradientStart),
            ContextCompat.getColor(context, R.color.gradientCentre),
            Shader.TileMode.REPEAT)
    }
}