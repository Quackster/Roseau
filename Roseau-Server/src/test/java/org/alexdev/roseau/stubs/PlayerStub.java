package org.alexdev.roseau.stubs;

import com.google.common.collect.ImmutableList;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.messages.OutgoingMessageComposer;

/**
 * A stub {@link Player} backed by a {@link PlayerNetworkStub} that stores all responses sent to the player,
 * accessible via {@link #getResponses}.
 */
public class PlayerStub extends Player {

    private final PlayerNetworkStub networkStub;

    public PlayerStub() {
        this(new PlayerNetworkStub());
    }

    public PlayerStub(int connectionId) {
        this(new PlayerNetworkStub(connectionId, /* serverPort= */ 1000));
    }

    private PlayerStub(PlayerNetworkStub networkStub) {
        super(networkStub);
        this.networkStub = networkStub;
    }

    public ImmutableList<OutgoingMessageComposer> getResponses() {
        return networkStub.getResponses();
    }

    public PlayerNetworkStub getNetworkStub() {
        return networkStub;
    }
}
