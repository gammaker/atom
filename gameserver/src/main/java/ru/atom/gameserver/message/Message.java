package ru.atom.gameserver.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import ru.atom.gameserver.model.Movable.Direction;
import ru.atom.gameserver.util.JsonHelper;

public class Message {
    public final Topic topic;
    public final String data;

    public Message(Topic topic, String data) {
        this.topic = topic;
        this.data = data;
    }

    @JsonCreator
    public Message(@JsonProperty("topic") Topic topic, @JsonProperty("data") JsonNode data) {
        this.topic = topic;
        this.data = data.toString();
    }


    public static class MoveData {
        public Direction direction;

        @JsonCreator
        public MoveData(@JsonProperty("direction") Direction direction) {
            this.direction = direction;
        }
    }

    public Message(MoveData data) {
        this.topic = Topic.MOVE;
        this.data = JsonHelper.toJson(data);
    }

    public MoveData moveData() {
        if (topic != Topic.MOVE) return null;
        return JsonHelper.fromJson(data, Message.MoveData.class);
    }


    public static class PlantBombData {
        @JsonCreator
        public PlantBombData() {
        }
    }

    public Message(PlantBombData data) {
        this.topic = Topic.PLANT_BOMB;
        this.data = JsonHelper.toJson(data);
    }

    public PlantBombData plantBombData() {
        if (topic != Topic.PLANT_BOMB) return null;
        return JsonHelper.fromJson(data, Message.PlantBombData.class);
    }
}