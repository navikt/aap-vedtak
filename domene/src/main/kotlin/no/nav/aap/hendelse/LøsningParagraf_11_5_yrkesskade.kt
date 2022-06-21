package no.nav.aap.hendelse

import no.nav.aap.dto.DtoLøsningParagraf_11_5_yrkesskade

internal class LøsningParagraf_11_5_yrkesskade(
    private val vurdertAv: String,
    private val arbeidsevneErNedsattMedMinst50Prosent: Boolean,
    private val arbeidsevneErNedsattMedMinst30Prosent: Boolean
) : Hendelse() {

    internal fun vurdertAv() = vurdertAv
    internal fun erNedsattMedMinst30Prosent() = arbeidsevneErNedsattMedMinst30Prosent

    internal fun toDto() = DtoLøsningParagraf_11_5_yrkesskade(
        vurdertAv = vurdertAv,
        arbeidsevneErNedsattMedMinst50Prosent = arbeidsevneErNedsattMedMinst50Prosent,
        arbeidsevneErNedsattMedMinst30Prosent = arbeidsevneErNedsattMedMinst30Prosent
    )
}
