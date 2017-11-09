package com.cometproject.api.game.players;

import com.cometproject.api.game.players.data.IPlayerData;
import com.cometproject.api.game.players.data.IPlayerSettings;
import com.cometproject.api.game.players.data.IPlayerStatistics;
import com.cometproject.api.game.players.data.components.PlayerAchievements;
import com.cometproject.api.game.players.data.components.PlayerBots;
import com.cometproject.api.game.players.data.components.PlayerInventory;
import com.cometproject.api.game.players.data.components.PlayerPermissions;
import com.cometproject.api.game.rooms.entities.PlayerRoomEntity;
import com.cometproject.api.networking.messages.IMessageComposer;
import com.cometproject.api.networking.sessions.ISession;

import java.util.List;

public interface IPlayer {
    int INFINITE_BALANCE = 999999;

    void dispose();

    void sendBalance();

    IMessageComposer composeCreditBalance();

    IMessageComposer composeCurrenciesBalance();

    void loadRoom(int id, String password);

    void poof();

    void ignorePlayer(int playerId);

    void unignorePlayer(int playerId);

    boolean ignores(int playerId);

    List<Integer> getRooms();

    List<Integer> getRoomsWithRights();

    void setRooms(List<Integer> rooms);

    void setSession(ISession client);

    PlayerRoomEntity getEntity();

    ISession getSession();

    IPlayerData getData();

    IPlayerSettings getSettings();

    IPlayerStatistics getStats();

    PlayerPermissions getPermissions();

    PlayerAchievements getAchievements();

//    MessengerComponent getMessenger();
//
    PlayerInventory getInventory();
//
//    SubscriptionComponent getSubscription();
//
//    RelationshipComponent getRelationships();
//
    PlayerBots getBots();
//
//    PetComponent getPets();
//
//    QuestComponent getQuests();

    int getId();

    void sendNotif(String title, String message);

    void sendMotd(String message);

    boolean isTeleporting();

    long getTeleportId();

    void setTeleportId(long teleportId);

    long getRoomLastMessageTime();

    void setRoomLastMessageTime(long roomLastMessageTime);

    double getRoomFloodTime();

    void setRoomFloodTime(double roomFloodTime);

    int getRoomFloodFlag();

    void setRoomFloodFlag(int roomFloodFlag);

    String getLastMessage();

    void setLastMessage(String lastMessage);

    List<Integer> getGroups();

    int getNotifCooldown();

    void setNotifCooldown(int notifCooldown);

    int getLastRoomId();

    void setLastRoomId(int lastRoomId);

    int getLastGift();

    void setLastGift(int lastGift);

    long getMessengerLastMessageTime();

    void setMessengerLastMessageTime(long messengerLastMessageTime);

    double getMessengerFloodTime();

    void setMessengerFloodTime(double messengerFloodTime);

    int getMessengerFloodFlag();

    void setMessengerFloodFlag(int messengerFloodFlag);

    boolean isDeletingGroup();

    void setDeletingGroup(boolean isDeletingGroup);

    long getDeletingGroupAttempt();

    void setDeletingGroupAttempt(long deletingGroupAttempt);

    void bypassRoomAuth(boolean bypassRoomAuth);

    boolean isBypassingRoomAuth();

    int getLastFigureUpdate();

    void setLastFigureUpdate(int lastFigureUpdate);

    long getLastReward();

    void setLastReward(long lastReward);
}
