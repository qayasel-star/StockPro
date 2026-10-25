package com.stockpro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.stockpro.data.ProductEntity
import com.stockpro.ui.StockProViewModel
import com.stockpro.ui.theme.StockProTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = android.graphics.Color.rgb(16, 23, 19)
        window.navigationBarColor = android.graphics.Color.rgb(16, 23, 19)
        setContent { StockProTheme { StockProApp() } }
    }
}

private val Lime = Color(0xFFB6F36A)
private val Muted = Color(0xFF9BA99E)

@Composable
private fun StockProApp(vm: StockProViewModel = viewModel()) {
    var showWelcome by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) { delay(2300); showWelcome = false }
    AnimatedVisibility(visible = showWelcome, enter = fadeIn(tween(350)), exit = fadeOut(tween(500))) { WelcomeScreen() }
    AnimatedVisibility(visible = !showWelcome, enter = fadeIn(tween(650)) + scaleIn(initialScale = .98f, animationSpec = tween(650))) {
        MainShell(vm)
    }
}

@Composable
private fun WelcomeScreen() {
    val transition = rememberInfiniteTransition(label = "welcome")
    val pulse by transition.animateFloat(initialValue = .94f, targetValue = 1.06f, animationSpec = infiniteRepeatable(tween(1500, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "pulse")
    val shimmer by transition.animateFloat(initialValue = .35f, targetValue = 1f, animationSpec = infiniteRepeatable(tween(1300), RepeatMode.Reverse), label = "shimmer")
    Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF0D1510), Color(0xFF14251A), Color(0xFF0B100D)))), contentAlignment = Alignment.Center) {
        Box(Modifier.size(270.dp).scale(pulse).background(Lime.copy(alpha = .035f), CircleShape))
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Box(Modifier.size(94.dp).background(Color(0xFF1D3322), RoundedCornerShape(28.dp)), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Inventory2, null, tint = Lime, modifier = Modifier.size(48.dp))
                Icon(Icons.Default.TrendingUp, null, tint = Color.White, modifier = Modifier.size(25.dp).align(Alignment.BottomEnd).offset(x = 4.dp, y = 3.dp))
            }
            Spacer(Modifier.height(24.dp))
            Row {
                Text("Stock", color = Color.White, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.ExtraBold)
                Text("Pro", color = Lime, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.ExtraBold)
            }
            Spacer(Modifier.height(8.dp))
            Text("Tu negocio en buenas manos", color = Muted, style = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.height(34.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                repeat(4) { Box(Modifier.size(8.dp).alpha(shimmer).background(Lime.copy(alpha = 1f - it * .12f), CircleShape)) }
            }
            Spacer(Modifier.height(18.dp))
            Text("Preparando tu espacio de trabajo…", color = Muted.copy(alpha = .8f), style = MaterialTheme.typography.labelMedium)
            Spacer(Modifier.height(48.dp))
            Text("Más que un inventario…", color = Color.White.copy(alpha = .88f), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text("¡es tu aliado de negocio!", color = Lime, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        }
    }
}

private enum class Tab(val title: String) { HOME("Inicio"), INVENTORY("Inventario"), SALES("Ventas"), PURCHASES("Compras"), MORE("Más") }

@Composable
private fun MainShell(vm: StockProViewModel) {
    var tab by remember { mutableStateOf(Tab.HOME) }
    var addProductRequest by remember { mutableStateOf(0) }
    val products by vm.products.collectAsStateWithLifecycle()
    val count by vm.productCount.collectAsStateWithLifecycle()
    val low by vm.lowStockCount.collectAsStateWithLifecycle()
    Scaffold(
        containerColor = Color(0xFF101713),
        bottomBar = {
            NavigationBar(containerColor = Color(0xFF17211B), contentColor = Lime) {
                val tabs = listOf(Tab.HOME, Tab.INVENTORY, Tab.SALES, Tab.PURCHASES, Tab.MORE)
                val icons = listOf(Icons.Default.Home, Icons.Default.Inventory2, Icons.Default.ShoppingCart, Icons.Default.ArrowUpward, Icons.Default.MoreHoriz)
                tabs.forEachIndexed { i, item -> NavigationBarItem(selected = tab == item, onClick = { tab = item }, icon = { Icon(icons[i], contentDescription = item.title) }, label = { Text(item.title, maxLines = 1) }, colors = NavigationBarItemDefaults.colors(selectedIconColor = Lime, selectedTextColor = Lime, indicatorColor = Color(0xFF2A3B2C), unselectedIconColor = Muted, unselectedTextColor = Muted)) }
            }
        }
    ) { padding ->
        when (tab) {
            Tab.HOME -> Dashboard(
                Modifier.padding(padding), count, low,
                onInventory = { tab = Tab.INVENTORY },
                onAddProduct = { tab = Tab.INVENTORY; addProductRequest++ }
            )
            Tab.INVENTORY -> InventoryScreen(Modifier.padding(padding), products, vm, addProductRequest, onAddRequestHandled = { addProductRequest = 0 })
            Tab.SALES -> PlaceholderScreen(Modifier.padding(padding), "Ventas", "El TPV y las transacciones de venta se implementarán en la siguiente fase.")
            Tab.PURCHASES -> PlaceholderScreen(Modifier.padding(padding), "Compras", "El registro de compras y la recepción de mercancía están pendientes.")
            Tab.MORE -> PlaceholderScreen(Modifier.padding(padding), "Más herramientas", "Configuración, proveedores, caja, informes y copias de seguridad se incorporarán por módulos.")
        }
    }
}

@Composable
private fun Dashboard(modifier: Modifier, count: Int, low: Int, onInventory: () -> Unit, onAddProduct: () -> Unit) {
    Column(modifier.fillMaxSize().padding(horizontal = 20.dp).padding(top = 24.dp)) {
        Text("Hola, bienvenido 👋", style = MaterialTheme.typography.titleMedium, color = Muted)
        Text("Tu negocio, bajo control", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(22.dp))
        Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF233B28)), shape = RoundedCornerShape(24.dp), modifier = Modifier.fillMaxWidth().clickable(onClick = onInventory)) {
            Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("INVENTARIO", color = Lime, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Text("Todo listo para organizar", color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text("Consulta y actualiza tus productos", color = Color(0xFFC3D1C5))
                }
                Icon(Icons.Default.Inventory2, null, tint = Lime, modifier = Modifier.size(42.dp))
            }
        }
        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard("Productos activos", count.toString(), Icons.Default.Inventory2, Modifier.weight(1f))
            StatCard("Stock bajo", low.toString(), Icons.Default.TrendingUp, Modifier.weight(1f), warning = low > 0)
        }
        Spacer(Modifier.height(24.dp))
        Text("Accesos rápidos", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            QuickAction("Ver inventario", Icons.Default.Inventory2, onInventory, Modifier.weight(1f))
            QuickAction("Añadir producto", Icons.Default.Add, onAddProduct, Modifier.weight(1f))
        }
        Spacer(Modifier.height(20.dp))
        Text("Modo local activado", color = Lime, style = MaterialTheme.typography.labelLarge)
        Text("Los datos se guardan en este dispositivo.", color = Muted, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun StatCard(title: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier = Modifier, warning: Boolean = false) {
    Card(modifier, colors = CardDefaults.cardColors(containerColor = Color(0xFF18211B)), shape = RoundedCornerShape(20.dp)) {
        Column(Modifier.padding(16.dp)) {
            Icon(icon, null, tint = if (warning) Color(0xFFFFB86B) else Lime)
            Spacer(Modifier.height(12.dp))
            Text(value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(title, color = Muted, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun QuickAction(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(modifier.clickable(onClick = onClick), colors = CardDefaults.cardColors(containerColor = Color(0xFF18211B)), shape = RoundedCornerShape(18.dp)) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Icon(icon, null, tint = Lime)
            Text(title, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun InventoryScreen(modifier: Modifier, products: List<ProductEntity>, vm: StockProViewModel, openAddRequest: Int, onAddRequestHandled: () -> Unit) {
    var query by remember { mutableStateOf("") }
    var filterLow by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<ProductEntity?>(null) }
    var showForm by remember { mutableStateOf(false) }
    var adjust by remember { mutableStateOf<ProductEntity?>(null) }
    var message by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(openAddRequest) {
        if (openAddRequest > 0) {
            editing = null
            showForm = true
            onAddRequestHandled()
        }
    }
    val filtered = products.filter { it.name.contains(query, true) || it.sku.contains(query, true) || it.barcode.contains(query, true) }.filter { !filterLow || it.stockQuantity <= it.minimumStock }
    Column(modifier.fillMaxSize().padding(horizontal = 18.dp).padding(top = 18.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("Inventario", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Text("Gestiona tus productos", color = Muted)
            }
            FilledIconButton(onClick = { editing = null; showForm = true }, colors = IconButtonDefaults.filledIconButtonColors(containerColor = Lime, contentColor = Color(0xFF17210F))) { Icon(Icons.Default.Add, "Añadir producto") }
        }
        Spacer(Modifier.height(18.dp))
        OutlinedTextField(value = query, onValueChange = { query = it }, modifier = Modifier.fillMaxWidth(), placeholder = { Text("Buscar producto o código") }, leadingIcon = { Icon(Icons.Default.Search, null) }, singleLine = true, shape = RoundedCornerShape(16.dp))
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = !filterLow, onClick = { filterLow = false }, label = { Text("Todos (${products.size})") })
            FilterChip(selected = filterLow, onClick = { filterLow = true }, label = { Text("Stock bajo") })
        }
        Spacer(Modifier.height(10.dp))
        if (filtered.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Inventory2, null, tint = Muted, modifier = Modifier.size(48.dp))
                    Spacer(Modifier.height(12.dp))
                    Text(if (query.isBlank() && !filterLow) "Todavía no hay productos" else "No se encontraron productos", fontWeight = FontWeight.SemiBold)
                    Text("Pulsa + para registrar el primero.", color = Muted)
                }
            }
        } else {
            LazyColumn(contentPadding = PaddingValues(bottom = 20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(filtered, key = { it.id }) { product ->
                    ProductRow(product, onClick = { editing = product; showForm = true }, onAdjust = { adjust = product })
                }
            }
        }
    }
    if (showForm) ProductDialog(editing, onDismiss = { showForm = false }, onSave = { id, name, sku, barcode, stock, min, unit, notes -> vm.saveProduct(id, name, sku, barcode, stock, min, unit, notes) { error -> if (error != null) message = error else showForm = false } })
    adjust?.let { product -> StockAdjustmentDialog(product, onDismiss = { adjust = null }, onSave = { delta, reason -> vm.adjustStock(product.id, delta, reason) { error -> if (error != null) message = error else adjust = null } }) }
    message?.let { AlertDialog(onDismissRequest = { message = null }, title = { Text("No se pudo completar") }, text = { Text(it) }, confirmButton = { TextButton(onClick = { message = null }) { Text("Aceptar") } }) }
}

@Composable
private fun ProductRow(product: ProductEntity, onClick: () -> Unit, onAdjust: () -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF18211B)), shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(48.dp).background(Color(0xFF2A3B2C), RoundedCornerShape(14.dp)), contentAlignment = Alignment.Center) { Icon(Icons.Default.Inventory2, null, tint = Lime) }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f).clickable(onClick = onClick)) {
                Text(product.name, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(if (product.sku.isBlank()) "Sin referencia" else "Ref. ${product.sku}", color = Muted, style = MaterialTheme.typography.bodySmall)
                Text("Stock: ${product.stockQuantity} ${product.unit}", color = if (product.stockQuantity <= product.minimumStock) Color(0xFFFFB86B) else Lime, style = MaterialTheme.typography.labelMedium)
            }
            IconButton(onClick = onAdjust) { Icon(Icons.Default.Add, "Ajustar stock", tint = Lime) }
        }
    }
}

@Composable
private fun ProductDialog(product: ProductEntity?, onDismiss: () -> Unit, onSave: (Long?, String, String, String, Int, Int, String, String) -> Unit) {
    var name by remember(product?.id) { mutableStateOf(product?.name.orEmpty()) }
    var sku by remember(product?.id) { mutableStateOf(product?.sku.orEmpty()) }
    var barcode by remember(product?.id) { mutableStateOf(product?.barcode.orEmpty()) }
    var stock by remember(product?.id) { mutableStateOf((product?.stockQuantity ?: 0).toString()) }
    var min by remember(product?.id) { mutableStateOf((product?.minimumStock ?: 0).toString()) }
    var unit by remember(product?.id) { mutableStateOf(product?.unit ?: "unidad") }
    var notes by remember(product?.id) { mutableStateOf(product?.notes.orEmpty()) }
    var validation by remember { mutableStateOf<String?>(null) }
    AlertDialog(onDismissRequest = onDismiss, title = { Text(if (product == null) "Nuevo producto" else "Editar producto") }, text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.heightIn(max = 520.dp)) {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item { OutlinedTextField(name, { name = it; validation = null }, label = { Text("Nombre *") }, singleLine = true, modifier = Modifier.fillMaxWidth()) }
                item { OutlinedTextField(sku, { sku = it }, label = { Text("Referencia / SKU") }, singleLine = true, modifier = Modifier.fillMaxWidth()) }
                item { OutlinedTextField(barcode, { barcode = it }, label = { Text("Código de barras") }, singleLine = true, modifier = Modifier.fillMaxWidth()) }
                item { OutlinedTextField(stock, { stock = it.filter { c -> c.isDigit() } }, label = { Text("Stock actual") }, singleLine = true, modifier = Modifier.fillMaxWidth()) }
                item { OutlinedTextField(min, { min = it.filter { c -> c.isDigit() } }, label = { Text("Avisar con stock igual o inferior a") }, singleLine = true, modifier = Modifier.fillMaxWidth()) }
                item { OutlinedTextField(unit, { unit = it }, label = { Text("Unidad (unidad, kg, caja…)") }, singleLine = true, modifier = Modifier.fillMaxWidth()) }
                item { OutlinedTextField(notes, { notes = it }, label = { Text("Notas") }, modifier = Modifier.fillMaxWidth(), minLines = 2) }
                item { validation?.let { Text(it, color = MaterialTheme.colorScheme.error) } }
            }
        }
    }, confirmButton = { TextButton(onClick = { val s = stock.toIntOrNull(); val m = min.toIntOrNull(); if (name.isBlank()) validation = "El nombre es obligatorio." else if (s == null || m == null) validation = "Introduce cantidades válidas." else onSave(product?.id, name, sku, barcode, s, m, unit, notes) }) { Text("Guardar") } }, dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } })
}

@Composable
private fun StockAdjustmentDialog(product: ProductEntity, onDismiss: () -> Unit, onSave: (Int, String) -> Unit) {
    var amount by remember { mutableStateOf("1") }
    var reason by remember { mutableStateOf("") }
    var incoming by remember { mutableStateOf(true) }
    AlertDialog(onDismissRequest = onDismiss, title = { Text("Movimiento de stock") }, text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(product.name, fontWeight = FontWeight.SemiBold)
            Text("Stock actual: ${product.stockQuantity} ${product.unit}", color = Muted)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = incoming, onClick = { incoming = true }, label = { Text("Entrada +") })
                FilterChip(selected = !incoming, onClick = { incoming = false }, label = { Text("Salida −") })
            }
            OutlinedTextField(amount, { amount = it.filter(Char::isDigit) }, label = { Text("Cantidad") }, singleLine = true)
            OutlinedTextField(reason, { reason = it }, label = { Text("Motivo (recomendado)") }, minLines = 2)
        }
    }, confirmButton = { TextButton(onClick = { val n = amount.toIntOrNull(); if (n != null && n > 0) onSave(if (incoming) n else -n, reason.ifBlank { if (incoming) "Entrada manual" else "Salida manual" }) }) { Text("Registrar") } }, dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } })
}

@Composable
private fun PlaceholderScreen(modifier: Modifier, title: String, description: String) {
    Column(modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(Icons.Default.Inventory2, null, tint = Lime, modifier = Modifier.size(54.dp))
        Spacer(Modifier.height(18.dp))
        Text(title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text(description, color = Muted, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        Spacer(Modifier.height(18.dp))
        Text("Módulo pendiente de desarrollo", color = Lime, style = MaterialTheme.typography.labelLarge)
    }
}
