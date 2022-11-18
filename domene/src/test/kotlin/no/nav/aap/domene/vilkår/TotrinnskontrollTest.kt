package no.nav.aap.domene.vilkår

import no.nav.aap.domene.vilkår.Totrinnskontroll.Companion.leggTilKvalitetssikring
import no.nav.aap.hendelse.KvalitetssikringParagraf_11_5
import no.nav.aap.hendelse.LøsningParagraf_11_5
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.*

internal class TotrinnskontrollTest {

    @Test
    fun `Totrinnskontroll uten en Kvalitetssikring er ikke kvalitetssikret`() {
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
        val totrinnskontrollUtenKvalitetsikring = Totrinnskontroll(anyLøsning)

        val resultat = totrinnskontrollUtenKvalitetsikring.erTotrinnskontrollGjennomført()

        assertFalse(resultat)
    }

    @Test
    fun `Totrinnskontroll med en Kvalitetssikring er kvalitetssikret`() {
        val løsningId = UUID.randomUUID()
        val anyLøsning = LøsningParagraf_11_5(
            løsningId = løsningId,
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
        val totrinnskontroll = Totrinnskontroll(anyLøsning)

        val kvalitetssikring = KvalitetssikringParagraf_11_5(
            kvalitetssikringId = UUID.randomUUID(),
            løsningId = løsningId,
            kvalitetssikretAv = "",
            tidspunktForKvalitetssikring = LocalDateTime.now(),
            erGodkjent = true,
            begrunnelse = null
        )

        listOf(totrinnskontroll).leggTilKvalitetssikring(kvalitetssikring)

        assertTrue(totrinnskontroll.erTotrinnskontrollGjennomført())
    }

    @Test
    fun `Totrinnskontroll med en godkjent Kvalitetssikring er godkjent`() {
        val løsningId = UUID.randomUUID()
        val anyLøsning = LøsningParagraf_11_5(
            løsningId = løsningId,
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
        val totrinnskontroll = Totrinnskontroll(anyLøsning)

        val kvalitetssikring = KvalitetssikringParagraf_11_5(
            kvalitetssikringId = UUID.randomUUID(),
            løsningId = løsningId,
            kvalitetssikretAv = "",
            tidspunktForKvalitetssikring = LocalDateTime.now(),
            erGodkjent = true,
            begrunnelse = null
        )

        listOf(totrinnskontroll).leggTilKvalitetssikring(kvalitetssikring)

        assertTrue(totrinnskontroll.erGodkjent())
    }

    @Test
    fun `Totrinnskontroll med en underkjent Kvalitetssikring er ikke godkjent`() {
        val løsningId = UUID.randomUUID()
        val anyLøsning = LøsningParagraf_11_5(
            løsningId = løsningId,
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
        val totrinnskontroll = Totrinnskontroll(anyLøsning)

        val kvalitetssikring = KvalitetssikringParagraf_11_5(
            kvalitetssikringId = UUID.randomUUID(),
            løsningId = løsningId,
            kvalitetssikretAv = "",
            tidspunktForKvalitetssikring = LocalDateTime.now(),
            erGodkjent = false,
            begrunnelse = null
        )

        listOf(totrinnskontroll).leggTilKvalitetssikring(kvalitetssikring)

        assertFalse(totrinnskontroll.erGodkjent())
    }
}
