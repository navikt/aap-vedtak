package no.nav.aap.domene

import java.time.LocalDate


class Søker(
    private val personident: Personident,
    private val fødselsdato: Fødselsdato,
    private val navn: Navn,
    private val saker: List<Sak>,
    private val arbeidsgivere: List<Arbeidsgiver>
) {
    fun håndterSøknad(søknad: Søknad) {
        //finn eller opprett sak
        //send søknad til sak
    }
}

class Personident

@JvmInline
value class Fødselsdato(private val dato: LocalDate)

class Navn

class Sak(
    private val vilkårsvurderinger: List<Vilkårsvurdering>,
    private val helseopplysninger: Helseopplysninger,
    private val vedtak: Vedtak,
    private val forrigeSakMedSammeSøknad: Sak?
) {
    fun håndterSøknad(søknad: Søknad) {
        //opprett viklårsvurderinger
        //hent mer informasjon?
    }
}

class Søknad

class Vedtak(
)

class Vilkårsvurdering {

}

class Arbeidsgiver {

}

class Inntekt {

}

class Helseopplysninger
