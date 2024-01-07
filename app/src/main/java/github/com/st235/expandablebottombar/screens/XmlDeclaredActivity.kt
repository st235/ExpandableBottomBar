package github.com.st235.expandablebottombar.screens

import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.view.ViewAnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.ColorUtils
import github.com.st235.expandablebottombar.R
import github.com.st235.expandablebottombar.databinding.ActivityXmlDeclaredBinding

class XmlDeclaredActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityXmlDeclaredBinding.inflate(layoutInflater)
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
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.bottom_bar, menu)
        return true
    }
}
