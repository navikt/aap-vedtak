package no.nav.aap.app.modell

import no.nav.aap.avro.medlem.v1.Medlem
import no.nav.aap.avro.vedtak.v1.*
import no.nav.aap.dto.*
import no.nav.aap.hendelse.LøsningParagraf_11_2

fun Medlem.toDto(): LøsningParagraf_11_2 = LøsningParagraf_11_2(
    erMedlem = LøsningParagraf_11_2.ErMedlem.valueOf(response.erMedlem.name)
)

fun Soker.toDto(): DtoSøker = DtoSøker(
    personident = personident,
    fødselsdato = fodselsdato,
    saker = saker.map(Sak::toDto),
)

fun Sak.toDto(): DtoSak = DtoSak(
    tilstand = tilstand,
    vurderingsdato = vurderingsdato,
    vilkårsvurderinger = vilkarsvurderinger.map(Vilkarsvurdering::toDto),
    vedtak = null //TODO
)

fun Vilkarsvurdering.toDto(): DtoVilkårsvurdering = DtoVilkårsvurdering(
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    løsning_11_2_manuell = losning112Manuell?.let { DtoLøsningParagraf_11_2(it.erMedlem) },
    løsning_11_2_maskinell = losning112Maskinell?.let { DtoLøsningParagraf_11_2(it.erMedlem) },
    løsning_11_3_manuell = losning113Manuell?.let { DtoLøsningParagraf_11_3(it.erOppfylt) },
    løsning_11_4_ledd2_ledd3_manuell = losning114L2L3Manuell?.let { DtoLøsningParagraf_11_4_ledd2_ledd3(it.erOppfylt) },
    løsning_11_5_manuell = losning115Manuell?.let { DtoLøsningParagraf_11_5(it.grad) },
    løsning_11_6_manuell = losning116Manuell?.let { DtoLøsningParagraf_11_6(it.erOppfylt) },
    løsning_11_12_ledd1_manuell = losning1112L1Manuell?.let { DtoLøsningParagraf_11_12_ledd1(it.erOppfylt) },
    løsning_11_29_manuell = losning1129Manuell?.let { DtoLøsningParagraf_11_29(it.erOppfylt) },
)

fun DtoSøker.toAvro(): Soker = Soker.newBuilder()
    .setPersonident(personident)
    .setFodselsdato(fødselsdato)
    .setSaker(
        saker.map { sak ->
            Sak.newBuilder()
                .setTilstand(sak.tilstand)
                .setVilkarsvurderinger(
                    sak.vilkårsvurderinger.map { vilkår ->
                        Vilkarsvurdering.newBuilder()
                            .setLedd(vilkår.ledd)
                            .setParagraf(vilkår.paragraf)
                            .setTilstand(vilkår.tilstand)
                            .setLosning112Manuell(vilkår.løsning_11_2_manuell?.let {
                                Losning_11_2(
                                    it.erMedlem
                                )
                            }).setLosning112Maskinell(vilkår.løsning_11_2_maskinell?.let {
                                Losning_11_2(
                                    it.erMedlem
                                )
                            }).setLosning113Manuell(vilkår.løsning_11_3_manuell?.let {
                                Losning_11_3(
                                    it.erOppfylt
                                )
                            }).setLosning114L2L3Manuell(vilkår.løsning_11_4_ledd2_ledd3_manuell?.let {
                                Losning_11_4_l2_l3(
                                    it.erOppfylt
                                )
                            }).setLosning115Manuell(vilkår.løsning_11_5_manuell?.let {
                                Losning_11_5(
                                    it.grad
                                )
                            }).setLosning1112L1Manuell(vilkår.løsning_11_12_ledd1_manuell?.let {
                                Losning_11_12_l1(
                                    it.erOppfylt
                                )
                            }).setLosning1129Manuell(vilkår.løsning_11_29_manuell?.let {
                                Losning_11_29(
                                    it.erOppfylt
                                )
                            }).build()
                    }
                )
                .setVurderingsdato(sak.vurderingsdato)
                .build()
        }
    ).build()
