package github.com.st235.expandablebottonbar

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.ViewGroup
import android.widget.Button

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val rootView = findViewById<ViewGroup>(R.id.root)
        addButton(rootView, "Navigate to XML Declared Menu", XmlDeclaredActivity::class.java)
        addButton(rootView, "Navigate to Programmatically Declared Menu",
            ProgrammaticallyCreatedDemoActivity::class.java)
        addButton(rootView, "Java Interop with Expandable Menu", JavaActivity::class.java)
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
