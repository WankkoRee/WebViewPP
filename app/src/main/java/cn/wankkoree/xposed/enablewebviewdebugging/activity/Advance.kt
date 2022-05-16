package cn.wankkoree.xposed.enablewebviewdebugging.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.viewpager.widget.PagerAdapter
import cn.wankkoree.xposed.enablewebviewdebugging.BuildConfig
import cn.wankkoree.xposed.enablewebviewdebugging.R
import cn.wankkoree.xposed.enablewebviewdebugging.activity.component.License
import cn.wankkoree.xposed.enablewebviewdebugging.databinding.AdvanceBinding
import com.highcapable.yukihookapi.YukiHookAPI
import com.tmall.ultraviewpager.UltraViewPager
import com.tmall.ultraviewpager.transformer.UltraDepthScaleTransformer

class Advance: AppCompatActivity() {
    private lateinit var viewBinding: AdvanceBinding
    private var toast: Toast? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = AdvanceBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.advanceLicense.apply {
            setScrollMode(UltraViewPager.ScrollMode.HORIZONTAL)
            setMultiScreen(0.75f)
            setPageTransformer(false, UltraDepthScaleTransformer())
            adapter = LicenseAdapter(arrayListOf<View>(
                License(this@Advance).apply {
                    icon = AppCompatResources.getDrawable(this@Advance, R.mipmap.wankkoree)
                    title = getString(R.string.wankko_ree)
                    desc = getString(R.string.author_of_this_module)
                    color = getPrimaryColor(icon!!, this@Advance).first
                    setOnClickListener {
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/WankkoRee")))
                    }
                },
                License(this@Advance).apply {
                    icon = AppCompatResources.getDrawable(this@Advance, R.mipmap.yuki_hook_api)
                    title = getString(R.string.yuki_hook_api)
                    desc = getString(R.string.this_module_is_constructed_using_yukihookapi) + "\n" + getString(R.string.advance_yuki_version_text).format(YukiHookAPI.API_VERSION_NAME, YukiHookAPI.API_VERSION_CODE)
                    color = getPrimaryColor(icon!!, this@Advance).first
                    setOnClickListener {
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/fankes/YukiHookAPI")))
                    }
                },
                License(this@Advance).apply {
                    icon = packageManager.getApplicationIcon(BuildConfig.APPLICATION_ID)
                    title = getString(R.string.app_name)
                    desc = getString(R.string.app_description)
                    color = getPrimaryColor(icon!!, this@Advance).first
                    setOnClickListener {
                        toast?.cancel()
                        toast = Toast.makeText(this@Advance, getString(R.string.if_you_find_this_project_useful_please_star_this_project), Toast.LENGTH_SHORT)
                        toast!!.show()
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/WankkoRee/EnableWebViewDebugging")))
                    }
                },
            ))
        }

        viewBinding.advanceToolbarBack.setOnClickListener {
            finish()
        }
    }

    class LicenseAdapter(private val items: ArrayList<View>) : PagerAdapter() {
        override fun getCount(): Int = items.size

        override fun isViewFromObject(view: View, any: Any): Boolean {
            return view == any
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            return items[position].also {
                container.addView(it)
            }
        }

        override fun destroyItem(container: ViewGroup, position: Int, any: Any) {
            container.removeView(items[position])
        }
    }
}