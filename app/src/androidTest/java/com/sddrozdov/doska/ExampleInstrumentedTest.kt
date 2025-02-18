package com.sddrozdov.doska

import androidx.test.core.app.ActivityScenario

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions.close
import androidx.test.espresso.contrib.DrawerActions.open
import androidx.test.espresso.contrib.DrawerMatchers.isClosed
import androidx.test.espresso.contrib.DrawerMatchers.isOpen
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import androidx.test.espresso.matcher.ViewMatchers.withId

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith


import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.sddrozdov.doska", appContext.packageName)
    }
}

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @Test
    fun navigationDrawer_opensAndCloses() {
        // Запускаем активность
        val scenario = ActivityScenario.launch(MainActivity::class.java)

        // Проверяем, что Drawer закрыт при старте
        onView(withId(R.id.mainDrawerLayout)).check(matches(isClosed()))

        // Открываем Drawer
        onView(withId(R.id.mainDrawerLayout)).perform(open())

        // Проверяем, что открыт
        onView(withId(R.id.mainDrawerLayout)).check(matches(isOpen()))

        // Закрываем
        onView(withId(R.id.mainDrawerLayout)).perform(close())

        scenario.close()
    }

    @Test
    fun actionBarToggle_syncsWithDrawer() {
        ActivityScenario.launch(MainActivity::class.java).use {
            // Проверяем иконку "бургера"
            onView(withContentDescription(R.string.open)).check(matches(isDisplayed()))

            // Открываем Drawer
            onView(withId(R.id.mainDrawerLayout)).perform(open())

            // Проверяем, что иконка сменилась на "стрелку"
            onView(withContentDescription(R.string.close)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun testNavigationDrawer() {
        ActivityScenario.launch(MainActivity::class.java).use {

        // Открываем Drawer
        onView(withId(R.id.mainDrawerLayout)).perform(open())

        // Проверка клика по элементу
        onView(withId(R.id.menu_ads_cars)).perform(click())
        }

    }

    @Test
    fun menuClick_closesNavigationDrawer() {
        ActivityScenario.launch(MainActivity::class.java).use {
            // Открываем Drawer
            onView(withId(R.id.mainDrawerLayout)).perform(open())

            // Кликаем на любой пункт (например, "Телефоны")
            onView(withId(R.id.menu_ads_phones)).perform(click())

            // Проверяем, что Drawer закрылся
            onView(withId(R.id.mainDrawerLayout)).check(matches(isClosed()))
        }
    }
}

