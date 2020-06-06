package github.com.st235.expandablebottombar

data class ShowCaseInfo(
    val title: String,
    val description: String,
    val toClass: Class<*>
)

inline fun <reified T> createShowCase(title: String, description: String): ShowCaseInfo {
    return ShowCaseInfo(title, description, T::class.java)
}
