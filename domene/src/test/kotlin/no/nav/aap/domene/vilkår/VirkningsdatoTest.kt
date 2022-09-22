package no.nav.aap.domene.vilkår

import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.domene.entitet.Personident
import no.nav.aap.domene.vilkår.Vilkårsvurdering.Companion.toDto
import no.nav.aap.hendelse.LøsningParagraf_11_27_FørsteLedd
import no.nav.aap.hendelse.LøsningParagraf_22_13
import no.nav.aap.hendelse.LøsningSykepengedager
import no.nav.aap.hendelse.Søknad
import no.nav.aap.hendelse.entitet.Periode
import no.nav.aap.modellapi.Utfall
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

internal class VirkningsdatoTest {

    @Test
    fun `Hvis virkningsdato skal bestemmes etter søknadstidspunkt er kun 22-13 relevant`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår8_48 = Paragraf_8_48()
        val vilkår11_27 = Paragraf_11_27_FørsteLedd()
        val vilkår22_13 = Paragraf_22_13()

        val søknad = Søknad(personident, fødselsdato)
        vilkår8_48.håndterSøknad(søknad, fødselsdato, LocalDate.now())
        vilkår11_27.håndterSøknad(søknad, fødselsdato, LocalDate.now())
        vilkår22_13.håndterSøknad(søknad, fødselsdato, LocalDate.now())

        val løsning8_48 = LøsningSykepengedager(
            sykepengedager = LøsningSykepengedager.Sykepengedager.Har(
                gjenståendeSykedager = 10,
                foreløpigBeregnetSluttPåSykepenger = LocalDate.now(),
                kilde = LøsningSykepengedager.Kilde.SPLEIS
            )
        )
        vilkår8_48.håndterLøsning(løsning8_48)

        val løsning11_27 = LøsningParagraf_11_27_FørsteLedd(
            løsningId = UUID.randomUUID(),
            svangerskapspenger = LøsningParagraf_11_27_FørsteLedd.Svangerskapspenger(
                Periode(LocalDate.now(), LocalDate.now()), 100.0, LocalDate.now()
            )
        )
        vilkår11_27.håndterLøsning(løsning11_27)

        assertTilstand(Vilkårsvurdering.Tilstand.Tilstandsnavn.MANUELL_VURDERING_TRENGS, vilkår8_48)
        assertTilstand(Vilkårsvurdering.Tilstand.Tilstandsnavn.MANUELL_VURDERING_TRENGS, vilkår11_27)
        assertTilstand(Vilkårsvurdering.Tilstand.Tilstandsnavn.SØKNAD_MOTTATT, vilkår22_13)

        val løsning22_13 = LøsningParagraf_22_13(
            løsningId = UUID.randomUUID(),
            vurdertAv = "saksbehandler",
            tidspunktForVurdering = LocalDateTime.now(),
            bestemmesAv = LøsningParagraf_22_13.BestemmesAv.soknadstidspunkt,
            unntaksbegrunnelse = "",
            unntak = "",
            manueltSattVirkningsdato = null,
        )
        vilkår8_48.håndterLøsning(løsning22_13)
        vilkår11_27.håndterLøsning(løsning22_13)
        vilkår22_13.håndterLøsning(løsning22_13)

        assertUtfall(Utfall.IKKE_RELEVANT, vilkår8_48)
        assertUtfall(Utfall.IKKE_RELEVANT, vilkår11_27)
        assertUtfall(Utfall.OPPFYLT, vilkår22_13)

        assertTilstand(Vilkårsvurdering.Tilstand.Tilstandsnavn.IKKE_RELEVANT, vilkår8_48)
        assertTilstand(Vilkårsvurdering.Tilstand.Tilstandsnavn.IKKE_RELEVANT, vilkår11_27)
        assertTilstand(Vilkårsvurdering.Tilstand.Tilstandsnavn.OPPFYLT_MANUELT, vilkår22_13)
    }

    @Test
    fun `Hvis virkningsdato skal bestemmes etter maksdato på sykepenger er kun 8-48 relevant`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår8_48 = Paragraf_8_48()
        val vilkår11_27 = Paragraf_11_27_FørsteLedd()
        val vilkår22_13 = Paragraf_22_13()

        val søknad = Søknad(personident, fødselsdato)
        vilkår8_48.håndterSøknad(søknad, fødselsdato, LocalDate.now())
        vilkår11_27.håndterSøknad(søknad, fødselsdato, LocalDate.now())
        vilkår22_13.håndterSøknad(søknad, fødselsdato, LocalDate.now())

        val løsning8_48 = LøsningSykepengedager(
            sykepengedager = LøsningSykepengedager.Sykepengedager.Har(
                gjenståendeSykedager = 10,
                foreløpigBeregnetSluttPåSykepenger = LocalDate.now(),
                kilde = LøsningSykepengedager.Kilde.SPLEIS
            )
        )
        vilkår8_48.håndterLøsning(løsning8_48)

        val løsning11_27 = LøsningParagraf_11_27_FørsteLedd(
            løsningId = UUID.randomUUID(),
            svangerskapspenger = LøsningParagraf_11_27_FørsteLedd.Svangerskapspenger(
                Periode(LocalDate.now(), LocalDate.now()), 100.0, LocalDate.now()
            )
        )
        vilkår11_27.håndterLøsning(løsning11_27)

        assertTilstand(Vilkårsvurdering.Tilstand.Tilstandsnavn.MANUELL_VURDERING_TRENGS, vilkår8_48)
        assertTilstand(Vilkårsvurdering.Tilstand.Tilstandsnavn.MANUELL_VURDERING_TRENGS, vilkår11_27)
        assertTilstand(Vilkårsvurdering.Tilstand.Tilstandsnavn.SØKNAD_MOTTATT, vilkår22_13)

        val løsning22_13 = LøsningParagraf_22_13(
            løsningId = UUID.randomUUID(),
            vurdertAv = "saksbehandler",
            tidspunktForVurdering = LocalDateTime.now(),
            bestemmesAv = LøsningParagraf_22_13.BestemmesAv.maksdatoSykepenger,
            unntaksbegrunnelse = "",
            unntak = "",
            manueltSattVirkningsdato = null,
        )
        vilkår8_48.håndterLøsning(løsning22_13)
        vilkår11_27.håndterLøsning(løsning22_13)
        vilkår22_13.håndterLøsning(løsning22_13)

        assertUtfall(Utfall.OPPFYLT, vilkår8_48)
        assertUtfall(Utfall.IKKE_RELEVANT, vilkår11_27)
        assertUtfall(Utfall.IKKE_RELEVANT, vilkår22_13)

        assertTilstand(Vilkårsvurdering.Tilstand.Tilstandsnavn.OPPFYLT_MANUELT, vilkår8_48)
        assertTilstand(Vilkårsvurdering.Tilstand.Tilstandsnavn.IKKE_RELEVANT, vilkår11_27)
        assertTilstand(Vilkårsvurdering.Tilstand.Tilstandsnavn.IKKE_RELEVANT, vilkår22_13)
    }

    @Test
    fun `Hvis virkningsdato skal bestemmes etter svangerskapspenger er kun 11-27 relevant`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår8_48 = Paragraf_8_48()
        val vilkår11_27 = Paragraf_11_27_FørsteLedd()
        val vilkår22_13 = Paragraf_22_13()

        val søknad = Søknad(personident, fødselsdato)
        vilkår8_48.håndterSøknad(søknad, fødselsdato, LocalDate.now())
        vilkår11_27.håndterSøknad(søknad, fødselsdato, LocalDate.now())
        vilkår22_13.håndterSøknad(søknad, fødselsdato, LocalDate.now())

        val løsning8_48 = LøsningSykepengedager(
            sykepengedager = LøsningSykepengedager.Sykepengedager.Har(
                gjenståendeSykedager = 10,
                foreløpigBeregnetSluttPåSykepenger = LocalDate.now(),
                kilde = LøsningSykepengedager.Kilde.SPLEIS
            )
        )
        vilkår8_48.håndterLøsning(løsning8_48)

        val løsning11_27 = LøsningParagraf_11_27_FørsteLedd(
            løsningId = UUID.randomUUID(),
            svangerskapspenger = LøsningParagraf_11_27_FørsteLedd.Svangerskapspenger(
                Periode(LocalDate.now(), LocalDate.now()), 100.0, LocalDate.now()
            )
        )
        vilkår11_27.håndterLøsning(løsning11_27)

        assertTilstand(Vilkårsvurdering.Tilstand.Tilstandsnavn.MANUELL_VURDERING_TRENGS, vilkår8_48)
        assertTilstand(Vilkårsvurdering.Tilstand.Tilstandsnavn.MANUELL_VURDERING_TRENGS, vilkår11_27)
        assertTilstand(Vilkårsvurdering.Tilstand.Tilstandsnavn.SØKNAD_MOTTATT, vilkår22_13)

        val løsning22_13 = LøsningParagraf_22_13(
            løsningId = UUID.randomUUID(),
            vurdertAv = "saksbehandler",
            tidspunktForVurdering = LocalDateTime.now(),
            bestemmesAv = LøsningParagraf_22_13.BestemmesAv.svangerskapspenger,
            unntaksbegrunnelse = "",
            unntak = "",
            manueltSattVirkningsdato = null,
        )
        vilkår8_48.håndterLøsning(løsning22_13)
        vilkår11_27.håndterLøsning(løsning22_13)
        vilkår22_13.håndterLøsning(løsning22_13)

        assertUtfall(Utfall.IKKE_RELEVANT, vilkår8_48)
        assertUtfall(Utfall.OPPFYLT, vilkår11_27)
        assertUtfall(Utfall.IKKE_RELEVANT, vilkår22_13)

        assertTilstand(Vilkårsvurdering.Tilstand.Tilstandsnavn.IKKE_RELEVANT, vilkår8_48)
        assertTilstand(Vilkårsvurdering.Tilstand.Tilstandsnavn.OPPFYLT_MANUELT, vilkår11_27)
        assertTilstand(Vilkårsvurdering.Tilstand.Tilstandsnavn.IKKE_RELEVANT, vilkår22_13)
    }

    private fun assertUtfall(utfall: Utfall, vilkårsvurdering: Vilkårsvurdering<*>) {
        assertEquals(utfall, listOf(vilkårsvurdering).toDto().first().utfall)
    }

    private fun assertTilstand(
        tilstand: Vilkårsvurdering.Tilstand.Tilstandsnavn,
        vilkårsvurdering: Vilkårsvurdering<*>
    ) {
        assertEquals(tilstand.name, listOf(vilkårsvurdering).toDto().first().tilstand)
    }
}
