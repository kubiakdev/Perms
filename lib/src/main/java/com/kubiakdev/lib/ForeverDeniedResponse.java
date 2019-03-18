package com.kubiakdev.lib;

import java.util.List;

/**
 * Callback interface returns the forever denied permissions.
 */
public interface ForeverDeniedResponse {
    void onAtLeastOneForeverDenied(List<String> foreverDeniedPermissions);
}