package no.nav.aap.app.modell

class Oppgaver(val oppgaver: List<Oppgave>)
class Oppgave(
    val oppgaveId: Int,
    val personident: Personident,
    val alder: Int,
)

class Aldersvurdering(val oppgaveId: Int, val erMellom18og67: Boolean)

@JvmInline
value class Personident(val ident: String)
