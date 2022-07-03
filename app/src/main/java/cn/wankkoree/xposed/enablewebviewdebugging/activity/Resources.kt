package cn.wankkoree.xposed.enablewebviewdebugging.activity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import cn.wankkoree.xposed.enablewebviewdebugging.BuildConfig
import cn.wankkoree.xposed.enablewebviewdebugging.R
import cn.wankkoree.xposed.enablewebviewdebugging.ValueAlreadyExistedInSet
import cn.wankkoree.xposed.enablewebviewdebugging.activity.component.Tag
import cn.wankkoree.xposed.enablewebviewdebugging.data.*
import cn.wankkoree.xposed.enablewebviewdebugging.databinding.ResourcesBinding
import cn.wankkoree.xposed.enablewebviewdebugging.http.Http
import com.google.gson.Gson
import com.highcapable.yukihookapi.hook.factory.modulePrefs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Resources : AppCompatActivity() {
    private lateinit var viewBinding: ResourcesBinding
    private var toast: Toast? = null
    private lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ResourcesBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        context = this

        lifecycleScope.launch(Dispatchers.Main) {
            refresh()

            viewBinding.resourcesVconsoleDownload.isEnabled = false
            val vConsoleVersionStr = try {
                Http.get("https://data.jsdelivr.com/v1/package/npm/vconsole")
            } catch(e: Exception) {
                Log.e(BuildConfig.APPLICATION_ID, getString(R.string.pull_failed).format(getString(R.string.vconsole)), e)
                null
            }
            if (vConsoleVersionStr != null) {
                val vConsoleVersion = Gson().fromJson(vConsoleVersionStr, cn.wankkoree.xposed.enablewebviewdebugging.http.bean.api.npm.Versions::class.java)
                val adapter = ArrayAdapter(context, R.layout.component_spinneritem, vConsoleVersion.versions)
                adapter.setDropDownViewResource(R.layout.component_spinneritem)
                viewBinding.resourcesVconsoleVersion.adapter = adapter
                viewBinding.resourcesVconsoleVersion.setSelection(adapter.getPosition(vConsoleVersion.tags.latest))
                viewBinding.resourcesVconsoleDownload.isEnabled = true
            } else {
                toast?.cancel()
                toast = Toast.makeText(context, getString(R.string.pull_failed).format(getString(R.string.vconsole)), Toast.LENGTH_SHORT)
                toast!!.show()
            }

            viewBinding.resourcesVconsolePluginSourcesDownload.isEnabled = false
            val vConsolePluginSourcesVersionStr = try {
                Http.get("https://data.jsdelivr.com/v1/package/npm/vconsole-sources")
            } catch(e: Exception) {
                Log.e(BuildConfig.APPLICATION_ID, getString(R.string.pull_failed).format(getString(R.string.vconsole_plugin_sources)), e)
                null
            }
            if (vConsolePluginSourcesVersionStr != null) {
                val vConsolePluginSourcesVersion = Gson().fromJson(vConsolePluginSourcesVersionStr, cn.wankkoree.xposed.enablewebviewdebugging.http.bean.api.npm.Versions::class.java)
                val adapter = ArrayAdapter(context, R.layout.component_spinneritem, vConsolePluginSourcesVersion.versions)
                adapter.setDropDownViewResource(R.layout.component_spinneritem)
                viewBinding.resourcesVconsolePluginSourcesVersion.adapter = adapter
                viewBinding.resourcesVconsolePluginSourcesVersion.setSelection(adapter.getPosition(vConsolePluginSourcesVersion.tags.latest))
                viewBinding.resourcesVconsolePluginSourcesDownload.isEnabled = true
            } else {
                toast?.cancel()
                toast = Toast.makeText(context, getString(R.string.pull_failed).format(getString(R.string.vconsole_plugin_sources)), Toast.LENGTH_SHORT)
                toast!!.show()
            }

            viewBinding.resourcesVconsolePluginStatsDownload.isEnabled = false
            val vConsolePluginStatsVersionStr = try {
                Http.get("https://data.jsdelivr.com/v1/package/npm/vconsole-stats-plugin")
            } catch(e: Exception) {
                Log.e(BuildConfig.APPLICATION_ID, getString(R.string.pull_failed).format(getString(R.string.vconsole_plugin_stats)), e)
                null
            }
            if (vConsolePluginStatsVersionStr != null) {
                val vConsolePluginStatsVersion = Gson().fromJson(vConsolePluginStatsVersionStr, cn.wankkoree.xposed.enablewebviewdebugging.http.bean.api.npm.Versions::class.java)
                val adapter = ArrayAdapter(context, R.layout.component_spinneritem, vConsolePluginStatsVersion.versions)
                adapter.setDropDownViewResource(R.layout.component_spinneritem)
                viewBinding.resourcesVconsolePluginStatsVersion.adapter = adapter
                viewBinding.resourcesVconsolePluginStatsVersion.setSelection(adapter.getPosition(vConsolePluginStatsVersion.tags.latest))
                viewBinding.resourcesVconsolePluginStatsDownload.isEnabled = true
            } else {
                toast?.cancel()
                toast = Toast.makeText(context, getString(R.string.pull_failed).format(getString(R.string.vconsole_plugin_stats)), Toast.LENGTH_SHORT)
                toast!!.show()
            }

            viewBinding.resourcesVconsolePluginVueDevtoolsDownload.isEnabled = false
            val vConsolePluginVueDevtoolsVersionStr = try {
                Http.get("https://data.jsdelivr.com/v1/package/npm/vue-vconsole-devtools")
            } catch(e: Exception) {
                Log.e(BuildConfig.APPLICATION_ID, getString(R.string.pull_failed).format(getString(R.string.vconsole_plugin_vue_devtools)), e)
                null
            }
            if (vConsolePluginVueDevtoolsVersionStr != null) {
                val vConsolePluginVueDevtoolsVersion = Gson().fromJson(vConsolePluginVueDevtoolsVersionStr, cn.wankkoree.xposed.enablewebviewdebugging.http.bean.api.npm.Versions::class.java)
                val adapter = ArrayAdapter(context, R.layout.component_spinneritem, vConsolePluginVueDevtoolsVersion.versions)
                adapter.setDropDownViewResource(R.layout.component_spinneritem)
                viewBinding.resourcesVconsolePluginVueDevtoolsVersion.adapter = adapter
                viewBinding.resourcesVconsolePluginVueDevtoolsVersion.setSelection(adapter.getPosition(vConsolePluginVueDevtoolsVersion.tags.latest))
                viewBinding.resourcesVconsolePluginVueDevtoolsDownload.isEnabled = true
            } else {
                toast?.cancel()
                toast = Toast.makeText(context, getString(R.string.pull_failed).format(getString(R.string.vconsole_plugin_vue_devtools)), Toast.LENGTH_SHORT)
                toast!!.show()
            }

            viewBinding.resourcesVconsolePluginOutputlogDownload.isEnabled = false
            val vConsolePluginOutputlogVersionStr = try {
                Http.get("https://data.jsdelivr.com/v1/package/npm/vconsole-outputlog-plugin")
            } catch(e: Exception) {
                Log.e(BuildConfig.APPLICATION_ID, getString(R.string.pull_failed).format(getString(R.string.vconsole_plugin_outputlog)), e)
                null
            }
            if (vConsolePluginOutputlogVersionStr != null) {
                val vConsolePluginOutputlogVersion = Gson().fromJson(vConsolePluginOutputlogVersionStr, cn.wankkoree.xposed.enablewebviewdebugging.http.bean.api.npm.Versions::class.java)
                val adapter = ArrayAdapter(context, R.layout.component_spinneritem, vConsolePluginOutputlogVersion.versions)
                adapter.setDropDownViewResource(R.layout.component_spinneritem)
                viewBinding.resourcesVconsolePluginOutputlogVersion.adapter = adapter
                viewBinding.resourcesVconsolePluginOutputlogVersion.setSelection(adapter.getPosition(vConsolePluginOutputlogVersion.tags.latest))
                viewBinding.resourcesVconsolePluginOutputlogDownload.isEnabled = true
            } else {
                toast?.cancel()
                toast = Toast.makeText(context, getString(R.string.pull_failed).format(getString(R.string.vconsole_plugin_outputlog)), Toast.LENGTH_SHORT)
                toast!!.show()
            }

            viewBinding.resourcesErudaDownload.isEnabled = false
            val erudaVersionStr = try {
                Http.get("https://data.jsdelivr.com/v1/package/npm/eruda")
            } catch(e: Exception) {
                Log.e(BuildConfig.APPLICATION_ID, getString(R.string.pull_failed).format(getString(R.string.eruda)), e)
                null
            }
            if (erudaVersionStr != null) {
                val erudaVersion = Gson().fromJson(erudaVersionStr, cn.wankkoree.xposed.enablewebviewdebugging.http.bean.api.npm.Versions::class.java)
                val adapter = ArrayAdapter(context, R.layout.component_spinneritem, erudaVersion.versions)
                adapter.setDropDownViewResource(R.layout.component_spinneritem)
                viewBinding.resourcesErudaVersion.adapter = adapter
                viewBinding.resourcesErudaVersion.setSelection(adapter.getPosition(erudaVersion.tags.latest))
                viewBinding.resourcesErudaDownload.isEnabled = true
            } else {
                toast?.cancel()
                toast = Toast.makeText(context, getString(R.string.pull_failed).format(getString(R.string.eruda)), Toast.LENGTH_SHORT)
                toast!!.show()
            }

            viewBinding.resourcesErudaPluginFpsDownload.isEnabled = false
            val erudaPluginFpsVersionStr = try {
                Http.get("https://data.jsdelivr.com/v1/package/npm/eruda-fps")
            } catch(e: Exception) {
                Log.e(BuildConfig.APPLICATION_ID, getString(R.string.pull_failed).format(getString(R.string.eruda_plugin_fps)), e)
                null
            }
            if (erudaPluginFpsVersionStr != null) {
                val erudaPluginFpsVersion = Gson().fromJson(erudaPluginFpsVersionStr, cn.wankkoree.xposed.enablewebviewdebugging.http.bean.api.npm.Versions::class.java)
                val adapter = ArrayAdapter(context, R.layout.component_spinneritem, erudaPluginFpsVersion.versions)
                adapter.setDropDownViewResource(R.layout.component_spinneritem)
                viewBinding.resourcesErudaPluginFpsVersion.adapter = adapter
                viewBinding.resourcesErudaPluginFpsVersion.setSelection(adapter.getPosition(erudaPluginFpsVersion.tags.latest))
                viewBinding.resourcesErudaPluginFpsDownload.isEnabled = true
            } else {
                toast?.cancel()
                toast = Toast.makeText(context, getString(R.string.pull_failed).format(getString(R.string.eruda_plugin_fps)), Toast.LENGTH_SHORT)
                toast!!.show()
            }

            viewBinding.resourcesErudaPluginFeaturesDownload.isEnabled = false
            val erudaPluginFeaturesVersionStr = try {
                Http.get("https://data.jsdelivr.com/v1/package/npm/eruda-features")
            } catch(e: Exception) {
                Log.e(BuildConfig.APPLICATION_ID, getString(R.string.pull_failed).format(getString(R.string.eruda_plugin_features)), e)
                null
            }
            if (erudaPluginFeaturesVersionStr != null) {
                val erudaPluginFeaturesVersion = Gson().fromJson(erudaPluginFeaturesVersionStr, cn.wankkoree.xposed.enablewebviewdebugging.http.bean.api.npm.Versions::class.java)
                val adapter = ArrayAdapter(context, R.layout.component_spinneritem, erudaPluginFeaturesVersion.versions)
                adapter.setDropDownViewResource(R.layout.component_spinneritem)
                viewBinding.resourcesErudaPluginFeaturesVersion.adapter = adapter
                viewBinding.resourcesErudaPluginFeaturesVersion.setSelection(adapter.getPosition(erudaPluginFeaturesVersion.tags.latest))
                viewBinding.resourcesErudaPluginFeaturesDownload.isEnabled = true
            } else {
                toast?.cancel()
                toast = Toast.makeText(context, getString(R.string.pull_failed).format(getString(R.string.eruda_plugin_features)), Toast.LENGTH_SHORT)
                toast!!.show()
            }

            viewBinding.resourcesErudaPluginTimingDownload.isEnabled = false
            val erudaPluginTimingVersionStr = try {
                Http.get("https://data.jsdelivr.com/v1/package/npm/eruda-timing")
            } catch(e: Exception) {
                Log.e(BuildConfig.APPLICATION_ID, getString(R.string.pull_failed).format(getString(R.string.eruda_plugin_timing)), e)
                null
            }
            if (erudaPluginTimingVersionStr != null) {
                val erudaPluginTimingVersion = Gson().fromJson(erudaPluginTimingVersionStr, cn.wankkoree.xposed.enablewebviewdebugging.http.bean.api.npm.Versions::class.java)
                val adapter = ArrayAdapter(context, R.layout.component_spinneritem, erudaPluginTimingVersion.versions)
                adapter.setDropDownViewResource(R.layout.component_spinneritem)
                viewBinding.resourcesErudaPluginTimingVersion.adapter = adapter
                viewBinding.resourcesErudaPluginTimingVersion.setSelection(adapter.getPosition(erudaPluginTimingVersion.tags.latest))
                viewBinding.resourcesErudaPluginTimingDownload.isEnabled = true
            } else {
                toast?.cancel()
                toast = Toast.makeText(context, getString(R.string.pull_failed).format(getString(R.string.eruda_plugin_timing)), Toast.LENGTH_SHORT)
                toast!!.show()
            }

            viewBinding.resourcesErudaPluginMemoryDownload.isEnabled = false
            val erudaPluginMemoryVersionStr = try {
                Http.get("https://data.jsdelivr.com/v1/package/npm/eruda-memory")
            } catch(e: Exception) {
                Log.e(BuildConfig.APPLICATION_ID, getString(R.string.pull_failed).format(getString(R.string.eruda_plugin_memory)), e)
                null
            }
            if (erudaPluginMemoryVersionStr != null) {
                val erudaPluginMemoryVersion = Gson().fromJson(erudaPluginMemoryVersionStr, cn.wankkoree.xposed.enablewebviewdebugging.http.bean.api.npm.Versions::class.java)
                val adapter = ArrayAdapter(context, R.layout.component_spinneritem, erudaPluginMemoryVersion.versions)
                adapter.setDropDownViewResource(R.layout.component_spinneritem)
                viewBinding.resourcesErudaPluginMemoryVersion.adapter = adapter
                viewBinding.resourcesErudaPluginMemoryVersion.setSelection(adapter.getPosition(erudaPluginMemoryVersion.tags.latest))
                viewBinding.resourcesErudaPluginMemoryDownload.isEnabled = true
            } else {
                toast?.cancel()
                toast = Toast.makeText(context, getString(R.string.pull_failed).format(getString(R.string.eruda_plugin_memory)), Toast.LENGTH_SHORT)
                toast!!.show()
            }

            viewBinding.resourcesErudaPluginCodeDownload.isEnabled = false
            val erudaPluginCodeVersionStr = try {
                Http.get("https://data.jsdelivr.com/v1/package/npm/eruda-code")
            } catch(e: Exception) {
                Log.e(BuildConfig.APPLICATION_ID, getString(R.string.pull_failed).format(getString(R.string.eruda_plugin_code)), e)
                null
            }
            if (erudaPluginCodeVersionStr != null) {
                val erudaPluginCodeVersion = Gson().fromJson(erudaPluginCodeVersionStr, cn.wankkoree.xposed.enablewebviewdebugging.http.bean.api.npm.Versions::class.java)
                val adapter = ArrayAdapter(context, R.layout.component_spinneritem, erudaPluginCodeVersion.versions)
                adapter.setDropDownViewResource(R.layout.component_spinneritem)
                viewBinding.resourcesErudaPluginCodeVersion.adapter = adapter
                viewBinding.resourcesErudaPluginCodeVersion.setSelection(adapter.getPosition(erudaPluginCodeVersion.tags.latest))
                viewBinding.resourcesErudaPluginCodeDownload.isEnabled = true
            } else {
                toast?.cancel()
                toast = Toast.makeText(context, getString(R.string.pull_failed).format(getString(R.string.eruda_plugin_code)), Toast.LENGTH_SHORT)
                toast!!.show()
            }

            viewBinding.resourcesErudaPluginBenchmarkDownload.isEnabled = false
            val erudaPluginBenchmarkVersionStr = try {
                Http.get("https://data.jsdelivr.com/v1/package/npm/eruda-benchmark")
            } catch(e: Exception) {
                Log.e(BuildConfig.APPLICATION_ID, getString(R.string.pull_failed).format(getString(R.string.eruda_plugin_benchmark)), e)
                null
            }
            if (erudaPluginBenchmarkVersionStr != null) {
                val erudaPluginBenchmarkVersion = Gson().fromJson(erudaPluginBenchmarkVersionStr, cn.wankkoree.xposed.enablewebviewdebugging.http.bean.api.npm.Versions::class.java)
                val adapter = ArrayAdapter(context, R.layout.component_spinneritem, erudaPluginBenchmarkVersion.versions)
                adapter.setDropDownViewResource(R.layout.component_spinneritem)
                viewBinding.resourcesErudaPluginBenchmarkVersion.adapter = adapter
                viewBinding.resourcesErudaPluginBenchmarkVersion.setSelection(adapter.getPosition(erudaPluginBenchmarkVersion.tags.latest))
                viewBinding.resourcesErudaPluginBenchmarkDownload.isEnabled = true
            } else {
                toast?.cancel()
                toast = Toast.makeText(context, getString(R.string.pull_failed).format(getString(R.string.eruda_plugin_benchmark)), Toast.LENGTH_SHORT)
                toast!!.show()
            }

            viewBinding.resourcesErudaPluginGeolocationDownload.isEnabled = false
            val erudaPluginGeolocationVersionStr = try {
                Http.get("https://data.jsdelivr.com/v1/package/npm/eruda-geolocation")
            } catch(e: Exception) {
                Log.e(BuildConfig.APPLICATION_ID, getString(R.string.pull_failed).format(getString(R.string.eruda_plugin_geolocation)), e)
                null
            }
            if (erudaPluginGeolocationVersionStr != null) {
                val erudaPluginGeolocationVersion = Gson().fromJson(erudaPluginGeolocationVersionStr, cn.wankkoree.xposed.enablewebviewdebugging.http.bean.api.npm.Versions::class.java)
                val adapter = ArrayAdapter(context, R.layout.component_spinneritem, erudaPluginGeolocationVersion.versions)
                adapter.setDropDownViewResource(R.layout.component_spinneritem)
                viewBinding.resourcesErudaPluginGeolocationVersion.adapter = adapter
                viewBinding.resourcesErudaPluginGeolocationVersion.setSelection(adapter.getPosition(erudaPluginGeolocationVersion.tags.latest))
                viewBinding.resourcesErudaPluginGeolocationDownload.isEnabled = true
            } else {
                toast?.cancel()
                toast = Toast.makeText(context, getString(R.string.pull_failed).format(getString(R.string.eruda_plugin_geolocation)), Toast.LENGTH_SHORT)
                toast!!.show()
            }

            viewBinding.resourcesErudaPluginDomDownload.isEnabled = false
            val erudaPluginDomVersionStr = try {
                Http.get("https://data.jsdelivr.com/v1/package/npm/eruda-dom")
            } catch(e: Exception) {
                Log.e(BuildConfig.APPLICATION_ID, getString(R.string.pull_failed).format(getString(R.string.eruda_plugin_dom)), e)
                null
            }
            if (erudaPluginDomVersionStr != null) {
                val erudaPluginDomVersion = Gson().fromJson(erudaPluginDomVersionStr, cn.wankkoree.xposed.enablewebviewdebugging.http.bean.api.npm.Versions::class.java)
                val adapter = ArrayAdapter(context, R.layout.component_spinneritem, erudaPluginDomVersion.versions)
                adapter.setDropDownViewResource(R.layout.component_spinneritem)
                viewBinding.resourcesErudaPluginDomVersion.adapter = adapter
                viewBinding.resourcesErudaPluginDomVersion.setSelection(adapter.getPosition(erudaPluginDomVersion.tags.latest))
                viewBinding.resourcesErudaPluginDomDownload.isEnabled = true
            } else {
                toast?.cancel()
                toast = Toast.makeText(context, getString(R.string.pull_failed).format(getString(R.string.eruda_plugin_dom)), Toast.LENGTH_SHORT)
                toast!!.show()
            }

            viewBinding.resourcesErudaPluginOrientationDownload.isEnabled = false
            val erudaPluginOrientationVersionStr = try {
                Http.get("https://data.jsdelivr.com/v1/package/npm/eruda-orientation")
            } catch(e: Exception) {
                Log.e(BuildConfig.APPLICATION_ID, getString(R.string.pull_failed).format(getString(R.string.eruda_plugin_orientation)), e)
                null
            }
            if (erudaPluginOrientationVersionStr != null) {
                val erudaPluginOrientationVersion = Gson().fromJson(erudaPluginOrientationVersionStr, cn.wankkoree.xposed.enablewebviewdebugging.http.bean.api.npm.Versions::class.java)
                val adapter = ArrayAdapter(context, R.layout.component_spinneritem, erudaPluginOrientationVersion.versions)
                adapter.setDropDownViewResource(R.layout.component_spinneritem)
                viewBinding.resourcesErudaPluginOrientationVersion.adapter = adapter
                viewBinding.resourcesErudaPluginOrientationVersion.setSelection(adapter.getPosition(erudaPluginOrientationVersion.tags.latest))
                viewBinding.resourcesErudaPluginOrientationDownload.isEnabled = true
            } else {
                toast?.cancel()
                toast = Toast.makeText(context, getString(R.string.pull_failed).format(getString(R.string.eruda_plugin_orientation)), Toast.LENGTH_SHORT)
                toast!!.show()
            }

            viewBinding.resourcesErudaPluginTouchesDownload.isEnabled = false
            val erudaPluginTouchesVersionStr = try {
                Http.get("https://data.jsdelivr.com/v1/package/npm/eruda-touches")
            } catch(e: Exception) {
                Log.e(BuildConfig.APPLICATION_ID, getString(R.string.pull_failed).format(getString(R.string.eruda_plugin_touches)), e)
                null
            }
            if (erudaPluginTouchesVersionStr != null) {
                val erudaPluginTouchesVersion = Gson().fromJson(erudaPluginTouchesVersionStr, cn.wankkoree.xposed.enablewebviewdebugging.http.bean.api.npm.Versions::class.java)
                val adapter = ArrayAdapter(context, R.layout.component_spinneritem, erudaPluginTouchesVersion.versions)
                adapter.setDropDownViewResource(R.layout.component_spinneritem)
                viewBinding.resourcesErudaPluginTouchesVersion.adapter = adapter
                viewBinding.resourcesErudaPluginTouchesVersion.setSelection(adapter.getPosition(erudaPluginTouchesVersion.tags.latest))
                viewBinding.resourcesErudaPluginTouchesDownload.isEnabled = true
            } else {
                toast?.cancel()
                toast = Toast.makeText(context, getString(R.string.pull_failed).format(getString(R.string.eruda_plugin_touches)), Toast.LENGTH_SHORT)
                toast!!.show()
            }

            viewBinding.resourcesNebulaucsdkDownload.isEnabled = false
            val nebulaUCSDKVersionStr = try {
                Http.get("https://api.github.com/repos/WankkoRee/EnableWebViewDebugging-Rules/contents/resources/nebulaucsdk")
            } catch(e: Exception) {
                Log.e(BuildConfig.APPLICATION_ID, getString(R.string.pull_failed).format(getString(R.string.nebulaucsdk)), e)
                null
            }
            if (nebulaUCSDKVersionStr != null) {
                val nebulaUCSDKVersion = Gson().fromJson(nebulaUCSDKVersionStr, Array<cn.wankkoree.xposed.enablewebviewdebugging.http.bean.api.github.RepoContent>::class.java)
                val adapter = ArrayAdapter(context, R.layout.component_spinneritem, nebulaUCSDKVersion.map{ it.name })
                adapter.setDropDownViewResource(R.layout.component_spinneritem)
                viewBinding.resourcesNebulaucsdkVersion.adapter = adapter
                viewBinding.resourcesNebulaucsdkVersion.setSelection(0)
                viewBinding.resourcesNebulaucsdkDownload.isEnabled = true
            } else {
                toast?.cancel()
                toast = Toast.makeText(context, getString(R.string.pull_failed).format(getString(R.string.nebulaucsdk)), Toast.LENGTH_SHORT)
                toast!!.show()
            }
        }

        viewBinding.resourcesToolbarBack.setOnClickListener {
            finish()
        }
        viewBinding.resourcesVconsoleCard.setOnClickListener { tips() }
        viewBinding.resourcesVconsolePluginSourcesCard.setOnClickListener { tips() }
        viewBinding.resourcesVconsolePluginStatsCard.setOnClickListener { tips() }
        viewBinding.resourcesVconsolePluginVueDevtoolsCard.setOnClickListener { tips() }
        viewBinding.resourcesVconsolePluginOutputlogCard.setOnClickListener { tips() }
        viewBinding.resourcesErudaCard.setOnClickListener { tips() }
        viewBinding.resourcesErudaPluginFpsCard.setOnClickListener { tips() }
        viewBinding.resourcesErudaPluginFeaturesCard.setOnClickListener { tips() }
        viewBinding.resourcesErudaPluginTimingCard.setOnClickListener { tips() }
        viewBinding.resourcesErudaPluginMemoryCard.setOnClickListener { tips() }
        viewBinding.resourcesErudaPluginCodeCard.setOnClickListener { tips() }
        viewBinding.resourcesErudaPluginBenchmarkCard.setOnClickListener { tips() }
        viewBinding.resourcesErudaPluginGeolocationCard.setOnClickListener { tips() }
        viewBinding.resourcesErudaPluginDomCard.setOnClickListener { tips() }
        viewBinding.resourcesErudaPluginOrientationCard.setOnClickListener { tips() }
        viewBinding.resourcesErudaPluginTouchesCard.setOnClickListener { tips() }
        viewBinding.resourcesNebulaucsdkCard.setOnClickListener { tips() }
        viewBinding.resourcesVconsoleDownload.setOnClickListener { lifecycleScope.launch(Dispatchers.Main) {
            val version = viewBinding.resourcesVconsoleVersion.selectedItem as String
            toast?.cancel()
            toast = Toast.makeText(context, "${getString(R.string.download_started)} ${getString(R.string.vconsole)}$version", Toast.LENGTH_SHORT)
            toast!!.show()
            val vConsoleStr = try {
                Http.get("https://cdn.jsdelivr.net/npm/vconsole@$version/dist/vconsole.min.js")
            } catch(e: Exception) {
                Log.e(BuildConfig.APPLICATION_ID, getString(R.string.download_failed), e)
                null
            }
            if (vConsoleStr != null) {
                toast?.cancel()
                toast = Toast.makeText(context, getString(R.string.download_completed), Toast.LENGTH_SHORT)
                toast!!.show()
                with(modulePrefs) {
                    name("resources_vConsole_$version")
                    putString("vConsole", vConsoleStr)
                    name("resources")
                    try { put(ResourcesSP.vConsole_versions, version) } catch (e: ValueAlreadyExistedInSet) {
                        toast?.cancel()
                        toast = Toast.makeText(context, getString(R.string.the_target_version_already_exists_it_will_be_overwritten), Toast.LENGTH_SHORT)
                        toast!!.show()
                    }
                }
                refresh()
            } else {
                toast?.cancel()
                toast = Toast.makeText(context, getString(R.string.download_failed), Toast.LENGTH_SHORT)
                toast!!.show()
            }
        } }
        viewBinding.resourcesVconsolePluginSourcesDownload.setOnClickListener { lifecycleScope.launch(Dispatchers.Main) {
            val version = viewBinding.resourcesVconsolePluginSourcesVersion.selectedItem as String
            toast?.cancel()
            toast = Toast.makeText(context, "${getString(R.string.download_started)} ${getString(R.string.vconsole_plugin_sources)}$version", Toast.LENGTH_SHORT)
            toast!!.show()
            val vConsolePluginSourcesStr = try {
                Http.get("https://cdn.jsdelivr.net/npm/vconsole-sources@$version/dist/vconsole-sources.min.js")
            } catch(e: Exception) {
                Log.e(BuildConfig.APPLICATION_ID, getString(R.string.download_failed), e)
                null
            }
            if (vConsolePluginSourcesStr != null) {
                toast?.cancel()
                toast = Toast.makeText(context, getString(R.string.download_completed), Toast.LENGTH_SHORT)
                toast!!.show()
                with(modulePrefs) {
                    name("resources_vConsole_plugin_sources_$version")
                    putString("vConsole_plugin_sources", vConsolePluginSourcesStr)
                    name("resources")
                    try { put(ResourcesSP.vConsole_plugin_sources_versions, version) } catch (e: ValueAlreadyExistedInSet) {
                        toast?.cancel()
                        toast = Toast.makeText(context, getString(R.string.the_target_version_already_exists_it_will_be_overwritten), Toast.LENGTH_SHORT)
                        toast!!.show()
                    }
                }
                refresh()
            } else {
                toast?.cancel()
                toast = Toast.makeText(context, getString(R.string.download_failed), Toast.LENGTH_SHORT)
                toast!!.show()
            }
        } }
        viewBinding.resourcesVconsolePluginStatsDownload.setOnClickListener { lifecycleScope.launch(Dispatchers.Main) {
            val version = viewBinding.resourcesVconsolePluginStatsVersion.selectedItem as String
            toast?.cancel()
            toast = Toast.makeText(context, "${getString(R.string.download_started)} ${getString(R.string.vconsole_plugin_stats)}$version", Toast.LENGTH_SHORT)
            toast!!.show()
            val vConsolePluginStatsStr = try {
                Http.get("https://cdn.jsdelivr.net/npm/vconsole-stats-plugin@$version/dist/vconsole-stats-plugin.min.js")
            } catch(e: Exception) {
                Log.e(BuildConfig.APPLICATION_ID, getString(R.string.download_failed), e)
                null
            }
            if (vConsolePluginStatsStr != null) {
                toast?.cancel()
                toast = Toast.makeText(context, getString(R.string.download_completed), Toast.LENGTH_SHORT)
                toast!!.show()
                with(modulePrefs) {
                    name("resources_vConsole_plugin_stats_$version")
                    putString("vConsole_plugin_stats", vConsolePluginStatsStr)
                    name("resources")
                    try { put(ResourcesSP.vConsole_plugin_stats_versions, version) } catch (e: ValueAlreadyExistedInSet) {
                        toast?.cancel()
                        toast = Toast.makeText(context, getString(R.string.the_target_version_already_exists_it_will_be_overwritten), Toast.LENGTH_SHORT)
                        toast!!.show()
                    }
                }
                refresh()
            } else {
                toast?.cancel()
                toast = Toast.makeText(context, getString(R.string.download_failed), Toast.LENGTH_SHORT)
                toast!!.show()
            }
        } }
        viewBinding.resourcesVconsolePluginVueDevtoolsDownload.setOnClickListener { lifecycleScope.launch(Dispatchers.Main) {
            val version = viewBinding.resourcesVconsolePluginVueDevtoolsVersion.selectedItem as String
            toast?.cancel()
            toast = Toast.makeText(context, "${getString(R.string.download_started)} ${getString(R.string.vconsole_plugin_vue_devtools)}$version", Toast.LENGTH_SHORT)
            toast!!.show()
            val vConsolePluginVueDevtoolsStr = try {
                Http.get("https://cdn.jsdelivr.net/npm/vue-vconsole-devtools@$version/dist/vue_plugin.js")
            } catch(e: Exception) {
                Log.e(BuildConfig.APPLICATION_ID, getString(R.string.download_failed), e)
                null
            }
            if (vConsolePluginVueDevtoolsStr != null) {
                toast?.cancel()
                toast = Toast.makeText(context, getString(R.string.download_completed), Toast.LENGTH_SHORT)
                toast!!.show()
                with(modulePrefs) {
                    name("resources_vConsole_plugin_vue_devtools_$version")
                    putString("vConsole_plugin_vue_devtools", vConsolePluginVueDevtoolsStr)
                    name("resources")
                    try { put(ResourcesSP.vConsole_plugin_vue_devtools_versions, version) } catch (e: ValueAlreadyExistedInSet) {
                        toast?.cancel()
                        toast = Toast.makeText(context, getString(R.string.the_target_version_already_exists_it_will_be_overwritten), Toast.LENGTH_SHORT)
                        toast!!.show()
                    }
                }
                refresh()
            } else {
                toast?.cancel()
                toast = Toast.makeText(context, getString(R.string.download_failed), Toast.LENGTH_SHORT)
                toast!!.show()
            }
        } }
        viewBinding.resourcesVconsolePluginOutputlogDownload.setOnClickListener { lifecycleScope.launch(Dispatchers.Main) {
            val version = viewBinding.resourcesVconsolePluginOutputlogVersion.selectedItem as String
            toast?.cancel()
            toast = Toast.makeText(context, "${getString(R.string.download_started)} ${getString(R.string.vconsole_plugin_outputlog)}$version", Toast.LENGTH_SHORT)
            toast!!.show()
            val vConsolePluginOutputlogStr = try {
                Http.get("https://cdn.jsdelivr.net/npm/vconsole-outputlog-plugin@$version/dist/vconsole-outputlog-plugin.min.js")
            } catch(e: Exception) {
                Log.e(BuildConfig.APPLICATION_ID, getString(R.string.download_failed), e)
                null
            }
            if (vConsolePluginOutputlogStr != null) {
                toast?.cancel()
                toast = Toast.makeText(context, getString(R.string.download_completed), Toast.LENGTH_SHORT)
                toast!!.show()
                with(modulePrefs) {
                    name("resources_vConsole_plugin_outputlog_$version")
                    putString("vConsole_plugin_outputlog", vConsolePluginOutputlogStr)
                    name("resources")
                    try { put(ResourcesSP.vConsole_plugin_outputlog_versions, version) } catch (e: ValueAlreadyExistedInSet) {
                        toast?.cancel()
                        toast = Toast.makeText(context, getString(R.string.the_target_version_already_exists_it_will_be_overwritten), Toast.LENGTH_SHORT)
                        toast!!.show()
                    }
                }
                refresh()
            } else {
                toast?.cancel()
                toast = Toast.makeText(context, getString(R.string.download_failed), Toast.LENGTH_SHORT)
                toast!!.show()
            }
        } }
        viewBinding.resourcesErudaDownload.setOnClickListener { lifecycleScope.launch(Dispatchers.Main) {
            val version = viewBinding.resourcesErudaVersion.selectedItem as String
            toast?.cancel()
            toast = Toast.makeText(context, "${getString(R.string.download_started)} ${getString(R.string.eruda)}$version", Toast.LENGTH_SHORT)
            toast!!.show()
            val erudaStr = try {
                Http.get("https://cdn.jsdelivr.net/npm/eruda@$version/eruda.js")
            } catch(e: Exception) {
                Log.e(BuildConfig.APPLICATION_ID, getString(R.string.download_failed), e)
                null
            }
            if (erudaStr != null) {
                toast?.cancel()
                toast = Toast.makeText(context, getString(R.string.download_completed), Toast.LENGTH_SHORT)
                toast!!.show()
                with(modulePrefs) {
                    name("resources_eruda_$version")
                    putString("eruda", erudaStr)
                    name("resources")
                    try { put(ResourcesSP.eruda_versions, version) } catch (e: ValueAlreadyExistedInSet) {
                        toast?.cancel()
                        toast = Toast.makeText(context, getString(R.string.the_target_version_already_exists_it_will_be_overwritten), Toast.LENGTH_SHORT)
                        toast!!.show()
                    }
                }
                refresh()
            } else {
                toast?.cancel()
                toast = Toast.makeText(context, getString(R.string.download_failed), Toast.LENGTH_SHORT)
                toast!!.show()
            }
        } }
        viewBinding.resourcesErudaPluginFpsDownload.setOnClickListener { lifecycleScope.launch(Dispatchers.Main) {
            val version = viewBinding.resourcesErudaPluginFpsVersion.selectedItem as String
            toast?.cancel()
            toast = Toast.makeText(context, "${getString(R.string.download_started)} ${getString(R.string.eruda_plugin_fps)}$version", Toast.LENGTH_SHORT)
            toast!!.show()
            val erudaPluginFpsStr = try {
                Http.get("https://cdn.jsdelivr.net/npm/eruda-fps@$version/eruda-fps.min.js")
            } catch(e: Exception) {
                Log.e(BuildConfig.APPLICATION_ID, getString(R.string.download_failed), e)
                null
            }
            if (erudaPluginFpsStr != null) {
                toast?.cancel()
                toast = Toast.makeText(context, getString(R.string.download_completed), Toast.LENGTH_SHORT)
                toast!!.show()
                with(modulePrefs) {
                    name("resources_eruda_plugin_fps_$version")
                    putString("eruda_plugin_fps", erudaPluginFpsStr)
                    name("resources")
                    try { put(ResourcesSP.eruda_plugin_fps_versions, version) } catch (e: ValueAlreadyExistedInSet) {
                        toast?.cancel()
                        toast = Toast.makeText(context, getString(R.string.the_target_version_already_exists_it_will_be_overwritten), Toast.LENGTH_SHORT)
                        toast!!.show()
                    }
                }
                refresh()
            } else {
                toast?.cancel()
                toast = Toast.makeText(context, getString(R.string.download_failed), Toast.LENGTH_SHORT)
                toast!!.show()
            }
        } }
        viewBinding.resourcesErudaPluginFeaturesDownload.setOnClickListener { lifecycleScope.launch(Dispatchers.Main) {
            val version = viewBinding.resourcesErudaPluginFeaturesVersion.selectedItem as String
            toast?.cancel()
            toast = Toast.makeText(context, "${getString(R.string.download_started)} ${getString(R.string.eruda_plugin_features)}$version", Toast.LENGTH_SHORT)
            toast!!.show()
            val erudaPluginFeaturesStr = try {
                Http.get("https://cdn.jsdelivr.net/npm/eruda-features@$version/eruda-features.min.js")
            } catch(e: Exception) {
                Log.e(BuildConfig.APPLICATION_ID, getString(R.string.download_failed), e)
                null
            }
            if (erudaPluginFeaturesStr != null) {
                toast?.cancel()
                toast = Toast.makeText(context, getString(R.string.download_completed), Toast.LENGTH_SHORT)
                toast!!.show()
                with(modulePrefs) {
                    name("resources_eruda_plugin_features_$version")
                    putString("eruda_plugin_features", erudaPluginFeaturesStr)
                    name("resources")
                    try { put(ResourcesSP.eruda_plugin_features_versions, version) } catch (e: ValueAlreadyExistedInSet) {
                        toast?.cancel()
                        toast = Toast.makeText(context, getString(R.string.the_target_version_already_exists_it_will_be_overwritten), Toast.LENGTH_SHORT)
                        toast!!.show()
                    }
                }
                refresh()
            } else {
                toast?.cancel()
                toast = Toast.makeText(context, getString(R.string.download_failed), Toast.LENGTH_SHORT)
                toast!!.show()
            }
        } }
        viewBinding.resourcesErudaPluginTimingDownload.setOnClickListener { lifecycleScope.launch(Dispatchers.Main) {
            val version = viewBinding.resourcesErudaPluginTimingVersion.selectedItem as String
            toast?.cancel()
            toast = Toast.makeText(context, "${getString(R.string.download_started)} ${getString(R.string.eruda_plugin_timing)}$version", Toast.LENGTH_SHORT)
            toast!!.show()
            val erudaPluginTimingStr = try {
                Http.get("https://cdn.jsdelivr.net/npm/eruda-timing@$version/eruda-timing.min.js")
            } catch(e: Exception) {
                Log.e(BuildConfig.APPLICATION_ID, getString(R.string.download_failed), e)
                null
            }
            if (erudaPluginTimingStr != null) {
                toast?.cancel()
                toast = Toast.makeText(context, getString(R.string.download_completed), Toast.LENGTH_SHORT)
                toast!!.show()
                with(modulePrefs) {
                    name("resources_eruda_plugin_timing_$version")
                    putString("eruda_plugin_timing", erudaPluginTimingStr)
                    name("resources")
                    try { put(ResourcesSP.eruda_plugin_timing_versions, version) } catch (e: ValueAlreadyExistedInSet) {
                        toast?.cancel()
                        toast = Toast.makeText(context, getString(R.string.the_target_version_already_exists_it_will_be_overwritten), Toast.LENGTH_SHORT)
                        toast!!.show()
                    }
                }
                refresh()
            } else {
                toast?.cancel()
                toast = Toast.makeText(context, getString(R.string.download_failed), Toast.LENGTH_SHORT)
                toast!!.show()
            }
        } }
        viewBinding.resourcesErudaPluginMemoryDownload.setOnClickListener { lifecycleScope.launch(Dispatchers.Main) {
            val version = viewBinding.resourcesErudaPluginMemoryVersion.selectedItem as String
            toast?.cancel()
            toast = Toast.makeText(context, "${getString(R.string.download_started)} ${getString(R.string.eruda_plugin_memory)}$version", Toast.LENGTH_SHORT)
            toast!!.show()
            val erudaPluginMemoryStr = try {
                Http.get("https://cdn.jsdelivr.net/npm/eruda-memory@$version/eruda-memory.min.js")
            } catch(e: Exception) {
                Log.e(BuildConfig.APPLICATION_ID, getString(R.string.download_failed), e)
                null
            }
            if (erudaPluginMemoryStr != null) {
                toast?.cancel()
                toast = Toast.makeText(context, getString(R.string.download_completed), Toast.LENGTH_SHORT)
                toast!!.show()
                with(modulePrefs) {
                    name("resources_eruda_plugin_memory_$version")
                    putString("eruda_plugin_memory", erudaPluginMemoryStr)
                    name("resources")
                    try { put(ResourcesSP.eruda_plugin_memory_versions, version) } catch (e: ValueAlreadyExistedInSet) {
                        toast?.cancel()
                        toast = Toast.makeText(context, getString(R.string.the_target_version_already_exists_it_will_be_overwritten), Toast.LENGTH_SHORT)
                        toast!!.show()
                    }
                }
                refresh()
            } else {
                toast?.cancel()
                toast = Toast.makeText(context, getString(R.string.download_failed), Toast.LENGTH_SHORT)
                toast!!.show()
            }
        } }
        viewBinding.resourcesErudaPluginCodeDownload.setOnClickListener { lifecycleScope.launch(Dispatchers.Main) {
            val version = viewBinding.resourcesErudaPluginCodeVersion.selectedItem as String
            toast?.cancel()
            toast = Toast.makeText(context, "${getString(R.string.download_started)} ${getString(R.string.eruda_plugin_code)}$version", Toast.LENGTH_SHORT)
            toast!!.show()
            val erudaPluginCodeStr = try {
                Http.get("https://cdn.jsdelivr.net/npm/eruda-code@$version/eruda-code.min.js")
            } catch(e: Exception) {
                Log.e(BuildConfig.APPLICATION_ID, getString(R.string.download_failed), e)
                null
            }
            if (erudaPluginCodeStr != null) {
                toast?.cancel()
                toast = Toast.makeText(context, getString(R.string.download_completed), Toast.LENGTH_SHORT)
                toast!!.show()
                with(modulePrefs) {
                    name("resources_eruda_plugin_code_$version")
                    putString("eruda_plugin_code", erudaPluginCodeStr)
                    name("resources")
                    try { put(ResourcesSP.eruda_plugin_code_versions, version) } catch (e: ValueAlreadyExistedInSet) {
                        toast?.cancel()
                        toast = Toast.makeText(context, getString(R.string.the_target_version_already_exists_it_will_be_overwritten), Toast.LENGTH_SHORT)
                        toast!!.show()
                    }
                }
                refresh()
            } else {
                toast?.cancel()
                toast = Toast.makeText(context, getString(R.string.download_failed), Toast.LENGTH_SHORT)
                toast!!.show()
            }
        } }
        viewBinding.resourcesErudaPluginBenchmarkDownload.setOnClickListener { lifecycleScope.launch(Dispatchers.Main) {
            val version = viewBinding.resourcesErudaPluginBenchmarkVersion.selectedItem as String
            toast?.cancel()
            toast = Toast.makeText(context, "${getString(R.string.download_started)} ${getString(R.string.eruda_plugin_benchmark)}$version", Toast.LENGTH_SHORT)
            toast!!.show()
            val erudaPluginBenchmarkStr = try {
                Http.get("https://cdn.jsdelivr.net/npm/eruda-benchmark@$version/eruda-benchmark.min.js")
            } catch(e: Exception) {
                Log.e(BuildConfig.APPLICATION_ID, getString(R.string.download_failed), e)
                null
            }
            if (erudaPluginBenchmarkStr != null) {
                toast?.cancel()
                toast = Toast.makeText(context, getString(R.string.download_completed), Toast.LENGTH_SHORT)
                toast!!.show()
                with(modulePrefs) {
                    name("resources_eruda_plugin_benchmark_$version")
                    putString("eruda_plugin_benchmark", erudaPluginBenchmarkStr)
                    name("resources")
                    try { put(ResourcesSP.eruda_plugin_benchmark_versions, version) } catch (e: ValueAlreadyExistedInSet) {
                        toast?.cancel()
                        toast = Toast.makeText(context, getString(R.string.the_target_version_already_exists_it_will_be_overwritten), Toast.LENGTH_SHORT)
                        toast!!.show()
                    }
                }
                refresh()
            } else {
                toast?.cancel()
                toast = Toast.makeText(context, getString(R.string.download_failed), Toast.LENGTH_SHORT)
                toast!!.show()
            }
        } }
        viewBinding.resourcesErudaPluginGeolocationDownload.setOnClickListener { lifecycleScope.launch(Dispatchers.Main) {
            val version = viewBinding.resourcesErudaPluginGeolocationVersion.selectedItem as String
            toast?.cancel()
            toast = Toast.makeText(context, "${getString(R.string.download_started)} ${getString(R.string.eruda_plugin_geolocation)}$version", Toast.LENGTH_SHORT)
            toast!!.show()
            val erudaPluginGeolocationStr = try {
                Http.get("https://cdn.jsdelivr.net/npm/eruda-geolocation@$version/eruda-geolocation.min.js")
            } catch(e: Exception) {
                Log.e(BuildConfig.APPLICATION_ID, getString(R.string.download_failed), e)
                null
            }
            if (erudaPluginGeolocationStr != null) {
                toast?.cancel()
                toast = Toast.makeText(context, getString(R.string.download_completed), Toast.LENGTH_SHORT)
                toast!!.show()
                with(modulePrefs) {
                    name("resources_eruda_plugin_geolocation_$version")
                    putString("eruda_plugin_geolocation", erudaPluginGeolocationStr)
                    name("resources")
                    try { put(ResourcesSP.eruda_plugin_geolocation_versions, version) } catch (e: ValueAlreadyExistedInSet) {
                        toast?.cancel()
                        toast = Toast.makeText(context, getString(R.string.the_target_version_already_exists_it_will_be_overwritten), Toast.LENGTH_SHORT)
                        toast!!.show()
                    }
                }
                refresh()
            } else {
                toast?.cancel()
                toast = Toast.makeText(context, getString(R.string.download_failed), Toast.LENGTH_SHORT)
                toast!!.show()
            }
        } }
        viewBinding.resourcesErudaPluginDomDownload.setOnClickListener { lifecycleScope.launch(Dispatchers.Main) {
            val version = viewBinding.resourcesErudaPluginDomVersion.selectedItem as String
            toast?.cancel()
            toast = Toast.makeText(context, "${getString(R.string.download_started)} ${getString(R.string.eruda_plugin_dom)}$version", Toast.LENGTH_SHORT)
            toast!!.show()
            val erudaPluginDomStr = try {
                Http.get("https://cdn.jsdelivr.net/npm/eruda-dom@$version/eruda-dom.min.js")
            } catch(e: Exception) {
                Log.e(BuildConfig.APPLICATION_ID, getString(R.string.download_failed), e)
                null
            }
            if (erudaPluginDomStr != null) {
                toast?.cancel()
                toast = Toast.makeText(context, getString(R.string.download_completed), Toast.LENGTH_SHORT)
                toast!!.show()
                with(modulePrefs) {
                    name("resources_eruda_plugin_dom_$version")
                    putString("eruda_plugin_dom", erudaPluginDomStr)
                    name("resources")
                    try { put(ResourcesSP.eruda_plugin_dom_versions, version) } catch (e: ValueAlreadyExistedInSet) {
                        toast?.cancel()
                        toast = Toast.makeText(context, getString(R.string.the_target_version_already_exists_it_will_be_overwritten), Toast.LENGTH_SHORT)
                        toast!!.show()
                    }
                }
                refresh()
            } else {
                toast?.cancel()
                toast = Toast.makeText(context, getString(R.string.download_failed), Toast.LENGTH_SHORT)
                toast!!.show()
            }
        } }
        viewBinding.resourcesErudaPluginOrientationDownload.setOnClickListener { lifecycleScope.launch(Dispatchers.Main) {
            val version = viewBinding.resourcesErudaPluginOrientationVersion.selectedItem as String
            toast?.cancel()
            toast = Toast.makeText(context, "${getString(R.string.download_started)} ${getString(R.string.eruda_plugin_orientation)}$version", Toast.LENGTH_SHORT)
            toast!!.show()
            val erudaPluginOrientationStr = try {
                Http.get("https://cdn.jsdelivr.net/npm/eruda-orientation@$version/eruda-orientation.min.js")
            } catch(e: Exception) {
                Log.e(BuildConfig.APPLICATION_ID, getString(R.string.download_failed), e)
                null
            }
            if (erudaPluginOrientationStr != null) {
                toast?.cancel()
                toast = Toast.makeText(context, getString(R.string.download_completed), Toast.LENGTH_SHORT)
                toast!!.show()
                with(modulePrefs) {
                    name("resources_eruda_plugin_orientation_$version")
                    putString("eruda_plugin_orientation", erudaPluginOrientationStr)
                    name("resources")
                    try { put(ResourcesSP.eruda_plugin_orientation_versions, version) } catch (e: ValueAlreadyExistedInSet) {
                        toast?.cancel()
                        toast = Toast.makeText(context, getString(R.string.the_target_version_already_exists_it_will_be_overwritten), Toast.LENGTH_SHORT)
                        toast!!.show()
                    }
                }
                refresh()
            } else {
                toast?.cancel()
                toast = Toast.makeText(context, getString(R.string.download_failed), Toast.LENGTH_SHORT)
                toast!!.show()
            }
        } }
        viewBinding.resourcesErudaPluginTouchesDownload.setOnClickListener { lifecycleScope.launch(Dispatchers.Main) {
            val version = viewBinding.resourcesErudaPluginTouchesVersion.selectedItem as String
            toast?.cancel()
            toast = Toast.makeText(context, "${getString(R.string.download_started)} ${getString(R.string.eruda_plugin_touches)}$version", Toast.LENGTH_SHORT)
            toast!!.show()
            val erudaPluginTouchesStr = try {
                Http.get("https://cdn.jsdelivr.net/npm/eruda-touches@$version/eruda-touches.min.js")
            } catch(e: Exception) {
                Log.e(BuildConfig.APPLICATION_ID, getString(R.string.download_failed), e)
                null
            }
            if (erudaPluginTouchesStr != null) {
                toast?.cancel()
                toast = Toast.makeText(context, getString(R.string.download_completed), Toast.LENGTH_SHORT)
                toast!!.show()
                with(modulePrefs) {
                    name("resources_eruda_plugin_touches_$version")
                    putString("eruda_plugin_touches", erudaPluginTouchesStr)
                    name("resources")
                    try { put(ResourcesSP.eruda_plugin_touches_versions, version) } catch (e: ValueAlreadyExistedInSet) {
                        toast?.cancel()
                        toast = Toast.makeText(context, getString(R.string.the_target_version_already_exists_it_will_be_overwritten), Toast.LENGTH_SHORT)
                        toast!!.show()
                    }
                }
                refresh()
            } else {
                toast?.cancel()
                toast = Toast.makeText(context, getString(R.string.download_failed), Toast.LENGTH_SHORT)
                toast!!.show()
            }
        } }
        viewBinding.resourcesNebulaucsdkDownload.setOnClickListener { lifecycleScope.launch(Dispatchers.Main) {
            val version = viewBinding.resourcesNebulaucsdkVersion.selectedItem as String
            toast?.cancel()
            toast = Toast.makeText(context, "${getString(R.string.download_started)} ${getString(R.string.nebulaucsdk)}$version", Toast.LENGTH_SHORT)
            toast!!.show()
            val nebulaUCSDKArm64V8aBin = try {
                Http.getBytes("https://raw.githubusercontent.com/WankkoRee/EnableWebViewDebugging-Rules/master/resources/nebulaucsdk/$version/arm64-v8a.so")
            } catch(e: Exception) {
                Log.e(BuildConfig.APPLICATION_ID, getString(R.string.download_failed), e)
                null
            }
            val nebulaUCSDKArmeabiV7aBin = try {
                Http.getBytes("https://raw.githubusercontent.com/WankkoRee/EnableWebViewDebugging-Rules/master/resources/nebulaucsdk/$version/armeabi-v7a.so")
            } catch(e: Exception) {
                Log.e(BuildConfig.APPLICATION_ID, getString(R.string.download_failed), e)
                null
            }
            if (nebulaUCSDKArm64V8aBin != null && nebulaUCSDKArmeabiV7aBin != null) {
                toast?.cancel()
                toast = Toast.makeText(context, getString(R.string.download_completed), Toast.LENGTH_SHORT)
                toast!!.show()
                with(modulePrefs) {
                    name("resources_nebulaUCSDK_$version")
                    putString("nebulaUCSDK_arm64-v8a", Base64.encodeToString(nebulaUCSDKArm64V8aBin, Base64.NO_WRAP))
                    putString("nebulaUCSDK_armeabi-v7a", Base64.encodeToString(nebulaUCSDKArmeabiV7aBin, Base64.NO_WRAP))
                    name("resources")
                    try { put(ResourcesSP.nebulaUCSDK_versions, version) } catch (e: ValueAlreadyExistedInSet) {
                        toast?.cancel()
                        toast = Toast.makeText(context, getString(R.string.the_target_version_already_exists_it_will_be_overwritten), Toast.LENGTH_SHORT)
                        toast!!.show()
                    }
                }
                refresh()
            } else {
                toast?.cancel()
                toast = Toast.makeText(context, getString(R.string.download_failed), Toast.LENGTH_SHORT)
                toast!!.show()
            }
        } }
    }

    private fun refresh() {
        val vConsoleVersions: HashSet<String>
        val vConsolePluginSourcesVersions: HashSet<String>
        val vConsolePluginStatsVersions: HashSet<String>
        val vConsolePluginVueDevtoolsVersions: HashSet<String>
        val vConsolePluginOutputlogVersions: HashSet<String>
        val erudaVersions: HashSet<String>
        val erudaPluginFpsVersions: HashSet<String>
        val erudaPluginFeaturesVersions: HashSet<String>
        val erudaPluginTimingVersions: HashSet<String>
        val erudaPluginMemoryVersions: HashSet<String>
        val erudaPluginCodeVersions: HashSet<String>
        val erudaPluginBenchmarkVersions: HashSet<String>
        val erudaPluginGeolocationVersions: HashSet<String>
        val erudaPluginDomVersions: HashSet<String>
        val erudaPluginOrientationVersions: HashSet<String>
        val erudaPluginTouchesVersions: HashSet<String>
        val nebulaUCSDKVersions: HashSet<String>
        with(modulePrefs("resources")) {
            vConsoleVersions = getSet(ResourcesSP.vConsole_versions)
            vConsolePluginSourcesVersions = getSet(ResourcesSP.vConsole_plugin_sources_versions)
            vConsolePluginStatsVersions = getSet(ResourcesSP.vConsole_plugin_stats_versions)
            vConsolePluginVueDevtoolsVersions = getSet(ResourcesSP.vConsole_plugin_vue_devtools_versions)
            vConsolePluginOutputlogVersions = getSet(ResourcesSP.vConsole_plugin_outputlog_versions)
            erudaVersions = getSet(ResourcesSP.eruda_versions)
            erudaPluginFpsVersions = getSet(ResourcesSP.eruda_plugin_fps_versions)
            erudaPluginFeaturesVersions = getSet(ResourcesSP.eruda_plugin_features_versions)
            erudaPluginTimingVersions = getSet(ResourcesSP.eruda_plugin_timing_versions)
            erudaPluginMemoryVersions = getSet(ResourcesSP.eruda_plugin_memory_versions)
            erudaPluginCodeVersions = getSet(ResourcesSP.eruda_plugin_code_versions)
            erudaPluginBenchmarkVersions = getSet(ResourcesSP.eruda_plugin_benchmark_versions)
            erudaPluginGeolocationVersions = getSet(ResourcesSP.eruda_plugin_geolocation_versions)
            erudaPluginDomVersions = getSet(ResourcesSP.eruda_plugin_dom_versions)
            erudaPluginOrientationVersions = getSet(ResourcesSP.eruda_plugin_orientation_versions)
            erudaPluginTouchesVersions = getSet(ResourcesSP.eruda_plugin_touches_versions)
            nebulaUCSDKVersions = getSet(ResourcesSP.nebulaUCSDK_versions)
        }
        viewBinding.resourcesVconsoleLocal.apply {
            removeAllViews()
            vConsoleVersions.forEach { vConsoleVersion ->
                addView(Tag(context).apply {
                    text = vConsoleVersion
                    color = getColor(R.color.backgroundInfo)
                    setOnLongClickListener { t ->
                        val version = (t as Tag).text as String
                        with(modulePrefs) {
                            name("apps")
                            getSet(AppsSP.enabled).forEach { pkg ->
                                name("apps_$pkg")
                                if (get(AppSP.vConsole) && get(AppSP.vConsole_version) == version) {
                                    toast?.cancel()
                                    toast = Toast.makeText(context, getString(R.string.delete_failed)+'\n'+getString(R.string.because_s_is_using_it).format(pkg), Toast.LENGTH_SHORT)
                                    toast!!.show()
                                    return@setOnLongClickListener true
                                }
                            }
                            name("resources")
                            remove(ResourcesSP.vConsole_versions, version)
                            name("resources_vConsole_$version")
                            remove("vConsole")
                        }
                        toast?.cancel()
                        toast = Toast.makeText(context, getString(R.string.delete_completed), Toast.LENGTH_SHORT)
                        toast!!.show()
                        refresh()
                        true
                    }
                })
            }
        }
        viewBinding.resourcesVconsolePluginSourcesLocal.apply {
            removeAllViews()
            vConsolePluginSourcesVersions.forEach { vConsolePluginSourcesVersion ->
                addView(Tag(context).apply {
                    text = vConsolePluginSourcesVersion
                    color = getColor(R.color.backgroundInfo)
                    setOnLongClickListener { t ->
                        val version = (t as Tag).text as String
                        with(modulePrefs) {
                            name("apps")
                            getSet(AppsSP.enabled).forEach { pkg ->
                                name("apps_$pkg")
                                if (get(AppSP.vConsole_plugin_sources) && get(AppSP.vConsole_plugin_sources_version) == version) {
                                    toast?.cancel()
                                    toast = Toast.makeText(context, getString(R.string.delete_failed)+'\n'+getString(R.string.because_s_is_using_it).format(pkg), Toast.LENGTH_SHORT)
                                    toast!!.show()
                                    return@setOnLongClickListener true
                                }
                            }
                            name("resources")
                            remove(ResourcesSP.vConsole_plugin_sources_versions, version)
                            name("resources_vConsole_plugin_sources_$version")
                            remove("vConsole_plugin_sources")
                        }
                        toast?.cancel()
                        toast = Toast.makeText(context, getString(R.string.delete_completed), Toast.LENGTH_SHORT)
                        toast!!.show()
                        refresh()
                        true
                    }
                })
            }
        }
        viewBinding.resourcesVconsolePluginStatsLocal.apply {
            removeAllViews()
            vConsolePluginStatsVersions.forEach { vConsolePluginStatsVersion ->
                addView(Tag(context).apply {
                    text = vConsolePluginStatsVersion
                    color = getColor(R.color.backgroundInfo)
                    setOnLongClickListener { t ->
                        val version = (t as Tag).text as String
                        with(modulePrefs) {
                            name("apps")
                            getSet(AppsSP.enabled).forEach { pkg ->
                                name("apps_$pkg")
                                if (get(AppSP.vConsole_plugin_stats) && get(AppSP.vConsole_plugin_stats_version) == version) {
                                    toast?.cancel()
                                    toast = Toast.makeText(context, getString(R.string.delete_failed)+'\n'+getString(R.string.because_s_is_using_it).format(pkg), Toast.LENGTH_SHORT)
                                    toast!!.show()
                                    return@setOnLongClickListener true
                                }
                            }
                            name("resources")
                            remove(ResourcesSP.vConsole_plugin_stats_versions, version)
                            name("resources_vConsole_plugin_stats_$version")
                            remove("vConsole_plugin_stats")
                        }
                        toast?.cancel()
                        toast = Toast.makeText(context, getString(R.string.delete_completed), Toast.LENGTH_SHORT)
                        toast!!.show()
                        refresh()
                        true
                    }
                })
            }
        }
        viewBinding.resourcesVconsolePluginVueDevtoolsLocal.apply {
            removeAllViews()
            vConsolePluginVueDevtoolsVersions.forEach { vConsolePluginVueDevtoolsVersion ->
                addView(Tag(context).apply {
                    text = vConsolePluginVueDevtoolsVersion
                    color = getColor(R.color.backgroundInfo)
                    setOnLongClickListener { t ->
                        val version = (t as Tag).text as String
                        with(modulePrefs) {
                            name("apps")
                            getSet(AppsSP.enabled).forEach { pkg ->
                                name("apps_$pkg")
                                if (get(AppSP.vConsole_plugin_vue_devtools) && get(AppSP.vConsole_plugin_vue_devtools_version) == version) {
                                    toast?.cancel()
                                    toast = Toast.makeText(context, getString(R.string.delete_failed)+'\n'+getString(R.string.because_s_is_using_it).format(pkg), Toast.LENGTH_SHORT)
                                    toast!!.show()
                                    return@setOnLongClickListener true
                                }
                            }
                            name("resources")
                            remove(ResourcesSP.vConsole_plugin_vue_devtools_versions, version)
                            name("resources_vConsole_plugin_vue_devtools_$version")
                            remove("vConsole_plugin_vue_devtools")
                        }
                        toast?.cancel()
                        toast = Toast.makeText(context, getString(R.string.delete_completed), Toast.LENGTH_SHORT)
                        toast!!.show()
                        refresh()
                        true
                    }
                })
            }
        }
        viewBinding.resourcesVconsolePluginOutputlogLocal.apply {
            removeAllViews()
            vConsolePluginOutputlogVersions.forEach { vConsolePluginOutputlogVersion ->
                addView(Tag(context).apply {
                    text = vConsolePluginOutputlogVersion
                    color = getColor(R.color.backgroundInfo)
                    setOnLongClickListener { t ->
                        val version = (t as Tag).text as String
                        with(modulePrefs) {
                            name("apps")
                            getSet(AppsSP.enabled).forEach { pkg ->
                                name("apps_$pkg")
                                if (get(AppSP.vConsole_plugin_outputlog) && get(AppSP.vConsole_plugin_outputlog_version) == version) {
                                    toast?.cancel()
                                    toast = Toast.makeText(context, getString(R.string.delete_failed)+'\n'+getString(R.string.because_s_is_using_it).format(pkg), Toast.LENGTH_SHORT)
                                    toast!!.show()
                                    return@setOnLongClickListener true
                                }
                            }
                            name("resources")
                            remove(ResourcesSP.vConsole_plugin_outputlog_versions, version)
                            name("resources_vConsole_plugin_outputlog_$version")
                            remove("vConsole_plugin_outputlog")
                        }
                        toast?.cancel()
                        toast = Toast.makeText(context, getString(R.string.delete_completed), Toast.LENGTH_SHORT)
                        toast!!.show()
                        refresh()
                        true
                    }
                })
            }
        }
        viewBinding.resourcesErudaLocal.apply {
            removeAllViews()
            erudaVersions.forEach { erudaVersion ->
                addView(Tag(context).apply {
                    text = erudaVersion
                    color = getColor(R.color.backgroundInfo)
                    setOnLongClickListener { t ->
                        val version = (t as Tag).text as String
                        with(modulePrefs) {
                            name("apps")
                            getSet(AppsSP.enabled).forEach { pkg ->
                                name("apps_$pkg")
                                if (get(AppSP.eruda) && get(AppSP.eruda_version) == version) {
                                    toast?.cancel()
                                    toast = Toast.makeText(context, getString(R.string.delete_failed)+'\n'+getString(R.string.because_s_is_using_it).format(pkg), Toast.LENGTH_SHORT)
                                    toast!!.show()
                                    return@setOnLongClickListener true
                                }
                            }
                            name("resources")
                            remove(ResourcesSP.eruda_versions, version)
                            name("resources_eruda_$version")
                            remove("eruda")
                        }
                        toast?.cancel()
                        toast = Toast.makeText(context, getString(R.string.delete_completed), Toast.LENGTH_SHORT)
                        toast!!.show()
                        refresh()
                        true
                    }
                })
            }
        }
        viewBinding.resourcesErudaPluginFpsLocal.apply {
            removeAllViews()
            erudaPluginFpsVersions.forEach { erudaPluginFpsVersion ->
                addView(Tag(context).apply {
                    text = erudaPluginFpsVersion
                    color = getColor(R.color.backgroundInfo)
                    setOnLongClickListener { t ->
                        val version = (t as Tag).text as String
                        with(modulePrefs) {
                            name("apps")
                            getSet(AppsSP.enabled).forEach { pkg ->
                                name("apps_$pkg")
                                if (get(AppSP.eruda_plugin_fps) && get(AppSP.eruda_plugin_fps_version) == version) {
                                    toast?.cancel()
                                    toast = Toast.makeText(context, getString(R.string.delete_failed)+'\n'+getString(R.string.because_s_is_using_it).format(pkg), Toast.LENGTH_SHORT)
                                    toast!!.show()
                                    return@setOnLongClickListener true
                                }
                            }
                            name("resources")
                            remove(ResourcesSP.eruda_plugin_fps_versions, version)
                            name("resources_eruda_plugin_fps_$version")
                            remove("eruda_plugin_fps")
                        }
                        toast?.cancel()
                        toast = Toast.makeText(context, getString(R.string.delete_completed), Toast.LENGTH_SHORT)
                        toast!!.show()
                        refresh()
                        true
                    }
                })
            }
        }
        viewBinding.resourcesErudaPluginFeaturesLocal.apply {
            removeAllViews()
            erudaPluginFeaturesVersions.forEach { erudaPluginFeaturesVersion ->
                addView(Tag(context).apply {
                    text = erudaPluginFeaturesVersion
                    color = getColor(R.color.backgroundInfo)
                    setOnLongClickListener { t ->
                        val version = (t as Tag).text as String
                        with(modulePrefs) {
                            name("apps")
                            getSet(AppsSP.enabled).forEach { pkg ->
                                name("apps_$pkg")
                                if (get(AppSP.eruda_plugin_features) && get(AppSP.eruda_plugin_features_version) == version) {
                                    toast?.cancel()
                                    toast = Toast.makeText(context, getString(R.string.delete_failed)+'\n'+getString(R.string.because_s_is_using_it).format(pkg), Toast.LENGTH_SHORT)
                                    toast!!.show()
                                    return@setOnLongClickListener true
                                }
                            }
                            name("resources")
                            remove(ResourcesSP.eruda_plugin_features_versions, version)
                            name("resources_eruda_plugin_features_$version")
                            remove("eruda_plugin_features")
                        }
                        toast?.cancel()
                        toast = Toast.makeText(context, getString(R.string.delete_completed), Toast.LENGTH_SHORT)
                        toast!!.show()
                        refresh()
                        true
                    }
                })
            }
        }
        viewBinding.resourcesErudaPluginTimingLocal.apply {
            removeAllViews()
            erudaPluginTimingVersions.forEach { erudaPluginTimingVersion ->
                addView(Tag(context).apply {
                    text = erudaPluginTimingVersion
                    color = getColor(R.color.backgroundInfo)
                    setOnLongClickListener { t ->
                        val version = (t as Tag).text as String
                        with(modulePrefs) {
                            name("apps")
                            getSet(AppsSP.enabled).forEach { pkg ->
                                name("apps_$pkg")
                                if (get(AppSP.eruda_plugin_timing) && get(AppSP.eruda_plugin_timing_version) == version) {
                                    toast?.cancel()
                                    toast = Toast.makeText(context, getString(R.string.delete_failed)+'\n'+getString(R.string.because_s_is_using_it).format(pkg), Toast.LENGTH_SHORT)
                                    toast!!.show()
                                    return@setOnLongClickListener true
                                }
                            }
                            name("resources")
                            remove(ResourcesSP.eruda_plugin_timing_versions, version)
                            name("resources_eruda_plugin_timing_$version")
                            remove("eruda_plugin_timing")
                        }
                        toast?.cancel()
                        toast = Toast.makeText(context, getString(R.string.delete_completed), Toast.LENGTH_SHORT)
                        toast!!.show()
                        refresh()
                        true
                    }
                })
            }
        }
        viewBinding.resourcesErudaPluginMemoryLocal.apply {
            removeAllViews()
            erudaPluginMemoryVersions.forEach { erudaPluginMemoryVersion ->
                addView(Tag(context).apply {
                    text = erudaPluginMemoryVersion
                    color = getColor(R.color.backgroundInfo)
                    setOnLongClickListener { t ->
                        val version = (t as Tag).text as String
                        with(modulePrefs) {
                            name("apps")
                            getSet(AppsSP.enabled).forEach { pkg ->
                                name("apps_$pkg")
                                if (get(AppSP.eruda_plugin_memory) && get(AppSP.eruda_plugin_memory_version) == version) {
                                    toast?.cancel()
                                    toast = Toast.makeText(context, getString(R.string.delete_failed)+'\n'+getString(R.string.because_s_is_using_it).format(pkg), Toast.LENGTH_SHORT)
                                    toast!!.show()
                                    return@setOnLongClickListener true
                                }
                            }
                            name("resources")
                            remove(ResourcesSP.eruda_plugin_memory_versions, version)
                            name("resources_eruda_plugin_memory_$version")
                            remove("eruda_plugin_memory")
                        }
                        toast?.cancel()
                        toast = Toast.makeText(context, getString(R.string.delete_completed), Toast.LENGTH_SHORT)
                        toast!!.show()
                        refresh()
                        true
                    }
                })
            }
        }
        viewBinding.resourcesErudaPluginCodeLocal.apply {
            removeAllViews()
            erudaPluginCodeVersions.forEach { erudaPluginCodeVersion ->
                addView(Tag(context).apply {
                    text = erudaPluginCodeVersion
                    color = getColor(R.color.backgroundInfo)
                    setOnLongClickListener { t ->
                        val version = (t as Tag).text as String
                        with(modulePrefs) {
                            name("apps")
                            getSet(AppsSP.enabled).forEach { pkg ->
                                name("apps_$pkg")
                                if (get(AppSP.eruda_plugin_code) && get(AppSP.eruda_plugin_code_version) == version) {
                                    toast?.cancel()
                                    toast = Toast.makeText(context, getString(R.string.delete_failed)+'\n'+getString(R.string.because_s_is_using_it).format(pkg), Toast.LENGTH_SHORT)
                                    toast!!.show()
                                    return@setOnLongClickListener true
                                }
                            }
                            name("resources")
                            remove(ResourcesSP.eruda_plugin_code_versions, version)
                            name("resources_eruda_plugin_code_$version")
                            remove("eruda_plugin_code")
                        }
                        toast?.cancel()
                        toast = Toast.makeText(context, getString(R.string.delete_completed), Toast.LENGTH_SHORT)
                        toast!!.show()
                        refresh()
                        true
                    }
                })
            }
        }
        viewBinding.resourcesErudaPluginBenchmarkLocal.apply {
            removeAllViews()
            erudaPluginBenchmarkVersions.forEach { erudaPluginBenchmarkVersion ->
                addView(Tag(context).apply {
                    text = erudaPluginBenchmarkVersion
                    color = getColor(R.color.backgroundInfo)
                    setOnLongClickListener { t ->
                        val version = (t as Tag).text as String
                        with(modulePrefs) {
                            name("apps")
                            getSet(AppsSP.enabled).forEach { pkg ->
                                name("apps_$pkg")
                                if (get(AppSP.eruda_plugin_benchmark) && get(AppSP.eruda_plugin_benchmark_version) == version) {
                                    toast?.cancel()
                                    toast = Toast.makeText(context, getString(R.string.delete_failed)+'\n'+getString(R.string.because_s_is_using_it).format(pkg), Toast.LENGTH_SHORT)
                                    toast!!.show()
                                    return@setOnLongClickListener true
                                }
                            }
                            name("resources")
                            remove(ResourcesSP.eruda_plugin_benchmark_versions, version)
                            name("resources_eruda_plugin_benchmark_$version")
                            remove("eruda_plugin_benchmark")
                        }
                        toast?.cancel()
                        toast = Toast.makeText(context, getString(R.string.delete_completed), Toast.LENGTH_SHORT)
                        toast!!.show()
                        refresh()
                        true
                    }
                })
            }
        }
        viewBinding.resourcesErudaPluginGeolocationLocal.apply {
            removeAllViews()
            erudaPluginGeolocationVersions.forEach { erudaPluginGeolocationVersion ->
                addView(Tag(context).apply {
                    text = erudaPluginGeolocationVersion
                    color = getColor(R.color.backgroundInfo)
                    setOnLongClickListener { t ->
                        val version = (t as Tag).text as String
                        with(modulePrefs) {
                            name("apps")
                            getSet(AppsSP.enabled).forEach { pkg ->
                                name("apps_$pkg")
                                if (get(AppSP.eruda_plugin_geolocation) && get(AppSP.eruda_plugin_geolocation_version) == version) {
                                    toast?.cancel()
                                    toast = Toast.makeText(context, getString(R.string.delete_failed)+'\n'+getString(R.string.because_s_is_using_it).format(pkg), Toast.LENGTH_SHORT)
                                    toast!!.show()
                                    return@setOnLongClickListener true
                                }
                            }
                            name("resources")
                            remove(ResourcesSP.eruda_plugin_geolocation_versions, version)
                            name("resources_eruda_plugin_geolocation_$version")
                            remove("eruda_plugin_geolocation")
                        }
                        toast?.cancel()
                        toast = Toast.makeText(context, getString(R.string.delete_completed), Toast.LENGTH_SHORT)
                        toast!!.show()
                        refresh()
                        true
                    }
                })
            }
        }
        viewBinding.resourcesErudaPluginDomLocal.apply {
            removeAllViews()
            erudaPluginDomVersions.forEach { erudaPluginDomVersion ->
                addView(Tag(context).apply {
                    text = erudaPluginDomVersion
                    color = getColor(R.color.backgroundInfo)
                    setOnLongClickListener { t ->
                        val version = (t as Tag).text as String
                        with(modulePrefs) {
                            name("apps")
                            getSet(AppsSP.enabled).forEach { pkg ->
                                name("apps_$pkg")
                                if (get(AppSP.eruda_plugin_dom) && get(AppSP.eruda_plugin_dom_version) == version) {
                                    toast?.cancel()
                                    toast = Toast.makeText(context, getString(R.string.delete_failed)+'\n'+getString(R.string.because_s_is_using_it).format(pkg), Toast.LENGTH_SHORT)
                                    toast!!.show()
                                    return@setOnLongClickListener true
                                }
                            }
                            name("resources")
                            remove(ResourcesSP.eruda_plugin_dom_versions, version)
                            name("resources_eruda_plugin_dom_$version")
                            remove("eruda_plugin_dom")
                        }
                        toast?.cancel()
                        toast = Toast.makeText(context, getString(R.string.delete_completed), Toast.LENGTH_SHORT)
                        toast!!.show()
                        refresh()
                        true
                    }
                })
            }
        }
        viewBinding.resourcesErudaPluginOrientationLocal.apply {
            removeAllViews()
            erudaPluginOrientationVersions.forEach { erudaPluginOrientationVersion ->
                addView(Tag(context).apply {
                    text = erudaPluginOrientationVersion
                    color = getColor(R.color.backgroundInfo)
                    setOnLongClickListener { t ->
                        val version = (t as Tag).text as String
                        with(modulePrefs) {
                            name("apps")
                            getSet(AppsSP.enabled).forEach { pkg ->
                                name("apps_$pkg")
                                if (get(AppSP.eruda_plugin_orientation) && get(AppSP.eruda_plugin_orientation_version) == version) {
                                    toast?.cancel()
                                    toast = Toast.makeText(context, getString(R.string.delete_failed)+'\n'+getString(R.string.because_s_is_using_it).format(pkg), Toast.LENGTH_SHORT)
                                    toast!!.show()
                                    return@setOnLongClickListener true
                                }
                            }
                            name("resources")
                            remove(ResourcesSP.eruda_plugin_orientation_versions, version)
                            name("resources_eruda_plugin_orientation_$version")
                            remove("eruda_plugin_orientation")
                        }
                        toast?.cancel()
                        toast = Toast.makeText(context, getString(R.string.delete_completed), Toast.LENGTH_SHORT)
                        toast!!.show()
                        refresh()
                        true
                    }
                })
            }
        }
        viewBinding.resourcesErudaPluginTouchesLocal.apply {
            removeAllViews()
            erudaPluginTouchesVersions.forEach { erudaPluginTouchesVersion ->
                addView(Tag(context).apply {
                    text = erudaPluginTouchesVersion
                    color = getColor(R.color.backgroundInfo)
                    setOnLongClickListener { t ->
                        val version = (t as Tag).text as String
                        with(modulePrefs) {
                            name("apps")
                            getSet(AppsSP.enabled).forEach { pkg ->
                                name("apps_$pkg")
                                if (get(AppSP.eruda_plugin_touches) && get(AppSP.eruda_plugin_touches_version) == version) {
                                    toast?.cancel()
                                    toast = Toast.makeText(context, getString(R.string.delete_failed)+'\n'+getString(R.string.because_s_is_using_it).format(pkg), Toast.LENGTH_SHORT)
                                    toast!!.show()
                                    return@setOnLongClickListener true
                                }
                            }
                            name("resources")
                            remove(ResourcesSP.eruda_plugin_touches_versions, version)
                            name("resources_eruda_plugin_touches_$version")
                            remove("eruda_plugin_touches")
                        }
                        toast?.cancel()
                        toast = Toast.makeText(context, getString(R.string.delete_completed), Toast.LENGTH_SHORT)
                        toast!!.show()
                        refresh()
                        true
                    }
                })
            }
        }
        viewBinding.resourcesNebulaucsdkLocal.apply {
            removeAllViews()
            nebulaUCSDKVersions.forEach { nebulaUCSDKVersion ->
                addView(Tag(context).apply {
                    text = nebulaUCSDKVersion
                    color = getColor(R.color.backgroundInfo)
                    setOnLongClickListener { t ->
                        val version = (t as Tag).text as String
                        with(modulePrefs) {
                            name("apps")
                            getSet(AppsSP.enabled).forEach { pkg ->
                                name("apps_$pkg")
                                if (get(AppSP.nebulaUCSDK) && get(AppSP.nebulaUCSDK_version) == version) {
                                    toast?.cancel()
                                    toast = Toast.makeText(context, getString(R.string.delete_failed)+'\n'+getString(R.string.because_s_is_using_it).format(pkg), Toast.LENGTH_SHORT)
                                    toast!!.show()
                                    return@setOnLongClickListener true
                                }
                            }
                            name("resources")
                            remove(ResourcesSP.nebulaUCSDK_versions, version)
                            name("resources_nebulaUCSDK_$version")
                            remove("nebulaUCSDK_arm64-v8a")
                            remove("nebulaUCSDK_armeabi-v7a")
                        }
                        toast?.cancel()
                        toast = Toast.makeText(context, getString(R.string.delete_completed), Toast.LENGTH_SHORT)
                        toast!!.show()
                        refresh()
                        true
                    }
                })
            }
        }
    }

    private fun tips() {
        toast?.cancel()
        toast = Toast.makeText(context, getString(R.string.please_click_the_download_button_instead_of_here_long_press_on_a_version_tag_to_delete_it), Toast.LENGTH_SHORT)
        toast!!.show()
    }
}