package no.nav.aap.domene.vilkår

import no.nav.aap.domene.visitor.VilkårsvurderingVisitor
import no.nav.aap.modellapi.LøsningModellApi

internal interface Løsning<LØSNING, KVALITETSSIKRING>
        where LØSNING : Løsning<LØSNING, KVALITETSSIKRING>,
              KVALITETSSIKRING : Kvalitetssikring<LØSNING, KVALITETSSIKRING> {
    fun matchMedKvalitetssikring(
        totrinnskontroll: Totrinnskontroll<LØSNING, KVALITETSSIKRING>,
        kvalitetssikring: KVALITETSSIKRING
    )

    fun accept(visitor: VilkårsvurderingVisitor) {}

    fun toDto(): LøsningModellApi
}
