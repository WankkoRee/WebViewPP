package cn.wankkoree.xposed.enablewebviewdebugging.activity

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.PagerAdapter
import cn.wankkoree.xposed.enablewebviewdebugging.BuildConfig
import cn.wankkoree.xposed.enablewebviewdebugging.R
import cn.wankkoree.xposed.enablewebviewdebugging.databinding.AdvanceBinding
import com.highcapable.yukihookapi.YukiHookAPI

class Advance: AppCompatActivity() {
    private lateinit var viewBinding: AdvanceBinding
    private var toast: Toast? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = AdvanceBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        getPrimaryColor(viewBinding.advanceLicenseAuthorIcon.drawable, this@Advance).first.also {
            viewBinding.advanceLicenseAuthorCard.backgroundTintList = colorStateSingle((it or 0xff000000.toInt()) and 0x33ffffff)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) viewBinding.advanceLicenseAuthorCard.outlineSpotShadowColor = it
        }
        viewBinding.advanceLicenseAuthorButtonGithub.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/WankkoRee")))
        }
        viewBinding.advanceLicenseAuthorButtonEmail.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("mailto://wkr@wkr.moe")))
        }

        viewBinding.advanceLicenseYukiDesc.text = getString(R.string.this_module_is_constructed_using_yukihookapi) + "\n" + getString(R.string.advance_yuki_version_text).format(YukiHookAPI.API_VERSION_NAME, YukiHookAPI.API_VERSION_CODE)
        getPrimaryColor(viewBinding.advanceLicenseYukiIcon.drawable, this@Advance).first.also {
            viewBinding.advanceLicenseYukiCard.backgroundTintList = colorStateSingle((it or 0xff000000.toInt()) and 0x33ffffff)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) viewBinding.advanceLicenseYukiCard.outlineSpotShadowColor = it
        }
        viewBinding.advanceLicenseYukiButtonGithub.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/fankes/YukiHookAPI")))
        }

        viewBinding.advanceLicenseModuleIcon.setImageDrawable(packageManager.getApplicationIcon(BuildConfig.APPLICATION_ID))
        getPrimaryColor(viewBinding.advanceLicenseModuleIcon.drawable, this@Advance).first.also {
            viewBinding.advanceLicenseModuleCard.backgroundTintList = colorStateSingle((it or 0xff000000.toInt()) and 0x33ffffff)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) viewBinding.advanceLicenseModuleCard.outlineSpotShadowColor = it
        }
        viewBinding.advanceLicenseModuleButtonGithub.setOnClickListener {
            toast?.cancel()
            toast = Toast.makeText(this@Advance, getString(R.string.if_you_find_this_project_useful_please_star_this_project), Toast.LENGTH_SHORT)
            toast!!.show()
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/WankkoRee/EnableWebViewDebugging")))
        }
        viewBinding.advanceLicenseModuleButtonTelegram.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/EnableWebViewDebugging")))
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