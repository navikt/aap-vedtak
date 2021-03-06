
```mermaid

%%{init: {'theme': 'dark', 'themeVariables': { 'primaryColor': '#07cff6', 'textColor': '#dad9e0', 'lineColor': '#07cff6'}}}%%

graph LR

subgraph Vedtak
    %% TOPICS
    aap.soknad-sendt.v1([aap.soknad-sendt.v1])
	aap.manuell.11-12.v1([aap.manuell.11-12.v1])
	aap.manuell.11-2.v1([aap.manuell.11-2.v1])
	aap.manuell.11-29.v1([aap.manuell.11-29.v1])
	aap.manuell.11-3.v1([aap.manuell.11-3.v1])
	aap.manuell.11-4.v1([aap.manuell.11-4.v1])
	aap.manuell.11-5.v1([aap.manuell.11-5.v1])
	aap.manuell.11-6.v1([aap.manuell.11-6.v1])
	aap.manuell.beregningsdato.v1([aap.manuell.beregningsdato.v1])
	aap.sokere.v1([aap.sokere.v1])
	aap.inntekter.v1([aap.inntekter.v1])
	aap.medlem.v1-joined-aap.sokere.v1-repartition([aap.medlem.v1-joined-aap.sokere.v1-repartition])
	aap.medlem.v1([aap.medlem.v1])
    
    %% JOINS
    join-0{join}
	join-1{join}
	join-2{join}
	join-3{join}
	join-4{join}
	join-5{join}
	join-6{join}
	join-7{join}
	join-8{join}
	join-9{join}
	join-10{join}
    
    %% STATE STORES
    soker-state-store-v2[(soker-state-store-v2)]
    
    %% PROCESSOR API JOBS
    metrics-soker-state-store-v2((metrics-soker-state-store-v2))
	migrate-soker-state-store-v2((migrate-soker-state-store-v2))
    
    %% JOIN STREAMS
    aap.soknad-sendt.v1 --> join-0
	soker-state-store-v2 --> join-0
	join-0 --> |branch-soknad-inntekter-produced-behov| aap.inntekter.v1
	join-0 --> |branch-soknad-medlem-produced-behov| aap.medlem.v1
	join-0 --> |produced-ny-soker| aap.sokere.v1
	aap.manuell.11-12.v1 --> join-1
	soker-state-store-v2 --> join-1
	join-1 --> |branch-manuell-11-12-inntekter-produced-behov| aap.inntekter.v1
	join-1 --> |branch-manuell-11-12-medlem-produced-behov| aap.medlem.v1
	join-1 --> |produced-soker-med-manuell-11-12| aap.sokere.v1
	aap.manuell.11-2.v1 --> join-2
	soker-state-store-v2 --> join-2
	join-2 --> |branch-manuell-11-2-medlem-produced-behov| aap.medlem.v1
	join-2 --> |branch-manuell-11-2-inntekter-produced-behov| aap.inntekter.v1
	join-2 --> |produced-soker-med-manuell-11-2| aap.sokere.v1
	aap.manuell.11-29.v1 --> join-3
	soker-state-store-v2 --> join-3
	join-3 --> |branch-manuell-11-29-medlem-produced-behov| aap.medlem.v1
	join-3 --> |branch-manuell-11-29-inntekter-produced-behov| aap.inntekter.v1
	join-3 --> |produced-soker-med-manuell-11-29| aap.sokere.v1
	aap.manuell.11-3.v1 --> join-4
	soker-state-store-v2 --> join-4
	join-4 --> |branch-manuell-11-3-inntekter-produced-behov| aap.inntekter.v1
	join-4 --> |branch-manuell-11-3-medlem-produced-behov| aap.medlem.v1
	join-4 --> |produced-soker-med-manuell-11-3| aap.sokere.v1
	aap.manuell.11-4.v1 --> join-5
	soker-state-store-v2 --> join-5
	join-5 --> |branch-manuell-11-4-medlem-produced-behov| aap.medlem.v1
	join-5 --> |branch-manuell-11-4-inntekter-produced-behov| aap.inntekter.v1
	join-5 --> |produced-soker-med-manuell-11-4| aap.sokere.v1
	aap.manuell.11-5.v1 --> join-6
	soker-state-store-v2 --> join-6
	join-6 --> |produced-soker-med-manuell-11-5| aap.sokere.v1
	join-6 --> |branch-manuell-11-5-medlem-produced-behov| aap.medlem.v1
	join-6 --> |branch-manuell-11-5-inntekter-produced-behov| aap.inntekter.v1
	aap.manuell.11-6.v1 --> join-7
	soker-state-store-v2 --> join-7
	join-7 --> |branch-manuell-11-6-inntekter-produced-behov| aap.inntekter.v1
	join-7 --> |branch-manuell-11-6-medlem-produced-behov| aap.medlem.v1
	join-7 --> |produced-soker-med-manuell-11-6| aap.sokere.v1
	aap.manuell.beregningsdato.v1 --> join-8
	soker-state-store-v2 --> join-8
	join-8 --> |branch-manuell-beregningsdato-inntekter-produced-behov| aap.inntekter.v1
	join-8 --> |branch-manuell-beregningsdato-medlem-produced-behov| aap.medlem.v1
	join-8 --> |produced-soker-med-manuell-beregningsdato| aap.sokere.v1
	aap.inntekter.v1 --> join-9
	soker-state-store-v2 --> join-9
	join-9 --> |produced-soker-med-handtert-inntekter| aap.sokere.v1
	aap.medlem.v1-joined-aap.sokere.v1-repartition --> join-10
	soker-state-store-v2 --> join-10
	join-10 --> |produced-soker-med-medlem| aap.sokere.v1
    
    %% JOB STREAMS
    metrics-soker-state-store-v2 --> soker-state-store-v2
	migrate-soker-state-store-v2 --> soker-state-store-v2
    
    %% REPARTITION STREAMS
    aap.medlem.v1 --> |re-key| aap.medlem.v1-joined-aap.sokere.v1-repartition
end

%% COLORS
%% light    #dad9e0
%% purple   #78369f
%% pink     #c233b4
%% dark     #2a204a
%% blue     #07cff6

%% STYLES
style aap.soknad-sendt.v1 fill:#c233b4, stroke:#2a204a, stroke-width:2px, color:#2a204a
style aap.manuell.11-12.v1 fill:#c233b4, stroke:#2a204a, stroke-width:2px, color:#2a204a
style aap.manuell.11-2.v1 fill:#c233b4, stroke:#2a204a, stroke-width:2px, color:#2a204a
style aap.manuell.11-29.v1 fill:#c233b4, stroke:#2a204a, stroke-width:2px, color:#2a204a
style aap.manuell.11-3.v1 fill:#c233b4, stroke:#2a204a, stroke-width:2px, color:#2a204a
style aap.manuell.11-4.v1 fill:#c233b4, stroke:#2a204a, stroke-width:2px, color:#2a204a
style aap.manuell.11-5.v1 fill:#c233b4, stroke:#2a204a, stroke-width:2px, color:#2a204a
style aap.manuell.11-6.v1 fill:#c233b4, stroke:#2a204a, stroke-width:2px, color:#2a204a
style aap.manuell.beregningsdato.v1 fill:#c233b4, stroke:#2a204a, stroke-width:2px, color:#2a204a
style aap.sokere.v1 fill:#c233b4, stroke:#2a204a, stroke-width:2px, color:#2a204a
style aap.inntekter.v1 fill:#c233b4, stroke:#2a204a, stroke-width:2px, color:#2a204a
style aap.medlem.v1-joined-aap.sokere.v1-repartition fill:#c233b4, stroke:#2a204a, stroke-width:2px, color:#2a204a
style aap.medlem.v1 fill:#c233b4, stroke:#2a204a, stroke-width:2px, color:#2a204a
style soker-state-store-v2 fill:#78369f, stroke:#2a204a, stroke-width:2px, color:#2a204a
style metrics-soker-state-store-v2 fill:#78369f, stroke:#2a204a, stroke-width:2px, color:#2a204a
style migrate-soker-state-store-v2 fill:#78369f, stroke:#2a204a, stroke-width:2px, color:#2a204a

```
