package no.nav.aap.dto

import no.nav.aap.domene.Søker
import no.nav.aap.domene.vilkår.Vilkårsvurdering
import no.nav.aap.hendelse.Behov
import no.nav.aap.hendelse.KvalitetssikringMedlemskapYrkesskade
import no.nav.aap.hendelse.LøsningManuellMedlemskapYrkesskade
import no.nav.aap.hendelse.LøsningMaskinellMedlemskapYrkesskade
import java.time.LocalDateTime
import java.util.*

data class LøsningMaskinellMedlemskapYrkesskadeModellApi(
    val løsningId: UUID,
    val erMedlem: String
) {
    fun håndter(søker: Søker): List<Behov> {
        val løsning = toLøsning()
        søker.håndterLøsning(løsning, Vilkårsvurdering<*>::håndterLøsning)
        return løsning.behov()
    }

    private fun toLøsning() = LøsningMaskinellMedlemskapYrkesskade(løsningId, enumValueOf(erMedlem.uppercase()))
}

data class LøsningManuellMedlemskapYrkesskadeModellApi(
    val løsningId: UUID,
    val vurdertAv: String,
    val tidspunktForVurdering: LocalDateTime,
    val erMedlem: String
) {
    fun håndter(søker: Søker): List<Behov> {
        val løsning = toLøsning()
        søker.håndterLøsning(løsning, Vilkårsvurdering<*>::håndterLøsning)
        return løsning.behov()
    }

    private fun toLøsning() =
        LøsningManuellMedlemskapYrkesskade(
            løsningId,
            vurdertAv,
            tidspunktForVurdering,
            enumValueOf(erMedlem.uppercase())
        )
}

data class KvalitetssikringMedlemskapYrkesskadeModellApi(
    val kvalitetssikringId: UUID,
    val løsningId: UUID,
    val kvalitetssikretAv: String,
    val tidspunktForKvalitetssikring: LocalDateTime,
    val erGodkjent: Boolean,
    val begrunnelse: String
) {

    fun håndter(søker: Søker): List<Behov> {
        val kvalitetssikring = toKvalitetssikring()
        søker.håndterKvalitetssikring(kvalitetssikring, Vilkårsvurdering<*>::håndterKvalitetssikring)
        return kvalitetssikring.behov()
    }

    private fun toKvalitetssikring() = KvalitetssikringMedlemskapYrkesskade(
        kvalitetssikringId = kvalitetssikringId,
        løsningId = løsningId,
        kvalitetssikretAv = kvalitetssikretAv,
        tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
        erGodkjent = erGodkjent,
        begrunnelse = begrunnelse
    )
}