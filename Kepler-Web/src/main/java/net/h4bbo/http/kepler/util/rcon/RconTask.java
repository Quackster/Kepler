package net.h4bbo.http.kepler.util.rcon;

import net.h4bbo.kepler.server.rcon.messages.RconHeader;
import net.h4bbo.kepler.util.StringUtil;
import net.h4bbo.kepler.util.config.ServerConfiguration;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Map;

public class RconTask implements Runnable {
    private final RconHeader header;
    private final Map<String, Object> parameters;

    public RconTask(RconHeader header, Map<String, Object> parameters) {
        this.header = header;
        this.parameters = parameters;
    }

    @Override
    public void run() {
        try (Socket socket = new Socket(ServerConfiguration.getString("rcon.ip"), ServerConfiguration.getInteger("rcon.port"))) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream daos = new DataOutputStream(baos);

            daos.writeInt(header.getRawHeader().length());
            daos.write(header.getRawHeader().getBytes(StringUtil.getCharset()));
            daos.writeInt(parameters.size());

            for (var entry : parameters.entrySet()) {
                daos.writeInt(entry.getKey().length());
                daos.write(entry.getKey().getBytes(StringUtil.getCharset()));

                daos.writeInt(entry.getValue().toString().length());
                daos.write(entry.getValue().toString().getBytes(StringUtil.getCharset()));
            }

            try (DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream())) {
                byte[] message = baos.toByteArray();

                dataOutputStream.writeInt(message.length);
                dataOutputStream.write(message);
                dataOutputStream.flush();

                baos.close();
                daos.close();
                socket.close();
            } catch (IOException ignored) {

            }
        } catch (IOException ignored) {

        }
    }
}
