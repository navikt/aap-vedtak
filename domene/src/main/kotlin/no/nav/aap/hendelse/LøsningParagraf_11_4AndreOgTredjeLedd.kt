package no.nav.aap.hendelse

import no.nav.aap.dto.DtoKvalitetssikringParagraf_11_4AndreOgTredjeLedd
import no.nav.aap.dto.DtoLøsningParagraf_11_4AndreOgTredjeLedd
import java.time.LocalDateTime

internal class LøsningParagraf_11_4AndreOgTredjeLedd(
    private val vurdertAv: String,
    private val tidspunktForVurdering: LocalDateTime,
    private val erOppfylt: Boolean
) : Hendelse() {

    internal companion object {
        internal fun Iterable<LøsningParagraf_11_4AndreOgTredjeLedd>.toDto() =
            map(LøsningParagraf_11_4AndreOgTredjeLedd::toDto)
    }

    internal fun vurdertAv() = vurdertAv
    internal fun erManueltOppfylt() = erOppfylt

    private fun toDto() = DtoLøsningParagraf_11_4AndreOgTredjeLedd(
        vurdertAv = vurdertAv,
        tidspunktForVurdering = tidspunktForVurdering,
        erOppfylt = erOppfylt
    )
}

class KvalitetssikringParagraf_11_4AndreOgTredjeLedd(
    private val kvalitetssikretAv: String,
    private val tidspunktForKvalitetssikring: LocalDateTime,
    private val erGodkjent: Boolean,
    private val begrunnelse: String
) : Hendelse() {

    internal companion object {
        internal fun Iterable<KvalitetssikringParagraf_11_4AndreOgTredjeLedd>.toDto() =
            map(KvalitetssikringParagraf_11_4AndreOgTredjeLedd::toDto)
    }

    internal fun erGodkjent() = erGodkjent
    internal fun kvalitetssikretAv() = kvalitetssikretAv
    internal fun toDto() = DtoKvalitetssikringParagraf_11_4AndreOgTredjeLedd(
        kvalitetssikretAv,
        tidspunktForKvalitetssikring,
        erGodkjent,
        begrunnelse
    )
}

