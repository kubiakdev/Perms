package com.kubiakdev.lib

import org.junit.Assert.fail

fun Perms.onResultWithFailAsDefault(
    onAllAccepted: (List<String>) -> Unit = { fail() },
    onAtLeastOneDenied: (List<String>) -> Unit = { fail() },
    onAtLeastOneForeverDenied: (List<String>) -> Unit = { fail() }
) {
    onResult(
        { acceptedPermissions -> onAllAccepted.invoke(acceptedPermissions) },
        { deniedPermissions -> onAtLeastOneDenied.invoke(deniedPermissions) },
        { foreverDeniedPermissions -> onAtLeastOneForeverDenied.invoke(foreverDeniedPermissions) }
    )
}