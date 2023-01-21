package cn.wankkoree.xp.webviewpp.activity.component

import android.content.Context
import android.text.Html
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.widget.HorizontalScrollView
import com.google.android.material.textview.MaterialTextView
import cn.wankkoree.xp.webviewpp.R

class Code : HorizontalScrollView {
    private lateinit var codeView : MaterialTextView

    var code : String = ""
        set(value) {
            field = value
            codeView.text = Html.fromHtml(value, Html.FROM_HTML_MODE_LEGACY)
        }

    constructor(context : Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?): this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int): super(context, attrs, defStyleAttr) {
        init(context)
        attrs?.let { retrieveAttributes(attrs) }
    }

    private fun init(context: Context) {
        val view = LayoutInflater.from(context).inflate(R.layout.component_code,this)
        codeView = view.findViewById(R.id.component_code_code)
    }

    private fun retrieveAttributes(attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.Code)
        code = typedArray.getString(R.styleable.Code_code)!!
        typedArray.recycle()
    }

    override fun setClickable(clickable: Boolean) {
        codeView.isClickable = clickable
        if (clickable) {
            val outValue = TypedValue()
            this.context.theme.resolveAttribute(android.R.attr.selectableItemBackground, outValue, true)
            codeView.setBackgroundResource(outValue.resourceId)
        } else {
            codeView.setBackgroundResource(0)
        }
    }

    override fun setOnClickListener(l: OnClickListener?) = codeView.setOnClickListener(l)

    override fun setOnLongClickListener(l: OnLongClickListener?) = codeView.setOnLongClickListener(l)
}