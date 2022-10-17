package no.nav.aap.modellapi

import no.nav.aap.domene.vilkår.*
import java.util.*

abstract class VilkårsvurderingModellApi {
    abstract val vilkårsvurderingsid: UUID
    abstract val vurdertAv: String?
    abstract val kvalitetssikretAv: String?
    abstract val paragraf: String
    abstract val ledd: List<String>
    abstract val tilstand: String
    abstract val utfall: Utfall
    abstract val vurdertMaskinelt: Boolean

    internal abstract fun gjenopprett(): Vilkårsvurdering<*>

    abstract fun accept(visitor: VilkårsvurderingModellApiVisitor)
}

interface VilkårsvurderingModellApiVisitor {
    fun visitMedlemskapYrkesskade(modellApi: MedlemskapYrkesskadeModellApi)
    fun visitParagraf_8_48(modellApi: Paragraf_8_48ModellApi)
    fun visitParagraf_11_2(modellApi: Paragraf_11_2ModellApi)
    fun visitParagraf_11_3(modellApi: Paragraf_11_3ModellApi)
    fun visitParagraf_11_4FørsteLedd(modellApi: Paragraf_11_4FørsteLeddModellApi)
    fun visitParagraf_11_4AndreOgTredjeLedd(modellApi: Paragraf_11_4AndreOgTredjeLeddModellApi)
    fun visitParagraf_11_5(modellApi: Paragraf_11_5ModellApi)
    fun visitParagraf_11_5Yrkesskade(modellApi: Paragraf_11_5YrkesskadeModellApi)
    fun visitParagraf_11_6(modellApi: Paragraf_11_6ModellApi)
    fun visitParagraf_11_14(modellApi: Paragraf_11_14ModellApi)
    fun visitParagraf_11_19(modellApi: Paragraf_11_19ModellApi)
    fun visitParagraf_11_22(modellApi: Paragraf_11_22ModellApi)
    fun visitParagraf_11_27FørsteLedd(modellApi: Paragraf_11_27FørsteLeddModellApi)
    fun visitParagraf_11_29(modellApi: Paragraf_11_29ModellApi)
    fun visitParagraf_22_13(modellApi: Paragraf_22_13ModellApi)
}

data class MedlemskapYrkesskadeModellApi(
    override val vilkårsvurderingsid: UUID,
    override val vurdertAv: String?,
    override val kvalitetssikretAv: String?,
    override val paragraf: String,
    override val ledd: List<String>,
    override val tilstand: String,
    override val utfall: Utfall,
    override val vurdertMaskinelt: Boolean,
    val løsning_medlemskap_yrkesskade_maskinell: List<LøsningMaskinellMedlemskapYrkesskadeModellApi>,
    val løsning_medlemskap_yrkesskade_manuell: List<LøsningManuellMedlemskapYrkesskadeModellApi>,
    val kvalitetssikringer_medlemskap_yrkesskade: List<KvalitetssikringMedlemskapYrkesskadeModellApi>,
) : VilkårsvurderingModellApi() {
    override fun gjenopprett(): MedlemskapYrkesskade {
        val medlemskapYrkesskade = MedlemskapYrkesskade.gjenopprett(vilkårsvurderingsid, enumValueOf(tilstand))
        medlemskapYrkesskade.gjenopprettTilstand(this)
        return medlemskapYrkesskade
    }

    override fun accept(visitor: VilkårsvurderingModellApiVisitor) {
        visitor.visitMedlemskapYrkesskade(this)
    }
}

data class Paragraf_8_48ModellApi(
    override val vilkårsvurderingsid: UUID,
    override val vurdertAv: String?,
    override val kvalitetssikretAv: String?,
    override val paragraf: String,
    override val ledd: List<String>,
    override val tilstand: String,
    override val utfall: Utfall,
    override val vurdertMaskinelt: Boolean,
    val løsning_8_48_maskinell: List<SykepengedagerModellApi>,
    val løsning_22_13_manuell: List<LøsningParagraf_22_13ModellApi>,
    val kvalitetssikringer_22_13: List<KvalitetssikringParagraf_22_13ModellApi>,
) : VilkårsvurderingModellApi() {
    override fun gjenopprett(): Paragraf_8_48 {
        val paragraf = Paragraf_8_48.gjenopprett(vilkårsvurderingsid, enumValueOf(tilstand))
        paragraf.gjenopprettTilstand(this)
        return paragraf
    }

    override fun accept(visitor: VilkårsvurderingModellApiVisitor) {
        visitor.visitParagraf_8_48(this)
    }
}

data class Paragraf_11_2ModellApi(
    override val vilkårsvurderingsid: UUID,
    override val vurdertAv: String?,
    override val kvalitetssikretAv: String?,
    override val paragraf: String,
    override val ledd: List<String>,
    override val tilstand: String,
    override val utfall: Utfall,
    override val vurdertMaskinelt: Boolean,
    val løsning_11_2_maskinell: List<LøsningMaskinellParagraf_11_2ModellApi>,
    val løsning_11_2_manuell: List<LøsningParagraf_11_2ModellApi>,
    val kvalitetssikringer_11_2: List<KvalitetssikringParagraf_11_2ModellApi>,
) : VilkårsvurderingModellApi() {
    override fun gjenopprett(): Paragraf_11_2 {
        val paragraf = Paragraf_11_2.gjenopprett(vilkårsvurderingsid, enumValueOf(tilstand))
        paragraf.gjenopprettTilstand(this)
        return paragraf
    }

    override fun accept(visitor: VilkårsvurderingModellApiVisitor) {
        visitor.visitParagraf_11_2(this)
    }
}

data class Paragraf_11_3ModellApi(
    override val vilkårsvurderingsid: UUID,
    override val vurdertAv: String?,
    override val kvalitetssikretAv: String?,
    override val paragraf: String,
    override val ledd: List<String>,
    override val tilstand: String,
    override val utfall: Utfall,
    override val vurdertMaskinelt: Boolean,
    val løsning_11_3_manuell: List<LøsningParagraf_11_3ModellApi>,
    val kvalitetssikringer_11_3: List<KvalitetssikringParagraf_11_3ModellApi>,
) : VilkårsvurderingModellApi() {
    override fun gjenopprett(): Paragraf_11_3 {
        val paragraf = Paragraf_11_3.gjenopprett(vilkårsvurderingsid, enumValueOf(tilstand))
        paragraf.gjenopprettTilstand(this)
        return paragraf
    }

    override fun accept(visitor: VilkårsvurderingModellApiVisitor) {
        visitor.visitParagraf_11_3(this)
    }
}

data class Paragraf_11_4FørsteLeddModellApi(
    override val vilkårsvurderingsid: UUID,
    override val vurdertAv: String?,
    override val kvalitetssikretAv: String?,
    override val paragraf: String,
    override val ledd: List<String>,
    override val tilstand: String,
    override val utfall: Utfall,
    override val vurdertMaskinelt: Boolean,
) : VilkårsvurderingModellApi() {
    override fun gjenopprett(): Paragraf_11_4FørsteLedd {
        val paragraf = Paragraf_11_4FørsteLedd.gjenopprett(vilkårsvurderingsid, enumValueOf(tilstand))
        paragraf.gjenopprettTilstand(this)
        return paragraf
    }

    override fun accept(visitor: VilkårsvurderingModellApiVisitor) {
        visitor.visitParagraf_11_4FørsteLedd(this)
    }
}

data class Paragraf_11_4AndreOgTredjeLeddModellApi(
    override val vilkårsvurderingsid: UUID,
    override val vurdertAv: String?,
    override val kvalitetssikretAv: String?,
    override val paragraf: String,
    override val ledd: List<String>,
    override val tilstand: String,
    override val utfall: Utfall,
    override val vurdertMaskinelt: Boolean,
    val løsning_11_4_ledd2_ledd3_manuell: List<LøsningParagraf_11_4AndreOgTredjeLeddModellApi>,
    val kvalitetssikringer_11_4_ledd2_ledd3: List<KvalitetssikringParagraf_11_4AndreOgTredjeLeddModellApi>,
) : VilkårsvurderingModellApi() {
    override fun gjenopprett(): Paragraf_11_4AndreOgTredjeLedd {
        val paragraf = Paragraf_11_4AndreOgTredjeLedd.gjenopprett(vilkårsvurderingsid, enumValueOf(tilstand))
        paragraf.gjenopprettTilstand(this)
        return paragraf
    }

    override fun accept(visitor: VilkårsvurderingModellApiVisitor) {
        visitor.visitParagraf_11_4AndreOgTredjeLedd(this)
    }
}

data class Paragraf_11_5ModellApi(
    override val vilkårsvurderingsid: UUID,
    override val vurdertAv: String?,
    override val kvalitetssikretAv: String?,
    override val paragraf: String,
    override val ledd: List<String>,
    override val tilstand: String,
    override val utfall: Utfall,
    override val vurdertMaskinelt: Boolean,
    val løsning_11_5_manuell: List<LøsningParagraf_11_5ModellApi>,
    val kvalitetssikringer_11_5: List<KvalitetssikringParagraf_11_5ModellApi>,
) : VilkårsvurderingModellApi() {
    override fun gjenopprett(): Paragraf_11_5 {
        val paragraf = Paragraf_11_5.gjenopprett(vilkårsvurderingsid, enumValueOf(tilstand))
        paragraf.gjenopprettTilstand(this)
        return paragraf
    }

    override fun accept(visitor: VilkårsvurderingModellApiVisitor) {
        visitor.visitParagraf_11_5(this)
    }
}

data class Paragraf_11_5YrkesskadeModellApi(
    override val vilkårsvurderingsid: UUID,
    override val vurdertAv: String?,
    override val kvalitetssikretAv: String?,
    override val paragraf: String,
    override val ledd: List<String>,
    override val tilstand: String,
    override val utfall: Utfall,
    override val vurdertMaskinelt: Boolean,
    val løsning_11_5_yrkesskade_manuell: List<LøsningParagraf_11_5YrkesskadeModellApi>,
    val kvalitetssikringer_11_5_yrkesskade: List<KvalitetssikringParagraf_11_5YrkesskadeModellApi>,
) : VilkårsvurderingModellApi() {
    override fun gjenopprett(): Paragraf_11_5Yrkesskade {
        val paragraf = Paragraf_11_5Yrkesskade.gjenopprett(vilkårsvurderingsid, enumValueOf(tilstand))
        paragraf.gjenopprettTilstand(this)
        return paragraf
    }

    override fun accept(visitor: VilkårsvurderingModellApiVisitor) {
        visitor.visitParagraf_11_5Yrkesskade(this)
    }
}

data class Paragraf_11_6ModellApi(
    override val vilkårsvurderingsid: UUID,
    override val vurdertAv: String?,
    override val kvalitetssikretAv: String?,
    override val paragraf: String,
    override val ledd: List<String>,
    override val tilstand: String,
    override val utfall: Utfall,
    override val vurdertMaskinelt: Boolean,
    val løsning_11_6_manuell: List<LøsningParagraf_11_6ModellApi>,
    val kvalitetssikringer_11_6: List<KvalitetssikringParagraf_11_6ModellApi>,
) : VilkårsvurderingModellApi() {
    override fun gjenopprett(): Paragraf_11_6 {
        val paragraf = Paragraf_11_6.gjenopprett(vilkårsvurderingsid, enumValueOf(tilstand))
        paragraf.gjenopprettTilstand(this)
        return paragraf
    }

    override fun accept(visitor: VilkårsvurderingModellApiVisitor) {
        visitor.visitParagraf_11_6(this)
    }
}

data class Paragraf_11_14ModellApi(
    override val vilkårsvurderingsid: UUID,
    override val vurdertAv: String?,
    override val kvalitetssikretAv: String?,
    override val paragraf: String,
    override val ledd: List<String>,
    override val tilstand: String,
    override val utfall: Utfall,
    override val vurdertMaskinelt: Boolean,
) : VilkårsvurderingModellApi() {
    override fun gjenopprett(): Paragraf_11_14 {
        val paragraf = Paragraf_11_14.gjenopprett(vilkårsvurderingsid, enumValueOf(tilstand))
        paragraf.gjenopprettTilstand(this)
        return paragraf
    }

    override fun accept(visitor: VilkårsvurderingModellApiVisitor) {
        visitor.visitParagraf_11_14(this)
    }
}

data class Paragraf_11_19ModellApi(
    override val vilkårsvurderingsid: UUID,
    override val vurdertAv: String?,
    override val kvalitetssikretAv: String?,
    override val paragraf: String,
    override val ledd: List<String>,
    override val tilstand: String,
    override val utfall: Utfall,
    override val vurdertMaskinelt: Boolean,
    val løsning_11_19_manuell: List<LøsningParagraf_11_19ModellApi>,
    val kvalitetssikringer_11_19: List<KvalitetssikringParagraf_11_19ModellApi>,
) : VilkårsvurderingModellApi() {
    override fun gjenopprett(): Paragraf_11_19 {
        val paragraf = Paragraf_11_19.gjenopprett(vilkårsvurderingsid, enumValueOf(tilstand))
        paragraf.gjenopprettTilstand(this)
        return paragraf
    }

    override fun accept(visitor: VilkårsvurderingModellApiVisitor) {
        visitor.visitParagraf_11_19(this)
    }
}

data class Paragraf_11_22ModellApi(
    override val vilkårsvurderingsid: UUID,
    override val vurdertAv: String?,
    override val kvalitetssikretAv: String?,
    override val paragraf: String,
    override val ledd: List<String>,
    override val tilstand: String,
    override val utfall: Utfall,
    override val vurdertMaskinelt: Boolean,
    val løsning_11_22_manuell: List<LøsningParagraf_11_22ModellApi>,
    val kvalitetssikringer_11_22: List<KvalitetssikringParagraf_11_22ModellApi>,
) : VilkårsvurderingModellApi() {
    override fun gjenopprett(): Paragraf_11_22 {
        val paragraf = Paragraf_11_22.gjenopprett(vilkårsvurderingsid, enumValueOf(tilstand))
        paragraf.gjenopprettTilstand(this)
        return paragraf
    }

    override fun accept(visitor: VilkårsvurderingModellApiVisitor) {
        visitor.visitParagraf_11_22(this)
    }
}

data class Paragraf_11_27FørsteLeddModellApi(
    override val vilkårsvurderingsid: UUID,
    override val vurdertAv: String?,
    override val kvalitetssikretAv: String?,
    override val paragraf: String,
    override val ledd: List<String>,
    override val tilstand: String,
    override val utfall: Utfall,
    override val vurdertMaskinelt: Boolean,
    val løsning_11_27_maskinell: List<LøsningParagraf_11_27_FørsteLedd_ModellApi>,
    val løsning_22_13_manuell: List<LøsningParagraf_22_13ModellApi>,
    val kvalitetssikringer_22_13: List<KvalitetssikringParagraf_22_13ModellApi>,
) : VilkårsvurderingModellApi() {
    override fun gjenopprett(): Paragraf_11_27FørsteLedd {
        val paragraf = Paragraf_11_27FørsteLedd.gjenopprett(vilkårsvurderingsid, enumValueOf(tilstand))
        paragraf.gjenopprettTilstand(this)
        return paragraf
    }

    override fun accept(visitor: VilkårsvurderingModellApiVisitor) {
        visitor.visitParagraf_11_27FørsteLedd(this)
    }
}

data class Paragraf_11_29ModellApi(
    override val vilkårsvurderingsid: UUID,
    override val vurdertAv: String?,
    override val kvalitetssikretAv: String?,
    override val paragraf: String,
    override val ledd: List<String>,
    override val tilstand: String,
    override val utfall: Utfall,
    override val vurdertMaskinelt: Boolean,
    val løsning_11_29_manuell: List<LøsningParagraf_11_29ModellApi>,
    val kvalitetssikringer_11_29: List<KvalitetssikringParagraf_11_29ModellApi>,
) : VilkårsvurderingModellApi() {
    override fun gjenopprett(): Paragraf_11_29 {
        val paragraf = Paragraf_11_29.gjenopprett(vilkårsvurderingsid, enumValueOf(tilstand))
        paragraf.gjenopprettTilstand(this)
        return paragraf
    }

    override fun accept(visitor: VilkårsvurderingModellApiVisitor) {
        visitor.visitParagraf_11_29(this)
    }
}

data class Paragraf_22_13ModellApi(
    override val vilkårsvurderingsid: UUID,
    override val vurdertAv: String?,
    override val kvalitetssikretAv: String?,
    override val paragraf: String,
    override val ledd: List<String>,
    override val tilstand: String,
    override val utfall: Utfall,
    override val vurdertMaskinelt: Boolean,
    val løsning_22_13_manuell: List<LøsningParagraf_22_13ModellApi>,
    val kvalitetssikringer_22_13: List<KvalitetssikringParagraf_22_13ModellApi>,
) : VilkårsvurderingModellApi() {
    override fun gjenopprett(): Paragraf_22_13 {
        val paragraf = Paragraf_22_13.gjenopprett(vilkårsvurderingsid, enumValueOf(tilstand))
        paragraf.gjenopprettTilstand(this)
        return paragraf
    }

    override fun accept(visitor: VilkårsvurderingModellApiVisitor) {
        visitor.visitParagraf_22_13(this)
    }
}
