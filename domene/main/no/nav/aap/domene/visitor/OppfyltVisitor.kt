package no.nav.aap.domene.visitor

internal class OppfyltVisitor : SakstypeVisitor {
    internal var erOppfylt = true
        private set(value) {
            field = field && value
        }
    internal var erIkkeOppfylt = false
        private set(value) {
            field = field || value
        }

    override fun visitIkkeVurdert() {
        erOppfylt = false
        erIkkeOppfylt = false
    }

    override fun visitAvventerMaskinellVurdering() {
        erOppfylt = false
        erIkkeOppfylt = false
    }

    override fun visitAvventerInnstilling() {
        erOppfylt = false
        erIkkeOppfylt = false
    }

    override fun visitAvventerManuellVurdering() {
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
