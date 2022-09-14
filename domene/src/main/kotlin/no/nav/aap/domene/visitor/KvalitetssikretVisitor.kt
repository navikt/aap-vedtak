package no.nav.aap.domene.visitor

import no.nav.aap.domene.Sakstype

internal class KvalitetssikretVisitor(sakstype: Sakstype) : SakstypeVisitor {
    internal var erKvalitetssikret = true
        private set(value) {
            field = field && value
        }
    internal var erIKvalitetssikring = true
        private set(value) {
            field = field && value
        }

    init {
        sakstype.accept(this)
    }

    override fun visitIkkeVurdert() {
        erKvalitetssikret = false
        erIKvalitetssikring = false
    }

    override fun visitSøknadMottatt() {
        erKvalitetssikret = false
        erIKvalitetssikring = false
    }

    override fun visitManuellVurderingTrengs() {
        erKvalitetssikret = false
        erIKvalitetssikring = false
    }

    override fun visitOppfyltMaskinelt() {
        erKvalitetssikret = false
        erIKvalitetssikring = true
    }

    override fun visitOppfyltMaskineltKvalitetssikret() {
        erKvalitetssikret = true
        erIKvalitetssikring = true
    }

    override fun visitIkkeOppfyltMaskinelt() {
        erKvalitetssikret = false
        erIKvalitetssikring = true
    }

    override fun visitIkkeOppfyltMaskineltKvalitetssikret() {
        erKvalitetssikret = true
        erIKvalitetssikring = true
    }

    override fun visitOppfyltManuelt() {
        erKvalitetssikret = false
        erIKvalitetssikring = true
    }

    override fun visitOppfyltManueltKvalitetssikret() {
        erKvalitetssikret = true
        erIKvalitetssikring = true
    }

    override fun visitIkkeOppfyltManuelt() {
        erKvalitetssikret = false
        erIKvalitetssikring = true
    }

    override fun visitIkkeOppfyltManueltKvalitetssikret() {
        erKvalitetssikret = true
        erIKvalitetssikring = true
    }

    override fun visitIkkeRelevant() {
        erKvalitetssikret = true
        erIKvalitetssikring = true
    }
}
