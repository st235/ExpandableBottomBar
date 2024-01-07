package github.com.st235.expandablebottombar.screens

import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewAnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.ColorUtils
import github.com.st235.expandablebottombar.R
import github.com.st235.expandablebottombar.databinding.ActivityNotificationBadgeBinding

class NotificationBadgeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotificationBadgeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNotificationBadgeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            setSupportActionBar(toolbar)

            color.setBackgroundColor(ColorUtils.setAlphaComponent(Color.GRAY, 60))

            expandableBottomBar.onItemSelectedListener = { v, i, _ ->
                val anim = ViewAnimationUtils.createCircularReveal(color,
                    expandableBottomBar.x.toInt() + v.x.toInt() + v.width / 2,
                    expandableBottomBar.y.toInt() + v.y.toInt() + v.height / 2, 0F,
                    findViewById<View>(android.R.id.content).height.toFloat())
                color.setBackgroundColor(ColorUtils.setAlphaComponent(i.activeColor, 60))
                anim.duration = 420
                anim.start()
            }

            expandableBottomBar.onItemReselectedListener = { v, i, _ ->
                val notification = i.notification()

                if (v.tag == null) {
                    notification.show()
                    v.tag = 0
                } else {
                    val counter = (v.tag as Int) + 1
                    notification.show(counter.toString())
                    v.tag = counter
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.clear -> {
                for (menuItem in binding.expandableBottomBar.menu) {
                    menuItem.notification().clear()
                }
            }
        }

        return true
    }
}
