package org.alexdev.roseau.messages;

import java.util.HashMap;

import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.messages.incoming.ADDITEM;
import org.alexdev.roseau.messages.incoming.ADDSTRIPITEM;
import org.alexdev.roseau.messages.incoming.APPROVENAME;
import org.alexdev.roseau.messages.incoming.ASSIGNRIGHTS;
import org.alexdev.roseau.messages.incoming.CARRYDRINK;
import org.alexdev.roseau.messages.incoming.CARRYITEM;
import org.alexdev.roseau.messages.incoming.CLOSE_UIMAKOPPI;
import org.alexdev.roseau.messages.incoming.CREATEFLAT;
import org.alexdev.roseau.messages.incoming.CRYFORHELP;
import org.alexdev.roseau.messages.incoming.DANCE;
import org.alexdev.roseau.messages.incoming.DELETEFLAT;
import org.alexdev.roseau.messages.incoming.FINDUSER;
import org.alexdev.roseau.messages.incoming.FLATPROPERTYBYITEM;
import org.alexdev.roseau.messages.incoming.GETCREDITS;
import org.alexdev.roseau.messages.incoming.GETFLATINFO;
import org.alexdev.roseau.messages.incoming.GETORDERINFO;
import org.alexdev.roseau.messages.incoming.GETSTRIP;
import org.alexdev.roseau.messages.incoming.GETUNITUSERS;
import org.alexdev.roseau.messages.incoming.GIVE_TICKETS;
import org.alexdev.roseau.messages.incoming.GOAWAY;
import org.alexdev.roseau.messages.incoming.GOTOFLAT;
import org.alexdev.roseau.messages.incoming.INFORETRIEVE;
import org.alexdev.roseau.messages.incoming.INITUNITLISTENER;
import org.alexdev.roseau.messages.incoming.INTODOOR;
import org.alexdev.roseau.messages.incoming.JUMPPERF;
import org.alexdev.roseau.messages.incoming.KILLUSER;
import org.alexdev.roseau.messages.incoming.LETUSERIN;
import org.alexdev.roseau.messages.incoming.LOGIN;
import org.alexdev.roseau.messages.incoming.LOOKTO;
import org.alexdev.roseau.messages.incoming.MESSENGER_ACCEPTBUDDY;
import org.alexdev.roseau.messages.incoming.MESSENGER_ASSIGNPERSMSG;
import org.alexdev.roseau.messages.incoming.MESSENGER_DECLINEBUDDY;
import org.alexdev.roseau.messages.incoming.MESSENGER_INIT;
import org.alexdev.roseau.messages.incoming.MESSENGER_MARKREAD;
import org.alexdev.roseau.messages.incoming.MESSENGER_REMOVEBUDDY;
import org.alexdev.roseau.messages.incoming.MESSENGER_REQUESTBUDDY;
import org.alexdev.roseau.messages.incoming.MESSENGER_SENDMSG;
import org.alexdev.roseau.messages.incoming.MOVE;
import org.alexdev.roseau.messages.incoming.MOVESTUFF;
import org.alexdev.roseau.messages.incoming.PLACEITEMFROMSTRIP;
import org.alexdev.roseau.messages.incoming.PLACESTUFFFROMSTRIP;
import org.alexdev.roseau.messages.incoming.PURCHASE;
import org.alexdev.roseau.messages.incoming.REGISTER;
import org.alexdev.roseau.messages.incoming.REMOVEITEM;
import org.alexdev.roseau.messages.incoming.REMOVERIGHTS;
import org.alexdev.roseau.messages.incoming.REMOVESTUFF;
import org.alexdev.roseau.messages.incoming.SEARCHBUSYFLATS;
import org.alexdev.roseau.messages.incoming.SEARCHFLAT;
import org.alexdev.roseau.messages.incoming.SEARCHFLATFORUSER;
import org.alexdev.roseau.messages.incoming.SETFLATINFO;
import org.alexdev.roseau.messages.incoming.SETITEMDATA;
import org.alexdev.roseau.messages.incoming.SETSTRIPITEMDATA;
import org.alexdev.roseau.messages.incoming.SETSTUFFDATA;
import org.alexdev.roseau.messages.incoming.SIGN;
import org.alexdev.roseau.messages.incoming.SPLASHPOSITION;
import org.alexdev.roseau.messages.incoming.STATUSOK;
import org.alexdev.roseau.messages.incoming.STOP;
import org.alexdev.roseau.messages.incoming.TALK;
import org.alexdev.roseau.messages.incoming.TRYFLAT;
import org.alexdev.roseau.messages.incoming.UPDATE;
import org.alexdev.roseau.messages.incoming.UPDATEFLAT;
import org.alexdev.roseau.messages.incoming.VERSIONCHECK;
import org.alexdev.roseau.server.messages.ClientMessage;

import com.google.common.collect.Maps;

public class MessageHandler {
	
	private HashMap<String, MessageEvent> messages;

	public MessageHandler() {
		this.messages = Maps.newHashMap();
		this.register();
	}
	
	public void register() {
		this.messages.clear();
		this.registerUserPackets();
		this.registerHandshakePackets();
		this.registerLoginPackets();
		this.registerRegisterPackets();
		this.registerNavigatorPackets();
		this.registerRoomPackets();
		this.registerMessengerPackets();
		this.registerItemPackets();
		this.registerPoolHandlers();
		this.registerModeration();
	}

	private void registerUserPackets() {
		this.messages.put("UPDATE", new UPDATE());
	}

	private void registerHandshakePackets() {
		this.messages.put("VERSIONCHECK", new VERSIONCHECK());
	}
	
	private void registerLoginPackets() {
		this.messages.put("LOGIN", new LOGIN());
		this.messages.put("INFORETRIEVE", new INFORETRIEVE());
		this.messages.put("GETCREDITS", new GETCREDITS());
	}
		
	private void registerRegisterPackets() {
		this.messages.put("APPROVENAME", new APPROVENAME());
		this.messages.put("FINDUSER", new FINDUSER());
		this.messages.put("REGISTER", new REGISTER());
	}

	private void registerNavigatorPackets() {
		this.messages.put("INITUNITLISTENER", new INITUNITLISTENER());
		this.messages.put("GETUNITUSERS", new GETUNITUSERS());
		this.messages.put("SEARCHFLATFORUSER", new SEARCHFLATFORUSER());
		this.messages.put("SEARCHBUSYFLATS", new SEARCHBUSYFLATS());
		this.messages.put("SETFLATINFO", new SETFLATINFO());
		this.messages.put("GETFLATINFO", new GETFLATINFO());
		this.messages.put("UPDATEFLAT", new UPDATEFLAT());
		this.messages.put("DELETEFLAT", new DELETEFLAT());
		this.messages.put("TRYFLAT", new TRYFLAT());
		this.messages.put("GOTOFLAT", new GOTOFLAT());
		this.messages.put("CLOSE_UIMAKOPPI", new CLOSE_UIMAKOPPI());
		this.messages.put("LOOKTO", new LOOKTO());
		this.messages.put("SEARCHFLAT", new SEARCHFLAT());
		
	}
	
	private void registerRoomPackets() {
		this.messages.put("STATUSOK", new STATUSOK());
		this.messages.put("Move", new MOVE());
		this.messages.put("Dance", new DANCE());
		this.messages.put("STOP", new STOP());
		this.messages.put("CHAT", new TALK());
		this.messages.put("SHOUT", new TALK());
		this.messages.put("WHISPER", new TALK());
		this.messages.put("GOAWAY", new GOAWAY());
		this.messages.put("CREATEFLAT", new CREATEFLAT());
		this.messages.put("ASSIGNRIGHTS", new ASSIGNRIGHTS());
		this.messages.put("REMOVERIGHTS", new REMOVERIGHTS());
		this.messages.put("KILLUSER", new KILLUSER());
	}
	
	private void registerMessengerPackets() {
		this.messages.put("MESSENGERINIT", new MESSENGER_INIT());
		this.messages.put("MESSENGER_SENDMSG", new MESSENGER_SENDMSG());
		this.messages.put("MESSENGER_REQUESTBUDDY", new MESSENGER_REQUESTBUDDY());
		this.messages.put("MESSENGER_ACCEPTBUDDY", new MESSENGER_ACCEPTBUDDY());
		this.messages.put("MESSENGER_DECLINEBUDDY", new MESSENGER_DECLINEBUDDY());
		this.messages.put("MESSENGER_REMOVEBUDDY", new MESSENGER_REMOVEBUDDY());
		this.messages.put("MESSENGER_MARKREAD", new MESSENGER_MARKREAD());
		this.messages.put("MESSENGER_ASSIGNPERSMSG", new MESSENGER_ASSIGNPERSMSG());
	}

	private void registerItemPackets() {
		this.messages.put("GETORDERINFO", new GETORDERINFO());
		this.messages.put("GETSTRIP", new GETSTRIP());
		this.messages.put("PURCHASE", new PURCHASE());
		this.messages.put("PLACESTUFFFROMSTRIP", new PLACESTUFFFROMSTRIP());
		this.messages.put("PLACEITEMFROMSTRIP", new PLACEITEMFROMSTRIP());
		this.messages.put("MOVESTUFF", new MOVESTUFF());
		this.messages.put("FLATPROPERTYBYITEM", new FLATPROPERTYBYITEM());
		this.messages.put("ADDSTRIPITEM", new ADDSTRIPITEM());
		this.messages.put("REMOVEITEM", new REMOVEITEM());
		this.messages.put("SETSTUFFDATA", new SETSTUFFDATA());
		this.messages.put("CarryItem", new CARRYITEM());
		this.messages.put("CarryDrink", new CARRYDRINK());
		this.messages.put("IntoDoor", new INTODOOR());
		this.messages.put("REMOVESTUFF", new REMOVESTUFF());
		this.messages.put("ADDITEM", new ADDITEM());
		this.messages.put("SETSTRIPITEMDATA", new SETSTRIPITEMDATA());
		this.messages.put("LETUSERIN", new LETUSERIN());
		this.messages.put("SETITEMDATA", new SETITEMDATA());
	}
	
	private void registerPoolHandlers() {
		this.messages.put("JUMPPERF", new JUMPPERF());
		this.messages.put("SPLASH_POSITION", new SPLASHPOSITION());
		this.messages.put("GIVE_TICKETS", new GIVE_TICKETS());
		this.messages.put("Sign", new SIGN());
	}
	
	private void registerModeration() {
		this.messages.put("CRYFORHELP", new CRYFORHELP());
	}
	
	
	public void handleRequest(Player player, ClientMessage message) {
		if (messages.containsKey(message.getHeader())) {
			messages.get(message.getHeader()).handle(player, message);
		}
	}

	public HashMap<String, MessageEvent> getMessages() {
		return messages;
	}

}
