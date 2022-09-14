package no.nav.aap.domene.visitor

import no.nav.aap.domene.Sakstype

internal class OppfyltVisitor(sakstype: Sakstype) : SakstypeVisitor {
    internal var erOppfylt = true
        private set(value) {
            field = field && value
        }
    internal var erIkkeOppfylt = false
        private set(value) {
            field = field || value
        }

    init {
        sakstype.accept(this)
    }

    override fun visitIkkeVurdert() {
        erOppfylt = false
        erIkkeOppfylt = false
    }

    override fun visitSøknadMottatt() {
        erOppfylt = false
        erIkkeOppfylt = false
    }

    override fun visitManuellVurderingTrengs() {
        erOppfylt = false
        erIkkeOppfylt = false
    }

    override fun visitOppfyltMaskinelt() {
        erOppfylt = true
        erIkkeOppfylt = false
    }

    override fun visitOppfyltMaskineltKvalitetssikret() {
        erOppfylt = true
        erIkkeOppfylt = false
    }

    override fun visitIkkeOppfyltMaskinelt() {
        erOppfylt = false
        erIkkeOppfylt = true
    }

    override fun visitIkkeOppfyltMaskineltKvalitetssikret() {
        erOppfylt = false
        erIkkeOppfylt = true
    }

    override fun visitOppfyltManuelt() {
        erOppfylt = true
        erIkkeOppfylt = false
    }

    override fun visitOppfyltManueltKvalitetssikret() {
        erOppfylt = true
        erIkkeOppfylt = false
    }

    override fun visitIkkeOppfyltManuelt() {
        erOppfylt = false
        erIkkeOppfylt = true
    }

    override fun visitIkkeOppfyltManueltKvalitetssikret() {
        erOppfylt = false
        erIkkeOppfylt = true
    }

    override fun visitIkkeRelevant() {
        erOppfylt = true
        erIkkeOppfylt = false
    }
}
