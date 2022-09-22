package no.nav.aap.hendelse

import no.nav.aap.domene.vilkår.Paragraf_8_48
import no.nav.aap.domene.visitor.VilkårsvurderingVisitor
import no.nav.aap.modellapi.SykepengedagerModellApi
import java.time.LocalDate

internal class LøsningSykepengedager(
    private val sykepengedager: Sykepengedager
) : Hendelse() {

    internal companion object {
        internal fun Iterable<LøsningSykepengedager>.toDto() = map(LøsningSykepengedager::toDto)
    }

    enum class Kilde {
        SPLEIS, INFOTRYGD,
    }

    internal fun gjenståendeSykedager() = sykepengedager.gjenståendeSykedager()

    internal fun erRelevantFor8_48() = sykepengedager.erRelevantFor8_48()

    internal fun toDto() = sykepengedager.toDto()

    fun accept(visitor: VilkårsvurderingVisitor) {

    }

    internal sealed class Sykepengedager {

        internal abstract fun gjenståendeSykedager(): Int
        internal abstract fun toDto(): SykepengedagerModellApi
        internal abstract fun erRelevantFor8_48(): Boolean

        internal class Har(
            private val gjenståendeSykedager: Int,
            private val foreløpigBeregnetSluttPåSykepenger: LocalDate,
            private val kilde: Kilde,
        ) : Sykepengedager() {
            override fun gjenståendeSykedager() = gjenståendeSykedager

            override fun toDto() = SykepengedagerModellApi(
                sykepengedager = SykepengedagerModellApi.Sykepengedager(
                gjenståendeSykedager = gjenståendeSykedager,
                foreløpigBeregnetSluttPåSykepenger = foreløpigBeregnetSluttPåSykepenger,
                kilde = kilde.name
                )
            )

            override fun erRelevantFor8_48() = true
        }

        internal class HarIkke : Sykepengedager() {
            override fun gjenståendeSykedager() = 260 // Du har ikke brukt noen sykepengedager

            override fun toDto() = SykepengedagerModellApi(null)

            override fun erRelevantFor8_48(): Boolean = false
        }
    }
}
