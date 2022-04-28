package no.nav.aap.visitor

import no.nav.aap.domene.Sakstype
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

internal interface SøkerVisitor : BeregningVisitor, EntitetVisitor, VilkårVisitor {
    fun preVisitSøker() {}
    fun postVisitSøker() {}

    fun preVisitSak(saksid: UUID, vurderingsdato: LocalDate, søknadstidspunkt: LocalDateTime) {}
    fun postVisitSak(saksid: UUID, vurderingsdato: LocalDate, søknadstidspunkt: LocalDateTime) {}

    fun preVisitSakstype(sakstypenavn: Sakstype.Type, aktiv: Boolean) {}
    fun postVisitSakstype(sakstypenavn: Sakstype.Type, aktiv: Boolean) {}

    fun preVisitVurderingAvBeregningsdato() {}
    fun postVisitVurderingAvBeregningsdato() {}

    fun preVisitVedtak(vedtaksid: UUID, innvilget: Boolean, vedtaksdato: LocalDate, virkningsdato: LocalDate) {}
    fun postVisitVedtak(vedtaksid: UUID, innvilget: Boolean, vedtaksdato: LocalDate, virkningsdato: LocalDate) {}

    fun visitSakTilstandStart() {}
    fun visitSakTilstandSøknadMottatt() {}
    fun visitSakTilstandBeregnInntekt() {}
    fun visitSakTilstandVedtakFattet() {}
    fun visitSakTilstandIkkeOppfylt() {}

    fun visitVurderingAvBeregningsdatoTilstandStart() {}
    fun visitVurderingAvBeregningsdatoTilstandSøknadMottatt() {}
    fun visitVurderingAvBeregningsdatoTilstandFerdig(beregningsdato: LocalDate) {}
}
