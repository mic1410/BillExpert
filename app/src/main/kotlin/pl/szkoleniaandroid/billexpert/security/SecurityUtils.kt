@file:Suppress("MatchingDeclarationName")
package pl.szkoleniaandroid.billexpert.security

import android.annotation.SuppressLint
import android.content.Context
import com.scottyab.rootbeer.RootBeer
import java.math.BigInteger
import java.security.MessageDigest
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.util.Base64
import pl.szkoleniaandroid.billexpert.BuildConfig
import timber.log.Timber

private const val DIGEST_ALGORITHM_MD5 = "MD5"
private const val MD5_STRING_LENGTH = 32
private const val DIGEST_ALGORITHM_SHA = "SHA"
private const val HEX_RADIX = 16


fun String.hash(): String {
    val md = MessageDigest.getInstance(DIGEST_ALGORITHM_MD5)
    return BigInteger(1, md.digest(toByteArray())).toString(HEX_RADIX).padStart(MD5_STRING_LENGTH, '0')
}

fun antiTamperCheck(context: Context): TamperStatus {
    val rootBeer = RootBeer(context)
    val emulatorDetector = EmulatorDetector(context)
    return when {
//        BuildConfig.DEBUG -> TamperStatus.OK
//        rootBeer.isRooted -> TamperStatus.ROOT
//        emulatorDetector.isEmulator -> TamperStatus.EMULATOR
//        isDebuggable(context) -> TamperStatus.DEBUGGABLE
//        !isValidSignature(context) -> TamperStatus.INVALID_SIGNATURE
        else -> TamperStatus.OK
    }
}

private const val SIGNATURE = "2uRhRxwv13EsWcyjlcGU/4xX+d8="

@Suppress("DEPRECATION", "TooGenericExceptionCaught")
@SuppressLint("PackageManagerGetSignatures")
fun isValidSignature(context: Context): Boolean {
    try {
        val packageInfo = context.packageManager
                .getPackageInfo(context.packageName,
                        PackageManager.GET_SIGNATURES)
        val appSignatures = packageInfo.signatures

        for (signature in appSignatures) {
            val md = MessageDigest.getInstance(DIGEST_ALGORITHM_SHA);
            md.update(signature.toByteArray());
            val currentSignature = Base64.encodeToString(md.digest(), Base64.NO_WRAP);
            //compare signatures
            if (SIGNATURE == currentSignature) return true
        }
    } catch (e: Exception) {
        Timber.e(e)
    }
    return false
}

fun isDebuggable(context: Context) = context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0

enum class TamperStatus(val message: String) {
    OK(""),
    EMULATOR("Emulator detected!"),
    ROOT("Device is rooted!"),
    DEBUGGABLE("Debuggable flag is set!"),
    INVALID_SIGNATURE("App was repackaged!")
}
