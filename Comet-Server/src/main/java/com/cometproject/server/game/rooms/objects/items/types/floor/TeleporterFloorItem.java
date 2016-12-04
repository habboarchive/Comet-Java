package com.cometproject.server.game.rooms.objects.items.types.floor;

import com.cometproject.server.boot.Comet;
import com.cometproject.server.boot.CometServer;
import com.cometproject.server.game.items.ItemManager;
import com.cometproject.server.game.rooms.RoomManager;
import com.cometproject.server.game.rooms.objects.entities.RoomEntity;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.objects.items.RoomItemFactory;
import com.cometproject.server.game.rooms.objects.items.RoomItemFloor;
import com.cometproject.server.game.rooms.objects.items.types.AdvancedFloorItem;
import com.cometproject.server.game.rooms.objects.items.types.state.FloorItemEvent;
import com.cometproject.server.game.rooms.objects.misc.Position;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.game.rooms.types.mapping.RoomTile;
import com.cometproject.server.network.messages.outgoing.room.engine.RoomForwardMessageComposer;

public class TeleporterFloorItem extends AdvancedFloorItem<TeleporterFloorItem.TeleporterItemEvent> {
    class TeleporterItemEvent extends FloorItemEvent {

        public int state;
        public PlayerEntity outgoingEntity;
        public PlayerEntity incomingEntity;

        protected TeleporterItemEvent(int delay) {
            super(delay);
        }
    }

    private boolean inUse = false;

    private long pairId = -1;
    boolean isDoor = false;

    public TeleporterFloorItem(long id, int itemId, Room room, int owner, String ownerName, int x, int y, double z, int rotation, String data) {
        super(id, itemId, room, owner, ownerName, x, y, z, rotation, data);

        this.setExtraData("0");

        if (this.getDefinition().getInteraction().equals("teleport_door")) {
            this.isDoor = true;
        }
    }

    @Override
    protected void onEventComplete(TeleporterItemEvent event) {
        try {
            switch (event.state) {
                case 0: {
                    event.outgoingEntity.moveTo(this.getPosition().getX(), this.getPosition().getY());

                    if (!(this instanceof TeleportPadFloorItem)) {
                        this.toggleDoor(true);
                    }

                    event.state = 1;

                    event.setTotalTicks(RoomItemFactory.getProcessTime(1));
                    this.queueEvent(event);
                    break;
                }

                case 1: {
                    RoomItemFloor pairItem = this.getPartner(this.getPairId());

                    if (pairItem == null) {
                        int roomId = ItemManager.getInstance().roomIdByItemId(pairId);

                        if (RoomManager.getInstance().get(roomId) == null) {
                            event.state = 8;

                            event.setTotalTicks(RoomItemFactory.getProcessTime(0.5));
                            this.queueEvent(event);
                            return;
                        }
                    }

                    if (!this.isDoor && !(this instanceof TeleportPadFloorItem))
                        this.toggleDoor(false);

                    event.state = 2;
                    event.setTotalTicks(RoomItemFactory.getProcessTime(this instanceof TeleportPadFloorItem ? 0.1 : 0.5));
                    this.queueEvent(event);
                    break;
                }

                case 2: {
                    if (!this.isDoor) {
                        this.toggleAnimation(true);

                        event.state = 3;
                        event.setTotalTicks(RoomItemFactory.getProcessTime(1));
                        this.queueEvent(event);
                    } else {
                        event.state = 3;
                        event.setTotalTicks(RoomItemFactory.getProcessTime(0.1));
                        this.queueEvent(event);
                    }
                    break;
                }

                case 3: {
                    long pairId = this.getPairId();

                    if (pairId == 0) {
                        event.state = 8;
                        event.setTotalTicks(RoomItemFactory.getProcessTime(0.5));
                        this.queueEvent(event);
                        return;
                    }

                    RoomItemFloor pairItem = this.getPartner(pairId);

                    if (pairItem == null) {
                        int roomId = ItemManager.getInstance().roomIdByItemId(pairId);

                        if (RoomManager.getInstance().get(roomId) != null) {
                            if (event.outgoingEntity != null) {
                                PlayerEntity pEntity = (PlayerEntity) event.outgoingEntity;

                                if (pEntity.getPlayer() != null && pEntity.getPlayer().getSession() != null) {
                                    pEntity.getPlayer().setTeleportId(pairId);
                                    pEntity.getPlayer().setTeleportRoomId(roomId);
                                    pEntity.getPlayer().getSession().send(new RoomForwardMessageComposer(roomId));
                                }

                                event.state = 7;
                                event.setTotalTicks(RoomItemFactory.getProcessTime(0.5));
                                this.queueEvent(event);
                            }
                        } else {
                            event.state = 8;
                            event.setTotalTicks(RoomItemFactory.getProcessTime(0.5));
                            this.queueEvent(event);
                            return;
                        }
                    }

                    if (!this.isDoor) {
                        event.state = 9;
                        event.setTotalTicks(RoomItemFactory.getProcessTime(1));
                        this.queueEvent(event);
                    }

                    TeleporterFloorItem teleItem = (TeleporterFloorItem) pairItem;

                    if (teleItem != null)
                        teleItem.handleIncomingEntity(event.outgoingEntity, this);
                    break;
                }

                case 5: {
                    this.toggleAnimation(false);
                    event.state = 6;

                    event.setTotalTicks(RoomItemFactory.getProcessTime(0.5));
                    this.queueEvent(event);
                    break;
                }

                case 6: {
                    if (!(this instanceof TeleportPadFloorItem))
                        this.toggleDoor(true);

                    if (event.incomingEntity != null) {
                        event.incomingEntity.setBodyRotation(this.rotation);
                        event.incomingEntity.setHeadRotation(this.rotation);
                        event.incomingEntity.refresh();

                        event.incomingEntity.moveTo(this.getPosition().squareInFront(this.getRotation()).getX(), this.getPosition().squareInFront(this.getRotation()).getY());
                    }

                    event.state = 7;
                    event.setTotalTicks(RoomItemFactory.getProcessTime(1));
                    this.queueEvent(event);
                    break;
                }

                case 7: {
                    if (!(this instanceof TeleportPadFloorItem))
                        this.toggleDoor(false);

                    if (event.incomingEntity != null) {
                        event.incomingEntity.setOverriden(false);
                        event.incomingEntity = null;
                    }

                    this.inUse = false;
                    break;
                }

                case 8: {
                    if (!(this instanceof TeleportPadFloorItem))
                        this.toggleDoor(true);

                    if (event.outgoingEntity != null) {
                        event.outgoingEntity.moveTo(this.getPosition().squareBehind(this.rotation).getX(), this.getPosition().squareBehind(this.rotation).getY());
                    }

                    event.state = 7;
                    event.setTotalTicks(RoomItemFactory.getProcessTime(1));
                    this.queueEvent(event);
                    break;
                }

                case 9: {
                    this.endTeleporting();
                }
            }
        } catch (Exception e) {
            Comet.getServer().getLogger().error("Failed to handle teleport event", e);
        }
    }

    @Override
    public boolean onInteract(RoomEntity entity, int requestData, boolean isWiredTrigger) {
        if (isWiredTrigger) return false;

        Position posInFront = this.getPosition().squareInFront(this.getRotation());

        if (entity.isOverriden()) return false;

        if (entity.getPosition().getX() != posInFront.getX() || entity.getPosition().getY() != posInFront.getY()) {
            entity.moveTo(posInFront.getX(), posInFront.getY());

            RoomTile tile = this.getRoom().getMapping().getTile(posInFront.getX(), posInFront.getY());

            if (tile != null) {
                tile.scheduleEvent(entity.getId(), (e) -> onInteract(e, requestData, false));
            }
            return false;
        }

//
        this.inUse = true;

        final TeleporterItemEvent event = new TeleporterItemEvent(RoomItemFactory.getProcessTime(1));
        event.outgoingEntity = (PlayerEntity) entity;

        entity.setOverriden(false);
        event.outgoingEntity.setOverriden(true);

        event.state = 0;

        try {
            this.queueEvent(event);
        } catch (Exception e) {
            Comet.getServer().getLogger().error("Failed to queue teleporter item event", e);
        }

        return true;
    }

    @Override
    public void onEntityStepOn(RoomEntity entity) {
        if (this.inUse) {
            return;
        }

        //if (event.incomingEntity != null && event.incomingEntity.getId() == entity.getId()) {
         //   return;
        //}

        this.inUse = true;
        //event.outgoingEntity = entity;
       // event.outgoingEntity.setOverriden(true);

        //event.state = 1;
        this.setTicks(RoomItemFactory.getProcessTime(0.01));
    }

    @Override
    public void onTickComplete() {

    }

    @Override
    public void onPlaced() {
        this.setExtraData("0");
    }

    private long getPairId() {
        if (this.pairId == -1) {
            this.pairId = ItemManager.getInstance().getTeleportPartner(this.getId());
        }

        return this.pairId;
    }

    public void endTeleporting() {
        this.toggleAnimation(false);
        this.inUse = false;
    }

    public void handleIncomingEntity(PlayerEntity entity, TeleporterFloorItem otherItem) {
        if (otherItem != null)
            otherItem.endTeleporting();

        entity.updateAndSetPosition(this.getPosition().copy());
        this.toggleAnimation(true);

        final TeleporterItemEvent event = new TeleporterItemEvent(0);

        event.incomingEntity = entity;

        if (!this.isDoor) {
            event.state = 5;
            event.setTotalTicks(RoomItemFactory.getProcessTime(1));
        } else {
            event.state = 6;
            event.setTotalTicks(RoomItemFactory.getProcessTime(0.1));
        }

        try {
            this.queueEvent(event);
        } catch (Exception e) {
            Comet.getServer().getLogger().error("Error while queueing teleport event", e);
        }
    }

    protected void toggleDoor(boolean state) {
        if (state)
            this.setExtraData("1");
        else
            this.setExtraData("0");

        this.sendUpdate();
    }

    protected void toggleAnimation(boolean state) {
        if (state) {
            this.setExtraData("2");
        } else {
            this.setExtraData("0");
        }

        this.sendUpdate();
    }

    protected RoomItemFloor getPartner(long pairId) {
        return this.getRoom().getItems().getFloorItem(pairId);
    }
}
