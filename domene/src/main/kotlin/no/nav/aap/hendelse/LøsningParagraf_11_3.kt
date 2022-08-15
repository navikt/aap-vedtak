package no.nav.aap.hendelse

import no.nav.aap.dto.DtoKvalitetssikringParagraf_11_3
import no.nav.aap.dto.DtoLøsningParagraf_11_3
import java.time.LocalDateTime
import java.util.*

internal class LøsningParagraf_11_3(
    private val løsningId: UUID,
    private val vurdertAv: String,
    private val tidspunktForVurdering: LocalDateTime,
    private val erOppfylt: Boolean,
) : Hendelse() {

    internal companion object {
        internal fun Iterable<LøsningParagraf_11_3>.toDto() = map(LøsningParagraf_11_3::toDto)
    }

    internal fun vurdertAv() = vurdertAv
    internal fun erManueltOppfylt() = erOppfylt

    internal fun toDto() = DtoLøsningParagraf_11_3(løsningId, vurdertAv, tidspunktForVurdering, erOppfylt)
}

class KvalitetssikringParagraf_11_3(
    private val kvalitetssikringId: UUID, 
    private val løsningId: UUID, 
    private val kvalitetssikretAv: String,
    private val tidspunktForKvalitetssikring: LocalDateTime,
    private val erGodkjent: Boolean,
    private val begrunnelse: String,
) : Hendelse() {

    internal companion object {
        internal fun Iterable<KvalitetssikringParagraf_11_3>.toDto() = map(KvalitetssikringParagraf_11_3::toDto)
    }

    internal fun erGodkjent() = erGodkjent
    internal fun kvalitetssikretAv() = kvalitetssikretAv
    internal fun toDto() = DtoKvalitetssikringParagraf_11_3(
        kvalitetssikringId = kvalitetssikringId,
        løsningId = løsningId,
        kvalitetssikretAv = kvalitetssikretAv,
        tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
        erGodkjent = erGodkjent,
        begrunnelse = begrunnelse
    )
}
