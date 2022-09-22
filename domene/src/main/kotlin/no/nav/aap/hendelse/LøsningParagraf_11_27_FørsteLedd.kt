package no.nav.aap.hendelse

import no.nav.aap.domene.entitet.Periode
import no.nav.aap.domene.visitor.VilkårsvurderingVisitor
import no.nav.aap.modellapi.KvalitetssikringParagraf_11_27_FørsteLedd_ModellApi
import no.nav.aap.modellapi.LøsningParagraf_11_27_FørsteLedd_ModellApi
import no.nav.aap.modellapi.SvangerskapspengerModellApi
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

internal class LøsningParagraf_11_27_FørsteLedd(
    private val løsningId: UUID,
    private val svangerskapspenger: Svangerskapspenger
) : Hendelse() {
    internal companion object {
        internal fun Iterable<LøsningParagraf_11_27_FørsteLedd>.toDto() = map(LøsningParagraf_11_27_FørsteLedd::toDto)
    }

    internal fun accept(visitor: VilkårsvurderingVisitor) {
        visitor.visitLøsningParagraf_11_27(
            løsning = this,
            løsningId = løsningId,
            svangerskapspenger = svangerskapspenger
        )
    }

    internal fun harEnFullYtelse() = svangerskapspenger.erFullYtelse()

    internal fun toDto() = LøsningParagraf_11_27_FørsteLedd_ModellApi(
        løsningId = løsningId,
        svangerskapspenger = svangerskapspenger.toModellApi()
    )

    internal class Svangerskapspenger(
        private val periode: Periode?,
        private val grad: Double?,
        private val vedtaksdato: LocalDate?
    ) {

        internal fun erFullYtelse() = grad == 100.0

        internal fun toModellApi() = SvangerskapspengerModellApi(
            fom = periode?.fom(),
            tom = periode?.tom(),
            grad = grad,
            vedtaksdato = vedtaksdato
        )
    }
}

internal class KvalitetssikringParagraf_11_27_FørsteLedd(
    private val kvalitetssikringId: UUID,
    private val løsningId: UUID,
    private val kvalitetssikretAv: String,
    private val tidspunktForKvalitetssikring: LocalDateTime,
    private val erGodkjent: Boolean,
    private val begrunnelse: String
) : Hendelse() {

    internal companion object {
        internal fun Iterable<KvalitetssikringParagraf_11_27_FørsteLedd>.toDto() = map(KvalitetssikringParagraf_11_27_FørsteLedd::toDto)
    }

    internal fun erGodkjent() = erGodkjent
    internal fun kvalitetssikretAv() = kvalitetssikretAv
    internal fun toDto() = KvalitetssikringParagraf_11_27_FørsteLedd_ModellApi(
        kvalitetssikringId = kvalitetssikringId,
        løsningId = løsningId,
        kvalitetssikretAv = kvalitetssikretAv,
        tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
        erGodkjent = erGodkjent,
        begrunnelse = begrunnelse
    )
}
