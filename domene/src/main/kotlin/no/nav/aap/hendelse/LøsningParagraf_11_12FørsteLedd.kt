package no.nav.aap.hendelse

import no.nav.aap.dto.DtoLøsningParagraf_11_12_ledd1
import java.time.LocalDate
import java.time.LocalDateTime

internal class LøsningParagraf_11_12FørsteLedd(
    private val vurdertAv: String,
    private val tidspunktForVurdering: LocalDateTime,
    private val bestemmesAv: String,
    private val unntak: String,
    private val unntaksbegrunnelse: String,
    private val manueltSattVirkningsdato: LocalDate
) : Hendelse() {
    internal companion object {
        internal fun Iterable<LøsningParagraf_11_12FørsteLedd>.toDto() = map(LøsningParagraf_11_12FørsteLedd::toDto)
    }

    internal fun vurdertAv() = vurdertAv

    private fun toDto() = DtoLøsningParagraf_11_12_ledd1(
        vurdertAv = vurdertAv,
        tidspunktForVurdering = tidspunktForVurdering,
        bestemmesAv = bestemmesAv,
        unntak = unntak,
        unntaksbegrunnelse = unntaksbegrunnelse,
        manueltSattVirkningsdato = manueltSattVirkningsdato
    )
}
