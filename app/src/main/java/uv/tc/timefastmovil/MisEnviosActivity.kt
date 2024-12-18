package uv.tc.timefastmovil

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.koushikdutta.ion.Ion
import uv.tc.timefastmovil.Adaptadores.RecycleEnviosAdapter
import uv.tc.timefastmovil.Interfaces.ListenerRecycleEnvios
import uv.tc.timefastmovil.Poko.Envio
import uv.tc.timefastmovil.databinding.ActivityMisEnviosBinding
import uv.tc.timefastmovil.poko.Colaborador
import uv.tc.timefastmovil.poko.LoginColaborador
import uv.tc.timefastmovil.util.Constantes

class MisEnviosActivity : AppCompatActivity() , ListenerRecycleEnvios{
    private lateinit var binding:ActivityMisEnviosBinding
    private lateinit var recycleview : RecyclerView
    private lateinit var adapter : RecycleEnviosAdapter
    private lateinit var arrayEnvios: ArrayList<Envio>
    private lateinit var colaborador: Colaborador
    private var colaboradorJson = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMisEnviosBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        val stringJson = intent.getStringExtra("colaborador")!!

        colaboradorJson = stringJson

        obtenerDatosColaborador(colaboradorJson)
        println("ESTE ES EL COLABORADOR JSON: "+ colaboradorJson)

        recycleview = binding.recycleEnvios
        recycleview.layoutManager = LinearLayoutManager(this@MisEnviosActivity)
        arrayEnvios = arrayListOf<Envio>()

        adapter = RecycleEnviosAdapter(arrayEnvios, this@MisEnviosActivity)
        recycleview.adapter = adapter

        obtenerEnvios(colaborador.noPersonal.toString().toInt())

        binding.iconBuscarNumGuia.setOnClickListener{

        }

        binding.iconoPerfilUsuario.setOnClickListener{
            val gson = Gson()
            val colaboradorJSON = gson.toJson(colaborador)
            println("CONDUCTOR ENVIOSACTIVITY ${colaboradorJSON}")
            println("ID Colaborador:"+colaborador.idColaborador.toString())
            obtenerColaboradorRecargar(colaborador.idColaborador.toString().toInt())
        }

    }

    fun obtenerColaboradorRecargar(idColaborador : Int){
        Ion.with(this@MisEnviosActivity)
            .load("GET",Constantes().urlServicio+"/obtener-colaborador-id/${idColaborador}")
            .asString()
            .setCallback{e, result->
                if(e==null){
                    serializarInformacion(result)
                }else{
                    Toast.makeText(this@MisEnviosActivity, "Error: "+e.message,Toast.LENGTH_LONG).show()
                }
            }
    }

    fun serializarInformacion(json:String){
        val gson = Gson()
        val respuestaLoginColaborador = gson.fromJson(json, LoginColaborador::class.java)
        Toast.makeText(this@MisEnviosActivity, respuestaLoginColaborador.mensaje, Toast.LENGTH_LONG).show()
        if(respuestaLoginColaborador.error==false){
            var clienteJson = gson.toJson(respuestaLoginColaborador.colaborador)
            println("COLABRADOR JSON ACTUALIZADO: "+clienteJson)
            irPantallaPerfil(clienteJson)
            }
        }

    fun obtenerDatosColaborador(jsonColaborador : String){
        if (jsonColaborador != null){
            val gson = Gson()
            colaborador = gson.fromJson(jsonColaborador, Colaborador::class.java)
            println("ApellidoMaterno: "+ colaborador.apellidoMaterno.toString())
        }
    }

    fun irPantallaPerfil(colaborador: String){
        val intent = Intent(this@MisEnviosActivity,PerfilActivity::class.java)
        intent.putExtra("colaborador",colaborador)
        startActivity(intent)
    }


    fun obtenerEnvios(noPersonal : Int) {
        Ion.getDefault(this).conscryptMiddleware.enable(false)
        Ion.with(this)
            .load("GET", "${Constantes().urlServicio}envio/obtener-envios-colaborador/${noPersonal}")
            .asString() // Convierte la respuesta en un String
            .setCallback { e, result ->
                if (e == null) {
                    try {
                        println("resultado ${result}")
                        val gson = Gson()
                        val envios = gson.fromJson(result, Array<Envio>::class.java).toList()
                        arrayEnvios.clear()
                        arrayEnvios.addAll(envios)
                        adapter.notifyDataSetChanged()
                    } catch (ex: Exception) {
                        Toast.makeText(this, "Error al procesar los datos: ${ex.message}", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(this, "Ha ocurrido un error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    override fun clickEnvio(position: Int) {
        val noGuia = arrayEnvios[position].noGuia.toString()
        val intent = Intent(this@MisEnviosActivity, EnvioActivity::class.java);
        intent.putExtra("colaborador",colaboradorJson)
        intent.putExtra("noGuia",noGuia)
        startActivity(intent)
    }

}