package com.cometproject.server.game.wired;

import com.cometproject.server.game.rooms.items.RoomItemFloor;
import com.cometproject.server.game.wired.data.WiredDataFactory;
import com.cometproject.server.game.wired.effects.MoveRotateEffect;
import com.cometproject.server.game.wired.effects.MoveUserEffect;
import com.cometproject.server.game.wired.effects.SayMessageEffect;
import com.cometproject.server.game.wired.effects.ToggleFurniEffect;
import com.cometproject.server.game.wired.triggers.*;
import com.cometproject.server.game.wired.types.TriggerType;
import com.cometproject.server.game.wired.types.WiredCondition;
import com.cometproject.server.game.wired.types.WiredEffect;
import com.cometproject.server.game.wired.types.WiredTrigger;
import javolution.util.FastMap;

public class WiredManager {
    private FastMap<String, WiredTrigger> triggers;
    private FastMap<String, WiredCondition> conditions;
    private FastMap<String, WiredEffect> effects;

    public WiredManager() {
        this.triggers = new FastMap<>();
        this.conditions = new FastMap<>();
        this.effects = new FastMap<>();

        this.triggers.put("wf_trg_onsay", new OnSayTrigger());
        this.triggers.put("wf_trg_enterroom", new EnterRoomTrigger());
        this.triggers.put("wf_trg_onfurni", new OnFurniTrigger());
        this.triggers.put("wf_trg_offfurni", new OffFurniTrigger());
        this.triggers.put("wf_trg_timer", new TimerTrigger());

        this.effects.put("wf_act_saymsg", new SayMessageEffect());
        this.effects.put("wf_act_moveuser", new MoveUserEffect());
        this.effects.put("wf_act_togglefurni", new ToggleFurniEffect());
        this.effects.put("wf_act_moverotate", new MoveRotateEffect());

        // TODO: conditions!!

        WiredDataFactory.init();
    }

    public boolean isWiredTrigger(RoomItemFloor item) {
        return this.triggers.containsKey(item.getDefinition().getInteraction());
    }

    public boolean isWiredEffect(RoomItemFloor item) {
        return this.effects.containsKey(item.getDefinition().getInteraction());
    }

    public boolean isWiredCondition(RoomItemFloor item) {
        return this.conditions.containsKey(item.getDefinition().getInteraction());
    }

    public boolean isWiredItem(RoomItemFloor item) {
        return (isWiredTrigger(item) || isWiredEffect(item) || isWiredCondition(item));
    }

    public String getString(TriggerType type) {
        if (type == TriggerType.ON_SAY) {
            return "wf_trg_onsay";
        } else if (type == TriggerType.ENTER_ROOM) {
            return "wf_trg_enterroom";
        } else if (type == TriggerType.ON_FURNI) {
            return "wf_trg_onfurni";
        } else if (type == TriggerType.OFF_FURNI) {
            return "wf_trg_offfurni";
        } else if (type == TriggerType.TIMER) {
            return "wf_trg_timer";
        }

        return "wf_trg_unknown";
    }

    public WiredTrigger getTrigger(String interaction) {
        return this.triggers.get(interaction);
    }

    public WiredCondition getCondition(String interaction) {
        return this.conditions.get(interaction);
    }

    public WiredEffect getEffect(String interaction) {
        return this.effects.get(interaction);
    }
}
