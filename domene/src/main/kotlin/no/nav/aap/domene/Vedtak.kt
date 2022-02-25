package no.nav.aap.domene

import no.nav.aap.domene.beregning.Inntektsgrunnlag
import no.nav.aap.dto.DtoVedtak
import no.nav.aap.frontendView.FrontendVedtak
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

    internal fun toFrontendVedtak() = FrontendVedtak(
        innvilget = innvilget,
        inntektsgrunnlag = inntektsgrunnlag.toFrontendInntektsgrunnlag(),
        søknadstidspunkt = søknadstidspunkt,
        vedtaksdato = vedtaksdato,
        virkningsdato = virkningsdato
    )

    internal companion object {
        internal fun create(dtoVedtak: DtoVedtak) = Vedtak(
            innvilget = dtoVedtak.innvilget,
            inntektsgrunnlag = Inntektsgrunnlag.create(dtoVedtak.inntektsgrunnlag),
            søknadstidspunkt = dtoVedtak.søknadstidspunkt,
            vedtaksdato = dtoVedtak.vedtaksdato,
            virkningsdato = dtoVedtak.virkningsdato
        )
    }
}
