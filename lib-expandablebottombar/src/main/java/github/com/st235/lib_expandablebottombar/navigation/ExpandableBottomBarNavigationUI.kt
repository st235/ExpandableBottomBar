package github.com.st235.lib_expandablebottombar.navigation

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.navigation.*
import androidx.navigation.NavController.OnDestinationChangedListener
import github.com.st235.lib_expandablebottombar.ExpandableBottomBar
import github.com.st235.lib_expandablebottombar.MenuItem
import github.com.st235.lib_expandablebottombar.R
import java.lang.ref.WeakReference

object ExpandableBottomBarNavigationUI {

    @JvmStatic
    fun setupWithNavController(
            expandableBottomBar: ExpandableBottomBar,
            navigationController: NavController
    ) {
        expandableBottomBar.onItemSelectedListener = { _, menuItem ->
            onNavDestinationSelected(menuItem, navigationController)
        }

        val weakReference = WeakReference(expandableBottomBar)
        navigationController.addOnDestinationChangedListener(
                object : OnDestinationChangedListener {
                    override fun onDestinationChanged(
                            controller: NavController,
                            destination: NavDestination, arguments: Bundle?
                    ) {
                        val view = weakReference.get()
                        if (view == null) {
                            navigationController.removeOnDestinationChangedListener(this)
                            return
                        }
                        for (menuItem in view.getMenuItems()) {
                            if (destination.matchDestination(menuItem.id)) {
                                expandableBottomBar.select(menuItem.id)
                            }
                        }
                    }
                })
    }

    //TODO(st235): https://android.googlesource.com/platform/frameworks/support/+/refs/heads/androidx-main/navigation/navigation-ui/src/main/java/androidx/navigation/ui/NavigationUI.kt#85
    private fun onNavDestinationSelected(
            item: MenuItem,
            navController: NavController
    ): Boolean {
        val builder = NavOptions.Builder()
                .setLaunchSingleTop(true)

        if (navController.currentDestination!!.parent!!.findNode(item.id)
                        is ActivityNavigator.Destination) {
            builder
                    .setEnterAnim(R.anim.nav_default_enter_anim)
                    .setExitAnim(R.anim.nav_default_exit_anim)
                    .setPopEnterAnim(R.anim.nav_default_pop_enter_anim)
                    .setPopExitAnim(R.anim.nav_default_pop_exit_anim)
        } else {
            builder
                    .setEnterAnim(R.animator.nav_default_enter_anim)
                    .setExitAnim(R.animator.nav_default_exit_anim)
                    .setPopEnterAnim(R.animator.nav_default_pop_enter_anim)
                    .setPopExitAnim(R.animator.nav_default_pop_exit_anim)
        }

        val topDestination = navController.findStartDestination()

        topDestination?.let {
            builder.setPopUpTo(it.id, false)
        }

        val options = builder.build()
        return try {
            navController.navigate(item.id, null, options)
            true
        } catch (e: IllegalArgumentException) {
            false
        }
    }

    private fun NavDestination.matchDestination(@IdRes destId: Int): Boolean {
        var currentDestination: NavDestination? = this
        while (currentDestination!!.id != destId && currentDestination.parent != null) {
            currentDestination = currentDestination.parent
        }
        return currentDestination.id == destId
    }

    private fun NavController.findStartDestination(): NavDestination? {
        var startDestination: NavDestination? = graph

        while (startDestination is NavGraph) {
            val parent = startDestination
            startDestination = parent.findNode(parent.startDestination)
        }

        return startDestination
    }

}
