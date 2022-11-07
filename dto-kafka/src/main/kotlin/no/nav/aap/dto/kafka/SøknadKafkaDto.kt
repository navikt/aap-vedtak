package no.nav.aap.dto.kafka

import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

data class SøknadKafkaDto(
    val studier: Studier,
    val medlemsskap: Medlemskap,
    val registrerteBehandlere: List<RegistrertBehandler>,
    val andreBehandlere: List<AnnenBehandler>,
    val yrkesskadeType: Yrkesskade,
    val utbetalinger: Utbetalinger?,
    val registrerteBarn: List<BarnOgInntekt>,
    val andreBarn: List<AnnetBarnOgInntekt>,
    val vedlegg: Vedlegg?,
    val fødselsdato: LocalDate,
    val innsendingTidspunkt: LocalDateTime,
) {
    enum class Yrkesskade { JA, NEI }
}

data class AnnenBehandler(
    val type: RegistrertBehandler.BehandlerType,
    val kategori: RegistrertBehandler.BehandlerKategori?,
    val navn: Navn,
    val kontaktinformasjon: RegistrertBehandler.KontaktInformasjon,
)

data class RegistrertBehandler(
    val type: BehandlerType,
    val kategori: BehandlerKategori,
    val navn: Navn,
    val kontaktinformasjon: KontaktInformasjon,
    val erRegistrertFastlegeRiktig: Boolean?,
) {
    enum class BehandlerType {
        FASTLEGE,
        SYKMELDER,
    }

    enum class BehandlerKategori {
        LEGE,
        FYSIOTERAPEUT,
        KIROPRAKTOR,
        MANUELLTERAPEUT,
        TANNLEGE,
    }

    data class KontaktInformasjon(
        val kontor: String?,
        val orgnummer: OrgNummer?,
        val adresse: Adresse?,
        var telefon: String?,
    )
}

data class Vedlegg(val tittel: String?, val deler: List<UUID?>?)

data class Studier(
    val erStudent: StudieSvar?,
    val kommeTilbake: RadioValg?,
    val vedlegg: Vedlegg?
) {

    enum class StudieSvar {
        JA,
        NEI,
        AVBRUTT
    }
}

data class Medlemskap(
    val boddINorgeSammenhengendeSiste5: Boolean,
    val jobbetUtenforNorgeFørSyk: Boolean?,
    val jobbetSammenhengendeINorgeSiste5: Boolean?,
    val iTilleggArbeidUtenforNorge: Boolean?,
    val utenlandsopphold: List<Utenlandsopphold>,
)

data class Utenlandsopphold(
    val land: String,
    val periode: Periode,
    val arbeidet: Boolean,
    val id: String?,
)

data class BarnOgInntekt(val merEnnIG: Boolean?)
data class AnnetBarnOgInntekt(
    val barn: Barn,
    val relasjon: Relasjon,
    val merEnnIG: Boolean?,
    val vedlegg: Vedlegg?,
) {

    enum class Relasjon {
        FOSTERFORELDER,
        FORELDER,
    }
}

enum class RadioValg {
    JA,
    NEI,
    VET_IKKE,
}

data class Utbetalinger(
    val ekstraFraArbeidsgiver: FraArbeidsgiver,
    val andreStønader: List<AnnenStønad>,
) {

    data class FraArbeidsgiver(
        val fraArbeidsgiver: Boolean,
        val vedlegg: Vedlegg?,
    )

    data class AnnenStønad(
        val type: AnnenStønadstype,
        val hvemUtbetalerAFP: String?,
        val vedlegg: Vedlegg?,
    )

    enum class AnnenStønadstype {
        KVALIFISERINGSSTØNAD,
        ØKONOMISK_SOSIALHJELP,
        INTRODUKSJONSSTØNAD,
        OMSORGSSTØNAD,
        STIPEND,
        LÅN,
        AFP,
        VERV,
        UTLAND,
        ANNET,
        NEI,
    }
}

data class Navn(val fornavn: String?, val mellomnavn: String?, val etternavn: String?)
data class Periode(val fom: LocalDate, val tom: LocalDate?)

data class Adresse(
    val adressenavn: String?,
    val husbokstav: String?,
    val husnummer: String?,
    val postnummer: PostNummer?,
)

data class OrgNummer(val orgnr: String)
data class PostNummer(val postnr: String, val poststed: String?)
data class Barn(val navn: Navn, val fødseldato: LocalDate?)
