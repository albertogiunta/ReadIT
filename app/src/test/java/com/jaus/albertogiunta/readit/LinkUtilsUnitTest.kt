package com.jaus.albertogiunta.readit

import com.jaus.albertogiunta.readit.model.Link
import com.jaus.albertogiunta.readit.utils.Utils
import com.jaus.albertogiunta.readit.utils.faviconURL
import org.junit.Assert
import org.junit.Test

class LinkUtilsUnitTest {

    @Test
    fun extractHostFromURL() {
        Assert.assertEquals(
            "www.google.it",
            Utils.getHostOfURL("https://www.google.it/search?q=test&oq=test&aqs=chrome..69i57j69i60l3j69i65l2.3566j0j1&sourceid=chrome&ie=UTF-8")
        )
        Assert.assertEquals(
            "google.it",
            Utils.getHostOfURL("https://google.it/search?q=test&oq=test&aqs=chrome..69i57j69i60l3j69i65l2.3566j0j1&sourceid=chrome&ie=UTF-8")
        )
    }

    @Test
    fun buildCorrectFaviconURL() {
        Assert.assertEquals(
            "https://www.google.it/favicon.ico",
            Link(url = "https://www.google.it/search?q=test&oq=test&aqs=chrome..69i57j69i60l3j69i65l2.3566j0j1&sourceid=chrome&ie=UTF-8").faviconURL()
        )
    }


}