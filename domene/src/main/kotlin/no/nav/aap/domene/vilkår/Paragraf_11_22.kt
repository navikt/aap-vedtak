package no.nav.aap.domene.vilkår

import no.nav.aap.domene.UlovligTilstandException.Companion.ulovligTilstand
import no.nav.aap.domene.beregning.Beløp.Companion.beløp
import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.domene.vilkår.Paragraf_11_22.SøknadMottatt
import no.nav.aap.dto.DtoVilkårsvurdering
import no.nav.aap.dto.Utfall
import no.nav.aap.hendelse.Hendelse
import no.nav.aap.hendelse.LøsningParagraf_11_22
import no.nav.aap.hendelse.Søknad
import no.nav.aap.hendelse.behov.Behov_11_22
import org.slf4j.LoggerFactory
import java.time.LocalDate
import java.util.*

private val log = LoggerFactory.getLogger("Paragraf_11_22")

internal class Paragraf_11_22 private constructor(
    vilkårsvurderingsid: UUID,
    tilstand: Tilstand<Paragraf_11_22>
) :
    Vilkårsvurdering<Paragraf_11_22>(
        vilkårsvurderingsid,
        Paragraf.PARAGRAF_11_22,
        Ledd.LEDD_1 + Ledd.LEDD_2,
        tilstand
    ) {
    private lateinit var løsning: LøsningParagraf_11_22

    internal constructor() : this(UUID.randomUUID(), IkkeVurdert)

    override fun <T> callWithReceiver(block: Paragraf_11_22.() -> T) = this.block()

    object IkkeVurdert : Tilstand.IkkeVurdert<Paragraf_11_22>() {
        override fun håndterSøknad(
            vilkårsvurdering: Paragraf_11_22,
            søknad: Søknad,
            fødselsdato: Fødselsdato,
            vurderingsdato: LocalDate
        ) {
            vilkårsvurdering.tilstand(SøknadMottatt, søknad)
        }

        override fun toDto(vilkårsvurdering: Paragraf_11_22): DtoVilkårsvurdering =
            ulovligTilstand("IkkeVurdert skal håndtere søknad før serialisering")
    }

    object SøknadMottatt : Tilstand.SøknadMottatt<Paragraf_11_22>() {
        override fun onEntry(vilkårsvurdering: Paragraf_11_22, hendelse: Hendelse) {
            hendelse.opprettBehov(Behov_11_22())
        }

        override fun håndterLøsning(
            vilkårsvurdering: Paragraf_11_22,
            løsning: LøsningParagraf_11_22
        ) {
            vilkårsvurdering.løsning = løsning

            if (løsning.erOppfylt()) {
                vilkårsvurdering.tilstand(Oppfylt, løsning)
            } else {
                vilkårsvurdering.tilstand(IkkeOppfylt, løsning)
            }
        }

        override fun toDto(vilkårsvurdering: Paragraf_11_22): DtoVilkårsvurdering = DtoVilkårsvurdering(
            vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
            vurdertAv = null,
            godkjentAv = null,
            paragraf = vilkårsvurdering.paragraf.name,
            ledd = vilkårsvurdering.ledd.map(Ledd::name),
            tilstand = tilstandsnavn.name,
            utfall = Utfall.IKKE_VURDERT
        )
    }

    object Oppfylt : Tilstand.OppfyltManuelt<Paragraf_11_22>() {
        override fun yrkesskade(paragraf1122: Paragraf_11_22) = paragraf1122.løsning.yrkesskade()

        override fun toDto(vilkårsvurdering: Paragraf_11_22): DtoVilkårsvurdering = DtoVilkårsvurdering(
            vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
            vurdertAv = vilkårsvurdering.løsning.vurdertAv(),
            godkjentAv = null,
            paragraf = vilkårsvurdering.paragraf.name,
            ledd = vilkårsvurdering.ledd.map(Ledd::name),
            tilstand = tilstandsnavn.name,
            utfall = Utfall.OPPFYLT,
            løsning_11_22_manuell = vilkårsvurdering.løsning.toDto()
        )

        override fun gjenopprettTilstand(vilkårsvurdering: Paragraf_11_22, dtoVilkårsvurdering: DtoVilkårsvurdering) {
            val vurdertAv = requireNotNull(dtoVilkårsvurdering.vurdertAv)
            val løsning = requireNotNull(dtoVilkårsvurdering.løsning_11_22_manuell)
            vilkårsvurdering.løsning = LøsningParagraf_11_22(
                vurdertAv = vurdertAv,
                erOppfylt = løsning.erOppfylt,
                andelNedsattArbeidsevne = løsning.andelNedsattArbeidsevne,
                år = løsning.år,
                antattÅrligArbeidsinntekt = løsning.antattÅrligArbeidsinntekt.beløp
            )
        }
    }

    object IkkeOppfylt : Tilstand.IkkeOppfyltManuelt<Paragraf_11_22>() {
        override fun toDto(vilkårsvurdering: Paragraf_11_22): DtoVilkårsvurdering = DtoVilkårsvurdering(
            vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
            vurdertAv = vilkårsvurdering.løsning.vurdertAv(),
            godkjentAv = null,
            paragraf = vilkårsvurdering.paragraf.name,
            ledd = vilkårsvurdering.ledd.map(Ledd::name),
            tilstand = tilstandsnavn.name,
            utfall = Utfall.IKKE_OPPFYLT,
            løsning_11_22_manuell = vilkårsvurdering.løsning.toDto()
        )

        override fun gjenopprettTilstand(vilkårsvurdering: Paragraf_11_22, dtoVilkårsvurdering: DtoVilkårsvurdering) {
            val vurdertAv = requireNotNull(dtoVilkårsvurdering.vurdertAv)
            val løsning = requireNotNull(dtoVilkårsvurdering.løsning_11_22_manuell)
            vilkårsvurdering.løsning = LøsningParagraf_11_22(
                vurdertAv = vurdertAv,
                erOppfylt = løsning.erOppfylt,
                andelNedsattArbeidsevne = løsning.andelNedsattArbeidsevne,
                år = løsning.år,
                antattÅrligArbeidsinntekt = løsning.antattÅrligArbeidsinntekt.beløp
            )
        }
    }

    internal companion object {
        internal fun gjenopprett(vilkårsvurdering: DtoVilkårsvurdering): Paragraf_11_22 =
            Paragraf_11_22(
                vilkårsvurdering.vilkårsvurderingsid,
                tilknyttetTilstand(enumValueOf(vilkårsvurdering.tilstand))
            )
                .apply { this.tilstand.gjenopprettTilstand(this, vilkårsvurdering) }

        private fun tilknyttetTilstand(tilstandsnavn: Tilstand.Tilstandsnavn) = when (tilstandsnavn) {
            Tilstand.Tilstandsnavn.IKKE_VURDERT -> IkkeVurdert
            Tilstand.Tilstandsnavn.SØKNAD_MOTTATT -> SøknadMottatt
            Tilstand.Tilstandsnavn.OPPFYLT_MANUELT -> Oppfylt
            Tilstand.Tilstandsnavn.IKKE_OPPFYLT_MANUELT -> IkkeOppfylt
            else -> error("Tilstand ${tilstandsnavn.name} ikke i bruk i Paragraf_11_22")
        }
    }
}
