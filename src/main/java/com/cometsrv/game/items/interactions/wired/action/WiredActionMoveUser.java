package com.cometsrv.game.items.interactions.wired.action;

import com.cometsrv.game.GameEngine;
import com.cometsrv.game.items.interactions.Interactor;
import com.cometsrv.game.rooms.avatars.Avatar;
import com.cometsrv.game.rooms.items.FloorItem;
import com.cometsrv.game.wired.WiredStaticConfig;
import com.cometsrv.game.wired.data.WiredDataFactory;
import com.cometsrv.game.wired.data.effects.TeleportToItemData;
import com.cometsrv.network.messages.headers.Composers;
import com.cometsrv.network.messages.types.Composer;

public class WiredActionMoveUser extends Interactor {
    @Override
    public boolean onWalk(boolean state, FloorItem item, Avatar avatar) {
        return false;
    }

    @Override
    public boolean onInteract(int request, FloorItem item, Avatar avatar) {
        TeleportToItemData data = (TeleportToItemData) WiredDataFactory.get(item);

        if(data == null) {
            GameEngine.getLogger().debug("Failed to find WiredDataInstance for item: " + item.getId());
            return false;
        }

        Composer msg = new Composer(Composers.WiredEffectMessageComposer);

        msg.writeBoolean(false);
        msg.writeInt(WiredStaticConfig.MAX_FURNI_SELECTION);

        msg.writeInt(data.getCount());

        for(int itemId : data.getItems()){
            msg.writeInt(itemId);
        }

        msg.writeInt(item.getDefinition().getSpriteId());
        msg.writeInt(item.getId());

        msg.writeString("");
        msg.writeInt(0);
        msg.writeInt(8);
        msg.writeInt(0);
        msg.writeInt(data.getDelay());
        msg.writeInt(0);
        msg.writeString("");

        avatar.getPlayer().getSession().send(msg);
        return false;
    }

    @Override
    public boolean onPlace(FloorItem item, Avatar avatar) {
        return false;
    }

    @Override
    public boolean onPickup(FloorItem item, Avatar avatar) {
        return false;
    }

    @Override
    public boolean onTick(FloorItem item, Avatar avatar) {
        return false;
    }

    @Override
    public boolean requiresRights() {
        return true;
    }
}
