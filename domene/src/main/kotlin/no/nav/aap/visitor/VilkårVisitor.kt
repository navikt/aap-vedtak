package no.nav.aap.visitor

import no.nav.aap.domene.beregning.Beløp
import no.nav.aap.domene.vilkår.Vilkårsvurdering
import no.nav.aap.hendelse.*
import java.time.LocalDate
import java.time.Year
import java.util.*

internal interface VilkårVisitor {

    fun visitVilkårsvurdering(
        tilstandsnavn: String,
        vilkårsvurderingsid: UUID,
        paragraf: Vilkårsvurdering.Paragraf,
        ledd: List<Vilkårsvurdering.Ledd>,
        måVurderesManuelt: Boolean,
    ) {
    }

    fun `preVisit §2`() {}
    fun `preVisit §2`(maskinell: LøsningMaskinellMedlemskapYrkesskade) {}
    fun `preVisit §2`(maskinell: LøsningMaskinellMedlemskapYrkesskade, manuell: LøsningManuellMedlemskapYrkesskade) {}
    fun `postVisit §2`() {}
    fun `postVisit §2`(maskinell: LøsningMaskinellMedlemskapYrkesskade) {}
    fun `postVisit §2`(maskinell: LøsningMaskinellMedlemskapYrkesskade, manuell: LøsningManuellMedlemskapYrkesskade) {}

    fun `preVisit §11-4 L1`() {}
    fun `preVisit §11-4 L1`(vurderingsdato: LocalDate) {}
    fun `postVisit §11-4 L1`() {}
    fun `postVisit §11-4 L1`(vurderingsdato: LocalDate) {}

    fun `preVisit §11-4 L2 L3`() {}
    fun `preVisit §11-4 L2 L3`(manuellLøsning: LøsningParagraf_11_4AndreOgTredjeLedd) {}
    fun `postVisit §11-4 L2 L3`() {}
    fun `postVisit §11-4 L2 L3`(manuellLøsning: LøsningParagraf_11_4AndreOgTredjeLedd) {}

    fun `preVisit §11-2`() {}
    fun `preVisit §11-2`(maskinellLøsning: LøsningParagraf_11_2) {}
    fun `preVisit §11-2`(maskinellLøsning: LøsningParagraf_11_2, manuellLøsning: LøsningParagraf_11_2) {}
    fun `postVisit §11-2`() {}
    fun `postVisit §11-2`(maskinellLøsning: LøsningParagraf_11_2) {}
    fun `postVisit §11-2`(maskinellLøsning: LøsningParagraf_11_2, manuellLøsning: LøsningParagraf_11_2) {}

    fun `preVisit §11-3`() {}
    fun `preVisit §11-3`(manuellLøsning: LøsningParagraf_11_3) {}
    fun `postVisit §11-3`() {}
    fun `postVisit §11-3`(manuellLøsning: LøsningParagraf_11_3) {}

    fun `preVisit §11-5`() {}
    fun `preVisit §11-5`(nedsattArbeidsevnegrad: LøsningParagraf_11_5.NedsattArbeidsevnegrad) {}
    fun `postVisit §11-5`() {}
    fun `postVisit §11-5`(nedsattArbeidsevnegrad: LøsningParagraf_11_5.NedsattArbeidsevnegrad) {}

    fun `preVisit §11-5 yrkesskade`() {}
    fun `preVisit §11-5 yrkesskade`(manuellLøsning: LøsningParagraf_11_5_yrkesskade) {}
    fun `postVisit §11-5 yrkesskade`() {}
    fun `postVisit §11-5 yrkesskade`(manuellLøsning: LøsningParagraf_11_5_yrkesskade) {}

    fun `preVisit §11-6`() {}
    fun `preVisit §11-6`(manuellLøsning: LøsningParagraf_11_6) {}
    fun `postVisit §11-6`() {}
    fun `postVisit §11-6`(manuellLøsning: LøsningParagraf_11_6) {}

    fun `preVisit §11-12 L1`() {}
    fun `preVisit §11-12 L1`(løsning: LøsningParagraf_11_12FørsteLedd) {}
    fun `postVisit §11-12 L1`() {}
    fun `postVisit §11-12 L1`(løsning: LøsningParagraf_11_12FørsteLedd) {}

    fun `preVisit §11-14`() {} // todo: fixme
    fun `postVisit §11-14`() {}

    fun `preVisit §11-22`() {}
    fun `preVisit §11-22 løsning`(andelNedsattArbeidsevne: Int, år: Year) {}
    fun `postVisit §11-22 løsning`(andelNedsattArbeidsevne: Int, år: Year) {}
    fun `postVisit §11-22`() {}

    fun `preVisit §11-29`() {}
    fun `postVisit §11-29`() {}

    fun `preVisit §11-29`(løsning: LøsningParagraf_11_29) {}
    fun `postVisit §11-29`(løsning: LøsningParagraf_11_29) {}
}
