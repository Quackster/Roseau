package org.alexdev.roseau.stubs;

import org.alexdev.roseau.game.Game;
import org.alexdev.roseau.game.player.PlayerManager;

/**
 * A stub {@link Game} that routes {@link #getPlayerManager()} to a provided {@link PlayerManager},
 * allowing tests to control player lookups without a running game.
 */
public class GameStub extends Game {

    private final PlayerManager playerManager;

    public GameStub(PlayerManager playerManager) throws Exception {
        super(/* dao= */ null);
        this.playerManager = playerManager;
    }

    @Override
    public PlayerManager getPlayerManager() {
        return playerManager;
    }
}
