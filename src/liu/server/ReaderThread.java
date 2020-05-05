package liu.server;

import java.io.IOException;
import java.io.InputStream;


public class ReaderThread extends Thread{
    private InputStream inputStream = null;
    private MsgBuffer msgBuffer = null;
    private int id = -1;

    public ReaderThread(MsgBuffer msgBuffer, InputStream inputStream, int id){
        this.inputStream = inputStream;
        this.msgBuffer = msgBuffer;
        this.id = id;
    }
    @Override
    public void run() {


        byte[] b = null;

        b = msgBuffer.getMsg((byte) id);

        if(b == null){
            byte[] msg = new byte[7];
            try {
                inputStream.read(msg);

            } catch (IOException e) {
                e.printStackTrace();
            }


            msgBuffer.setMsg((byte)id, msg);

        }
        else{

        }
    }
}
