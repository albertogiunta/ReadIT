package com.jaus.albertogiunta.readit

import com.jaus.albertogiunta.readit.model.Link
import com.jaus.albertogiunta.readit.utils.filterAndSortForLinksActivity
import com.jaus.albertogiunta.readit.utils.filterAndSortForNotification
import org.joda.time.DateTime
import org.junit.Assert
import org.junit.Test

class LinkListForNotificationUnitTest {

    @Test
    fun unseenAndUnexpiredLinksForNotificationAreAllShown() {
        val list = listOf(
            Link(id = 0, seen = false, timestamp = DateTime().minusHours(1)),
            Link(id = 1, seen = false, timestamp = DateTime().minusHours(1).minusSeconds(10)),
            Link(id = 2, seen = false, timestamp = DateTime().minusMinutes(3)),
            Link(id = 3, seen = false, timestamp = DateTime().minusSeconds(10))
        )

        Assert.assertEquals(list.size, list.filterAndSortForNotification().size)
        Assert.assertTrue(listOf(0, 1, 2, 3).containsAll(list.filterAndSortForNotification().map { it.id }))
        Assert.assertEquals(listOf(3, 2, 1, 0), list.filterAndSortForNotification().map { it.id })
    }

    @Test
    fun unseenAndSomeExpiredLinksForNotificationArePartiallyShown() {
        val list = listOf(
            Link(id = 0, seen = false, timestamp = DateTime().minusDays(3)),
            Link(id = 1, seen = false, timestamp = DateTime().minusHours(1).minusSeconds(10)),
            Link(id = 2, seen = false, timestamp = DateTime().minusDays(2)),
            Link(id = 3, seen = false, timestamp = DateTime().minusSeconds(10))
        )

        Assert.assertEquals(list.size - 2, list.filterAndSortForNotification().size)
        Assert.assertTrue(listOf(1, 3).containsAll(list.filterAndSortForNotification().map { it.id }))
        Assert.assertEquals(listOf(3, 1), list.filterAndSortForNotification().map { it.id })
    }

    @Test
    fun unseenAndAllExpiredLinksForNotificationAreNotShown() {
        val list = listOf(
            Link(id = 0, seen = false, timestamp = DateTime().minusDays(3)),
            Link(id = 1, seen = false, timestamp = DateTime().minusHours(25)),
            Link(id = 2, seen = false, timestamp = DateTime().minusDays(2)),
            Link(id = 3, seen = false, timestamp = DateTime().minusHours(30))
        )

        Assert.assertTrue(list.filterAndSortForNotification().isEmpty())
    }

    @Test
    fun someSeenAndUnexpiredLinksForNotificationArePartiallyShown() {
        val list = listOf(
            Link(id = 0, seen = false, timestamp = DateTime().minusDays(3)),
            Link(id = 1, seen = true, timestamp = DateTime().minusHours(1).minusSeconds(10)),
            Link(id = 2, seen = false, timestamp = DateTime().minusHours(1)),
            Link(id = 3, seen = true, timestamp = DateTime().minusMinutes(3)),
            Link(id = 4, seen = false, timestamp = DateTime().minusSeconds(10))
        )

        Assert.assertEquals(listOf(4, 2), list.filterAndSortForNotification().map { it.id })

        Assert.assertEquals(listOf(2, 4, 1, 3), list.filterAndSortForLinksActivity(showSeen = true, rewardIsActive = false).map { it.id })

        Assert.assertEquals(listOf(2, 4), list.filterAndSortForLinksActivity(showSeen = false, rewardIsActive = false).map { it.id })

        Assert.assertEquals(listOf(0, 2, 4), list.filterAndSortForLinksActivity(showSeen = false, rewardIsActive = true).map { it.id })

        Assert.assertEquals(listOf(0, 2, 4, 1, 3), list.filterAndSortForLinksActivity(showSeen = true, rewardIsActive = true).map { it.id })
    }

    @Test
    fun someSeenAndSomeExpiredLinksForNotificationArePartiallyShown() {
        val list = listOf(
            Link(id = 0, seen = true, timestamp = DateTime().minusMinutes(40)),
            Link(id = 1, seen = false, timestamp = DateTime().minusDays(2)),
            Link(id = 2, seen = true, timestamp = DateTime().minusDays(2)),
            Link(id = 3, seen = false, timestamp = DateTime().minusHours(3))
        )

        Assert.assertEquals(list.size - 3, list.filterAndSortForNotification().size)
        Assert.assertTrue(listOf(3).containsAll(list.filterAndSortForNotification().map { it.id }))
    }

    @Test
    fun someSeenAndSomeExpiredLinksForNotificationAreNotShown() {
        val list = listOf(
            Link(id = 0, seen = true, timestamp = DateTime()),
            Link(id = 1, seen = false, timestamp = DateTime().minusDays(2)),
            Link(id = 2, seen = true, timestamp = DateTime().minusDays(2)),
            Link(id = 3, seen = true, timestamp = DateTime())
        )

        Assert.assertTrue(list.filterAndSortForNotification().isEmpty())
    }

    @Test
    fun emptyLinksForNotificationAreNotShown() {
        val list = listOf<Link>()
        Assert.assertTrue(list.filterAndSortForNotification().isEmpty())
    }

}