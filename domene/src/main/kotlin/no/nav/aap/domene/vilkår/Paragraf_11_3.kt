package no.nav.aap.domene.vilkår

import no.nav.aap.domene.UlovligTilstandException.Companion.ulovligTilstand
import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.dto.DtoVilkårsvurdering
import no.nav.aap.hendelse.Hendelse
import no.nav.aap.hendelse.LøsningParagraf_11_3
import no.nav.aap.hendelse.Søknad
import no.nav.aap.hendelse.behov.Behov_11_3
import no.nav.aap.visitor.SøkerVisitor
import org.slf4j.LoggerFactory
import java.time.LocalDate
import java.util.*

private val log = LoggerFactory.getLogger("Paragraf_11_3")

internal class Paragraf_11_3 private constructor(
    vilkårsvurderingsid: UUID,
    private var tilstand: Tilstand
) :
    Vilkårsvurdering(vilkårsvurderingsid, Paragraf.PARAGRAF_11_3, Ledd.LEDD_1 + Ledd.LEDD_2 + Ledd.LEDD_3) {
    private lateinit var løsning: LøsningParagraf_11_3

    internal constructor() : this(UUID.randomUUID(), Tilstand.IkkeVurdert)

    override fun accept(visitor: SøkerVisitor) = tilstand.accept(visitor, this)

    private fun tilstand(nyTilstand: Tilstand, hendelse: Hendelse) {
        this.tilstand.onExit(this, hendelse)
        this.tilstand = nyTilstand
        nyTilstand.onEntry(this, hendelse)
    }

    override fun håndterSøknad(søknad: Søknad, fødselsdato: Fødselsdato, vurderingsdato: LocalDate) {
        tilstand.håndterSøknad(this, søknad, fødselsdato, vurderingsdato)
    }

    override fun håndterLøsning(løsning: LøsningParagraf_11_3) {
        tilstand.håndterLøsning(this, løsning)
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
            SØKNAD_MOTTATT({ SøknadMottatt }),
            OPPFYLT({ Oppfylt }),
            IKKE_OPPFYLT({ IkkeOppfylt }),
        }

        abstract fun accept(visitor: SøkerVisitor, paragraf: Paragraf_11_3)

        internal open fun onEntry(vilkårsvurdering: Paragraf_11_3, hendelse: Hendelse) {}
        internal open fun onExit(vilkårsvurdering: Paragraf_11_3, hendelse: Hendelse) {}
        internal fun erOppfylt() = erOppfylt
        internal fun erIkkeOppfylt() = erIkkeOppfylt

        internal open fun håndterSøknad(
            vilkårsvurdering: Paragraf_11_3,
            søknad: Søknad,
            fødselsdato: Fødselsdato,
            vurderingsdato: LocalDate
        ) {
            log.info("Søknad skal ikke håndteres i tilstand $tilstandsnavn")
        }

        internal open fun håndterLøsning(
            vilkårsvurdering: Paragraf_11_3,
            løsning: LøsningParagraf_11_3
        ) {
            log.info("Oppgave skal ikke håndteres i tilstand $tilstandsnavn")
        }

        object IkkeVurdert : Tilstand(
            tilstandsnavn = Tilstandsnavn.IKKE_VURDERT,
            erOppfylt = false,
            erIkkeOppfylt = false
        ) {
            override fun accept(visitor: SøkerVisitor, paragraf: Paragraf_11_3) =
                ulovligTilstand("IkkeVurdert skal håndtere søknad før serialisering")

            override fun håndterSøknad(
                vilkårsvurdering: Paragraf_11_3,
                søknad: Søknad,
                fødselsdato: Fødselsdato,
                vurderingsdato: LocalDate
            ) {
                vilkårsvurdering.tilstand(SøknadMottatt, søknad)
            }

            override fun toDto(paragraf: Paragraf_11_3): DtoVilkårsvurdering =
                ulovligTilstand("IkkeVurdert skal håndtere søknad før serialisering")
        }

        object SøknadMottatt : Tilstand(
            tilstandsnavn = Tilstandsnavn.SØKNAD_MOTTATT,
            erOppfylt = false,
            erIkkeOppfylt = false
        ) {
            override fun accept(visitor: SøkerVisitor, paragraf: Paragraf_11_3) {
                visitor.`preVisit §11-3`()
                visitor.visitVilkårsvurdering(
                    tilstandsnavn = tilstandsnavn.name,
                    vilkårsvurderingsid = paragraf.vilkårsvurderingsid,
                    paragraf = paragraf.paragraf,
                    ledd = paragraf.ledd,
                    måVurderesManuelt = true,
                )
                visitor.`postVisit §11-3`()
            }

            override fun onEntry(vilkårsvurdering: Paragraf_11_3, hendelse: Hendelse) {
                hendelse.opprettBehov(Behov_11_3())
            }

            override fun håndterLøsning(
                vilkårsvurdering: Paragraf_11_3,
                løsning: LøsningParagraf_11_3
            ) {
                vilkårsvurdering.løsning = løsning
                if (løsning.erManueltOppfylt()) {
                    vilkårsvurdering.tilstand(Oppfylt, løsning)
                } else {
                    vilkårsvurdering.tilstand(IkkeOppfylt, løsning)
                }
            }

            override fun toDto(paragraf: Paragraf_11_3): DtoVilkårsvurdering = DtoVilkårsvurdering(
                vilkårsvurderingsid = paragraf.vilkårsvurderingsid,
                paragraf = paragraf.paragraf.name,
                ledd = paragraf.ledd.map(Ledd::name),
                tilstand = tilstandsnavn.name,
                måVurderesManuelt = true
            )
        }

        object Oppfylt : Tilstand(
            tilstandsnavn = Tilstandsnavn.OPPFYLT,
            erOppfylt = true,
            erIkkeOppfylt = false
        ) {
            override fun accept(visitor: SøkerVisitor, paragraf: Paragraf_11_3) {
                visitor.`preVisit §11-3`(manuellLøsning = paragraf.løsning)
                visitor.visitVilkårsvurdering(
                    tilstandsnavn = tilstandsnavn.name,
                    vilkårsvurderingsid = paragraf.vilkårsvurderingsid,
                    paragraf = paragraf.paragraf,
                    ledd = paragraf.ledd,
                    måVurderesManuelt = false,
                )
                visitor.`postVisit §11-3`(manuellLøsning = paragraf.løsning)
            }

            override fun toDto(paragraf: Paragraf_11_3): DtoVilkårsvurdering = DtoVilkårsvurdering(
                vilkårsvurderingsid = paragraf.vilkårsvurderingsid,
                paragraf = paragraf.paragraf.name,
                ledd = paragraf.ledd.map(Ledd::name),
                tilstand = tilstandsnavn.name,
                måVurderesManuelt = false,
                løsning_11_3_manuell = paragraf.løsning.toDto()
            )

            override fun gjenopprettTilstand(paragraf: Paragraf_11_3, vilkårsvurdering: DtoVilkårsvurdering) {
                val løsning113Manuell = requireNotNull(vilkårsvurdering.løsning_11_3_manuell)
                paragraf.løsning = LøsningParagraf_11_3(løsning113Manuell.erOppfylt)
            }
        }

        object IkkeOppfylt : Tilstand(
            tilstandsnavn = Tilstandsnavn.IKKE_OPPFYLT,
            erOppfylt = false,
            erIkkeOppfylt = true
        ) {
            override fun accept(visitor: SøkerVisitor, paragraf: Paragraf_11_3) {
                visitor.`preVisit §11-3`(manuellLøsning = paragraf.løsning)
                visitor.visitVilkårsvurdering(
                    tilstandsnavn = tilstandsnavn.name,
                    vilkårsvurderingsid = paragraf.vilkårsvurderingsid,
                    paragraf = paragraf.paragraf,
                    ledd = paragraf.ledd,
                    måVurderesManuelt = false,
                )
                visitor.`postVisit §11-3`(manuellLøsning = paragraf.løsning)
            }

            override fun toDto(paragraf: Paragraf_11_3): DtoVilkårsvurdering = DtoVilkårsvurdering(
                vilkårsvurderingsid = paragraf.vilkårsvurderingsid,
                paragraf = paragraf.paragraf.name,
                ledd = paragraf.ledd.map(Ledd::name),
                tilstand = tilstandsnavn.name,
                måVurderesManuelt = false,
                løsning_11_3_manuell = paragraf.løsning.toDto()
            )

            override fun gjenopprettTilstand(paragraf: Paragraf_11_3, vilkårsvurdering: DtoVilkårsvurdering) {
                val løsning113Manuell = requireNotNull(vilkårsvurdering.løsning_11_3_manuell)
                paragraf.løsning = LøsningParagraf_11_3(løsning113Manuell.erOppfylt)
            }
        }

        internal open fun gjenopprettTilstand(paragraf: Paragraf_11_3, vilkårsvurdering: DtoVilkårsvurdering) {}
        internal abstract fun toDto(paragraf: Paragraf_11_3): DtoVilkårsvurdering
    }

    override fun toDto(): DtoVilkårsvurdering = tilstand.toDto(this)

    internal companion object {
        internal fun gjenopprett(vilkårsvurdering: DtoVilkårsvurdering): Paragraf_11_3 =
            enumValueOf<Tilstand.Tilstandsnavn>(vilkårsvurdering.tilstand)
                .tilknyttetTilstand()
                .let { tilstand -> Paragraf_11_3(vilkårsvurdering.vilkårsvurderingsid, tilstand) }
                .apply { this.tilstand.gjenopprettTilstand(this, vilkårsvurdering) }
    }
}
