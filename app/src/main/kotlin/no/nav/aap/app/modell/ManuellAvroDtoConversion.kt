package no.nav.aap.app.modell

import no.nav.aap.avro.manuell.v1.*
import no.nav.aap.dto.*

fun Manuell.toDto(): DtoManuell = DtoManuell(
    løsning_11_2_manuell = losning112Manuell?.let { DtoLøsningParagraf_11_2(it.erMedlem) },
    løsning_11_3_manuell = losning113Manuell?.let { DtoLøsningParagraf_11_3(it.erOppfylt) },
    løsning_11_4_ledd2_ledd3_manuell = losning114L2L3Manuell?.let { DtoLøsningParagraf_11_4_ledd2_ledd3(it.erOppfylt) },
    løsning_11_5_manuell = losning115Manuell?.let { DtoLøsningParagraf_11_5(it.grad) },
    løsning_11_6_manuell = losning116Manuell?.let { DtoLøsningParagraf_11_6(it.erOppfylt) },
    løsning_11_12_ledd1_manuell = losning1112L1Manuell?.let { DtoLøsningParagraf_11_12_ledd1(it.erOppfylt) },
    løsning_11_29_manuell = losning1129Manuell?.let { DtoLøsningParagraf_11_29(it.erOppfylt) },
    løsningVurderingAvBeregningsdato = losningVurderingAvBeregningsdato?.let { DtoLøsningVurderingAvBeregningsdato(it.beregningsdato) }
)

fun DtoManuell.toAvro(): Manuell = Manuell(
    løsning_11_2_manuell?.let { Losning_11_2(it.erMedlem) },
    løsning_11_3_manuell?.let { Losning_11_3(it.erOppfylt) },
    løsning_11_4_ledd2_ledd3_manuell?.let { Losning_11_4_l2_l3(it.erOppfylt) },
    løsning_11_5_manuell?.let { Losning_11_5(it.grad) },
    løsning_11_6_manuell?.let { Losning_11_6(it.erOppfylt) },
    løsning_11_12_ledd1_manuell?.let { Losning_11_12_l1(it.erOppfylt) },
    løsning_11_29_manuell?.let { Losning_11_29(it.erOppfylt) },
    løsningVurderingAvBeregningsdato?.let { LosningVurderingAvBeregningsdato(it.beregningsdato) }
)
