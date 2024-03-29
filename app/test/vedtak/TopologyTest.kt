package vedtak

import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.jackson.*
import io.ktor.server.config.*
import io.ktor.server.testing.*
import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import no.nav.aap.dto.kafka.*
import no.nav.aap.dto.kafka.InntekterKafkaDto.Response.Inntekt
import no.nav.aap.kafka.streams.v2.Topology
import no.nav.aap.kafka.streams.v2.config.StreamsConfig
import no.nav.aap.kafka.streams.v2.test.StreamsMock
import no.nav.aap.modellapi.*
import org.apache.kafka.clients.producer.MockProducer
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import vedtak.kafka.Tables
import vedtak.kafka.Topics
import vedtak.kafka.toModellApi
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.test.Ignore

internal class ApiTest {

    @Test
    fun `søker får innvilget vedtak`() {

        fun mockKafkaResponses(topology: Topology) {
            topology
                .consumeAgain(Topics.subscribeSykepengedager)
                .map { _ -> SykepengedagerKafkaDto(response = SykepengedagerKafkaDto.Response(sykepengedager = null)) }
                .produce(Topics.sykepengedager)

            topology.consumeAgain(Topics.medlem)
                .filter { it.response == null }
                .map { req -> req.copy(response = MedlemKafkaDto.Response(MedlemKafkaDto.ErMedlem.JA, null)) }
                .produce(Topics.medlem)

            topology.consumeAgain(Topics.andreFolketrygdsytelser)
                .filter { it.response == null }
                .map { req ->
                    req.copy(
                        response = AndreFolketrygdytelserKafkaDto.Response(
                            AndreFolketrygdytelserKafkaDto.Response.Svangerskapspenger(null, null, null, null)
                        )
                    )
                }
                .produce(Topics.andreFolketrygdsytelser)


            topology.consumeAgain(Topics.inntekter)
                .filter { it.response == null }
                .map { inntekter ->
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
                .produce(Topics.inntekter)
        }

        StreamsMock().apply {
            connect(
                config = StreamsConfig("vedtak", "mock://aiven"),
                registry = SimpleMeterRegistry(),
                topology = topology(SimpleMeterRegistry(), MockProducer(), true).apply(::mockKafkaResponses)
            )
        }.use { kafka ->
            kafka.testTopic(Topics.sykepengedager)
            kafka.testTopic(Topics.subscribeSykepengedager)
            kafka.testTopic(Topics.medlem)
            kafka.testTopic(Topics.andreFolketrygdsytelser)
            kafka.testTopic(Topics.inntekter)

            val søknadTopic = kafka.testTopic(Topics.søknad)
            val innstilling_11_6_Topic = kafka.testTopic(Topics.innstilling_11_6)
            val manuell_11_3_Topic = kafka.testTopic(Topics.manuell_11_3)
            val manuell_11_5_Topic = kafka.testTopic(Topics.manuell_11_5)
            val manuell_11_6_Topic = kafka.testTopic(Topics.manuell_11_6)
            val manuell_11_19_Topic = kafka.testTopic(Topics.manuell_11_19)
            val manuell_11_29_Topic = kafka.testTopic(Topics.manuell_11_29)
            val manuell_22_13_Topic = kafka.testTopic(Topics.manuell_22_13)
            val kvalitetssikring_11_2_Topic = kafka.testTopic(Topics.kvalitetssikring_11_2)
            val kvalitetssikring_11_3_Topic = kafka.testTopic(Topics.kvalitetssikring_11_3)
            val kvalitetssikring_11_5_Topic = kafka.testTopic(Topics.kvalitetssikring_11_5)
            val kvalitetssikring_11_6_Topic = kafka.testTopic(Topics.kvalitetssikring_11_6)
            val kvalitetssikring_11_19_Topic = kafka.testTopic(Topics.kvalitetssikring_11_19)
            kafka.testTopic(Topics.kvalitetssikring_11_29)
            val kvalitetssikring_22_13_Topic = kafka.testTopic(Topics.kvalitetssikring_22_13)
            val iverksettelseAvVedtakTopic = kafka.testTopic(Topics.iverksettelseAvVedtak)
            val iverksettVedtakTopic = kafka.testTopic(Topics.vedtak)
            val stateStore = kafka.getStore(Tables.søkere)

            val fnr = "123"
            val tidspunktForVurdering = LocalDateTime.now()
            søknadTopic.produce(fnr) {
                SøknadKafkaDto(
                    sykepenger = false,
                    ferie = null,
                    studier = Studier(
                        erStudent = Studier.StudieSvar.NEI,
                        kommeTilbake = null,
                        vedlegg = emptyList(),
                    ),
                    medlemsskap = Medlemskap(
                        boddINorgeSammenhengendeSiste5 = true,
                        jobbetUtenforNorgeFørSyk = false,
                        jobbetSammenhengendeINorgeSiste5 = null,
                        iTilleggArbeidUtenforNorge = null,
                        utenlandsopphold = emptyList(),
                    ),
                    registrerteBehandlere = emptyList(),
                    andreBehandlere = emptyList(),
                    yrkesskadeType = SøknadKafkaDto.Yrkesskade.NEI,
                    utbetalinger = null,
                    tilleggsopplysninger = null,
                    registrerteBarn = emptyList(),
                    andreBarn = emptyList(),
                    vedlegg = emptyList(),
                    fødselsdato = LocalDate.now().minusYears(40),
                    innsendingTidspunkt = LocalDateTime.now(),
                )
            }

            manuell_11_3_Topic.produce(fnr) {
                Løsning_11_3_manuellKafkaDto("saksbehandler", tidspunktForVurdering, true)
            }

            manuell_11_5_Topic.produce(fnr) {
                Løsning_11_5_manuellKafkaDto(
                    vurdertAv = "veileder",
                    tidspunktForVurdering = tidspunktForVurdering,
                    kravOmNedsattArbeidsevneErOppfylt = true,
                    kravOmNedsattArbeidsevneErOppfyltBegrunnelse = "Begrunnelse",
                    nedsettelseSkyldesSykdomEllerSkade = true,
                    nedsettelseSkyldesSykdomEllerSkadeBegrunnelse = "Begrunnelse",
                    kilder = emptyList(),
                    legeerklæringDato = null,
                    sykmeldingDato = null,
                )
            }

            innstilling_11_6_Topic.produce(fnr) {
                Innstilling_11_6KafkaDto(
                    vurdertAv = "veileder",
                    tidspunktForVurdering = tidspunktForVurdering,
                    harBehovForBehandling = true,
                    harBehovForTiltak = true,
                    harMulighetForÅKommeIArbeid = true,
                    individuellBegrunnelse = "Begrunnelse",
                )
            }

            manuell_11_6_Topic.produce(fnr) {
                Løsning_11_6_manuellKafkaDto(
                    vurdertAv = "saksbehandler",
                    tidspunktForVurdering = tidspunktForVurdering,
                    harBehovForBehandling = true,
                    harBehovForTiltak = true,
                    harMulighetForÅKommeIArbeid = true,
                    individuellBegrunnelse = "Begrunnelse",
                )
            }

            manuell_22_13_Topic.produce(fnr) {
                Løsning_22_13_manuellKafkaDto(
                    vurdertAv = "saksbehandler",
                    tidspunktForVurdering = tidspunktForVurdering,
                    bestemmesAv = "soknadstidspunkt",
                    unntak = "INGEN",
                    unntaksbegrunnelse = "",
                    manueltSattVirkningsdato = LocalDate.now(),
                    begrunnelseForAnnet = null,
                )
            }

            manuell_11_19_Topic.produce(fnr) {
                Løsning_11_19_manuellKafkaDto("saksbehandler", tidspunktForVurdering, LocalDate.of(2022, 1, 1))
            }

            manuell_11_29_Topic.produce(fnr) {
                Løsning_11_29_manuellKafkaDto("saksbehandler", tidspunktForVurdering, true)
            }

            val søker = requireNotNull(stateStore[fnr]).søkereKafkaDto
            assertNotNull(søker)
            val actual = søker.toModellApi()

            fun løsningsid2(index: Int) =
                (actual.saker.first().sakstyper.first().vilkårsvurderinger[index] as Paragraf_11_2ModellApi).løsning_11_2_maskinell[0].løsningId

            fun løsningsid3(index: Int) =
                (actual.saker.first().sakstyper.first().vilkårsvurderinger[index] as Paragraf_11_3ModellApi).totrinnskontroller[0].løsning.løsningId

            fun løsningsid5(index: Int) =
                (actual.saker.first().sakstyper.first().vilkårsvurderinger[index] as Paragraf_11_5ModellApi).totrinnskontroller[0].løsning.løsningId

            fun løsningsid6(index: Int) =
                (actual.saker.first().sakstyper.first().vilkårsvurderinger[index] as Paragraf_11_6ModellApi).totrinnskontroller[0].løsning.løsningId

            fun løsningsid19(index: Int) =
                (actual.saker.first().sakstyper.first().vilkårsvurderinger[index] as Paragraf_11_19ModellApi).totrinnskontroller[0].løsning.løsningId

            fun løsningsid29(index: Int) =
                (actual.saker.first().sakstyper.first().vilkårsvurderinger[index] as Paragraf_11_29ModellApi).totrinnskontroller[0].løsning.løsningId

            fun løsningsid2213(index: Int) =
                (actual.saker.first().sakstyper.first().vilkårsvurderinger[index] as Paragraf_22_13ModellApi).totrinnskontroller[0].løsning.løsningId

            kvalitetssikring_11_2_Topic.produce(fnr) {
                Kvalitetssikring_11_2KafkaDto(
                    løsningId = løsningsid2(1),
                    kvalitetssikretAv = "X",
                    tidspunktForKvalitetssikring = LocalDateTime.now(),
                    erGodkjent = true,
                    begrunnelse = ""
                )
            }

            kvalitetssikring_11_3_Topic.produce(fnr) {
                Kvalitetssikring_11_3KafkaDto(
                    løsningId = løsningsid3(2),
                    kvalitetssikretAv = "X",
                    tidspunktForKvalitetssikring = LocalDateTime.now(),
                    erGodkjent = true,
                    begrunnelse = ""
                )
            }

            kvalitetssikring_11_5_Topic.produce(fnr) {
                Kvalitetssikring_11_5KafkaDto(
                    løsningId = løsningsid5(5),
                    kvalitetssikretAv = "X",
                    tidspunktForKvalitetssikring = LocalDateTime.now(),
                    kravOmNedsattArbeidsevneErGodkjent = true,
                    kravOmNedsattArbeidsevneErGodkjentBegrunnelse = null,
                    nedsettelseSkyldesSykdomEllerSkadeErGodkjent = true,
                    nedsettelseSkyldesSykdomEllerSkadeErGodkjentBegrunnelse = null,
                )
            }

            kvalitetssikring_11_6_Topic.produce(fnr) {
                Kvalitetssikring_11_6KafkaDto(
                    løsningId = løsningsid6(6),
                    kvalitetssikretAv = "X",
                    tidspunktForKvalitetssikring = LocalDateTime.now(),
                    erGodkjent = true,
                    begrunnelse = ""
                )
            }

            kvalitetssikring_11_19_Topic.produce(fnr) {
                Kvalitetssikring_11_19KafkaDto(
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
                Kvalitetssikring_22_13KafkaDto(
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

            iverksettVedtakTopic.assertThat()
                .hasNumberOfRecords(1)
                .hasKey(fnr)
                .hasLastValueMatching { assertTrue(it?.innvilget ?: false) }
        }
    }

    @Test
    @Ignore
    fun `Tester metrikker`() {
        val kafka = StreamsMock()
        testApplication {
            environment {
                config = MapApplicationConfig(
                    "TOGGLE_LES_SOKNADER" to "true",
                    "KAFKA_STREAMS_APPLICATION_ID" to "test",
                    "KAFKA_BROKERS" to "mock://kafka",
                    "KAFKA_TRUSTSTORE_PATH" to "",
                    "KAFKA_KEYSTORE_PATH" to "",
                    "KAFKA_CREDSTORE_PASSWORD" to ""
                )
            }
            application {
                server(kafka)
                val soknadTopic = kafka.testTopic(Topics.søknad)
                soknadTopic.produce("123") {
                    SøknadKafkaDto(
                        sykepenger = false,
                        ferie = null,
                        studier = Studier(
                            erStudent = Studier.StudieSvar.NEI,
                            kommeTilbake = null,
                            vedlegg = emptyList(),
                        ),
                        medlemsskap = Medlemskap(
                            boddINorgeSammenhengendeSiste5 = true,
                            jobbetUtenforNorgeFørSyk = false,
                            jobbetSammenhengendeINorgeSiste5 = null,
                            iTilleggArbeidUtenforNorge = null,
                            utenlandsopphold = emptyList(),
                        ),
                        registrerteBehandlere = emptyList(),
                        andreBehandlere = emptyList(),
                        yrkesskadeType = SøknadKafkaDto.Yrkesskade.NEI,
                        utbetalinger = null,
                        tilleggsopplysninger = null,
                        registrerteBarn = emptyList(),
                        andreBarn = emptyList(),
                        vedlegg = emptyList(),
                        fødselsdato = LocalDate.now().minusYears(40),
                        innsendingTidspunkt = LocalDateTime.now(),
                    )
                }
            }
            val client = createClient { install(ContentNegotiation) { jackson { } } }
            client.get("/actuator/metrics")
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
    fun `oppdaterer søker med ny personident`() {
        StreamsMock().apply {
            connect(
                config = StreamsConfig("vedtak", "mock://aiven"),
                registry = SimpleMeterRegistry(),
                topology = topology(SimpleMeterRegistry(), MockProducer(), true)
            )
        }.use { kafka ->
            val søkere = kafka.testTopic(Topics.søkere)
            val endredePersonidenter = kafka.testTopic(Topics.endredePersonidenter)
            val søker = SøkereKafkaDtoHistorikk(
                søkereKafkaDto = SøkereKafkaDto(
                    personident = "123",
                    fødselsdato = LocalDate.now(),
                    saker = emptyList()
                ),
                forrigeSøkereKafkaDto = ForrigeSøkereKafkaDto(
                    personident = "123",
                    fødselsdato = LocalDate.now(),
                    saker = emptyList()
                )
            )

            søkere.produce("123") { søker }
            endredePersonidenter.produce("123") { "456" }

            søkere.assertThat()
                .hasNumberOfRecords(2)
//                .containsTombstone("123") // TODO: husk å asserte denne hvis vi sender tombstone likevel
                .hasValuesForPredicate("456") { it == søker }
                .hasValuesForPredicate("123") { it == søker }
        }

    }
}
