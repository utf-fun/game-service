package org.readutf.gameservice.common;

import com.esotericsoftware.kryo.Kryo;
import org.readutf.gameservice.common.packet.HeartbeatPacket;
import org.readutf.gameservice.common.packet.ServerRegisterPacket;

public class SharedKryo {

    public static Kryo createKryo() {
        Kryo kryo = new Kryo();

        kryo.register(HeartbeatPacket.class);
        kryo.register(ServerRegisterPacket.class);

        return kryo;
    }

}
