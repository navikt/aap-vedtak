package no.nav.aap.modellapi

import no.nav.aap.domene.Søker
import no.nav.aap.domene.vilkår.Vilkårsvurdering
import no.nav.aap.hendelse.*
import java.time.LocalDateTime
import java.util.*

data class LøsningParagraf_11_5ModellApi(
    val løsningId: UUID,
    val vurdertAv: String,
    val tidspunktForVurdering: LocalDateTime,
    val kravOmNedsattArbeidsevneErOppfylt: Boolean,
    val nedsettelseSkyldesSykdomEllerSkade: Boolean
) {

    constructor(
        vurdertAv: String,
        tidspunktForVurdering: LocalDateTime,
        kravOmNedsattArbeidsevneErOppfylt: Boolean,
        nedsettelseSkyldesSykdomEllerSkade: Boolean
    ) : this(
        løsningId = UUID.randomUUID(),
        vurdertAv = vurdertAv,
        tidspunktForVurdering = tidspunktForVurdering,
        kravOmNedsattArbeidsevneErOppfylt = kravOmNedsattArbeidsevneErOppfylt,
        nedsettelseSkyldesSykdomEllerSkade = nedsettelseSkyldesSykdomEllerSkade
    )

    fun håndter(søker: SøkerModellApi): Pair<SøkerModellApi, List<Behov>> {
        val modellSøker = Søker.gjenopprett(søker)
        val løsning = toLøsning()
        modellSøker.håndterLøsning(løsning, Vilkårsvurdering<*>::håndterLøsning)
        return modellSøker.toDto() to løsning.behov()
    }

    private fun toLøsning() = LøsningParagraf_11_5(
        løsningId = løsningId,
        vurdertAv = vurdertAv,
        tidspunktForVurdering = tidspunktForVurdering,
        nedsattArbeidsevnegrad = LøsningParagraf_11_5.NedsattArbeidsevnegrad(
            kravOmNedsattArbeidsevneErOppfylt = kravOmNedsattArbeidsevneErOppfylt,
            nedsettelseSkyldesSykdomEllerSkade = nedsettelseSkyldesSykdomEllerSkade,
        )
    )
}

data class KvalitetssikringParagraf_11_5ModellApi(
    val kvalitetssikringId: UUID,
    val løsningId: UUID,
    val kvalitetssikretAv: String,
    val tidspunktForKvalitetssikring: LocalDateTime,
    val erGodkjent: Boolean,
    val begrunnelse: String
) {

    constructor(
        løsningId: UUID,
        kvalitetssikretAv: String,
        tidspunktForKvalitetssikring: LocalDateTime,
        erGodkjent: Boolean,
        begrunnelse: String
    ) : this(
        kvalitetssikringId = UUID.randomUUID(),
        løsningId = løsningId,
        kvalitetssikretAv = kvalitetssikretAv,
        tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
        erGodkjent = erGodkjent,
        begrunnelse = begrunnelse
    )

    fun håndter(søker: SøkerModellApi): Pair<SøkerModellApi, List<Behov>> {
        val modellSøker = Søker.gjenopprett(søker)
        val kvalitetssikring = toKvalitetssikring()
        modellSøker.håndterKvalitetssikring(kvalitetssikring, Vilkårsvurdering<*>::håndterKvalitetssikring)
        return modellSøker.toDto() to kvalitetssikring.behov()
    }

    private fun toKvalitetssikring() = KvalitetssikringParagraf_11_5(
        kvalitetssikringId = kvalitetssikringId,
        løsningId = løsningId,
        kvalitetssikretAv = kvalitetssikretAv,
        tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
        erGodkjent = erGodkjent,
        begrunnelse = begrunnelse
    )
}

data class LøsningParagraf_11_5YrkesskadeModellApi(
    val løsningId: UUID,
    val vurdertAv: String,
    val tidspunktForVurdering: LocalDateTime,
    val arbeidsevneErNedsattMedMinst50Prosent: Boolean,
    val arbeidsevneErNedsattMedMinst30Prosent: Boolean
) {

    fun håndter(søker: SøkerModellApi): Pair<SøkerModellApi, List<Behov>> {
        val modellSøker = Søker.gjenopprett(søker)
        val løsning = toLøsning()
        modellSøker.håndterLøsning(løsning, Vilkårsvurdering<*>::håndterLøsning)
        return modellSøker.toDto() to løsning.behov()
    }

    private fun toLøsning() = LøsningParagraf_11_5Yrkesskade(
        løsningId = løsningId,
        vurdertAv = vurdertAv,
        tidspunktForVurdering = tidspunktForVurdering,
        arbeidsevneErNedsattMedMinst50Prosent = arbeidsevneErNedsattMedMinst50Prosent,
        arbeidsevneErNedsattMedMinst30Prosent = arbeidsevneErNedsattMedMinst30Prosent
    )
}

data class KvalitetssikringParagraf_11_5YrkesskadeModellApi(
    val kvalitetssikringId: UUID,
    val løsningId: UUID,
    val kvalitetssikretAv: String,
    val tidspunktForKvalitetssikring: LocalDateTime,
    val erGodkjent: Boolean,
    val begrunnelse: String
) {

    fun håndter(søker: SøkerModellApi): Pair<SøkerModellApi, List<Behov>> {
        val modellSøker = Søker.gjenopprett(søker)
        val kvalitetssikring = toKvalitetssikring()
        modellSøker.håndterKvalitetssikring(kvalitetssikring, Vilkårsvurdering<*>::håndterKvalitetssikring)
        return modellSøker.toDto() to kvalitetssikring.behov()
    }

    private fun toKvalitetssikring() = KvalitetssikringParagraf_11_5Yrkesskade(
        kvalitetssikringId = kvalitetssikringId,
        løsningId = løsningId,
        kvalitetssikretAv = kvalitetssikretAv,
        tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
        erGodkjent = erGodkjent,
        begrunnelse = begrunnelse
    )
}