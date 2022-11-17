package no.nav.aap.modellapi

import no.nav.aap.domene.Søker
import no.nav.aap.domene.entitet.Periode.Companion.til
import no.nav.aap.domene.vilkår.Vilkårsvurdering
import no.nav.aap.hendelse.KvalitetssikringParagraf_11_27_FørsteLedd
import no.nav.aap.hendelse.LøsningParagraf_11_27_FørsteLedd
import no.nav.aap.hendelse.behov.Behov.Companion.toDto
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

data class LøsningParagraf_11_27_FørsteLedd_ModellApi(
    val løsningId: UUID,
    val tidspunktForVurdering: LocalDateTime,
    val svangerskapspenger: SvangerskapspengerModellApi
) {
    constructor(
        svangerskapspenger: SvangerskapspengerModellApi
    ) : this(
        løsningId = UUID.randomUUID(),
        tidspunktForVurdering = LocalDateTime.now(),
        svangerskapspenger = svangerskapspenger,
    )

    fun håndter(søker: SøkerModellApi): Pair<SøkerModellApi, List<BehovModellApi>> {
        val modellSøker = Søker.gjenopprett(søker)
        val løsning = gjenopprett()
        modellSøker.håndterLøsning(løsning, Vilkårsvurdering<*, *>::håndterLøsning)
        return modellSøker.toDto() to løsning.behov().toDto(søker.personident)
    }

    internal fun gjenopprett() = LøsningParagraf_11_27_FørsteLedd(
        løsningId = løsningId,
        tidspunktForVurdering = tidspunktForVurdering,
        svangerskapspenger = svangerskapspenger.gjenopprett()
    )
}

data class SvangerskapspengerModellApi(
    val fom: LocalDate?,
    val tom: LocalDate?,
    val grad: Double?,
    val vedtaksdato: LocalDate?
) {
    internal fun gjenopprett() = LøsningParagraf_11_27_FørsteLedd.Svangerskapspenger(
        periode = if (fom != null && tom != null) fom til tom else null,
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

    fun håndter(søker: SøkerModellApi): Pair<SøkerModellApi, List<BehovModellApi>> {
        val modellSøker = Søker.gjenopprett(søker)
        val kvalitetssikring = toKvalitetssikring()
        modellSøker.håndterKvalitetssikring(kvalitetssikring, Vilkårsvurdering<*, *>::håndterKvalitetssikring)
        return modellSøker.toDto() to kvalitetssikring.behov().toDto(søker.personident)
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