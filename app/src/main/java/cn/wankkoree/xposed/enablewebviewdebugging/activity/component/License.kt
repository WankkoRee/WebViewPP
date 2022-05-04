package cn.wankkoree.xposed.enablewebviewdebugging.activity.component

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.cardview.widget.CardView
import cn.wankkoree.xposed.enablewebviewdebugging.R
import cn.wankkoree.xposed.enablewebviewdebugging.activity.colorStateSingle

class License: LinearLayout {
    private lateinit var cardView: CardView
    private lateinit var iconView: ImageView
    private lateinit var titleView: TextView
    private lateinit var descView: TextView

    var icon: Drawable? = null
        set(value) {
            field = value
            iconView.setImageDrawable(value)
        }
    var title: String = ""
        set(value) {
            field = value
            titleView.text = value
            iconView.contentDescription = value
        }
    var desc: String = ""
        set(value) {
            field = value
            descView.text = value
        }
    @ColorInt
    var color: Int = 0
        set(value) {
            field = value
            cardView.backgroundTintList = colorStateSingle((value or 0xff000000.toInt()) and 0x33ffffff)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) cardView.outlineSpotShadowColor = value
        }

    constructor(context: Context): this(context, null)
    constructor(context: Context, attrs: AttributeSet?): this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int): super(context, attrs, defStyleAttr) {
        init(context)
        attrs?.let { retrieveAttributes(attrs) }
    }

    private fun init(context: Context) {
        val view = LayoutInflater.from(context).inflate(R.layout.component_license,this)
        cardView = view.findViewById(R.id.component_license_card)
        iconView = view.findViewById(R.id.component_license_icon)
        titleView = view.findViewById(R.id.component_license_title)
        descView = view.findViewById(R.id.component_license_desc)
    }

    private fun retrieveAttributes(attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.License)
        icon = typedArray.getDrawable(R.styleable.License_icon)!!
        title = typedArray.getString(R.styleable.License_title)!!
        desc = typedArray.getString(R.styleable.License_desc)!!
        color = typedArray.getColor(R.styleable.License_color, 0)
        typedArray.recycle()
    }

    override fun setOnClickListener(l: OnClickListener?) = cardView.setOnClickListener(l)

    override fun setOnLongClickListener(l: OnLongClickListener?) = cardView.setOnLongClickListener(l)
}