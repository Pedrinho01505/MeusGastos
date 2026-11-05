package br.unasp.meusgastos.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.unasp.meusgastos.model.Categoria
import br.unasp.meusgastos.model.Gasto
import br.unasp.meusgastos.viewmodel.GastosViewModel
import java.text.NumberFormat
import java.util.Locale

// ---------- Paleta ----------
private val Marinho = Color(0xFF123B5E)
private val Laranja = Color(0xFFF4734B)
private val FundoTela = Color(0xFFF1F5FA)
private val AzulClaro = Color(0xFFE3EEF8)
private val AzulLinha = Color(0xFFB9D6EE)
private val AzulTexto = Color(0xFF5E7C99)

private fun moeda(valor: Double): String =
    NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(valor)

// ---------- Tela (única que conhece o ViewModel) ----------

@Composable
fun TelaGastos(
    vm: GastosViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(FundoTela)
            .padding(16.dp)
    ) {

        Text(
            "Meus Gastos",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Marinho
        )
        Spacer(Modifier.height(16.dp))

        FormularioGasto(
            descricao = vm.descricao,
            valorTexto = vm.valorTexto,
            categoria = vm.categoriaSelecionada,
            erro = vm.erro,
            aoDigitarDescricao = vm::aoDigitarDescricao,
            aoDigitarValor = vm::aoDigitarValor,
            aoSelecionarCategoria = vm::aoSelecionarCategoria,
            aoAdicionar = vm::adicionar
        )
        Spacer(Modifier.height(16.dp))

        FiltroCategorias(selecionada = vm.filtro, aoFiltrar = vm::aoFiltrar)
        Spacer(Modifier.height(12.dp))

        ResumoTotal(total = vm.total, quantidade = vm.quantidade)
        Spacer(Modifier.height(12.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(vm.gastosVisiveis, key = { it.id }) { gasto ->
                LinhaGasto(gasto = gasto, aoRemover = { vm.remover(gasto.id) })
            }
        }
    }
}

// ---------- Componentes sem estado: recebem valores, devolvem eventos ----------

@Composable
fun FormularioGasto(
    descricao: String,
    valorTexto: String,
    categoria: Categoria,
    erro: String?,
    aoDigitarDescricao: (String) -> Unit,
    aoDigitarValor: (String) -> Unit,
    aoSelecionarCategoria: (Categoria) -> Unit,
    aoAdicionar: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        CampoTexto(
            valor = descricao,
            dica = "Descrição do gasto",
            aoMudar = aoDigitarDescricao,
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CampoTexto(
                valor = valorTexto,
                dica = "0,00",
                aoMudar = aoDigitarValor,
                teclado = KeyboardType.Decimal,
                modifier = Modifier.weight(1f)
            )
            Button(
                onClick = aoAdicionar,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Marinho)
            ) {
                Text("Adicionar", fontWeight = FontWeight.Bold)
            }
        }

        if (erro != null) {
            Text(erro, color = Laranja, fontSize = 13.sp)
        }

        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Categoria.entries.forEach { cat ->
                Etiqueta(
                    texto = cat.rotulo,
                    selecionada = cat == categoria,
                    aoClicar = { aoSelecionarCategoria(cat) }
                )
            }
        }
    }
}

@Composable
fun FiltroCategorias(
    selecionada: Categoria?,
    aoFiltrar: (Categoria?) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Etiqueta(
            texto = "Todas",
            selecionada = selecionada == null,
            aoClicar = { aoFiltrar(null) }
        )
        Categoria.entries.forEach { cat ->
            Etiqueta(
                texto = cat.rotulo,
                selecionada = cat == selecionada,
                aoClicar = { aoFiltrar(cat) }
            )
        }
    }
}

@Composable
fun ResumoTotal(
    total: Double,
    quantidade: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Marinho)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text("Total", color = AzulLinha, fontSize = 13.sp)
            Text(
                moeda(total),
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Text("$quantidade gasto(s)", color = AzulLinha, fontSize = 13.sp)
    }
}

@Composable
fun LinhaGasto(
    gasto: Gasto,
    aoRemover: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                gasto.descricao,
                color = Marinho,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
            Text(gasto.categoria.rotulo, color = AzulTexto, fontSize = 13.sp)
        }
        Text(moeda(gasto.valor), color = Marinho, fontSize = 16.sp)
        Text(
            "remover",
            color = Laranja,
            fontSize = 13.sp,
            modifier = Modifier
                .padding(start = 10.dp)
                .clickable { aoRemover() }
        )
    }
}

@Composable
fun Etiqueta(
    texto: String,
    selecionada: Boolean,
    aoClicar: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(if (selecionada) Laranja else AzulClaro)
            .clickable { aoClicar() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            texto,
            fontSize = 14.sp,
            fontWeight = if (selecionada) FontWeight.Bold else FontWeight.Normal,
            color = if (selecionada) Color.White else AzulTexto
        )
    }
}

@Composable
private fun CampoTexto(
    valor: String,
    dica: String,
    aoMudar: (String) -> Unit,
    modifier: Modifier = Modifier,
    teclado: KeyboardType = KeyboardType.Text
) {
    TextField(
        value = valor,
        onValueChange = aoMudar,
        placeholder = { Text(dica, color = AzulLinha) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = teclado),
        shape = RoundedCornerShape(8.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = FundoTela,
            unfocusedContainerColor = FundoTela,
            focusedIndicatorColor = AzulLinha,
            unfocusedIndicatorColor = AzulLinha,
            focusedTextColor = Marinho,
            unfocusedTextColor = Marinho
        ),
        modifier = modifier
    )
}

// ---------- Previews (Etapa 5) ----------

@Preview(showBackground = true)
@Composable
fun ResumoTotalPreview() {
    ResumoTotal(total = 201.70, quantidade = 4)
}

@Preview(showBackground = true)
@Composable
fun LinhaGastoPreview() {
    LinhaGasto(
        gasto = Gasto(1, "Almoço no bandejão", 18.50, Categoria.ALIMENTACAO),
        aoRemover = {}
    )
}

@Preview(showBackground = true)
@Composable
fun FormularioComErroPreview() {
    FormularioGasto(
        descricao = "Café", valorTexto = "abc",
        categoria = Categoria.ALIMENTACAO,
        erro = "O valor precisa ser um número",
        aoDigitarDescricao = {}, aoDigitarValor = {},
        aoSelecionarCategoria = {}, aoAdicionar = {}
    )
}

@Preview(showBackground = true)
@Composable
fun FiltroCategoriasPreview() {
    FiltroCategorias(selecionada = Categoria.TRANSPORTE, aoFiltrar = {})
}

@Preview(showBackground = true)
@Composable
fun EtiquetaPreview() {
    Etiqueta(texto = "Alimentação", selecionada = true, aoClicar = {})
}
