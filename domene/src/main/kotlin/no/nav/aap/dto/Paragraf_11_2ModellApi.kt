package no.nav.aap.dto

import no.nav.aap.domene.Søker
import no.nav.aap.domene.vilkår.Vilkårsvurdering
import no.nav.aap.hendelse.Behov
import no.nav.aap.hendelse.KvalitetssikringParagraf_11_2
import no.nav.aap.hendelse.LøsningManuellParagraf_11_2
import no.nav.aap.hendelse.LøsningMaskinellParagraf_11_2
import java.time.LocalDateTime
import java.util.*

data class LøsningParagraf_11_2ModellApi(
    val løsningId: UUID,
    val vurdertAv: String,
    val tidspunktForVurdering: LocalDateTime,
    val erMedlem: String
) {

    constructor(
        vurdertAv: String,
        tidspunktForVurdering: LocalDateTime,
        erMedlem: String
    ) : this(
        løsningId = UUID.randomUUID(),
        vurdertAv = vurdertAv,
        tidspunktForVurdering = tidspunktForVurdering,
        erMedlem = erMedlem
    )

    fun håndter(søker: Søker): List<Behov> {
        val løsning = toLøsning()
        søker.håndterLøsning(løsning, Vilkårsvurdering<*>::håndterLøsning)
        return løsning.behov()
    }

    private fun toLøsning() = LøsningManuellParagraf_11_2(
        løsningId = løsningId,
        vurdertAv = vurdertAv,
        tidspunktForVurdering = tidspunktForVurdering,
        erMedlem = if (erMedlem.lowercase() in listOf("true", "ja"))
            LøsningManuellParagraf_11_2.ErMedlem.JA else LøsningManuellParagraf_11_2.ErMedlem.NEI
    )
}

data class KvalitetssikringParagraf_11_2ModellApi(
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

    private fun toKvalitetssikring() = KvalitetssikringParagraf_11_2(
        kvalitetssikringId = kvalitetssikringId,
        løsningId = løsningId,
        kvalitetssikretAv = kvalitetssikretAv,
        tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
        erGodkjent = erGodkjent,
        begrunnelse = begrunnelse
    )
}

data class LøsningMaskinellParagraf_11_2ModellApi(
    val løsningId: UUID,
    val tidspunktForVurdering: LocalDateTime,
    val erMedlem: String
) {

    constructor(
        erMedlem: String
    ) : this(
        løsningId = UUID.randomUUID(),
        tidspunktForVurdering = LocalDateTime.now(),
        erMedlem = erMedlem
    )

    fun håndter(søker: Søker): List<Behov> {
        val løsning = toLøsning()
        søker.håndterLøsning(løsning, Vilkårsvurdering<*>::håndterLøsning)
        return løsning.behov()
    }

    private fun toLøsning() =
        LøsningMaskinellParagraf_11_2(løsningId, tidspunktForVurdering, enumValueOf(erMedlem.uppercase()))
}