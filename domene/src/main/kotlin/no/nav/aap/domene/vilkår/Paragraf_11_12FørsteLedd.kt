package no.nav.aap.domene.vilkår

import no.nav.aap.domene.UlovligTilstandException.Companion.ulovligTilstand
import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.domene.vilkår.Paragraf_11_12FørsteLedd.SøknadMottatt
import no.nav.aap.dto.DtoVilkårsvurdering
import no.nav.aap.dto.Utfall
import no.nav.aap.hendelse.Hendelse
import no.nav.aap.hendelse.LøsningParagraf_11_12FørsteLedd
import no.nav.aap.hendelse.Søknad
import no.nav.aap.hendelse.behov.Behov_11_12FørsteLedd
import org.slf4j.LoggerFactory
import java.time.LocalDate
import java.util.*

private val log = LoggerFactory.getLogger("Paragraf_11_12FørsteLedd")

internal class Paragraf_11_12FørsteLedd private constructor(
    vilkårsvurderingsid: UUID,
    tilstand: Tilstand<Paragraf_11_12FørsteLedd>
) :
    Vilkårsvurdering<Paragraf_11_12FørsteLedd>(
        vilkårsvurderingsid,
        Paragraf.PARAGRAF_11_12,
        Ledd.LEDD_1,
        tilstand
    ) {
    private lateinit var løsning: LøsningParagraf_11_12FørsteLedd

    internal constructor() : this(UUID.randomUUID(), IkkeVurdert)

    override fun <T> callWithReceiver(block: Paragraf_11_12FørsteLedd.() -> T) = this.block()

    object IkkeVurdert : Tilstand.IkkeVurdert<Paragraf_11_12FørsteLedd>() {
        override fun håndterSøknad(
            vilkårsvurdering: Paragraf_11_12FørsteLedd,
            søknad: Søknad,
            fødselsdato: Fødselsdato,
            vurderingsdato: LocalDate
        ) {
            vilkårsvurdering.tilstand(SøknadMottatt, søknad)
        }

        override fun toDto(vilkårsvurdering: Paragraf_11_12FørsteLedd): DtoVilkårsvurdering =
            ulovligTilstand("IkkeVurdert skal håndtere søknad før serialisering")
    }

    object SøknadMottatt : Tilstand.SøknadMottatt<Paragraf_11_12FørsteLedd>() {
        override fun onEntry(vilkårsvurdering: Paragraf_11_12FørsteLedd, hendelse: Hendelse) {
            hendelse.opprettBehov(Behov_11_12FørsteLedd())
        }

        override fun håndterLøsning(
            vilkårsvurdering: Paragraf_11_12FørsteLedd,
            løsning: LøsningParagraf_11_12FørsteLedd
        ) {
            vilkårsvurdering.løsning = løsning
            vilkårsvurdering.tilstand(Oppfylt, løsning)
        }

        override fun toDto(vilkårsvurdering: Paragraf_11_12FørsteLedd): DtoVilkårsvurdering = DtoVilkårsvurdering(
            vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
            vurdertAv = null,
            godkjentAv = null,
            paragraf = vilkårsvurdering.paragraf.name,
            ledd = vilkårsvurdering.ledd.map(Ledd::name),
            utfall = Utfall.IKKE_VURDERT,
            tilstand = tilstandsnavn.name
        )
    }

    object Oppfylt : Tilstand.OppfyltManuelt<Paragraf_11_12FørsteLedd>() {
        override fun toDto(vilkårsvurdering: Paragraf_11_12FørsteLedd): DtoVilkårsvurdering = DtoVilkårsvurdering(
            vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
            vurdertAv = vilkårsvurdering.løsning.vurdertAv(),
            godkjentAv = null,
            paragraf = vilkårsvurdering.paragraf.name,
            ledd = vilkårsvurdering.ledd.map(Ledd::name),
            tilstand = tilstandsnavn.name,
            utfall = Utfall.OPPFYLT,
            løsning_11_12_ledd1_manuell = vilkårsvurdering.løsning.toDto()
        )

        override fun gjenopprettTilstand(
            vilkårsvurdering: Paragraf_11_12FørsteLedd,
            dtoVilkårsvurdering: DtoVilkårsvurdering
        ) {
            val vurdertAv = requireNotNull(dtoVilkårsvurdering.vurdertAv)
            val løsning = requireNotNull(dtoVilkårsvurdering.løsning_11_12_ledd1_manuell)
            vilkårsvurdering.løsning = LøsningParagraf_11_12FørsteLedd(
                vurdertAv = vurdertAv,
                bestemmesAv = løsning.bestemmesAv,
                unntak = løsning.unntak,
                unntaksbegrunnelse = løsning.unntaksbegrunnelse,
                manueltSattVirkningsdato = løsning.manueltSattVirkningsdato
            )
        }
    }

    internal companion object {
        internal fun gjenopprett(vilkårsvurdering: DtoVilkårsvurdering): Paragraf_11_12FørsteLedd =
            Paragraf_11_12FørsteLedd(
                vilkårsvurdering.vilkårsvurderingsid,
                tilknyttetTilstand(enumValueOf(vilkårsvurdering.tilstand))
            )
                .apply { this.tilstand.gjenopprettTilstand(this, vilkårsvurdering) }

        private fun tilknyttetTilstand(tilstandsnavn: Tilstand.Tilstandsnavn) = when (tilstandsnavn) {
            Tilstand.Tilstandsnavn.IKKE_VURDERT -> IkkeVurdert
            Tilstand.Tilstandsnavn.SØKNAD_MOTTATT -> SøknadMottatt
            Tilstand.Tilstandsnavn.OPPFYLT_MANUELT -> Oppfylt
            else -> error("Tilstand ${tilstandsnavn.name} ikke i bruk i Paragraf_11_12FørsteLedd")
        }
    }
}
