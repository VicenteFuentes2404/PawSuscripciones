package com.example.pawsuscripciones.utils

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    fun formatoFechaActual(): String {
        val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return formato.format(Date())
    }
}
