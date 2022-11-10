package no.nav.aap.dto.kafka

data class TotrinnskontrollKafkaDto(
    val løsning: SøkereKafkaDto.LøsningParagraf_11_5,
    val kvalitetssikring: SøkereKafkaDto.KvalitetssikringParagraf_11_5?
)
