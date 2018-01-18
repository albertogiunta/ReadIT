package com.jaus.albertogiunta.readit

import com.jaus.albertogiunta.readit.utils.polished
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class LinkInteractionUnitTest {

    @Before
    fun setUp() {
    }

    /**
     * Sanity checks for URLs (both for fixable and unfixable urls)
     *
     * ✅ text text text https://www.website.com
     * ✅ https://www.website.com
     * ✅ www.website.com
     * ✅ website.com
     * ❌ text text text www.website.com
     */
    @Test
    fun urlPolishing_isCorrect() {

        // fixable links
        assertEquals("text text text https://www.website.com".polished(), "https://www.website.com")
        assertEquals("https://www.website.com".polished(), "https://www.website.com")
        assertEquals("www.website.com".polished(), "http://www.website.com")
        assertEquals("  www.website .com   ".polished(), "http://www.website.com")
        assertEquals("website.com".polished(), "http://website.com")

        // unfixable links
        assertEquals(
            "text text text www.website.com".polished(),
            "http://texttexttextwww.website.com"
        )
    }


}
