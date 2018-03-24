package com.jaus.albertogiunta.readit

import com.jaus.albertogiunta.readit.utils.getRemainingTime
import com.jaus.albertogiunta.readit.utils.isNotExpired24h
import com.jaus.albertogiunta.readit.utils.toHHmm
import com.jaus.albertogiunta.readit.utils.toLiteralString
import org.joda.time.DateTime
import org.joda.time.Period
import org.junit.Assert
import org.junit.Test

class TimesAndPeriodsUnitTest {

    private var dateTime: DateTime = DateTime.now()

    @Test
    fun remainingTimeForLinkAdded1HourAgo() {
        dateTime = DateTime.now().minusHours(1).withSecondOfMinute(59)

        val expectedRemainingTime = Period(23, 0, 0, 0)
        assertRemainingTime(expectedRemainingTime)

        Assert.assertTrue(dateTime.isNotExpired24h())
    }

    @Test
    fun remainingTimeForLinkAdded23HoursAgo() {
        dateTime = DateTime.now().minusHours(23).withSecondOfMinute(59)

        val expectedRemainingTime = Period(1, 0, 0, 0)
        assertRemainingTime(expectedRemainingTime)

        Assert.assertTrue(dateTime.isNotExpired24h())
    }


    @Test
    fun remainingTimeForLinkAdded25HoursAgo() {
        dateTime = DateTime.now().minusHours(25)

        val expectedRemainingTime = Period(-1, 0, 0, 0)
        assertRemainingTime(expectedRemainingTime)

        Assert.assertFalse(dateTime.isNotExpired24h())
    }


    @Test
    fun extractLiteralExpirationCompactStringFromPeriod() {
        dateTime = DateTime.now().minusHours(10).minusMinutes(23).withSecondOfMinute(59)
        Assert.assertEquals("13h 37m left", dateTime.getRemainingTime().toLiteralString(false))

        dateTime = DateTime.now().minusHours(1).withSecondOfMinute(59)
        Assert.assertEquals("23h left", dateTime.getRemainingTime().toLiteralString(false))

        dateTime = DateTime.now().minusHours(23).minusMinutes(23).withSecondOfMinute(59)
        Assert.assertEquals("37m left", dateTime.getRemainingTime().toLiteralString(false))

        dateTime = DateTime.now().minusDays(5).withSecondOfMinute(59)
        Assert.assertEquals("Expired 3d 23h 59m ago", dateTime.getRemainingTime().toLiteralString(false))

        dateTime = DateTime.now().minusHours(25).minusMinutes(23)
        Assert.assertEquals("Expired 1h 23m ago", dateTime.getRemainingTime().toLiteralString(false))

        dateTime = DateTime.now().minusDays(4).minusHours(3)
        Assert.assertEquals("Expired 3d 3h ago", dateTime.getRemainingTime().toLiteralString(false))

        dateTime = DateTime.now().minusHours(27).minusMinutes(23)
        Assert.assertEquals("Expired 3h 23m ago", dateTime.getRemainingTime().toLiteralString(false))

        dateTime = DateTime.now().minusDays(4)
        Assert.assertEquals("Expired 3d ago", dateTime.getRemainingTime().toLiteralString(false))

        dateTime = DateTime.now().minusHours(25)
        Assert.assertEquals("Expired 1h ago", dateTime.getRemainingTime().toLiteralString(false))

        dateTime = DateTime.now().minusHours(24).minusMinutes(23)
        Assert.assertEquals("Expired 23m ago", dateTime.getRemainingTime().toLiteralString(false))
    }

    @Test
    fun extractLiteralExpirationVerboseStringFromPeriod() {
        dateTime = DateTime.now().minusHours(10).minusMinutes(23).withSecondOfMinute(59)
        Assert.assertEquals("13 hours and 37 minutes left", dateTime.getRemainingTime().toLiteralString(true))

        dateTime = DateTime.now().minusHours(1).withSecondOfMinute(59)
        Assert.assertEquals("23 hours left", dateTime.getRemainingTime().toLiteralString(true))

        dateTime = DateTime.now().minusHours(23).minusMinutes(23).withSecondOfMinute(59)
        Assert.assertEquals("37 minutes left", dateTime.getRemainingTime().toLiteralString(true))

        dateTime = DateTime.now().minusDays(5).withSecondOfMinute(59)
        Assert.assertEquals("Expired 3 days, 23 hours and 59 minutes ago", dateTime.getRemainingTime().toLiteralString(true))

        dateTime = DateTime.now().minusHours(25).minusMinutes(23)
        Assert.assertEquals("Expired 1 hour and 23 minutes ago", dateTime.getRemainingTime().toLiteralString(true))

        dateTime = DateTime.now().minusDays(4).minusHours(3)
        Assert.assertEquals("Expired 3 days and 3 hours ago", dateTime.getRemainingTime().toLiteralString(true))

        dateTime = DateTime.now().minusHours(27).minusMinutes(23)
        Assert.assertEquals("Expired 3 hours and 23 minutes ago", dateTime.getRemainingTime().toLiteralString(true))

        dateTime = DateTime.now().minusDays(4)
        Assert.assertEquals("Expired 3 days ago", dateTime.getRemainingTime().toLiteralString(true))

        dateTime = DateTime.now().minusHours(25)
        Assert.assertEquals("Expired 1 hour ago", dateTime.getRemainingTime().toLiteralString(true))

        dateTime = DateTime.now().minusHours(24).minusMinutes(23)
        Assert.assertEquals("Expired 23 minutes ago", dateTime.getRemainingTime().toLiteralString(true))
    }

    @Test
    fun exactExpirationDateReturnsEmptyString() {
        dateTime = DateTime.now().minusHours(24).withSecondOfMinute(59)
        Assert.assertEquals("", dateTime.getRemainingTime().toLiteralString(true))

        dateTime = DateTime.now().minusHours(24).withSecondOfMinute(59)
        Assert.assertEquals("", dateTime.getRemainingTime().toLiteralString(false))
    }

    @Test
    fun extractHHmmStringFromPeriod() {
        Assert.assertEquals("00:00", Period(0, 0, 0, 0).toHHmm())
        Assert.assertEquals("01:34", Period(1, 34, 2, 4).toHHmm())
        Assert.assertEquals("23:59", Period(23, 59, 0, 0).toHHmm())
        Assert.assertEquals("25:00", Period(25, 0, 0, 0).toHHmm())
    }

    private fun assertRemainingTime(expectedRemainingTime: Period) {
        val actualRemainingTime = dateTime.getRemainingTime()

        Assert.assertEquals(
            Pair(expectedRemainingTime.hours, expectedRemainingTime.minutes),
            Pair(actualRemainingTime.hours, actualRemainingTime.minutes)
        )
    }

}
