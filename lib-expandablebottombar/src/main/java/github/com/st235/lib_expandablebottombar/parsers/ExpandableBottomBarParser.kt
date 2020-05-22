package github.com.st235.lib_expandablebottombar.parsers

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.Xml
import github.com.st235.lib_expandablebottombar.ExpandableBottomBarMenuItem
import org.xmlpull.v1.XmlPullParser
import github.com.st235.lib_expandablebottombar.R

internal class ExpandableBottomBarParser(private val context: Context) {
    companion object {
        private const val NO_ID = 0
        private const val NO_TEXT = ""
        private const val NO_COLOR = Color.TRANSPARENT

        private const val MENU_TAG = "menu"
        private const val MENU_ITEM_TAG = "item"
    }

    // We don't use namespaces
    private val namespace: String? = null

    fun inflate(menuId: Int): List<ExpandableBottomBarMenuItem> {
        val parser = context.resources.getLayout(menuId)
        parser.use {
            val attrs = Xml.asAttributeSet(parser)
            return readBottomBar(parser, attrs)
        }
    }

    private fun readBottomBar(parser: XmlPullParser,
                              attrs: AttributeSet): List<ExpandableBottomBarMenuItem> {
        val items = mutableListOf<ExpandableBottomBarMenuItem>()
        var eventType = parser.eventType
        var tagName: String

        // This loop will skip to the menu start tag
        do {
            if (eventType == XmlPullParser.START_TAG) {
                tagName = parser.name

                if (tagName == MENU_TAG) {
                    // Go to next tag
                    eventType = parser.next()
                    break
                }
                throw RuntimeException("Expecting menu, got $tagName")
            }
            eventType = parser.next()
        } while (eventType != XmlPullParser.END_DOCUMENT)

        var reachedEndOfMenu = false
        while (!reachedEndOfMenu) {
            tagName = parser.name

            if (eventType == XmlPullParser.END_TAG) {
                if (tagName == MENU_TAG) {
                    reachedEndOfMenu = true
                }
            }

            if (eventType == XmlPullParser.START_TAG) {
                when (tagName) {
                    MENU_ITEM_TAG -> items.add(readBottomBarItem(parser, attrs))
                }
            }

            eventType = parser.next()
        }

        return items
    }

    private fun readBottomBarItem(parser: XmlPullParser,
                                  attrs: AttributeSet): ExpandableBottomBarMenuItem {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ExpandableBottomBarItem)

        val id = typedArray.getResourceId(R.styleable.ExpandableBottomBarItem_android_id, NO_ID)
        val iconId = typedArray.getResourceId(R.styleable.ExpandableBottomBarItem_android_icon, NO_ID)
        val color = typedArray.getColor(R.styleable.ExpandableBottomBarItem_exb_color, NO_COLOR)
        val text = typedArray.getString(R.styleable.ExpandableBottomBarItem_android_title) ?: NO_TEXT

        typedArray.recycle()

        parser.require(XmlPullParser.START_TAG, namespace, MENU_ITEM_TAG)
        return ExpandableBottomBarMenuItem(id, iconId, text, color)
    }
}
