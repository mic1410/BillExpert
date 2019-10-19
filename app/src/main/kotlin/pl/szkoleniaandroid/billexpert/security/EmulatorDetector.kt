package pl.szkoleniaandroid.billexpert.security

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Debug
import timber.log.Timber
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.net.NetworkInterface
import java.net.SocketException

/**
 * Source: https://github.com/CalebFenton/AndroidEmulatorDetect
 * References:
 * https://users.ece.cmu.edu/~tvidas/papers/ASIACCS14.pdf
 * http://stackoverflow.com/questions/2799097/how-can-i-detect-when-an-android-application-is-running-in-the-emulator
 * http://webcache.googleusercontent.com/search?q=cache:7NRl_DBrk2AJ:www.oguzhantopgul.com/2014/12/android-malware-evasion-techniques.html+&cd=6&hl=en&ct=clnk&gl=us
 * https://github.com/Fuzion24/AndroidHostileEnvironmentDetection
 */
class EmulatorDetector internal constructor(private val context: Context) {

    //Other builds of android that are not production usually have build types of "eng", "debug", etc...
    //Although this doesn't denote having an emulator, there is a possibility that the user is more intelligent
    //than a normal Android user and thus increases the risk of getting caught .. avoid that situation
    private val isNotUserBuild: Boolean
        get() = "user" != getProp(context, "ro.build.type")

    private val isDebuggerConnected: Boolean
        get() = Debug.isDebuggerConnected()

    val isEmulator: Boolean
        get() = hasEmulatorBuildProp()
                || hasQemuBuildProps()
                || hasQemuCpuInfo()
                || hasQemuFile()
                || hasEth0Interface()
                || isNotUserBuild

    private fun hasEth0Interface(): Boolean {
        try {
            val en = NetworkInterface.getNetworkInterfaces()
            while (en.hasMoreElements()) {
                val intf = en.nextElement()
                if (intf.name == "eth0")
                    return true
            }
        } catch (ex: SocketException) {
            Timber.e(ex)
        }

        return false
    }

    private fun hasQemuCpuInfo(): Boolean {
        try {
            val cpuInfoReader = BufferedReader(FileReader("/proc/cpuinfo"))
            var line: String? = cpuInfoReader.readLine()
            while (line != null) {
                if (line.contains("Goldfish"))
                    return true
                line = cpuInfoReader.readLine()
            }
        } catch (e: IOException) {
            Timber.e(e)
        }

        return false
    }

    private fun hasQemuFile(): Boolean {
        return (File("/init.goldfish.rc").exists()
                || File("/sys/qemu_trace").exists()
                || File("/system/bin/qemud").exists())

    }


    @SuppressLint("PrivateApi")
    @Suppress("TooGenericExceptionCaught", "SpreadOperator")
    private fun getProp(ctx: Context, propName: String): String {
        try {
            val cl = ctx.classLoader
            val klazz = cl.loadClass("android.os.properties")
            val getProp = klazz.getMethod("get", String::class.java)
            val params = arrayOf<Any>(propName)
            return getProp.invoke(klazz, *params) as String
        } catch (e: Exception) {
            Timber.e(e)
            return ""
        }

    }

    private fun hasQemuBuildProps(): Boolean {
        return ("goldfish" == getProp(context, "ro.hardware")
                || "ranchu" == getProp(context, "ro.hardware")
                || "generic" == getProp(context, "ro.product.device")
                || "1" == getProp(context, "ro.kernel.qemu")
                || "0" == getProp(context, "ro.secure"))
    }

    @SuppressLint("HardwareIds")
    private fun hasEmulatorBuildProp(): Boolean {
        return (Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk") || Build.MODEL.contains("sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")
                || Build.PRODUCT.contains("google_sdk") || Build.PRODUCT.contains("sdk")
                || Build.HARDWARE.contains("goldfish")
                || Build.HARDWARE.contains("ranchu")
                || Build.BOARD.contains("unknown")
                || Build.ID.contains("FRF91")
                || Build.MANUFACTURER.contains("unknown")
                || Build.SERIAL == null
                || Build.TAGS.contains("test-keys")
                || Build.USER.contains("android-build"))
    }

//    @SuppressLint("MissingPermission")
//    private fun hasEmulatorTelephonyProperty(): Boolean {
//        val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
//        return ("Android" == tm.networkOperatorName
//                || "Android" == tm.simOperator
//                || "000000000000000" == tm.deviceId || tm.deviceId.matches("^0+$".toRegex())
//                || tm.line1Number.startsWith("155552155")
//                || tm.subscriberId.endsWith("0000000000")
//                || "15552175049" == tm.voiceMailNumber)
//    }
}
