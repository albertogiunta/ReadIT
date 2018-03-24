package com.jaus.albertogiunta.readit

import com.jaus.albertogiunta.readit.utils.polished
import org.junit.Assert
import org.junit.Test

/**
 * Sanity checks for URLs (both for fixable and unfixable urls)
 *
 * ✅ text text text https://www.website.com
 * ✅ https://www.website.com
 * ✅ www.website.com
 * ✅ website.com
 * ❌ text text text www.website.com
 */
class LinkPolishingUnitTest {

    @Test
    fun doNotCorruptCorrectHTTPSLink() {
        Assert.assertEquals("https://www.website.com", "https://www.website.com".polished())
    }

    @Test
    fun doNotCorruptCorrectHTTPLink() {
        Assert.assertEquals("http://www.website.com", "http://www.website.com".polished())
    }

    @Test
    fun addHTTPProtocolToLinkWithoutProtocol() {
        Assert.assertEquals("http://www.website.com", "www.website.com".polished())
        Assert.assertEquals("http://website.com", "website.com".polished())
    }

    @Test
    fun removeBlankSpaceBeforeAndAfterLink() {
        Assert.assertEquals("http://www.website.com", "  www.website .com   ".polished())
    }

    @Test
    fun removeWordsBeforeLink() {
        Assert.assertEquals("https://www.website.com", "text text text https://www.website.com".polished())
    }

    @Test
    fun doNotFixLinkPrecededByWordsAndWithoutProtocol() {
        Assert.assertNotEquals("http://www.website.com", "text text text www.website.com".polished())
        Assert.assertEquals("http://texttexttextwww.website.com", "text text text www.website.com".polished())
    }

}
