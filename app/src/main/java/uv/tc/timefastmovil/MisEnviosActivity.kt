package uv.tc.timefastmovil

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.koushikdutta.ion.Ion
import uv.tc.timefastmovil.Adaptadores.RecycleEnviosAdapter
import uv.tc.timefastmovil.Interfaces.ListenerRecycleEnvios
import uv.tc.timefastmovil.Poko.Colaborador
import uv.tc.timefastmovil.Poko.Envio
import uv.tc.timefastmovil.databinding.ActivityMisEnviosBinding
import uv.tc.timefastmovil.util.Constantes

class MisEnviosActivity : AppCompatActivity() , ListenerRecycleEnvios{
    private lateinit var binding:ActivityMisEnviosBinding
    private lateinit var recycleview : RecyclerView
    private lateinit var adapter : RecycleEnviosAdapter
    private lateinit var arrayEnvios: ArrayList<Envio>
    private lateinit var colaborador: Colaborador
    private lateinit var colaboradorActualizado : Colaborador

    private var colaboradorJson = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMisEnviosBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val stringJson = intent.getStringExtra("colaborador")!!
        obtenerDatosColaborador(stringJson)
        //JSON QUE ME MANDA LA ACTIVITY DEL LOGIN
        colaboradorJson = stringJson


        recycleview = binding.recycleEnvios
        recycleview.layoutManager = LinearLayoutManager(this@MisEnviosActivity)
        arrayEnvios = arrayListOf<Envio>()

        adapter = RecycleEnviosAdapter(arrayEnvios, this@MisEnviosActivity)
        recycleview.adapter = adapter

        //MÉTODO PARA OBTENER LOS ENVIOS Y EL RECYCLE VIEW
        obtenerEnvios(colaborador.noPersonal.toString().toInt())

        binding.iconBuscarNumGuia.setOnClickListener{
            if(binding.etNumGuia.text.isNotEmpty()) {
                var numeroGuia = binding.etNumGuia.text.toString().toInt()
                obtenerEnviosNoGuia(numeroGuia)
            }else{
                Toast.makeText(this@MisEnviosActivity,"Ingrese un número de guía para buscar", Toast.LENGTH_LONG).show()
            }
        }


        binding.iconoPerfilUsuario.setOnClickListener{
            println("ID DEL COLABORADOR: "+colaborador.idColaborador)
            println("COLABORADOR: "+colaborador)
            obtenerColaboradorRecargar(colaborador.idColaborador)
        }

        binding.recargar.setOnClickListener {
            obtenerEnvios(colaborador.noPersonal.toString().toInt())
            binding.etNumGuia.text.clear()
        }

    }

    override fun onResume() {
        super.onResume()
        obtenerEnvios(colaborador.noPersonal.toString().toInt())
    }

    fun obtenerColaboradorRecargar(idColaborador : Int){
        Ion.with(this@MisEnviosActivity)
            .load("GET", Constantes().urlServicio+"colaborador/obtener-foto/${idColaborador}")
            .asString()
            .setCallback{e, result->
                if(e==null){
                    println("RESPUESTA RECIBIDA "+result)
                    serializarInformacion(result)
                }else{
                    Toast.makeText(this@MisEnviosActivity, "Error: "+e.message,Toast.LENGTH_LONG).show()
                }
            }
    }

    fun serializarInformacion(json:String){
        irPantallaPerfil(json)
    }

    fun irPantallaPerfil(colaborador: String){
        val intent = Intent(this@MisEnviosActivity,PerfilActivity::class.java)
        guardarColaboradorEnSharedPreferences(colaborador)
        startActivity(intent)
    }

    fun guardarColaboradorEnSharedPreferences(colaborador: String) {
        val sharedPreferences: SharedPreferences = getSharedPreferences("MisEnviosPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("colaborador", colaborador)
        editor.apply()
    }

    fun obtenerDatosColaborador(jsonColaborador : String){
        if (jsonColaborador != null){
            val gson = Gson()
            colaborador = gson.fromJson(jsonColaborador, Colaborador::class.java)
            println("ApellidoMaterno: "+ colaborador.apellidoMaterno.toString())
        }
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
                        if(envios.size > 0 ){
                            arrayEnvios.clear()
                            arrayEnvios.addAll(envios)
                            adapter.notifyDataSetChanged()
                        }else{
                        Toast.makeText(this, "No se encontraron envios",Toast.LENGTH_LONG).show();
                        }

                    } catch (ex: Exception) {
                        Toast.makeText(this, "Error al procesar los datos: ${ex.message}", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(this, "Ha ocurrido un error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    fun obtenerEnviosNoGuia(noGuia : Int) {
        Ion.getDefault(this).conscryptMiddleware.enable(false)
        Ion.with(this)
            .load("GET", "${Constantes().urlServicio}envio/envio-NoGuia/"+noGuia)
            .asString() // Convierte la respuesta en un String
            .setCallback { e, result ->
                if (e == null) {
                    try {
                        println("resultado ${result}")
                        val gson = Gson()
                        val envio = gson.fromJson(result,Envio::class.java)
                        if(envio!=null){
                            arrayEnvios.clear()
                            arrayEnvios.add(envio)
                            adapter.notifyDataSetChanged()
                        }else{
                            Toast.makeText(this, "No se encontraron envios con el N0. de Guia",Toast.LENGTH_LONG).show();
                        }
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