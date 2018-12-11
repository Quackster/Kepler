package org.alexdev.roseau.game.room.entity;

import java.util.Hashtable;
import java.util.Map;
import java.util.Random;

/*
 * Written by office.boy (Mike) and Nillus from Blunk v5
 */
public class ChatUtility {
	
	public final static int HEARING_RADIUS_NORM = 5;
	public final static int HEARING_RADIUS_MAX = 7;
	
	private static Random m_random = new Random();
	private static Hashtable<String, String> m_emotes = new Hashtable<String, String>();
	
	public static void setupEmotes(boolean classicOnly)
	{
		// Smile emotes
		m_emotes.put(":)", "sml");
		m_emotes.put(":-)", "sml");
		m_emotes.put(":d", "sml");
		m_emotes.put(":p", "sml");
		m_emotes.put(";)", "sml");
		m_emotes.put(";-)", "sml");
		
		// Sad emotes
		m_emotes.put(":s", "sad");
		m_emotes.put(":(", "sad");
		m_emotes.put(":-(", "sad");
		m_emotes.put(":'(", "sad");
		
		// Surprise emotes
		m_emotes.put(":o", "srp");
		
		// Aggression emotes
		m_emotes.put(":@", "agr");
		m_emotes.put(">:(", "agr");
		
		// These ones are used alot too
		if(!classicOnly)
		{
			m_emotes.put(":]", "sml");
			m_emotes.put("=)", "sml");
			m_emotes.put("=]", "sml");
			m_emotes.put("=D", "sml");
			m_emotes.put("=(", "sad");
			m_emotes.put("=[", "sad");
			m_emotes.put("=o", "srp");
		}
	}
	
	public static String[] filterWords(String[] words)
	{
		// TODO: crappy word filter for swearwords
		return words;
	}
	
	public static String detectEmote(String words[])
	{
		for (String word : words)
		{
			if (word.length() > 0)
			{
				// If the word is not starting with ':' or '>', then we can skip it
				char start = word.charAt(0);
				if (start != ':' && start != '>' && start != '=')
				{
					continue;
				}
				
				// Is this a known emote?
				String emote = m_emotes.get(word.toLowerCase());
				if(emote != null)
				{
					return emote;
				}
			}
		}
		
		// No emote AT ALL
		return null;
	}
	
	public final static String garbleChat(String text)
	{		
		char[] chars = text.toCharArray();
		for (int pos = 0; pos < chars.length; pos++)
		{
			// Only garble A-Z, a-z
			if(chars[pos] >= 'A' && chars[pos] <= 'z')
			{
				float chance = m_random.nextFloat();
				if (chance <= 0.25f)
				{
					chars[pos] = '.';
				}
			}
		}
		return new String(chars);
	}

	public static Map<String, String> getEmotes() {
		return m_emotes;
	}
}
