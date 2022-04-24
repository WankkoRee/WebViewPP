package cn.wankkoree.xposed.enablewebviewdebugging.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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

        viewBinding.advanceYukiVersion.text = getString(R.string.advance_yuki_version_text).format(YukiHookAPI.API_VERSION_NAME, YukiHookAPI.API_VERSION_CODE)

        viewBinding.advanceToolbarBack.setOnClickListener {
            finish()
        }
        viewBinding.advanceWankkoreeCard.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/WankkoRee")))
        }
        viewBinding.advanceYukiCard.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/fankes/YukiHookAPI")))
        }
        viewBinding.advanceAboutCard.setOnClickListener {
            toast?.cancel()
            toast = Toast.makeText(this@Advance, getString(R.string.if_you_find_this_project_useful_please_star_this_project), Toast.LENGTH_SHORT)
            toast!!.show()
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/WankkoRee/EnableWebViewDebugging")))
        }
    }
}