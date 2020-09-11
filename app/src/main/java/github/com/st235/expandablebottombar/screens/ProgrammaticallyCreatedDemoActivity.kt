package github.com.st235.expandablebottombar.screens

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewAnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.ColorUtils
import github.com.st235.expandablebottombar.R
import github.com.st235.lib_expandablebottombar.ExpandableBottomBar
import github.com.st235.lib_expandablebottombar.ExpandableBottomBarMenuItem


class ProgrammaticallyCreatedDemoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_programmatically_declared)

        val color: View = findViewById(R.id.color)
        val bottomBar: ExpandableBottomBar = findViewById(R.id.expandable_bottom_bar)

        color.setBackgroundColor(ColorUtils.setAlphaComponent(Color.GRAY, 60))

        bottomBar.addItems(
                ExpandableBottomBarMenuItem.Builder(this)
                        .addItem(
                            R.id.icon_home,
                            R.drawable.ic_home,
                            R.string.text, Color.GRAY)
                        .addItem(
                            R.id.icon_likes,
                            R.drawable.ic_likes
                        ).textRes(R.string.text2).colorRes(
                        R.color.colorLike
                    ).create()
                        .addItem(
                            R.id.icon_bookmarks,
                            R.drawable.ic_bookmarks,
                            R.string.text3, Color.parseColor("#58a5f0"))
                        .addItem(
                            R.id.icon_settings,
                            R.drawable.ic_settings,
                            R.string.text4, Color.parseColor("#be9c91"))
                        .build()
        )

        bottomBar.onItemSelectedListener = { v, i ->
            val anim = ViewAnimationUtils.createCircularReveal(color,
                bottomBar.x.toInt() + v.x.toInt() + v.width / 2,
                bottomBar.y.toInt() + v.y.toInt() + v.height / 2, 0F,
                findViewById<View>(android.R.id.content).height.toFloat())
            color.setBackgroundColor(ColorUtils.setAlphaComponent(i.activeColor, 60))
            anim.duration = 420
            anim.start()
        }

        bottomBar.onItemReselectedListener = { _, i ->
            Log.d("ExpandableBottomBar", "OnReselected: ${i.itemId}")
        }
    }
}
