package com.kubiakdev.lib;

import java.util.List;

/**
 * Callback interface returns the accepted permissions.
 */
public interface AcceptedResponse {
    void onAllAccepted(List<String> acceptedPermissions);
}