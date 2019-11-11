package pl.szkoleniaandroid.billexpert

import android.app.Application
import android.preference.PreferenceManager
import androidx.room.Room
import com.facebook.stetho.Stetho
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.jakewharton.threetenabp.AndroidThreeTen
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.bind
import org.koin.dsl.module
import org.threeten.bp.LocalDateTime
import pl.szkoleniaandroid.billexpert.api.BASE_URL
import pl.szkoleniaandroid.billexpert.api.BillApi
import pl.szkoleniaandroid.billexpert.api.REST_API_KEY
import pl.szkoleniaandroid.billexpert.api.REST_APP_ID
import pl.szkoleniaandroid.billexpert.data.db.BillDatabase
import pl.szkoleniaandroid.billexpert.data.db.BillRoomRepository
import pl.szkoleniaandroid.billexpert.data.db.LOCAL_DATE_TIME_FORMATTER
import pl.szkoleniaandroid.billexpert.data.repositories.ApiRemoteRepository
import pl.szkoleniaandroid.billexpert.data.repositories.SPLocalStorage
import pl.szkoleniaandroid.billexpert.domain.model.LoggedUser
import pl.szkoleniaandroid.billexpert.domain.repositories.BillRepository
import pl.szkoleniaandroid.billexpert.domain.repositories.LocalRepository
import pl.szkoleniaandroid.billexpert.domain.repositories.RemoteRepository
import pl.szkoleniaandroid.billexpert.domain.usecase.SignInUseCase
import pl.szkoleniaandroid.billexpert.features.bills.BillsListViewModel
import pl.szkoleniaandroid.billexpert.features.signin.SignInViewModel
import pl.szkoleniaandroid.billexpert.utils.ContextStringProvider
import pl.szkoleniaandroid.billexpert.utils.StringProvider
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import timber.log.Timber
import java.io.IOException
import java.util.*

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

class Rfc3339LocalDateTimeJsonAdapter : JsonAdapter<LocalDateTime>() {

    @Throws(IOException::class)
    override fun fromJson(reader: JsonReader): LocalDateTime {
        val string = reader.nextString()
        return LocalDateTime.parse(string, LOCAL_DATE_TIME_FORMATTER)
    }

    @Throws(IOException::class)
    override fun toJson(writer: JsonWriter, value: LocalDateTime?) {
        if (value != null) {
            val string = LOCAL_DATE_TIME_FORMATTER.format(value)
            writer.value(string)
        } else {
            writer.nullValue()
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

    single<BillDatabase> {
        Room.databaseBuilder(androidApplication(), BillDatabase::class.java, "bill.db")
                .build()
    }

    single {
        get<BillDatabase>().getBillDao()
    }

    single {
        Moshi.Builder()
                .add(Date::class.java, Rfc3339DateJsonAdapter())
                .add(LocalDateTime::class.java, Rfc3339LocalDateTimeJsonAdapter())
                .build()
    }

    single { _ ->
        val clientBuilder = OkHttpClient.Builder()
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY //#INSECURE should be removed in release
        clientBuilder
                .addInterceptor {
                    val builder = it.request().newBuilder()
                            .addHeader("X-Parse-Application-Id", REST_APP_ID)
                            .addHeader("X-Parse-REST-API-Key", REST_API_KEY)
                    it.proceed(builder.build())
                }
                .addInterceptor(logging)
                .build()
    }

    single {
        Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create(get()))
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .client(get())
                .build()
    }

    single<BillApi> { get<Retrofit>().create(BillApi::class.java) }

    single<RemoteRepository> {
        ApiRemoteRepository(
                billApi = get()
        )
    }

    factory {
        SignInUseCase(
                remoteRepository = get(),
                localRepository = get()
        )
    }

    viewModel {
        SignInViewModel(
                stringProvider = get(),
                signInUseCase = get()
        )
    }

    viewModel {
        BillsListViewModel(
                billApi = get(),
                localRepository = get(),
                billRepository = get(),
                billDao = get()
        )
    }

    single<BillRepository> {
        BillRoomRepository(billDao = get())
    }

}