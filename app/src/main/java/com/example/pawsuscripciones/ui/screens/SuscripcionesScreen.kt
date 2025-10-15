@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)


package com.example.pawsuscripciones.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pawsuscripciones.data.Suscripcion
import com.example.pawsuscripciones.viewmodel.SuscripcionViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SuscripcionesScreen(
    viewModel: SuscripcionViewModel,
    onAdd: () -> Unit,
    onShowNotificationDemo: () -> Unit
) {
    // Observamos el StateFlow expuesto por el ViewModel
    val lista = viewModel.suscripciones.collectAsState()

    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("Suscripciones") }) },
        floatingActionButton = { FloatingActionButton(onClick = onAdd) { Text("+") } }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (lista.value.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("No hay suscripciones. Pulsa + para agregar.")
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = onShowNotificationDemo) { Text("Probar notificación") }
                    }
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                    items(lista.value) { s ->
                        SuscripcionCard(s, onDelete = { viewModel.eliminar(s) })
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun SuscripcionCard(s: Suscripcion, onDelete: () -> Unit) {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val fecha = sdf.format(Date(s.fechaVencimiento))
    Card(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium) {
        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text(text = s.nombre, style = MaterialTheme.typography.titleMedium)
                Text(text = "Vence: $fecha")
                Text(text = "Método: ${s.metodoPago}")
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(text = String.format(Locale.getDefault(), "$%.2f", s.monto))
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Eliminar", modifier = Modifier.clickable { onDelete() }.padding(6.dp), color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
