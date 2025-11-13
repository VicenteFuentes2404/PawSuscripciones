package com.example.pawsuscripciones.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pawsuscripciones.data.Suscripcion
import com.example.pawsuscripciones.viewmodel.SuscripcionViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuscripcionesScreen(
    viewModel: SuscripcionViewModel,
    onAdd: () -> Unit,
    onEdit: (Long) -> Unit,
    onShowNotificationDemo: () -> Unit
) {
    // 1. Observamos los estados del ViewModel
    val lista by viewModel.suscripciones.collectAsState()
    val divisaState by viewModel.divisaState.collectAsState()

    // 2. Calculamos el total en CLP (base)
    val totalMensualCLP = lista.sumOf { it.monto }

    // 3. Calculamos el total convertido reactivamente en la UI
    val tasaActual = if (divisaState.selectedCode == "CLP") 1.0 else divisaState.rates[divisaState.selectedCode] ?: 1.0
    val totalMensualConvertido = totalMensualCLP * tasaActual

    // 4. Preparamos el formateador de moneda
    val formatter = remember(divisaState.selectedCode) {
        try {
            val locale = if (divisaState.selectedCode == "CLP") {
                Locale("es", "CL")
            } else {
                Locale.getDefault()
            }
            NumberFormat.getCurrencyInstance(locale).apply {
                currency = Currency.getInstance(divisaState.selectedCode)
                maximumFractionDigits = if (divisaState.selectedCode == "CLP") 0 else 2
            }
        } catch (e: Exception) {
            NumberFormat.getCurrencyInstance().apply {
                currency = Currency.getInstance("CLP")
                maximumFractionDigits = 0
            }
        }
    }

    // 5. Lista de monedas disponibles
    val availableCurrencies = remember(divisaState.rates) {
        (listOf("CLP") + divisaState.rates.keys).distinct().sorted()
    }

    var expandedDivisa by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Gestor de Suscripciones",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            if (lista.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize().weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No hay suscripciones.", fontSize = 18.sp)
                }
            } else {
                Column(modifier = Modifier.weight(1f)) {
                    Spacer(Modifier.height(16.dp))

                    // Título "Monto estimado..."
                    Text(
                        text = "Monto estimado mensual",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // --- FILA HÍBRIDA: MONTO + SELECTOR ---
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start // Alineación al inicio para que queden juntos
                    ) {
                        // 1. El Monto (ajustamos un poco el tamaño para que quepa bien)
                        Text(
                            text = formatter.format(totalMensualConvertido),
                            fontSize = 42.sp, // Ligeramente más pequeño que 48sp para balancear
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            // Usamos weight(fill = false) para que ocupe lo que necesita pero no empuje al selector al infinito si sobra espacio
                            modifier = Modifier.weight(weight = 1f, fill = false)
                        )

                        Spacer(Modifier.width(12.dp))

                        // 2. El Selector (Compacto)
                        ExposedDropdownMenuBox(
                            expanded = expandedDivisa,
                            onExpandedChange = { expandedDivisa = !expandedDivisa },
                            modifier = Modifier.width(110.dp) // Ancho fijo y contenido
                        ) {
                            OutlinedTextField(
                                value = divisaState.selectedCode,
                                onValueChange = { },
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedDivisa) },
                                modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable),
                                textStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp), // Bordes más redondeados
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                    focusedBorderColor = MaterialTheme.colorScheme.primary
                                )
                            )
                            ExposedDropdownMenu(
                                expanded = expandedDivisa,
                                onDismissRequest = { expandedDivisa = false }
                            ) {
                                availableCurrencies.forEach { code ->
                                    DropdownMenuItem(
                                        text = { Text(code) },
                                        onClick = {
                                            viewModel.setSelectedDivisa(code)
                                            expandedDivisa = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Mensaje de error discreto si falla la API
                    if (divisaState.error != null) {
                        Text(
                            text = "⚠ ${divisaState.error}",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    Spacer(Modifier.height(24.dp))

                    // --- LISTA DE SUSCRIPCIONES ---
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(lista) { s ->
                            SuscripcionCard(
                                s = s,
                                onDelete = { viewModel.eliminar(s) },
                                onEdit = {
                                    s.id?.let { nonNullId ->
                                        onEdit(nonNullId)
                                    }
                                }
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // --- BOTÓN AÑADIR ---
            Button(
                onClick = onAdd,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    "Añadir Suscripción",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}


@Composable
fun SuscripcionCard(s: Suscripcion, onDelete: () -> Unit, onEdit: () -> Unit) {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val fecha = sdf.format(Date(s.fechaVencimiento))

    var showDialog by remember { mutableStateOf(false) }
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(targetValue = if (isPressed) 0.95f else 1.0f, label = "pressAnimation")

    val isDueToday = Calendar.getInstance().apply {
        timeInMillis = System.currentTimeMillis()
        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
    }.timeInMillis == Calendar.getInstance().apply {
        timeInMillis = s.fechaVencimiento
        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    val (etiquetaColor, etiquetaTextColor) = when (s.etiqueta) {
        "Entretenimiento" -> Pair(Color(0xFFE9D5FF), Color(0xFF581C87))
        "Educación"       -> Pair(Color(0xFFD1FAE5), Color(0xFF065F46))
        "Productividad"   -> Pair(Color(0xFFFEF3C7), Color(0xFFB45309))
        "Utilidad"        -> Pair(Color(0xFFDBEAFE), Color(0xFF1E40AF))
        else              -> Pair(MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.onSecondaryContainer)
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Confirmar Eliminación") },
            text = { Text("¿Estás seguro de que quieres eliminar la suscripción a '${s.nombre}'? Esta acción no se puede deshacer.") },
            confirmButton = { Button(onClick = { onDelete(); showDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) { Text("Eliminar", color = MaterialTheme.colorScheme.onError) } },
            dismissButton = { OutlinedButton(onClick = { showDialog = false }) { Text("Cancelar") } }
        )
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 0.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(s.nombre, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                Spacer(Modifier.height(6.dp))
                Text("Vence el $fecha", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(10.dp))

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = etiquetaColor,
                    ) {
                        Text(
                            text = s.etiqueta,
                            color = etiquetaTextColor,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Text(
                        text = s.metodoPago,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                // Precio individual SIEMPRE en CLP
                Text(
                    text = NumberFormat.getCurrencyInstance(Locale("es", "CL")).apply { maximumFractionDigits = 0 }.format(s.monto),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                if (isDueToday) {
                    Spacer(Modifier.height(8.dp))
                    Surface(color = Color(0xFFFFFBE6), shape = RoundedCornerShape(6.dp), modifier = Modifier.align(Alignment.End)) {
                        Text(
                            text = "Vence Hoy",
                            color = Color(0xFFB54708),
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Editar",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .pointerInput(Unit) { detectTapGestures(onTap = { onEdit() }) }
                    )

                    Spacer(Modifier.width(16.dp))

                    Text(
                        text = "Eliminar",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .graphicsLayer { scaleX = scale; scaleY = scale }
                            .pointerInput(Unit) { detectTapGestures(onPress = { isPressed = true; tryAwaitRelease(); isPressed = false }, onTap = { showDialog = true }) }
                    )
                }
            }
        }
    }
}