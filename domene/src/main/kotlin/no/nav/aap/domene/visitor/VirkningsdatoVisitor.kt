package no.nav.aap.domene.visitor

import no.nav.aap.domene.Sakstype
import no.nav.aap.hendelse.LøsningParagraf_11_12FørsteLedd
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

internal class VirkningsdatoVisitor(sakstype: Sakstype) : SakstypeVisitor {
    internal lateinit var bestemmesAv: LøsningParagraf_11_12FørsteLedd.BestemmesAv
        private set
    internal var virkningsdato: LocalDate? = null
        private set

    init {
        sakstype.accept(this)
    }

    override fun visitLøsningParagraf_11_12FørsteLedd(
        løsning: LøsningParagraf_11_12FørsteLedd,
        løsningId: UUID,
        vurdertAv: String,
        tidspunktForVurdering: LocalDateTime,
        bestemmesAv: LøsningParagraf_11_12FørsteLedd.BestemmesAv,
        unntak: String,
        unntaksbegrunnelse: String,
        manueltSattVirkningsdato: LocalDate?
    ) {
        this.bestemmesAv = bestemmesAv
        this.virkningsdato = manueltSattVirkningsdato
    }
}
