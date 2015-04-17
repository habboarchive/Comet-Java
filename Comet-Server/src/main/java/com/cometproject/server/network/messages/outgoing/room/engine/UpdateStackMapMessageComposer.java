package com.cometproject.server.network.messages.outgoing.room.engine;

import com.cometproject.server.game.rooms.types.mapping.Tile;
import com.cometproject.server.network.messages.composers.MessageComposer;
import com.cometproject.server.network.messages.headers.Composers;
import com.cometproject.server.network.messages.types.Composer;

import java.util.List;

public class UpdateStackMapMessageComposer extends MessageComposer {
    private final List<Tile> tilesToUpdate;
    private final Tile singleTile;

    public UpdateStackMapMessageComposer(final List<Tile> tilesToUpdate) {
        this.tilesToUpdate = tilesToUpdate;
        this.singleTile = null;
    }

    public UpdateStackMapMessageComposer(Tile tile) {
        this.tilesToUpdate = null;
        this.singleTile = tile;
    }

    @Override
    public short getId() {
        return Composers.UpdateStackMapMessageComposer;
    }

    @Override
    public void compose(Composer msg) {
        msg.writeByte(singleTile != null ? 1 : tilesToUpdate.size());

        if(singleTile != null) {
            this.composeUpdate(this.singleTile, msg);
            return;
        }

        for(Tile tile : tilesToUpdate) {
            this.composeUpdate(tile, msg);
        }
    }

    private void composeUpdate(Tile tile, Composer msg) {
        msg.writeByte(tile.getPosition().getX());
        msg.writeByte(tile.getPosition().getY());

        msg.writeShort((int) ((tile.getStackHeight()) * 256));
    }
}