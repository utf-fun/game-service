package org.readutf.social.party;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;

import java.util.UUID;

/**
 * Retrofit service interface for Party API operations.
 */
public interface PartyService {

    /**
     * Creates a new party with the specified owner.
     *
     * @param owner The owner of the party
     * @return API response
     */
    @PUT("/api/v1/party/{owner}")
    Call<PartyApiResponse> createParty(
            @Path("owner") UUID owner
    );

    /**
     * Invites a player to join a party.
     *
     * @param owner  The owner of the party
     * @param player The player to invite
     * @return API response
     */
    @PUT("/api/v1/party/{owner}/invite/{player}")
    Call<PartyApiResponse> invitePlayer(
            @Path("owner") UUID owner,
            @Path("player") UUID player
    );

    /**
     * Player joins an owner's party.
     *
     * @param player The player joining the party
     * @param owner  The owner of the party
     * @return API response
     */
    @PUT("/api/v1/party/{player}/join/{owner}")
    Call<PartyApiResponse> joinParty(
            @Path("player") UUID player,
            @Path("owner") UUID owner
    );

    /**
     * Opens a party for a player.
     *
     * @param player The player opening the party
     * @return API response
     */
    @PUT("/api/v1/party/{player}/open")
    Call<PartyApiResponse> openParty(
            @Path("player") UUID player
    );

    @GET("/api/v1/party/{player}")
    Call<PartyApiResponse> getParty(
            @Path("player") UUID player
    );

}