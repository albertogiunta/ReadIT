package com.jaus.albertogiunta.readit

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.jaus.albertogiunta.readit.db.LinkDao
import com.jaus.albertogiunta.readit.viewPresenter.linksList.LinksListCompatActivity
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith



@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    @Rule
    @JvmField
    var activity: ActivityTestRule<LinksListCompatActivity> = ActivityTestRule(LinksListCompatActivity::class.java)

    companion object {
        @BeforeClass
        @JvmStatic
        fun before() {
            val dao: LinkDao = MyApplication.database.linkDao()
            dao.deleteAll()
        }
    }

    @Test
    fun checkLandingScreenOnFirstRun() {
        onView(withId(R.id.landingScreenLayout))
            .check(matches(isDisplayed()))
    }
}
