package cn.wankkoree.xp.webviewpp.activity.component

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.appcompat.widget.LinearLayoutCompat
import com.google.android.material.textview.MaterialTextView
import androidx.annotation.ColorInt
import androidx.cardview.widget.CardView
import cn.wankkoree.xp.webviewpp.R

class Tag: LinearLayoutCompat {
    private var cardView: CardView? = null
    private var textView: MaterialTextView? = null

    @ColorInt
    var color: Int? = null
        set(value) {
            field = value
            cardView?.setCardBackgroundColor(value!!)
        }
    var text: CharSequence? = null
        set(value) {
            field = value
            textView?.text = value
        }

    constructor(context: Context): this(context, null)
    constructor(context: Context, attrs: AttributeSet?): this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int): super(context, attrs, defStyleAttr) {
        init(context)
        attrs?.let { retrieveAttributes(attrs) }
    }

    private fun init(context: Context) {
        val view = LayoutInflater.from(context).inflate(R.layout.component_tag,this)
        cardView = view.findViewById(R.id.component_tag_card)
        textView = view.findViewById(R.id.component_tag_text)
    }

    private fun retrieveAttributes(attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.Tag)
        color = typedArray.getColor(R.styleable.Tag_color, 0)
        text = typedArray.getText(R.styleable.Tag_text)
        typedArray.recycle()
    }
}