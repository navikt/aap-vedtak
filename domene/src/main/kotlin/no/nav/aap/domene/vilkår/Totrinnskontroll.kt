package no.nav.aap.domene.vilkår

import no.nav.aap.hendelse.KvalitetssikringParagraf_11_5
import no.nav.aap.hendelse.LøsningParagraf_11_5
import no.nav.aap.modellapi.TotrinnskontrollModellApi

internal class Totrinnskontroll(private val løsning: LøsningParagraf_11_5) {

    private var kvalitetssikring: KvalitetssikringParagraf_11_5? = null

    internal fun erTotrinnskontrollGjennomført() = kvalitetssikring != null

    internal fun leggTilKvalitetssikring(kvalitetssikring: KvalitetssikringParagraf_11_5) {
        this.kvalitetssikring = kvalitetssikring
    }

    private fun toDto() = TotrinnskontrollModellApi(
        løsning = løsning.toDto(),
        kvalitetssikring = kvalitetssikring?.toDto()
    )

    internal companion object {
        internal fun Iterable<Totrinnskontroll>.leggTilKvalitetssikring(kvalitetssikring: KvalitetssikringParagraf_11_5) {
            this.forEach { it.løsning.matchMedKvalitetssikring(it, kvalitetssikring) }
        }

        internal fun Iterable<Totrinnskontroll>.toDto() = this.map { it.toDto() }

        internal fun gjenopprett(totrinnskontrollModellApi: List<TotrinnskontrollModellApi>) = totrinnskontrollModellApi.map {
            Totrinnskontroll(
                LøsningParagraf_11_5(
                    løsningId = it.løsning.løsningId,
                    vurdertAv = it.løsning.vurdertAv,
                    tidspunktForVurdering = it.løsning.tidspunktForVurdering,
                    nedsattArbeidsevnegrad = LøsningParagraf_11_5.NedsattArbeidsevnegrad(
                        kravOmNedsattArbeidsevneErOppfylt = it.løsning.kravOmNedsattArbeidsevneErOppfylt,
                        kravOmNedsattArbeidsevneErOppfyltBegrunnelse = it.løsning.kravOmNedsattArbeidsevneErOppfyltBegrunnelse,
                        nedsettelseSkyldesSykdomEllerSkade = it.løsning.nedsettelseSkyldesSykdomEllerSkade,
                        nedsettelseSkyldesSykdomEllerSkadeBegrunnelse = it.løsning.nedsettelseSkyldesSykdomEllerSkadeBegrunnelse,
                        kilder = it.løsning.kilder,
                        legeerklæringDato = it.løsning.legeerklæringDato,
                        sykmeldingDato = it.løsning.sykmeldingDato,
                    )
                )
            )
        }
    }

}
