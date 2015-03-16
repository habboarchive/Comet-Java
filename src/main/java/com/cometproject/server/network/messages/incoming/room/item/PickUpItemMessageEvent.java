package com.cometproject.server.network.messages.incoming.room.item;

import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.objects.items.RoomItemWall;
import com.cometproject.server.game.rooms.objects.items.types.wall.PostItWallItem;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.network.NetworkManager;
import com.cometproject.server.network.messages.incoming.IEvent;
import com.cometproject.server.network.messages.outgoing.room.items.RemoveWallItemMessageComposer;
import com.cometproject.server.network.messages.types.Event;
import com.cometproject.server.network.sessions.Session;


public class PickUpItemMessageEvent implements IEvent {
    public void handle(Session client, Event msg) {
        boolean isFloorItem = msg.readInt() == 2;

        int id = msg.readInt();
        Room room = client.getPlayer().getEntity().getRoom();

        if (room == null) {
            return;
        }

        boolean eject = false;

        RoomItemFloor item = room.getItems().getFloorItem(id);

        if (item == null) {
            RoomItemWall wItem = room.getItems().getWallItem(id);

            if (wItem == null || wItem instanceof PostItWallItem) {
                return;
            }

            if(wItem.getOwner() != client.getPlayer().getId() && !client.getPlayer().getPermissions().hasPermission("room_full_control")) {
                eject = true;
            }

            if(!eject) {
                room.getItems().removeItem(wItem, client);
            } else {
                Session owner = NetworkManager.getInstance().getSessions().getByPlayerId(wItem.getOwner());
                room.getItems().removeItem(wItem, owner);
            }

            client.send(new RemoveWallItemMessageComposer(wItem.getId(), client.getPlayer().getId()));
            return;
        }

        if(item.getOwner() != client.getPlayer().getId() && !client.getPlayer().getPermissions().hasPermission("room_full_control")) {
            eject = true;
        }

        item.onPickup();

        if(!eject) {
            room.getItems().removeItem(item, client);
        } else {
            Session owner = NetworkManager.getInstance().getSessions().getByPlayerId(item.getOwner());
            room.getItems().removeItem(item, owner);
        }
    }
}
