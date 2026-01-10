package org.alexdev.roseau.game.inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.GameVariables;
import org.alexdev.roseau.game.item.Item;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.messages.outgoing.STRIPINFO;

public class Inventory {
	private final Player player;

	private List<Item> items;
	private Map<Integer, List<Item>> paginatedItems;

	private int cursor;

	public Inventory(Player player) {
		this.player = player;
		this.paginatedItems = new java.util.concurrent.ConcurrentHashMap<>();
		this.items = new ArrayList<>();
		this.cursor = 0;
	}

	public void load() {
		this.dispose();
		
		this.items = Roseau.getDao().getInventory().getInventoryItems(this.player.getDetails().getId());
		
		this.refreshPagination();
	}

	public void refreshPagination() {
		this.paginatedItems.clear();

		if (items.isEmpty()) {
			return;
		}

		IntStream.range(0, items.size())
			.boxed()
			.collect(Collectors.groupingBy(
				index -> index / GameVariables.MAX_ITEMS_PER_PAGE,
				Collectors.mapping(items::get, Collectors.toList())
			))
			.forEach(paginatedItems::put);
	}

	public Item getItem(int id) {
		return items.stream()
			.filter(item -> item.getId() == id)
			.findFirst()
			.orElse(null);
	}

	public void removeItem(Item item) {
		this.removeItem(item, true);
	}
	
	public void removeItem(Item item, boolean refreshPagination) {
		Optional.ofNullable(item)
			.ifPresent(items::remove);

		if (refreshPagination) {
			this.refreshPagination();
		}
	}

	public void addItem(Item item) {
		Optional.ofNullable(item)
			.ifPresent(items::add);

		this.refreshPagination();
	}

	public void refresh(String mode) {
		Optional.of(mode)
			.ifPresent(m -> {
				switch (m) {
					case "last" -> cursor = Math.max(0, paginatedItems.size() - 1);
					case "new" -> cursor = 0;
					case "next" -> cursor++;
				}
			});

		Optional.ofNullable(paginatedItems.get(cursor))
			.ifPresentOrElse(
				pageItems -> player.send(new STRIPINFO(pageItems)),
				() -> {
					if (!paginatedItems.isEmpty()) {
						this.refresh("new");
					} else {
						player.send(new STRIPINFO());
					}
				}
			);
	}

	public void dispose() {
		Optional.ofNullable(this.items)
			.ifPresent(List::clear);
		this.items = new ArrayList<>();
	}

	public List<Item> getItems() {
		return items;
	}
}
