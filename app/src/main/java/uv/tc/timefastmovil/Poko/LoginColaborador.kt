package uv.tc.timefastmovil.poko

import uv.tc.timefastmovil.Poko.Colaborador

data class LoginColaborador(
    val error: Boolean,
    val mensaje: String,
    val colaborador: Colaborador?
)
