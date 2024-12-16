package uv.tc.timefastmovil.Poko

data class Envio(
    val idEnvio : Int,
    val  idCliente: Int,
    val  calle : String,
    val  numero: Int,
    val  colonia:String,
    val cp:String,
    val ciudad:String,
    val estado:String,
    val destino:String,
    val noGuia:Int,
    val costo:Float,
    val estatus:String,
    val motivo:String,
    val idConductor:Int,
    val nombreCliente:String,
    val cantidadPaquetes:Int,
    val conductorAsignado:String
)
