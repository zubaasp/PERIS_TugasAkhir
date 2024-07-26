package com.example.peris.customview

import android.content.Context
import android.graphics.Canvas
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import com.example.peris.R
import com.google.android.material.textfield.TextInputEditText


class PasswordEditText : TextInputEditText {

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
    }

    private fun init() {
        addTextChangedListener(object : TextWatcher { //TextWatcher biar bisa bikin kondisi beforeTextChanged, onTextChanged, afterTextChanged
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {//CharSequence itu isinya text, count d sini p2

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.toString().length < 8) {
                    setError(context.getString(R.string.error_password), null)//klo null menampilkan erroryna skedar nampilin text doang dan nampilin ikon tanda seru
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })
    }
}