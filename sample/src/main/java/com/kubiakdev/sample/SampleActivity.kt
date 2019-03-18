package com.kubiakdev.sample

import android.Manifest
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.kubiakdev.lib.Perms
import com.kubiakdev.perms.R
import kotlinx.android.synthetic.main.activity_main.*

class SampleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener {

            Perms(this)
                .request(
                    Manifest.permission.CAMERA,
                    Manifest.permission.CALL_PHONE
                )
                .onResult(
                    onAllAccepted = { acceptedPermissions ->
                        Log.i(TAG, "All permissions accepted.")
                        Log.i(TAG, "The sum of accepted permissions is ${acceptedPermissions.size}")
                    },
                    onAtLeastOneDenied = { deniedPermissions ->
                        Log.i(TAG, "At least one permission is denied.")
                        Log.i(TAG, "The sum of denied permissions is ${deniedPermissions.size}")
                    },
                    onAtLeastOneForeverDenied = { foreverDeniedPermissions ->
                        Log.i(TAG, "At least one permission is forever denied and you have to open the settings.")
                        Log.i(TAG, "The sum of forever denied permissions is ${foreverDeniedPermissions.size}")
                    }
                )
        }
    }

    companion object {

        private const val TAG = "SampleActivity"

    }

}
