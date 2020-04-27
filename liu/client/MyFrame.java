package liu.client;


import others.abstractLevel.ClientInf;
import liu.Move;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MyFrame extends JFrame implements Runnable{

    public MyFrame(ClientBuffer clientBuffer){
        this.clientBuffer = clientBuffer;
    }

    private String[] name = {"liu","wang","zhang","li"};
    private byte n = 0;
    private ClientBuffer clientBuffer;

    //放置玩家手牌牌的区域
    private JPanel cardsArea = new JPanel();
    //玩家操作的类别
    private Move move_tpye = Move.n;
    //玩家操作的手牌编号
    private byte move_card = -1;
    //从服务器接收到的一张新牌
    private byte card = -1;

    @Override
    public void run() {
         /*
        创建连接线程
         */
        ClientInf client = new Conn(clientBuffer);
        client.start();

        /*
        界面
         */
        JFrame jFrame = new JFrame(name[n++]);
        jFrame.setLayout(new FlowLayout());

        TextArea textArea = new TextArea(20,20);
        jFrame.add(textArea);

        //等待开始
        Button prepare = new Button("准备");
        prepare.setName("prepare");
        prepare.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                textArea.append(jFrame.getName()+"玩家已经准备");
                textArea.append("\\\\n");
                /*
                通知连接类去获取开始信号量
                 */
                synchronized (client){
                    client.notify();
                }
            }
        });

        jFrame.add(prepare);
        // 手牌区域
        jFrame.add(cardsArea);

        jFrame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        jFrame.setVisible(true);
        jFrame.setSize(new Dimension(500, 300));
        /*
        从缓冲池接收开始信息
         */
        byte[] msg = clientBuffer.getMsg();
        while(msg == null){
            msg = clientBuffer.getMsg();
            jFrame.validate();
        }    //msg数据已被取走

        textArea.append("\\\\n 收到"+msg+"游戏开始");

         /*
        接收序号信息
         */
        System.out.println("想要靠近你");
        synchronized (client){
            client.notify();
        }

        /*
        设置序号信息
         */
        msg = clientBuffer.getMsg();
        while(msg == null){
            msg = clientBuffer.getMsg();
                jFrame.validate();
        }    //msg数据已被取走
        textArea.append("\\\\n 收到值为"+msg[0]+"的序号");

        /*
         添加操作按钮
         */
        Button[] button = initButton();
        for (int i = 0; i < button.length; i++) {
            jFrame.add(button[i]);
        }
        jFrame.validate();

        /*
        获取初始13张牌
         */
        synchronized (client){
            client.notify();
        }


        msg = clientBuffer.getMsg();
        while(msg == null){
            msg = clientBuffer.getMsg();
            jFrame.validate();
        }    //msg数据已被取走

        Button[] cards = new Button[msg.length];
        for (int i = 0; i < msg.length; i++) {
            cards[i] = new Button(((Byte) msg[i]).toString());
            cards[i].setName(((Byte) msg[i]).toString());

            cardsArea.add(cards[i]);
        }
        cardsArea.validate();

    }

    /*
     * 操作按钮
     */
    public Button[] initButton() {
        Button[] button = new Button[4];
        String[] button_label = { "peng", "gang", "hu", "chupai" };
        for (int i = 0; i < 4; i++) {
            button[i] = new Button(button_label[i]);
            button[i].setName(button_label[i]);
            button[i].addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    String b_name = e.getComponent().getName();
                    String r = null;
                    switch (b_name) {
                        case "peng":
                            r = "peng  card";

                            break;

                        case "gang":
                            r = "gang card";
                            break;

                        case "hu":
                            r = "hu  card";
                            break;

                        case "chupai":
                            r = "put card";
                            setMove_card((byte)1);
                            setMove_tpye(Move.c);
                            break;
                    }
                    System.out.println(r);
                }
            });
        }
        return button;
    }

    public Move getMove_tpye() {
        return move_tpye;
    }

    public void setMove_tpye(Move move_tpye) {
        this.move_tpye = move_tpye;
    }

    public byte getMove_card() {
        return move_card;
    }

    public void setMove_card(byte move_card) {
        this.move_card = move_card;
    }

    public byte getCard() {
        return card;
    }

    public void setCard(byte card) {
        this.card = card;
    }
}

