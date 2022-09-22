package no.nav.aap.modellapi

import no.nav.aap.domene.Søker
import no.nav.aap.domene.vilkår.Vilkårsvurdering
import no.nav.aap.hendelse.*
import no.nav.aap.hendelse.Behov.Companion.toDto
import java.time.LocalDateTime
import java.util.*

data class LøsningMaskinellMedlemskapYrkesskadeModellApi(
    val løsningId: UUID,
    val erMedlem: String
) {

    fun håndter(søker: SøkerModellApi): Pair<SøkerModellApi, List<DtoBehov>> {
        val modellSøker = Søker.gjenopprett(søker)
        val løsning = toLøsning()
        modellSøker.håndterLøsning(løsning, Vilkårsvurdering<*>::håndterLøsning)
        return modellSøker.toDto() to løsning.behov().toDto(søker.personident)
    }

    private fun toLøsning() = LøsningMaskinellMedlemskapYrkesskade(løsningId, enumValueOf(erMedlem.uppercase()))
}

data class LøsningManuellMedlemskapYrkesskadeModellApi(
    val løsningId: UUID,
    val vurdertAv: String,
    val tidspunktForVurdering: LocalDateTime,
    val erMedlem: String
) {

    fun håndter(søker: SøkerModellApi): Pair<SøkerModellApi, List<DtoBehov>> {
        val modellSøker = Søker.gjenopprett(søker)
        val løsning = toLøsning()
        modellSøker.håndterLøsning(løsning, Vilkårsvurdering<*>::håndterLøsning)
        return modellSøker.toDto() to løsning.behov().toDto(søker.personident)
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

    fun håndter(søker: SøkerModellApi): Pair<SøkerModellApi, List<DtoBehov>> {
        val modellSøker = Søker.gjenopprett(søker)
        val kvalitetssikring = toKvalitetssikring()
        modellSøker.håndterKvalitetssikring(kvalitetssikring, Vilkårsvurdering<*>::håndterKvalitetssikring)
        return modellSøker.toDto() to kvalitetssikring.behov().toDto(søker.personident)
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