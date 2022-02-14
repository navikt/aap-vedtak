package no.nav.aap.app.modell

import no.nav.aap.avro.medlem.v1.Medlem
import no.nav.aap.avro.vedtak.v1.Løsning_11_2
import no.nav.aap.avro.vedtak.v1.Sak
import no.nav.aap.avro.vedtak.v1.Søker
import no.nav.aap.avro.vedtak.v1.Vilkårsvurdering
import no.nav.aap.dto.DtoLøsningParagraf_11_2
import no.nav.aap.dto.DtoSak
import no.nav.aap.dto.DtoSøker
import no.nav.aap.dto.DtoVilkårsvurdering
import no.nav.aap.hendelse.LøsningParagraf_11_2

fun Medlem.toDto(): LøsningParagraf_11_2 = LøsningParagraf_11_2(
    erMedlem = LøsningParagraf_11_2.ErMedlem.valueOf(response.erMedlem.name)
)

fun Søker.toDto(): DtoSøker = DtoSøker(
    personident = personident,
    fødselsdato = fødselsdato,
    saker = saker.map(Sak::toDto),
)

fun Sak.toDto(): DtoSak = DtoSak(
    tilstand = tilstand,
    vurderingsdato = vurderingsdato,
    vilkårsvurderinger = vilkårsvurderinger.map(Vilkårsvurdering::toDto)
)

fun Vilkårsvurdering.toDto(): DtoVilkårsvurdering = DtoVilkårsvurdering(
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    løsning_11_2_manuell = løsning112Manuell?.let { DtoLøsningParagraf_11_2(it.erMedlem) },
    løsning_11_2_maskinell = løsning112Maskinell?.let { DtoLøsningParagraf_11_2(it.erMedlem) },
)

fun DtoSøker.toAvro(): Søker = Søker.newBuilder()
    .setPersonident(personident)
    .setFødselsdato(fødselsdato)
    .setSaker(
        saker.map { sak ->
            Sak.newBuilder()
                .setTilstand(sak.tilstand)
                .setVilkårsvurderinger(
                    sak.vilkårsvurderinger.map { vilkår ->
                        Vilkårsvurdering.newBuilder()
                            .setLedd(vilkår.ledd)
                            .setParagraf(vilkår.paragraf)
                            .setTilstand(vilkår.tilstand)
                            .setLøsning112Manuell(vilkår.løsning_11_2_manuell?.let { Løsning_11_2(it.erMedlem) })
                            .setLøsning112Maskinell(vilkår.løsning_11_2_maskinell?.let { Løsning_11_2(it.erMedlem) })
                            .build()
                    }
                )
                .setVurderingsdato(sak.vurderingsdato)
                .build()
        }
    ).build()