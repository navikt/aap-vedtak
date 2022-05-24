package no.nav.aap.hendelse

import no.nav.aap.dto.DtoLøsningParagraf_11_12_ledd1
import java.time.LocalDate

internal class LøsningParagraf_11_12FørsteLedd(
    private val bestemmesAv: String,
    private val unntak: String,
    private val unntaksbegrunnelse: String,
    private val manueltSattVirkningsdato: LocalDate
) : Hendelse() {
    internal fun toDto() = DtoLøsningParagraf_11_12_ledd1(
        bestemmesAv = bestemmesAv,
        unntak = unntak,
        unntaksbegrunnelse = unntaksbegrunnelse,
        manueltSattVirkningsdato = manueltSattVirkningsdato
    )
}
