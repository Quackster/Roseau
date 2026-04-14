package org.alexdev.roseau.stubs;

import org.alexdev.roseau.dao.PlayerDao;
import org.alexdev.roseau.game.player.Permission;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.game.player.PlayerDetails;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A {@link PlayerDao} that stores players in memory. To be used for testing purposes.
 */
public class InMemoryPlayerDaoStub implements PlayerDao {

    private final Map<String, PlayerDetails> byUsername = new HashMap<>();
    private int nextId = 1;

    @Override
    public void createPlayer(String username, String password, String email, String mission,
                             String figure, int credits, String sex, String birthday) {
        PlayerDetails details = new PlayerDetails(null);
        details.fill(nextId++, username, mission, figure, /* poolFigure= */ "", email,
                /* rank= */ 1, credits, sex, /* country= */ "", /* badge= */ "", birthday,
                /* lastonline= */ 0L, /* personalGreeting= */ "", /* tickets= */ 0);
        details.setPassword(password);
        byUsername.put(username, details);
    }

    @Override
    public boolean login(Player player, String username, String password) {
        PlayerDetails stored = byUsername.get(username);
        if (stored == null || !stored.getPassword().equals(password)) return false;
        player.getDetails().fill(stored.getID(), stored.getName(), stored.getMission(),
                stored.getFigure(), stored.getPoolFigure(), stored.getEmail(), stored.getRank(),
                stored.getCredits(), stored.getSex(), stored.getCountry(), stored.getBadge(),
                stored.getBirthday(), stored.getLastOnline(), stored.getPersonalGreeting(),
                stored.getTickets());
        return true;
    }

    @Override
    public PlayerDetails getDetails(int userID) {
        return byUsername.values().stream()
                .filter(d -> d.getID() == userID)
                .findFirst().orElse(null);
    }

    @Override
    public PlayerDetails getDetails(String username) {
        return byUsername.get(username);
    }

    @Override
    public int getId(String username) {
        PlayerDetails d = byUsername.get(username);
        return d != null ? d.getID() : -1;
    }

    @Override
    public boolean isNameTaken(String name) {
        return byUsername.containsKey(name);
    }

    @Override
    public void updatePlayer(PlayerDetails details) {
        if (byUsername.containsKey(details.getName())) {
            byUsername.put(details.getName(), details);
        }
    }

    @Override
    public void updateLastLogin(PlayerDetails details) {
        PlayerDetails stored = byUsername.get(details.getName());
        if (stored == null) return;
        stored.fill(stored.getID(), stored.getName(), stored.getMission(), stored.getFigure(),
                stored.getPoolFigure(), stored.getEmail(), stored.getRank(), stored.getCredits(),
                stored.getSex(), stored.getCountry(), stored.getBadge(), stored.getBirthday(),
                System.currentTimeMillis(), stored.getPersonalGreeting(), stored.getTickets());
    }

    @Override
    public List<Permission> getPermissions() {
        return Collections.emptyList();
    }
}
