package uv.tc.timefastmovil

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.koushikdutta.ion.Ion
import uv.tc.timefastmovil.Adaptadores.RecycleEnviosAdapter
import uv.tc.timefastmovil.Adaptadores.RecyclePaquetesAdapter
import uv.tc.timefastmovil.Poko.Envio
import uv.tc.timefastmovil.Poko.Paquete
import uv.tc.timefastmovil.databinding.ActivityPaquetesBinding
import uv.tc.timefastmovil.util.Constantes

class PaquetesActivity : AppCompatActivity() {
    private lateinit var binding:ActivityPaquetesBinding
    private lateinit var paquete: Paquete

    private lateinit var recycleview : RecyclerView
    private lateinit var adapter : RecyclePaquetesAdapter
    private lateinit var arrayPaquetes: ArrayList<Paquete>

    private lateinit var idEnvio : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaquetesBinding.inflate(layoutInflater)
        var view = binding.root
        setContentView(view)


        idEnvio=intent.getStringExtra("envio")!!
        println("ID ENVIO PAQUETES: "+idEnvio)

        recycleview=binding.recyclePaquetes
        recycleview.layoutManager = LinearLayoutManager(this@PaquetesActivity)
        arrayPaquetes = arrayListOf<Paquete>()
        adapter = RecyclePaquetesAdapter(arrayPaquetes)
        recycleview.adapter=adapter

        obtenerPaquete(idEnvio)

    }

    fun obtenerPaquete(idEnvio : String){
        val url = "${Constantes().urlServicio}paquete/obtener-paquete-envio/${idEnvio}"
        println("URL PAQUETE: "+url)
        Ion.with(this@PaquetesActivity)
            .load("GET","${Constantes().urlServicio}paquete/obtener-paquete-envio/${idEnvio}")
            .asString()
            .setCallback { e, result ->
                if (e == null){
                    try {
                        println("OBTENERPAQUETES:  ${result}")
                        val gson = Gson()
                        val paquetes = gson.fromJson(result, Array<Paquete>::class.java).toList()
                        if(paquetes.size > 0 ){
                            arrayPaquetes.clear()
                            arrayPaquetes.addAll(paquetes)
                            adapter.notifyDataSetChanged()
                        }else{
                            Toast.makeText(this, "No se encontraron paquetes",Toast.LENGTH_LONG).show();
                        }

                    } catch (ex: Exception) {
                        Toast.makeText(this, "Error al procesar los datos: ${ex.message}", Toast.LENGTH_LONG).show()
                    }
                }else{
                    println("ERROR AL OBTENER LOS PAQUETES: "+ e.message)
                    Toast.makeText(this@PaquetesActivity, "Error al actualizar el envio", Toast.LENGTH_LONG).show()
                }
            }
    }
}