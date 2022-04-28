package no.nav.aap.domene

import no.nav.aap.domene.beregning.Inntektsgrunnlag
import no.nav.aap.dto.DtoVedtak
import no.nav.aap.visitor.SøkerVisitor
import java.time.LocalDate
import java.util.*

internal class Vedtak(
    private val vedtaksid: UUID,
    private val innvilget: Boolean,
    private val inntektsgrunnlag: Inntektsgrunnlag,
    private val vedtaksdato: LocalDate,
    private val virkningsdato: LocalDate
) {

    fun accept(visitor: SøkerVisitor) {
        visitor.preVisitVedtak(vedtaksid, innvilget, vedtaksdato, virkningsdato)
        inntektsgrunnlag.accept(visitor)
        visitor.postVisitVedtak(vedtaksid, innvilget, vedtaksdato, virkningsdato)
    }

    internal fun toDto() = DtoVedtak(
        vedtaksid = vedtaksid,
        innvilget = innvilget,
        inntektsgrunnlag = inntektsgrunnlag.toDto(),
        vedtaksdato = vedtaksdato,
        virkningsdato = virkningsdato
    )

    internal companion object {
        internal fun gjenopprett(dtoVedtak: DtoVedtak) = Vedtak(
            vedtaksid = dtoVedtak.vedtaksid,
            innvilget = dtoVedtak.innvilget,
            inntektsgrunnlag = Inntektsgrunnlag.gjenopprett(dtoVedtak.inntektsgrunnlag),
            vedtaksdato = dtoVedtak.vedtaksdato,
            virkningsdato = dtoVedtak.virkningsdato
        )
    }
}
