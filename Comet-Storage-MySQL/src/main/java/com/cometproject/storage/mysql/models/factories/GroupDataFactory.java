package com.cometproject.storage.mysql.models.factories;

import com.cometproject.api.game.groups.types.GroupType;
import com.cometproject.api.game.groups.types.IGroupData;
import com.cometproject.storage.mysql.models.GroupData;

public class GroupDataFactory {

    public IGroupData create(int id, String title, String description, String badge, int ownerId, String ownerName,
                             int roomId, int created, GroupType type, int colourA, int colourB,
                             boolean canMembersDecorate, boolean hasForum) {
        return new GroupData(id, title, description, badge, ownerId, ownerName, roomId, created, type, colourA, colourB,
                canMembersDecorate, hasForum);
    }
}