package org.alexdev.roseau.messages.incoming;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.player.PlayerManager;
import org.alexdev.roseau.messages.outgoing.ERROR;
import org.alexdev.roseau.server.netty.readers.NettyRequest;
import org.alexdev.roseau.stubs.GameStub;
import org.alexdev.roseau.stubs.InMemoryDaoStub;
import org.alexdev.roseau.stubs.InMemoryPlayerDaoStub;
import org.alexdev.roseau.stubs.PlayerStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

public class LOGINTest {

    private InMemoryPlayerDaoStub playerDao;
    private PlayerManager playerManager;

    @BeforeEach
    void setUp() throws Exception {
        playerDao = new InMemoryPlayerDaoStub();
        setRoseauStaticField("dao", new InMemoryDaoStub(playerDao));
        playerManager = new PlayerManager();
        setRoseauStaticField("game", new GameStub(playerManager));
    }

    @Test
    void tooFewArguments_sendsError() {
        PlayerStub player = new PlayerStub();
        NettyRequest request = new NettyRequest("LOGIN", "testuser");

        new LOGIN().handle(player, request);

        assertFalse(player.getDetails().isAuthenticated());
        assertEquals(1, player.getResponses().size());
        assertInstanceOf(ERROR.class, player.getResponses().get(0));
    }

    @Test
    void userNotFound_sendsError() {
        PlayerStub player = new PlayerStub();
        NettyRequest request = new NettyRequest("LOGIN", "unknownuser somepassword");

        new LOGIN().handle(player, request);

        assertFalse(player.getDetails().isAuthenticated());
        assertEquals(1, player.getResponses().size());
        assertInstanceOf(ERROR.class, player.getResponses().get(0));
    }

    @Test
    void wrongPassword_sendsError() {
        registerPlayer("testuser", "correctpassword");
        PlayerStub player = new PlayerStub();
        NettyRequest request = new NettyRequest("LOGIN", "testuser wrongpassword");

        new LOGIN().handle(player, request);

        assertFalse(player.getDetails().isAuthenticated());
        assertEquals(1, player.getResponses().size());
        assertInstanceOf(ERROR.class, player.getResponses().get(0));
    }

    @Test
    void correctPassword_authenticatesPlayer() {
        registerPlayer("testuser", "correctpassword");
        PlayerStub player = new PlayerStub();
        NettyRequest request = new NettyRequest("LOGIN", "testuser correctpassword");

        new LOGIN().handle(player, request);

        assertTrue(player.getDetails().isAuthenticated());
        assertEquals("correctpassword", player.getDetails().getPassword());
        assertTrue(player.getResponses().isEmpty());
    }

    @Test
    void correctPasswordWithDuplicateSession_closesDuplicateSession() {
        registerPlayer("testuser", "correctpassword");
        PlayerStub existingPlayer = new PlayerStub(/* connectionId= */ 1);
        playerDao.login(existingPlayer, "testuser", "correctpassword");
        playerManager.getPlayers().put(existingPlayer.getDetails().getID(), existingPlayer);
        PlayerStub newPlayer = new PlayerStub(/* connectionId= */ 2);
        NettyRequest request = new NettyRequest("LOGIN", "testuser correctpassword");

        new LOGIN().handle(newPlayer, request);

        assertTrue(newPlayer.getDetails().isAuthenticated());
        assertFalse(existingPlayer.getDetails().isAuthenticated());
        assertTrue(existingPlayer.getNetworkStub().isClosed());
    }

    private void registerPlayer(String username, String password) {
        playerDao.createPlayer(username, password,
                /* email= */    username + "@test.com",
                /* mission= */  "",
                /* figure= */   "hd=180-1.ch=215-91.lg=695-91",
                /* credits= */  0,
                /* sex= */      "M",
                /* birthday= */ "01-01-2000");
    }

    private static void setRoseauStaticField(String name, Object value) throws Exception {
        Field field = Roseau.class.getDeclaredField(name);
        field.setAccessible(true);
        field.set(null, value);
    }
}
