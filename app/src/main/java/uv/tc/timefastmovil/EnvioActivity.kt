package uv.tc.timefastmovil

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.gson.Gson
import com.koushikdutta.ion.Ion
import uv.tc.timefastmovil.Poko.Cliente
import uv.tc.timefastmovil.Poko.Colaborador
import uv.tc.timefastmovil.Poko.Envio
import uv.tc.timefastmovil.Util.Constantes
import uv.tc.timefastmovil.databinding.ActivityEnvioBinding
import com.google.gson.reflect.TypeToken

class EnvioActivity : AppCompatActivity() {
    private lateinit var binding:ActivityEnvioBinding
    private lateinit var noGuia : String
    private lateinit var colaboradorJson : String
    private lateinit var colaborador : Colaborador
    private  lateinit var envio : Envio
    private lateinit var cliente : Cliente

    val gson = Gson()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityEnvioBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        colaboradorJson=intent.getStringExtra("colaborador")!!
        noGuia=intent.getStringExtra("noGuia")!!

        println("Colaborador Envio: ${colaboradorJson}")
        println("No Guia : ${noGuia}")

        colaborador=gson.fromJson(colaboradorJson, Colaborador::class.java)

        obtenerEnvio(noGuia.toInt())

    }
    fun obtenerEnvio(noGuia : Int){
        Ion.with(this)
            .load("GET", "${Constantes().urlServicio}envio/envio-NoGuia/${noGuia}")
            .setHeader("Content-Type","application/json")
            .asString()
            .setCallback{error,result->
                if(error==null){
                    println("SERVICIO RESPUESTA: "+result.toString())
                    envio=gson.fromJson(result.toString(),Envio::class.java)
                    obtenerCliente(envio.idCliente)
                    cargarDatosEnvio(envio)
                }else{
                    Toast.makeText(this,"Error en la peticion: "+error.message,
                        Toast.LENGTH_LONG).show()
                    println("Error al editar cliente : "+error.message)
                }
            }
    }


    fun obtenerCliente(idCliente: Int) {
        Ion.with(this)
            .load("GET", "${Constantes().urlServicio}cliente/buscar-cliente/${idCliente}")
            .setHeader("Content-Type", "application/json")
            .asString()
            .setCallback { error, result ->
                if (error == null) {
                    println("SERVICIO CLIENTE RESPUESTA: $result")

                    // Deserializa la respuesta como una lista de objetos Cliente
                    val tipo = object : TypeToken<List<Cliente>>() {}.type
                    val clientes: List<Cliente> = gson.fromJson(result, tipo)

                    if (clientes.isNotEmpty()) {
                        cliente = clientes[0]
                        cargarDatosCliente(cliente)
                        println("Cliente obtenido: $cliente")
                    } else {
                        Toast.makeText(this, "No se encontraron clientes", Toast.LENGTH_SHORT).show()
                    }

                } else {
                    Toast.makeText(this, "Error en la petición: ${error.message}", Toast.LENGTH_LONG).show()
                    println("Error al editar cliente: ${error.message}")
                }
            }
    }
fun cargarDatosEnvio(envio: Envio){

    binding.tvPaquetes.text = envio.cantidadPaquetes.toString()
    binding.tvDestino.text = envio.destino
    binding.tvOrigen.text = "${envio.ciudad}, ${envio.estado}"
}
    fun cargarDatosCliente(cliente : Cliente){
     binding.tvNombreCliente.text = cliente.nombre
     binding.tvCorreoCliente.text = cliente.correo
    binding.tvNumCliente.text = cliente.telefono

    }


}