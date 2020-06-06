package github.com.st235.expandablebottombar.screens

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import github.com.st235.expandablebottombar.R
import kotlinx.android.synthetic.main.activity_coordinator_layout.*

class CoordinatorLayoutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coordinator_layout)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Meow", Snackbar.LENGTH_LONG).show()
        }
    }

}
