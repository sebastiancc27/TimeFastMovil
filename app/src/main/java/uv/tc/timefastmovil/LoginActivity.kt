package uv.tc.timefastmovil

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.gson.Gson
import com.koushikdutta.async.Util
import com.koushikdutta.ion.Ion
import uv.tc.timefastmovil.Poko.LoginColaborador
import uv.tc.timefastmovil.Util.Constantes
import uv.tc.timefastmovil.databinding.ActivityLoginBinding

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
            val noPersonal = binding.etNoPersonal.text.toString();
            val contrasena = binding.etContrasena.text.toString();
            if(camposValidos(noPersonal, contrasena)){
                verificarCredenciales(noPersonal,contrasena)
            }
        }
    }

    fun camposValidos (correo : String, password : String): Boolean{
        var camposValidos = true
        if(correo.isEmpty()){
            camposValidos=false
            binding.etNoPersonal.setError("No Personal obligatorio")
        }
        if(password.isEmpty()){
            camposValidos=false
            binding.etContrasena.setError("Contraseña obligatoria")
        }
        return camposValidos
    }
    fun verificarCredenciales( noPersonal : String, password: String){
        Ion.getDefault(this@LoginActivity).conscryptMiddleware.enable(false)
        Ion.with(this@LoginActivity).load("POST","${Constantes().urlServicio}login/login-colaborador")
            .setHeader("Content-Type","application/x-www-form-urlencoded")
            .setBodyParameter("noPersonal", noPersonal)
            .setBodyParameter("contrasena",password)
            .asString().setCallback { e, result ->
                if(e==null){
                    serializarInformacion(result)
                }else{
                    Toast.makeText(this@LoginActivity,"Ha ocurrido un error: "+e.message, Toast.LENGTH_LONG).show()
                }
            }//EL RESULT ES LA CADENA STRING DEL JSON
    }
    fun serializarInformacion(json:String){
        val gson = Gson()
        val respuestaLoginColaborador = gson.fromJson(json, LoginColaborador::class.java)
        Toast.makeText(this@LoginActivity, respuestaLoginColaborador.mensaje, Toast.LENGTH_LONG).show()
        if(respuestaLoginColaborador.error==false){
            var clienteJson = gson.toJson(respuestaLoginColaborador.colaborador)
            irPantallaPrincipal(clienteJson)
        }
    }

    fun irPantallaPrincipal(colaborador : String){
        val intent = Intent(this@LoginActivity, MisEnviosActivity::class.java)
        intent.putExtra("colaborador", colaborador)
        startActivity(intent)
        finish()
    }


}