package pl.szkoleniaandroid.billexpert.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import pl.szkoleniaandroid.billexpert.api.Bill
import pl.szkoleniaandroid.billexpert.api.BillApi
import pl.szkoleniaandroid.billexpert.db.User
import pl.szkoleniaandroid.billexpert.security.hash
import retrofit2.Converter
import retrofit2.HttpException

interface BillsRemoteRepository {
    suspend fun login(username: String, password: String): User
    suspend fun deleteBill(objectId: String)
    suspend fun postBill(bill: Bill): Bill
    suspend fun putBill(bill: Bill): Bill
}

class BillsApiRepository(
        private val billApi: BillApi,
        private val errorConverter: Converter<ResponseBody, ApiError>
) : BillsRemoteRepository {
    override suspend fun putBill(bill: Bill): Bill = handleWithError {
        val reponse = billApi.putBill(bill, bill.objectId)
        bill.copy(updatedAt = bill.updatedAt)
    }

    override suspend fun postBill(bill: Bill): Bill = handleWithError {
        val postBillResponse = billApi.postBill(bill)
        bill.copy(
                objectId = postBillResponse.objectId,
                createdAt = postBillResponse.createdAt,
                updatedAt = postBillResponse.createdAt
        )
    }

    override suspend fun deleteBill(objectId: String): Unit = handleWithError {
        billApi.deleteBill(objectId)
    }

    override suspend fun login(username: String, password: String): User =
            handleWithError {
                val response = billApi.getLogin(username, password)
                User(
                        objectId = response.objectId,
                        username = response.username,
                        password = password.hash(),
                        token = response.sessionToken
                )
            }


    private suspend fun <T> handleWithError(call: suspend () -> T): T = withContext(Dispatchers.IO) {
        return@withContext try {
            call()
        } catch (ex: HttpException) {
            val error = errorConverter.convert(ex.response()!!.errorBody()!!)
            throw RepositoryError(error!!.error)
        }
    }

}

class ApiError(val code: Int, val error: String)

class RepositoryError(message: String) : Exception(message)
