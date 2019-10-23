package chatNode;

import node.MessageType;
import node.NodeMessage;

import java.io.*;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.UUID;

/*
сначала в любом сообщении uuid сообщения в строковом формате
затем  int messageType,
далее в зависимости от вида сообщения
CONNECTION: ничего
TEXT: длина сообщения(int) + сообщение
ACK: UUID полученного сообщения
ALTERNATIVE: длина (int) ip + ip + порт (int)
PERIOD CHECK: ничего
 */

public class ChatNodeMessage implements NodeMessage {
    private UUID uuid;
    private int messageType;
    private ByteArrayOutputStream message;

    ChatNodeMessage(int messageType) throws IOException {
        message = new ByteArrayOutputStream();
        uuid = UUID.randomUUID();
        message.write(uuid.toString().getBytes(Charset.forName(CHARSET_NAME)));
        message.write(messageType);
    }

    ChatNodeMessage(int messageType, String textMessage) throws IOException{
        this(messageType);
        if(messageType == TEXT){
            byte[] textByteArray = textMessage.getBytes(Charset.forName(CHARSET_NAME));
            message.write(textByteArray.length);
            message.write(textByteArray);
        }
        if(messageType == ACK){
            message.write(textMessage.getBytes(Charset.forName(CHARSET_NAME)));
        }
    }

    ChatNodeMessage(int messageType, String ip, int port) throws IOException{
        this(messageType);
        byte[] ipByteArray = ip.getBytes(Charset.forName(CHARSET_NAME));
        message.write(ipByteArray.length);
        message.write(ipByteArray);
        message.write(port);
    }

    ChatNodeMessage(byte[] recvMessage) throws IOException {
        int uuidSize = UUID.randomUUID().toString().getBytes(Charset.forName(CHARSET_NAME)).length;
        //нужно ли учитывать, что не все байты дошли ?
       // ByteArrayInputStream recvMessageStream = new ByteArrayInputStream(recvMessage);
        DataInputStream recvMessageStream = new DataInputStream(new ByteArrayInputStream(recvMessage));
        byte[] uuidByteArray = new byte[uuidSize];
        //uuid = UUID.fromString(new String(Arrays.copyOfRange(recvMessage, 0, uuidSize), CHARSET_NAME));
        recvMessageStream.readFully(uuidByteArray, 0, uuidSize);
        uuid = UUID.fromString(new String(uuidByteArray, CHARSET_NAME));
        messageType = recvMessageStream.readInt();
        message = new ByteArrayOutputStream();
        message.write(recvMessage);
    }

    @Override
    public byte[] toBytes() {
        return message.toByteArray();
    }

    @Override
    public String getUUID() {
        return uuid.toString();
    }

    @Override
    public int getMessageType(){
        return messageType;
    }
}
