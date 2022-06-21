package no.nav.aap.domene.vilkår

import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.dto.DtoVilkårsvurdering
import no.nav.aap.dto.Utfall
import no.nav.aap.hendelse.Søknad
import org.slf4j.LoggerFactory
import java.time.LocalDate
import java.util.*
import kotlin.properties.Delegates

private val log = LoggerFactory.getLogger("Paragraf_11_14")

internal class Paragraf_11_14 private constructor(
    vilkårsvurderingsid: UUID,
    tilstand: Tilstand<Paragraf_11_14>
) :
    Vilkårsvurdering<Paragraf_11_14>(
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

    object IkkeVurdert : Tilstand.IkkeVurdert<Paragraf_11_14>() {
        override fun håndterSøknad(
            vilkårsvurdering: Paragraf_11_14,
            søknad: Søknad,
            fødselsdato: Fødselsdato,
            vurderingsdato: LocalDate
        ) {
            vilkårsvurdering.vurderErStudent(søknad, søknad.erStudent())
        }

        override fun toDto(vilkårsvurdering: Paragraf_11_14): DtoVilkårsvurdering = DtoVilkårsvurdering(
            vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
            vurdertAv = null,
            godkjentAv = null,
            paragraf = vilkårsvurdering.paragraf.name,
            ledd = vilkårsvurdering.ledd.map(Ledd::name),
            tilstand = tilstandsnavn.name,
            utfall = Utfall.IKKE_VURDERT
        )
    }

    object Oppfylt : Tilstand.OppfyltMaskinelt<Paragraf_11_14>() {
        override fun toDto(vilkårsvurdering: Paragraf_11_14): DtoVilkårsvurdering = DtoVilkårsvurdering(
            vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
            vurdertAv = "maskinell saksbehandling",
            godkjentAv = null,
            paragraf = vilkårsvurdering.paragraf.name,
            ledd = vilkårsvurdering.ledd.map(Ledd::name),
            tilstand = tilstandsnavn.name,
            utfall = Utfall.OPPFYLT
        )
    }

    object IkkeOppfylt : Tilstand.IkkeOppfyltMaskinelt<Paragraf_11_14>() {
        override fun toDto(vilkårsvurdering: Paragraf_11_14): DtoVilkårsvurdering = DtoVilkårsvurdering(
            vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
            vurdertAv = "maskinell saksbehandling",
            godkjentAv = null,
            paragraf = vilkårsvurdering.paragraf.name,
            ledd = vilkårsvurdering.ledd.map(Ledd::name),
            tilstand = tilstandsnavn.name,
            utfall = Utfall.IKKE_OPPFYLT
        )
    }

    internal companion object {
        internal fun gjenopprett(vilkårsvurdering: DtoVilkårsvurdering): Paragraf_11_14 =
            Paragraf_11_14(
                vilkårsvurdering.vilkårsvurderingsid,
                tilknyttetTilstand(enumValueOf(vilkårsvurdering.tilstand))
            )
                .apply { this.tilstand.gjenopprettTilstand(this, vilkårsvurdering) }

        private fun tilknyttetTilstand(tilstandsnavn: Tilstand.Tilstandsnavn) = when (tilstandsnavn) {
            Tilstand.Tilstandsnavn.IKKE_VURDERT -> IkkeVurdert
            Tilstand.Tilstandsnavn.OPPFYLT_MASKINELT -> Oppfylt
            Tilstand.Tilstandsnavn.IKKE_OPPFYLT_MASKINELT -> IkkeOppfylt
            else -> error("Tilstand ${tilstandsnavn.name} ikke i bruk i Paragraf_11_14")
        }
    }
}
