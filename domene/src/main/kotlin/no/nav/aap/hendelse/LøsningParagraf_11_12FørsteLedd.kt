package no.nav.aap.hendelse

import no.nav.aap.domene.visitor.VilkårsvurderingVisitor
import no.nav.aap.modellapi.KvalitetssikringParagraf_11_12FørsteLeddModellApi
import no.nav.aap.modellapi.LøsningParagraf_11_12FørsteLeddModellApi
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

internal class LøsningParagraf_11_12FørsteLedd(
    private val løsningId: UUID,
    private val vurdertAv: String,
    private val tidspunktForVurdering: LocalDateTime,
    private val bestemmesAv: BestemmesAv,
    private val unntak: String,
    private val unntaksbegrunnelse: String,
    private val manueltSattVirkningsdato: LocalDate?,
) : Hendelse() {
    internal companion object {
        internal fun Iterable<LøsningParagraf_11_12FørsteLedd>.toDto() = map(LøsningParagraf_11_12FørsteLedd::toDto)
    }

    internal enum class BestemmesAv {
        soknadstidspunkt,
        maksdatoSykepenger,
        ermiraSays,
        unntaksvurderingForhindret,
        unntaksvurderingMangelfull,
        etterSisteLoenn,
    }

    internal fun accept(visitor: VilkårsvurderingVisitor) {
        visitor.visitLøsningParagraf_11_12FørsteLedd(
            this,
            løsningId,
            vurdertAv,
            tidspunktForVurdering,
            bestemmesAv,
            unntak,
            unntaksbegrunnelse,
            manueltSattVirkningsdato
        )
    }

    internal fun vurdertAv() = vurdertAv
    internal fun bestemmesAv() = bestemmesAv
    internal fun virkningsdato() = manueltSattVirkningsdato

    private fun toDto() = LøsningParagraf_11_12FørsteLeddModellApi(
        løsningId = løsningId,
        vurdertAv = vurdertAv,
        tidspunktForVurdering = tidspunktForVurdering,
        bestemmesAv = bestemmesAv.name,
        unntak = unntak,
        unntaksbegrunnelse = unntaksbegrunnelse,
        manueltSattVirkningsdato = manueltSattVirkningsdato
    )
}

class KvalitetssikringParagraf_11_12FørsteLedd(
    private val kvalitetssikringId: UUID,
    private val løsningId: UUID,
    private val kvalitetssikretAv: String,
    private val tidspunktForKvalitetssikring: LocalDateTime,
    private val erGodkjent: Boolean,
    private val begrunnelse: String
) : Hendelse() {

    internal companion object {
        internal fun Iterable<KvalitetssikringParagraf_11_12FørsteLedd>.toDto() =
            map(KvalitetssikringParagraf_11_12FørsteLedd::toDto)
    }

    internal fun erGodkjent() = erGodkjent
    internal fun kvalitetssikretAv() = kvalitetssikretAv
    internal fun toDto() = KvalitetssikringParagraf_11_12FørsteLeddModellApi(
        kvalitetssikringId = kvalitetssikringId,
        løsningId = løsningId,
        kvalitetssikretAv = kvalitetssikretAv,
        tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
        erGodkjent = erGodkjent,
        begrunnelse = begrunnelse
    )
}

