package no.nav.aap.app.modell

data class Oppgaver(val oppgaver: List<Oppgave>)

data class Oppgave(
    val oppgaveId: Int,
    val personident: Personident,
    val alder: Int,
)

data class Aldersvurdering(val oppgaveId: Int, val erMellom18og67: Boolean)

data class Personident(val ident: String)
