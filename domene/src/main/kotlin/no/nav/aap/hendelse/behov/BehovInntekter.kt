package no.nav.aap.hendelse.behov

import no.nav.aap.hendelse.Behov
import no.nav.aap.hendelse.DtoBehov
import java.time.Year

class BehovInntekter(
    private val fom: Year,
    private val tom: Year
) : Behov {
    override fun toDto(ident: String) = DtoBehov.DtoInntekter(ident, fom, tom)
}
