package pl.szkoleniaandroid.billexpert

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module
import pl.szkoleniaandroid.billexpert.domain.usecase.SignInUseCase
import pl.szkoleniaandroid.billexpert.features.signin.SignInViewModel
import pl.szkoleniaandroid.billexpert.utils.ContextStringProvider
import timber.log.Timber

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        //#INSECURE logs should be only printed when BuildConfig.DEBUG is true
        Timber.plant(Timber.DebugTree())
        AndroidThreeTen.init(this)

        startKoin {
            androidContext(this@App)
            modules(appModule)
        }
    }
}

val appModule = module {
    viewModel {
        SignInViewModel(
                stringProvider = ContextStringProvider(androidApplication()),
                signInUseCase = SignInUseCase())
    }

}