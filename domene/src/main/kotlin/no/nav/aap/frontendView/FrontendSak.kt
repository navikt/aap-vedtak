package no.nav.aap.frontendView

import java.time.LocalDate

data class FrontendSak(
    val personident: String,
    val fødselsdato: LocalDate,
    val tilstand: String,
    val vilkårsvurderinger: List<FrontendVilkårsvurdering>
)

data class FrontendVilkårsvurdering(
    val paragraf: String,
    val ledd: List<String>,
    val tilstand: String,
    val harÅpenOppgave: Boolean
)
