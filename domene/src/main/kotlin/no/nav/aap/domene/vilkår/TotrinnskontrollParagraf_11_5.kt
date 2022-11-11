package no.nav.aap.domene.vilkår

import no.nav.aap.hendelse.KvalitetssikringParagraf_11_5
import no.nav.aap.hendelse.LøsningParagraf_11_5
import no.nav.aap.modellapi.TotrinnskontrollModellApi

internal class TotrinnskontrollParagraf_11_5 private constructor(
    private val løsning: LøsningParagraf_11_5,
    private var kvalitetssikring: KvalitetssikringParagraf_11_5?
) {

    internal constructor(løsning: LøsningParagraf_11_5) : this(løsning, null)

    internal fun erTotrinnskontrollGjennomført() = kvalitetssikring != null

    internal fun leggTilKvalitetssikring(kvalitetssikring: KvalitetssikringParagraf_11_5) {
        this.kvalitetssikring = kvalitetssikring
    }

    private fun toDto() = TotrinnskontrollModellApi(
        løsning = løsning.toDto(),
        kvalitetssikring = kvalitetssikring?.toDto()
    )

    internal companion object {
        internal fun Iterable<TotrinnskontrollParagraf_11_5>.leggTilKvalitetssikring(kvalitetssikring: KvalitetssikringParagraf_11_5) {
            this.forEach { it.løsning.matchMedKvalitetssikring(it, kvalitetssikring) }
        }

        internal fun Iterable<TotrinnskontrollParagraf_11_5>.toDto() = this.map { it.toDto() }

        internal fun gjenopprett(totrinnskontrollModellApi: List<TotrinnskontrollModellApi>) = totrinnskontrollModellApi.map {
            TotrinnskontrollParagraf_11_5(
                it.løsning.toLøsning(),
                it.kvalitetssikring?.toKvalitetssikring()
            )
        }
    }

}
