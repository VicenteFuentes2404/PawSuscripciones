package com.example.pawsuscripciones.navigation

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pawsuscripciones.notifications.NotificationHelper
import com.example.pawsuscripciones.ui.screens.FormularioSuscripcionScreen
import com.example.pawsuscripciones.ui.screens.InicioScreen
import com.example.pawsuscripciones.ui.screens.SuscripcionesScreen
import com.example.pawsuscripciones.viewmodel.SuscripcionViewModel
import com.example.pawsuscripciones.viewmodel.SuscripcionViewModelFactory

sealed class Routes(val route: String) {
    object Inicio : Routes("inicio")
    object Suscripciones : Routes("suscripciones")
    object Formulario : Routes("formulario")
}

@Composable
fun NavGraph(notificationHelper: NotificationHelper) {
    val navController = rememberNavController()
    // Obtiene el contexto de la aplicación para pasárselo al factory
    val application = LocalContext.current.applicationContext as Application

    // Crea el ViewModel usando el Factory para inyectar el NotificationHelper
    val vm: SuscripcionViewModel = viewModel(
        factory = SuscripcionViewModelFactory(application, notificationHelper)
    )

    NavHost(navController = navController, startDestination = Routes.Inicio.route) {
        composable(Routes.Inicio.route) {
            InicioScreen(
                onEntrar = { navController.navigate(Routes.Suscripciones.route) }
            )
        }

        composable(Routes.Suscripciones.route) {
            SuscripcionesScreen(
                viewModel = vm,
                onAdd = { navController.navigate(Routes.Formulario.route) },
                onShowNotificationDemo = {
                    notificationHelper.showNotificationDemo(
                        "Recordatorio",
                        "Tienes una suscripción próxima"
                    )
                }
            )
        }

        composable(Routes.Formulario.route) {
            FormularioSuscripcionScreen(
                onSaved = { navController.popBackStack() },
                viewModel = vm,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
