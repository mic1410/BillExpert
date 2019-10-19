@file:Suppress("MatchingDeclarationName")
package pl.szkoleniaandroid.billexpert.di

import android.preference.PreferenceManager
import androidx.room.Room
import com.commonsware.cwac.netsecurity.OkHttp3Integrator
import com.commonsware.cwac.netsecurity.TrustManagerBuilder
import com.commonsware.cwac.saferoom.SafeHelperFactory
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.experimental.builder.viewModel
import org.koin.dsl.module.module
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import pl.szkoleniaandroid.billexpert.api.BASE_URL
import pl.szkoleniaandroid.billexpert.api.BillApi
import pl.szkoleniaandroid.billexpert.api.REST_API_KEY
import pl.szkoleniaandroid.billexpert.api.REST_APP_ID
import pl.szkoleniaandroid.billexpert.db.BillDatabase
import pl.szkoleniaandroid.billexpert.db.BillRepository
import pl.szkoleniaandroid.billexpert.db.BillRoomRepository
import pl.szkoleniaandroid.billexpert.db.RoomUserRepository
import pl.szkoleniaandroid.billexpert.db.UserRepository
import pl.szkoleniaandroid.billexpert.features.billdetails.BillDetailsViewModel
import pl.szkoleniaandroid.billexpert.features.bills.BillsListViewModel
import pl.szkoleniaandroid.billexpert.features.bills.BillsViewModel
import pl.szkoleniaandroid.billexpert.features.signin.ContextStringProvider
import pl.szkoleniaandroid.billexpert.features.signin.LoginViewModel
import pl.szkoleniaandroid.billexpert.features.signin.StringProvider
import pl.szkoleniaandroid.billexpert.repository.SessionRepository
import pl.szkoleniaandroid.billexpert.repository.SessionRepositoryImpl
import pl.szkoleniaandroid.billexpert.security.hash
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.IOException
import java.util.*

private const val LOCAL_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
val LOCAL_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(LOCAL_DATE_TIME_FORMAT)!!
@Suppress("MagicNumber")
val MIN_DATE_TIME = LocalDateTime.of(1970, 1, 1, 0, 0)!!

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
    //dao
    single {

        //this is NOT a secure way to supply a key for your encrypted database #INSECURE
        val factory = SafeHelperFactory("c#Qx5SY9cfdaOK@kYYBAt0".hash().reversed().toCharArray())

        Room.databaseBuilder(
                androidContext(),
                BillDatabase::class.java,
                "bill_db"
        )
                .openHelperFactory(factory)
                .build()
    }

    single<BillRepository> {
        BillRoomRepository(get<BillDatabase>().getBillDao())
    }

    single<UserRepository> {
        RoomUserRepository(get<BillDatabase>().getUserDao())
    }

    single<SessionRepository> {
        SessionRepositoryImpl(
                PreferenceManager.getDefaultSharedPreferences(androidContext()),
                get()
        )
    }

    //api
    single {
        Moshi.Builder()
                .add(Date::class.java, Rfc3339DateJsonAdapter())
                .add(LocalDateTime::class.java, Rfc3339LocalDateTimeJsonAdapter())
                .build()
    }
    single { _ ->
        val sessionRepository: SessionRepository = get()
        val tmb = TrustManagerBuilder().withManifestConfig(androidContext())
        val clientBuilder = OkHttpClient.Builder()
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY //#INSECURE should be removed in release
        OkHttp3Integrator.applyTo(tmb, clientBuilder) //net security config enabled for older devices
        clientBuilder
                .addInterceptor {
                    val builder = it.request().newBuilder()
                            .addHeader("X-Parse-Application-Id", REST_APP_ID)
                            .addHeader("X-Parse-REST-API-Key", REST_API_KEY)
                    val currentUser = sessionRepository.currentUser
                    if (currentUser != null) {
                        builder.addHeader("X-Parse-Session-Token", currentUser.token)
                    }
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
    single<StringProvider> { ContextStringProvider(androidContext()) }

    //view models
    viewModel<BillDetailsViewModel>()
    viewModel<BillsListViewModel>()
    viewModel<LoginViewModel>()
    viewModel<BillsViewModel>()
}
