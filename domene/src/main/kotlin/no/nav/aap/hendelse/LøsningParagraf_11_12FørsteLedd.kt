package no.nav.aap.hendelse

import no.nav.aap.dto.DtoKvalitetssikringParagraf_11_12FørsteLedd
import no.nav.aap.dto.DtoLøsningParagraf_11_12FørsteLedd
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
    private val manueltSattVirkningsdato: LocalDate
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

    internal fun vurdertAv() = vurdertAv

    private fun toDto() = DtoLøsningParagraf_11_12FørsteLedd(
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
    internal fun toDto() = DtoKvalitetssikringParagraf_11_12FørsteLedd(
        kvalitetssikringId = kvalitetssikringId,
        løsningId = løsningId,
        kvalitetssikretAv = kvalitetssikretAv,
        tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
        erGodkjent = erGodkjent,
        begrunnelse = begrunnelse
    )
}

