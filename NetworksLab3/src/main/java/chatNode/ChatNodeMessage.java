package chatNode;

import node.MessageType;
import node.NodeMessage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.UUID;

/*
сначала в любом сообщении uuid сообщения в строковом формате
затем размер int MessageType, MessageType
далее в зависимости от вида сообщения
CONNECTION: ничего
TEXT: длина сообщения(int) + сообщение
ACK: UUID полученного сообщения
ALTERNATIVE: длина (int) ip + ip + порт (int)
PERIOD CHECK: ничего
 */

public class ChatNodeMessage implements NodeMessage {
    private UUID uuid;
    private MessageType messageType;
    //if TEXT message or UID message or ip
    private String text;
    private String ip;
    private int port;
    private ByteArrayOutputStream message;

    ChatNodeMessage(MessageType messageType) throws IOException {
        message = new ByteArrayOutputStream();
        uuid = UUID.randomUUID();
        message.write(uuid.toString().getBytes(Charset.forName(CHARSET_NAME)));
        byte[] messageTypeBytes = messageType.toString().getBytes(Charset.forName(CHARSET_NAME));
        message.write(messageTypeBytes.length);
        message.write(messageTypeBytes);
    }

    ChatNodeMessage(MessageType messageType, String textMessage) throws IOException{
        this(messageType);
        if(messageType.equals(MessageType.TEXT)){
            byte[] textByteArray = textMessage.getBytes(Charset.forName(CHARSET_NAME));
            message.write(textByteArray.length);
            message.write(textByteArray);
        }
        if(messageType.equals(MessageType.ACK)){
            message.write(textMessage.getBytes(Charset.forName(CHARSET_NAME)));
        }
    }

    ChatNodeMessage(MessageType messageType, String ip, int port) throws IOException{
        this(messageType);
        byte[] ipByteArray = ip.getBytes(Charset.forName(CHARSET_NAME));
        message.write(ipByteArray.length);
        message.write(ipByteArray);
        message.write(port);
    }

    ChatNodeMessage(byte[] recvMessage) throws UnsupportedEncodingException {
        int uuidSize = UUID.randomUUID().toString().getBytes(Charset.forName(CHARSET_NAME)).length;
        ByteArrayInputStream recvMessageStream = new ByteArrayInputStream(recvMessage);
        uuid = UUID.fromString(new String(Arrays.copyOfRange(recvMessage, 0, uuidSize), CHARSET_NAME));

    }

    @Override
    public byte[] toBytes() {
        return message.toByteArray();
    }

    @Override
    public String getUUID() {
        return uuid.toString();
    }
}
