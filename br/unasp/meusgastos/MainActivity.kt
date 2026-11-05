package br.unasp.meusgastos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import br.unasp.meusgastos.ui.TelaGastos
import br.unasp.meusgastos.ui.theme.MeusGastosTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MeusGastosTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    TelaGastos(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}
