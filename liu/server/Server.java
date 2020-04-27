package liu.server;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	//ByteArrayInputStream byteBuffer = new ByteArrayInputStream(msg);

	private static Socket[] socket = new Socket[4];
	private static OutputStream[] out = new OutputStream[4];
	private static InputStream[] in = new InputStream[4];
	private static MsgBuffer msgBuffer = new MsgBuffer();

	public static void main(String[] args) {
		try {
			ServerSocket serverSocket = new ServerSocket(8888);
			for (int count = 0; count < 4; count++) {
				socket[count] = serverSocket.accept();
				System.out.println(count + "已连接");
				out[count] = socket[count].getOutputStream();
				in[count] = socket[count].getInputStream();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("所有玩家已连接");

		/*
		等所有玩家准备
		 */
		//启动4个读线程
		for (int j = 0; j < 4; j++) {
			System.out.println("正在启动第"+j+"个线程");
			ReaderThread readerThread = new ReaderThread(msgBuffer, in[j], j);
			readerThread.start();
		}

		byte mutex = 0;
		mutex = msgBuffer.getMutex();
		System.out.println("i was run");

		while(mutex < 4 ) {
			Thread.yield();
			mutex = msgBuffer.getMutex();
		}
		msgBuffer.refreshMutex();

//		System.out.println("i was run");
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
				out[count].write(rand(count));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("玩家初始手牌已写入完毕");

/*
		//生成初始玩家编号
//		Random random = new Random();
//		int first = random.nextInt(4);
		int first = 2;
		System.out.println(first);

		//生成初始玩家数据包，并发送
		byte[] msg_s = new byte[]{
				(byte) first,
				-1,
				7,
				9,
				-1,
				-1
		};
		try {
			out[first].write(msg_s);
		} catch (IOException e) {
			e.printStackTrace();
		}

		//
		byte[] msg_r = null;
		boolean gameOver = false;


		int flag = 0;
		while (!gameOver && flag++ < 30) {
			System.out.println("服务器端" + flag + "轮次准备进行");
			msg_r = chooseOne();

			for(int i=0; i<4; i++){
				try {
					out[i].write(msg_r);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		*/
	}

	/**
	 * 接收有操作的几个玩家发的数据包中跳出一个生出的
	 * 没有操作的玩家发送的是默认数据包
	 * @return
	 */
	public static byte[] chooseOne(){
		byte[][] info = new byte[4][7];
		for(int i=0; i<4; i++) {
			byte[] bag = new byte[7];

			//启动4个读线程
			for (int j = 0; j < 4; j++) {
				new ReaderThread(msgBuffer, in[j], j).start();
			}
			while (msgBuffer.getMutex() != 4) {
				;
			}

			for (int j = 0; j < 4; j++) {
				info[j] = msgBuffer.getMsg((byte) j);
			}

			/*
			 * 如果出牌的人是第2个玩家，
			 * 但当前程序会阻塞在i=0，始终不会向下运行
			 * 解决
			 * 1 排除、不干事的发送空数据包
			 * 2、用多线程
			 *
			 * 理解错误，其他客户端会发送默认数据包，所以4个read基本是同步的
			 * 只需要筛选一下就好
			 */
		}
		return info[2];
	}

	public static byte[] rand(int count) {
		byte[] b = new byte[13];
		for(int i=0; i<13; i++) {
			b[i] = (byte) (count*13+i);
		}
		return b;
	}
}
