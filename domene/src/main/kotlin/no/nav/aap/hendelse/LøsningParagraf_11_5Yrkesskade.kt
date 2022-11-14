package no.nav.aap.hendelse

import no.nav.aap.domene.vilkår.Kvalitetssikring
import no.nav.aap.domene.vilkår.Løsning
import no.nav.aap.domene.vilkår.Totrinnskontroll
import no.nav.aap.modellapi.KvalitetssikringParagraf_11_5YrkesskadeModellApi
import no.nav.aap.modellapi.LøsningParagraf_11_5YrkesskadeModellApi
import java.time.LocalDateTime
import java.util.*

internal class LøsningParagraf_11_5Yrkesskade(
    private val løsningId: UUID,
    private val vurdertAv: String,
    private val tidspunktForVurdering: LocalDateTime,
    private val arbeidsevneErNedsattMedMinst50Prosent: Boolean,
    private val arbeidsevneErNedsattMedMinst30Prosent: Boolean
) : Hendelse(), Løsning<LøsningParagraf_11_5Yrkesskade, KvalitetssikringParagraf_11_5Yrkesskade> {

    internal companion object {
        internal fun Iterable<LøsningParagraf_11_5Yrkesskade>.toDto() = map(LøsningParagraf_11_5Yrkesskade::toDto)
    }

    override fun matchMedKvalitetssikring(
        totrinnskontroll: Totrinnskontroll<LøsningParagraf_11_5Yrkesskade, KvalitetssikringParagraf_11_5Yrkesskade>,
        kvalitetssikring: KvalitetssikringParagraf_11_5Yrkesskade
    ) {
        kvalitetssikring.matchMedLøsning(totrinnskontroll, løsningId)
    }

    internal fun vurdertAv() = vurdertAv
    internal fun erNedsattMedMinst30Prosent() = arbeidsevneErNedsattMedMinst30Prosent

    override fun toDto() = LøsningParagraf_11_5YrkesskadeModellApi(
        løsningId = løsningId,
        vurdertAv = vurdertAv,
        tidspunktForVurdering = tidspunktForVurdering,
        arbeidsevneErNedsattMedMinst50Prosent = arbeidsevneErNedsattMedMinst50Prosent,
        arbeidsevneErNedsattMedMinst30Prosent = arbeidsevneErNedsattMedMinst30Prosent
    )
}

internal class KvalitetssikringParagraf_11_5Yrkesskade(
    private val kvalitetssikringId: UUID,
    private val løsningId: UUID,
    private val kvalitetssikretAv: String,
    private val tidspunktForKvalitetssikring: LocalDateTime,
    private val erGodkjent: Boolean,
    private val begrunnelse: String?,
) : Hendelse(), Kvalitetssikring<LøsningParagraf_11_5Yrkesskade, KvalitetssikringParagraf_11_5Yrkesskade> {

    internal companion object {
        internal fun Iterable<KvalitetssikringParagraf_11_5Yrkesskade>.toDto() =
            map(KvalitetssikringParagraf_11_5Yrkesskade::toDto)
    }

    override fun matchMedLøsning(
        totrinnskontroll: Totrinnskontroll<LøsningParagraf_11_5Yrkesskade, KvalitetssikringParagraf_11_5Yrkesskade>,
        løsningId: UUID
    ) {
        if(this.løsningId == løsningId){
            totrinnskontroll.leggTilKvalitetssikring(this)
        }
    }

    internal fun erGodkjent() = erGodkjent
    internal fun kvalitetssikretAv() = kvalitetssikretAv
    override fun toDto() = KvalitetssikringParagraf_11_5YrkesskadeModellApi(
        kvalitetssikringId = kvalitetssikringId,
        løsningId = løsningId,
        kvalitetssikretAv = kvalitetssikretAv,
        tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
        erGodkjent = erGodkjent,
        begrunnelse = begrunnelse
    )
}

