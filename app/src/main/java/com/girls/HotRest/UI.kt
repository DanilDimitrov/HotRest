package com.girls.HotRest

import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.widget.TextView

class UI {
    fun textApp(textView: TextView, text: String): LinearGradient {

        textView.text = text

        val paint = textView.paint
        val widthText = paint.measureText("ReImage")

        return LinearGradient(
            0f, 0f, widthText, textView.textSize,
            intArrayOf(
                Color.parseColor("#FD559A"),
                Color.parseColor("#ff2d55"),
                Color.parseColor("#ff9f0a"),

                ), null, Shader.TileMode.CLAMP
        )
    }
}