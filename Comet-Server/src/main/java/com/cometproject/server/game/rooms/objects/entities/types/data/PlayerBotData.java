package com.cometproject.server.game.rooms.objects.entities.types.data;

import com.cometproject.api.game.bots.BotMode;
import com.cometproject.api.game.bots.BotType;
import com.cometproject.server.game.bots.BotData;
import com.cometproject.server.game.rooms.objects.misc.Position;
import org.apache.log4j.Logger;


public class PlayerBotData extends BotData {
    private Logger log = Logger.getLogger(PlayerBotData.class.getName());

    private Position position;

    public PlayerBotData(int id, String username, String motto, String figure, String gender, String ownerName, int ownerId, String messages, boolean automaticChat, int chatDelay, BotType botType, BotMode mode, String data) {
        super(id, username, motto, figure, gender, ownerName, ownerId, messages, automaticChat, chatDelay, botType, mode, data);
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }
}
