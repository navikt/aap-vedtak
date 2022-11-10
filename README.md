![img](doc/logo.jpg)

Vedtaksapplikasjon for AAP

# Topology
See mermaid representation of the topology: <br>
[doc/topology.md](doc/topology.md)

# Migrering
Hvis man skal gjøre endringer i SøkereKafkaDto, vil dette føre til en migrering.

## Før man endrer SøkereKafkaDto
 - Merk SøkereKafkaDto.kt og ForrigeSøkereKafkaDto.kt og trykk `Cmd + D` (diff)
 - Gjør filene like bortsett fra øverst og nederst (gjør data-klassene like)
 - Øk VERSION i SøkereKafkaDto med 1

## Etter man har endret SøkereKafkaDto
 - Fiks compile feil i ForrigeSøkereKafkaDto
 - Rydd opp i det som er annotert med FIXME fra forrige migrering
 - Gjør migreringslogikk i ForrigeSøkereKafkaDtoExtensions
 - Annoter denne logikken med en FIXME