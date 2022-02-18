package no.nav.aap.domene

import no.nav.aap.domene.beregning.Inntektsgrunnlag
import no.nav.aap.dto.DtoVedtak
import java.time.LocalDate
import java.time.LocalDateTime

internal class Vedtak(
    private val innvilget: Boolean,
    private val inntektsgrunnlag: Inntektsgrunnlag,
    private val søknadstidspunkt: LocalDateTime,
    private val vedtaksdato: LocalDate,
    private val virkningsdato: LocalDate
) {
    internal fun toDto() = DtoVedtak(
        innvilget = innvilget,
        inntektsgrunnlag = inntektsgrunnlag.toDto(),
        søknadstidspunkt = søknadstidspunkt,
        vedtaksdato = vedtaksdato,
        virkningsdato = virkningsdato
    )
}
