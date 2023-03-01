package no.nav.aap.modellapi

data class SakstypeModellApi(
    val type: String,
    val aktiv: Boolean,
    val vilkårsvurderinger: List<VilkårsvurderingModellApi>
)