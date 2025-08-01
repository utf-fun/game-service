package org.readutf.social.party;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record PartyApiResponse(@NotNull PartyResultType resultType, @Nullable Party party) {

}
