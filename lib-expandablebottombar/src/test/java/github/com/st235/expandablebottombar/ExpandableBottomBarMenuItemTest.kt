package github.com.st235.expandablebottombar

import android.content.Context
import com.nhaarman.mockitokotlin2.mock
import github.com.st235.lib_expandablebottombar.ExpandableBottomBarMenuItem
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class ExpandableBottomBarMenuItemTest {

    private val context = mock<Context>()
    private val builder = ExpandableBottomBarMenuItem.Builder(context)

    @Test(expected = IllegalStateException::class)
    fun `test that creating empty item will rise an exception`() {
        val item = ExpandableBottomBarMenuItem.ItemBuildRequest(builder, context).create()
    }

    @Test(expected = IllegalStateException::class)
    fun `test that creating item with id only will rise an exception`() {
        val item = ExpandableBottomBarMenuItem.ItemBuildRequest(builder, context).id(0).create()
    }

    @Test(expected = IllegalStateException::class)
    fun `test that creating item without color and icon will rise an exception`() {
        val item = ExpandableBottomBarMenuItem.ItemBuildRequest(builder, context).id(0).text("").create()
    }

    @Test(expected = IllegalStateException::class)
    fun `test that creating item without title and icon will rise an exception`() {
        val item = ExpandableBottomBarMenuItem.ItemBuildRequest(builder, context).id(0).color(0).create()
    }

    @Test(expected = IllegalStateException::class)
    fun `test that creating item without color and text will rise an exception`() {
        val item = ExpandableBottomBarMenuItem.ItemBuildRequest(builder, context).id(0).icon(0).create()
    }

    @Test(expected = IllegalStateException::class)
    fun `test that creating item without color will rise an exception`() {
        val item = ExpandableBottomBarMenuItem.ItemBuildRequest(builder, context).id(0).icon(0).text("").create()
    }

    @Test(expected = IllegalStateException::class)
    fun `test that creating item without text rise an exception`() {
        val item = ExpandableBottomBarMenuItem.ItemBuildRequest(builder, context).id(0).color(0).icon(0).create()
    }

    @Test(expected = IllegalStateException::class)
    fun `test that creating item without icon will rise an exception`() {
        val item = ExpandableBottomBarMenuItem.ItemBuildRequest(builder, context).id(0).text("").color(0).create()
    }

    @Test
    fun `test that right created item will not be rise an exception`() {
        val item = ExpandableBottomBarMenuItem.ItemBuildRequest(builder, context).id(1).text("").icon(1).color(1).create()
    }
}