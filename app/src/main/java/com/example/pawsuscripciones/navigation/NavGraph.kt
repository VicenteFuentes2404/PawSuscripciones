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
import androidx.navigation.NavType
import androidx.navigation.navArgument

sealed class Routes(val route: String) {
    object Inicio : Routes("inicio")
    object Suscripciones : Routes("suscripciones")
    object Formulario : Routes("formulario?id={id}") {
        const val routeWithArgs = "formulario?id={id}"
        const val argId = "id"
        // Ruta para crear uno nuevo
        fun createRouteNuevo() = "formulario?id=-1"
        // Ruta para editar uno existente
        fun createRouteEditar(id: Long) = "formulario?id=$id"
    }
}

@Composable
fun NavGraph(notificationHelper: NotificationHelper) {
    val navController = rememberNavController()
    // Obtiene el contexto de la aplicación para pasárselo al factory
    val application = LocalContext.current.applicationContext as Application

    // Crea el ViewModel usando el Factory para inyectar el NotificationHelper
    // Esta instancia 'vm' se compartirá entre todas las pantallas.
    val vm: SuscripcionViewModel = viewModel(
        factory = SuscripcionViewModelFactory(application, notificationHelper)
    )

    NavHost(navController = navController, startDestination = Routes.Inicio.route) {
        composable(Routes.Inicio.route) {
            InicioScreen(
                // Pasamos el ViewModel a la pantalla de inicio para que
                // pueda llamar a refreshData() al presionar "Entrar".
                viewModel = vm,
                onEntrar = { navController.navigate(Routes.Suscripciones.route) }
            )
        }

        composable(Routes.Suscripciones.route) {
            SuscripcionesScreen(
                viewModel = vm,
                onAdd = { navController.navigate(Routes.Formulario.createRouteNuevo()) },
                // Añadimos el callback para editar
                onEdit = { id -> navController.navigate(Routes.Formulario.createRouteEditar(id)) },
                onShowNotificationDemo = {
                    // ... (notificationHelper)
                }
            )
        }

        // Actualizamos el composable para que reciba el argumento "id"
        composable(
            route = Routes.Formulario.routeWithArgs, // Usar la nueva ruta con argumentos
            arguments = listOf(navArgument(Routes.Formulario.argId) {
                type = NavType.LongType
                defaultValue = -1L // -1L indica "nuevo"
            })
        ) { backStackEntry ->
            // Obtenemos el ID de los argumentos de la ruta
            val id = backStackEntry.arguments?.getLong(Routes.Formulario.argId) ?: -1L

            FormularioSuscripcionScreen(
                onSaved = { navController.popBackStack() },
                viewModel = vm,
                onBack = { navController.popBackStack() },
                suscripcionId = id
            )
        }
    }
}