package com.example.pawsuscripciones.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch // Importa 'launch'

@Composable
fun InicioScreen(onEntrar: () -> Unit) {
    var loading by remember { mutableStateOf(false) }
    // 1. Obtiene un CoroutineScope que está ligado al ciclo de vida del Composable.
    val coroutineScope = rememberCoroutineScope()

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "PawSuscripciones", modifier = Modifier.padding(bottom = 24.dp))
            if (loading) {
                CircularProgressIndicator(modifier = Modifier.size(64.dp))
            } else {
                Button(onClick = {
                    loading = true
                    // 2. Usa el scope para lanzar una corrutina.
                    coroutineScope.launch {
                        delay(1400)
                        onEntrar()
                    }
                }) {
                    Text(text = "Entrar")
                }
            }
        }
    }
}
