package pl.szkoleniaandroid.billexpert

import android.app.Activity
import android.app.Instrumentation
import androidx.test.InstrumentationRegistry
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.clearText
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.rule.GrantPermissionRule
import com.jraska.falcon.FalconSpoonRule
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import org.hamcrest.CoreMatchers.allOf
import org.junit.Rule
import org.junit.Test
import pl.szkoleniaandroid.billexpert.api.*
import pl.szkoleniaandroid.billexpert.features.bills.BillsActivity
import pl.szkoleniaandroid.billexpert.features.signin.LoginActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivityTest {


    val loginResponse = LoginResponse(
            "username", "", "", "objectId", "token"
    )

    val loginCall: Call<LoginResponse> = mock {
        on { enqueue(any()) } doAnswer {
            (it.getArgument(0) as Callback<LoginResponse>).onResponse(mock(), Response.success(loginResponse))
        }
    }

    @Rule
    @JvmField
    val permissionRule = GrantPermissionRule.grant(android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE)

    @Rule
    @JvmField
    val spoonRule = FalconSpoonRule()

    @Rule
    @JvmField
    val rule = object : IntentsTestRule<LoginActivity>(LoginActivity::class.java) {
        override fun beforeActivityLaunched() {
            super.beforeActivityLaunched()

            val app: App = InstrumentationRegistry.getTargetContext().applicationContext as App
            app.billApi = mock {
                on { getLogin("test", "pass") } doReturn loginCall
            }


        }
    }

    @Test
    fun showErrorOnEmptyUsername() {
        //given
        onView(withId(R.id.username_edit_text)).perform(clearText())
        //when
        R.id.login_button.click()
        //then
        onView(withText("Username can't be empty!")).check(matches(isDisplayed()))
        spoonRule.screenshot(rule.activity, "username_error")



    }

    @Test
    fun goToMainOnValidLogin() {
        //given
        intending(anyIntent())
                .respondWith(Instrumentation.ActivityResult(Activity.RESULT_OK, null))
        //when
        onView(withId(R.id.login_button)).perform(click())
        //then
        intended(allOf(hasComponent(BillsActivity::class.java.canonicalName)))
        //onView(withText("Username can't be empty!")).check(matches(isDisplayed()))

    }
}

fun Int.click() {
    onView(withId(this)).perform(ViewActions.click())
}