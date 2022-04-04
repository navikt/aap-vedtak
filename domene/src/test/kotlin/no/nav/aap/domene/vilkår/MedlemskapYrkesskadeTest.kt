package no.nav.aap.domene.vilkår

import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.domene.entitet.Personident
import no.nav.aap.domene.vilkår.Vilkårsvurdering.Companion.toDto
import no.nav.aap.hendelse.*
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.LocalDate

internal class MedlemskapYrkesskadeTest {
    @Test
    fun `Hvis søker er medlem, er vilkår for medlemskap oppfylt`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = MedlemskapYrkesskade()

        val søknad = Søknad(personident, fødselsdato)
        vilkår.håndterSøknad(søknad, fødselsdato, LocalDate.now())
        assertHarBehov(søknad)
        assertSkalIkkeVurdersManuelt(vilkår)

        val løsning = LøsningMaskinellMedlemskapYrkesskade(LøsningMaskinellMedlemskapYrkesskade.ErMedlem.JA)
        vilkår.håndterLøsning(løsning)
        assertHarIkkeBehov(løsning)
        assertSkalIkkeVurdersManuelt(vilkår)

        assertTrue(vilkår.erOppfylt())
        assertFalse(vilkår.erIkkeOppfylt())
    }

    @Test
    fun `Hvis søker ikke er medlem, er vilkår for medlemskap ikke oppfylt`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = MedlemskapYrkesskade()

        val søknad = Søknad(personident, fødselsdato)
        vilkår.håndterSøknad(søknad, fødselsdato, LocalDate.now())
        assertHarBehov(søknad)
        assertSkalIkkeVurdersManuelt(vilkår)

        val løsning = LøsningMaskinellMedlemskapYrkesskade(LøsningMaskinellMedlemskapYrkesskade.ErMedlem.NEI)
        vilkår.håndterLøsning(løsning)
        assertHarIkkeBehov(løsning)
        assertSkalIkkeVurdersManuelt(vilkår)

        assertFalse(vilkår.erOppfylt())
        assertTrue(vilkår.erIkkeOppfylt())
    }

    @Test
    fun `Hvis vi ikke vet om søker er medlem, er vilkår for medlemskap ikke vurdert ferdig`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = MedlemskapYrkesskade()

        val søknad = Søknad(personident, fødselsdato)
        vilkår.håndterSøknad(søknad, fødselsdato, LocalDate.now())
        assertHarBehov(søknad)
        assertSkalIkkeVurdersManuelt(vilkår)

        val maskinellLøsning = LøsningMaskinellMedlemskapYrkesskade(LøsningMaskinellMedlemskapYrkesskade.ErMedlem.UAVKLART)
        vilkår.håndterLøsning(maskinellLøsning)
        assertHarIkkeBehov(maskinellLøsning)
        assertMåVurderesManuelt(vilkår)

        assertFalse(vilkår.erOppfylt())
        assertFalse(vilkår.erIkkeOppfylt())
    }

    @Test
    fun `Hvis søker er medlem etter manuell vurdering, er vilkår for medlemskap oppfylt`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = MedlemskapYrkesskade()

        val søknad = Søknad(personident, fødselsdato)
        vilkår.håndterSøknad(søknad, fødselsdato, LocalDate.now())
        assertHarBehov(søknad)

        val maskinellLøsning = LøsningMaskinellMedlemskapYrkesskade(LøsningMaskinellMedlemskapYrkesskade.ErMedlem.UAVKLART)
        vilkår.håndterLøsning(maskinellLøsning)
        assertHarIkkeBehov(maskinellLøsning)
        assertMåVurderesManuelt(vilkår)

        val manuellLøsning = LøsningManuellMedlemskapYrkesskade(LøsningManuellMedlemskapYrkesskade.ErMedlem.JA)
        vilkår.håndterLøsning(manuellLøsning)
        assertHarIkkeBehov(manuellLøsning)
        assertSkalIkkeVurdersManuelt(vilkår)

        assertTrue(vilkår.erOppfylt())
        assertFalse(vilkår.erIkkeOppfylt())
    }

    @Test
    fun `Hvis søker ikke er medlem etter manuell vurdering, er vilkår for medlemskap ikke oppfylt`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = MedlemskapYrkesskade()

        val søknad = Søknad(personident, fødselsdato)
        vilkår.håndterSøknad(søknad, fødselsdato, LocalDate.now())
        assertHarBehov(søknad)

        val maskinellLøsning = LøsningMaskinellMedlemskapYrkesskade(LøsningMaskinellMedlemskapYrkesskade.ErMedlem.UAVKLART)
        vilkår.håndterLøsning(maskinellLøsning)
        assertHarIkkeBehov(maskinellLøsning)
        assertMåVurderesManuelt(vilkår)

        val manuellLøsning = LøsningManuellMedlemskapYrkesskade(LøsningManuellMedlemskapYrkesskade.ErMedlem.NEI)
        vilkår.håndterLøsning(manuellLøsning)
        assertHarIkkeBehov(manuellLøsning)
        assertSkalIkkeVurdersManuelt(vilkår)

        assertFalse(vilkår.erOppfylt())
        assertTrue(vilkår.erIkkeOppfylt())
    }

    @Test
    fun `Hvis søknad ikke er håndtert, er vilkåret hverken oppfylt eller ikke-oppfylt`() {
        val vilkår = MedlemskapYrkesskade()

        assertFalse(vilkår.erOppfylt())
        assertFalse(vilkår.erIkkeOppfylt())
    }

    @Test
    fun `Hvis søknad er håndtert, men ikke løsning, er vilkåret hverken oppfylt eller ikke-oppfylt`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = MedlemskapYrkesskade()

        val søknad = Søknad(personident, fødselsdato)
        vilkår.håndterSøknad(søknad, fødselsdato, LocalDate.now())
        assertHarBehov(søknad)
        assertSkalIkkeVurdersManuelt(vilkår)

        assertFalse(vilkår.erOppfylt())
        assertFalse(vilkår.erIkkeOppfylt())
    }

    private fun assertHarBehov(hendelse: Hendelse) {
        assertTrue(hendelse.behov().isNotEmpty())
    }

    private fun assertHarIkkeBehov(hendelse: Hendelse) {
        assertTrue(hendelse.behov().isEmpty())
    }

    private fun assertMåVurderesManuelt(vilkårsvurdering: Vilkårsvurdering) {
        assertTrue(listOf(vilkårsvurdering).toDto().first().måVurderesManuelt)
    }

    private fun assertSkalIkkeVurdersManuelt(vilkårsvurdering: Vilkårsvurdering) {
        assertFalse(listOf(vilkårsvurdering).toDto().first().måVurderesManuelt)
    }
}
