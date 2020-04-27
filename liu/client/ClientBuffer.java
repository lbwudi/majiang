package liu.client;

import java.util.Arrays;

class ClientBuffer {
    private byte[] msg = null;

    /**
     * 取走一条数据，然后自动重置
     * @return
     */
    public byte[] getMsg() {
        if(msg != null){
            System.out.print(msg[0]+"   "+msg[1]);
            byte[] cp_msg = msg.clone();
            msg = null;

            for (byte i: cp_msg) {
                System.out.print(i+"  ");
            }
            System.out.print("美人已被降服");

            return cp_msg;
        }
        else{
           return null;
        }
    }

    public void setMsg(byte[] msg) {
        System.out.println("美人长眠中,望君来采撷");
        this.msg = msg;
    }

}
