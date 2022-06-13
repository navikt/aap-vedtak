
```mermaid

%%{init: {'theme': 'dark', 'themeVariables': { 'primaryColor': '#07cff6', 'textColor': '#dad9e0', 'lineColor': '#07cff6'}}}%%

graph LR

subgraph Vedtak
    %% TOPICS
    aap.soknad-sendt.v1([aap.soknad-sendt.v1])
	aap.manuell.v1([aap.manuell.v1])
	aap.sokere.v1([aap.sokere.v1])
	aap.inntekter.v1([aap.inntekter.v1])
	aap.medlem.v1-joined-aap.sokere.v1-repartition([aap.medlem.v1-joined-aap.sokere.v1-repartition])
	aap.medlem.v1([aap.medlem.v1])
    
    %% JOINS
    join-0{join}
	join-1{join}
	join-2{join}
	join-3{join}
    
    %% STATE STORES
    soker-state-store-v2[(soker-state-store-v2)]
    
    %% PROCESSOR API JOBS
    cleanup-soker-state-store-v2((cleanup-soker-state-store-v2))
	metrics-soker-state-store-v2((metrics-soker-state-store-v2))
    
    %% JOIN STREAMS
    aap.soknad-sendt.v1 --> join-0
	soker-state-store-v2 --> join-0
	join-0 --> |produced-ny-soker| aap.sokere.v1
	join-0 --> |branch-soknad-medlem-produced-behov| aap.medlem.v1
	join-0 --> |branch-soknad-inntekter-produced-behov| aap.inntekter.v1
	aap.manuell.v1 --> join-1
	soker-state-store-v2 --> join-1
	join-1 --> |branch-manuell-inntekter-produced-behov| aap.inntekter.v1
	join-1 --> |branch-manuell-medlem-produced-behov| aap.medlem.v1
	join-1 --> |produced-soker-med-handtert-losning| aap.sokere.v1
	aap.inntekter.v1 --> join-2
	soker-state-store-v2 --> join-2
	join-2 --> |produced-soker-med-handtert-inntekter| aap.sokere.v1
	aap.medlem.v1-joined-aap.sokere.v1-repartition --> join-3
	soker-state-store-v2 --> join-3
	join-3 --> |produced-soker-med-medlem| aap.sokere.v1
    
    %% JOB STREAMS
    cleanup-soker-state-store-v2 --> soker-state-store-v2
	metrics-soker-state-store-v2 --> soker-state-store-v2
    
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
style aap.manuell.v1 fill:#c233b4, stroke:#2a204a, stroke-width:2px, color:#2a204a
style aap.sokere.v1 fill:#c233b4, stroke:#2a204a, stroke-width:2px, color:#2a204a
style aap.inntekter.v1 fill:#c233b4, stroke:#2a204a, stroke-width:2px, color:#2a204a
style aap.medlem.v1-joined-aap.sokere.v1-repartition fill:#c233b4, stroke:#2a204a, stroke-width:2px, color:#2a204a
style aap.medlem.v1 fill:#c233b4, stroke:#2a204a, stroke-width:2px, color:#2a204a
style soker-state-store-v2 fill:#78369f, stroke:#2a204a, stroke-width:2px, color:#2a204a
style cleanup-soker-state-store-v2 fill:#78369f, stroke:#2a204a, stroke-width:2px, color:#2a204a
style metrics-soker-state-store-v2 fill:#78369f, stroke:#2a204a, stroke-width:2px, color:#2a204a

```