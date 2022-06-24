package no.nav.aap.domene.vilkår

import no.nav.aap.domene.UlovligTilstandException.Companion.ulovligTilstand
import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.domene.vilkår.Paragraf_11_5.SøknadMottatt
import no.nav.aap.dto.DtoVilkårsvurdering
import no.nav.aap.dto.Utfall
import no.nav.aap.hendelse.Hendelse
import no.nav.aap.hendelse.LøsningParagraf_11_5
import no.nav.aap.hendelse.LøsningParagraf_11_5.Companion.toDto
import no.nav.aap.hendelse.Søknad
import no.nav.aap.hendelse.behov.Behov_11_5
import java.time.LocalDate
import java.util.*

internal class Paragraf_11_5 private constructor(
    vilkårsvurderingsid: UUID, tilstand: Tilstand<Paragraf_11_5>
) : Vilkårsvurdering<Paragraf_11_5>(
    vilkårsvurderingsid, Paragraf.PARAGRAF_11_5, Ledd.LEDD_1 + Ledd.LEDD_2, tilstand
) {
    private val løsninger = mutableListOf<LøsningParagraf_11_5>()
    private lateinit var nedsattArbeidsevnegrad: LøsningParagraf_11_5.NedsattArbeidsevnegrad

    internal constructor() : this(UUID.randomUUID(), IkkeVurdert)

    override fun <T> callWithReceiver(block: Paragraf_11_5.() -> T) = this.block()

    object IkkeVurdert : Tilstand.IkkeVurdert<Paragraf_11_5>() {
        override fun håndterSøknad(
            vilkårsvurdering: Paragraf_11_5, søknad: Søknad, fødselsdato: Fødselsdato, vurderingsdato: LocalDate
        ) {
            vilkårsvurdering.tilstand(SøknadMottatt, søknad)
        }

        override fun toDto(vilkårsvurdering: Paragraf_11_5): DtoVilkårsvurdering =
            ulovligTilstand("IkkeVurdert skal håndtere søknad før serialisering")
    }

    object SøknadMottatt : Tilstand.SøknadMottatt<Paragraf_11_5>() {
        override fun onEntry(vilkårsvurdering: Paragraf_11_5, hendelse: Hendelse) {
            hendelse.opprettBehov(Behov_11_5())
        }

        override fun håndterLøsning(vilkårsvurdering: Paragraf_11_5, løsning: LøsningParagraf_11_5) {
            løsning.vurderNedsattArbeidsevne(this, vilkårsvurdering)
        }

        internal fun vurderNedsattArbeidsevne(
            vilkårsvurdering: Paragraf_11_5,
            løsning: LøsningParagraf_11_5,
            nedsattArbeidsevnegrad: LøsningParagraf_11_5.NedsattArbeidsevnegrad
        ) {
            vilkårsvurdering.løsninger.add(løsning)
            vilkårsvurdering.nedsattArbeidsevnegrad = nedsattArbeidsevnegrad
            if (nedsattArbeidsevnegrad.erOppfylt()) {
                vilkårsvurdering.tilstand(Oppfylt, løsning)
            } else {
                vilkårsvurdering.tilstand(IkkeOppfylt, løsning)
            }
        }

        override fun toDto(vilkårsvurdering: Paragraf_11_5): DtoVilkårsvurdering = DtoVilkårsvurdering(
            vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
            vurdertAv = null,
            godkjentAv = null,
            paragraf = vilkårsvurdering.paragraf.name,
            ledd = vilkårsvurdering.ledd.map(Ledd::name),
            tilstand = tilstandsnavn.name,
            utfall = Utfall.IKKE_VURDERT
        )
    }

    object Oppfylt : Tilstand.OppfyltManuelt<Paragraf_11_5>() {
        override fun toDto(vilkårsvurdering: Paragraf_11_5): DtoVilkårsvurdering = DtoVilkårsvurdering(
            vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
            vurdertAv = vilkårsvurdering.løsninger.last().vurdertAv(),
            godkjentAv = null,
            paragraf = vilkårsvurdering.paragraf.name,
            ledd = vilkårsvurdering.ledd.map(Ledd::name),
            tilstand = tilstandsnavn.name,
            utfall = Utfall.OPPFYLT,
            løsning_11_5_manuell = vilkårsvurdering.løsninger.toDto()
        )

        override fun gjenopprettTilstand(vilkårsvurdering: Paragraf_11_5, dtoVilkårsvurdering: DtoVilkårsvurdering) {
            val løsning = requireNotNull(dtoVilkårsvurdering.løsning_11_5_manuell)
            vilkårsvurdering.løsninger.addAll(løsning.map {
                LøsningParagraf_11_5(
                    vurdertAv = it.vurdertAv, nedsattArbeidsevnegrad = LøsningParagraf_11_5.NedsattArbeidsevnegrad(
                        kravOmNedsattArbeidsevneErOppfylt = it.kravOmNedsattArbeidsevneErOppfylt,
                        nedsettelseSkyldesSykdomEllerSkade = it.nedsettelseSkyldesSykdomEllerSkade,
                    )
                )
            })
        }
    }

    object IkkeOppfylt : Tilstand.IkkeOppfyltManuelt<Paragraf_11_5>() {
        override fun toDto(vilkårsvurdering: Paragraf_11_5): DtoVilkårsvurdering = DtoVilkårsvurdering(
            vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
            vurdertAv = vilkårsvurdering.løsninger.last().vurdertAv(),
            godkjentAv = null,
            paragraf = vilkårsvurdering.paragraf.name,
            ledd = vilkårsvurdering.ledd.map(Ledd::name),
            tilstand = tilstandsnavn.name,
            utfall = Utfall.IKKE_OPPFYLT,
            løsning_11_5_manuell = vilkårsvurdering.løsninger.toDto()
        )

        override fun gjenopprettTilstand(vilkårsvurdering: Paragraf_11_5, dtoVilkårsvurdering: DtoVilkårsvurdering) {
            val løsning = requireNotNull(dtoVilkårsvurdering.løsning_11_5_manuell)
            vilkårsvurdering.løsninger.addAll(løsning.map {
                LøsningParagraf_11_5(
                    vurdertAv = it.vurdertAv, nedsattArbeidsevnegrad = LøsningParagraf_11_5.NedsattArbeidsevnegrad(
                        kravOmNedsattArbeidsevneErOppfylt = it.kravOmNedsattArbeidsevneErOppfylt,
                        nedsettelseSkyldesSykdomEllerSkade = it.nedsettelseSkyldesSykdomEllerSkade,
                    )
                )
            })
        }
    }

    internal companion object {
        internal fun gjenopprett(vilkårsvurdering: DtoVilkårsvurdering): Paragraf_11_5 = Paragraf_11_5(
            vilkårsvurdering.vilkårsvurderingsid, tilknyttetTilstand(enumValueOf(vilkårsvurdering.tilstand))
        ).apply { this.tilstand.gjenopprettTilstand(this, vilkårsvurdering) }

        private fun tilknyttetTilstand(tilstandsnavn: Tilstand.Tilstandsnavn) = when (tilstandsnavn) {
            Tilstand.Tilstandsnavn.IKKE_VURDERT -> IkkeVurdert
            Tilstand.Tilstandsnavn.SØKNAD_MOTTATT -> SøknadMottatt
            Tilstand.Tilstandsnavn.OPPFYLT_MANUELT -> Oppfylt
            Tilstand.Tilstandsnavn.IKKE_OPPFYLT_MANUELT -> IkkeOppfylt
            else -> error("Tilstand ${tilstandsnavn.name} ikke i bruk i Paragraf_11_5")
        }
    }
}
