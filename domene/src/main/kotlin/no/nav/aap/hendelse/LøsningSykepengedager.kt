package no.nav.aap.hendelse

import no.nav.aap.domene.visitor.VilkårsvurderingVisitor
import no.nav.aap.modellapi.SykepengedagerModellApi
import java.time.LocalDate
import java.util.*

internal class LøsningSykepengedager(
    private val sykepengedager: Sykepengedager
) : Hendelse() {

    internal companion object {
        internal fun Iterable<LøsningSykepengedager>.toDto() = map(LøsningSykepengedager::toDto)
    }

    internal enum class Kilde {
        SPLEIS, INFOTRYGD,
    }

    internal fun gjenståendeSykedager() = sykepengedager.gjenståendeSykedager()
    internal fun erRelevantFor8_48() = sykepengedager.erRelevantFor8_48()

    internal fun accept(visitor: VilkårsvurderingVisitor) {
        sykepengedager.accept(this, visitor)
    }

    internal fun toDto() = sykepengedager.toDto()

    internal sealed class Sykepengedager {

        internal abstract fun gjenståendeSykedager(): Int
        internal abstract fun erRelevantFor8_48(): Boolean

        internal abstract fun accept(løsning: LøsningSykepengedager, visitor: VilkårsvurderingVisitor)

        internal abstract fun toDto(): SykepengedagerModellApi

        internal class Har(
            private val gjenståendeSykedager: Int,
            private val foreløpigBeregnetSluttPåSykepenger: LocalDate,
            private val kilde: Kilde,
        ) : Sykepengedager() {
            override fun gjenståendeSykedager() = gjenståendeSykedager
            override fun erRelevantFor8_48() = true

            private fun virkningsdato() = foreløpigBeregnetSluttPåSykepenger.plusDays(1)

            override fun accept(løsning: LøsningSykepengedager, visitor: VilkårsvurderingVisitor) {
                //FIXME: løsningId
                visitor.visitLøsningParagraf_8_48Har(løsning, UUID.randomUUID(), virkningsdato())
            }

            override fun toDto() = SykepengedagerModellApi(
                sykepengedager = SykepengedagerModellApi.Sykepengedager(
                    gjenståendeSykedager = gjenståendeSykedager,
                    foreløpigBeregnetSluttPåSykepenger = foreløpigBeregnetSluttPåSykepenger,
                    kilde = kilde.name
                )
            )
        }

        internal object HarIkke : Sykepengedager() {
            override fun gjenståendeSykedager() = 260 // Du har ikke brukt noen sykepengedager
            override fun erRelevantFor8_48(): Boolean = false

            override fun accept(løsning: LøsningSykepengedager, visitor: VilkårsvurderingVisitor) {
                //FIXME: løsningId
                visitor.visitLøsningParagraf_8_48HarIkke(løsning, UUID.randomUUID())
            }

            override fun toDto() = SykepengedagerModellApi(null)
        }
    }
}
