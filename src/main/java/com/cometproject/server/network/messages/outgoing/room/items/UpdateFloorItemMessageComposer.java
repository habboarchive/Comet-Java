package com.cometproject.server.network.messages.outgoing.room.items;

import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.network.messages.composers.MessageComposer;
import com.cometproject.server.network.messages.headers.Composers;
import com.cometproject.server.network.messages.types.Composer;


public class UpdateFloorItemMessageComposer extends MessageComposer {
    private final RoomItemFloor item;

    public UpdateFloorItemMessageComposer(RoomItemFloor item) {
        this.item = item;
    }

    @Override
    public short getId() {
        return Composers.UpdateRoomItemMessageComposer;
    }

    @Override
    public void compose(Composer msg) {
        this.item.serialize(msg);
        msg.writeInt(this.item.getOwner());
    }
}
