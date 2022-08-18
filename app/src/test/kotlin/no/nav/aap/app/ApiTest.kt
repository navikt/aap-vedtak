package no.nav.aap.app

import io.ktor.server.testing.*
import no.nav.aap.app.kafka.SØKERE_STORE_NAME
import no.nav.aap.app.kafka.Topics
import no.nav.aap.app.modell.*
import no.nav.aap.app.modell.InntekterKafkaDto.Response.Inntekt
import no.nav.aap.kafka.streams.test.readAndAssert
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables
import java.time.LocalDate
import java.time.LocalDateTime

internal class ApiTest {

    @Test
    fun `søker får innvilget vedtak`() {
        withTestApp { mocks ->
            val søknadTopic = mocks.kafka.inputTopic(Topics.søknad)
            val medlemTopic = mocks.kafka.inputTopic(Topics.medlem)
            val medlemOutputTopic = mocks.kafka.outputTopic(Topics.medlem)
            val manuell_11_3_Topic = mocks.kafka.inputTopic(Topics.manuell_11_3)
            val manuell_11_5_Topic = mocks.kafka.inputTopic(Topics.manuell_11_5)
            val manuell_11_6_Topic = mocks.kafka.inputTopic(Topics.manuell_11_6)
            val manuell_11_12_Topic = mocks.kafka.inputTopic(Topics.manuell_11_12)
            val manuell_11_19_Topic = mocks.kafka.inputTopic(Topics.manuell_11_19)
            val manuell_11_29_Topic = mocks.kafka.inputTopic(Topics.manuell_11_29)
            val kvalitetssikring_11_2_Topic = mocks.kafka.inputTopic(Topics.kvalitetssikring_11_2)
            val kvalitetssikring_11_3_Topic = mocks.kafka.inputTopic(Topics.kvalitetssikring_11_3)
            val kvalitetssikring_11_5_Topic = mocks.kafka.inputTopic(Topics.kvalitetssikring_11_5)
            val kvalitetssikring_11_6_Topic = mocks.kafka.inputTopic(Topics.kvalitetssikring_11_6)
            val kvalitetssikring_11_12_Topic = mocks.kafka.inputTopic(Topics.kvalitetssikring_11_12)
            val kvalitetssikring_11_19_Topic = mocks.kafka.inputTopic(Topics.kvalitetssikring_11_19)
            val kvalitetssikring_11_29_Topic = mocks.kafka.inputTopic(Topics.kvalitetssikring_11_29)
            val inntektTopic = mocks.kafka.inputTopic(Topics.inntekter)
            val inntektOutputTopic = mocks.kafka.outputTopic(Topics.inntekter)
            val iverksettVedtakTopic = mocks.kafka.outputTopic(Topics.vedtak)
            val stateStore = mocks.kafka.getStore<SøkereKafkaDto>(SØKERE_STORE_NAME)

            val fnr = "123"
            val tidspunktForVurdering = LocalDateTime.now()
            søknadTopic.produce(fnr) {
                JsonSøknad(fødselsdato = LocalDate.now().minusYears(40))
            }

            val medlemRequest = medlemOutputTopic.readValue()
            medlemTopic.produce(fnr) {
                medlemRequest.copy(
                    response = MedlemKafkaDto.Response(
                        erMedlem = MedlemKafkaDto.ErMedlem.JA,
                        begrunnelse = null
                    )
                )
            }

            manuell_11_3_Topic.produce(fnr) {
                Løsning_11_3_manuell("saksbehandler", tidspunktForVurdering, true)
            }
            manuell_11_5_Topic.produce(fnr) {
                Løsning_11_5_manuell(
                    vurdertAv = "veileder",
                    tidspunktForVurdering = tidspunktForVurdering,
                    kravOmNedsattArbeidsevneErOppfylt = true,
                    nedsettelseSkyldesSykdomEllerSkade = true
                )
            }
            manuell_11_6_Topic.produce(fnr) {
                Løsning_11_6_manuell(
                    vurdertAv = "saksbehandler",
                    tidspunktForVurdering = tidspunktForVurdering,
                    harBehovForBehandling = true,
                    harBehovForTiltak = true,
                    harMulighetForÅKommeIArbeid = true
                )
            }
            manuell_11_12_Topic.produce(fnr) {
                Løsning_11_12_ledd1_manuell(
                    vurdertAv = "saksbehandler",
                    tidspunktForVurdering = tidspunktForVurdering,
                    bestemmesAv = "soknadstidspunkt",
                    unntak = "INGEN",
                    unntaksbegrunnelse = "",
                    manueltSattVirkningsdato = LocalDate.now()
                )
            }
            manuell_11_19_Topic.produce(fnr) {
                Løsning_11_19_manuell("saksbehandler", tidspunktForVurdering, LocalDate.of(2022, 1, 1))
            }
            manuell_11_29_Topic.produce(fnr) {
                Løsning_11_29_manuell("saksbehandler", tidspunktForVurdering, true)
            }

            val inntekter: InntekterKafkaDto = inntektOutputTopic.readValue()
            inntektTopic.produce(fnr) {
                inntekter.copy(
                    response = InntekterKafkaDto.Response(
                        listOf(
                            Inntekt("321", inntekter.request.fom.plusYears(2), 400000.0),
                            Inntekt("321", inntekter.request.fom.plusYears(1), 400000.0),
                            Inntekt("321", inntekter.request.fom, 400000.0),
                        )
                    )
                )
            }

            val søker = stateStore[fnr]
            assertNotNull(søker)
            val actual = søker.toDto()

            fun løsningsid2(index: Int) =
                actual.saker.first().sakstyper.first().vilkårsvurderinger[index].løsning_11_2_maskinell!![0].løsningId

            fun løsningsid3(index: Int) =
                actual.saker.first().sakstyper.first().vilkårsvurderinger[index].løsning_11_3_manuell!![0].løsningId

            fun løsningsid5(index: Int) =
                actual.saker.first().sakstyper.first().vilkårsvurderinger[index].løsning_11_5_manuell!![0].løsningId

            fun løsningsid6(index: Int) =
                actual.saker.first().sakstyper.first().vilkårsvurderinger[index].løsning_11_6_manuell!![0].løsningId

            fun løsningsid12(index: Int) =
                actual.saker.first().sakstyper.first().vilkårsvurderinger[index].løsning_11_12_ledd1_manuell!![0].løsningId

            fun løsningsid19(index: Int) =
                actual.saker.first().sakstyper.first().vilkårsvurderinger[index].løsning_11_19_manuell!![0].løsningId

            fun løsningsid29(index: Int) =
                actual.saker.first().sakstyper.first().vilkårsvurderinger[index].løsning_11_29_manuell!![0].løsningId

            kvalitetssikring_11_2_Topic.produce(fnr) {
                Kvalitetssikring_11_2(
                    løsningId = løsningsid2(0),
                    kvalitetssikretAv = "X",
                    tidspunktForKvalitetssikring = LocalDateTime.now(),
                    erGodkjent = true,
                    begrunnelse = ""
                )
            }

            kvalitetssikring_11_3_Topic.produce(fnr) {
                Kvalitetssikring_11_3(
                    løsningId = løsningsid3(1),
                    kvalitetssikretAv = "X",
                    tidspunktForKvalitetssikring = LocalDateTime.now(),
                    erGodkjent = true,
                    begrunnelse = ""
                )
            }

            kvalitetssikring_11_5_Topic.produce(fnr) {
                Kvalitetssikring_11_5(
                    løsningId = løsningsid5(4),
                    kvalitetssikretAv = "X",
                    tidspunktForKvalitetssikring = LocalDateTime.now(),
                    erGodkjent = true,
                    begrunnelse = ""
                )
            }

            kvalitetssikring_11_6_Topic.produce(fnr) {
                Kvalitetssikring_11_6(
                    løsningId = løsningsid6(5),
                    kvalitetssikretAv = "X",
                    tidspunktForKvalitetssikring = LocalDateTime.now(),
                    erGodkjent = true,
                    begrunnelse = ""
                )
            }

            kvalitetssikring_11_12_Topic.produce(fnr) {
                Kvalitetssikring_11_12_ledd1(
                    løsningId = løsningsid12(6),
                    kvalitetssikretAv = "X",
                    tidspunktForKvalitetssikring = LocalDateTime.now(),
                    erGodkjent = true,
                    begrunnelse = ""
                )
            }

            kvalitetssikring_11_19_Topic.produce(fnr) {
                Kvalitetssikring_11_19(
                    løsningId = løsningsid19(7),
                    kvalitetssikretAv = "X",
                    tidspunktForKvalitetssikring = LocalDateTime.now(),
                    erGodkjent = true,
                    begrunnelse = ""
                )
            }

            kvalitetssikring_11_29_Topic.produce(fnr) {
                Kvalitetssikring_11_29(
                    løsningId = løsningsid29(8),
                    kvalitetssikretAv = "X",
                    tidspunktForKvalitetssikring = LocalDateTime.now(),
                    erGodkjent = true,
                    begrunnelse = ""
                )
            }

            iverksettVedtakTopic.readAndAssert()
                .hasNumberOfRecords(1)
                .hasKey(fnr)
                .hasLastValueMatching { it?.innvilget }
        }
    }

    @Test
    fun `SøkereKafkaDto opprettes med nyeste versjon`() {
        val nyeste = SøkereKafkaDto("", LocalDate.now(), emptyList())
        assertEquals(SøkereKafkaDto.VERSION, nyeste.version)
    }

    @Test
    fun `versjon på forrige søkereKafkaDto skal være 1 mindre enn nyeste versjon`() {
        val forrige = ForrigeSøkereKafkaDto("", LocalDate.now(), emptyList())
        assertEquals(SøkereKafkaDto.VERSION - 1, forrige.version)
    }
}

private fun withTestApp(test: ApplicationTestBuilder.(mocks: Mocks) -> Unit) = Mocks().use { mocks ->
    EnvironmentVariables(containerProperties()).execute {
        testApplication {
            application {
                server(mocks.kafka)
                this@testApplication.test(mocks)
            }
        }
    }
}

private fun containerProperties(): Map<String, String> = mapOf(
    "KAFKA_STREAMS_APPLICATION_ID" to "vedtak",
    "KAFKA_BROKERS" to "mock://kafka",
    "KAFKA_TRUSTSTORE_PATH" to "",
    "KAFKA_KEYSTORE_PATH" to "",
    "KAFKA_CREDSTORE_PASSWORD" to "",
    "KAFKA_CLIENT_ID" to "vedtak",
    "KAFKA_GROUP_ID" to "vedtak-1",
    "KAFKA_SCHEMA_REGISTRY" to "mock://schema-registry",
    "KAFKA_SCHEMA_REGISTRY_USER" to "",
    "KAFKA_SCHEMA_REGISTRY_PASSWORD" to "",
)
