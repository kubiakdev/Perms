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

    public PermissionsFragment() {
        setRetainInstance(true);
    }

    static PermissionsFragment newInstance(String[] permissions) {
        final Bundle arguments = new Bundle();
        final PermissionsFragment fragment = new PermissionsFragment();
        arguments.putStringArray(PERMISSIONS, permissions);
        fragment.setArguments(arguments);
        return fragment;
    }

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

    private void removeFragment() {
        if (getFragmentManager() != null) {
            getFragmentManager().beginTransaction()
                    .remove(this)
                    .commitAllowingStateLoss();
        }
    }

    void setListener(ReceivedListener listener) {
        this.listener = listener;
    }

    interface ReceivedListener {
        void onReceivedResult(List<String> acceptedPermissions,
                              List<String> deniedPermissions,
                              List<String> foreverDeniedPermissions);
    }

}