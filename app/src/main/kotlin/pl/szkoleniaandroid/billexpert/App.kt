package pl.szkoleniaandroid.billexpert

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import timber.log.Timber

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        //#INSECURE logs should be only printed when BuildConfig.DEBUG is true
        Timber.plant(Timber.DebugTree())
        AndroidThreeTen.init(this)
    }
}
