package com.cometproject.server.api;

import com.cometproject.server.api.rooms.RoomStats;
import com.cometproject.server.api.transformers.JsonTransformer;
import com.cometproject.server.boot.Comet;
import com.cometproject.server.game.CometManager;
import com.cometproject.server.game.rooms.types.Room;
import org.apache.log4j.Logger;
import spark.Spark;

import java.util.ArrayList;
import java.util.List;

public class APIManager {
    /**
     * Logger
     */
    private static final Logger log = Logger.getLogger(APIManager.class.getName());

    /**
     * Create an array of config properties that are required before enabling the API
     * If none of these properties exist, the API will be automatically disabled
     */
    private static final String[] configProperties = new String[]{
            "comet.api.enabled",
            "comet.api.port",
    };

    /**
     * Is the API enabled?
     */
    private boolean enabled;

    /**
     * The port the API server will listen on
     */
    private int port;

    /**
     * The transformer to convert objects into JSON formatted strings
     */
    private JsonTransformer jsonTransformer;

    /**
     * Construct the API manager
     */
    public APIManager() {
        this.initializeConfiguration();
        this.initializeSpark();
        this.initializeRouting();
    }

    /**
     * Initialize the configuration
     */
    private void initializeConfiguration() {
        for (String configProperty : configProperties) {
            if (!Comet.getServer().getConfig().containsKey(configProperty)) {
                log.warn("API configuration property not available: " + configProperty + ", API is disabled");
                this.enabled = false;

                return;
            }
        }

        this.enabled = Comet.getServer().getConfig().getProperty("comet.api.enabled").equals("true");
        this.port = Integer.parseInt(Comet.getServer().getConfig().getProperty("comet.api.port"));
    }

    /**
     * Initialize the Spark web framework
     */
    private void initializeSpark() {
        if (!this.enabled)
            return;

        Spark.setPort(this.port);

        this.jsonTransformer = new JsonTransformer();
    }

    /**
     * Initialize the API routing
     */
    private void initializeRouting() {
        if(!this.enabled)
            return;

        Spark.get("/", (request, response) -> {
            Spark.halt(404);
            return "Invalid request, if you believe you received this in error, please contact the server administrator!";
        });

        Spark.get("/rooms/active/all", (request, response) -> {
            response.type("application/json");

            List<RoomStats> activeRooms = new ArrayList<>();

            for(Room room : CometManager.getRooms().getRoomInstances().values()) {
                if (!room.needsRemoving()) {
                    activeRooms.add(new RoomStats(room));
                }
            }

            return activeRooms;
        }, jsonTransformer);
    }
}
