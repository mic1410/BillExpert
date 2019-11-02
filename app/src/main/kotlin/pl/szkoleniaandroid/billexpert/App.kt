package pl.szkoleniaandroid.billexpert

import android.app.Application
import android.preference.PreferenceManager
import com.facebook.stetho.Stetho
import com.jakewharton.threetenabp.AndroidThreeTen
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.bind
import org.koin.dsl.module
import pl.szkoleniaandroid.billexpert.data.repositories.SPLocalStorage
import pl.szkoleniaandroid.billexpert.domain.model.LoggedUser
import pl.szkoleniaandroid.billexpert.domain.repositories.LocalRepository
import pl.szkoleniaandroid.billexpert.domain.repositories.RemoteRepository
import pl.szkoleniaandroid.billexpert.domain.usecase.SignInUseCase
import pl.szkoleniaandroid.billexpert.features.signin.SignInViewModel
import pl.szkoleniaandroid.billexpert.utils.ContextStringProvider
import pl.szkoleniaandroid.billexpert.utils.StringProvider
import timber.log.Timber

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        //#INSECURE logs should be only printed when BuildConfig.DEBUG is true
        Timber.plant(Timber.DebugTree())
        AndroidThreeTen.init(this)
        Stetho.initializeWithDefaults(this)

        startKoin {
            androidContext(this@App)
            modules(appModule)
        }
    }
}

val appModule = module {

    single {
        ContextStringProvider(androidApplication())
    } bind StringProvider::class

    single<LocalRepository> {
        SPLocalStorage(
                PreferenceManager.getDefaultSharedPreferences(androidApplication())
        )
    }

    factory {
        SignInUseCase(
                remoteRepository = object : RemoteRepository {
                    override fun login(username: String, password: String): LoggedUser {
                        return LoggedUser("userId", "token")
                    }

                },
                localRepository = get()
        )
    }

    viewModel {
        SignInViewModel(
                stringProvider = get(),
                signInUseCase = get()
        )
    }
}
