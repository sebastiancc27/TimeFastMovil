package uv.tc.timefastmovil.Poko

data class Paquete(
    val  idPaquete: Int,
    val  noPaquete : Int,
    val  descripcion: String,
    val  peso : Float,
    val      alto :  Float,
    val  ancho: Float,
    val  profundidad : Float,
    val  envio : Int
)
