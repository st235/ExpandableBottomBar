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
import github.com.st235.lib_expandablebottombar.MenuItemDescriptor


class ProgrammaticallyCreatedDemoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_programmatically_declared)

        val colorView: View = findViewById(R.id.color)
        val bottomBar: ExpandableBottomBar = findViewById(R.id.expandable_bottom_bar)

        colorView.setBackgroundColor(ColorUtils.setAlphaComponent(Color.GRAY, 60))

        val menu = bottomBar.menu

        menu.add(
            MenuItemDescriptor.Builder(
                this,
                R.id.icon_home,
                R.drawable.ic_home,
                R.string.text, Color.GRAY
            )
                .build()
        )

        menu.add(
            MenuItemDescriptor.Builder(
                this,
                R.id.icon_likes,
                R.drawable.ic_likes
            )
                .textRes(R.string.text2)
                .colorRes(R.color.colorLike)
                .build()
        )

        menu.add(
            MenuItemDescriptor.Builder(
                this,
                R.id.icon_bookmarks,
                R.drawable.ic_bookmarks,
                R.string.text3,
                Color.parseColor("#58a5f0")
            )
                .build()
        )

        menu.add(
            MenuItemDescriptor.Builder(
                this,
                R.id.icon_settings,
                R.drawable.ic_settings,
                R.string.text4,
                Color.parseColor("#be9c91")
            )
                .build()
        )

        bottomBar.onItemSelectedListener = { v, i, _ ->
            val anim = ViewAnimationUtils.createCircularReveal(
                colorView,
                bottomBar.x.toInt() + v.x.toInt() + v.width / 2,
                bottomBar.y.toInt() + v.y.toInt() + v.height / 2, 0F,
                findViewById<View>(android.R.id.content).height.toFloat()
            )
            colorView.setBackgroundColor(ColorUtils.setAlphaComponent(i.activeColor, 60))
            anim.duration = 420
            anim.start()
        }

        bottomBar.onItemReselectedListener = { _, i, _ ->
            Log.d("ExpandableBottomBar", "OnReselected: ${i.id}")
        }
    }
}
