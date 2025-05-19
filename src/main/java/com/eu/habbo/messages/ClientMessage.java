package com.eu.habbo.messages;

import com.eu.habbo.messages.incoming.Incoming;
import com.eu.habbo.util.PacketUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientMessage {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientMessage.class);
    private final int header;
    private final ByteBuf buffer;

    public ClientMessage(int messageId, ByteBuf buffer) {
        this.header = messageId;
        this.buffer = ((buffer == null) || (buffer.readableBytes() == 0) ? Unpooled.EMPTY_BUFFER : buffer);
        if (header != Incoming.PongEvent) {
            if (buffer != null) LOGGER.debug("ClientMessage: {}; {} bytes", header, buffer.readableBytes());
            else LOGGER.debug("ClientMessage: {} EMPTY BUFFER", header);
        }
    }
    public ByteBuf getBuffer() {
        return this.buffer;
    }

    public int getMessageId() {
        return this.header;
    }


    @Override
    public ClientMessage clone() throws CloneNotSupportedException {
        return new ClientMessage(this.header, this.buffer.duplicate());
    }

    public int readShort() {
        try {
            return this.buffer.readShort();
        } catch (Exception ignored) {}

        return 0;
    }

    public Integer readInt() {
        try {
            return this.buffer.readInt();
        } catch (Exception ignored) {}
        return 0;
    }

    public boolean readBoolean() {
        try {
            return this.buffer.readByte() == 1;
        } catch (Exception ignored) {}

        return false;
    }

    public String readString() {
        try {
            int length = this.readShort();
            byte[] data = new byte[length];
            this.buffer.readBytes(data);
            return new String(data);
        } catch (Exception e) {
            return "";
        }
    }

    public String getMessageBody() {
        return PacketUtils.formatPacket(this.buffer);
    }

    public int bytesAvailable() {
        return this.buffer.readableBytes();
    }

    public void release() {
        this.buffer.release();
    }

}