package no.nav.aap.dto

import no.nav.aap.domene.Søker
import no.nav.aap.domene.beregning.Arbeidsgiver
import no.nav.aap.domene.beregning.Beløp.Companion.beløp
import no.nav.aap.domene.beregning.Inntekt
import no.nav.aap.domene.entitet.Personident
import no.nav.aap.domene.vilkår.Vilkårsvurdering
import no.nav.aap.hendelse.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Year
import java.time.YearMonth
import java.util.*

enum class Utfall {
    OPPFYLT, IKKE_OPPFYLT, IKKE_VURDERT, IKKE_RELEVANT
}

data class SøkerModellApi(
    val personident: String,
    val fødselsdato: LocalDate,
    val saker: List<SakModellApi>
)

data class SakModellApi(
    val saksid: UUID,
    val tilstand: String,
    val sakstyper: List<SakstypeModellApi>,
    val vurderingsdato: LocalDate,
    val søknadstidspunkt: LocalDateTime,
    val vedtak: VedtakModellApi?
)

data class SakstypeModellApi(
    val type: String,
    val aktiv: Boolean,
    val vilkårsvurderinger: List<VilkårsvurderingModellApi>
)

data class IverksettelseAvVedtakModellApi(
    val iverksattAv: String,
) {
    fun håndter(søker: Søker): List<Behov> {
        val løsning = toLøsning()
        søker.håndterIverksettelse(løsning)
        return løsning.behov()
    }

    private fun toLøsning() = IverksettelseAvVedtak(
        iverksattAv = iverksattAv,
    )
}

data class SykepengedagerModellApi(
    val gjenståendeSykedager: Int,
    val foreløpigBeregnetSluttPåSykepenger: LocalDate,
    val kilde: String,
) {
    fun håndter(søker: Søker): List<Behov> {
        val løsning = toLøsning()
        søker.håndterLøsning(løsning)
        return løsning.behov()
    }

    private fun toLøsning() = LøsningSykepengedager(
        personident = Personident("slette?"),
        gjenståendeSykedager = gjenståendeSykedager,
        foreløpigBeregnetSluttPåSykepenger = foreløpigBeregnetSluttPåSykepenger,
        kilde = LøsningSykepengedager.Kilde.valueOf(kilde)
    )
}

data class VilkårsvurderingModellApi(
    val vilkårsvurderingsid: UUID,
    val vurdertAv: String?,
    val kvalitetssikretAv: String?,
    val paragraf: String,
    val ledd: List<String>,
    val tilstand: String,
    val utfall: Utfall,
    val vurdertMaskinelt: Boolean,
    val løsning_medlemskap_yrkesskade_maskinell: List<LøsningMaskinellMedlemskapYrkesskadeModellApi>? = null,
    val løsning_medlemskap_yrkesskade_manuell: List<LøsningManuellMedlemskapYrkesskadeModellApi>? = null,
    val løsning_11_2_maskinell: List<LøsningMaskinellParagraf_11_2ModellApi>? = null,
    val løsning_11_2_manuell: List<LøsningParagraf_11_2ModellApi>? = null,
    val løsning_11_3_manuell: List<LøsningParagraf_11_3ModellApi>? = null,
    val løsning_11_4_ledd2_ledd3_manuell: List<LøsningParagraf_11_4AndreOgTredjeLeddModellApi>? = null,
    val løsning_11_5_manuell: List<LøsningParagraf_11_5ModellApi>? = null,
    val løsning_11_5_yrkesskade_manuell: List<LøsningParagraf_11_5YrkesskadeModellApi>? = null,
    val løsning_11_6_manuell: List<LøsningParagraf_11_6ModellApi>? = null,
    val løsning_11_12_ledd1_manuell: List<LøsningParagraf_11_12FørsteLeddModellApi>? = null,
    val løsning_11_19_manuell: List<LøsningParagraf_11_19ModellApi>? = null,
    val løsning_11_22_manuell: List<LøsningParagraf_11_22ModellApi>? = null,
    val løsning_11_29_manuell: List<LøsningParagraf_11_29ModellApi>? = null,
    val kvalitetssikringer_medlemskap_yrkesskade: List<KvalitetssikringMedlemskapYrkesskadeModellApi>? = null,
    val kvalitetssikringer_11_2: List<KvalitetssikringParagraf_11_2ModellApi>? = null,
    val kvalitetssikringer_11_3: List<KvalitetssikringParagraf_11_3ModellApi>? = null,
    val kvalitetssikringer_11_4_ledd2_ledd3: List<KvalitetssikringParagraf_11_4AndreOgTredjeLeddModellApi>? = null,
    val kvalitetssikringer_11_5: List<KvalitetssikringParagraf_11_5ModellApi>? = null,
    val kvalitetssikringer_11_5_yrkesskade: List<KvalitetssikringParagraf_11_5YrkesskadeModellApi>? = null,
    val kvalitetssikringer_11_6: List<KvalitetssikringParagraf_11_6ModellApi>? = null,
    val kvalitetssikringer_11_12_ledd1: List<KvalitetssikringParagraf_11_12FørsteLeddModellApi>? = null,
    val kvalitetssikringer_11_19: List<KvalitetssikringParagraf_11_19ModellApi>? = null,
    val kvalitetssikringer_11_22: List<KvalitetssikringParagraf_11_22ModellApi>? = null,
    val kvalitetssikringer_11_29: List<KvalitetssikringParagraf_11_29ModellApi>? = null,
)

data class LøsningMaskinellMedlemskapYrkesskadeModellApi(
    val løsningId: UUID,
    val erMedlem: String
) {
    fun håndter(søker: Søker): List<Behov> {
        val løsning = toLøsning()
        søker.håndterLøsning(løsning, Vilkårsvurdering<*>::håndterLøsning)
        return løsning.behov()
    }

    private fun toLøsning() = LøsningMaskinellMedlemskapYrkesskade(løsningId, enumValueOf(erMedlem.uppercase()))
}

data class LøsningManuellMedlemskapYrkesskadeModellApi(
    val løsningId: UUID,
    val vurdertAv: String,
    val tidspunktForVurdering: LocalDateTime,
    val erMedlem: String
) {
    fun håndter(søker: Søker): List<Behov> {
        val løsning = toLøsning()
        søker.håndterLøsning(løsning, Vilkårsvurdering<*>::håndterLøsning)
        return løsning.behov()
    }

    private fun toLøsning() =
        LøsningManuellMedlemskapYrkesskade(
            løsningId,
            vurdertAv,
            tidspunktForVurdering,
            enumValueOf(erMedlem.uppercase())
        )
}

data class KvalitetssikringMedlemskapYrkesskadeModellApi(
    val kvalitetssikringId: UUID,
    val løsningId: UUID,
    val kvalitetssikretAv: String,
    val tidspunktForKvalitetssikring: LocalDateTime,
    val erGodkjent: Boolean,
    val begrunnelse: String
) {

    fun håndter(søker: Søker): List<Behov> {
        val kvalitetssikring = toKvalitetssikring()
        søker.håndterKvalitetssikring(kvalitetssikring, Vilkårsvurdering<*>::håndterKvalitetssikring)
        return kvalitetssikring.behov()
    }

    private fun toKvalitetssikring() = KvalitetssikringMedlemskapYrkesskade(
        kvalitetssikringId = kvalitetssikringId,
        løsningId = løsningId,
        kvalitetssikretAv = kvalitetssikretAv,
        tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
        erGodkjent = erGodkjent,
        begrunnelse = begrunnelse
    )
}

data class LøsningParagraf_11_2ModellApi(
    val løsningId: UUID,
    val vurdertAv: String,
    val tidspunktForVurdering: LocalDateTime,
    val erMedlem: String
) {

    constructor(
        vurdertAv: String,
        tidspunktForVurdering: LocalDateTime,
        erMedlem: String
    ) : this(
        løsningId = UUID.randomUUID(),
        vurdertAv = vurdertAv,
        tidspunktForVurdering = tidspunktForVurdering,
        erMedlem = erMedlem
    )

    fun håndter(søker: Søker): List<Behov> {
        val løsning = toLøsning()
        søker.håndterLøsning(løsning, Vilkårsvurdering<*>::håndterLøsning)
        return løsning.behov()
    }

    private fun toLøsning() = LøsningManuellParagraf_11_2(
        løsningId = løsningId,
        vurdertAv = vurdertAv,
        tidspunktForVurdering = tidspunktForVurdering,
        erMedlem = if (erMedlem.lowercase() in listOf("true", "ja"))
            LøsningManuellParagraf_11_2.ErMedlem.JA else LøsningManuellParagraf_11_2.ErMedlem.NEI
    )
}

data class KvalitetssikringParagraf_11_2ModellApi(
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

    fun håndter(søker: Søker): List<Behov> {
        val kvalitetssikring = toKvalitetssikring()
        søker.håndterKvalitetssikring(kvalitetssikring, Vilkårsvurdering<*>::håndterKvalitetssikring)
        return kvalitetssikring.behov()
    }

    private fun toKvalitetssikring() = KvalitetssikringParagraf_11_2(
        kvalitetssikringId = kvalitetssikringId,
        løsningId = løsningId,
        kvalitetssikretAv = kvalitetssikretAv,
        tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
        erGodkjent = erGodkjent,
        begrunnelse = begrunnelse
    )
}

data class LøsningMaskinellParagraf_11_2ModellApi(
    val løsningId: UUID,
    val tidspunktForVurdering: LocalDateTime,
    val erMedlem: String
) {

    constructor(
        erMedlem: String
    ) : this(
        løsningId = UUID.randomUUID(),
        tidspunktForVurdering = LocalDateTime.now(),
        erMedlem = erMedlem
    )

    fun håndter(søker: Søker): List<Behov> {
        val løsning = toLøsning()
        søker.håndterLøsning(løsning, Vilkårsvurdering<*>::håndterLøsning)
        return løsning.behov()
    }

    private fun toLøsning() =
        LøsningMaskinellParagraf_11_2(løsningId, tidspunktForVurdering, enumValueOf(erMedlem.uppercase()))
}

data class LøsningParagraf_11_3ModellApi(
    val løsningId: UUID,
    val vurdertAv: String,
    val tidspunktForVurdering: LocalDateTime,
    val erOppfylt: Boolean
) {

    constructor(
        vurdertAv: String,
        tidspunktForVurdering: LocalDateTime,
        erOppfylt: Boolean
    ) : this(
        løsningId = UUID.randomUUID(),
        vurdertAv = vurdertAv,
        tidspunktForVurdering = tidspunktForVurdering,
        erOppfylt = erOppfylt
    )

    fun håndter(søker: Søker): List<Behov> {
        val løsning = toLøsning()
        søker.håndterLøsning(løsning, Vilkårsvurdering<*>::håndterLøsning)
        return løsning.behov()
    }

    private fun toLøsning() = LøsningParagraf_11_3(løsningId, vurdertAv, tidspunktForVurdering, erOppfylt)
}

data class KvalitetssikringParagraf_11_3ModellApi(
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

    fun håndter(søker: Søker): List<Behov> {
        val kvalitetssikring = toKvalitetssikring()
        søker.håndterKvalitetssikring(kvalitetssikring, Vilkårsvurdering<*>::håndterKvalitetssikring)
        return kvalitetssikring.behov()
    }

    private fun toKvalitetssikring() = KvalitetssikringParagraf_11_3(
        kvalitetssikringId = kvalitetssikringId,
        løsningId = løsningId,
        kvalitetssikretAv = kvalitetssikretAv,
        tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
        erGodkjent = erGodkjent,
        begrunnelse = begrunnelse
    )
}

data class LøsningParagraf_11_4AndreOgTredjeLeddModellApi(
    val løsningId: UUID,
    val vurdertAv: String,
    val tidspunktForVurdering: LocalDateTime,
    val erOppfylt: Boolean
) {

    constructor(
        vurdertAv: String,
        tidspunktForVurdering: LocalDateTime,
        erOppfylt: Boolean
    ) : this(
        løsningId = UUID.randomUUID(),
        vurdertAv = vurdertAv,
        tidspunktForVurdering = tidspunktForVurdering,
        erOppfylt = erOppfylt
    )

    fun håndter(søker: Søker): List<Behov> {
        val løsning = toLøsning()
        søker.håndterLøsning(løsning, Vilkårsvurdering<*>::håndterLøsning)
        return løsning.behov()
    }

    private fun toLøsning() =
        LøsningParagraf_11_4AndreOgTredjeLedd(løsningId, vurdertAv, tidspunktForVurdering, erOppfylt)
}

data class KvalitetssikringParagraf_11_4AndreOgTredjeLeddModellApi(
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

    fun håndter(søker: Søker): List<Behov> {
        val kvalitetssikring = toKvalitetssikring()
        søker.håndterKvalitetssikring(kvalitetssikring, Vilkårsvurdering<*>::håndterKvalitetssikring)
        return kvalitetssikring.behov()
    }

    private fun toKvalitetssikring() = KvalitetssikringParagraf_11_4AndreOgTredjeLedd(
        kvalitetssikringId = kvalitetssikringId,
        løsningId = løsningId,
        kvalitetssikretAv = kvalitetssikretAv,
        tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
        erGodkjent = erGodkjent,
        begrunnelse = begrunnelse
    )
}

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

    fun håndter(søker: Søker): List<Behov> {
        val løsning = toLøsning()
        søker.håndterLøsning(løsning, Vilkårsvurdering<*>::håndterLøsning)
        return løsning.behov()
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

    fun håndter(søker: Søker): List<Behov> {
        val kvalitetssikring = toKvalitetssikring()
        søker.håndterKvalitetssikring(kvalitetssikring, Vilkårsvurdering<*>::håndterKvalitetssikring)
        return kvalitetssikring.behov()
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
    fun håndter(søker: Søker): List<Behov> {
        val løsning = toLøsning()
        søker.håndterLøsning(løsning, Vilkårsvurdering<*>::håndterLøsning)
        return løsning.behov()
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

    fun håndter(søker: Søker): List<Behov> {
        val kvalitetssikring = toKvalitetssikring()
        søker.håndterKvalitetssikring(kvalitetssikring, Vilkårsvurdering<*>::håndterKvalitetssikring)
        return kvalitetssikring.behov()
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

data class LøsningParagraf_11_6ModellApi(
    val løsningId: UUID,
    val vurdertAv: String,
    val tidspunktForVurdering: LocalDateTime,
    val harBehovForBehandling: Boolean,
    val harBehovForTiltak: Boolean,
    val harMulighetForÅKommeIArbeid: Boolean
) {

    constructor(
        vurdertAv: String,
        tidspunktForVurdering: LocalDateTime,
        harBehovForBehandling: Boolean,
        harBehovForTiltak: Boolean,
        harMulighetForÅKommeIArbeid: Boolean
    ) : this(
        løsningId = UUID.randomUUID(),
        vurdertAv = vurdertAv,
        tidspunktForVurdering = tidspunktForVurdering,
        harBehovForBehandling = harBehovForBehandling,
        harBehovForTiltak = harBehovForTiltak,
        harMulighetForÅKommeIArbeid = harMulighetForÅKommeIArbeid
    )

    fun håndter(søker: Søker): List<Behov> {
        val løsning = toLøsning()
        søker.håndterLøsning(løsning, Vilkårsvurdering<*>::håndterLøsning)
        return løsning.behov()
    }

    private fun toLøsning() = LøsningParagraf_11_6(
        løsningId = løsningId,
        vurdertAv = vurdertAv,
        tidspunktForVurdering = tidspunktForVurdering,
        harBehovForBehandling = harBehovForBehandling,
        harBehovForTiltak = harBehovForTiltak,
        harMulighetForÅKommeIArbeid = harMulighetForÅKommeIArbeid
    )
}

data class KvalitetssikringParagraf_11_6ModellApi(
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

    fun håndter(søker: Søker): List<Behov> {
        val kvalitetssikring = toKvalitetssikring()
        søker.håndterKvalitetssikring(kvalitetssikring, Vilkårsvurdering<*>::håndterKvalitetssikring)
        return kvalitetssikring.behov()
    }

    private fun toKvalitetssikring() = KvalitetssikringParagraf_11_6(
        kvalitetssikringId = kvalitetssikringId,
        løsningId = løsningId,
        kvalitetssikretAv = kvalitetssikretAv,
        tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
        erGodkjent = erGodkjent,
        begrunnelse = begrunnelse
    )
}

data class LøsningParagraf_11_12FørsteLeddModellApi(
    val løsningId: UUID,
    val vurdertAv: String,
    val tidspunktForVurdering: LocalDateTime,
    val bestemmesAv: String,
    val unntak: String,
    val unntaksbegrunnelse: String,
    val manueltSattVirkningsdato: LocalDate?,
) {

    constructor(
        vurdertAv: String,
        tidspunktForVurdering: LocalDateTime,
        bestemmesAv: String,
        unntak: String,
        unntaksbegrunnelse: String,
        manueltSattVirkningsdato: LocalDate
    ) : this(
        løsningId = UUID.randomUUID(),
        vurdertAv = vurdertAv,
        tidspunktForVurdering = tidspunktForVurdering,
        bestemmesAv = bestemmesAv,
        unntak = unntak,
        unntaksbegrunnelse = unntaksbegrunnelse,
        manueltSattVirkningsdato = manueltSattVirkningsdato
    )

    fun håndter(søker: Søker): List<Behov> {
        val løsning = toLøsning()
        søker.håndterLøsning(løsning, Vilkårsvurdering<*>::håndterLøsning)
        return løsning.behov()
    }

    private fun toLøsning() = LøsningParagraf_11_12FørsteLedd(
        løsningId = løsningId,
        vurdertAv = vurdertAv,
        tidspunktForVurdering = tidspunktForVurdering,
        bestemmesAv = when {
            bestemmesAv == "soknadstidspunkt" -> LøsningParagraf_11_12FørsteLedd.BestemmesAv.soknadstidspunkt
            bestemmesAv == "maksdatoSykepenger" -> LøsningParagraf_11_12FørsteLedd.BestemmesAv.maksdatoSykepenger
            bestemmesAv == "ermiraSays" -> LøsningParagraf_11_12FørsteLedd.BestemmesAv.ermiraSays
            bestemmesAv == "unntaksvurdering" && unntak == "forhindret" -> LøsningParagraf_11_12FørsteLedd.BestemmesAv.unntaksvurderingForhindret
            bestemmesAv == "unntaksvurdering" && unntak == "mangelfull" -> LøsningParagraf_11_12FørsteLedd.BestemmesAv.unntaksvurderingMangelfull
            bestemmesAv == "etterSisteLoenn" -> LøsningParagraf_11_12FørsteLedd.BestemmesAv.etterSisteLoenn
            else -> error("Ukjent bestemmesAv: $bestemmesAv og unntak: $unntak")
        },
        unntak = unntak,
        unntaksbegrunnelse = unntaksbegrunnelse,
        manueltSattVirkningsdato = manueltSattVirkningsdato
    )
}

data class KvalitetssikringParagraf_11_12FørsteLeddModellApi(
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

    fun håndter(søker: Søker): List<Behov> {
        val kvalitetssikring = toKvalitetssikring()
        søker.håndterKvalitetssikring(kvalitetssikring, Vilkårsvurdering<*>::håndterKvalitetssikring)
        return kvalitetssikring.behov()
    }

    private fun toKvalitetssikring() = KvalitetssikringParagraf_11_12FørsteLedd(
        kvalitetssikringId = kvalitetssikringId,
        løsningId = løsningId,
        kvalitetssikretAv = kvalitetssikretAv,
        tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
        erGodkjent = erGodkjent,
        begrunnelse = begrunnelse
    )
}

data class LøsningParagraf_11_22ModellApi(
    val løsningId: UUID,
    val vurdertAv: String,
    val tidspunktForVurdering: LocalDateTime,
    val erOppfylt: Boolean,
    val andelNedsattArbeidsevne: Int,
    val år: Year,
    val antattÅrligArbeidsinntekt: Double
) {
    fun håndter(søker: Søker): List<Behov> {
        val løsning = toLøsning()
        søker.håndterLøsning(løsning, Vilkårsvurdering<*>::håndterLøsning)
        return løsning.behov()
    }

    private fun toLøsning() = LøsningParagraf_11_22(
        løsningId = løsningId,
        vurdertAv = vurdertAv,
        tidspunktForVurdering = tidspunktForVurdering,
        erOppfylt = erOppfylt,
        andelNedsattArbeidsevne = andelNedsattArbeidsevne,
        år = år,
        antattÅrligArbeidsinntekt = antattÅrligArbeidsinntekt.beløp
    )
}

data class KvalitetssikringParagraf_11_22ModellApi(
    val kvalitetssikringId: UUID,
    val løsningId: UUID,
    val kvalitetssikretAv: String,
    val tidspunktForKvalitetssikring: LocalDateTime,
    val erGodkjent: Boolean,
    val begrunnelse: String
) {

    fun håndter(søker: Søker): List<Behov> {
        val kvalitetssikring = toKvalitetssikring()
        søker.håndterKvalitetssikring(kvalitetssikring, Vilkårsvurdering<*>::håndterKvalitetssikring)
        return kvalitetssikring.behov()
    }

    private fun toKvalitetssikring() = KvalitetssikringParagraf_11_22(
        kvalitetssikringId = kvalitetssikringId,
        løsningId = løsningId,
        kvalitetssikretAv = kvalitetssikretAv,
        tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
        erGodkjent = erGodkjent,
        begrunnelse = begrunnelse
    )
}

data class LøsningParagraf_11_29ModellApi(
    val løsningId: UUID,
    val vurdertAv: String,
    val tidspunktForVurdering: LocalDateTime,
    val erOppfylt: Boolean
) {

    constructor(
        vurdertAv: String,
        tidspunktForVurdering: LocalDateTime,
        erOppfylt: Boolean
    ) : this(
        løsningId = UUID.randomUUID(),
        vurdertAv = vurdertAv,
        tidspunktForVurdering = tidspunktForVurdering,
        erOppfylt = erOppfylt
    )

    fun håndter(søker: Søker): List<Behov> {
        val løsning = toLøsning()
        søker.håndterLøsning(løsning, Vilkårsvurdering<*>::håndterLøsning)
        return løsning.behov()
    }

    private fun toLøsning() = LøsningParagraf_11_29(løsningId, vurdertAv, tidspunktForVurdering, erOppfylt)
}

data class KvalitetssikringParagraf_11_29ModellApi(
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

    fun håndter(søker: Søker): List<Behov> {
        val kvalitetssikring = toKvalitetssikring()
        søker.håndterKvalitetssikring(kvalitetssikring, Vilkårsvurdering<*>::håndterKvalitetssikring)
        return kvalitetssikring.behov()
    }

    private fun toKvalitetssikring() = KvalitetssikringParagraf_11_29(
        kvalitetssikringId = kvalitetssikringId,
        løsningId = løsningId,
        kvalitetssikretAv = kvalitetssikretAv,
        tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
        erGodkjent = erGodkjent,
        begrunnelse = begrunnelse
    )
}

data class LøsningParagraf_11_19ModellApi(
    val løsningId: UUID,
    val vurdertAv: String,
    val tidspunktForVurdering: LocalDateTime,
    val beregningsdato: LocalDate
) {

    constructor(
        vurdertAv: String,
        tidspunktForVurdering: LocalDateTime,
        beregningsdato: LocalDate
    ) : this(
        løsningId = UUID.randomUUID(),
        vurdertAv = vurdertAv,
        tidspunktForVurdering = tidspunktForVurdering,
        beregningsdato = beregningsdato
    )

    fun håndter(søker: Søker): List<Behov> {
        val løsning = toLøsning()
        søker.håndterLøsning(løsning, Vilkårsvurdering<*>::håndterLøsning)
        return løsning.behov()
    }

    private fun toLøsning() = LøsningParagraf_11_19(løsningId, vurdertAv, tidspunktForVurdering, beregningsdato)
}

data class KvalitetssikringParagraf_11_19ModellApi(
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

    fun håndter(søker: Søker): List<Behov> {
        val kvalitetssikring = toKvalitetssikring()
        søker.håndterKvalitetssikring(kvalitetssikring, Vilkårsvurdering<*>::håndterKvalitetssikring)
        return kvalitetssikring.behov()
    }

    private fun toKvalitetssikring() = KvalitetssikringParagraf_11_19(
        kvalitetssikringId = kvalitetssikringId,
        løsningId = løsningId,
        kvalitetssikretAv = kvalitetssikretAv,
        tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
        erGodkjent = erGodkjent,
        begrunnelse = begrunnelse
    )
}

data class VedtakModellApi(
    val vedtaksid: UUID,
    val innvilget: Boolean,
    val inntektsgrunnlag: InntektsgrunnlagModellApi,
    val vedtaksdato: LocalDate,
    val virkningsdato: LocalDate
)

data class InntektsgrunnlagModellApi(
    val beregningsdato: LocalDate,
    val inntekterSiste3Kalenderår: List<InntekterForBeregningModellApi>,
    val yrkesskade: YrkesskadeModellApi?,
    val fødselsdato: LocalDate,
    val sisteKalenderår: Year,
    val grunnlagsfaktor: Double
)

data class InntekterForBeregningModellApi(
    val inntekter: List<InntektModellApi>,
    val inntektsgrunnlagForÅr: InntektsgrunnlagForÅrModellApi
)

data class InntektsgrunnlagForÅrModellApi(
    val år: Year,
    val beløpFørJustering: Double,
    val beløpJustertFor6G: Double,
    val erBeløpJustertFor6G: Boolean,
    val grunnlagsfaktor: Double
)

data class YrkesskadeModellApi(
    val gradAvNedsattArbeidsevneKnyttetTilYrkesskade: Double,
    val inntektsgrunnlag: InntektsgrunnlagForÅrModellApi
)

data class InntektModellApi(
    val arbeidsgiver: String,
    val inntekstmåned: YearMonth,
    val beløp: Double
)

data class InntekterModellApi(
    val inntekter: List<InntektModellApi>
) {
    fun håndter(søker: Søker) {
        søker.håndterLøsning(LøsningInntekter(inntekter.map {
            Inntekt(
                arbeidsgiver = Arbeidsgiver(it.arbeidsgiver),
                inntekstmåned = it.inntekstmåned,
                beløp = it.beløp.beløp
            )
        }))
    }
}
