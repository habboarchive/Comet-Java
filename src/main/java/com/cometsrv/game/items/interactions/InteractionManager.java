package com.cometsrv.game.items.interactions;

import com.cometsrv.boot.Comet;
import com.cometsrv.game.GameEngine;
import com.cometsrv.game.items.interactions.banzai.BanzaiPatchInteraction;
import com.cometsrv.game.items.interactions.banzai.gates.BanzaiGateBlueInteraction;
import com.cometsrv.game.items.interactions.banzai.gates.BanzaiGateGreenInteraction;
import com.cometsrv.game.items.interactions.banzai.gates.BanzaiGateRedInteraction;
import com.cometsrv.game.items.interactions.banzai.gates.BanzaiGateYellowInteraction;
import com.cometsrv.game.items.interactions.football.BallInteraction;
import com.cometsrv.game.items.interactions.items.*;
import com.cometsrv.game.items.interactions.wired.action.WiredActionMoveRotate;
import com.cometsrv.game.items.interactions.wired.action.WiredActionMoveUser;
import com.cometsrv.game.items.interactions.wired.action.WiredActionShowMessage;
import com.cometsrv.game.items.interactions.wired.trigger.WiredTriggerEnterRoom;
import com.cometsrv.game.items.interactions.wired.trigger.WiredTriggerOnSay;
import com.cometsrv.game.rooms.entities.types.PlayerEntity;
import com.cometsrv.game.rooms.items.FloorItem;
import com.cometsrv.game.rooms.items.RoomItem;
import com.cometsrv.game.rooms.types.Room;
import javolution.util.FastMap;

import java.util.Map;

public class InteractionManager {
    public static final boolean DICE_ENABLED = Integer.parseInt(Comet.getServer().getConfig().get("comet.game.interactions.dice.enabled")) == 1;
    public static final int DICE_ROLL_TIME = Integer.parseInt(Comet.getServer().getConfig().get("comet.game.interactions.dice.cycles"));

    private FastMap<String, Interactor> interactions;

    public InteractionManager() {
        this.interactions = new FastMap<>();
        this.loadInteractions();
    }

    public void loadInteractions() {
        // Furniture
        this.interactions.put("default", new DefaultInteraction());
        this.interactions.put("gate", new GateInteraction());
        this.interactions.put("pressure_pad", new PressurePadInteraction());
        this.interactions.put("teleport", new TeleportInteraction());
        this.interactions.put("habbowheel", new WheelInteractor());
        this.interactions.put("roller", new RollerInteraction());
        this.interactions.put("ball", new BallInteraction());

        if (InteractionManager.DICE_ENABLED) { this.interactions.put("dice", new DiceInteraction()); }

        // Wired Actions
        this.interactions.put("wf_act_moverotate", new WiredActionMoveRotate());
        this.interactions.put("wf_act_saymsg", new WiredActionShowMessage());
        this.interactions.put("wf_act_moveuser", new WiredActionMoveUser());

        // Wired Triggers
        this.interactions.put("wf_trg_onsay", new WiredTriggerOnSay());
        this.interactions.put("wf_trg_enterroom", new WiredTriggerEnterRoom());

        // Wired Conditions

        // Banzai
        this.interactions.put("bb_patch", new BanzaiPatchInteraction());
        this.interactions.put("bb_green_gate", new BanzaiGateGreenInteraction());
        this.interactions.put("bb_yellow_gate", new BanzaiGateYellowInteraction());
        this.interactions.put("bb_blue_gate", new BanzaiGateBlueInteraction());
        this.interactions.put("bb_red_gate", new BanzaiGateRedInteraction());
    }

    public void onWalk(boolean state, FloorItem item, PlayerEntity avatar) {
        if (this.isInteraction(item.getDefinition().getInteraction())) {
            if (this.getInteractions().get(item.getDefinition().getInteraction()).onWalk(state, item, avatar)) {
                // ??
            }
        }
    }

    public void onPreWalk(FloorItem item, PlayerEntity avatar) {
        if (this.isInteraction(item.getDefinition().getInteraction())) {
            if (this.getInteractions().get(item.getDefinition().getInteraction()).onPreWalk(item, avatar)) {
                // ??
            }
        }
    }

    public void onInteract(int state, RoomItem item, PlayerEntity avatar) {
        GameEngine.getLogger().debug("Interacted with: " + item.getDefinition().getInteraction());

        if(!this.isInteraction(item.getDefinition().getInteraction())) {
            return;
        }

        Interactor action = this.getInteractions().get(item.getDefinition().getInteraction());

        if(action.requiresRights() && !avatar.getRoom().getRights().hasRights(avatar.getPlayer().getId())) {
            return;
        }

        if (action.onInteract(state, item, avatar)) {
            // ??
        }
    }

    // Method not yet finished!
    public void onPlace(FloorItem item, PlayerEntity avatar, Room room) {

    }

    // Method not yet finished!
    public void onPickup(FloorItem item, PlayerEntity avatar, Room room) {

    }

    public void onTick(FloorItem item) {
        GameEngine.getLogger().debug("GenericRoomItem tick: " + item.getDefinition().getInteraction());

        if (!this.isInteraction(item.getDefinition().getInteraction())) {
            return;
        }

        if (item.getNextInteraction().needsCycling()) {
            return;
        }

        Interactor action = this.getInteractions().get(item.getDefinition().getInteraction());

        if (action.onTick(item)) {
            // ??
        }
    }

    private boolean isInteraction(String interaction) {
        return this.getInteractions().containsKey(interaction);
    }

    public Map<String, Interactor> getInteractions() {
        return this.interactions;
    }
}
