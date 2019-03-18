package com.kubiakdev.lib;

import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class PermissionsFragment extends Fragment {

    private static final String PERMISSIONS = "PERMISSIONS";
    private static final int REQUEST_CODE = 1410;

    private ReceivedListener listener;

    /**
     * Empty constructor setting a {@code setRetainInstance} to {@code true} to avoid multiple requests on fragment
     * state change.
     */
    public PermissionsFragment() {
        setRetainInstance(true);
    }

    /**
     * Create a new PermissionsFragment object with permissions array in extra.
     *
     * @param permissions a set of required permissions.
     * @return new PermissionsFragment object with permissions array in extra.
     */
    static PermissionsFragment newInstance(String[] permissions) {
        final Bundle arguments = new Bundle();
        final PermissionsFragment fragment = new PermissionsFragment();
        arguments.putStringArray(PERMISSIONS, permissions);
        fragment.setArguments(arguments);
        return fragment;
    }

    /**
     * {@link PermissionsFragment} {@code onResume()} method.
     * <p>
     * Request permissions when permissions in {@code getArguments()} is not null.
     */
    @Override
    public void onResume() {
        super.onResume();
        if (getArguments() != null) {
            final String[] permissions = getArguments().getStringArray(PERMISSIONS);
            if (permissions != null) {
                requestPermissions(permissions, REQUEST_CODE);
            }
        }
    }

    /**
     * {@link PermissionsFragment} {@code onRequestPermissionsResult(requestCode, permissions, grantResults)} method.
     * <p>
     * Returns the permissions requests results.
     *
     * @param requestCode  defines the specific {@code requestPermissions(permissions, requestCode} method invocation.
     * @param permissions  an array of required permissions.
     * @param grantResults an array of permissions request result.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE && listener != null) {

            final List<String> acceptedPermissions = new ArrayList<>();
            final List<String> deniedPermissions = new ArrayList<>();
            final List<String> foreverDeniedPermissions = new ArrayList<>();

            for (int i = 0; i < permissions.length; i++) {
                final String permissionName = permissions[i];
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    acceptedPermissions.add(permissionName);
                } else {
                    if (shouldShowRequestPermissionRationale(permissionName)) {
                        deniedPermissions.add(permissionName);
                    } else {
                        foreverDeniedPermissions.add(permissionName);
                    }
                }
            }

            listener.onReceivedResult(acceptedPermissions, deniedPermissions, foreverDeniedPermissions);

            removeFragment();
        }
    }

    /**
     * Remove the {@link PermissionsFragment} object from {@code fragmentManager}
     */
    private void removeFragment() {
        if (getFragmentManager() != null) {
            getFragmentManager().beginTransaction()
                    .remove(this)
                    .commitAllowingStateLoss();
        }
    }

    /**
     * Set the {@link ReceivedListener} listener.
     *
     * @param listener to set
     */
    void setListener(ReceivedListener listener) {
        this.listener = listener;
    }

    /**
     * Callback interface that invokes during receiving permissions result.
     */
    interface ReceivedListener {
        void onReceivedResult(List<String> acceptedPermissions,
                              List<String> deniedPermissions,
                              List<String> foreverDeniedPermissions);
    }

}