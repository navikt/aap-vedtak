package no.nav.aap.domene.visitor

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class OppfyltVisitorTest {

    @Test
    fun `IkkeVurdert er hverken oppylt eller ikkeOppfylt`() {
        val visitor = OppfyltVisitor()

        visitor.visitIkkeVurdert()

        assertFalse(visitor.erOppfylt)
        assertFalse(visitor.erIkkeOppfylt)
    }

    @Test
    fun `SøknadMottatt er hverken oppylt eller ikkeOppfylt`() {
        val visitor = OppfyltVisitor()

        visitor.visitSøknadMottatt()

        assertFalse(visitor.erOppfylt)
        assertFalse(visitor.erIkkeOppfylt)
    }

    @Test
    fun `ManuellVurderingTrengs er hverken oppylt eller ikkeOppfylt`() {
        val visitor = OppfyltVisitor()

        visitor.visitManuellVurderingTrengs()

        assertFalse(visitor.erOppfylt)
        assertFalse(visitor.erIkkeOppfylt)
    }

    @Test
    fun `OppfyltMaskinelt er oppfylt og ikke ikkeOppfylt`() {
        val visitor = OppfyltVisitor()

        visitor.visitOppfyltMaskinelt()

        assertTrue(visitor.erOppfylt)
        assertFalse(visitor.erIkkeOppfylt)
    }

    @Test
    fun `OppfyltMaskineltKvalitetssikret er oppfylt og ikke ikkeOppfylt`() {
        val visitor = OppfyltVisitor()

        visitor.visitOppfyltMaskineltKvalitetssikret()

        assertTrue(visitor.erOppfylt)
        assertFalse(visitor.erIkkeOppfylt)
    }

    @Test
    fun `IkkeOppfyltMaskinelt er ikke oppfylt og ikkeOppfylt`() {
        val visitor = OppfyltVisitor()

        visitor.visitIkkeOppfyltMaskinelt()

        assertFalse(visitor.erOppfylt)
        assertTrue(visitor.erIkkeOppfylt)
    }

    @Test
    fun `IkkeOppfyltMaskineltKvalitetssikret er ikke oppfylt og ikkeOppfylt`() {
        val visitor = OppfyltVisitor()

        visitor.visitIkkeOppfyltMaskineltKvalitetssikret()

        assertFalse(visitor.erOppfylt)
        assertTrue(visitor.erIkkeOppfylt)
    }

    @Test
    fun `OppfyltManuelt er oppfylt og ikke ikkeOppfylt`() {
        val visitor = OppfyltVisitor()

        visitor.visitOppfyltManuelt()

        assertTrue(visitor.erOppfylt)
        assertFalse(visitor.erIkkeOppfylt)
    }

    @Test
    fun `OppfyltManueltKvalitetssikret er oppfylt og ikke ikkeOppfylt`() {
        val visitor = OppfyltVisitor()

        visitor.visitOppfyltManueltKvalitetssikret()

        assertTrue(visitor.erOppfylt)
        assertFalse(visitor.erIkkeOppfylt)
    }

    @Test
    fun `IkkeOppfyltManuelt er ikke oppfylt og ikkeOppfylt`() {
        val visitor = OppfyltVisitor()

        visitor.visitIkkeOppfyltManuelt()

        assertFalse(visitor.erOppfylt)
        assertTrue(visitor.erIkkeOppfylt)
    }

    @Test
    fun `IkkeOppfyltManueltKvalitetssikret er ikke oppfylt og ikkeOppfylt`() {
        val visitor = OppfyltVisitor()

        visitor.visitIkkeOppfyltManueltKvalitetssikret()

        assertFalse(visitor.erOppfylt)
        assertTrue(visitor.erIkkeOppfylt)
    }

    @Test
    fun `IkkeRelevant er oppfylt og ikke ikkeOppfylt`() {
        val visitor = OppfyltVisitor()

        visitor.visitIkkeRelevant()

        assertTrue(visitor.erOppfylt)
        assertFalse(visitor.erIkkeOppfylt)
    }

    @Test
    fun `Hvis alle tilstandene er oppfylt blir resultatet oppfylt`() {
        val visitor = OppfyltVisitor()

        visitor.visitOppfyltMaskineltKvalitetssikret()
        visitor.visitOppfyltMaskineltKvalitetssikret()
        visitor.visitOppfyltMaskineltKvalitetssikret()

        assertTrue(visitor.erOppfylt)
    }

    @Test
    fun `Hvis minst en av tilstandene ikke er oppfylt blir resultatet ikke oppfylt`() {
        val visitor = OppfyltVisitor()

        visitor.visitOppfyltMaskineltKvalitetssikret()
        visitor.visitIkkeOppfyltMaskineltKvalitetssikret()
        visitor.visitOppfyltMaskineltKvalitetssikret()

        assertFalse(visitor.erOppfylt)
    }

    @Test
    fun `Hvis alle tilstandene ikke er ikkeOppfylt blir resultatet ikke ikkeOppfylt`() {
        val visitor = OppfyltVisitor()

        visitor.visitOppfyltMaskinelt()
        visitor.visitOppfyltMaskinelt()
        visitor.visitOppfyltMaskinelt()

        assertFalse(visitor.erIkkeOppfylt)
    }

    @Test
    fun `Hvis minst en av tilstandene er ikkeOppfylt blir resultatet ikkeOppfylt`() {
        val visitor = OppfyltVisitor()

        visitor.visitOppfyltMaskinelt()
        visitor.visitIkkeOppfyltMaskinelt()
        visitor.visitOppfyltMaskinelt()

        assertTrue(visitor.erIkkeOppfylt)
    }
}
