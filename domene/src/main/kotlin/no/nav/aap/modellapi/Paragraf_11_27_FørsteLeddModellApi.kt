package no.nav.aap.modellapi

import no.nav.aap.domene.Søker
import no.nav.aap.domene.vilkår.Vilkårsvurdering
import no.nav.aap.hendelse.Behov
import no.nav.aap.hendelse.KvalitetssikringParagraf_11_27_FørsteLedd
import no.nav.aap.hendelse.LøsningParagraf_11_27_FørsteLedd
import no.nav.aap.hendelse.entitet.Periode
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

data class LøsningParagraf_11_27_FørsteLedd_ModellApi(
    val løsningId: UUID,
    val svangerskapspenger: SvangerskapspengerModellApi
)

data class SvangerskapspengerModellApi(
    val fom: LocalDate,
    val tom: LocalDate,
    val grad: Double,
    val vedtaksdato: LocalDate
) {
    internal fun gjenopprett() = LøsningParagraf_11_27_FørsteLedd.Svangerskapspenger (
        periode = Periode(fom, tom),
        grad = grad,
        vedtaksdato = vedtaksdato
    )
}

data class KvalitetssikringParagraf_11_27_FørsteLedd_ModellApi(
    val kvalitetssikringId: UUID,
    val løsningId: UUID,
    val kvalitetssikretAv: String,
    val tidspunktForKvalitetssikring: LocalDateTime,
    val erGodkjent: Boolean,
    val begrunnelse: String
) {
    constructor(
        løsningId: UUID,
        kvalitetssikretAv: String,
        tidspunktForKvalitetssikring: LocalDateTime,
        erGodkjent: Boolean,
        begrunnelse: String
    ) : this(
        kvalitetssikringId = UUID.randomUUID(),
        løsningId = løsningId,
        kvalitetssikretAv = kvalitetssikretAv,
        tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
        erGodkjent = erGodkjent,
        begrunnelse = begrunnelse
    )

    fun håndter(søker: Søker): List<Behov> {
        val kvalitetssikring = toKvalitetssikring()
        søker.håndterKvalitetssikring(kvalitetssikring, Vilkårsvurdering<*>::håndterKvalitetssikring)
        return kvalitetssikring.behov()
    }

    private fun toKvalitetssikring() = KvalitetssikringParagraf_11_27_FørsteLedd(
        kvalitetssikringId = kvalitetssikringId,
        løsningId = løsningId,
        kvalitetssikretAv = kvalitetssikretAv,
        tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
        erGodkjent = erGodkjent,
        begrunnelse = begrunnelse
    )
}