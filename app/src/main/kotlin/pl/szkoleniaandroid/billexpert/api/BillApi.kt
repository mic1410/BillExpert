package pl.szkoleniaandroid.billexpert.api

import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

const val REST_APP_ID = "RRQfzogXeuQI2VzK0bqEgn02IElfm3ifCUf1lNQX"
const val REST_API_KEY = "mt4btJUcnmVaEJGzncHqkogm0lDM3n2185UNSjiX"
const val BASE_URL = "https://parseapi.back4app.com/"

interface BillApi {

    @Headers("X-Parse-Revocable-Session: 1")
    @GET("login")
    fun getLogin(@Query("username") username: String,
                 @Query("password") password: String): Deferred<Response<LoginResponse>>


    @POST("classes/Bill")
    fun postBill(@Body bill: Bill): Deferred<Response<PostBillResponse>>

    @GET("classes/Bill")
    fun getBillsForUser(@Query("where") where: String, @Query("limit") limit: Int): Deferred<Response<BillsResponse>>

    @PUT("classes/Bill/{id}")
    fun putBill(@Body bill: Bill, @Path("id") objectId: String): Deferred<Response<PutBillResponse>>

    @DELETE("classes/Bill/{id}")
    fun deleteBill(@Path("id") objectId: String): Deferred<Response<Any>>
}
