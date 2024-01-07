package github.com.st235.expandablebottombar.screens

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import github.com.st235.expandablebottombar.databinding.ActivityCoordinatorLayoutBinding

class CoordinatorLayoutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityCoordinatorLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            fab.setOnClickListener { view ->
                Snackbar.make(view, "Meow", Snackbar.LENGTH_LONG).show()
            }
        }
    }
}
