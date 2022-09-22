package no.nav.aap.hendelse

import no.nav.aap.domene.vilkår.Paragraf_11_5
import no.nav.aap.modellapi.KvalitetssikringParagraf_11_5ModellApi
import no.nav.aap.modellapi.LøsningParagraf_11_5ModellApi
import java.time.LocalDateTime
import java.util.*

internal class LøsningParagraf_11_5(
    private val løsningId: UUID,
    private val vurdertAv: String,
    private val tidspunktForVurdering: LocalDateTime,
    private val nedsattArbeidsevnegrad: NedsattArbeidsevnegrad
) : Hendelse() {
    internal class NedsattArbeidsevnegrad(
        private val kravOmNedsattArbeidsevneErOppfylt: Boolean,
        private val nedsettelseSkyldesSykdomEllerSkade: Boolean,
    ) {

        internal fun erOppfylt() = kravOmNedsattArbeidsevneErOppfylt && nedsettelseSkyldesSykdomEllerSkade

        internal fun toDto(løsningId: UUID, vurdertAv: String, tidspunktForVurdering: LocalDateTime) = LøsningParagraf_11_5ModellApi(
            løsningId = løsningId,
            vurdertAv = vurdertAv,
            tidspunktForVurdering = tidspunktForVurdering,
            kravOmNedsattArbeidsevneErOppfylt = kravOmNedsattArbeidsevneErOppfylt,
            nedsettelseSkyldesSykdomEllerSkade = nedsettelseSkyldesSykdomEllerSkade,
        )
    }

    internal companion object {
        internal fun Iterable<LøsningParagraf_11_5>.toDto() = map(LøsningParagraf_11_5::toDto)
    }

    internal fun vurdertAv() = vurdertAv

    internal fun vurderNedsattArbeidsevne(vilkår: Paragraf_11_5.SøknadMottatt, vilkårsvurdering: Paragraf_11_5) {
        vilkår.vurderNedsattArbeidsevne(vilkårsvurdering, this, nedsattArbeidsevnegrad)
    }

    internal fun toDto() = nedsattArbeidsevnegrad.toDto(løsningId, vurdertAv, tidspunktForVurdering)
}

internal class KvalitetssikringParagraf_11_5(
    private val kvalitetssikringId: UUID, 
    private val løsningId: UUID, 
    private val kvalitetssikretAv: String,
    private val tidspunktForKvalitetssikring: LocalDateTime,
    private val erGodkjent: Boolean,
    private val begrunnelse: String
) : Hendelse() {

    internal companion object {
        internal fun Iterable<KvalitetssikringParagraf_11_5>.toDto() = map(KvalitetssikringParagraf_11_5::toDto)
    }

    internal fun erGodkjent() = erGodkjent
    internal fun kvalitetssikretAv() = kvalitetssikretAv
    internal fun toDto() = KvalitetssikringParagraf_11_5ModellApi(
        kvalitetssikringId = kvalitetssikringId,
        løsningId = løsningId,
        kvalitetssikretAv = kvalitetssikretAv,
        tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
        erGodkjent = erGodkjent,
        begrunnelse = begrunnelse
    )
}

