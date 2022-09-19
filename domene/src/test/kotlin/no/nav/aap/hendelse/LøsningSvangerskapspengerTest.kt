package no.nav.aap.hendelse

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class LøsningSvangerskapspengerTest {

    @Test
    fun `Dersom en sak ikke har startet behandling enda, og man mottar at bruker er på svangerskapspenger, skal alle villkår settes til ikke relevant og aap avslås`() {

    }

    @Test
    fun `Dersom en sak er under behandling og man mottar at bruker har fått svangerskapspenger, skal alle vilkår settes til ikke relevant og aap avslås`() {

    }

    @Test
    fun `Dersom vedtak er iverksat og man mottar at bruker har fått svangerskapspenger, skal det sendes et midlertidig stans-vedtak av AAP`() {

    }

    // TODO
    /*
        - Er det riktig å sette vilkår til ikke-relevant? Funker det med å sjekke erOppfylt?
        - Skal det være mulig å "overstyre", dvs at saksbehandler skal kunne si at vedtak om sv.penger er feil?
        - Må denne håndteringen settes på alle tilstander på alle vilkår, eller kan noe av dette generaliseres?
     */

}