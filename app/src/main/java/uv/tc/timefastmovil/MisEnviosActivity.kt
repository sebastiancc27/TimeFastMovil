package uv.tc.timefastmovil

import android.content.Intent
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
import uv.tc.timefastmovil.Interfaces.ListenerRecycleEnvios
import uv.tc.timefastmovil.Poko.Colaborador
import uv.tc.timefastmovil.Poko.Envio
import uv.tc.timefastmovil.Util.Constantes
import uv.tc.timefastmovil.databinding.ActivityMisEnviosBinding

class MisEnviosActivity : AppCompatActivity() , ListenerRecycleEnvios{
    private lateinit var binding:ActivityMisEnviosBinding
    private lateinit var recycleview : RecyclerView
    private lateinit var adapter : RecycleEnviosAdapter
    private lateinit var arrayEnvios : ArrayList<Envio>
    private lateinit var colaborador: Colaborador
    private var colaboradorJSON = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMisEnviosBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        colaboradorJSON=intent.getStringExtra("colaborador")!!
        println("Colaborador ${colaboradorJSON}")

        recycleview=binding.recycleEnvios
        recycleview.layoutManager=LinearLayoutManager(this@MisEnviosActivity)
        arrayEnvios= arrayListOf<Envio>()

        adapter = RecycleEnviosAdapter(arrayEnvios, this)
        recycleview.adapter = adapter

        obtenerEnvios(36)
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
        intent.putExtra("colaborador", colaboradorJSON)
        intent.putExtra("noGuia",noGuia)
        startActivity(intent)
    }


}