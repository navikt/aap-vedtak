package no.nav.aap.domene.frontendView

import java.time.LocalDate

data class FrontendSak(
    val personident: String,
    val fødselsdato: LocalDate,
    val tilstand: String,
    val vilkårsvurderinger: List<FrontendVilkårsvurdering>
)

data class FrontendVilkårsvurdering(
    val vilkår: FrontendVilkår,
    val tilstand: String
)

data class FrontendVilkår(
    val paragraf: String,
    val ledd: List<String>
)
