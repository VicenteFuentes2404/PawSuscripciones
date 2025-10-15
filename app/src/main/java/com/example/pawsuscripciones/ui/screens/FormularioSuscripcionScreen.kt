@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)


package com.example.pawsuscripciones.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange // Si usas el ícono relleno

import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem

import androidx.compose.material3.TextField

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.pawsuscripciones.data.Suscripcion
import com.example.pawsuscripciones.viewmodel.SuscripcionViewModel
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text

import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioSuscripcionScreen(
    onSaved: () -> Unit,
    viewModel: SuscripcionViewModel
) {
    val context = LocalContext.current

    var nombre by remember { mutableStateOf("") }
    var montoText by remember { mutableStateOf("") }
    var fechaMillis by remember { mutableStateOf(System.currentTimeMillis()) }
    var metodoPago by remember { mutableStateOf("Tarjeta Débito") }
    var etiqueta by remember { mutableStateOf("Entretenimiento") }
    var recordar by remember { mutableStateOf(true) }

    var nombreError by remember { mutableStateOf<String?>(null) }
    var montoError by remember { mutableStateOf<String?>(null) }

    val cal = Calendar.getInstance(); cal.timeInMillis = fechaMillis
    val year = cal.get(Calendar.YEAR); val month = cal.get(Calendar.MONTH); val day = cal.get(Calendar.DAY_OF_MONTH)

    val datePicker = DatePickerDialog(context, { _: DatePicker, y: Int, m: Int, d: Int ->
        val c = Calendar.getInstance()
        c.set(y, m, d, 0, 0, 0)
        fechaMillis = c.timeInMillis
    }, year, month, day)

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Añadir Suscripción", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(value = nombre, onValueChange = { nombre = it; nombreError = null }, label = { Text("Nombre") }, isError = nombreError != null, modifier = Modifier.fillMaxWidth())
        if (nombreError != null) Text(nombreError ?: "", color = MaterialTheme.colorScheme.error)
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(value = montoText, onValueChange = { montoText = it.filter { ch -> ch.isDigit() || ch == '.' }; montoError = null }, label = { Text("Monto (CLP)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), isError = montoError != null, modifier = Modifier.fillMaxWidth())
        if (montoError != null) Text(montoError ?: "", color = MaterialTheme.colorScheme.error)
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(value = java.text.SimpleDateFormat
            ("dd/MM/yyyy",
            Locale.getDefault()).format(Date(fechaMillis)),
            onValueChange = {}, enabled = false,
            modifier = Modifier.fillMaxWidth().clickable { datePicker.show() },
            label = { Text("Fecha de vencimiento") },
            trailingIcon = { Icon(Icons.Default.DateRange,
                contentDescription = null) })
        Spacer(Modifier.height(8.dp))



        val opcionesPago = listOf("Tarjeta Débito", "Tarjeta Crédito", "PayPal", "Otro")
        var expanded by remember { mutableStateOf(false) }

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth() // opcional: hace que todo el box ocupe ancho completo
        ) {
            OutlinedTextField(
                value = metodoPago,
                onValueChange = {},
                readOnly = true,
                label = { Text("Método de pago") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth() // <- hace que el TextField tenga el ancho completo
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth(0.9f) // <- ajusta el ancho del menú desplegable
            ) {
                opcionesPago.forEach { sel ->
                    DropdownMenuItem(
                        text = { Text(sel) },
                        onClick = {
                            metodoPago = sel
                            expanded = false
                        }
                    )
                }
            }
        }


        Spacer(Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            val etiquetas = listOf("Entretenimiento", "Servicios", "Productividad", "Educación")
            etiquetas.forEach { et ->
                Surface(shape = MaterialTheme.shapes.small, tonalElevation = if (et == etiqueta) 4.dp else 0.dp, modifier = Modifier.clickable { etiqueta = et }) {
                    Text(text = et, modifier = Modifier.padding(8.dp))
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            Text("Recordar")
            Spacer(Modifier.width(8.dp))
            Switch(checked = recordar, onCheckedChange = { recordar = it })
        }

        Spacer(Modifier.height(16.dp))

        Button(onClick = {
            val result = viewModel.validarFormulario(nombre, montoText)
            if (!result.ok) {
                nombreError = result.nombreError
                montoError = result.montoError
                return@Button
            }
            val montoVal = montoText.toDouble()
            val nueva = Suscripcion(nombre = nombre.trim(), monto = montoVal, fechaVencimiento = fechaMillis, metodoPago = metodoPago, etiqueta = etiqueta, recordar = recordar)
            viewModel.agregar(nueva) { onSaved() }
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Guardar")
        }
    }
}
