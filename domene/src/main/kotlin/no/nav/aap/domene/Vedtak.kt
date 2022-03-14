package no.nav.aap.domene

import no.nav.aap.domene.beregning.Inntektsgrunnlag
import no.nav.aap.domene.tidslinje.Tidslinje
import no.nav.aap.dto.DtoVedtak
import java.time.LocalDate
import java.time.LocalDateTime

internal class Vedtak(
    private val innvilget: Boolean,
    private val inntektsgrunnlag: Inntektsgrunnlag,
    private val søknadstidspunkt: LocalDateTime,
    private val vedtaksdato: LocalDate,
    private val virkningsdato: LocalDate,
    private val tidslinje: Tidslinje
) {
    internal fun toDto() = DtoVedtak(
        innvilget = innvilget,
        inntektsgrunnlag = inntektsgrunnlag.toDto(),
        søknadstidspunkt = søknadstidspunkt,
        vedtaksdato = vedtaksdato,
        virkningsdato = virkningsdato
    )

    internal companion object {
        internal fun gjenopprett(dtoVedtak: DtoVedtak) = Vedtak(
            innvilget = dtoVedtak.innvilget,
            inntektsgrunnlag = Inntektsgrunnlag.gjenopprett(dtoVedtak.inntektsgrunnlag),
            søknadstidspunkt = dtoVedtak.søknadstidspunkt,
            vedtaksdato = dtoVedtak.vedtaksdato,
            virkningsdato = dtoVedtak.virkningsdato,
            tidslinje = Tidslinje() //FIXME
        )
    }
}
