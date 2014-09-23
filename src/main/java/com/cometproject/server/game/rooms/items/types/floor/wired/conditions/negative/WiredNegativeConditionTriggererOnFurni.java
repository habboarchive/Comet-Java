package com.cometproject.server.game.rooms.items.types.floor.wired.conditions.negative;

import com.cometproject.server.game.rooms.items.types.floor.wired.conditions.positive.WiredConditionTriggererOnFurni;

public class WiredNegativeConditionTriggererOnFurni extends WiredConditionTriggererOnFurni {
    /**
     * The default constructor
     *
     * @param id       The ID of the item
     * @param itemId   The ID of the item definition
     * @param roomId   The ID of the room
     * @param owner    The ID of the owner
     * @param x        The position of the item on the X axis
     * @param y        The position of the item on the Y axis
     * @param z        The position of the item on the z axis
     * @param rotation The orientation of the item
     * @param data     The JSON object associated with this item
     */
    public WiredNegativeConditionTriggererOnFurni(int id, int itemId, int roomId, int owner, int x, int y, double z, int rotation, String data) {
        super(id, itemId, roomId, owner, x, y, z, rotation, data);
    }
}