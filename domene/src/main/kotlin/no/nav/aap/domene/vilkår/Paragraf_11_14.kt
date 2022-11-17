package no.nav.aap.domene.vilkår

import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.hendelse.Søknad
import no.nav.aap.modellapi.Paragraf_11_14ModellApi
import no.nav.aap.modellapi.Utfall
import java.time.LocalDate
import java.util.*
import kotlin.properties.Delegates

internal class Paragraf_11_14 private constructor(
    vilkårsvurderingsid: UUID,
    tilstand: Tilstand<Paragraf_11_14, Paragraf_11_14ModellApi>
) :
    Vilkårsvurdering<Paragraf_11_14, Paragraf_11_14ModellApi>(
        vilkårsvurderingsid,
        Paragraf.PARAGRAF_11_14,
        Ledd.LEDD_1,
        tilstand
    ) {
    private var erStudent by Delegates.notNull<Boolean>()

    internal constructor() : this(UUID.randomUUID(), IkkeVurdert)

    private fun vurderErStudent(søknad: Søknad, erStudent: Boolean) {
        this.erStudent = erStudent
        if (erStudent) tilstand(Oppfylt, søknad)
        else tilstand(IkkeOppfylt, søknad)
    }

    override fun <T> callWithReceiver(block: Paragraf_11_14.() -> T) = this.block()

    object IkkeVurdert : Tilstand.IkkeVurdert<Paragraf_11_14, Paragraf_11_14ModellApi>() {
        override fun håndterSøknad(
            vilkårsvurdering: Paragraf_11_14,
            søknad: Søknad,
            fødselsdato: Fødselsdato,
            vurderingsdato: LocalDate
        ) {
            vilkårsvurdering.vurderErStudent(søknad, søknad.erStudent())
        }

        override fun toDto(vilkårsvurdering: Paragraf_11_14): Paragraf_11_14ModellApi = Paragraf_11_14ModellApi(
            vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
            vurdertAv = null,
            kvalitetssikretAv = null,
            paragraf = vilkårsvurdering.paragraf.name,
            ledd = vilkårsvurdering.ledd.map(Ledd::name),
            tilstand = tilstandsnavn.name,
            utfall = Utfall.IKKE_VURDERT,
            vurdertMaskinelt = vurdertMaskinelt,
        )
    }

    object Oppfylt : Tilstand.OppfyltMaskineltKvalitetssikret<Paragraf_11_14, Paragraf_11_14ModellApi>() {
        override fun toDto(vilkårsvurdering: Paragraf_11_14): Paragraf_11_14ModellApi = Paragraf_11_14ModellApi(
            vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
            vurdertAv = "maskinell saksbehandling",
            kvalitetssikretAv = null,
            paragraf = vilkårsvurdering.paragraf.name,
            ledd = vilkårsvurdering.ledd.map(Ledd::name),
            tilstand = tilstandsnavn.name,
            utfall = Utfall.OPPFYLT,
            vurdertMaskinelt = vurdertMaskinelt,
        )
    }

    object IkkeOppfylt : Tilstand.IkkeOppfyltMaskineltKvalitetssikret<Paragraf_11_14, Paragraf_11_14ModellApi>() {
        override fun toDto(vilkårsvurdering: Paragraf_11_14): Paragraf_11_14ModellApi = Paragraf_11_14ModellApi(
            vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
            vurdertAv = "maskinell saksbehandling",
            kvalitetssikretAv = null,
            paragraf = vilkårsvurdering.paragraf.name,
            ledd = vilkårsvurdering.ledd.map(Ledd::name),
            tilstand = tilstandsnavn.name,
            utfall = Utfall.IKKE_OPPFYLT,
            vurdertMaskinelt = vurdertMaskinelt,
        )
    }

    internal companion object {
        internal fun gjenopprett(vilkårsvurderingsid: UUID, tilstandsnavn: Tilstand.Tilstandsnavn) =
            Paragraf_11_14(vilkårsvurderingsid, tilknyttetTilstand(tilstandsnavn))

        private fun tilknyttetTilstand(tilstandsnavn: Tilstand.Tilstandsnavn) = when (tilstandsnavn) {
            Tilstand.Tilstandsnavn.IKKE_VURDERT -> IkkeVurdert
            Tilstand.Tilstandsnavn.OPPFYLT_MASKINELT_KVALITETSSIKRET -> Oppfylt
            Tilstand.Tilstandsnavn.IKKE_OPPFYLT_MASKINELT_KVALITETSSIKRET -> IkkeOppfylt
            else -> error("Tilstand ${tilstandsnavn.name} ikke i bruk i Paragraf_11_14")
        }
    }
}
