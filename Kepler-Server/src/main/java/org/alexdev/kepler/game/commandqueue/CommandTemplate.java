package org.alexdev.kepler.game.commandqueue;

import java.util.ArrayList;

public class CommandTemplate {
    public String Type;
    public int UserId;
    public int Credits;
    public int DefinitionId;
    public int RoomId;
    public String RoomType;
    public int RoomAccessType;
    public String RoomDescription;
    public String RoomName;
    public boolean RoomShowOwnerName;
    public String Message;
    public String MessageUrl;
    public String MessageLink;
    public int FriendRequestTo;
    public boolean OnlineOnly;
    public ArrayList<String> Users;
    public int BanLength;
    public String ExtraInfo;
    public Boolean BanIp;
    public Boolean BanMachine;
}
