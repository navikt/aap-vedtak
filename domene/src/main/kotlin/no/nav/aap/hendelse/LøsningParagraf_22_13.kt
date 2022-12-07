package no.nav.aap.hendelse

import no.nav.aap.domene.vilkår.Kvalitetssikring
import no.nav.aap.domene.vilkår.Løsning
import no.nav.aap.domene.vilkår.Totrinnskontroll
import no.nav.aap.domene.visitor.VilkårsvurderingVisitor
import no.nav.aap.modellapi.KvalitetssikringParagraf_22_13ModellApi
import no.nav.aap.modellapi.LøsningParagraf_22_13ModellApi
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

internal class LøsningParagraf_22_13(
    private val løsningId: UUID,
    private val vurdertAv: String,
    private val tidspunktForVurdering: LocalDateTime,
    private val bestemmesAv: BestemmesAv,
    private val unntak: String?,
    private val unntaksbegrunnelse: String?,
    private val manueltSattVirkningsdato: LocalDate?,
) : Hendelse(), Løsning<LøsningParagraf_22_13, KvalitetssikringParagraf_22_13> {

    init {
        if (bestemmesAv == BestemmesAv.annet) {
            requireNotNull(manueltSattVirkningsdato)
        }

        if (bestemmesAv in listOf(BestemmesAv.unntaksvurderingForhindret, BestemmesAv.unntaksvurderingMangelfull)) {
            requireNotNull(unntak)
            requireNotNull(unntaksbegrunnelse)
            requireNotNull(manueltSattVirkningsdato)
        }

        if (bestemmesAv == BestemmesAv.etterSisteLoenn) {
            requireNotNull(manueltSattVirkningsdato)
        }
    }

    internal companion object {
        internal fun Iterable<LøsningParagraf_22_13>.toDto() = map(LøsningParagraf_22_13::toDto)
    }

    internal enum class BestemmesAv {
        soknadstidspunkt,
        maksdatoSykepenger,
        dagpenger,
        omsorgspenger,
        pleiepenger,
        opplæringspenger,
        foreldrepenger,
        svangerskapspenger,
        unntaksvurderingForhindret,
        unntaksvurderingMangelfull,
        etterSisteLoenn,
        annet,
    }

    internal fun bestemmesAv8_48() = bestemmesAv == BestemmesAv.maksdatoSykepenger
    internal fun bestemmesAv11_27() = bestemmesAv in arrayOf(
        BestemmesAv.dagpenger,
        BestemmesAv.omsorgspenger,
        BestemmesAv.pleiepenger,
        BestemmesAv.opplæringspenger,
        BestemmesAv.foreldrepenger,
        BestemmesAv.svangerskapspenger,
    )

    internal fun bestemmesAv22_13() = !bestemmesAv8_48() && !bestemmesAv11_27()


    override fun accept(visitor: VilkårsvurderingVisitor) {
        visitor.visitLøsningParagraf_22_13(
            this,
            løsningId,
            vurdertAv,
            tidspunktForVurdering,
            bestemmesAv,
            unntak,
            unntaksbegrunnelse,
            manueltSattVirkningsdato
        )
    }

    override fun matchMedKvalitetssikring(
        totrinnskontroll: Totrinnskontroll<LøsningParagraf_22_13, KvalitetssikringParagraf_22_13>,
        kvalitetssikring: KvalitetssikringParagraf_22_13
    ) {
        kvalitetssikring.matchMedLøsning(totrinnskontroll, løsningId)
    }

    internal fun vurdertAv() = vurdertAv
    internal fun bestemmesAv() = bestemmesAv
    internal fun virkningsdato() = manueltSattVirkningsdato

    override fun toDto() = LøsningParagraf_22_13ModellApi(
        løsningId = løsningId,
        vurdertAv = vurdertAv,
        tidspunktForVurdering = tidspunktForVurdering,
        bestemmesAv = bestemmesAv.name,
        unntak = unntak,
        unntaksbegrunnelse = unntaksbegrunnelse,
        manueltSattVirkningsdato = manueltSattVirkningsdato
    )
}

internal class KvalitetssikringParagraf_22_13(
    private val kvalitetssikringId: UUID,
    private val løsningId: UUID,
    private val kvalitetssikretAv: String,
    private val tidspunktForKvalitetssikring: LocalDateTime,
    private val erGodkjent: Boolean,
    private val begrunnelse: String?,
) : Hendelse(), Kvalitetssikring<LøsningParagraf_22_13, KvalitetssikringParagraf_22_13> {

    internal companion object {
        internal fun Iterable<KvalitetssikringParagraf_22_13>.toDto() =
            map(KvalitetssikringParagraf_22_13::toDto)
    }

    override fun matchMedLøsning(
        totrinnskontroll: Totrinnskontroll<LøsningParagraf_22_13, KvalitetssikringParagraf_22_13>,
        løsningId: UUID
    ) {
        if (this.løsningId == løsningId) {
            totrinnskontroll.leggTilKvalitetssikring(this)
        }
    }

    override fun erGodkjent() = erGodkjent
    internal fun kvalitetssikretAv() = kvalitetssikretAv
    override fun toDto() = KvalitetssikringParagraf_22_13ModellApi(
        kvalitetssikringId = kvalitetssikringId,
        løsningId = løsningId,
        kvalitetssikretAv = kvalitetssikretAv,
        tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
        erGodkjent = erGodkjent,
        begrunnelse = begrunnelse
    )
}
