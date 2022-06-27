package no.nav.aap.app.modell

import no.nav.aap.avro.medlem.v1.Medlem
import no.nav.aap.hendelse.LøsningManuellParagraf_11_2
import no.nav.aap.hendelse.LøsningMaskinellParagraf_11_2

fun Medlem.toDto(): LøsningMaskinellParagraf_11_2 = LøsningMaskinellParagraf_11_2(
    erMedlem = LøsningMaskinellParagraf_11_2.ErMedlem.valueOf(response.erMedlem.name)
)
