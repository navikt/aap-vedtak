package no.nav.aap.app.modell

import no.nav.aap.avro.medlem.v1.Medlem
import no.nav.aap.avro.vedtak.v1.Losning_11_2
import no.nav.aap.avro.vedtak.v1.Sak
import no.nav.aap.avro.vedtak.v1.Soker
import no.nav.aap.avro.vedtak.v1.Vilkarsvurdering
import no.nav.aap.dto.DtoLøsningParagraf_11_2
import no.nav.aap.dto.DtoSak
import no.nav.aap.dto.DtoSøker
import no.nav.aap.dto.DtoVilkårsvurdering
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
    vilkårsvurderinger = vilkarsvurderinger.map(Vilkarsvurdering::toDto)
)

fun Vilkarsvurdering.toDto(): DtoVilkårsvurdering = DtoVilkårsvurdering(
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    løsning_11_2_manuell = losning112Manuell?.let { DtoLøsningParagraf_11_2(it.erMedlem) },
    løsning_11_2_maskinell = losning112Maskinell?.let { DtoLøsningParagraf_11_2(it.erMedlem) },
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
                            .setLosning112Manuell(vilkår.løsning_11_2_manuell?.let { Losning_11_2(it.erMedlem) })
                            .setLosning112Maskinell(vilkår.løsning_11_2_maskinell?.let { Losning_11_2(it.erMedlem) })
                            .build()
                    }
                )
                .setVurderingsdato(sak.vurderingsdato)
                .build()
        }
    ).build()