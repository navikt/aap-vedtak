package no.nav.aap.visitor

import java.time.LocalDate

internal interface EntitetVisitor {
    fun visitFødselsdato(dato: LocalDate) {}
    fun visitGrunnlagsfaktor(verdi: Double) {}
    fun visitPersonident(ident: String) {}
}
