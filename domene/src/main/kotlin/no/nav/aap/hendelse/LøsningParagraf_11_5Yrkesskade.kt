package no.nav.aap.hendelse

import no.nav.aap.dto.DtoKvalitetssikringParagraf_11_5Yrkesskade
import no.nav.aap.dto.DtoLøsningParagraf_11_5Yrkesskade
import java.time.LocalDateTime

internal class LøsningParagraf_11_5Yrkesskade(
    private val vurdertAv: String,
    private val tidspunktForVurdering: LocalDateTime,
    private val arbeidsevneErNedsattMedMinst50Prosent: Boolean,
    private val arbeidsevneErNedsattMedMinst30Prosent: Boolean
) : Hendelse() {

    internal companion object {
        internal fun Iterable<LøsningParagraf_11_5Yrkesskade>.toDto() = map(LøsningParagraf_11_5Yrkesskade::toDto)
    }

    internal fun vurdertAv() = vurdertAv
    internal fun erNedsattMedMinst30Prosent() = arbeidsevneErNedsattMedMinst30Prosent

    private fun toDto() = DtoLøsningParagraf_11_5Yrkesskade(
        vurdertAv = vurdertAv,
        tidspunktForVurdering = tidspunktForVurdering,
        arbeidsevneErNedsattMedMinst50Prosent = arbeidsevneErNedsattMedMinst50Prosent,
        arbeidsevneErNedsattMedMinst30Prosent = arbeidsevneErNedsattMedMinst30Prosent
    )
}

class KvalitetssikringParagraf_11_5Yrkesskade(
    private val kvalitetssikretAv: String,
    private val erGodkjent: Boolean,
    private val begrunnelse: String
) : Hendelse() {

    internal companion object {
        internal fun Iterable<KvalitetssikringParagraf_11_5Yrkesskade>.toDto() = map(KvalitetssikringParagraf_11_5Yrkesskade::toDto)
    }

    internal fun erGodkjent() = erGodkjent
    internal fun kvalitetssikretAv() = kvalitetssikretAv
    internal fun toDto() = DtoKvalitetssikringParagraf_11_5Yrkesskade(kvalitetssikretAv, erGodkjent, begrunnelse)
}

