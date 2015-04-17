package com.cometproject.server.network.messages.incoming.help.tool;

import com.cometproject.server.network.messages.composers.MessageComposer;
import com.cometproject.server.network.messages.incoming.Event;
import com.cometproject.server.network.messages.types.Composer;
import com.cometproject.server.network.messages.types.MessageEvent;
import com.cometproject.server.network.sessions.Session;

public class OpenGuideToolMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        final boolean onDuty = msg.readBoolean();

        final boolean handleTourRequests = msg.readBoolean();
        final boolean handleHelpRequests = msg.readBoolean();
        final boolean handleBullyRequests = msg.readBoolean();

        if (onDuty) {
            // we're on duty!!
        }

        // 2200
        client.send(new MessageComposer() {
            @Override
            public short getId() {
                return 2200;
            }

            @Override
            public void compose(Composer msg) {
                msg.writeBoolean(onDuty);
                msg.writeInt(onDuty ? 1 : 0);
                msg.writeInt(0);
                msg.writeInt(0);
            }
        });
    }
}