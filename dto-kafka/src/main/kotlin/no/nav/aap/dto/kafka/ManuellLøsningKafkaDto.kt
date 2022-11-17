package no.nav.aap.dto.kafka

import java.time.LocalDate
import java.time.LocalDateTime

data class Løsning_11_2_manuellKafkaDto(
    val vurdertAv: String,
    val tidspunktForVurdering: LocalDateTime,
    val erMedlem: String
)

data class Løsning_11_3_manuellKafkaDto(
    val vurdertAv: String,
    val tidspunktForVurdering: LocalDateTime,
    val erOppfylt: Boolean
)

data class Løsning_11_4_ledd2_ledd3_manuellKafkaDto(
    val vurdertAv: String,
    val tidspunktForVurdering: LocalDateTime,
    val erOppfylt: Boolean
)

data class Løsning_11_5_manuellKafkaDto(
    val vurdertAv: String,
    val tidspunktForVurdering: LocalDateTime,
    val kravOmNedsattArbeidsevneErOppfylt: Boolean,
    val kravOmNedsattArbeidsevneErOppfyltBegrunnelse: String,
    val nedsettelseSkyldesSykdomEllerSkade: Boolean,
    val nedsettelseSkyldesSykdomEllerSkadeBegrunnelse: String,
    val kilder: List<String>,
    val legeerklæringDato: LocalDate?,
    val sykmeldingDato: LocalDate?,
)

data class Løsning_11_6_manuellKafkaDto(
    val vurdertAv: String,
    val tidspunktForVurdering: LocalDateTime,
    val harBehovForBehandling: Boolean,
    val harBehovForTiltak: Boolean,
    val harMulighetForÅKommeIArbeid: Boolean,
    val individuellBegrunnelse: String?,
)

data class Løsning_11_19_manuellKafkaDto(
    val vurdertAv: String,
    val tidspunktForVurdering: LocalDateTime,
    val beregningsdato: LocalDate
)

data class Løsning_11_29_manuellKafkaDto(
    val vurdertAv: String,
    val tidspunktForVurdering: LocalDateTime,
    val erOppfylt: Boolean
)

data class Løsning_22_13_manuellKafkaDto(
    val vurdertAv: String,
    val tidspunktForVurdering: LocalDateTime,
    val bestemmesAv: String,
    val unntak: String?,
    val unntaksbegrunnelse: String?,
    val manueltSattVirkningsdato: LocalDate?,
)
