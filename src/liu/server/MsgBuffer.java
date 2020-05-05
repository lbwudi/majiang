package liu.server;

class MsgBuffer {
    private byte mutex = 0;
    private byte[][] msg = new byte[4][7];

    protected MsgBuffer(){
        for(byte[] i : msg){
            i = null;
        }
    }

    public synchronized byte[] getMsg(byte index) {
        if(mutex > 0 && msg[index] != null){
            mutex--;
            byte[] clone = msg[index].clone();
            msg[index] = null;
            return clone;
        }
        else{

            return null;
        }
    }

    public synchronized void setMsg(byte index, byte[] info) {
        mutex++;
        msg[index] = info;
    }

    public synchronized byte getMutex(){
        return mutex;
    }

    public synchronized void setMutex(){
        mutex++;
    }

    public synchronized void refreshMutex(){
        mutex = 0;
    }

}
