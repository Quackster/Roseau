package org.alexdev.roseau.game;

import org.alexdev.roseau.Roseau;

public class GameVariables {

	public static int CREDITS_EVERY_SECS;
	public static int CREDITS_EVERY_AMOUNT;
	public static String USERNAME_CHARS;
	public static int BOT_RESPONSE_DELAY;
	public static int CARRY_DRINK_INTERVAL;
	public static int CARRY_DRINK_TIME;
	public static int TALK_LOOKAT_RESET;
	public static int TALK_DISTANCE;
	public static int USER_DEFAULT_CREDITS;
	
	
	public static void setVariables() {
		CREDITS_EVERY_SECS = Roseau.getUtilities().getHabboConfig().get("Scheduler", "credits.every.x.secs", Integer.class);
		CREDITS_EVERY_AMOUNT = Roseau.getUtilities().getHabboConfig().get("Scheduler", "credits.every.x.amount", Integer.class);
		USERNAME_CHARS = Roseau.getUtilities().getHabboConfig().get("Register", "user.name.chars", String.class);
		BOT_RESPONSE_DELAY = Roseau.getUtilities().getHabboConfig().get("Bot", "bot.response.delay", Integer.class);
		CARRY_DRINK_INTERVAL = Roseau.getUtilities().getHabboConfig().get("Player", "carry.drink.interval", Integer.class);
		CARRY_DRINK_TIME = Roseau.getUtilities().getHabboConfig().get("Player", "carry.drink.time", Integer.class);
		TALK_LOOKAT_RESET = Roseau.getUtilities().getHabboConfig().get("Player", "talking.lookat.reset", Integer.class);
		TALK_DISTANCE = Roseau.getUtilities().getHabboConfig().get("Player", "talking.lookat.distance", Integer.class);
		USER_DEFAULT_CREDITS = Roseau.getUtilities().getHabboConfig().get("Register", "user.default.credits", int.class);
	}
}
