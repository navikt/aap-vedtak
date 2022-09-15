package no.nav.aap.domene.visitor

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class KvalitetssikretVisitorTest {

    @Test
    fun `IkkeVurdert er hverken kvalitetssikret eller i kvalitetssikring`() {
        val visitor = KvalitetssikretVisitor()

        visitor.visitIkkeVurdert()

        assertFalse(visitor.erKvalitetssikret)
        assertTrue(visitor.erIkkeIKvalitetssikring)
    }

    @Test
    fun `SøknadMottatt er hverken kvalitetssikret eller i kvalitetssikring`() {
        val visitor = KvalitetssikretVisitor()

        visitor.visitSøknadMottatt()

        assertFalse(visitor.erKvalitetssikret)
        assertTrue(visitor.erIkkeIKvalitetssikring)
    }

    @Test
    fun `ManuellVurderingTrengs er hverken kvalitetssikret eller i kvalitetssikring`() {
        val visitor = KvalitetssikretVisitor()

        visitor.visitManuellVurderingTrengs()

        assertFalse(visitor.erKvalitetssikret)
        assertTrue(visitor.erIkkeIKvalitetssikring)
    }

    @Test
    fun `OppfyltMaskinelt er ikke kvalitetssikret, men er i kvalitetssikring`() {
        val visitor = KvalitetssikretVisitor()

        visitor.visitOppfyltMaskinelt()

        assertFalse(visitor.erKvalitetssikret)
        assertFalse(visitor.erIkkeIKvalitetssikring)
    }

    @Test
    fun `OppfyltMaskineltKvalitetssikret er både kvalitetssikret og i kvalitetssikring`() {
        val visitor = KvalitetssikretVisitor()

        visitor.visitOppfyltMaskineltKvalitetssikret()

        assertTrue(visitor.erKvalitetssikret)
        assertFalse(visitor.erIkkeIKvalitetssikring)
    }

    @Test
    fun `IkkeOppfyltMaskinelt er ikke kvalitetssikret, men er i kvalitetssikring`() {
        val visitor = KvalitetssikretVisitor()

        visitor.visitIkkeOppfyltMaskinelt()

        assertFalse(visitor.erKvalitetssikret)
        assertFalse(visitor.erIkkeIKvalitetssikring)
    }

    @Test
    fun `IkkeOppfyltMaskineltKvalitetssikret er både kvalitetssikret og i kvalitetssikring`() {
        val visitor = KvalitetssikretVisitor()

        visitor.visitIkkeOppfyltMaskineltKvalitetssikret()

        assertTrue(visitor.erKvalitetssikret)
        assertFalse(visitor.erIkkeIKvalitetssikring)
    }

    @Test
    fun `OppfyltManuelt er ikke kvalitetssikret, men er i kvalitetssikring`() {
        val visitor = KvalitetssikretVisitor()

        visitor.visitOppfyltManuelt()

        assertFalse(visitor.erKvalitetssikret)
        assertFalse(visitor.erIkkeIKvalitetssikring)
    }

    @Test
    fun `OppfyltManueltKvalitetssikret er både kvalitetssikret og i kvalitetssikring`() {
        val visitor = KvalitetssikretVisitor()

        visitor.visitOppfyltManueltKvalitetssikret()

        assertTrue(visitor.erKvalitetssikret)
        assertFalse(visitor.erIkkeIKvalitetssikring)
    }

    @Test
    fun `IkkeOppfyltManuelt er ikke kvalitetssikret, men er i kvalitetssikring`() {
        val visitor = KvalitetssikretVisitor()

        visitor.visitIkkeOppfyltManuelt()

        assertFalse(visitor.erKvalitetssikret)
        assertFalse(visitor.erIkkeIKvalitetssikring)
    }

    @Test
    fun `IkkeOppfyltManueltKvalitetssikret er både kvalitetssikret og i kvalitetssikring`() {
        val visitor = KvalitetssikretVisitor()

        visitor.visitIkkeOppfyltManueltKvalitetssikret()

        assertTrue(visitor.erKvalitetssikret)
        assertFalse(visitor.erIkkeIKvalitetssikring)
    }

    @Test
    fun `IkkeRelevant er både kvalitetssikret og i kvalitetssikring`() {
        val visitor = KvalitetssikretVisitor()

        visitor.visitIkkeRelevant()

        assertTrue(visitor.erKvalitetssikret)
        assertFalse(visitor.erIkkeIKvalitetssikring)
    }

    @Test
    fun `Hvis alle tilstandene er kvalitetssikret blir resultatet kvalitetssikret`() {
        val visitor = KvalitetssikretVisitor()

        visitor.visitOppfyltMaskineltKvalitetssikret()
        visitor.visitOppfyltMaskineltKvalitetssikret()
        visitor.visitOppfyltMaskineltKvalitetssikret()

        assertTrue(visitor.erKvalitetssikret)
    }

    @Test
    fun `Hvis minst en av tilstandene ikke er kvalitetssikret blir resultatet ikke kvalitetssikret`() {
        val visitor = KvalitetssikretVisitor()

        visitor.visitOppfyltMaskineltKvalitetssikret()
        visitor.visitOppfyltMaskinelt()
        visitor.visitOppfyltMaskineltKvalitetssikret()

        assertFalse(visitor.erKvalitetssikret)
    }

    @Test
    fun `Hvis alle tilstandene er i kvalitetssikring blir resultatet i kvalitetssikring`() {
        val visitor = KvalitetssikretVisitor()

        visitor.visitOppfyltMaskinelt()
        visitor.visitOppfyltMaskinelt()
        visitor.visitOppfyltMaskinelt()

        assertFalse(visitor.erIkkeIKvalitetssikring)
    }

    @Test
    fun `Hvis minst en av tilstandene ikke er i kvalitetssikring blir resultatet ikke i kvalitetssikring`() {
        val visitor = KvalitetssikretVisitor()

        visitor.visitOppfyltMaskinelt()
        visitor.visitSøknadMottatt()
        visitor.visitOppfyltMaskinelt()

        assertTrue(visitor.erIkkeIKvalitetssikring)
    }
}
