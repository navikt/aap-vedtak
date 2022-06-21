package no.nav.aap.domene.vilkår

import no.nav.aap.domene.UlovligTilstandException.Companion.ulovligTilstand
import no.nav.aap.domene.beregning.Beløp.Companion.beløp
import no.nav.aap.domene.beregning.Yrkesskade
import no.nav.aap.domene.entitet.Fødselsdato
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
    tilstand: Tilstand
) :
    Vilkårsvurdering<Paragraf_11_22, Paragraf_11_22.Tilstand>(
        vilkårsvurderingsid,
        Paragraf.PARAGRAF_11_22,
        Ledd.LEDD_1 + Ledd.LEDD_2,
        tilstand
    ) {
    private lateinit var løsning: LøsningParagraf_11_22

    internal constructor() : this(UUID.randomUUID(), Tilstand.IkkeVurdert)

    override fun håndterSøknad(søknad: Søknad, fødselsdato: Fødselsdato, vurderingsdato: LocalDate) {
        tilstand.håndterSøknad(this, søknad, fødselsdato, vurderingsdato)
    }

    override fun håndterLøsning(løsning: LøsningParagraf_11_22) {
        tilstand.håndterLøsning(this, løsning)
    }

    override fun onEntry(hendelse: Hendelse) {
        tilstand.onEntry(this, hendelse)
    }

    override fun onExit(hendelse: Hendelse) {
        tilstand.onExit(this, hendelse)
    }

    internal fun yrkesskade() = tilstand.yrkesskade(this)

    internal sealed class Tilstand(
        protected val tilstandsnavn: Tilstandsnavn,
        private val erOppfylt: Boolean,
        private val erIkkeOppfylt: Boolean
    ) : Vilkårsvurderingstilstand<Paragraf_11_22> {
        enum class Tilstandsnavn(internal val tilknyttetTilstand: () -> Tilstand) {
            IKKE_VURDERT({ IkkeVurdert }),
            SØKNAD_MOTTATT({ SøknadMottatt }),
            OPPFYLT({ Oppfylt }),
            IKKE_OPPFYLT({ IkkeOppfylt }),
        }

        override fun erOppfylt() = erOppfylt
        override fun erIkkeOppfylt() = erIkkeOppfylt

        internal open fun håndterSøknad(
            vilkårsvurdering: Paragraf_11_22,
            søknad: Søknad,
            fødselsdato: Fødselsdato,
            vurderingsdato: LocalDate
        ) {
            log.info("Søknad skal ikke håndteres i tilstand $tilstandsnavn")
        }

        internal open fun håndterLøsning(
            vilkårsvurdering: Paragraf_11_22,
            løsning: LøsningParagraf_11_22
        ) {
            log.info("Oppgave skal ikke håndteres i tilstand $tilstandsnavn")
        }

        internal open fun yrkesskade(paragraf1122: Paragraf_11_22): Yrkesskade {
            error("") //FIXME
        }

        object IkkeVurdert : Tilstand(
            tilstandsnavn = Tilstandsnavn.IKKE_VURDERT,
            erOppfylt = false,
            erIkkeOppfylt = false
        ) {
            override fun håndterSøknad(
                vilkårsvurdering: Paragraf_11_22,
                søknad: Søknad,
                fødselsdato: Fødselsdato,
                vurderingsdato: LocalDate
            ) {
                vilkårsvurdering.tilstand(SøknadMottatt, søknad)
            }

            override fun toDto(paragraf: Paragraf_11_22): DtoVilkårsvurdering =
                ulovligTilstand("IkkeVurdert skal håndtere søknad før serialisering")
        }

        object SøknadMottatt : Tilstand(
            tilstandsnavn = Tilstandsnavn.SØKNAD_MOTTATT,
            erOppfylt = false,
            erIkkeOppfylt = false
        ) {
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

            override fun toDto(paragraf: Paragraf_11_22): DtoVilkårsvurdering = DtoVilkårsvurdering(
                vilkårsvurderingsid = paragraf.vilkårsvurderingsid,
                vurdertAv = null,
                godkjentAv = null,
                paragraf = paragraf.paragraf.name,
                ledd = paragraf.ledd.map(Ledd::name),
                tilstand = tilstandsnavn.name,
                utfall = Utfall.IKKE_VURDERT
            )
        }

        object Oppfylt : Tilstand(
            tilstandsnavn = Tilstandsnavn.OPPFYLT,
            erOppfylt = true,
            erIkkeOppfylt = false
        ) {
            override fun yrkesskade(paragraf1122: Paragraf_11_22) = paragraf1122.løsning.yrkesskade()

            override fun toDto(paragraf: Paragraf_11_22): DtoVilkårsvurdering = DtoVilkårsvurdering(
                vilkårsvurderingsid = paragraf.vilkårsvurderingsid,
                vurdertAv = paragraf.løsning.vurdertAv(),
                godkjentAv = null,
                paragraf = paragraf.paragraf.name,
                ledd = paragraf.ledd.map(Ledd::name),
                tilstand = tilstandsnavn.name,
                utfall = Utfall.OPPFYLT,
                løsning_11_22_manuell = paragraf.løsning.toDto()
            )

            override fun gjenopprettTilstand(paragraf: Paragraf_11_22, vilkårsvurdering: DtoVilkårsvurdering) {
                val vurdertAv = requireNotNull(vilkårsvurdering.vurdertAv)
                val løsning = requireNotNull(vilkårsvurdering.løsning_11_22_manuell)
                paragraf.løsning = LøsningParagraf_11_22(
                    vurdertAv = vurdertAv,
                    erOppfylt = løsning.erOppfylt,
                    andelNedsattArbeidsevne = løsning.andelNedsattArbeidsevne,
                    år = løsning.år,
                    antattÅrligArbeidsinntekt = løsning.antattÅrligArbeidsinntekt.beløp
                )
            }
        }

        object IkkeOppfylt : Tilstand(
            tilstandsnavn = Tilstandsnavn.IKKE_OPPFYLT,
            erOppfylt = false,
            erIkkeOppfylt = true
        ) {
            override fun toDto(paragraf: Paragraf_11_22): DtoVilkårsvurdering = DtoVilkårsvurdering(
                vilkårsvurderingsid = paragraf.vilkårsvurderingsid,
                vurdertAv = paragraf.løsning.vurdertAv(),
                godkjentAv = null,
                paragraf = paragraf.paragraf.name,
                ledd = paragraf.ledd.map(Ledd::name),
                tilstand = tilstandsnavn.name,
                utfall = Utfall.IKKE_OPPFYLT,
                løsning_11_22_manuell = paragraf.løsning.toDto()
            )

            override fun gjenopprettTilstand(paragraf: Paragraf_11_22, vilkårsvurdering: DtoVilkårsvurdering) {
                val vurdertAv = requireNotNull(vilkårsvurdering.vurdertAv)
                val løsning = requireNotNull(vilkårsvurdering.løsning_11_22_manuell)
                paragraf.løsning = LøsningParagraf_11_22(
                    vurdertAv = vurdertAv,
                    erOppfylt = løsning.erOppfylt,
                    andelNedsattArbeidsevne = løsning.andelNedsattArbeidsevne,
                    år = løsning.år,
                    antattÅrligArbeidsinntekt = løsning.antattÅrligArbeidsinntekt.beløp
                )
            }
        }
    }

    override fun toDto(): DtoVilkårsvurdering = tilstand.toDto(this)

    internal companion object {
        internal fun gjenopprett(vilkårsvurdering: DtoVilkårsvurdering): Paragraf_11_22 =
            enumValueOf<Tilstand.Tilstandsnavn>(vilkårsvurdering.tilstand)
                .tilknyttetTilstand()
                .let { tilstand -> Paragraf_11_22(vilkårsvurdering.vilkårsvurderingsid, tilstand) }
                .apply { this.tilstand.gjenopprettTilstand(this, vilkårsvurdering) }
    }
}
