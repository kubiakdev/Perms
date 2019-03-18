package com.kubiakdev.lib

import android.Manifest
import android.app.Instrumentation
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.test.filters.SdkSuppress
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObjectNotFoundException
import androidx.test.uiautomator.UiSelector
import com.kubiakdev.sample.SampleActivity
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
@SdkSuppress(minSdkVersion = 18)
class PermsInstrumentedTest {

    private val grantPermissionSelector = UiSelector()
            .clickable(true)
            .checkable(false)
            .index(GRANT_PERMISSION_BUTTON_INDEX)

    private val denyPermissionSelector = UiSelector()
            .clickable(true)
            .checkable(false)
            .index(DENY_PERMISSION_BUTTON_INDEX)

    private val denyForeverPermissionSelector = UiSelector()
            .className(CHECKBOX_CLASS_FULL_NAME)
            .index(DENY_FOREVER_PERMISSION_CHECKBOX_INDEX)

    private val device: UiDevice by lazy {
        UiDevice.getInstance(instrumentation)
    }

    private val instrumentation: Instrumentation by lazy {
        InstrumentationRegistry.getInstrumentation()
    }

    private lateinit var activity: AppCompatActivity
    private lateinit var monitor: Instrumentation.ActivityMonitor

    @Before
    fun setUp() {
        device.pressHome()
        attachMonitor()
    }

    @Test
    fun requireEmptyPermissionArray_shouldDoNothing() {
        Perms(activity).request().onResultWithFailAsDefault()
    }

    @Test
    fun requireEmptyPermissionList_shouldDoNothing() {
        Perms(activity).request(arrayListOf()).onResultWithFailAsDefault()
    }

    @Test
    fun requireSinglePermission_shouldBeGranted() {
        Perms(activity).request(*EXAMPLE_SINGLE_PERMISSION)
                .onResultWithFailAsDefault(
                        onAllAccepted = { assertArrayEquals(it.toTypedArray(), EXAMPLE_SINGLE_PERMISSION) }
                )

        findUiObjectsAndPerformClick(grantPermissionSelector)
    }

    @Test
    fun requireMultiplePermissions_allShouldBeGranted() {
        Perms(activity).request(*EXAMPLE_MULTIPLE_PERMISSIONS)
                .onResultWithFailAsDefault(
                        onAllAccepted = { assertArrayEquals(it.toTypedArray(), EXAMPLE_MULTIPLE_PERMISSIONS) }
                )

        findUiObjectsAndPerformClick(grantPermissionSelector, grantPermissionSelector)
    }

    @Test
    fun requireSinglePermission_shouldBeDenied() {
        Perms(activity).request(*EXAMPLE_SINGLE_PERMISSION)
                .onResultWithFailAsDefault(
                        onAtLeastOneDenied = { assertArrayEquals(it.toTypedArray(), EXAMPLE_SINGLE_PERMISSION) }
                )

        findUiObjectsAndPerformClick(denyPermissionSelector)
    }

    @Test
    fun requireMultiplePermissions_oneShouldBeDenied() {
        Perms(activity).request(*EXAMPLE_MULTIPLE_PERMISSIONS)
                .onResultWithFailAsDefault(
                        onAtLeastOneDenied = { assertArrayEquals(it.toTypedArray(), EXAMPLE_SINGLE_PERMISSION) }
                )

        findUiObjectsAndPerformClick(denyPermissionSelector, grantPermissionSelector)
    }

    @Test
    fun requireMultiplePermissions_allShouldBeDenied() {
        Perms(activity).request(*EXAMPLE_MULTIPLE_PERMISSIONS)
                .onResultWithFailAsDefault(
                        onAtLeastOneDenied = { assertArrayEquals(it.toTypedArray(), EXAMPLE_MULTIPLE_PERMISSIONS) }
                )

        findUiObjectsAndPerformClick(denyPermissionSelector, denyPermissionSelector)
    }

    @Test
    fun requireSinglePermission_shouldBeForeverDenied() {
        Perms(activity).request(*EXAMPLE_SINGLE_PERMISSION).onResultWithFailAsDefault(
                onAtLeastOneDenied = { assertArrayEquals(it.toTypedArray(), EXAMPLE_SINGLE_PERMISSION) }
        )

        findUiObjectsAndPerformClick(denyPermissionSelector)

        Perms(activity).request(*EXAMPLE_SINGLE_PERMISSION)
                .onResultWithFailAsDefault(
                        onAtLeastOneForeverDenied = { assertArrayEquals(it.toTypedArray(), EXAMPLE_SINGLE_PERMISSION) }
                )

        findUiObjectsAndPerformClick(denyForeverPermissionSelector, denyPermissionSelector)
    }

    @Test
    fun requireMultiplePermissions_oneShouldBeForeverDenied() {
        Perms(activity).request(*EXAMPLE_MULTIPLE_PERMISSIONS)
                .onResultWithFailAsDefault(
                        onAtLeastOneDenied = { assertArrayEquals(it.toTypedArray(), EXAMPLE_MULTIPLE_PERMISSIONS) }
                )

        findUiObjectsAndPerformClick(denyPermissionSelector, denyPermissionSelector)

        Perms(activity).request(*EXAMPLE_MULTIPLE_PERMISSIONS)
                .onResultWithFailAsDefault(
                        onAtLeastOneForeverDenied = { assertArrayEquals(it.toTypedArray(), EXAMPLE_SINGLE_PERMISSION) }
                )

        findUiObjectsAndPerformClick(denyForeverPermissionSelector, denyPermissionSelector)
    }

    @Test
    fun requireMultiplePermissions_allShouldBeForeverDenied() {
        Perms(activity).request(*EXAMPLE_MULTIPLE_PERMISSIONS)
                .onResultWithFailAsDefault(
                        onAtLeastOneDenied = { assertArrayEquals(it.toTypedArray(), EXAMPLE_MULTIPLE_PERMISSIONS) }
                )

        findUiObjectsAndPerformClick(denyPermissionSelector, denyPermissionSelector)

        Perms(activity).request(*EXAMPLE_MULTIPLE_PERMISSIONS)
                .onResultWithFailAsDefault(
                        onAtLeastOneForeverDenied = {
                            assertArrayEquals(it.toTypedArray(), EXAMPLE_MULTIPLE_PERMISSIONS)
                        }
                )

        findUiObjectsAndPerformClick(
                denyForeverPermissionSelector,
                denyPermissionSelector,
                denyForeverPermissionSelector,
                denyPermissionSelector
        )
    }

    private fun attachMonitor() {
        val className = SampleActivity::class.java.name
        monitor = instrumentation.addMonitor(className, null, false)
        launchSampleActivity(className)
        activity = instrumentation.waitForMonitor(monitor) as AppCompatActivity
    }

    private fun launchSampleActivity(className: String) {
        instrumentation.startActivitySync(
                Intent(Intent.ACTION_MAIN).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    setClassName(instrumentation.targetContext, className)
                }
        )
    }

    private fun findUiObjectsAndPerformClick(vararg selectors: UiSelector) {
        for (selector in selectors) {
            device.findObject(selector).apply {
                if (exists()) {
                    try {
                        click()
                    } catch (e: UiObjectNotFoundException) {
                        e.printStackTrace()
                        fail()
                    }
                }
            }
        }
    }

    companion object {

        private const val GRANT_PERMISSION_BUTTON_INDEX = 1
        private const val DENY_PERMISSION_BUTTON_INDEX = 0
        private const val DENY_FOREVER_PERMISSION_CHECKBOX_INDEX = 0

        private const val CHECKBOX_CLASS_FULL_NAME = "android.widget.CheckBox"

        private const val CAMERA_PERMISSION = Manifest.permission.CAMERA
        private const val CALL_PHONE_PERMISSION = Manifest.permission.CALL_PHONE

        private val EXAMPLE_SINGLE_PERMISSION = arrayOf(CAMERA_PERMISSION)
        private val EXAMPLE_MULTIPLE_PERMISSIONS = arrayOf(CAMERA_PERMISSION, CALL_PHONE_PERMISSION)

    }

}
