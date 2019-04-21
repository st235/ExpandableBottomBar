package github.com.st235.expandablebottombar.parsers

import android.graphics.Color
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.test.platform.app.InstrumentationRegistry
import github.com.st235.lib_expandablebottombar.ExpandableBottomBarMenuItem
import github.com.st235.lib_expandablebottombar.parsers.ExpandableBottomBarParser
import org.junit.Before
import org.junit.runner.RunWith
import github.com.st235.lib_expandablebottombar.test.R
import org.hamcrest.collection.IsEmptyCollection.empty
import org.hamcrest.collection.IsIterableContainingInOrder.contains
import org.junit.Assert.assertThat
import org.junit.Test


@RunWith(AndroidJUnit4::class)
@MediumTest
class ExpandableBottomBarParserTest {

    private lateinit var expandableBottomBarParser: ExpandableBottomBarParser

    @Before
    fun setUp() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        expandableBottomBarParser =
            ExpandableBottomBarParser(appContext)
    }

    @Test(expected = RuntimeException::class)
    fun testThatMenuWillThrowExceptionWhenThereIsNoMenu() {
        expandableBottomBarParser.inflate(R.menu.not_menu)
    }

    @Test
    fun testThatMenuWillBeEmptyWhenThereIsNoItems() {
        val items = expandableBottomBarParser.inflate(R.menu.empty_menu)
        assertThat(items, empty())
    }

    @Test
    fun testThatMenuWillBeParserOkWhenThereItemsExists() {
        val items = expandableBottomBarParser.inflate(R.menu.valid_menu)
        val actualItem = ExpandableBottomBarMenuItem(R.id.icon_id, R.drawable.item_icon, R.string.icon_text,
            Color.WHITE)
        assertThat(items, contains(actualItem))
    }
}