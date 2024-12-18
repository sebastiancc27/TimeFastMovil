package uv.tc.timefastmovil.Poko

data class Envio(
    val idEnvio: Int,
    var idCliente: Int,
    var calle: String,
    var numero: Int,
    var colonia: String,
    var cp: String,
    var ciudad: String,
    var estado: String,
    var destino: String,
    var noGuia: Int,
    var costo: Float,
    var estatus: String,
    var motivo: String,
    var idConductor: Int,
    var nombreCliente: String,
    var cantidadPaquetes: Int,
    var conductorAsignado: String

)
