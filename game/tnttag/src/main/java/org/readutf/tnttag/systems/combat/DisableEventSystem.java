package org.readutf.tnttag.systems.combat;

import java.util.Arrays;
import java.util.List;
import net.minestom.server.event.trait.CancellableEvent;
import org.jetbrains.annotations.NotNull;
import org.readutf.engine.event.listener.ListenerData;
import org.readutf.engine.feature.System;

public class DisableEventSystem implements System {

    private final Class<? extends CancellableEvent>[] events;

    @SafeVarargs
    public DisableEventSystem(Class<? extends CancellableEvent>... events) {
        this.events = events;
    }

    @Override
    public @NotNull List<ListenerData> getListeners() {
        return Arrays.stream(events).map(this::getListenerData).toList();
    }

    public ListenerData getListenerData(Class<? extends CancellableEvent> event) {
        return ListenerData.of(event, event1 -> {
            if(event1 instanceof CancellableEvent cancellableEvent) {
                cancellableEvent.setCancelled(true);
            }
        });
    }

}
