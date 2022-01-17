package no.nav.aap.app

fun resourceFile(path: String): String = object {}.javaClass.getResource(path)!!.readText()
