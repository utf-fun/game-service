package org.readutf.gameservice.common;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.serializers.DefaultSerializers;
import org.readutf.gameservice.common.packet.HeartbeatPacket;
import org.readutf.gameservice.common.packet.ServerRegisterPacket;

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

public class SharedKryo {

    public static Kryo createKryo() {
        Kryo kryo = new Kryo();

        kryo.register(Collections.emptyList().getClass(), new DefaultSerializers.CollectionsEmptyListSerializer());
        kryo.register(ArrayList.class);
        kryo.register(HeartbeatPacket.class);
        kryo.register(ServerRegisterPacket.class);
        kryo.register(ServerRegisterPacket.class);
        kryo.register(UUID.class, new DefaultSerializers.UUIDSerializer());

        return kryo;
    }

}
