package liu.client;

public class ClientStart {
    public static void main(String[] args) {
        MyFrame liu0 = new MyFrame(new ClientBuffer());
        MyFrame liu1 = new MyFrame(new ClientBuffer());
        MyFrame liu2 = new MyFrame(new ClientBuffer());
        MyFrame liu3 = new MyFrame(new ClientBuffer());
        MyFrame[] myFrames = new MyFrame[]{liu0,liu1,liu2,liu3};
        for(int i=0; i<4; i++){
            Thread thread = new Thread(myFrames[i]);
            thread.start();
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
        }
    }
}
