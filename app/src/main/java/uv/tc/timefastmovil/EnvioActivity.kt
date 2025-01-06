package uv.tc.timefastmovil

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.koushikdutta.ion.Ion
import uv.tc.timefastmovil.databinding.ActivityEnvioBinding
import com.google.gson.reflect.TypeToken
import uv.tc.timefastmovil.Poko.Cliente
import uv.tc.timefastmovil.Poko.Colaborador
import uv.tc.timefastmovil.Poko.Envio

import uv.tc.timefastmovil.util.Constantes

class EnvioActivity : AppCompatActivity() {
    private lateinit var binding:ActivityEnvioBinding
    private lateinit var noGuia : String
    private lateinit var colaboradorJson : String
    private lateinit var colaborador: Colaborador
    private lateinit var envio: Envio
    private lateinit var cliente: Cliente
    val listaEstatus = arrayOf("Pendiente","En Transito","Entregado","Detenido","Cancelado")

    val gson = Gson()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityEnvioBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val spinner: Spinner = findViewById(R.id.combo_estatus)
        val adapter = ArrayAdapter<String>(this@EnvioActivity, android.R.layout.simple_spinner_item,listaEstatus)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        colaboradorJson=intent.getStringExtra("colaborador")!!
        noGuia=intent.getStringExtra("noGuia")!!

        println("Colaborador Envio: ${colaboradorJson}")
        println("No Guia : ${noGuia}")

        colaborador=gson.fromJson(colaboradorJson, Colaborador::class.java)

        obtenerEnvio(noGuia.toInt())
        obtenerDatosColaborador(colaboradorJson)

        binding.iconoPerfilUsuario.setOnClickListener{
            val gson = Gson()
            val colaboradorJSON = gson.toJson(colaborador)
            println("CONDUCTOR ENVIOSACTIVITY ${colaboradorJSON}")
            irPantallaPerfil(colaborador)
        }

        binding.logo.setOnClickListener{
            finish()
        }

        binding.tvPaquetes.setOnClickListener{
            val intent = Intent(this@EnvioActivity, PaquetesActivity::class.java)
            println("ID DEL ENVIO AL DARLE CLIC: "+envio.idEnvio)
            intent.putExtra("envio", envio.noGuia.toString());
            startActivity(intent)
        }
        binding.btnGuardarCambiosEnvio.setOnClickListener{
            val valorSpinner = spinner.selectedItem.toString()
            println("VALOR SPINNER: "+valorSpinner)
            if ((valorSpinner.equals("Detenido") && binding.myTextArea.text.isEmpty()) ||
                (valorSpinner.equals("Cancelado") && binding.myTextArea.text.isEmpty())){
                binding.myTextArea.setError("Comentario Obligatorio")
            }else{
                envio.estatus = valorSpinner
                envio.motivo = binding.myTextArea.text.toString()
                envio.idColaborador = colaborador.idColaborador
                println("ENVIO: "+envio)
                actualizarEnvio(envio)
                finish()
            }
        }


    }

    fun actualizarEnvio(envio: Envio){
        println("ENVIO URL ESTATUS: "+"${Constantes().urlServicio}envio/editar-envio")
        Ion.with(this@EnvioActivity)
            .load("PUT","${Constantes().urlServicio}envio/editar-envio")
            .setHeader("Content-Type", "application/json")
            .setJsonPojoBody(envio)
            .asString()
            .setCallback { e, result ->
                if (e == null){
                    println("ENVIO STATUS"+ result)
                    Toast.makeText(this@EnvioActivity, "Envio actualizado",Toast.LENGTH_LONG).show()
                }else{
                    println("ERROR AL ACTUALIZAR EL ENVIO: "+ e.message)
                    Toast.makeText(this@EnvioActivity, "Error al actualizar el envio",Toast.LENGTH_LONG).show()
                }
            }
    }

    fun obtenerDatosColaborador(jsonColaborador : String){
        if (jsonColaborador != null){
            val gson = Gson()
            colaborador = gson.fromJson(jsonColaborador, Colaborador::class.java)
            println("ApellidoMaterno: "+ colaborador.apellidoMaterno.toString())
        }
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
                    println("DATOS DEL ENVIO: "+envio)
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
        binding.tvNoGuia.text = envio.noGuia.toString()
        binding.tvDestino.text = envio.destino
        binding.tvOrigen.text = "${envio.ciudad}, ${envio.estado}"

        val estatusEnvio = envio.estatus.toString()
        val index = listaEstatus.indexOf(estatusEnvio)
        println("INDICE DE ESTATUS: "+ index)
        if (index >= 0){
            binding.comboEstatus.setSelection(index)
        }
    }

    fun cargarDatosCliente(cliente : Cliente){
         binding.tvNombreCliente.text = cliente.nombre
         binding.tvCorreoCliente.text = cliente.correo
         binding.tvNumCliente.text = cliente.telefono
    }

    fun irPantallaPerfil(colaborador: Colaborador){
        val intent = Intent(this@EnvioActivity,PerfilActivity::class.java)
        val gson = Gson()
        val colaboradorGson = gson.toJson(colaborador)
        intent.putExtra("colaborador",colaboradorGson)
        startActivity(intent)
    }


}