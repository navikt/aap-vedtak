package no.nav.aap.dto

import java.time.LocalDate

data class DtoSøker(
    val personident: String,
    val fødselsdato: LocalDate,
    val saker: List<DtoSak>
)

data class DtoSak(
    val tilstand: String,
    val vilkårsvurderinger: List<DtoVilkårsvurdering>,
    val vurderingsdato: LocalDate
)

data class DtoVilkårsvurdering(
    val paragraf: String,
    val ledd: List<String>,
    val tilstand: String,
    val løsning_11_2_manuell: DtoLøsningParagraf_11_2?,
    val løsning_11_2_maskinell: DtoLøsningParagraf_11_2?,
)

data class DtoLøsningParagraf_11_2(val erMedlem: String)
