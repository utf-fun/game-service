package org.readutf.gameservice.social.party;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public record Party(
        @NotNull UUID id,
        @NotNull UUID owner,
        @NotNull List<UUID> members,
        @NotNull List<PartyInvite> invites,
        @NotNull AtomicBoolean open
) {

    public Party(@NotNull UUID id, @NotNull UUID owner, @NotNull List<UUID> members, @NotNull List<PartyInvite> invites, @NotNull AtomicBoolean open) {
        this.id = id;
        this.owner = owner;
        this.members = new ArrayList<>(members);
        this.invites = new ArrayList<>(invites);
        this.open = open;
    }

    public boolean isOpen() {
        return open.get();
    }

    @Nullable
    @Contract(pure = true)
    public PartyInvite getInvite(UUID id) {
        for (PartyInvite invite : invites) {
            if (invite.target() == id) {
                return invite;
            }
        }
        return null;
    }

    public void setOpen(boolean open) {
        this.open.set(open);
    }

}
