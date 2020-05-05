package liu.server;
import liu.ToInt;
import others.implAbstractLevel.Card;
import others.implAbstractLevel.Game;
import others.implAbstractLevel.Player;
import others.testImpAbstract.TestGame;
import sun.util.calendar.BaseCalendar;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class Server {
	//ByteArrayInputStream byteBuffer = new ByteArrayInputStream(msg);

	private static Socket[] socket = new Socket[4];
	private static OutputStream[] out = new OutputStream[4];
	private static InputStream[] in = new InputStream[4];
	private static MsgBuffer msgBuffer = new MsgBuffer();
	private static TestGame game = new TestGame(new ArrayList<>(33), new Random());;

	public static void main(String[] args) {

		try {
			ServerSocket serverSocket = new ServerSocket(8888);
			for (int count = 0; count < 4; count++) {
				socket[count] = serverSocket.accept();

				out[count] = socket[count].getOutputStream();
				in[count] = socket[count].getInputStream();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		/*
		等所有玩家准备
		 */
		//启动4个读线程
		for (int j = 0; j < 4; j++) {

			ReaderThread readerThread = new ReaderThread(msgBuffer, in[j], j);
			readerThread.start();
		}

		byte mutex = 0;
		mutex = msgBuffer.getMutex();


		while(mutex < 4 ) {
			Thread.yield();
			mutex = msgBuffer.getMutex();
		}
		msgBuffer.refreshMutex();


		/*
		写入开始信号
		 */
		for (byte count = 0; count < 4; count++) {
			try {
				out[count].write(1);
				out[count].write(count);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("所有玩家已准备，游戏开始");

		//写入初始13张牌
		for (int count = 0; count < 4; count++) {
			try {
				out[count].write(handCards(count));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}




		/*
		生成初始玩家数据包，并发送第一个玩家
		 */
		byte[] msg_r = new byte[4];
		Random random = new Random();
		//int first = random.nextInt(4);
		int first = 1;
		byte current = (byte)first;
		byte[] msg_s = new byte[]{current, -1, 7, newCard(), -1, -1, 0};

		/*
		轮回开始
		 */
		boolean gameOver = false;
		int flag = 300;
		while (!gameOver && flag-- > 0) {
			boolean exit = true;

			exit = socket[0].isClosed() && socket[1].isClosed() &&
					socket[2].isClosed() && socket[3].isClosed();

			if(exit){
				System.out.println("sorry");
				break;
			}

			for (int count = 0; count < 4; count++) {
				try {
					out[count].write(msg_s);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}



			/*
			读取响应信息
			 */
			try {
				in[current].read(msg_r);
			} catch (IOException e) {
				e.printStackTrace();
			}
			/*
			test
			 */
			System.out.print("收到"+current+"响应信息为");
			for(byte i:msg_r){
				System.out.print(i+",");
			}
			System.out.println();
			//End test

			if(msg_r[2] == 1){    //自摸
				msg_s = new byte[]{-1, msg_r[1], 1, -1, msg_r[0], -1, 1};
			}
			if(msg_r[2] == 2){    //暗杠
				msg_s = new byte[]{-1, msg_r[1], 2, -1, msg_r[0],-1, 1};
			}
			if(msg_r[2] == 6 || msg_r[2] == 8){    //出牌
				msg_s = new byte[]{-1, msg_r[1], 6, -1, msg_r[0],-1, 1};
			}

			//msg_s = 渲染数据包;
			for(int i=0; i<4; i++){
				try {
					out[i].write(msg_s);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
/*
			test
			 */
			System.out.print("返回客户"+current+"信息为");
			for(byte i:msg_s){
				System.out.print(i+",");
			}
			System.out.println();
			//End test


			/*
			处理用户响应事件
			 */
			Thread timer = getTimer(3);
			while(timer.isAlive()){	//3s内
				if(msgBuffer.getMutex() > 0){	//收到了有效操作
					byte[] b = null;
					if(msgBuffer.getMutex() > 1){	//收到多个
						b = chooseOne(msg_r[1]);	//上一轮谁出的牌
					}
					else{
						b = msg_r;
					}
					msg_s = new byte[]{-1, b[1], b[2], -1, b[0], b[3], 1};

					for(int i=0; i<4; i++){
						try {
							out[i].write(msg_s);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}

			/*
			下轮数据包
			 */
			current = (byte)(msg_r[1]+1);
			if(current == 4)
				current = 0;
			msg_s = new byte[]{
					current,
					-1,
					7,
					newCard(), -1, -1, 0};	//给下个用户的牌
		}

		for(int i=0; i<4; i++){
			try {
				in[i].close();
				out[i].close();
				socket[i].close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static byte newCard() {
		return (byte)game.newRandCard();
	}

	/**
	 * 接收有操作的几个玩家发的数据包中跳出一个生出的
	 * 没有操作的玩家发送的是默认数据包
	 * @param forward 上一轮谁出的牌
	 * @return
	 */
	public static byte[] chooseOne(byte forward){
		byte[][] bufferMsgs = new byte[4][7];
		for(byte[] i : bufferMsgs){
			i = null;
		}

		for(byte i=0; i<4; i++) {
			if(i!=forward){
				byte[] bufferMsg = msgBuffer.getMsg(i);
				bufferMsgs[i] = bufferMsg;
			}
		}

		for(byte i=0; i<4; i++) {
			//先返回第一个部不为null的
			if(bufferMsgs[i] != null){
				return bufferMsgs[i];
			}
		}
		return null;
	}

	public static byte[] handCards(int id) {
		byte[] b = new byte[13];

		int[] disptcher = game.disptcher(id);
		for(int i=0; i<13; i++) {
			b[i] = (byte) disptcher[i];
		}
		return b;
	}

		/**
		 * 线程计时器
		 * @param i 希望i s后结束
		 * @return
		 */
		private static Thread getTimer(int i) {
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
}
