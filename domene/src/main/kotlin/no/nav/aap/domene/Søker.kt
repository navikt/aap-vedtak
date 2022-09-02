package no.nav.aap.domene

import no.nav.aap.domene.Sak.Companion.toDto
import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.domene.entitet.Personident
import no.nav.aap.domene.vilkår.Vilkårsvurdering
import no.nav.aap.modellapi.SøkerModellApi
import no.nav.aap.hendelse.*

class Søker private constructor(
    private val personident: Personident,
    private val fødselsdato: Fødselsdato,
    private val saker: MutableList<Sak>
) {
    constructor(personident: Personident, fødselsdato: Fødselsdato) : this(personident, fødselsdato, mutableListOf())

    fun håndterSøknad(søknad: Søknad) {
        val sak = Sak()
        saker.add(sak)
        sak.håndterSøknad(søknad, fødselsdato)
    }

    internal fun <T : Hendelse> håndterLøsning(løsning: T, håndter: Vilkårsvurdering<*>.(T) -> Unit) {
        saker.forEach { it.håndterLøsning(løsning, håndter) }
    }

    internal fun håndterLøsning(løsning: LøsningInntekter) {
        saker.forEach { it.håndterLøsning(løsning, fødselsdato) }
    }

    internal fun <T : Hendelse> håndterKvalitetssikring(kvalitetssikring: T, håndter: Vilkårsvurdering<*>.(T) -> Unit) {
        saker.forEach { it.håndterKvalitetssikring(kvalitetssikring, håndter) }
    }

    internal fun håndterLøsning(løsning: LøsningSykepengedager) {
        saker.forEach { it.håndterLøsning(løsning) }
    }

    internal fun håndterIverksettelse(iverksettelseAvVedtak: IverksettelseAvVedtak) {
        saker.forEach { it.håndterIverksettelse(iverksettelseAvVedtak) }
    }

    fun toDto() = SøkerModellApi(
        personident = personident.toDto(),
        fødselsdato = fødselsdato.toDto(),
        saker = saker.toDto()
    )

    companion object {
        fun gjenopprett(søker: SøkerModellApi): Søker = Søker(
            personident = Personident(søker.personident),
            fødselsdato = Fødselsdato(søker.fødselsdato),
            saker = søker.saker.map(Sak::gjenopprett).toMutableList(),
        )
    }
}
