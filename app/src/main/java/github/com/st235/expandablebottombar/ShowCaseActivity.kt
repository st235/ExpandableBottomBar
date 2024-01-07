package github.com.st235.expandablebottombar

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import github.com.st235.expandablebottombar.databinding.ActivityShowcaseBinding
import github.com.st235.expandablebottombar.screens.*
import github.com.st235.expandablebottombar.screens.navigation.NavigationComponentActivity

class ShowCaseActivity : AppCompatActivity() {

    val showCaseInfos by lazy {
        listOf(
            createShowCase<ProgrammaticallyCreatedDemoActivity>(
                title = getString(R.string.programmatically_title),
                description = getString(R.string.programmatically_description)
            ),
            createShowCase<XmlDeclaredActivity>(
                title = getString(R.string.xml_declared_title),
                description = getString(R.string.xml_declared_description)
            ),
            createShowCase<JavaActivity>(
                title = getString(R.string.interop_title),
                description = getString(R.string.interop_description)
            ),
            createShowCase<CoordinatorLayoutActivity>(
                title = getString(R.string.coordinator_base_title),
                description = getString(R.string.coordinator_base_description)
            ),
            createShowCase<ScrollableCoordinatorLayoutActivity>(
                title = getString(R.string.coordinator_scroll_title),
                description = getString(R.string.coordinator_scroll_description)
            ),
            createShowCase<NavigationComponentActivity>(
                title = getString(R.string.navigation_components_title),
                description = getString(R.string.navigation_components_description)
            ),
            createShowCase<StylesActivity>(
                title = getString(R.string.styles_title),
                description = getString(R.string.styles_description)
            ),
            createShowCase<NotificationBadgeActivity>(
                title = getString(R.string.notification_badges_title),
                description = getString(R.string.notification_badges_description)
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityShowcaseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            setSupportActionBar(toolbar)

            val adapter = ShowCaseAdapter(showCaseInfos) { info ->
                val intent = Intent(this@ShowCaseActivity, info.toClass)
                intent.putExtra(ARGS_SCREEN_TITLE, title)
                startActivity(intent)
            }

            val layoutManager = LinearLayoutManager(this@ShowCaseActivity)

            recyclerView.adapter = adapter
            recyclerView.layoutManager = layoutManager
        }
    }

    companion object {
        const val ARGS_SCREEN_TITLE = "args.screen_title"
    }
}
