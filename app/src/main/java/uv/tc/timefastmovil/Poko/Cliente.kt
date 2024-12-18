package uv.tc.timefastmovil.Poko

data class Cliente(
    val idCliente : Int,
    val nombre : String,
    val apellidoPaterno : String,
    val apellidoMaterno: String,
    val calle: String,
    val numero : Int,
    val colonia: String,
    val cp : String,
    val telefono : String,
    val correo : String,
    val direccion : String

)
