package liu.client;


import liu.ToInt;
import liu.client.myEvent.MouseAdapter_Chupai;
import liu.client.myEvent.MouseAdapter_Gang;
import liu.client.myEvent.MouseAdapter_Hu;
import liu.client.myEvent.MouseAdapter_Peng;
import others.abstractLevel.ClientInf;
import liu.Move;
import others.abstractLevel.PlayerInterface;
import others.implAbstractLevel.Card;
import others.implAbstractLevel.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MyFrame extends JFrame implements Runnable{

    //操作的种类
    private Move move_type;
    //被选中的牌
    private byte chooseCard = -1;
    //被选中的排
    private JButton chooseButton = null;
    //放置玩家手牌牌的区域
    private JPanel cardsArea = new JPanel();
    //玩家的id
    private byte id = -1;
    //玩家
    private PlayerInterface player;
    //缓冲区
    private ClientBuffer clientBuffer;
    //写线程
    private Writer writer;

    public MyFrame(ClientBuffer clientBuffer){
        this.clientBuffer = clientBuffer;
    }

    @Override
    public void run() {
         /*
        创建连接线程
         */
        Conn client = new Conn(clientBuffer);
        client.start();

        /*
        界面
         */
        JFrame jFrame = new JFrame();
        jFrame.setLayout(new FlowLayout());

        TextArea textArea = new TextArea(20, 20);
        jFrame.add(textArea);

        //等待开始
        JButton prepare = new JButton("准备");
        prepare.setName("prepare");
        prepare.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                textArea.append(jFrame.getName() + "玩家已经准备");
                textArea.append("\r\n");
                 /*
                通知连接类去获取开始信号量
                 */
                synchronized (client) {
                    client.notify();
                }
            }
        });

        jFrame.add(prepare);
        // 手牌区域
        jFrame.add(cardsArea);
        //用addWindowListener代替，释放一下socket等
        //jFrame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        jFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                clientBuffer.setGameOver(true);
            }
        });
        jFrame.setVisible(true);
        jFrame.setSize(new Dimension(1000, 500));
        /*
        从缓冲池接收开始信息
         */

        byte[] msg = clientBuffer.getMsg();
        while (msg == null) {
            msg = clientBuffer.getMsg();
            jFrame.validate();
        }    //msg数据已被取走

        textArea.append("收到" + msg + "游戏开始");
        textArea.append("\r\n");

        /*
        在已经连接上之后，从Conn里获取输出连接，用于创建新线程
         */
        writer = new Writer(client.getOut(), clientBuffer);
        writer.start();
        textArea.append("writer is ok");
        textArea.append("\r\n");

         /*
        接收序号信息
         */
        synchronized (client) {
            client.notify();
        }

        /*
        设置序号信息
         */
        msg = clientBuffer.getMsg();
        while (msg == null) {
            msg = clientBuffer.getMsg();
            jFrame.validate();
        }    //msg数据已被取走

        id = msg[0];
        /*
        test
         */
        jFrame.setTitle(((Byte)msg[0]).toString());
        jFrame.validate();
        //end

        textArea.append("收到值为" + msg[0] + "的序号");
        textArea.append("\r\n");


        /*
         添加操作按钮
         */
        JButton[] moveButton = initButton();
        for (int i = 0; i < moveButton.length; i++) {
            jFrame.add(moveButton[i]);
        }
        jFrame.validate();

        /*
        获取初始13张牌
         */
        msg = readByte(client, jFrame);
        /*
        test
         */
        textArea.append("手牌");
        for(byte b : msg){
            textArea.append(b+",");
        }
        textArea.append("\r\n");
        //end test

        /*
        创建玩家对象
         */
        String[] name = new String[]{"张","钱", "孙", "黄"};
        player = new Player(name[id]);
        player.setpCards(ToInt.byteToInt_arr(msg));

        /*
        添加手牌按钮
         */
        JButton[] cards = new JButton[msg.length];
        for (int i = 0; i < msg.length; i++) {
            cards[i] = new JButton(Card.toNature(ToInt.byteToInt(msg[i])));

            String s = ((Byte) msg[i]).toString();
            cards[i].setName(s);
            cards[i].addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    chooseButton = (JButton) e.getComponent();
                    String name = e.getComponent().getName();
                    chooseCard = (byte)Integer.parseInt(name);
                    textArea.append(name+"click,chooseCard="+chooseCard);
                    textArea.append("\r\n");
                    jFrame.validate();
                }
            });
            cardsArea.add(cards[i]);
        }
        jFrame.validate();


        /*
        获取一张牌
         */
        boolean gameOver = false;
        int i=0;
        while (!gameOver && i++<300) {
            msg = readByte(client, jFrame);
            /*
            test
             */
            textArea.append("收到数据包");
            for(byte b : msg){
                textArea.append(b+",");
            }
            textArea.append("\r\n");
            //@end test

            //1 当前不是（消息）我的轮次，且当前数据包不是专门的渲染数据包
            boolean boo1 = true;
            if (msg[0] != id && msg[6] != 1) {
                /*
                test
                 */
                if(boo1) {
                    textArea.append("什么也不能做");
                    boo1 = false;
                }
                textArea.append("\r\n");
                //end test

                continue;
            }
            //2 当前是（消息）我的轮次，且当前数据包不是专门的渲染数据包
            boolean boo2 = true;
            if (msg[0] == id && msg[6] != 1) {
                /*
                test
                 */
                if(boo2) {
                    textArea.append("当前是我的轮次");
                    boo2 = false;
                }
                textArea.append("\r\n");
                //@end test

                addOneCard(msg);
                jFrame.validate();

                /*
                30s内完成操作
                 */
                MouseAdapter_Hu mouseAdapter_hu = new MouseAdapter_Hu();
                moveButton[0].addMouseListener(mouseAdapter_hu);

                MouseAdapter_Gang mouseAdapter_gang = new MouseAdapter_Gang();
                moveButton[1].addMouseListener(mouseAdapter_gang);

                MouseAdapter_Chupai mouseAdapter_chupai = new MouseAdapter_Chupai();
                moveButton[3].addMouseListener(mouseAdapter_chupai);
                jFrame.validate();

                /*
                20s计时器
                 */
                Thread timer = getTimer(20);
                boolean notSend = true;
                while (timer.isAlive() && notSend) {
                    //玩家通过事件触发
                    //2a
                    //自摸;
                    if (mouseAdapter_hu.getHu()) {

                        if (player.pHu((Player) player, Card.toCard(ToInt.byteToInt(msg[3])))) {

                            byte[] msg_s = new byte[]{msg[3], id, 1, -1};
                            writeByte(msg_s, writer, jFrame, clientBuffer);

                            notSend = false;
                            break;
                        }
                    }
                    //2b
                    //暗杠;
                    if (mouseAdapter_gang.getGang()) {
                        if (player.pGang((Player) player, Card.toCard(ToInt.byteToInt(msg[3])))) {

                            byte[] msg_s = new byte[]{msg[3], id, 2, -1};
                            writeByte(msg_s, writer, jFrame, clientBuffer);


                            notSend = false;
                            break;
                        }
                    }
                    //2c
                    //出牌
                    if (mouseAdapter_chupai.getChupai()) {
                        System.out.println("that is true");
                        int flag = 0;
                        while(flag < 3 || chooseCard != -1){
                            if(chooseCard == -1){
                                textArea.append("没有选中");
                                textArea.append("\r\n");
                                flag++;
                            }else{
                                textArea.append("选中"+chooseCard);
                                textArea.append("\r\n");
                                break;
                            }
                        }
                        if(chooseCard == -1)
                            chooseCard = (byte)player.getpCards()[0].getId();

                        byte[] msg_s = new byte[]{chooseCard, id, 6, -1};
                        writeByte(msg_s, writer, jFrame, clientBuffer);
                        textArea.append("have Wrote");
                        textArea.append("\r\n");
                        //从cardArea删去出过的牌
                        cardsArea.remove(chooseButton);

                        notSend = false;
                        break;
                    }
                }
                moveButton[0].removeMouseListener(mouseAdapter_hu);
                moveButton[1].removeMouseListener(mouseAdapter_gang);
                moveButton[3].removeMouseListener(mouseAdapter_chupai);
                jFrame.validate();

                //2d 30s结束仍然没有出牌，认为玩家离开
                if(notSend)
                {
                    textArea.append("当前系统自动出牌");
                    textArea.append("\r\n");
                    chooseCard = (byte)player.getpCards()[0].getId();
                    byte[] msg_s = new byte[]{chooseCard, id, 8, -1};
                    writeByte(msg_s, writer, jFrame, clientBuffer);
                    //cardsArea.remove(chooseButton);
                }
                continue;
            }

            //3  当前是专门的渲染数据包，为了所有玩家渲染某一玩家的操作（消息）
            boolean boo3 = true;
            if (msg[6] == 1)
            {
                 /*
                test
                 */
                if(boo3) {
                    textArea.append("渲染上一轮情况");
                    textArea.append("\r\n");
                    boo3 = false;
                }
                //@end test

                //      i A玩家把从B玩家处胡、杠、碰了的牌
                //AND   ii  自摸，暗杠
                if (msg[2] == 1 || msg[2] == 2)
                {
                    //A玩家相应的胡、杠、碰了的牌 加入自己的公示区里
                    //渲染页面（A胡、杠、碰了B）
                    if(msg[2] == 1) {
                        textArea.append(msg[1] + "自摸,是" + Card.toNature(
                                ToInt.byteToInt(msg[4])
                        ));
                    }
                    else{
                        textArea.append(msg[1] + "暗杠,是" + Card.toNature(
                                ToInt.byteToInt(msg[4])
                        ));
                    }
                    textArea.append("\r\n");
                }

                if(msg[2] == 3 || msg[2] == 4 || msg[2] == 5){
                    switch (msg[2]){
                        case 3:
                            textArea.append(msg[1] + "胡"+msg[5]+",是" + Card.toNature(
                                    ToInt.byteToInt(msg[4])
                            ));
                            break;
                        case 4:
                            textArea.append(msg[1] + "杠"+msg[5]+",是" + Card.toNature(
                                    ToInt.byteToInt(msg[4])
                            ));
                            break;
                        case 5:
                            textArea.append(msg[1] + "碰"+msg[5]+",是" + Card.toNature(
                                    ToInt.byteToInt(msg[4])
                            ));
                            break;
                    }
                    textArea.append("\r\n");
                }

                else {
                    //渲染页面（出牌）
                    textArea.append(msg[1] + "出了一张牌,是" + Card.toNature(
                            ToInt.byteToInt(msg[4])
                    ));
                    textArea.append("\r\n");
					textArea.append("其他玩家，可以执行操作");
					textArea.append("\r\n");

                    //操作选择
                    // 前提：是针对一张牌的第一次渲染
                    //实现：由于是出的牌，肯定满足前题
                    /*
                    3s计时器
                     */

                    MouseAdapter_Hu mouseAdapter_hu = new MouseAdapter_Hu();
                    moveButton[0].addMouseListener(mouseAdapter_hu);

                    MouseAdapter_Gang mouseAdapter_gang = new MouseAdapter_Gang();
                    moveButton[1].addMouseListener(mouseAdapter_gang);

                    MouseAdapter_Peng mouseAdapter_peng = new MouseAdapter_Peng();
                    moveButton[2].addMouseListener(mouseAdapter_peng);

                    jFrame.validate();

                    Thread timer = getTimer(3);
                    while (timer.isAlive())
                    {    //3s以内
                        if (mouseAdapter_hu.getHu()) {

                            if (player.pHu((Player) player, Card.toCard(ToInt.byteToInt(msg[4])))) {
                                //我胡别人的牌
                                byte[] msg_s = new byte[]{msg[3], id, 3, msg[1]};
                                writeByte(msg_s, writer, jFrame, clientBuffer);

                                mouseAdapter_hu.setHu(false);

                                break;
                            }
                        }


                        if (mouseAdapter_gang.getGang()) {

                            if (player.pGang((Player) player, Card.toCard(ToInt.byteToInt(msg[4])))) {
                                //我杠别人的牌
                                byte[] msg_s = new byte[]{msg[3], id, 4, msg[1]};
                                writeByte(msg_s, writer, jFrame, clientBuffer);
                                mouseAdapter_gang.setGang(false);

                                break;
                            }
                        }

                        if (mouseAdapter_peng.getPeng()) {  //碰

                            //我碰别人的牌
                            byte[] msg_s = new byte[]{msg[3], id, 5, msg[1]};
                            writeByte(msg_s, writer, jFrame, clientBuffer);
                            mouseAdapter_peng.setPeng(false);

                            break;
                        }
                    }

                    moveButton[0].removeMouseListener(mouseAdapter_hu);
                    moveButton[1].removeMouseListener(mouseAdapter_gang);
                    moveButton[2].removeMouseListener(mouseAdapter_peng);
                    jFrame.validate();
                }
            }
        }
    }

    /**
     * 线程计时器
     * @param i 希望i s后结束
     * @return
     */
    private Thread getTimer(int i) {
        Thread timer = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(i*1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        timer.start();
        return timer;
    }

    /**
     * 把一张牌加入手牌
     * @param msg
     */
    private void addOneCard(byte[] msg) {
        String s = ((Byte) msg[3]).toString();
        JButton b = new JButton(Card.toNature(ToInt.byteToInt(msg[3])));

        b.setName(s);
        b.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String name = e.getComponent().getName();
                chooseCard = (byte)Integer.parseInt(name);

            }
        });

        player.addCard(player.getpCards(), Card.toCard(ToInt.byteToInt(msg[3])));
        cardsArea.add(b);
    }

    /**
     * 通过Conn从服务器读取一个数据
     * @param client
     * @param jFrame
     * @return
     */
    private byte[] readByte(Conn client, JFrame jFrame) {
        synchronized (client){
            client.notify();
        }
        /*
        Writer的setMsg执行完了，还没写到服务器，readByte方法唤醒了conn
        ，但是conn的read还没读到是，主线程继续执行，拿走了Writer的setMsg
        越过了服务器
         */
        Thread.yield();

        byte[] msg;
        msg = clientBuffer.getMsg();

        //Writer执行
        while(!clientBuffer.getMutex()){;}

        //等待从服务器读到数据
        while(msg == null){
            msg = clientBuffer.getMsg();
            jFrame.validate();
        }    //msg数据已被取走

        return msg;
    }

    /**
     * 通过Conn写一条消息
     * @param jFrame
     * @param clientBuffer
     * @param msg
     * @return
     */
    private boolean writeByte(byte[] msg, Writer writer, JFrame jFrame, ClientBuffer clientBuffer) {
        boolean b = false;
        if(clientBuffer.getMsg() == null){
            clientBuffer.setMsg(msg);
            synchronized (writer){
                writer.notify();
            }
            b = true;
        }
        else{
            jFrame.add(new Label("当前Buffer的msg不为空"));
        }
        return b;
    }

    /**
     * 操作按钮初始化
     * @return
     */
    public JButton[] initButton() {
        JButton[] button = new JButton[4];
		String[] button_label = { "胡", "杠", "碰", "出牌" };
        String[] button_name = { "hu", "gang", "peng", "chupai" };
        for (int i = 0; i < 4; i++) {
            button[i] = new JButton(button_label[i]);
            button[i].setName(button_name[i]);
        }

        return button;
    }

    private boolean setMove(Move move) {
        if(move_type == null){
            move_type = move;
            return true;
        }
        return false;
    }

    public Move getMove_type() {
        Move l = move_type;
        move_type = null;
        return l;
    }

    public boolean checkHu(byte[] hands, byte one){
        return true;
    }

    public boolean checkGang(byte[] hands, byte one){
        return true;
    }

    public boolean checkPeng(byte[] hands, byte one){
        return true;
    }

    public boolean checkChupai(byte[] hands, byte one){
        return true;
    }

}

