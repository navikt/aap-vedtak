package no.nav.aap.domene.visitor

import no.nav.aap.hendelse.LøsningParagraf_11_19
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

internal class BeregningsdatoVisitor : SakstypeVisitor {
    internal lateinit var beregningsdato: LocalDate
        private set

    override fun visitLøsningParagraf_11_19(
        løsning: LøsningParagraf_11_19,
        løsningId: UUID,
        vurdertAv: String,
        tidspunktForVurdering: LocalDateTime,
        beregningsdato: LocalDate
    ) {
        this.beregningsdato = beregningsdato
    }
}
