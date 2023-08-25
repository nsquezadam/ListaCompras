package com.example.listacompras

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxColors
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.listacompras.db.Accion
import com.example.listacompras.db.AppDataBase
import com.example.listacompras.db.Compra
import com.example.listacompras.ui.theme.ListaComprasTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppComprasUI()
        }
    }
}
@Composable
fun AppComprasUI(){
    val contexto = LocalContext.current
    val (compras, setCompras)= remember { mutableStateOf(emptyList<Compra>()) }
    val (seleccion, setSeleccion) = remember { mutableStateOf<Compra?>(null) }
    val (accion, setAccion) = remember { mutableStateOf(Accion.LISTAR) }
    LaunchedEffect(compras ){
        withContext(Dispatchers.IO){
            val db= AppDataBase.getInstance(contexto)
            setCompras(db.compraDao().getAll())
            Log.v("Appcompras  ui", "Launchedffect()")
        }
    }
    val onSave={
        setAccion(Accion.LISTAR)
        setCompras(emptyList())
    }
    when (accion){
        Accion.CREAR -> formularioCompraUI(c = null, onSave)
        Accion.EDITAR-> formularioCompraUI(seleccion, onSave)
        else-> CompraListaUI(
            compras,
            onAdd = {setAccion(Accion.CREAR)},
            onEdit= {compra->
                setSeleccion(compra)
                setAccion(Accion.EDITAR)
            }
        )
    }
}

@Composable
fun CompraListaUI(
    compras:List<Compra>, onAdd:() -> Unit={},
    onEdit:(c: Compra)-> Unit = {}){
    val context = LocalContext.current
    // variable de estado mantiene el estado de los valores
    val (compras, setCompras)= remember { mutableStateOf(emptyList<Compra>())
    }
    LaunchedEffect(compras){
        withContext(Dispatchers.IO){
            val dao = AppDataBase.getInstance(context).compraDao()
            setCompras(dao.getAll())
        }
    }
    Scaffold(
        floatingActionButton = { ExtendedFloatingActionButton(
            onClick = { onAdd() },
            icon = {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "agregar"
                )
            },
            text = { Text(text = "Agregar")}
            ) }
    ) { contentPadding ->
        if(compras.isNotEmpty()){
            LazyColumn(modifier = Modifier.fillMaxSize()){
                items(compras){ compra ->
                            CompraItemUI(compra){
                                onEdit(compra)
                                setCompras(emptyList<Compra>())
                            }
                }
            }
        }else{
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "No hay Compras Guardadas")
            }
        }
        }
}

//@Preview(showBackground = true)
@Composable
fun formularioCompraUI(c:Compra?, onSave:()->Unit = {}){
    val alcanceCorrutina = rememberCoroutineScope()
    val contexto = LocalContext.current
    val (compra, setCompra)= remember { mutableStateOf( c?.compra ?: "") }
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold( snackbarHost = { SnackbarHost(snackbarHostState)}) {
            paddingValues ->
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            TextField(value = compra, onValueChange = {setCompra(it)}, label = { Text(text = "Nueva Compra")})
            Spacer(modifier = Modifier.height(20.dp))
            Button(onClick = {
                alcanceCorrutina.launch (Dispatchers.IO){
                    val dao = AppDataBase.getInstance(contexto).compraDao()
                    val compra = Compra(c?.id ?:0,compra,false)
                    if(compra.id > 0 ){
                        dao.updateCompra(compra)
                    }else{
                        dao.insertCompra(compra)
                    }
                    snackbarHostState.showSnackbar("Se ha guardado ${compra.compra}")
                    onSave()
                }

            }) {
                var textoGuardar = "Crear"
                if (c?.id ?: 0 > 0) {
                    textoGuardar = "Guardar"
                }
                Text(textoGuardar)


            }
            if (c?.id ?: 0 > 0) {
                Button(onClick = {
                    alcanceCorrutina.launch(Dispatchers.IO) {
                        val dao =
                            AppDataBase.getInstance(contexto).compraDao()
                        snackbarHostState.showSnackbar("Eliminando item ${c?.compra}")
                        if (c != null) {
                            dao.deleteCompra(c)

                        }
                        onSave()
                    }
                })
                {
                    Text("Eliminar")
                }
                /** Button(onClick = {
                    alcanceCorrutina.launch(Dispatchers.IO) {
                        val dao =
                            AppDataBase.getInstance(contexto).compraDao()
                        snackbarHostState.showSnackbar("Comprado item ${c?.compra}")
                        if (c != null) {
                            c.realizada = true
                            dao.updateCompra(c)

                        }
                        onSave()
                    }
                }) {
                    Text(text = "Comprado")
                }
                **/


            }
        }

    }

}


 @Composable
 fun CompraItemUI(compra:Compra, onClick: () -> Unit={}, onSave: () -> Unit ={}) {
         val alcanceCorrutina = rememberCoroutineScope()
         val contexto = LocalContext.current
         val resources = contexto.resources
         Row (
             modifier= Modifier
                 .fillMaxWidth()
                 .padding(vertical = 20.dp, horizontal = 20.dp)
                 .clickable { onSave() }
         ) {
             if(compra.realizada){
                 val checked = remember { mutableStateOf(true) }
                 Checkbox(checked = checked.value,
                          onCheckedChange = {checked_ ->
                                 alcanceCorrutina.launch(Dispatchers.IO) {
                                  val dao = AppDataBase
                                      .getInstance(contexto)
                                      .compraDao()
                                     checked.value = false
                                     compra.realizada = false
                                  dao.updateCompra(compra)
                                  onClick()
                              }
                      },
                 )
             }else{
                 val checked = remember { mutableStateOf(false) }
                 Checkbox(checked = checked.value,
                          onCheckedChange = { checked_ ->

                              alcanceCorrutina.launch(Dispatchers.IO) {
                              val dao = AppDataBase
                                  .getInstance(contexto)
                                  .compraDao()
                              checked.value = true
                              compra.realizada = true
                              dao.updateCompra(compra)
                              onClick()
                          }},

                 )



             }
             Spacer(modifier= Modifier.width(20.dp))
             Text(
                 text=compra.compra,
                 modifier= Modifier.weight(2f))


         }
     }

@Preview(showBackground = true)
@Composable
fun previewForm(){
    val c = Compra(1,"azucar", false)
    formularioCompraUI(c)
}
