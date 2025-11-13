package com.example.pawsuscripciones.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp // ▼▼▼ CAMBIO ▼▼▼ Import para tamaño de fuente
import com.example.pawsuscripciones.R
import com.example.pawsuscripciones.viewmodel.SuscripcionViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun InicioScreen(viewModel: SuscripcionViewModel, onEntrar: () -> Unit) {
    var loading by remember { mutableStateOf(false) }
    var contentVisible by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val scaleAnim by animateFloatAsState(
        targetValue = if (contentVisible) 1f else 0.8f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ), label = "logoScale"
    )

    LaunchedEffect(Unit) {
        delay(300)
        contentVisible = true
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                // ▼▼▼ CAMBIO ▼▼▼ Aumentamos el padding general
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedVisibility(
                visible = contentVisible,
                enter = fadeIn(animationSpec = tween(500)) + scaleIn(animationSpec = tween(500)),
                exit = fadeOut(animationSpec = tween(300))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo de PawSuscripciones",
                    modifier = Modifier
                        .size(500.dp)
                        .padding(bottom = 80.dp)
                        .graphicsLayer {
                            scaleX = scaleAnim
                            scaleY = scaleAnim
                        }
                )
            }


            AnimatedVisibility(
                visible = contentVisible,
                enter = slideInVertically(
                    initialOffsetY = { fullHeight -> fullHeight / 4 },
                    animationSpec = tween(durationMillis = 600, delayMillis = 200, easing = EaseOutCubic)
                ) + fadeIn(
                    animationSpec = tween(durationMillis = 600, delayMillis = 200)
                )
            ) {
                AnimatedContent(
                    targetState = loading,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
                    }, label = "loadingContent"
                ) { targetLoading ->
                    if (targetLoading) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(
                                // ▼▼▼ CAMBIO ▼▼▼ Aumentamos tamaño del spinner
                                modifier = Modifier.size(80.dp),
                                color = MaterialTheme.colorScheme.primary,
                                // ▼▼▼ CAMBIO ▼▼▼ Aumentamos el grosor
                                strokeWidth = 5.dp
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Text(
                                text = "Cargando...",
                                // ▼▼▼ CAMBIO ▼▼▼ Aumentamos tamaño de la fuente
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        Button(
                            onClick = {
                                loading = true
                                coroutineScope.launch {
                                    viewModel.refreshData()
                                    onEntrar()
                                }
                            },
                            modifier = Modifier

                                .fillMaxWidth(0.8f)
                                .height(64.dp),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Text(
                                text = "Entrar",

                                fontSize = 20.sp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}