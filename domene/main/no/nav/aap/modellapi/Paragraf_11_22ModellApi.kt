package no.nav.aap.modellapi

import no.nav.aap.domene.Søker
import no.nav.aap.domene.beregning.Beløp.Companion.beløp
import no.nav.aap.domene.vilkår.Vilkårsvurdering
import no.nav.aap.hendelse.KvalitetssikringParagraf_11_22
import no.nav.aap.hendelse.LøsningParagraf_11_22
import no.nav.aap.hendelse.behov.Behov.Companion.toDto
import java.time.LocalDateTime
import java.time.Year
import java.util.*

data class LøsningParagraf_11_22ModellApi(
    val løsningId: UUID,
    val vurdertAv: String,
    val tidspunktForVurdering: LocalDateTime,
    val erOppfylt: Boolean,
    val andelNedsattArbeidsevne: Int,
    val år: Year,
    val antattÅrligArbeidsinntekt: Double
) : LøsningModellApi() {

    fun håndter(søker: SøkerModellApi): Pair<SøkerModellApi, List<BehovModellApi>> {
        val modellSøker = Søker.gjenopprett(søker)
        val løsning = toLøsning()
        modellSøker.håndterLøsning(løsning, Vilkårsvurdering<*, *>::håndterLøsning)
        return modellSøker.toDto() to løsning.behov().toDto(søker.personident)
    }

    override fun toLøsning() = LøsningParagraf_11_22(
        løsningId = løsningId,
        vurdertAv = vurdertAv,
        tidspunktForVurdering = tidspunktForVurdering,
        erOppfylt = erOppfylt,
        andelNedsattArbeidsevne = andelNedsattArbeidsevne,
        år = år,
        antattÅrligArbeidsinntekt = antattÅrligArbeidsinntekt.beløp
    )
}

data class KvalitetssikringParagraf_11_22ModellApi(
    val kvalitetssikringId: UUID,
    val løsningId: UUID,
    val kvalitetssikretAv: String,
    val tidspunktForKvalitetssikring: LocalDateTime,
    val erGodkjent: Boolean,
    val begrunnelse: String?,
) : KvalitetssikringModellApi() {

    fun håndter(søker: SøkerModellApi): Pair<SøkerModellApi, List<BehovModellApi>> {
        val modellSøker = Søker.gjenopprett(søker)
        val kvalitetssikring = toKvalitetssikring()
        modellSøker.håndterKvalitetssikring(kvalitetssikring, Vilkårsvurdering<*, *>::håndterKvalitetssikring)
        return modellSøker.toDto() to kvalitetssikring.behov().toDto(søker.personident)
    }

    override fun toKvalitetssikring() = KvalitetssikringParagraf_11_22(
        kvalitetssikringId = kvalitetssikringId,
        løsningId = løsningId,
        kvalitetssikretAv = kvalitetssikretAv,
        tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
        erGodkjent = erGodkjent,
        begrunnelse = begrunnelse
    )
}