package uv.tc.timefastmovil

import android.content.Intent
import android.os.Bundle
import android.provider.Telephony.Mms.Intents
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.koushikdutta.ion.Ion
import uv.tc.timefastmovil.databinding.ActivityLoginBinding
import uv.tc.timefastmovil.poko.LoginColaborador
import uv.tc.timefastmovil.util.Constantes

class LoginActivity : AppCompatActivity() {
    private lateinit var binding : ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    override fun onStart() {
        super.onStart()

        binding.btnIniciarSesion.setOnClickListener{
            val noPersonal = binding.etNoPersonal.text.toString()
            val contrasena = binding.etContrasena.text.toString()
            if (camposValidos(noPersonal,contrasena)){
                verificacionCredenciales(noPersonal,contrasena)
            }
        }
    }

    fun camposValidos(noPersonal : String, contrasena : String) : Boolean{
        var validos = true
        if(noPersonal.isEmpty()){
            validos = false
            binding.etNoPersonal.setError("Número de personal obligatorio")
        }
        if(contrasena.isEmpty()){
            validos = false
            binding.etContrasena.setError("Contraseña obligatoria")
        }
        return validos
    }

    fun verificacionCredenciales(noPersonal: String, contrasena: String){
        //Configuración de la librería Ion. Solo se debe hacer la primera vez
        Ion.getDefault(this@LoginActivity).conscryptMiddleware.enable(false)
        Ion.with(this@LoginActivity)
            .load("POST","${Constantes().urlServicio}login/login-colaborador")
            .setHeader("Content-Type","application/x-www-form-urlencoded")
            .setBodyParameter("noPersonal", noPersonal)
            .setBodyParameter("contrasena", contrasena)
            .asString()
            .setCallback { e, result ->
                if (e == null){
                    serializarInformacion(result)
                }else{
                    Toast.makeText(this@LoginActivity,"Error: "+e.message, Toast.LENGTH_LONG).show()
                    println("Error: "+e.message)
                }
            }
    }

    fun serializarInformacion(json:String){
        val gson = Gson()
        val respuestaLoginColaborador = gson.fromJson(json, LoginColaborador::class.java)
        Toast.makeText(this@LoginActivity, respuestaLoginColaborador.mensaje, Toast.LENGTH_LONG).show()
        if (!respuestaLoginColaborador.error){
            val colaboradorJSON = gson.toJson(respuestaLoginColaborador.colaborador)
            println("Respuesta Login : ${colaboradorJSON}")
            irPantallaMisEnvios(colaboradorJSON)
        }
    }
    fun irPantallaMisEnvios(colaborador : String){
        val intent = Intent(this@LoginActivity, MisEnviosActivity::class.java)
        intent.putExtra("colaborador", colaborador)
        startActivity(intent)
        finish()
    }
}