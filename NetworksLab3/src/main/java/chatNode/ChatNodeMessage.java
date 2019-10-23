package chatNode;

import node.NodeMessage;

import java.io.*;
import java.nio.charset.Charset;
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
    private ByteArrayOutputStream messageByteArrayStream;
    private DataOutputStream messageDataStream;

    public ChatNodeMessage(int messageType) throws IOException {
        //message = new ByteArrayOutputStream();
        messageByteArrayStream = new ByteArrayOutputStream();
        messageDataStream = new DataOutputStream(messageByteArrayStream);
        uuid = UUID.randomUUID();
        this.messageType = messageType;
        messageDataStream.write(uuid.toString().getBytes(Charset.forName(CHARSET_NAME)));
        messageDataStream.writeInt(messageType);
        messageDataStream.flush();
    }

    ChatNodeMessage(int messageType, String textMessage) throws IOException{
        this(messageType);
        if(messageType == TEXT){
            byte[] textByteArray = textMessage.getBytes(Charset.forName(CHARSET_NAME));
            messageDataStream.writeInt(textByteArray.length);
            messageDataStream.write(textByteArray);
        }
        if(messageType == ACK){
            //to write uuid
            messageDataStream.write(textMessage.getBytes(Charset.forName(CHARSET_NAME)));
        }
        messageDataStream.flush();
    }

    ChatNodeMessage(int messageType, String ip, int port) throws IOException{
        this(messageType);
        byte[] ipByteArray = ip.getBytes(Charset.forName(CHARSET_NAME));
        messageDataStream.writeInt(ipByteArray.length);
        messageDataStream.write(ipByteArray);
        messageDataStream.writeInt(port);
        messageDataStream.flush();
    }

    public ChatNodeMessage(byte[] recvMessage) throws IOException {
        int uuidSize = UUID.randomUUID().toString().getBytes(Charset.forName(CHARSET_NAME)).length;
        //нужно ли учитывать, что не все байты дошли ?
        DataInputStream recvMessageStream = new DataInputStream(new ByteArrayInputStream(recvMessage));
        byte[] uuidByteArray = new byte[uuidSize];
        //uuid = UUID.fromString(new String(Arrays.copyOfRange(recvMessage, 0, uuidSize), CHARSET_NAME));
        recvMessageStream.readFully(uuidByteArray, 0, uuidSize);
        uuid = UUID.fromString(new String(uuidByteArray, CHARSET_NAME));
        messageType = recvMessageStream.readInt();
        messageByteArrayStream = new ByteArrayOutputStream();
        messageDataStream = new DataOutputStream(messageByteArrayStream);
        messageDataStream.write(recvMessage);
        messageDataStream.flush();
    }

    @Override
    public byte[] toBytes() {
        return messageByteArrayStream.toByteArray();
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
