package no.nav.aap.domene.visitor

import no.nav.aap.hendelse.LøsningParagraf_22_13
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

internal class VirkningsdatoVisitor : SakstypeVisitor {
    internal lateinit var bestemmesAv: LøsningParagraf_22_13.BestemmesAv
        private set
    internal var virkningsdato: LocalDate? = null
        private set

    override fun visitLøsningParagraf_22_13(
        løsning: LøsningParagraf_22_13,
        løsningId: UUID,
        vurdertAv: String,
        tidspunktForVurdering: LocalDateTime,
        bestemmesAv: LøsningParagraf_22_13.BestemmesAv,
        unntak: String,
        unntaksbegrunnelse: String,
        manueltSattVirkningsdato: LocalDate?
    ) {
        this.bestemmesAv = bestemmesAv
        this.virkningsdato = manueltSattVirkningsdato
    }
}
