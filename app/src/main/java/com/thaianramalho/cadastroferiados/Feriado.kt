package com.thaianramalho.cadastroferiados

import java.time.LocalDate

data class Feriado(
    val nome: String,
    val data: LocalDate,
    val tipo: TipoFeriado,
    val estado: String? = null,
    val municipio: String? = null
)

enum class TipoFeriado {
    NACIONAL, ESTADUAL, MUNICIPAL
}
