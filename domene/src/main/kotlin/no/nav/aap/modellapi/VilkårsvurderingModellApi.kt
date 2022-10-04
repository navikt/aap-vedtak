package no.nav.aap.modellapi

import java.util.*

sealed interface VilkårsvurderingModellApi {
    val vilkårsvurderingsid: UUID
    val vurdertAv: String?
    val kvalitetssikretAv: String?
    val paragraf: String
    val ledd: List<String>
    val tilstand: String
    val utfall: Utfall
    val vurdertMaskinelt: Boolean
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
) : VilkårsvurderingModellApi

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
) : VilkårsvurderingModellApi

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
) : VilkårsvurderingModellApi

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
) : VilkårsvurderingModellApi

data class Paragraf_11_4FørsteLeddModellApi(
    override val vilkårsvurderingsid: UUID,
    override val vurdertAv: String?,
    override val kvalitetssikretAv: String?,
    override val paragraf: String,
    override val ledd: List<String>,
    override val tilstand: String,
    override val utfall: Utfall,
    override val vurdertMaskinelt: Boolean,
) : VilkårsvurderingModellApi

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
) : VilkårsvurderingModellApi

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
) : VilkårsvurderingModellApi

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
) : VilkårsvurderingModellApi

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
) : VilkårsvurderingModellApi

data class Paragraf_11_14ModellApi(
    override val vilkårsvurderingsid: UUID,
    override val vurdertAv: String?,
    override val kvalitetssikretAv: String?,
    override val paragraf: String,
    override val ledd: List<String>,
    override val tilstand: String,
    override val utfall: Utfall,
    override val vurdertMaskinelt: Boolean,
) : VilkårsvurderingModellApi

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
) : VilkårsvurderingModellApi

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
) : VilkårsvurderingModellApi

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
) : VilkårsvurderingModellApi

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
) : VilkårsvurderingModellApi

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
) : VilkårsvurderingModellApi
