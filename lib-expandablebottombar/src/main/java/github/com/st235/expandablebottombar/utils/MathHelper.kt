package github.com.st235.lib_expandablebottombar.utils

internal fun <T: Comparable<T>> clamp(value: T, min: T, max: T): T {
    return max(min, min(value, max))
}

internal fun <T: Comparable<T>> max(value1: T, value2: T): T {
    if (value1 > value2) {
        return value1
    }
    return value2
}

internal fun <T: Comparable<T>> min(value1: T, value2: T): T {
    if (value1 > value2) {
        return value2
    }
    return value1
}
