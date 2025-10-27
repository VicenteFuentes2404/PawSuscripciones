package com.example.pawsuscripciones

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Surface
import com.example.pawsuscripciones.navigation.NavGraph
import com.example.pawsuscripciones.notifications.NotificationHelper
import com.example.pawsuscripciones.ui.theme.PawSuscripcionesTheme

class MainActivity : ComponentActivity() {

    private val requestCalendarLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
      
    }

    private val requestNotificationLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        // Cuando termine la notificación, lanzamos calendario
        requestCalendarLauncher.launch(Manifest.permission.READ_CALENDAR)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val notificationHelper = NotificationHelper(this)
        notificationHelper.createNotificationChannel()

        // Lanzamos primero notificaciones
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            // En versiones anteriores solo pedimos calendario
            requestCalendarLauncher.launch(Manifest.permission.READ_CALENDAR)
        }

        setContent {
            PawSuscripcionesTheme {
                Surface {
                    NavGraph(notificationHelper = notificationHelper)
                }
            }
        }
    }
}

