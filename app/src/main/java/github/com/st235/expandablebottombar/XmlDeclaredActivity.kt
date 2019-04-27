package github.com.st235.expandablebottombar

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewAnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.ColorUtils
import github.com.st235.lib_expandablebottombar.ExpandableBottomBar

class XmlDeclaredActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_xml_declared)

        val color: View = findViewById(R.id.color)
        val bottomBar: ExpandableBottomBar = findViewById(R.id.expandable_bottom_bar)

        color.setBackgroundColor(ColorUtils.setAlphaComponent(Color.GRAY, 60))

        bottomBar.onItemSelectedListener = { v, i ->
            val anim = ViewAnimationUtils.createCircularReveal(color,
                    bottomBar.x.toInt() + v.x.toInt() + v.width / 2,
                    bottomBar.y.toInt() + v.y.toInt() + v.height / 2, 0F,
                    findViewById<View>(android.R.id.content).height.toFloat())
            color.setBackgroundColor(ColorUtils.setAlphaComponent(i.activeColor, 60))
            anim.duration = 420
            anim.start()
        }
    }
}
