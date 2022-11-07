package no.nav.aap.domene

import no.nav.aap.domene.beregning.Inntektsgrunnlag
import no.nav.aap.hendelse.KvalitetssikringParagraf_11_5
import no.nav.aap.hendelse.LøsningParagraf_11_5
import no.nav.aap.modellapi.VedtakModellApi
import java.time.LocalDate
import java.util.*

internal class Vedtak(
    private val vedtaksid: UUID,
    private val innvilget: Boolean,
    private val inntektsgrunnlag: Inntektsgrunnlag,
    private val vedtaksdato: LocalDate,
    private val virkningsdato: LocalDate
) {
    private val settAvVurderteVilkårSomHarFørtTilDetteVedtaket = mutableListOf<Any>()

    internal fun toDto() = VedtakModellApi(
        vedtaksid = vedtaksid,
        innvilget = innvilget,
        inntektsgrunnlag = inntektsgrunnlag.toDto(),
        vedtaksdato = vedtaksdato,
        virkningsdato = virkningsdato
    )

    internal companion object {
        internal fun gjenopprett(vedtakModellApi: VedtakModellApi) = Vedtak(
            vedtaksid = vedtakModellApi.vedtaksid,
            innvilget = vedtakModellApi.innvilget,
            inntektsgrunnlag = Inntektsgrunnlag.gjenopprett(vedtakModellApi.inntektsgrunnlag),
            vedtaksdato = vedtakModellApi.vedtaksdato,
            virkningsdato = vedtakModellApi.virkningsdato
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Vedtak

        if (vedtaksid != other.vedtaksid) return false
        if (innvilget != other.innvilget) return false
        if (inntektsgrunnlag != other.inntektsgrunnlag) return false
        if (vedtaksdato != other.vedtaksdato) return false
        if (virkningsdato != other.virkningsdato) return false

        return true
    }

    override fun hashCode(): Int {
        var result = vedtaksid.hashCode()
        result = 31 * result + innvilget.hashCode()
        result = 31 * result + inntektsgrunnlag.hashCode()
        result = 31 * result + vedtaksdato.hashCode()
        result = 31 * result + virkningsdato.hashCode()
        return result
    }

    override fun toString() =
        "Vedtak(vedtaksid=$vedtaksid, innvilget=$innvilget, inntektsgrunnlag=$inntektsgrunnlag, vedtaksdato=$vedtaksdato, virkningsdato=$virkningsdato)"

    fun leggTilLøsning(løsning: LøsningParagraf_11_5, kvalitetssikring: KvalitetssikringParagraf_11_5) {
        settAvVurderteVilkårSomHarFørtTilDetteVedtaket.add(løsning)
        settAvVurderteVilkårSomHarFørtTilDetteVedtaket.add(kvalitetssikring)
    }
}
