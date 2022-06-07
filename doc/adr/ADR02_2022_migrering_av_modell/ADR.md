# Migrering av modell søkere og mottakere
Denne ADR'en fokuserer på migrering av data på topics `sokere` og
`mottakere` da dette er streams og ikke enkle topics med "fire and
forget".


## Problem
Siden vi bruker Kafka som plattform for kommunikasjon mellom apper, 
så får vi et problem dersom en app som produserer meldinger endrer 
på formatet på en sånn måte at det ikke kan parses av konsumentene.

En løsning på dette er å slette alle meldinger på topic, oppgradere 
alle apper og begynne på nytt. Men det kan vi ikke gjøre med streams
i prod, da vi trenger å ha alle data, så vi tar ikke dette med som
et alternativ i diskusjonen.

## Produsent
Det er kun en app som er produsent på disse topicene (`vedtak`
produserer på `sokere` og `utbetaling` produserer på `mottakere`).

### Løsning 1 - Vent med oppstart
- Deploy app
- I startup:
  - Sjekk om det er kommet en ny versjon av domenemodellen
  - Kjør migreringskode på alle data i streamen
  - Vent med å joine inn nye data til migrering er fullført

### Løsning 2 - Forkast joins
- Deploy app
- I startup:
    - Sjekk om det er kommet en ny versjon av domenemodellen
    - Kjør migreringskode på alle data i streamen i parallell
- Forkast meldinger som forsøkes å joine på data som ikke er migrert
- De meldingene som forkastes blir sendt på nytt som nye behov

## Konsument
For øyeblikket har vi kun en type konsument, og det er en konsument
som benytter seg av en database for å lagre de dataene som kommer
på streamen. Felles for begge løsningene under er at vi antar at
data med ny versjon etterhvert begynner å komme inn til konsument
når de blir migrert av produsenten.

### Løsning 1 - Stopp å lese
Det er mulig Kafka håndterer dette for oss: Dersom en melding ikke 
kan leses (fordi den har endret format og versjon), så stopper Kafka 
å lese inn og offsettet flyttes ikke.
Når konsumenten endelig er oppdatert, så kan meldingene leses inn
fra offset (der første nye melding kom), og data overskrives i 
database med de nye dataene.

### Løsning 2 - Versjonering i database
Konsumenten leser data inn helt rått og bryr seg ikke om innholdet,
men lagrer json rått i databasen med personnummer og versjon som 
nøkkel. Når konsumenten er oppdatert kan frontenden begynne å 
spørre på ny versjon og dataene konverteres riktig.

## Konlusjon