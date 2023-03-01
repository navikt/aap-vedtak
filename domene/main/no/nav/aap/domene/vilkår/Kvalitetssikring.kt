package no.nav.aap.domene.vilkår

import no.nav.aap.modellapi.KvalitetssikringModellApi
import java.util.*

internal interface Kvalitetssikring<LØSNING, KVALITETSSIKRING>
        where LØSNING : Løsning<LØSNING, KVALITETSSIKRING>,
              KVALITETSSIKRING : Kvalitetssikring<LØSNING, KVALITETSSIKRING> {
    fun matchMedLøsning(totrinnskontroll: Totrinnskontroll<LØSNING, KVALITETSSIKRING>, løsningId: UUID)

    fun erGodkjent(): Boolean

    fun toDto(): KvalitetssikringModellApi
}
