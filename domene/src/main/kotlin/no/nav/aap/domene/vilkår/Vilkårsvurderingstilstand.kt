package no.nav.aap.domene.vilkår

import no.nav.aap.dto.DtoVilkårsvurdering
import no.nav.aap.hendelse.Hendelse

internal sealed interface Vilkårsvurderingstilstand<in PARAGRAF> {
    fun onEntry(vilkårsvurdering: PARAGRAF, hendelse: Hendelse) {}
    fun onExit(vilkårsvurdering: PARAGRAF, hendelse: Hendelse) {}

    fun erOppfylt(): Boolean
    fun erIkkeOppfylt(): Boolean

    fun gjenopprettTilstand(paragraf: PARAGRAF, vilkårsvurdering: DtoVilkårsvurdering) {}
    fun toDto(paragraf: PARAGRAF): DtoVilkårsvurdering
}
