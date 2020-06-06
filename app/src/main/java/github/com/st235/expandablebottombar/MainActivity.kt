package github.com.st235.expandablebottombar

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import github.com.st235.expandablebottombar.screens.navigation.NavigationComponentActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val rootView = findViewById<ViewGroup>(R.id.root)
        addButton(rootView, "Navigate to XML Declared Menu", XmlDeclaredActivity::class.java)
        addButton(rootView, "Navigate to Programmatically Declared Menu",
            ProgrammaticallyCreatedDemoActivity::class.java)
        addButton(rootView, "Java Interop with Expandable Menu", JavaActivity::class.java)
        addButton(rootView, "Coordinator Layout Behavior", CoordinatorLayoutActivity::class.java)
        addButton(rootView, "Coordinator Layout Scrollable Behavior", ScrollableCoordinatorLayoutActivity::class.java)
        addButton(rootView, "Navigation component", NavigationComponentActivity::class.java)
    }

    private fun addButton(root: ViewGroup,
                          text: String, to: Class<*>) {
        val button = Button(this)
        button.text = text
        val layoutParams = ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.MATCH_PARENT,
            ViewGroup.MarginLayoutParams.WRAP_CONTENT)
        button.setOnClickListener {
            startActivity(Intent(this, to))
        }

        root.addView(button, layoutParams)
    }
}
