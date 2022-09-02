package no.nav.aap.dto

data class SakstypeModellApi(
    val type: String,
    val aktiv: Boolean,
    val vilkårsvurderinger: List<VilkårsvurderingModellApi>
)