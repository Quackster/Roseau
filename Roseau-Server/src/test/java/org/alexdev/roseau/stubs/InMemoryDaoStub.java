package org.alexdev.roseau.stubs;

import org.alexdev.roseau.dao.CatalogueDao;
import org.alexdev.roseau.dao.Dao;
import org.alexdev.roseau.dao.InventoryDao;
import org.alexdev.roseau.dao.ItemDao;
import org.alexdev.roseau.dao.MessengerDao;
import org.alexdev.roseau.dao.NavigatorDao;
import org.alexdev.roseau.dao.PlayerDao;
import org.alexdev.roseau.dao.RoomDao;

/**
 * An in-memory stub {@link Dao} backed by an {@link InMemoryPlayerDaoStub}. All other DAOs throw
 * {@link UnsupportedOperationException} for now.
 */
public class InMemoryDaoStub implements Dao {

    private final InMemoryPlayerDaoStub playerDao;

    public InMemoryDaoStub(InMemoryPlayerDaoStub playerDao) {
        this.playerDao = playerDao;
    }

    @Override
    public PlayerDao getPlayer() {
        return playerDao;
    }

    @Override
    public boolean connect() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isConnected() {
        throw new UnsupportedOperationException();
    }

    @Override
    public RoomDao getRoom() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ItemDao getItem() {
        throw new UnsupportedOperationException();
    }

    @Override
    public CatalogueDao getCatalogue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public InventoryDao getInventory() {
        throw new UnsupportedOperationException();
    }

    @Override
    public NavigatorDao getNavigator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public MessengerDao getMessenger() {
        throw new UnsupportedOperationException();
    }
}
