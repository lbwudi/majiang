package liu.client;

import java.io.IOException;
import java.io.OutputStream;

public class Writer extends Thread{
    private OutputStream out;
    private ClientBuffer clientBuffer;

    public Writer(OutputStream out, ClientBuffer clientBuffer) {
        this.out = out;
        this.clientBuffer = clientBuffer;
    }

    @Override
    public void run() {
        int i=0;
        while(!clientBuffer.getGameOver() && i++<300){
            synchronized (this) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            try {
                byte[] b = clientBuffer.getMsg();
                //防止还没有写入到服务器就被客户端读走（不经过服务器的非法交互）
                clientBuffer.setMutex(false);
                if(b!=null) {
                    out.write(b);
                    clientBuffer.setMutex(true);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
