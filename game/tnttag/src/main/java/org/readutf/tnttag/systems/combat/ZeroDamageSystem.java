package org.readutf.tnttag.systems.combat;

import io.github.togar2.pvp.events.FinalDamageEvent;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.readutf.engine.event.listener.ListenerData;
import org.readutf.engine.event.listener.TypedGameListener;
import org.readutf.engine.feature.System;

public class ZeroDamageSystem implements System {

    @Override
    public @NotNull List<ListenerData> getListeners() {
        return List.of(ListenerData.of(FinalDamageEvent.class, listener));
    }

    private final TypedGameListener<FinalDamageEvent> listener = event -> {
        event.getDamage().setAmount(0);
    };

}
