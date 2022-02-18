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
    val løsning_11_2_maskinell: DtoLøsningParagraf_11_2? = null,
    val løsning_11_2_manuell: DtoLøsningParagraf_11_2? = null,
    val løsning_11_3_manuell: DtoLøsningParagraf_11_3? = null,
    val løsning_11_4_ledd2_ledd3_manuell: DtoLøsningParagraf_11_4_ledd2_ledd3? = null,
    val løsning_11_5_manuell: DtoLøsningParagraf_11_5? = null,
    val løsning_11_6_manuell: DtoLøsningParagraf_11_6? = null,
    val løsning_11_12_ledd1_manuell: DtoLøsningParagraf_11_12_ledd1? = null,
    val løsning_11_29_manuell: DtoLøsningParagraf_11_29? = null,
)

data class DtoLøsningParagraf_11_2(val erMedlem: String)
data class DtoLøsningParagraf_11_3(val erOppfylt: Boolean)
data class DtoLøsningParagraf_11_4_ledd2_ledd3(val erOppfylt: Boolean)
data class DtoLøsningParagraf_11_5(val grad: Int)
data class DtoLøsningParagraf_11_6(val erOppfylt: Boolean)
data class DtoLøsningParagraf_11_12_ledd1(val erOppfylt: Boolean)
data class DtoLøsningParagraf_11_29(val erOppfylt: Boolean)
