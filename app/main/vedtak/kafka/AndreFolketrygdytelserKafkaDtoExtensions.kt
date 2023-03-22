package vedtak.kafka

import no.nav.aap.dto.kafka.AndreFolketrygdytelserKafkaDto
import no.nav.aap.modellapi.LøsningParagraf_11_27_FørsteLedd_ModellApi
import no.nav.aap.modellapi.SvangerskapspengerModellApi
import no.nav.aap.modellapi.SøkerModellApi

internal fun AndreFolketrygdytelserKafkaDto.håndter(søker: SøkerModellApi) = toModellApi().håndter(søker)

fun AndreFolketrygdytelserKafkaDto.toModellApi() = LøsningParagraf_11_27_FørsteLedd_ModellApi(
    // Vi ønsker at svangeskapsløsning skal være satt lenger bakobver, selv med tomme verdier,
    // for å slippe null-safety-sjekker overalt
    svangerskapspenger = SvangerskapspengerModellApi(
        fom = response?.svangerskapspenger?.fom,
        tom = response?.svangerskapspenger?.tom,
        grad = response?.svangerskapspenger?.grad,
        vedtaksdato = response?.svangerskapspenger?.vedtaksdato
    )
)
