package com.example.pawsuscripciones.viewmodel

import android.app.Application
import com.example.pawsuscripciones.data.SuscripcionRepository
import com.example.pawsuscripciones.notifications.NotificationHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class SuscripcionViewModelTest {

    // Definimos los Mocks (objetos falsos)
    @Mock
    private lateinit var mockApplication: Application

    @Mock
    private lateinit var mockNotificationHelper: NotificationHelper

    @Mock
    private lateinit var mockRepo: SuscripcionRepository

    private lateinit var viewModel: SuscripcionViewModel

    // Creamos un Despachador de pruebas para simular el Hilo Principal
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        // Inicializamos Mockito
        MockitoAnnotations.openMocks(this)

        Dispatchers.setMain(testDispatcher)

        // Simulamos que el repo devuelve una lista vacía para evitar errores en el init
        whenever(mockRepo.getAll()).thenReturn(flowOf(emptyList()))

        // Instanciamos el ViewModel pasando los mocks
        viewModel = SuscripcionViewModel(mockApplication, mockNotificationHelper, mockRepo)
    }

    @After
    fun tearDown() {
        // 4. IMPORTANTE: Limpiar y restaurar el Hilo Principal al terminar
        Dispatchers.resetMain()
    }

    // --- PRUEBA 1: Datos Correctos ---
    @Test
    fun `validarFormulario retorna true cuando los datos son correctos`() {
        // GIVEN (Datos de entrada)
        val nombre = "Netflix"
        val monto = "5000"

        // WHEN (Ejecutamos la validación)
        val resultado = viewModel.validarFormulario(nombre, monto)

        // THEN (Verificamos resultados)
        assertTrue("El resultado debería ser OK", resultado.ok)
        assertNull("No debería haber error de nombre", resultado.nombreError)
        assertNull("No debería haber error de monto", resultado.montoError)
    }

    // --- PRUEBA 2: Nombre Vacío ---
    @Test
    fun `validarFormulario falla cuando el nombre esta vacio`() {
        val nombre = ""
        val monto = "5000"

        val resultado = viewModel.validarFormulario(nombre, monto)

        assertFalse("El resultado NO debería ser OK", resultado.ok)
        assertEquals("Este campo es requerido", resultado.nombreError)
    }

    // --- PRUEBA 3: Monto Inválido ---
    @Test
    fun `validarFormulario falla cuando el monto es 0 o texto`() {
        val nombre = "Spotify"
        val montoCero = "0"
        val montoTexto = "gratis"

        val resultadoCero = viewModel.validarFormulario(nombre, montoCero)
        val resultadoTexto = viewModel.validarFormulario(nombre, montoTexto)

        assertFalse("Monto 0 debería fallar", resultadoCero.ok)
        assertEquals("Ingresa un monto válido mayor a 0", resultadoCero.montoError)

        assertFalse("Monto texto debería fallar", resultadoTexto.ok)
        assertNotNull("Debería haber error en monto texto", resultadoTexto.montoError)
    }
}