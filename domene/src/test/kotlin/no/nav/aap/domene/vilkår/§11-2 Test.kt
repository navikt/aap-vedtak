package no.nav.aap.domene.vilkår

import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.domene.entitet.Personident
import no.nav.aap.domene.vilkår.Vilkårsvurdering.Companion.toDto
import no.nav.aap.dto.Utfall
import no.nav.aap.hendelse.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime

internal class `§11-2 Test` {
    @Test
    fun `Hvis søker er medlem, er vilkår for medlemskap oppfylt`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = Paragraf_11_2()

        val søknad = Søknad(personident, fødselsdato)
        vilkår.håndterSøknad(søknad, fødselsdato, LocalDate.now())
        assertHarBehov(søknad)
        assertUtfall(Utfall.IKKE_VURDERT, vilkår)

        val løsning = LøsningMaskinellParagraf_11_2(LøsningMaskinellParagraf_11_2.ErMedlem.JA)
        vilkår.håndterLøsning(løsning)
        assertHarIkkeBehov(løsning)
        assertUtfall(Utfall.OPPFYLT, vilkår)

        assertTrue(vilkår.erOppfylt())
        assertFalse(vilkår.erIkkeOppfylt())
    }

    @Test
    fun `Hvis søker ikke er medlem, er vilkår for medlemskap ikke oppfylt`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = Paragraf_11_2()

        val søknad = Søknad(personident, fødselsdato)
        vilkår.håndterSøknad(søknad, fødselsdato, LocalDate.now())
        assertHarBehov(søknad)
        assertUtfall(Utfall.IKKE_VURDERT, vilkår)

        val løsning = LøsningMaskinellParagraf_11_2(LøsningMaskinellParagraf_11_2.ErMedlem.NEI)
        vilkår.håndterLøsning(løsning)
        assertHarIkkeBehov(løsning)
        assertUtfall(Utfall.IKKE_OPPFYLT, vilkår)

        assertFalse(vilkår.erOppfylt())
        assertTrue(vilkår.erIkkeOppfylt())
    }

    @Test
    fun `Hvis vi ikke vet om søker er medlem, er vilkår for medlemskap ikke vurdert ferdig`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = Paragraf_11_2()

        val søknad = Søknad(personident, fødselsdato)
        vilkår.håndterSøknad(søknad, fødselsdato, LocalDate.now())
        assertHarBehov(søknad)
        assertUtfall(Utfall.IKKE_VURDERT, vilkår)

        val maskinellLøsning = LøsningMaskinellParagraf_11_2(LøsningMaskinellParagraf_11_2.ErMedlem.UAVKLART)
        vilkår.håndterLøsning(maskinellLøsning)
        assertHarIkkeBehov(maskinellLøsning)
        assertUtfall(Utfall.IKKE_VURDERT, vilkår)

        assertFalse(vilkår.erOppfylt())
        assertFalse(vilkår.erIkkeOppfylt())
    }

    @Test
    fun `Hvis søker er medlem etter manuell vurdering, er vilkår for medlemskap oppfylt`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = Paragraf_11_2()

        val søknad = Søknad(personident, fødselsdato)
        vilkår.håndterSøknad(søknad, fødselsdato, LocalDate.now())
        assertHarBehov(søknad)

        val maskinellLøsning = LøsningMaskinellParagraf_11_2(LøsningMaskinellParagraf_11_2.ErMedlem.UAVKLART)
        vilkår.håndterLøsning(maskinellLøsning)
        assertHarIkkeBehov(maskinellLøsning)
        assertUtfall(Utfall.IKKE_VURDERT, vilkår)

        val manuellLøsning =
            LøsningManuellParagraf_11_2("saksbehandler", LocalDateTime.now(), LøsningManuellParagraf_11_2.ErMedlem.JA)
        vilkår.håndterLøsning(manuellLøsning)
        assertHarIkkeBehov(manuellLøsning)
        assertUtfall(Utfall.OPPFYLT, vilkår)

        assertTrue(vilkår.erOppfylt())
        assertFalse(vilkår.erIkkeOppfylt())
    }

    @Test
    fun `Hvis søker ikke er medlem etter manuell vurdering, er vilkår for medlemskap ikke oppfylt`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = Paragraf_11_2()

        val søknad = Søknad(personident, fødselsdato)
        vilkår.håndterSøknad(søknad, fødselsdato, LocalDate.now())
        assertHarBehov(søknad)

        val maskinellLøsning = LøsningMaskinellParagraf_11_2(LøsningMaskinellParagraf_11_2.ErMedlem.UAVKLART)
        vilkår.håndterLøsning(maskinellLøsning)
        assertHarIkkeBehov(maskinellLøsning)
        assertUtfall(Utfall.IKKE_VURDERT, vilkår)

        val manuellLøsning =
            LøsningManuellParagraf_11_2("saksbehandler", LocalDateTime.now(), LøsningManuellParagraf_11_2.ErMedlem.NEI)
        vilkår.håndterLøsning(manuellLøsning)
        assertHarIkkeBehov(manuellLøsning)
        assertUtfall(Utfall.IKKE_OPPFYLT, vilkår)

        assertFalse(vilkår.erOppfylt())
        assertTrue(vilkår.erIkkeOppfylt())
    }

    @Test
    fun `Hvis søknad ikke er håndtert, er vilkåret hverken oppfylt eller ikke-oppfylt`() {
        val vilkår = Paragraf_11_2()

        assertFalse(vilkår.erOppfylt())
        assertFalse(vilkår.erIkkeOppfylt())
    }

    @Test
    fun `Hvis søknad er håndtert, men ikke løsning, er vilkåret hverken oppfylt eller ikke-oppfylt`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = Paragraf_11_2()

        val søknad = Søknad(personident, fødselsdato)
        vilkår.håndterSøknad(søknad, fødselsdato, LocalDate.now())
        assertHarBehov(søknad)
        assertUtfall(Utfall.IKKE_VURDERT, vilkår)

        assertFalse(vilkår.erOppfylt())
        assertFalse(vilkår.erIkkeOppfylt())
    }

    @Test
    fun `Hvis tilstand oppfylt maskinelt blir godkjent av kvalitetssikrer, blir tilstand satt til oppfylt maskinelt kvalitetssikret`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = Paragraf_11_2()

        val søknad = Søknad(personident, fødselsdato)
        vilkår.håndterSøknad(søknad, fødselsdato, LocalDate.now())

        val maskinellLøsning = LøsningMaskinellParagraf_11_2(LøsningMaskinellParagraf_11_2.ErMedlem.JA)
        vilkår.håndterLøsning(maskinellLøsning)

        val kvalitetssikring = KvalitetssikringParagraf_11_2("X", LocalDateTime.now(), true, "JA")
        vilkår.håndterKvalitetssikring(kvalitetssikring)

        assertUtfall(Utfall.OPPFYLT, vilkår)
        assertKvalitetssikretAv("X", vilkår)
        assertTilstand(Vilkårsvurdering.Tilstand.Tilstandsnavn.OPPFYLT_MASKINELT_KVALITETSSIKRET, vilkår)
    }

    @Test
    fun `Hvis tilstand ikke oppfylt maskinelt blir godkjent av kvalitetssikrer, blir tilstand satt til ikke oppfylt maskinelt kvalitetssikret`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = Paragraf_11_2()

        val søknad = Søknad(personident, fødselsdato)
        vilkår.håndterSøknad(søknad, fødselsdato, LocalDate.now())

        val maskinellLøsning = LøsningMaskinellParagraf_11_2(LøsningMaskinellParagraf_11_2.ErMedlem.NEI)
        vilkår.håndterLøsning(maskinellLøsning)

        val kvalitetssikring = KvalitetssikringParagraf_11_2("X", LocalDateTime.now(), true, "JA")
        vilkår.håndterKvalitetssikring(kvalitetssikring)

        assertUtfall(Utfall.IKKE_OPPFYLT, vilkår)
        assertKvalitetssikretAv("X", vilkår)
        assertTilstand(Vilkårsvurdering.Tilstand.Tilstandsnavn.IKKE_OPPFYLT_MASKINELT_KVALITETSSIKRET, vilkår)
    }

    @Test
    fun `Hvis tilstand oppfylt manuelt blir godkjent av kvalitetssikrer, blir tilstand satt til oppfylt manuelt kvalitetssikret`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = Paragraf_11_2()

        val søknad = Søknad(personident, fødselsdato)
        vilkår.håndterSøknad(søknad, fødselsdato, LocalDate.now())

        val maskinellLøsning = LøsningMaskinellParagraf_11_2(LøsningMaskinellParagraf_11_2.ErMedlem.UAVKLART)
        vilkår.håndterLøsning(maskinellLøsning)

        val manuellLøsning =
            LøsningManuellParagraf_11_2("Y", LocalDateTime.now(), LøsningManuellParagraf_11_2.ErMedlem.JA)
        vilkår.håndterLøsning(manuellLøsning)

        val kvalitetssikring = KvalitetssikringParagraf_11_2("X", LocalDateTime.now(), true, "JA")
        vilkår.håndterKvalitetssikring(kvalitetssikring)

        assertUtfall(Utfall.OPPFYLT, vilkår)
        assertKvalitetssikretAv("X", vilkår)
        assertTilstand(Vilkårsvurdering.Tilstand.Tilstandsnavn.OPPFYLT_MANUELT_KVALITETSSIKRET, vilkår)
    }

    @Test
    fun `Hvis tilstand ikke oppfylt manuelt blir godkjent av kvalitetssikrer, blir tilstand satt til ikke oppfylt manuelt kvalitetssikret`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = Paragraf_11_2()

        val søknad = Søknad(personident, fødselsdato)
        vilkår.håndterSøknad(søknad, fødselsdato, LocalDate.now())

        val maskinellLøsning = LøsningMaskinellParagraf_11_2(LøsningMaskinellParagraf_11_2.ErMedlem.UAVKLART)
        vilkår.håndterLøsning(maskinellLøsning)

        val manuellLøsning =
            LøsningManuellParagraf_11_2("Y", LocalDateTime.now(), LøsningManuellParagraf_11_2.ErMedlem.NEI)
        vilkår.håndterLøsning(manuellLøsning)

        val kvalitetssikring = KvalitetssikringParagraf_11_2("X", LocalDateTime.now(), true, "JA")
        vilkår.håndterKvalitetssikring(kvalitetssikring)

        assertUtfall(Utfall.IKKE_OPPFYLT, vilkår)
        assertKvalitetssikretAv("X", vilkår)
        assertTilstand(Vilkårsvurdering.Tilstand.Tilstandsnavn.IKKE_OPPFYLT_MANUELT_KVALITETSSIKRET, vilkår)
    }

    @Test
    fun `Hvis tilstand oppfylt maskinelt ikke blir godkjent av kvalitetssikrer, blir tilstand satt til manuell vurdering trengs`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = Paragraf_11_2()

        val søknad = Søknad(personident, fødselsdato)
        vilkår.håndterSøknad(søknad, fødselsdato, LocalDate.now())

        val maskinellLøsning = LøsningMaskinellParagraf_11_2(LøsningMaskinellParagraf_11_2.ErMedlem.JA)
        vilkår.håndterLøsning(maskinellLøsning)

        val kvalitetssikring = KvalitetssikringParagraf_11_2("X", LocalDateTime.now(), false, "JA")
        vilkår.håndterKvalitetssikring(kvalitetssikring)

        assertUtfall(Utfall.IKKE_VURDERT, vilkår)
        assertIkkeKvalitetssikret(vilkår)
        assertTilstand(Vilkårsvurdering.Tilstand.Tilstandsnavn.MANUELL_VURDERING_TRENGS, vilkår)
    }

    @Test
    fun `Hvis tilstand ikke oppfylt maskinelt ikke blir godkjent av kvalitetssikrer, blir tilstand satt til manuell vurdering trengs`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = Paragraf_11_2()

        val søknad = Søknad(personident, fødselsdato)
        vilkår.håndterSøknad(søknad, fødselsdato, LocalDate.now())

        val maskinellLøsning = LøsningMaskinellParagraf_11_2(LøsningMaskinellParagraf_11_2.ErMedlem.NEI)
        vilkår.håndterLøsning(maskinellLøsning)

        val kvalitetssikring = KvalitetssikringParagraf_11_2("X", LocalDateTime.now(), false, "JA")
        vilkår.håndterKvalitetssikring(kvalitetssikring)

        assertUtfall(Utfall.IKKE_VURDERT, vilkår)
        assertIkkeKvalitetssikret(vilkår)
        assertTilstand(Vilkårsvurdering.Tilstand.Tilstandsnavn.MANUELL_VURDERING_TRENGS, vilkår)
    }

    @Test
    fun `Hvis tilstand oppfylt manuelt ikke blir godkjent av kvalitetssikrer, blir tilstand satt til manuell vurdering trengs`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = Paragraf_11_2()

        val søknad = Søknad(personident, fødselsdato)
        vilkår.håndterSøknad(søknad, fødselsdato, LocalDate.now())

        val maskinellLøsning = LøsningMaskinellParagraf_11_2(LøsningMaskinellParagraf_11_2.ErMedlem.UAVKLART)
        vilkår.håndterLøsning(maskinellLøsning)

        val manuellLøsning =
            LøsningManuellParagraf_11_2("Y", LocalDateTime.now(), LøsningManuellParagraf_11_2.ErMedlem.JA)
        vilkår.håndterLøsning(manuellLøsning)

        val kvalitetssikring = KvalitetssikringParagraf_11_2("X", LocalDateTime.now(), false, "JA")
        vilkår.håndterKvalitetssikring(kvalitetssikring)

        assertUtfall(Utfall.IKKE_VURDERT, vilkår)
        assertIkkeKvalitetssikret(vilkår)
        assertTilstand(Vilkårsvurdering.Tilstand.Tilstandsnavn.MANUELL_VURDERING_TRENGS, vilkår)
    }

    @Test
    fun `Hvis tilstand ikke oppfylt manuelt ikke blir godkjent av kvalitetssikrer, blir tilstand satt til manuell vurdering trengs`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = Paragraf_11_2()

        val søknad = Søknad(personident, fødselsdato)
        vilkår.håndterSøknad(søknad, fødselsdato, LocalDate.now())

        val maskinellLøsning = LøsningMaskinellParagraf_11_2(LøsningMaskinellParagraf_11_2.ErMedlem.UAVKLART)
        vilkår.håndterLøsning(maskinellLøsning)

        val manuellLøsning =
            LøsningManuellParagraf_11_2("Y", LocalDateTime.now(), LøsningManuellParagraf_11_2.ErMedlem.NEI)
        vilkår.håndterLøsning(manuellLøsning)

        val kvalitetssikring = KvalitetssikringParagraf_11_2("X", LocalDateTime.now(), false, "JA")
        vilkår.håndterKvalitetssikring(kvalitetssikring)

        assertUtfall(Utfall.IKKE_VURDERT, vilkår)
        assertIkkeKvalitetssikret(vilkår)
        assertTilstand(Vilkårsvurdering.Tilstand.Tilstandsnavn.MANUELL_VURDERING_TRENGS, vilkår)
    }

    private fun assertHarBehov(hendelse: Hendelse) {
        assertTrue(hendelse.behov().isNotEmpty())
    }

    private fun assertHarIkkeBehov(hendelse: Hendelse) {
        assertTrue(hendelse.behov().isEmpty())
    }

    private fun assertUtfall(utfall: Utfall, vilkårsvurdering: Paragraf_11_2) {
        assertEquals(utfall, listOf(vilkårsvurdering).toDto().first().utfall)
    }

    private fun assertTilstand(tilstand: Vilkårsvurdering.Tilstand.Tilstandsnavn, vilkårsvurdering: Paragraf_11_2) {
        assertEquals(tilstand.name, listOf(vilkårsvurdering).toDto().first().tilstand)
    }

    private fun assertKvalitetssikretAv(kvalitetssikretAv: String, vilkårsvurdering: Paragraf_11_2) {
        assertEquals(kvalitetssikretAv, listOf(vilkårsvurdering).toDto().first().kvalitetssikretAv)
    }

    private fun assertIkkeKvalitetssikret(vilkårsvurdering: Paragraf_11_2) {
        assertNull(listOf(vilkårsvurdering).toDto().first().kvalitetssikretAv?.takeIf { it.isNotEmpty() })
    }
}
