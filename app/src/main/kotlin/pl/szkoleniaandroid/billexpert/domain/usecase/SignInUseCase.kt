package pl.szkoleniaandroid.billexpert.domain.usecase

class SignInUseCase {
    operator fun invoke(username: String, password: String): Boolean {
        return (username == "user" && password == "pass")
    }
}