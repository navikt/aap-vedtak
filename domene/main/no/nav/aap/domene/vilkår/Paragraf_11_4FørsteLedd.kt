package no.nav.aap.domene.vilkår

import no.nav.aap.domene.UlovligTilstandException
import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.hendelse.Søknad
import no.nav.aap.modellapi.Paragraf_11_4FørsteLeddModellApi
import no.nav.aap.modellapi.Utfall
import org.slf4j.LoggerFactory
import java.time.LocalDate
import java.util.*

private val log = LoggerFactory.getLogger("Paragraf_11_4FørsteLedd")

internal class Paragraf_11_4FørsteLedd private constructor(
    vilkårsvurderingsid: UUID,
    tilstand: Tilstand<Paragraf_11_4FørsteLedd, Paragraf_11_4FørsteLeddModellApi>
) :
    Vilkårsvurdering<Paragraf_11_4FørsteLedd, Paragraf_11_4FørsteLeddModellApi>(
        vilkårsvurderingsid,
        Paragraf.PARAGRAF_11_4,
        Ledd.LEDD_1,
        tilstand
    ) {
    private lateinit var fødselsdato: Fødselsdato
    private lateinit var vurderingsdato: LocalDate

    internal constructor() : this(UUID.randomUUID(), IkkeVurdert)

    override fun <T> callWithReceiver(block: Paragraf_11_4FørsteLedd.() -> T) = this.block()

    private fun vurderAldersvilkår(søknad: Søknad, fødselsdato: Fødselsdato, vurderingsdato: LocalDate) {
        this.fødselsdato = fødselsdato
        this.vurderingsdato = vurderingsdato
        if (fødselsdato.erMellom18Og67År(vurderingsdato)) tilstand(Oppfylt, søknad)
        else tilstand(IkkeOppfylt, søknad)
    }

    object IkkeVurdert : Tilstand.IkkeVurdert<Paragraf_11_4FørsteLedd, Paragraf_11_4FørsteLeddModellApi>() {
        override fun håndterSøknad(
            vilkårsvurdering: Paragraf_11_4FørsteLedd,
            søknad: Søknad,
            fødselsdato: Fødselsdato,
            vurderingsdato: LocalDate
        ) {
            vilkårsvurdering.vurderAldersvilkår(søknad, fødselsdato, vurderingsdato)
        }

        override fun toDto(vilkårsvurdering: Paragraf_11_4FørsteLedd): Paragraf_11_4FørsteLeddModellApi =
            UlovligTilstandException.ulovligTilstand("IkkeVurdert skal håndtere søknad før serialisering")
    }

    object Oppfylt :
        Tilstand.OppfyltMaskineltKvalitetssikret<Paragraf_11_4FørsteLedd, Paragraf_11_4FørsteLeddModellApi>() {
        override fun håndterSøknad(
            vilkårsvurdering: Paragraf_11_4FørsteLedd,
            søknad: Søknad,
            fødselsdato: Fødselsdato,
            vurderingsdato: LocalDate
        ) {
            log.info("Vilkår allerede vurdert til oppfylt. Forventer ikke ny søknad")
        }

        override fun toDto(vilkårsvurdering: Paragraf_11_4FørsteLedd): Paragraf_11_4FørsteLeddModellApi =
            Paragraf_11_4FørsteLeddModellApi(
                vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
                paragraf = vilkårsvurdering.paragraf.name,
                ledd = vilkårsvurdering.ledd.map(Ledd::name),
                tilstand = tilstandsnavn.name,
                utfall = Utfall.OPPFYLT,
                vurdertMaskinelt = vurdertMaskinelt,
                //FIXME: Mangler lagring av løsning
            )
    }

    object IkkeOppfylt :
        Tilstand.IkkeOppfyltMaskineltKvalitetssikret<Paragraf_11_4FørsteLedd, Paragraf_11_4FørsteLeddModellApi>() {
        override fun håndterSøknad(
            vilkårsvurdering: Paragraf_11_4FørsteLedd,
            søknad: Søknad,
            fødselsdato: Fødselsdato,
            vurderingsdato: LocalDate
        ) {
            log.info("Vilkår allerede vurdert til ikke oppfylt. Forventer ikke ny søknad")
        }

        override fun toDto(vilkårsvurdering: Paragraf_11_4FørsteLedd): Paragraf_11_4FørsteLeddModellApi =
            Paragraf_11_4FørsteLeddModellApi(
                vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
                paragraf = vilkårsvurdering.paragraf.name,
                ledd = vilkårsvurdering.ledd.map(Ledd::name),
                tilstand = tilstandsnavn.name,
                utfall = Utfall.IKKE_OPPFYLT,
                vurdertMaskinelt = vurdertMaskinelt,
            )
    }

    internal companion object {
        internal fun gjenopprett(vilkårsvurderingsid: UUID, tilstandsnavn: Tilstand.Tilstandsnavn) =
            Paragraf_11_4FørsteLedd(vilkårsvurderingsid, tilknyttetTilstand(tilstandsnavn))

        private fun tilknyttetTilstand(tilstandsnavn: Tilstand.Tilstandsnavn) = when (tilstandsnavn) {
            Tilstand.Tilstandsnavn.IKKE_VURDERT -> IkkeVurdert
            Tilstand.Tilstandsnavn.OPPFYLT_MASKINELT_KVALITETSSIKRET -> Oppfylt
            Tilstand.Tilstandsnavn.IKKE_OPPFYLT_MASKINELT_KVALITETSSIKRET -> IkkeOppfylt

            else -> error("Tilstand ${tilstandsnavn.name} ikke i bruk i Paragraf_11_4FørsteLedd")
        }
    }
}
