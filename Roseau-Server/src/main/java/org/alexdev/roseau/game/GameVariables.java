package org.alexdev.roseau.game;

import org.alexdev.roseau.util.Util;

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
	public static long TELEPORTER_DELAY = 800;
	public static int MAX_ITEMS_PER_PAGE = 9;
	public static String MESSENGER_GREETING;
	public static boolean DEBUG_ENABLE = false;
	public static int AFK_ROOM_KICK;
	
	
	public static void setVariables() {
		CREDITS_EVERY_SECS = Util.getHabboConfig().get("Scheduler", "credits.every.x.secs", Integer.class);
		CREDITS_EVERY_AMOUNT = Util.getHabboConfig().get("Scheduler", "credits.every.x.amount", Integer.class);
		USERNAME_CHARS = Util.getHabboConfig().get("Register", "user.name.chars", String.class);
		MESSENGER_GREETING = Util.getHabboConfig().get("Register", "messenger.greeting", String.class);	
		BOT_RESPONSE_DELAY = Util.getHabboConfig().get("Bot", "bot.response.delay", Integer.class);
		CARRY_DRINK_INTERVAL = Util.getHabboConfig().get("Player", "carry.drink.interval", Integer.class);
		AFK_ROOM_KICK = Util.getHabboConfig().get("Player", "afk.room.kick", Integer.class);
		CARRY_DRINK_TIME = Util.getHabboConfig().get("Player", "carry.drink.time", Integer.class);
		TALK_LOOKAT_RESET = Util.getHabboConfig().get("Player", "talking.lookat.reset", Integer.class);
		TALK_DISTANCE = Util.getHabboConfig().get("Player", "talking.lookat.distance", Integer.class);
		USER_DEFAULT_CREDITS = Util.getHabboConfig().get("Register", "user.default.credits", int.class);
		DEBUG_ENABLE = Util.getHabboConfig().get("Debug", "debug.enable", Boolean.class);
	}
}
