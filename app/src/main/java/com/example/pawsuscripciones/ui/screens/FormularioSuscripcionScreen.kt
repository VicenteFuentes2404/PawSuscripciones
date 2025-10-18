@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.pawsuscripciones.ui.screens

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pawsuscripciones.data.Suscripcion
import com.example.pawsuscripciones.viewmodel.SuscripcionViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioSuscripcionScreen(
    onSaved: () -> Unit,
    onBack: () -> Unit,
    viewModel: SuscripcionViewModel
) {
    val context = LocalContext.current
    var nombre by remember { mutableStateOf("") }
    var montoText by remember { mutableStateOf("") }
    var fechaMillis by remember { mutableStateOf(System.currentTimeMillis()) }
    var metodoPago by remember { mutableStateOf("Tarjeta de Débito") }
    var etiqueta by remember { mutableStateOf("Entretenimiento") }

    var nombreError by remember { mutableStateOf<String?>(null) }
    var montoError by remember { mutableStateOf<String?>(null) }

    var showSuccessMessage by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val textFieldStyle = TextStyle(fontSize = 18.sp)

    val cal = Calendar.getInstance(); cal.timeInMillis = fechaMillis
    val year = cal.get(Calendar.YEAR); val month = cal.get(Calendar.MONTH); val day = cal.get(Calendar.DAY_OF_MONTH)

    val datePicker = DatePickerDialog(context, { _: DatePicker, y: Int, m: Int, d: Int ->
        val c = Calendar.getInstance(); c.set(y, m, d, 0, 0, 0); fechaMillis = c.timeInMillis
    }, year, month, day)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Añadir Suscripción",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Go back") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize().padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(16.dp))

            // Campos de Nombre, Monto y Fecha (sin cambios)
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it; nombreError = null },
                label = { Text("Nombre") },
                isError = nombreError != null,
                modifier = Modifier.fillMaxWidth(),
                textStyle = textFieldStyle
            )
            if (nombreError != null) {
                Text(nombreError ?: "", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(start = 16.dp, top = 4.dp).fillMaxWidth())
            }
            Spacer(Modifier.height(20.dp))
            OutlinedTextField(
                value = montoText,
                onValueChange = { montoText = it.filter { ch -> ch.isDigit() || ch == '.' }; montoError = null },
                label = { Text("Monto") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = montoError != null,
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = { Text("CLP", modifier = Modifier.padding(end = 16.dp), fontSize = 16.sp) },
                textStyle = textFieldStyle
            )
            if (montoError != null) {
                Text(montoError ?: "", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(start = 16.dp, top = 4.dp).fillMaxWidth())
            }
            Spacer(Modifier.height(20.dp))
            Box(modifier = Modifier.clickable { datePicker.show() }) {
                OutlinedTextField(
                    value = java.text.SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(fechaMillis)),
                    onValueChange = {}, enabled = false, modifier = Modifier.fillMaxWidth(),
                    label = { Text("Fecha de vencimiento") },
                    trailingIcon = { Icon(Icons.Default.DateRange, contentDescription = "Select Date") },
                    textStyle = textFieldStyle,
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
            Spacer(Modifier.height(20.dp))

            // Menú de Método de Pago (sin cambios)
            val opcionesPago = listOf("Tarjeta de Débito", "Tarjeta de Crédito", "PayPal", "Otro")
            var expandedPago by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(expanded = expandedPago, onExpandedChange = { expandedPago = !expandedPago }, modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = metodoPago, onValueChange = {}, readOnly = true,
                    label = { Text("Método de pago") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedPago) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    textStyle = textFieldStyle
                )
                ExposedDropdownMenu(expanded = expandedPago, onDismissRequest = { expandedPago = false }, modifier = Modifier.fillMaxWidth(0.9f)) {
                    opcionesPago.forEach { sel ->
                        DropdownMenuItem(
                            text = { Text(sel, fontSize = 18.sp) },
                            onClick = { metodoPago = sel; expandedPago = false }
                        )
                    }
                }
            }
            Spacer(Modifier.height(24.dp))

            // ▼▼▼ CAMBIO: Selector de etiquetas con botones ▼▼▼
            val opcionesEtiqueta = listOf("Entretenimiento", "Educación", "Productividad", "Utilidad")
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Etiqueta",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 4.dp, bottom = 12.dp)
                )
                // Usamos FlowRow para que los botones se ajusten si no caben en una línea
                @OptIn(ExperimentalLayoutApi::class)
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    opcionesEtiqueta.forEach { option ->
                        val isSelected = etiqueta == option
                        OutlinedButton(
                            onClick = { etiqueta = option },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent,
                            ),
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.outline
                            )
                        ) {
                            Text(text = option, color = if (isSelected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
            }
            // ▲▲▲ FIN DEL CAMBIO ▲▲▲

            Spacer(Modifier.weight(1f))
            AnimatedVisibility(
                visible = showSuccessMessage,
                enter = slideInVertically(initialOffsetY = { it }, animationSpec = tween(400, easing = LinearOutSlowInEasing)) + fadeIn(tween(300)),
                exit = slideOutVertically(targetOffsetY = { it }, animationSpec = tween(400, easing = FastOutLinearInEasing)) + fadeOut(tween(200))
            ) {
                Text(
                    text = "¡Suscripción guardada!",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            Button(
                onClick = {
                    val result = viewModel.validarFormulario(nombre, montoText)
                    if (!result.ok) {
                        nombreError = result.nombreError; montoError = result.montoError; return@Button
                    }
                    val montoVal = montoText.toDouble()
                    val nueva = Suscripcion(nombre = nombre.trim(), monto = montoVal, fechaVencimiento = fechaMillis, metodoPago = metodoPago, etiqueta = etiqueta)
                    viewModel.agregar(nueva) {
                        coroutineScope.launch { showSuccessMessage = true; delay(1500); onSaved() }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    "Guardar",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}