package pl.szkoleniaandroid.billexpert.api

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
const val REST_APP_ID = "dQ1W6GnwFdNMXFVifFe0KXygfKFdsUlMqvwkRCXf"
const val REST_API_KEY = "1HpxkkHPIdAMAQAVwjAs2W7fA56wU2Q5ipVGLUhx"
const val BASE_URL = "https://parseapi.back4app.com/"

/**
 * Retrofit interface
 */
interface BillApi {

    @Headers("X-Parse-Revocable-Session: 1")
    @GET("login")
    suspend fun getLogin(@Query("username") username: String,
                 @Query("password") password: String): LoginResponse


    @POST("classes/Bill")
    suspend fun postBill(@Body bill: Bill): PostBillResponse

    @GET("classes/Bill")
    suspend fun getBillsForUser(@Query("where") where: String, @Query("limit") limit: Int, @Query("skip") skip: Int, @Query("order") order: String): BillsResponse

    @PUT("classes/Bill/{id}")
    suspend fun putBill(@Body bill: Bill, @Path("id") objectId: String): PutBillResponse

    @DELETE("classes/Bill/{id}")
    suspend fun deleteBill(@Path("id") objectId: String)
}
