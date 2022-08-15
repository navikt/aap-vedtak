package no.nav.aap.app.modell

import no.nav.aap.avro.medlem.v1.Medlem
import no.nav.aap.dto.DtoLøsningMaskinellParagraf_11_2
import no.nav.aap.hendelse.LøsningManuellParagraf_11_2
import no.nav.aap.hendelse.LøsningMaskinellParagraf_11_2

fun Medlem.toDto(): DtoLøsningMaskinellParagraf_11_2 = DtoLøsningMaskinellParagraf_11_2(
    erMedlem = response.erMedlem.name
)
