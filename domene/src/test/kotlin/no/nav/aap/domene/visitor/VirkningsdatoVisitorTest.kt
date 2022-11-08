package no.nav.aap.domene.visitor

import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.domene.entitet.Personident
import no.nav.aap.domene.vilkår.Paragraf_22_13
import no.nav.aap.hendelse.LøsningParagraf_22_13
import no.nav.aap.hendelse.LøsningSykepengedager
import no.nav.aap.hendelse.Søknad
import no.nav.aap.juli
import no.nav.aap.oktober
import no.nav.aap.september
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime
import java.util.*

internal class VirkningsdatoVisitorTest {

    @Test
    fun `Henter at søknadstidspunkt skal bestemme virkningsdato fra løsning`() {
        val visitor = VirkningsdatoVisitor()
        val paragraf2213 = Paragraf_22_13()
        paragraf2213.håndterSøknad(
            søknad = Søknad(
                personident = Personident("12345678910"),
                fødselsdato = Fødselsdato(16 juli 1985),
                søknadstidspunkt = (28 september 2022).atTime(12, 0),
            ),
            fødselsdato = Fødselsdato(16 juli 1985),
            vurderingsdato = 1 oktober 2022
        )
        paragraf2213.håndterLøsning(
            LøsningParagraf_22_13(
                løsningId = UUID.randomUUID(),
                vurdertAv = "X",
                tidspunktForVurdering = (1 oktober 2022).atTime(12, 0),
                bestemmesAv = LøsningParagraf_22_13.BestemmesAv.soknadstidspunkt,
                unntak = "unntak",
                unntaksbegrunnelse = "unntaksbegrunnelse",
                manueltSattVirkningsdato = null,
            )
        )
        paragraf2213.accept(visitor)

        assertEquals(LøsningParagraf_22_13.BestemmesAv.soknadstidspunkt, visitor.bestemmesAv)
        assertEquals(28 september 2022, visitor.virkningsdato)
    }

    @Test
    fun `Henter at maksdato skal bestemme virkningsdato fra løsning`() {
        val visitor = VirkningsdatoVisitor()
        LøsningSykepengedager(
            løsningId = UUID.randomUUID(),
            tidspunktForVurdering = LocalDateTime.now(),
            sykepengedager = LøsningSykepengedager.Sykepengedager.Har(
                gjenståendeSykedager = 0,
                foreløpigBeregnetSluttPåSykepenger = 23 september 2022,
                kilde = LøsningSykepengedager.Kilde.SPLEIS,
            )
        ).accept(visitor)
        LøsningParagraf_22_13(
            løsningId = UUID.randomUUID(),
            vurdertAv = "X",
            tidspunktForVurdering = (1 oktober 2022).atTime(12, 0),
            bestemmesAv = LøsningParagraf_22_13.BestemmesAv.maksdatoSykepenger,
            unntak = "unntak",
            unntaksbegrunnelse = "unntaksbegrunnelse",
            manueltSattVirkningsdato = null,
        ).accept(visitor)

        assertEquals(LøsningParagraf_22_13.BestemmesAv.maksdatoSykepenger, visitor.bestemmesAv)
        assertEquals(24 september 2022, visitor.virkningsdato)
    }

    @Test
    fun `Henter at unntaksvurderingForhindret skal bestemme virkningsdato fra løsning og henter manuelt satt virkningsdato`() {
        val visitor = VirkningsdatoVisitor()
        LøsningParagraf_22_13(
            løsningId = UUID.randomUUID(),
            vurdertAv = "X",
            tidspunktForVurdering = (1 oktober 2022).atTime(12, 0),
            bestemmesAv = LøsningParagraf_22_13.BestemmesAv.unntaksvurderingForhindret,
            unntak = "unntak",
            unntaksbegrunnelse = "unntaksbegrunnelse",
            manueltSattVirkningsdato = 15 september 2022,
        ).accept(visitor)

        assertEquals(LøsningParagraf_22_13.BestemmesAv.unntaksvurderingForhindret, visitor.bestemmesAv)
        assertEquals(15 september 2022, visitor.virkningsdato)
    }

    @Test
    fun `Feiler ved henting av bestemmesAv hvis løsning ikke finnes`() {
        val visitor = VirkningsdatoVisitor()

        assertThrows<UninitializedPropertyAccessException> { visitor.bestemmesAv }
    }
}
