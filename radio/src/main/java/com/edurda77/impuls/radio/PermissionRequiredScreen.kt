package com.edurda77.impuls.radio

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.edurda77.impuls.tele_tv.resources.theme.Tele_TvTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


data class PermissionContent(
    val permission: String,
    val titleResId: Int,
)

@Composable
fun PermissionRequiredScreen(
    permissions: List<PermissionContent>,
    onPermissionGranted: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { granted ->
        if (granted.values.all { it }) {
            scope.launch {
                delay(300)
                onPermissionGranted()
            }
        }
    }
    LaunchedEffect(true) {
        launcher.launch(permissions.map { it.permission }.toTypedArray())
    }
}

@Preview
@Composable
private fun PermissionRequiredScreenView() {
    Tele_TvTheme() {
        PermissionRequiredScreen(
            permissions = listOf(
                PermissionContent(
                    permission = Manifest.permission.CAMERA,
                    titleResId = R.string.visualisation
                )
            ),
            onPermissionGranted = {}
        )
    }
}


@Composable
fun WithPermission(
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val permissions =
        remember {
            arrayOf(
                PermissionContent(
                    permission = Manifest.permission.RECORD_AUDIO,
                    titleResId = R.string.visualisation
                ),
            )
        }

    var permissionGranted by remember {
        mutableStateOf(permissions.map {
            context.checkSelfPermission(it.permission) == PackageManager.PERMISSION_GRANTED
        }.all { it })
    }

    if (!permissionGranted) {
        PermissionRequiredScreen(
            permissions = permissions.toList(),
            onPermissionGranted = {
                permissionGranted = true
            })
    } else {
        content()
    }
}