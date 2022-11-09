package no.nav.aap.domene.vilkår

import no.nav.aap.hendelse.LøsningParagraf_11_5
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.*

internal class BananTest {

    // er løsning kvalitetssikret


    @Test
    fun `Banan uten en Kvalitetssikring er ikke kvalitetssikret`() {


        // Gitt en banan med en løsning og uten en Kvalitetssikring
        val anyLøsning = LøsningParagraf_11_5(
            løsningId = UUID.randomUUID(),
            vurdertAv = "anyVeileder",
            tidspunktForVurdering = LocalDateTime.now(),
            nedsattArbeidsevnegrad = LøsningParagraf_11_5.NedsattArbeidsevnegrad(
                kravOmNedsattArbeidsevneErOppfylt = true,
                kravOmNedsattArbeidsevneErOppfyltBegrunnelse = "anyString",
                nedsettelseSkyldesSykdomEllerSkade = false,
                nedsettelseSkyldesSykdomEllerSkadeBegrunnelse = "anyString",
                kilder = listOf(),
                legeerklæringDato = null,
                sykmeldingDato = null
            )
        )
        val bananUtenKvalitetsikring = Banan(anyLøsning)

        // når vi spør om bananen om løsningen er kvalitetssikret
        val resultat = bananUtenKvalitetsikring.erTotrinnskontrollGjennomført()

        // så får vi svar nei
        assertFalse(resultat)

    }

    @Test
    fun test() {
        // Gitt en bananan med en løsning og en Kvalitetssikring
        // når vi spør om bananen om løsningen er kvalitetssikret
        // så får vi svar ja
        val anyLøsning = LøsningParagraf_11_5(
            løsningId = UUID.randomUUID(),
            vurdertAv = "anyVeileder",
            tidspunktForVurdering = LocalDateTime.now(),
            nedsattArbeidsevnegrad = LøsningParagraf_11_5.NedsattArbeidsevnegrad(
                kravOmNedsattArbeidsevneErOppfylt = true,
                kravOmNedsattArbeidsevneErOppfyltBegrunnelse = "anyString",
                nedsettelseSkyldesSykdomEllerSkade = false,
                nedsettelseSkyldesSykdomEllerSkadeBegrunnelse = "anyString",
                kilder = listOf(),
                legeerklæringDato = null,
                sykmeldingDato = null
            )
        )
        val bananMedKvalitetsikring = Banan(anyLøsning, )
    }
}
