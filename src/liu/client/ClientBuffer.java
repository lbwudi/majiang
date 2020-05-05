package liu.client;

public class ClientBuffer {
    private byte[] msg = null;
    private boolean mutex = true;
    private boolean gameOver = false;

    /**
     * 取走一条数据，然后自动重置
     * @return
     */
    public synchronized byte[] getMsg() {
        if(mutex){
            if(msg != null){
                byte[] cp_msg = msg.clone();
                msg = null;
                return cp_msg;
            }
            else{
               return null;
            }
        }
        else{
            return null;
        }
    }

    public synchronized void setMsg(byte[] msg) {
        this.msg = msg;
    }

    public synchronized boolean getMutex() {
        return mutex;
    }

    public synchronized void setMutex(boolean mutex) {
        this.mutex = mutex;
    }

    public synchronized boolean getGameOver() {
        return gameOver;
    }

    public synchronized void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }
}
