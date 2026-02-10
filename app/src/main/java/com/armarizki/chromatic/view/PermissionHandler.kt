package com.armarizki.chromatic.view

import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.compose.runtime.Stable
import androidx.core.content.ContextCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet

 
@Stable
class PermissionHandler(
    private val activity: ComponentActivity,
    private val permission: String,
) {

     
    private val launcher = activity.registerForActivityResult(RequestPermission()) {
        _granted.update { it }
        _firstRequest.update { false }
    }

     
    private val _firstRequest = MutableStateFlow(true)

     
    val firstRequest = _firstRequest.asStateFlow()

     
    private val _granted = MutableStateFlow(checkPerm())

     
    val granted = _granted.asStateFlow()

     
    fun request() {
        if (!check() && firstRequest.value) {
            launcher.launch(permission)
        }
    }

     
    fun check(): Boolean {
        return _granted.updateAndGet { checkPerm() }
    }

     
    private fun checkPerm(): Boolean {
        return ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED
    }
}