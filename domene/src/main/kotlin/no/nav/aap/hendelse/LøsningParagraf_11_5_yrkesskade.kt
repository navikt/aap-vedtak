package no.nav.aap.hendelse

import no.nav.aap.dto.DtoLøsningParagraf_11_5_yrkesskade

class LøsningParagraf_11_5_yrkesskade(
    private val arbeidsevneErNedsattMedMinst50Prosent: Boolean,
    private val arbeidsevneErNedsattMedMinst30Prosent: Boolean
) : Hendelse() {

    internal fun erNedsattMedMinst30Prosent() = arbeidsevneErNedsattMedMinst30Prosent
    internal fun erNedsattMedMinst50Prosent() = arbeidsevneErNedsattMedMinst50Prosent

    internal fun toDto() = DtoLøsningParagraf_11_5_yrkesskade(
        arbeidsevneErNedsattMedMinst50Prosent = arbeidsevneErNedsattMedMinst50Prosent,
        arbeidsevneErNedsattMedMinst30Prosent = arbeidsevneErNedsattMedMinst30Prosent
    )
}
