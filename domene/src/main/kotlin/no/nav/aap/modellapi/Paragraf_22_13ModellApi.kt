package no.nav.aap.modellapi

import no.nav.aap.domene.Søker
import no.nav.aap.domene.vilkår.Vilkårsvurdering
import no.nav.aap.hendelse.Behov.Companion.toDto
import no.nav.aap.hendelse.DtoBehov
import no.nav.aap.hendelse.KvalitetssikringParagraf_22_13
import no.nav.aap.hendelse.LøsningParagraf_22_13
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

data class LøsningParagraf_22_13ModellApi(
    val løsningId: UUID,
    val vurdertAv: String,
    val tidspunktForVurdering: LocalDateTime,
    val bestemmesAv: String,
    val unntak: String,
    val unntaksbegrunnelse: String,
    val manueltSattVirkningsdato: LocalDate?,
) {

    constructor(
        vurdertAv: String,
        tidspunktForVurdering: LocalDateTime,
        bestemmesAv: String,
        unntak: String,
        unntaksbegrunnelse: String,
        manueltSattVirkningsdato: LocalDate
    ) : this(
        løsningId = UUID.randomUUID(),
        vurdertAv = vurdertAv,
        tidspunktForVurdering = tidspunktForVurdering,
        bestemmesAv = bestemmesAv,
        unntak = unntak,
        unntaksbegrunnelse = unntaksbegrunnelse,
        manueltSattVirkningsdato = manueltSattVirkningsdato
    )

    fun håndter(søker: SøkerModellApi): Pair<SøkerModellApi, List<DtoBehov>> {
        val modellSøker = Søker.gjenopprett(søker)
        val løsning = toLøsning()
        modellSøker.håndterLøsning(løsning, Vilkårsvurdering<*>::håndterLøsning)
        return modellSøker.toDto() to løsning.behov().toDto(søker.personident)
    }

    private fun toLøsning() = LøsningParagraf_22_13(
        løsningId = løsningId,
        vurdertAv = vurdertAv,
        tidspunktForVurdering = tidspunktForVurdering,
        bestemmesAv = when {
            bestemmesAv == "soknadstidspunkt" -> LøsningParagraf_22_13.BestemmesAv.soknadstidspunkt
            bestemmesAv == "maksdatoSykepenger" -> LøsningParagraf_22_13.BestemmesAv.maksdatoSykepenger
            bestemmesAv == "ermiraSays" -> LøsningParagraf_22_13.BestemmesAv.ermiraSays
            bestemmesAv == "unntaksvurdering" && unntak == "forhindret" -> LøsningParagraf_22_13.BestemmesAv.unntaksvurderingForhindret
            bestemmesAv == "unntaksvurdering" && unntak == "mangelfull" -> LøsningParagraf_22_13.BestemmesAv.unntaksvurderingMangelfull
            bestemmesAv == "etterSisteLoenn" -> LøsningParagraf_22_13.BestemmesAv.etterSisteLoenn
            else -> error("Ukjent bestemmesAv: $bestemmesAv og unntak: $unntak")
        },
        unntak = unntak,
        unntaksbegrunnelse = unntaksbegrunnelse,
        manueltSattVirkningsdato = manueltSattVirkningsdato
    )
}

data class KvalitetssikringParagraf_22_13ModellApi(
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

    fun håndter(søker: SøkerModellApi): Pair<SøkerModellApi, List<DtoBehov>> {
        val modellSøker = Søker.gjenopprett(søker)
        val kvalitetssikring = toKvalitetssikring()
        modellSøker.håndterKvalitetssikring(kvalitetssikring, Vilkårsvurdering<*>::håndterKvalitetssikring)
        return modellSøker.toDto() to kvalitetssikring.behov().toDto(søker.personident)
    }

    private fun toKvalitetssikring() = KvalitetssikringParagraf_22_13(
        kvalitetssikringId = kvalitetssikringId,
        løsningId = løsningId,
        kvalitetssikretAv = kvalitetssikretAv,
        tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
        erGodkjent = erGodkjent,
        begrunnelse = begrunnelse
    )
}