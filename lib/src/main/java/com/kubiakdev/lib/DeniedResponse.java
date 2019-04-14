package com.kubiakdev.lib;

import java.util.List;

/**
 * Callback interface returns the denied permissions.
 */
public interface DeniedResponse {
    void onAtLeastOneDenied(List<String> deniedPermissions);
}