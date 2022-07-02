package github.com.st235.expandablebottombar.screens

import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.view.ViewAnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.graphics.ColorUtils
import github.com.st235.expandablebottombar.R
import github.com.st235.lib_expandablebottombar.ExpandableBottomBar

class XmlDeclaredActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_xml_declared)

        toolbar = findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)

        val color: View = findViewById(R.id.color)
        val bottomBar: ExpandableBottomBar = findViewById(R.id.expandable_bottom_bar)

        color.setBackgroundColor(ColorUtils.setAlphaComponent(Color.GRAY, 60))

        bottomBar.onItemSelectedListener = { v, i, _ ->
            val anim = ViewAnimationUtils.createCircularReveal(color,
                    bottomBar.x.toInt() + v.x.toInt() + v.width / 2,
                    bottomBar.y.toInt() + v.y.toInt() + v.height / 2, 0F,
                    findViewById<View>(android.R.id.content).height.toFloat())
            color.setBackgroundColor(ColorUtils.setAlphaComponent(i.activeColor, 60))
            anim.duration = 420
            anim.start()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.bottom_bar, menu)
        return true
    }
}
