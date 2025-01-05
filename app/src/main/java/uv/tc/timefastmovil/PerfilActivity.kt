package uv.tc.timefastmovil

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.gson.Gson
import com.koushikdutta.ion.Ion
import uv.tc.timefastmovil.Poko.Mensaje
import uv.tc.timefastmovil.databinding.ActivityPerfilBinding
import uv.tc.timefastmovil.poko.Colaborador
import uv.tc.timefastmovil.util.Constantes
import java.io.ByteArrayOutputStream
import java.io.InputStream

class PerfilActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPerfilBinding
    private lateinit var colaborador: Colaborador
    private var fotoPerfilBytes : ByteArray ? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPerfilBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        colaborador = obtenerColaboradorDesdeSharedPreferences()

        val gson = Gson()
        val stringColaborador =gson.toJson(colaborador)
        println("VEAMOS : "+colaborador.idColaborador+" nombre: "+colaborador.nombre)
        //val stringJson = intent.getStringExtra("colaborador")!!
        cargarDatosCliente(stringColaborador)


        binding.btnGuardarCambiosEnvio.setOnClickListener{
            val gson = Gson()
            val idColaborador = colaborador.idColaborador.toString().toInt()
            val noPersonalColaborador = binding.tvNoPersonal.text.toString()
            val nombreColaborador = binding.etNombre.text.toString()
            val apellidoPaternoColaborador = binding.etApellidoPaterno.text.toString()
            val apellidoMaternoColaborador = binding.etApellidoMaterno.text.toString()
            val correoColaborador = binding.etCorreo.text.toString()
            val contrasenaColaborador = binding.etContrasena.text.toString()
            val noLicenciaColaborador = binding.etNoLicencia.text.toString()

            val colaboradorEdicion = Colaborador(idColaborador,nombreColaborador,apellidoPaternoColaborador,
                apellidoMaternoColaborador,correoColaborador,noPersonalColaborador,contrasenaColaborador,
                colaborador.curp.toString(), colaborador.idRol.toString().toInt(),"",
                colaborador.noLicencia.toString(),"")

            val colaboradorJson = gson.toJson(colaboradorEdicion)
            println("JSON DEL OBJETO: ${colaboradorJson}")


            enviarDatosEdicion(colaboradorEdicion)
        }

        binding.btnCancelarCambioEnvio.setOnClickListener{
            //cargarDatosCliente(stringJson)
            Toast.makeText(this@PerfilActivity,"Cambios cancelados",Toast.LENGTH_LONG).show()
            finish()
        }

        binding.logo.setOnClickListener{
            finish()
        }
        binding.logout.setOnClickListener {
            cerrarSesion()
        }
    }

    override fun onStart() {
        super.onStart()

       obtenerFotoColaborador(colaborador.idColaborador)
        binding.ivCambiarFoto.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            seleccionarFotoPerfil.launch(intent)
        }
    }

    fun obtenerColaboradorDesdeSharedPreferences(): Colaborador {
        val sharedPreferences: SharedPreferences = getSharedPreferences("MisEnviosPrefs", Context.MODE_PRIVATE)

        // Obtener el JSON guardado de SharedPreferences
        val colaboradorJson = sharedPreferences.getString("colaborador", null)

        // Si se encontró el JSON, intentar deserializarlo
        if (colaboradorJson != null) {
            return try {
                // Utilizando Gson para deserializar el JSON en un objeto Colaborador
                val gson = Gson()
                gson.fromJson(colaboradorJson, Colaborador::class.java)
            } catch (e: Exception) {
                // En caso de que haya un error de deserialización, mostrar un mensaje de error
                Toast.makeText(this, "Error al cargar los datos del colaborador: ${e.message}", Toast.LENGTH_LONG).show()
                // Devolver un objeto predeterminado
                crearColaboradorPredeterminado()
            }
        } else {
            // Si no se encontró el JSON en SharedPreferences
            Toast.makeText(this, "No se encontró información del colaborador", Toast.LENGTH_LONG).show()
            return crearColaboradorPredeterminado()
        }
    }

    fun crearColaboradorPredeterminado(): Colaborador {
        // Devuelve un objeto Colaborador con valores predeterminados
        return Colaborador(
            idColaborador = 0,
            nombre = "Desconocido",
            apellidoPaterno = "Desconocido",
            apellidoMaterno = "Desconocido",
            correo = "desconocido@dominio.com",
            noPersonal = "0000",
            contrasena = "********",
            curp = "CURP000000000000",
            idRol = 1,
            foto = null,
            noLicencia = "0000",
            nombreColaborador = "Desconocido Desconocido"
        )
    }


    fun enviarDatosEdicion(colaborador: Colaborador){
        println("URL DEL SERVICIO :${Constantes().urlServicio}colaborador/editar-colaborador")
        val gson = Gson()
        val parametros = gson.toJson(colaborador)
        Ion.with(this@PerfilActivity)
            .load("PUT", "${Constantes().urlServicio}colaborador/editar-colaborador")
            .setHeader("Content-Type","application/json")
            .setStringBody(parametros)
            .asString()
            .setCallback{error,result->
                if(error==null){
                    println("SERVICIO RESPUESTA: "+result.toString())
                    respuestaServicioEditar(result)
                }else{
                    Toast.makeText(this@PerfilActivity,"Error en la peticion: "+error.message,Toast.LENGTH_LONG).show()
                    println("Error al editar cliente : "+error.message)
                    println("SERVICIO RESPUESTA: "+result.toString())
                    println("SERVICIO RESPUESTA: "+error.toString())
                }
            }
    }

    fun respuestaServicioEditar(resultado : String){
        try {
            val gson = Gson()
            val msj = gson.fromJson(resultado, Mensaje::class.java)//MAPEA EL JSON DE LA PETICIÓN
            Toast.makeText(this@PerfilActivity,msj.mensaje,Toast.LENGTH_LONG).show()
            println("MENSAJE: "+msj.mensaje)
            if(!msj.error){
                finish()
            }
        }catch (e : Exception){
            Toast.makeText(this@PerfilActivity,"Error al leer la respuesta del servicio: "+e.message,Toast.LENGTH_LONG).show()
        }
    }

    fun obtenerFotoColaborador(idColaborador: Int){
        Ion.with(this@PerfilActivity)
            .load("GET","${Constantes().urlServicio}colaborador/obtener-foto/${idColaborador}")
            .asString()
            .setCallback{e, result ->
                if (e == null) {
                    cargarFotoPerfilColaborador(result)
                }else{
                    Toast.makeText(this@PerfilActivity, "Error: " + e.message, Toast.LENGTH_LONG).show()
                }
            }
        println("URL OBTENER FOTO: ${Constantes().urlServicio}colaborador/obtener-foto/${idColaborador}")
    }

    private val seleccionarFotoPerfil = this.registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){ result : ActivityResult ->
        val data = result.data
        val imgUri = data?.data
        if (imgUri != null){
            fotoPerfilBytes = uriToByteArray(imgUri)
            if (fotoPerfilBytes != null){
                subirFotoPerfil(colaborador.idColaborador)
            }
        }

    }

    private fun uriToByteArray(uri : Uri) : ByteArray? {
        return try {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
            byteArrayOutputStream.toByteArray()
        }catch (e: Exception){
            e.printStackTrace()
            null
        }
    }

    fun subirFotoPerfil(idColaborador: Int){
        Ion.with(this@PerfilActivity)
            .load("PUT", "${Constantes().urlServicio}colaborador/subir-foto/${idColaborador}")
            .setByteArrayBody(fotoPerfilBytes)
            .asString()
            .setCallback { e, result ->
                if (e == null){
                    val gson = Gson()
                    val msj = gson.fromJson(result, Mensaje::class.java)
                    Toast.makeText(this@PerfilActivity, msj.mensaje, Toast.LENGTH_LONG).show()
                    if (!msj.error){
                        obtenerFotoColaborador(colaborador.idColaborador)
                    }
                }else{
                    Toast.makeText(this@PerfilActivity,e.message,Toast.LENGTH_LONG).show()
                }
            }
    }

    fun cargarFotoPerfilColaborador(json : String){
        if (json.isNotEmpty()){
            val gson = Gson()
            val colaboradorFoto = gson.fromJson(json, Colaborador::class.java)
            if (colaboradorFoto.foto != null){
                try {
                    val imgBytes = Base64.decode(colaboradorFoto.foto, Base64.DEFAULT)
                    val imgBitMap = BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.size)
                    binding.ivFotoPerfil.setImageBitmap(imgBitMap)
                }catch (e: Exception){
                    Toast.makeText(this@PerfilActivity, "Error img: "+ e.message,Toast.LENGTH_LONG).show()
                }
            }else{
                Toast.makeText(this@PerfilActivity,"No cuentas con foto de perfil", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun cargarDatosCliente(stringJson : String){
        val gson = Gson()
        colaborador = gson.fromJson(stringJson, Colaborador::class.java)
        binding.tvNoPersonal.setText(colaborador.noPersonal)
        binding.etNombre.setText(colaborador.nombre)
        binding.etApellidoPaterno.setText(colaborador.apellidoPaterno)
        binding.etApellidoMaterno.setText(colaborador.apellidoMaterno)
        binding.etCorreo.setText(colaborador.correo)
        binding.etContrasena.setText(colaborador.contrasena)
        binding.etNoLicencia.setText(colaborador.noLicencia)

    }

    fun validarVacio() : Boolean{
        var valido = false
        if (binding.etNombre.text.isEmpty()){
            binding.etNombre.setError("Nombre obligatorio")
            valido = true
        }
        if (binding.etApellidoPaterno.text.isEmpty()){
            binding.etApellidoPaterno.setError("Apellido paterno obligatorio")
            valido = true
        }
        if (binding.etApellidoMaterno.text.isEmpty()){
            binding.etApellidoMaterno.setError("Apellido materno obligatorio")
            valido = true
        }
        if (binding.etCorreo.text.isEmpty()){
            binding.etCorreo.setError("Correo obligatorio")
            valido = true
        }
        if (binding.etContrasena.text.isEmpty()){
            binding.etContrasena.setError("Contraseña obligatoria")
            valido = true
        }
        if (binding.etNoLicencia.text.isEmpty()){
            binding.etNoLicencia.setError("Numero de licencia obligatoria")
            valido = true
        }
        return valido
    }

    fun cerrarSesion(){
        val intent = Intent(this@PerfilActivity, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        ActivityCompat.finishAffinity(this@PerfilActivity)
    }
}