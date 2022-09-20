package no.nav.aap.domene.visitor

import no.nav.aap.domene.Sakstype
import no.nav.aap.domene.vilkår.Paragraf_22_13
import no.nav.aap.domene.vilkår.Paragraf_11_19
import no.nav.aap.domene.vilkår.Paragraf_11_22
import no.nav.aap.domene.vilkår.Paragraf_11_27_FørsteLedd
import no.nav.aap.hendelse.LøsningParagraf_22_13
import no.nav.aap.hendelse.LøsningParagraf_11_19
import no.nav.aap.hendelse.LøsningParagraf_11_22
import no.nav.aap.hendelse.LøsningParagraf_11_27_FørsteLedd
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

internal interface SakstypeVisitor : VilkårsvurderingVisitor {
    fun preVisitStandard(sakstype: Sakstype.Standard) {}
    fun postVisitStandard(sakstype: Sakstype.Standard) {}

    fun preVisitYrkesskade(sakstype: Sakstype.Yrkesskade) {}
    fun postVisitYrkesskade(sakstype: Sakstype.Yrkesskade) {}

    fun preVisitStudent(sakstype: Sakstype.Student) {}
    fun postVisitStudent(sakstype: Sakstype.Student) {}
}

internal interface VilkårsvurderingVisitor {
    fun visitIkkeVurdert() {}
    fun visitSøknadMottatt() {}
    fun visitManuellVurderingTrengs() {}
    fun visitOppfyltMaskinelt() {}
    fun visitOppfyltMaskineltKvalitetssikret() {}
    fun visitIkkeOppfyltMaskinelt() {}
    fun visitIkkeOppfyltMaskineltKvalitetssikret() {}
    fun visitOppfyltManuelt() {}
    fun visitOppfyltManueltKvalitetssikret() {}
    fun visitIkkeOppfyltManuelt() {}
    fun visitIkkeOppfyltManueltKvalitetssikret() {}
    fun visitIkkeRelevant() {}

    fun preVisitParagraf_22_13(vilkårsvurdering: Paragraf_22_13) {}
    fun preVisitGjeldendeLøsning(løsning: LøsningParagraf_22_13) {}
    fun visitLøsningParagraf_22_13(
        løsning: LøsningParagraf_22_13,
        løsningId: UUID,
        vurdertAv: String,
        tidspunktForVurdering: LocalDateTime,
        bestemmesAv: LøsningParagraf_22_13.BestemmesAv,
        unntak: String,
        unntaksbegrunnelse: String,
        manueltSattVirkningsdato: LocalDate?
    ) {
    }

    fun postVisitGjeldendeLøsning(løsning: LøsningParagraf_22_13) {}
    fun postVisitParagraf_22_13(vilkårsvurdering: Paragraf_22_13) {}

    fun preVisitParagraf_11_19(vilkårsvurdering: Paragraf_11_19) {}
    fun preVisitGjeldendeLøsning(løsning: LøsningParagraf_11_19) {}

    fun visitLøsningParagraf_11_19(
        løsning: LøsningParagraf_11_19,
        løsningId: UUID,
        vurdertAv: String,
        tidspunktForVurdering: LocalDateTime,
        beregningsdato: LocalDate
    ) {
    }

    fun postVisitGjeldendeLøsning(løsning: LøsningParagraf_11_19) {}
    fun postVisitParagraf_11_19(vilkårsvurdering: Paragraf_11_19) {}

    fun preVisitParagraf_11_22(vilkårsvurdering: Paragraf_11_22) {}
    fun preVisitGjeldendeLøsning(løsning: LøsningParagraf_11_22) {}

    fun visitLøsningParagraf_11_22(
        løsning: LøsningParagraf_11_22,
        løsningId: UUID,
        vurdertAv: String,
        tidspunktForVurdering: LocalDateTime,
    ) {
    }

    fun postVisitGjeldendeLøsning(løsning: LøsningParagraf_11_22) {}
    fun postVisitParagraf_11_22(vilkårsvurdering: Paragraf_11_22) {}

    fun preVisitParagraf_11_27(vilkårsvurdering: Paragraf_11_27_FørsteLedd) {}
    fun preVisitGjeldendeLøsning(løsning: LøsningParagraf_11_27_FørsteLedd) {}

    fun visitLøsningParagraf_11_27(løsning: LøsningParagraf_11_27_FørsteLedd,
                                   løsningId: UUID, svangerskapspenger:
                                   LøsningParagraf_11_27_FørsteLedd.Svangerskapspenger
    ) {
    }

    fun postVisitGjeldendeLøsning(løsning: LøsningParagraf_11_27_FørsteLedd) {}
    fun postVisitParagraf_11_27(vilkårsvurdering: Paragraf_11_27_FørsteLedd) {}


}
