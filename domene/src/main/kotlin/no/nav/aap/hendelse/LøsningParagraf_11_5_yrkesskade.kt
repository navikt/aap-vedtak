package no.nav.aap.hendelse

import no.nav.aap.dto.DtoLøsningParagraf_11_5_yrkesskade
import java.time.LocalDateTime

internal class LøsningParagraf_11_5_yrkesskade(
    private val vurdertAv: String,
    private val tidspunktForVurdering: LocalDateTime,
    private val arbeidsevneErNedsattMedMinst50Prosent: Boolean,
    private val arbeidsevneErNedsattMedMinst30Prosent: Boolean
) : Hendelse() {

    internal companion object {
        internal fun Iterable<LøsningParagraf_11_5_yrkesskade>.toDto() = map(LøsningParagraf_11_5_yrkesskade::toDto)
    }

    internal fun vurdertAv() = vurdertAv
    internal fun erNedsattMedMinst30Prosent() = arbeidsevneErNedsattMedMinst30Prosent

    private fun toDto() = DtoLøsningParagraf_11_5_yrkesskade(
        vurdertAv = vurdertAv,
        tidspunktForVurdering = tidspunktForVurdering,
        arbeidsevneErNedsattMedMinst50Prosent = arbeidsevneErNedsattMedMinst50Prosent,
        arbeidsevneErNedsattMedMinst30Prosent = arbeidsevneErNedsattMedMinst30Prosent
    )
}
