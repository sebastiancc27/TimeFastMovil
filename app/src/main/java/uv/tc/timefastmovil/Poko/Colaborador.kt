package uv.tc.timefastmovil.poko

data class Colaborador(
    val idColaborador: Int,
    var nombre: String,
    var apellidoPaterno: String,
    var apellidoMaterno: String,
    var correo: String,
    var noPersonal: String,
    var contrasena: String,
    var curp: String,
    var idRol: Int,
    var foto: String?,
    var noLicencia: String,
    var nombreColaborador: String
)
