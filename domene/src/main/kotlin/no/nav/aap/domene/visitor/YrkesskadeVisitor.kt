package no.nav.aap.domene.visitor

import no.nav.aap.domene.beregning.Yrkesskade
import no.nav.aap.hendelse.LøsningParagraf_11_22
import java.time.LocalDateTime
import java.util.*

internal class YrkesskadeVisitor : SakstypeVisitor {
    internal lateinit var yrkesskade: Yrkesskade
        private set

    override fun visitLøsningParagraf_11_22(
        løsning: LøsningParagraf_11_22,
        løsningId: UUID,
        vurdertAv: String,
        tidspunktForVurdering: LocalDateTime
    ) {

    }
}
