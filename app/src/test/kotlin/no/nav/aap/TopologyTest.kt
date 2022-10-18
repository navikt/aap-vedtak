package no.nav.aap

import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import no.nav.aap.app.kafka.SØKERE_STORE_NAME
import no.nav.aap.app.kafka.Topics
import no.nav.aap.app.kafka.toModellApi
import no.nav.aap.app.topology
import no.nav.aap.dto.kafka.*
import no.nav.aap.dto.kafka.InntekterKafkaDto.Response.Inntekt
import no.nav.aap.kafka.streams.KStreamsConfig
import no.nav.aap.kafka.streams.test.KafkaStreamsMock
import no.nav.aap.kafka.streams.test.readAndAssert
import no.nav.aap.kafka.streams.topology.Mermaid
import no.nav.aap.modellapi.*
import org.apache.kafka.clients.producer.MockProducer
import org.apache.kafka.streams.TestInputTopic
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime

internal class ApiTest {

    @Test
    fun `søker får innvilget vedtak`() {
        KafkaStreamsMock().apply {
            connect(
                config = KStreamsConfig("vedtak", "mock://aiven", commitIntervalMs = 0),
                registry = SimpleMeterRegistry(),
                topology = topology(SimpleMeterRegistry(), MockProducer())
            )
        }.use { kafka ->
            val søknadTopic = kafka.inputTopic(Topics.søknad)
            val medlemTopic = kafka.inputTopic(Topics.medlem)
            val medlemOutputTopic = kafka.outputTopic(Topics.medlem)
            val innstilling_11_6_Topic = kafka.inputTopic(Topics.innstilling_11_6)
            val manuell_11_3_Topic = kafka.inputTopic(Topics.manuell_11_3)
            val manuell_11_5_Topic = kafka.inputTopic(Topics.manuell_11_5)
            val manuell_11_6_Topic = kafka.inputTopic(Topics.manuell_11_6)
            val manuell_11_19_Topic = kafka.inputTopic(Topics.manuell_11_19)
            val manuell_11_29_Topic = kafka.inputTopic(Topics.manuell_11_29)
            val manuell_22_13_Topic = kafka.inputTopic(Topics.manuell_22_13)
            val kvalitetssikring_11_2_Topic = kafka.inputTopic(Topics.kvalitetssikring_11_2)
            val kvalitetssikring_11_3_Topic = kafka.inputTopic(Topics.kvalitetssikring_11_3)
            val kvalitetssikring_11_5_Topic = kafka.inputTopic(Topics.kvalitetssikring_11_5)
            val kvalitetssikring_11_6_Topic = kafka.inputTopic(Topics.kvalitetssikring_11_6)
            val kvalitetssikring_11_19_Topic = kafka.inputTopic(Topics.kvalitetssikring_11_19)
            val kvalitetssikring_11_29_Topic = kafka.inputTopic(Topics.kvalitetssikring_11_29)
            val kvalitetssikring_22_13_Topic = kafka.inputTopic(Topics.kvalitetssikring_22_13)
            val andreFolketrygdsytelserTopic = kafka.inputTopic(Topics.andreFolketrygdsytelser)
            val andreFolketrygdsytelserOutputTopic = kafka.outputTopic(Topics.andreFolketrygdsytelser)
            val inntektTopic = kafka.inputTopic(Topics.inntekter)
            val inntektOutputTopic = kafka.outputTopic(Topics.inntekter)
            val sykepengedagerTopic = kafka.inputTopic(Topics.sykepengedager)
            val sykepengedagerOutputTopic = kafka.outputTopic(Topics.sykepengedager)
            val iverksettelseAvVedtakTopic = kafka.inputTopic(Topics.iverksettelseAvVedtak)
            val iverksettVedtakTopic = kafka.outputTopic(Topics.vedtak)
            val stateStore = kafka.getStore<SøkereKafkaDto>(SØKERE_STORE_NAME)

            val fnr = "123"
            val tidspunktForVurdering = LocalDateTime.now()
            søknadTopic.produce(fnr) {
                SøknadKafkaDto(fødselsdato = LocalDate.now().minusYears(40))
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

            val sykepengedagerRequest = sykepengedagerOutputTopic.readValue()
            sykepengedagerTopic.produce(fnr) {
                sykepengedagerRequest.copy(
                    response = SykepengedagerKafkaDto.Response(
                        sykepengedager = null
                    )
                )
            }

            val andreFolketrygdytelserRequest = andreFolketrygdsytelserOutputTopic.readValue()
            andreFolketrygdsytelserTopic.produce(fnr) {
                andreFolketrygdytelserRequest.copy(
                    response = AndreFolketrygdytelserKafkaDto.Response(
                        svangerskapspenger = AndreFolketrygdytelserKafkaDto.Response.Svangerskapspenger(
                            fom = null,
                            tom = null,
                            grad = null,
                            vedtaksdato = null,
                        )
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
            innstilling_11_6_Topic.produce(fnr) {
                Innstilling_11_6(
                    vurdertAv = "veileder",
                    tidspunktForVurdering = tidspunktForVurdering,
                    harBehovForBehandling = true,
                    harBehovForTiltak = true,
                    harMulighetForÅKommeIArbeid = true
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
            manuell_22_13_Topic.produce(fnr) {
                Løsning_22_13_manuell(
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
            val actual = søker.toModellApi()

            fun løsningsid2(index: Int) =
                (actual.saker.first().sakstyper.first().vilkårsvurderinger[index] as Paragraf_11_2ModellApi).løsning_11_2_maskinell[0].løsningId

            fun løsningsid3(index: Int) =
                (actual.saker.first().sakstyper.first().vilkårsvurderinger[index] as Paragraf_11_3ModellApi).løsning_11_3_manuell[0].løsningId

            fun løsningsid5(index: Int) =
                (actual.saker.first().sakstyper.first().vilkårsvurderinger[index] as Paragraf_11_5ModellApi).løsning_11_5_manuell[0].løsningId

            fun løsningsid6(index: Int) =
                (actual.saker.first().sakstyper.first().vilkårsvurderinger[index] as Paragraf_11_6ModellApi).løsning_11_6_manuell[0].løsningId

            fun løsningsid19(index: Int) =
                (actual.saker.first().sakstyper.first().vilkårsvurderinger[index] as Paragraf_11_19ModellApi).løsning_11_19_manuell[0].løsningId

            fun løsningsid29(index: Int) =
                (actual.saker.first().sakstyper.first().vilkårsvurderinger[index] as Paragraf_11_29ModellApi).løsning_11_29_manuell[0].løsningId

            fun løsningsid2213(index: Int) =
                (actual.saker.first().sakstyper.first().vilkårsvurderinger[index] as Paragraf_22_13ModellApi).løsning_22_13_manuell[0].løsningId

            kvalitetssikring_11_2_Topic.produce(fnr) {
                Kvalitetssikring_11_2(
                    løsningId = løsningsid2(1),
                    kvalitetssikretAv = "X",
                    tidspunktForKvalitetssikring = LocalDateTime.now(),
                    erGodkjent = true,
                    begrunnelse = ""
                )
            }

            kvalitetssikring_11_3_Topic.produce(fnr) {
                Kvalitetssikring_11_3(
                    løsningId = løsningsid3(2),
                    kvalitetssikretAv = "X",
                    tidspunktForKvalitetssikring = LocalDateTime.now(),
                    erGodkjent = true,
                    begrunnelse = ""
                )
            }

            kvalitetssikring_11_5_Topic.produce(fnr) {
                Kvalitetssikring_11_5(
                    løsningId = løsningsid5(5),
                    kvalitetssikretAv = "X",
                    tidspunktForKvalitetssikring = LocalDateTime.now(),
                    erGodkjent = true,
                    begrunnelse = ""
                )
            }

            kvalitetssikring_11_6_Topic.produce(fnr) {
                Kvalitetssikring_11_6(
                    løsningId = løsningsid6(6),
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

//            kvalitetssikring_11_29_Topic.produce(fnr) {
//                Kvalitetssikring_11_29(
//                    løsningId = løsningsid29(9),
//                    kvalitetssikretAv = "X",
//                    tidspunktForKvalitetssikring = LocalDateTime.now(),
//                    erGodkjent = true,
//                    begrunnelse = ""
//                )
//            }

            kvalitetssikring_22_13_Topic.produce(fnr) {
                Kvalitetssikring_22_13(
                    løsningId = løsningsid2213(9),
                    kvalitetssikretAv = "X",
                    tidspunktForKvalitetssikring = LocalDateTime.now(),
                    erGodkjent = true,
                    begrunnelse = ""
                )
            }

            iverksettelseAvVedtakTopic.produce(fnr) {
                IverksettelseAvVedtakKafkaDto(
                    iverksattAv = "X",
                )
            }

            iverksettVedtakTopic.readAndAssert()
                .hasNumberOfRecords(1)
                .hasKey(fnr)
                .hasLastValueMatching { assertTrue(it?.innvilget ?: false) }
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

    @Test
    fun `mermaid topic diagram`() {
        val topology = topology(SimpleMeterRegistry(), MockProducer())
        val flowchart = Mermaid.graph("Vedtak", topology)
        val markdown = markdown(flowchart)
        File("../doc/topology.md").apply { writeText(markdown) }
        File("../doc/topology.mermaid").apply { writeText(flowchart) }
    }
}

inline fun <reified V : Any> TestInputTopic<String, V>.produce(key: String, value: () -> V) = pipeInput(key, value())

private fun markdown(mermaid: String) = """
```mermaid
$mermaid
```
"""
