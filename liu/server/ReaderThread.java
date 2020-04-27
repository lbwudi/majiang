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
        System.out.println("当前是"+id+"号线称");

        byte[] b = null;

        b = msgBuffer.getMsg((byte) id);

        if(b == null){
            byte[] msg = new byte[7];
            try {
                inputStream.read(msg);
                System.out.println(id+"号读到  "+msg[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("mutex="+ msgBuffer.getMutex());
            msgBuffer.setMsg((byte)id, msg);

        }
        else{
            System.out.println("问题少年是"+id+"号");
        }
    }
}
