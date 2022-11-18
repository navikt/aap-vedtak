package no.nav.aap.hendelse

import no.nav.aap.domene.vilkår.Kvalitetssikring
import no.nav.aap.domene.vilkår.Løsning
import no.nav.aap.domene.vilkår.Paragraf_11_5
import no.nav.aap.domene.vilkår.Totrinnskontroll
import no.nav.aap.modellapi.KvalitetssikringParagraf_11_5ModellApi
import no.nav.aap.modellapi.LøsningParagraf_11_5ModellApi
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

internal class LøsningParagraf_11_5(
    private val løsningId: UUID,
    private val vurdertAv: String,
    private val tidspunktForVurdering: LocalDateTime,
    private val nedsattArbeidsevnegrad: NedsattArbeidsevnegrad
) : Hendelse(), Løsning<LøsningParagraf_11_5, KvalitetssikringParagraf_11_5> {
    internal class NedsattArbeidsevnegrad(
        private val kravOmNedsattArbeidsevneErOppfylt: Boolean,
        private val kravOmNedsattArbeidsevneErOppfyltBegrunnelse: String,
        private val nedsettelseSkyldesSykdomEllerSkade: Boolean,
        private val nedsettelseSkyldesSykdomEllerSkadeBegrunnelse: String,
        private val kilder: List<String>,
        private val legeerklæringDato: LocalDate?,
        private val sykmeldingDato: LocalDate?,
    ) {

        internal fun erOppfylt() = kravOmNedsattArbeidsevneErOppfylt && nedsettelseSkyldesSykdomEllerSkade

        internal fun toDto(løsningId: UUID, vurdertAv: String, tidspunktForVurdering: LocalDateTime) =
            LøsningParagraf_11_5ModellApi(
                løsningId = løsningId,
                vurdertAv = vurdertAv,
                tidspunktForVurdering = tidspunktForVurdering,
                kravOmNedsattArbeidsevneErOppfylt = kravOmNedsattArbeidsevneErOppfylt,
                kravOmNedsattArbeidsevneErOppfyltBegrunnelse = kravOmNedsattArbeidsevneErOppfyltBegrunnelse,
                nedsettelseSkyldesSykdomEllerSkade = nedsettelseSkyldesSykdomEllerSkade,
                nedsettelseSkyldesSykdomEllerSkadeBegrunnelse = nedsettelseSkyldesSykdomEllerSkadeBegrunnelse,
                kilder = kilder,
                legeerklæringDato = legeerklæringDato,
                sykmeldingDato = sykmeldingDato,
            )
    }

    internal companion object {
        internal fun Iterable<LøsningParagraf_11_5>.toDto() = map(LøsningParagraf_11_5::toDto)
    }

    internal fun vurdertAv() = vurdertAv

    internal fun vurderNedsattArbeidsevne(
        vilkår: Paragraf_11_5.AvventerManuellVurdering,
        vilkårsvurdering: Paragraf_11_5
    ) {
        vilkår.vurderNedsattArbeidsevne(vilkårsvurdering, this, nedsattArbeidsevnegrad)
    }

    override fun matchMedKvalitetssikring(
        totrinnskontroll: Totrinnskontroll<LøsningParagraf_11_5, KvalitetssikringParagraf_11_5>,
        kvalitetssikring: KvalitetssikringParagraf_11_5
    ) {
        kvalitetssikring.matchMedLøsning(totrinnskontroll, løsningId)
    }

    override fun toDto() = nedsattArbeidsevnegrad.toDto(løsningId, vurdertAv, tidspunktForVurdering)
}

internal class KvalitetssikringParagraf_11_5(
    private val kvalitetssikringId: UUID,
    private val løsningId: UUID,
    private val kvalitetssikretAv: String,
    private val tidspunktForKvalitetssikring: LocalDateTime,
    private val erGodkjent: Boolean,
    private val begrunnelse: String?,
) : Hendelse(), Kvalitetssikring<LøsningParagraf_11_5, KvalitetssikringParagraf_11_5> {

    internal companion object {
        internal fun Iterable<KvalitetssikringParagraf_11_5>.toDto() = map(KvalitetssikringParagraf_11_5::toDto)
    }

    override fun erGodkjent() = erGodkjent
    internal fun kvalitetssikretAv() = kvalitetssikretAv

    override fun matchMedLøsning(
        totrinnskontroll: Totrinnskontroll<LøsningParagraf_11_5, KvalitetssikringParagraf_11_5>,
        løsningId: UUID
    ) {
        if (this.løsningId == løsningId) {
            totrinnskontroll.leggTilKvalitetssikring(this)
        }
    }

    override fun toDto() = KvalitetssikringParagraf_11_5ModellApi(
        kvalitetssikringId = kvalitetssikringId,
        løsningId = løsningId,
        kvalitetssikretAv = kvalitetssikretAv,
        tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
        erGodkjent = erGodkjent,
        begrunnelse = begrunnelse
    )
}

