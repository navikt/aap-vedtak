package no.nav.aap.modellapi

import no.nav.aap.domene.Søker
import no.nav.aap.domene.vilkår.Vilkårsvurdering
import no.nav.aap.hendelse.Behov
import no.nav.aap.hendelse.KvalitetssikringParagraf_11_12FørsteLedd
import no.nav.aap.hendelse.LøsningParagraf_11_12FørsteLedd
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

data class LøsningParagraf_11_12FørsteLeddModellApi(
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

    fun håndter(søker: Søker): List<Behov> {
        val løsning = toLøsning()
        søker.håndterLøsning(løsning, Vilkårsvurdering<*>::håndterLøsning)
        return løsning.behov()
    }

    private fun toLøsning() = LøsningParagraf_11_12FørsteLedd(
        løsningId = løsningId,
        vurdertAv = vurdertAv,
        tidspunktForVurdering = tidspunktForVurdering,
        bestemmesAv = when {
            bestemmesAv == "soknadstidspunkt" -> LøsningParagraf_11_12FørsteLedd.BestemmesAv.soknadstidspunkt
            bestemmesAv == "maksdatoSykepenger" -> LøsningParagraf_11_12FørsteLedd.BestemmesAv.maksdatoSykepenger
            bestemmesAv == "ermiraSays" -> LøsningParagraf_11_12FørsteLedd.BestemmesAv.ermiraSays
            bestemmesAv == "unntaksvurdering" && unntak == "forhindret" -> LøsningParagraf_11_12FørsteLedd.BestemmesAv.unntaksvurderingForhindret
            bestemmesAv == "unntaksvurdering" && unntak == "mangelfull" -> LøsningParagraf_11_12FørsteLedd.BestemmesAv.unntaksvurderingMangelfull
            bestemmesAv == "etterSisteLoenn" -> LøsningParagraf_11_12FørsteLedd.BestemmesAv.etterSisteLoenn
            else -> error("Ukjent bestemmesAv: $bestemmesAv og unntak: $unntak")
        },
        unntak = unntak,
        unntaksbegrunnelse = unntaksbegrunnelse,
        manueltSattVirkningsdato = manueltSattVirkningsdato
    )
}

data class KvalitetssikringParagraf_11_12FørsteLeddModellApi(
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

    private fun toKvalitetssikring() = KvalitetssikringParagraf_11_12FørsteLedd(
        kvalitetssikringId = kvalitetssikringId,
        løsningId = løsningId,
        kvalitetssikretAv = kvalitetssikretAv,
        tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
        erGodkjent = erGodkjent,
        begrunnelse = begrunnelse
    )
}