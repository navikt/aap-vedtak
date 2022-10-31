package no.nav.aap.domene.visitor

import no.nav.aap.hendelse.LøsningParagraf_11_27_FørsteLedd
import no.nav.aap.hendelse.LøsningParagraf_22_13
import no.nav.aap.hendelse.LøsningSykepengedager
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

internal class VirkningsdatoVisitor : SakstypeVisitor {
    internal lateinit var bestemmesAv: LøsningParagraf_22_13.BestemmesAv
        private set
    internal var virkningsdato: LocalDate = LocalDate.now() //TODO: Finne søknadstidspunkt
        private set

    private var virkningsdatoSykepenger: LocalDate? = null
    private var virkningsdatoSvangerskapspenger: LocalDate? = null

    override fun visitLøsningParagraf_8_48Har(
        løsning: LøsningSykepengedager,
        løsningId: UUID,
        virkningsdato: LocalDate
    ) {
        this.virkningsdatoSykepenger = virkningsdato
    }

    override fun visitLøsningParagraf_11_27(
        løsning: LøsningParagraf_11_27_FørsteLedd,
        løsningId: UUID,
        svangerskapspenger: LøsningParagraf_11_27_FørsteLedd.Svangerskapspenger
    ) {
        this.virkningsdatoSvangerskapspenger = svangerskapspenger.virkningsdato()
    }

    override fun visitLøsningParagraf_22_13(
        løsning: LøsningParagraf_22_13,
        løsningId: UUID,
        vurdertAv: String,
        tidspunktForVurdering: LocalDateTime,
        bestemmesAv: LøsningParagraf_22_13.BestemmesAv,
        unntak: String?,
        unntaksbegrunnelse: String?,
        manueltSattVirkningsdato: LocalDate?
    ) {
        this.bestemmesAv = bestemmesAv
        this.virkningsdato = when (bestemmesAv) {
            LøsningParagraf_22_13.BestemmesAv.maksdatoSykepenger -> requireNotNull(virkningsdatoSykepenger)
            LøsningParagraf_22_13.BestemmesAv.svangerskapspenger -> requireNotNull(virkningsdatoSvangerskapspenger)
            else -> manueltSattVirkningsdato ?: LocalDate.now() //TODO: Finne søknadstidspunkt
        }
    }
}
