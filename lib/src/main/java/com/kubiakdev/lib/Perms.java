package com.kubiakdev.lib;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.Size;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import java.util.Arrays;
import java.util.List;

public class Perms {

    private static final String TAG = Perms.class.getSimpleName();

    private final FragmentActivity activity;

    private final PermissionsFragment.ReceivedListener listener = new PermissionsFragment.ReceivedListener() {

        @Override
        public void onReceivedResult(List<String> acceptedPermissions,
                                     List<String> deniedPermissions,
                                     List<String> foreverDeniedPermissions) {
            if (!foreverDeniedPermissions.isEmpty() && foreverDeniedResponse != null) {
                foreverDeniedResponse.onAtLeastOneForeverDenied(foreverDeniedPermissions);
            } else if (!deniedPermissions.isEmpty() && deniedResponse != null) {
                deniedResponse.onAtLeastOneDenied(deniedPermissions);
            } else {
                acceptedResponse.onAllAccepted(acceptedPermissions);
            }
        }

    };

    private String[] permissions;

    private AcceptedResponse acceptedResponse;
    private DeniedResponse deniedResponse;
    private ForeverDeniedResponse foreverDeniedResponse;

    public Perms(@NonNull final FragmentActivity activity) {
        this.activity = activity;
    }

    /**
     * Select a list of required permissions.
     * <p>
     * Empty list will do nothing.
     *
     * @param permissions set required permissions.
     * @return Perms object.
     */
    public Perms request(@NonNull final List<String> permissions) {
        this.permissions = (permissions.isEmpty()) ? new String[0] : (String[]) permissions.toArray();
        return this;
    }

    /**
     * Select an array of required permissions.
     * <p>
     * Empty array will do nothing.
     *
     * @param permissions set required permissions.
     * @return Perms object.
     */
    public Perms request(@Size(min = 1) @NonNull final String... permissions) {
        this.permissions = (permissions.length == 0) ? new String[0] : permissions;
        return this;
    }

    /**
     * Return a list of granted permissions if all will be granted.
     * <p>
     * Denied and forever denied permissions will be ignored.
     * <p>
     * All permissions will be granted on devices with SDK &lt; 23.
     *
     * @param acceptedResponse callback with grantedPermissions
     */
    public void onResult(AcceptedResponse acceptedResponse) {
        onResult(acceptedResponse, null, null);
    }

    /**
     * Return a list of granted permissions if all will be granted.
     * <p>
     * Return a list of denied permissions if at least one will be denied and none forever denied.
     * <p>
     * Forever denied permissions will be ignored.
     * <p>
     * All permissions will be granted on devices with SDK &lt; 23.
     *
     * @param acceptedResponse callback with grantedPermissions
     * @param deniedResponse   callback with deniedPermissions
     */
    public void onResult(AcceptedResponse acceptedResponse, DeniedResponse deniedResponse
    ) {
        onResult(acceptedResponse, deniedResponse, null);
    }

    /**
     * Return a list of granted permissions if all will be granted.
     * <p>
     * Return a list of denied permissions if at least one will be denied and none forever denied.
     * <p>
     * Return a list of forever denied permissions if at least one will be forever denied.
     * <p>
     * All permissions will be granted on devices with SDK &lt; 23.
     *
     * @param acceptedResponse      callback with grantedPermissions
     * @param deniedResponse        callback with deniedPermissions
     * @param foreverDeniedResponse callback with foreverDeniedPermissions
     */
    public void onResult(AcceptedResponse acceptedResponse,
                         DeniedResponse deniedResponse,
                         ForeverDeniedResponse foreverDeniedResponse) {
        this.acceptedResponse = acceptedResponse;
        this.deniedResponse = deniedResponse;
        this.foreverDeniedResponse = foreverDeniedResponse;
        invokeRequest();
    }

    private void invokeRequest() {
        if (activity.isFinishing() || permissions.length == 0) {
            return;
        }

        if (arePermissionsCurrentlyAccepted(activity, permissions)) {
            acceptedResponse.onAllAccepted(Arrays.asList(permissions));
        } else {
            final PermissionsFragment fragment = PermissionsFragment.newInstance(permissions);
            fragment.setListener(listener);
            launchPermissionsFragment(fragment);
        }
    }

    private boolean arePermissionsCurrentlyAccepted(@NonNull Context context, @NonNull final String[] permissions) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return true;

        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
                return true;
            }
        }

        return false;
    }

    private void launchPermissionsFragment(final Fragment fragment) {
        activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                activity.getSupportFragmentManager()
                        .beginTransaction()
                        .add(fragment, TAG)
                        .commitNow();
            }

        });
    }

}
