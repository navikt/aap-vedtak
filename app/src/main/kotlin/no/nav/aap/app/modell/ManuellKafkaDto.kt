package no.nav.aap.app.modell

import no.nav.aap.dto.*
import java.time.LocalDate

data class ManuellKafkaDto(
    val løsning_11_2_manuell: Løsning_11_2_manuell? = null,
    val løsning_11_3_manuell: Løsning_11_3_manuell? = null,
    val løsning_11_4_ledd2_ledd3_manuell: Løsning_11_4_ledd2_ledd3_manuell? = null,
    val løsning_11_5_manuell: Løsning_11_5_manuell? = null,
    val løsning_11_6_manuell: Løsning_11_6_manuell? = null,
    val løsning_11_12_ledd1_manuell: Løsning_11_12_ledd1_manuell? = null,
    val løsning_11_29_manuell: Løsning_11_29_manuell? = null,
    val løsningVurderingAvBeregningsdato: LøsningVurderingAvBeregningsdato? = null,
) {
    data class Løsning_11_2_manuell(val erMedlem: String)
    data class Løsning_11_3_manuell(val erOppfylt: Boolean)
    data class Løsning_11_4_ledd2_ledd3_manuell(val erOppfylt: Boolean)
    data class Løsning_11_5_manuell(
        val kravOmNedsattArbeidsevneErOppfylt: Boolean,
        val nedsettelseSkyldesSykdomEllerSkade: Boolean
    )

    data class Løsning_11_6_manuell(val erOppfylt: Boolean)
    data class Løsning_11_12_ledd1_manuell(val erOppfylt: Boolean)
    data class Løsning_11_29_manuell(val erOppfylt: Boolean)
    data class LøsningVurderingAvBeregningsdato(val beregningsdato: LocalDate)

    fun toDto(): DtoManuell = DtoManuell(
        løsning_11_2_manuell = løsning_11_2_manuell?.let { DtoLøsningParagraf_11_2(it.erMedlem) },
        løsning_11_3_manuell = løsning_11_3_manuell?.let { DtoLøsningParagraf_11_3(it.erOppfylt) },
        løsning_11_4_ledd2_ledd3_manuell = løsning_11_4_ledd2_ledd3_manuell?.let {
            DtoLøsningParagraf_11_4_ledd2_ledd3(it.erOppfylt)
        },
        løsning_11_5_manuell = løsning_11_5_manuell?.let {
            DtoLøsningParagraf_11_5(
                kravOmNedsattArbeidsevneErOppfylt = it.kravOmNedsattArbeidsevneErOppfylt,
                nedsettelseSkyldesSykdomEllerSkade = it.nedsettelseSkyldesSykdomEllerSkade,
            )
        },
        løsning_11_6_manuell = løsning_11_6_manuell?.let { DtoLøsningParagraf_11_6(it.erOppfylt) },
        løsning_11_12_ledd1_manuell = løsning_11_12_ledd1_manuell?.let { DtoLøsningParagraf_11_12_ledd1(it.erOppfylt) },
        løsning_11_29_manuell = løsning_11_29_manuell?.let { DtoLøsningParagraf_11_29(it.erOppfylt) },
        løsningVurderingAvBeregningsdato = løsningVurderingAvBeregningsdato?.let {
            DtoLøsningVurderingAvBeregningsdato(it.beregningsdato)
        }
    )

    fun DtoManuell.toJson(): ManuellKafkaDto = ManuellKafkaDto(
        løsning_11_2_manuell?.let { Løsning_11_2_manuell(it.erMedlem) },
        løsning_11_3_manuell?.let { Løsning_11_3_manuell(it.erOppfylt) },
        løsning_11_4_ledd2_ledd3_manuell?.let { Løsning_11_4_ledd2_ledd3_manuell(it.erOppfylt) },
        løsning_11_5_manuell?.let {
            Løsning_11_5_manuell(
                kravOmNedsattArbeidsevneErOppfylt = true,
                nedsettelseSkyldesSykdomEllerSkade = true
            )
        },
        løsning_11_6_manuell?.let { Løsning_11_6_manuell(it.erOppfylt) },
        løsning_11_12_ledd1_manuell?.let { Løsning_11_12_ledd1_manuell(it.erOppfylt) },
        løsning_11_29_manuell?.let { Løsning_11_29_manuell(it.erOppfylt) },
        løsningVurderingAvBeregningsdato?.let { LøsningVurderingAvBeregningsdato(it.beregningsdato) }
    )
}
