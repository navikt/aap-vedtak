package no.nav.aap.domene.vilkår

import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.dto.DtoVilkårsvurdering
import no.nav.aap.hendelse.Søknad
import no.nav.aap.visitor.SøkerVisitor
import org.slf4j.LoggerFactory
import java.time.LocalDate
import java.util.*
import kotlin.properties.Delegates

private val log = LoggerFactory.getLogger("Paragraf_11_14")

internal class Paragraf_11_14 private constructor(
    vilkårsvurderingsid: UUID,
    private var tilstand: Tilstand
) :
    Vilkårsvurdering(vilkårsvurderingsid, Paragraf.PARAGRAF_11_14, Ledd.LEDD_1) {
    private var erStudent by Delegates.notNull<Boolean>()

    internal constructor() : this(UUID.randomUUID(), Tilstand.IkkeVurdert)

    override fun accept(visitor: SøkerVisitor) = tilstand.accept(visitor, this)

    private fun tilstand(nyTilstand: Tilstand) {
        this.tilstand = nyTilstand
    }

    private fun vurderErStudent(erStudent: Boolean) {
        this.erStudent = erStudent
        if (erStudent) tilstand(Tilstand.Oppfylt)
        else tilstand(Tilstand.IkkeOppfylt)
    }

    override fun håndterSøknad(søknad: Søknad, fødselsdato: Fødselsdato, vurderingsdato: LocalDate) {
        tilstand.håndterSøknad(this, søknad)
    }

    override fun erOppfylt() = tilstand.erOppfylt()
    override fun erIkkeOppfylt() = tilstand.erIkkeOppfylt()

    internal sealed class Tilstand(
        protected val tilstandsnavn: Tilstandsnavn,
        private val erOppfylt: Boolean,
        private val erIkkeOppfylt: Boolean
    ) {
        enum class Tilstandsnavn(internal val tilknyttetTilstand: () -> Tilstand) {
            IKKE_VURDERT({ IkkeVurdert }),
            OPPFYLT({ Oppfylt }),
            IKKE_OPPFYLT({ IkkeOppfylt }),
        }

        // fixme abstract
        fun accept(visitor: SøkerVisitor, paragraf: Paragraf_11_14) {
            visitor.`preVisit §11-14`()
            visitor.visitVilkårsvurdering(
                tilstandsnavn = tilstandsnavn.name,
                vilkårsvurderingsid = paragraf.vilkårsvurderingsid,
                paragraf = paragraf.paragraf,
                ledd = paragraf.ledd,
                måVurderesManuelt = false,
            )
            visitor.`postVisit §11-14`()
        }

        internal fun erOppfylt() = erOppfylt
        internal fun erIkkeOppfylt() = erIkkeOppfylt

        internal open fun håndterSøknad(
            vilkårsvurdering: Paragraf_11_14,
            søknad: Søknad
        ) {
            log.info("Vilkår allerede vurdert til oppfylt. Forventer ikke ny søknad")
        }

        object IkkeVurdert : Tilstand(
            tilstandsnavn = Tilstandsnavn.IKKE_VURDERT,
            erOppfylt = false,
            erIkkeOppfylt = false
        ) {
            override fun håndterSøknad(
                vilkårsvurdering: Paragraf_11_14,
                søknad: Søknad
            ) {
                vilkårsvurdering.vurderErStudent(søknad.erStudent())
            }
        }

        object Oppfylt : Tilstand(
            tilstandsnavn = Tilstandsnavn.OPPFYLT,
            erOppfylt = true,
            erIkkeOppfylt = false
        )

        object IkkeOppfylt : Tilstand(
            tilstandsnavn = Tilstandsnavn.IKKE_OPPFYLT,
            erOppfylt = false,
            erIkkeOppfylt = true
        )

        internal open fun gjenopprettTilstand(
            paragraf: Paragraf_11_14,
            vilkårsvurdering: DtoVilkårsvurdering
        ) {
        }

        internal fun toDto(paragraf: Paragraf_11_14): DtoVilkårsvurdering = DtoVilkårsvurdering(
            vilkårsvurderingsid = paragraf.vilkårsvurderingsid,
            paragraf = paragraf.paragraf.name,
            ledd = paragraf.ledd.map(Ledd::name),
            tilstand = tilstandsnavn.name,
            måVurderesManuelt = false
        )
    }

    override fun toDto(): DtoVilkårsvurdering = tilstand.toDto(this)

    internal companion object {
        internal fun gjenopprett(vilkårsvurdering: DtoVilkårsvurdering): Paragraf_11_14 =
            enumValueOf<Tilstand.Tilstandsnavn>(vilkårsvurdering.tilstand)
                .tilknyttetTilstand()
                .let { tilstand -> Paragraf_11_14(vilkårsvurdering.vilkårsvurderingsid, tilstand) }
                .apply { this.tilstand.gjenopprettTilstand(this, vilkårsvurdering) }
    }
}
