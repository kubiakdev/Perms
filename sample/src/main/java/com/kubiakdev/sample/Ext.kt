package com.kubiakdev.sample

import com.kubiakdev.lib.Perms

fun Perms.onResult(
    onAllAccepted: (List<String>) -> Unit = {},
    onAtLeastOneDenied: (List<String>) -> Unit = {},
    onAtLeastOneForeverDenied: (List<String>) -> Unit = {}
) {
    onResult(
        { acceptedPermissions -> onAllAccepted.invoke(acceptedPermissions) },
        { deniedPermissions -> onAtLeastOneDenied.invoke(deniedPermissions) },
        { foreverDeniedPermissions -> onAtLeastOneForeverDenied.invoke(foreverDeniedPermissions) }
    )
}