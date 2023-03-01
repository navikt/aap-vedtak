package no.nav.aap.hendelse.behov

import no.nav.aap.modellapi.BehovModellApi
import java.time.Year

internal class BehovInntekter(
    private val fom: Year,
    private val tom: Year
) : Behov {
    override fun toDto(ident: String) = BehovModellApi.BehovInntekterModellApi(ident, fom, tom)
}
