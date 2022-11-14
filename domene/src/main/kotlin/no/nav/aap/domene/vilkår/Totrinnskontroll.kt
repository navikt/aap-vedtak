package no.nav.aap.domene.vilkår

import no.nav.aap.domene.visitor.VilkårsvurderingVisitor
import no.nav.aap.modellapi.KvalitetssikringModellApi
import no.nav.aap.modellapi.LøsningModellApi
import no.nav.aap.modellapi.TotrinnskontrollModellApi

internal class Totrinnskontroll<LØSNING, KVALITETSSIKRING> private constructor(
    private val løsning: LØSNING,
    private var kvalitetssikring: KVALITETSSIKRING?
) where LØSNING : Løsning<LØSNING, KVALITETSSIKRING>,
        KVALITETSSIKRING : Kvalitetssikring<LØSNING, KVALITETSSIKRING> {

    internal constructor(løsning: LØSNING) : this(løsning, null)

    internal fun erTotrinnskontrollGjennomført() = kvalitetssikring != null

    internal fun leggTilKvalitetssikring(kvalitetssikring: KVALITETSSIKRING) {
        this.kvalitetssikring = kvalitetssikring
    }

    internal fun accept(visitor: VilkårsvurderingVisitor) {
        løsning.accept(visitor)
    }

    internal fun <LØSNING_MODELL_API : LøsningModellApi, KVALITETSSIKRING_MODELL_API : KvalitetssikringModellApi> toDto(
        toLøsningDto: LØSNING.() -> LØSNING_MODELL_API,
        toKvalitetssikringDto: KVALITETSSIKRING.() -> KVALITETSSIKRING_MODELL_API
    ) = TotrinnskontrollModellApi(
        løsning = løsning.toLøsningDto(),
        kvalitetssikring = kvalitetssikring?.toKvalitetssikringDto()
    )

    internal companion object {
        internal fun <LØSNING : Løsning<LØSNING, KVALITETSSIKRING>, KVALITETSSIKRING : Kvalitetssikring<LØSNING, KVALITETSSIKRING>> Iterable<Totrinnskontroll<LØSNING, KVALITETSSIKRING>>.leggTilKvalitetssikring(
            kvalitetssikring: KVALITETSSIKRING
        ) {
            this.forEach { it.løsning.matchMedKvalitetssikring(it, kvalitetssikring) }
        }

        internal fun <LØSNING, KVALITETSSIKRING, LØSNING_MODELL_API, KVALITETSSIKRING_MODELL_API> Iterable<Totrinnskontroll<LØSNING, KVALITETSSIKRING>>.toDto(
            toLøsningDto: LØSNING.() -> LØSNING_MODELL_API,
            toKvalitetssikringDto: KVALITETSSIKRING.() -> KVALITETSSIKRING_MODELL_API
        ) where LØSNING : Løsning<LØSNING, KVALITETSSIKRING>,
                KVALITETSSIKRING : Kvalitetssikring<LØSNING, KVALITETSSIKRING>,
                LØSNING_MODELL_API : LøsningModellApi,
                KVALITETSSIKRING_MODELL_API : KvalitetssikringModellApi =
            this.map { it.toDto(toLøsningDto, toKvalitetssikringDto) }

        internal fun Iterable<Totrinnskontroll<*, *>>.toDto(): List<TotrinnskontrollModellApi<LøsningModellApi, KvalitetssikringModellApi>> =
            this.map { totrinnskontroll ->
                totrinnskontroll.toDto(
                    toLøsningDto = { toDto() },
                    toKvalitetssikringDto = { toDto() }
                )
            }

        internal fun <LØSNING, KVALITETSSIKRING, LØSNING_MODELL_API, KVALITETSSIKRING_MODELL_API> Iterable<TotrinnskontrollModellApi<LØSNING_MODELL_API, KVALITETSSIKRING_MODELL_API>>.gjenopprett(
            gjenopprettToLøsning: LØSNING_MODELL_API.() -> LØSNING,
            gjenopprettToKvalitetssikring: KVALITETSSIKRING_MODELL_API.() -> KVALITETSSIKRING
        ) where LØSNING : Løsning<LØSNING, KVALITETSSIKRING>,
                KVALITETSSIKRING : Kvalitetssikring<LØSNING, KVALITETSSIKRING>,
                LØSNING_MODELL_API : LøsningModellApi,
                KVALITETSSIKRING_MODELL_API : KvalitetssikringModellApi =
            this.map { totrinnskontrollModellApi ->
                Totrinnskontroll(
                    løsning = totrinnskontrollModellApi.løsning.gjenopprettToLøsning(),
                    kvalitetssikring = totrinnskontrollModellApi.kvalitetssikring?.gjenopprettToKvalitetssikring()
                )
            }
    }
}
