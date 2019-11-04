package pl.szkoleniaandroid.billexpert.data.repositories

import pl.szkoleniaandroid.billexpert.api.BillApi
import pl.szkoleniaandroid.billexpert.api.LoginResponse
import pl.szkoleniaandroid.billexpert.domain.model.LoggedUser
import pl.szkoleniaandroid.billexpert.domain.repositories.RemoteRepository

class ApiRemoteRepository(
        private val billApi: BillApi
) : RemoteRepository {

    override suspend fun login(username: String, password: String): LoggedUser {
        return billApi.getLogin(username, password).toDomainModel()
    }


}

fun LoginResponse.toDomainModel(): LoggedUser = LoggedUser(this.objectId, this.sessionToken)