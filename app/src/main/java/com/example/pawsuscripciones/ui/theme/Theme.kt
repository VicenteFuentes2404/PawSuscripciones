package com.example.pawsuscripciones.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color // Importa Color

// Define tus colores personalizados aquí, inspirados en los bocetos
val AppLightColorScheme = lightColorScheme(
    primary = Color(0xFF1E88E5), // Un azul vibrante para botones y elementos principales
    onPrimary = Color.White,
    secondary = Color(0xFF673AB7), // Un color secundario si lo necesitas
    onSecondary = Color.White,
    background = Color.White, // Fondo blanco para ambas pantallas
    onBackground = Color(0xFF1C1B1F),
    surface = Color.White, // Color de fondo de las tarjetas
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFE7E0EC), // Para los chips no seleccionados
    onSurfaceVariant = Color(0xFF49454F), // Para texto secundario (gris)
    outlineVariant = Color(0xFFD6D6D6), // Para bordes de tarjetas y chips
    secondaryContainer = Color(0xFFD0BCFF), // Fondo de chip seleccionado
    onSecondaryContainer = Color(0xFF21005D), // Texto de chip seleccionado

    // Puedes añadir más colores si los bocetos tienen otros tonos específicos
)

val AppDarkColorScheme = darkColorScheme(
    primary = Color(0xFF8EBBFF), // Versión oscura del azul
    onPrimary = Color(0xFF003061),
    secondary = Color(0xFFD0BCFF),
    onSecondary = Color(0xFF381E72),
    background = Color(0xFF1C1B1F),
    onBackground = Color(0xFFE6E1E5),
    surface = Color(0xFF1C1B1F),
    onSurface = Color(0xFFE6E1E5),
    surfaceVariant = Color(0xFF49454F),
    onSurfaceVariant = Color(0xFFCAC4D0),
    outlineVariant = Color(0xFF948F99),
    secondaryContainer = Color(0xFF4F378B),
    onSecondaryContainer = Color(0xFFE8DDFF),
)


@Composable
fun PawSuscripcionesTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        AppDarkColorScheme
    } else {
        AppLightColorScheme
    }

    MaterialTheme(
        colorScheme = colors,
        typography = MaterialTheme.typography, // Mantén la tipografía por defecto o crea la tuya
        content = content
    )
}