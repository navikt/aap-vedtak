package no.nav.aap.domene.frontendView

import no.nav.aap.domene.SøkerVisitor
import java.time.LocalDate

class FrontendVisitor : SøkerVisitor {
    private lateinit var ident: String
    private lateinit var fødselsdato: LocalDate

    override fun visitPersonident(ident: String) {
        this.ident = ident
    }

    override fun visitFødselsdato(fødselsdato: LocalDate) {
        this.fødselsdato = fødselsdato
    }

    private val vilkårsvurderinger: MutableList<FrontendVilkårsvurdering> = mutableListOf()

    override fun preVisitSak() {
        vilkårsvurderinger.clear()
    }

    private lateinit var vilkår: FrontendVilkår

    override fun visitVilkår(paragraf: String, ledd: String) {
        this.vilkår = FrontendVilkår(paragraf, ledd)
    }

    override fun visitVilkårsvurderingIkkeVurdert() {
        vilkårsvurderinger.add(FrontendVilkårsvurdering(vilkår, "IKKE_VURDERT"))
    }

    override fun visitVilkårsvurderingOppfylt() {
        vilkårsvurderinger.add(FrontendVilkårsvurdering(vilkår, "OPPFYLT"))
    }

    override fun visitVilkårsvurderingIkkeOppfylt() {
        vilkårsvurderinger.add(FrontendVilkårsvurdering(vilkår, "IKKE_OPPFYLT"))
    }

    private val saker: MutableList<FrontendSak> = mutableListOf()

    override fun postVisitSak() {
        saker.add(FrontendSak(ident, fødselsdato, vilkårsvurderinger.toList()))
    }

    fun saker() = saker.toList()
}

data class FrontendSak(
    val personident: String,
    val fødselsdato: LocalDate,
    val vilkårsvurdering: List<FrontendVilkårsvurdering>
)

data class FrontendVilkårsvurdering(
    val vilkår: FrontendVilkår,
    val tilstand: String
)

data class FrontendVilkår(
    val paragraf: String,
    val ledd: String
)