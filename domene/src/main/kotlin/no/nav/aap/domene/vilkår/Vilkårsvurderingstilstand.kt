package no.nav.aap.domene.vilkår

import no.nav.aap.dto.DtoVilkårsvurdering
import no.nav.aap.hendelse.Hendelse

internal sealed interface Vilkårsvurderingstilstand<PARAGRAF> {
    fun onEntry(vilkårsvurdering: PARAGRAF, hendelse: Hendelse) {}
    fun onExit(vilkårsvurdering: PARAGRAF, hendelse: Hendelse) {}

    fun erOppfylt(): Boolean
    fun erIkkeOppfylt(): Boolean

    fun gjenopprettTilstand(vilkårsvurdering: PARAGRAF, dtoVilkårsvurdering: DtoVilkårsvurdering) {}
    fun toDto(vilkårsvurdering: PARAGRAF): DtoVilkårsvurdering
}
