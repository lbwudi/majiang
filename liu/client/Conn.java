package liu.client;

import others.abstractLevel.ClientInf;
import liu.Move;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Conn extends Thread implements ClientInf {
	private static byte n = 0;

	private ClientBuffer clientBuffer = null;
	protected int id = -1;

	protected Conn(ClientBuffer clientBuffer) {
		this.clientBuffer = clientBuffer;
	}

	@Override
	public void run() {
		//打开客户端
		//连接服务器
		Socket socket = null;
		InputStream inputStream = null;
		OutputStream outputStream = null;
		try {
			socket = new Socket(InetAddress.getLocalHost(), 8888);
			inputStream = socket.getInputStream();
			outputStream = socket.getOutputStream();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if(socket != null && inputStream != null) {
			/*
			等待玩家点击准备
			 */
			synchronized (this){
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}


			/*
			告诉服务器玩家已经准备
			 */
			byte[] b_0 = new byte[7]; b_0[0] = 1;
			try {
				outputStream.write(b_0);
			} catch (IOException e) {
				e.printStackTrace();
			}

			System.out.println("目前准备了"+(n++)+"个,"+b_0[0]);

			/*
			获取游戏开始变量
			 */
			boolean canStart = false;
			int read = 0;
			try {
				read = inputStream.read();
				System.out.println("conn read "+read);
			} catch (IOException e) {
				e.printStackTrace();
			}
			byte[] b = new byte[7]; b[0] = (byte)read;

			clientBuffer.setMsg(b);


			if (read == 1)
				canStart = true;

			synchronized (this){
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			/*
			获取玩家id
			 */
			System.out.println("当前开始读");
			int a = -1;
			try {
				a = inputStream.read();
			} catch (IOException e) {
				e.printStackTrace();
			}
			this.id = a;
			System.out.println("当前读到id="+id);
			byte[] b_1 = new byte[7]; b_1[0] = (byte)id;

				clientBuffer.setMsg(b_1);


			synchronized (this){
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}


			/*
			游戏开始判定
			 */
			if(canStart) {
				System.out.println("游戏开始");

				byte[] initCards = get13(inputStream);
				clientBuffer.setMsg(initCards);

				synchronized (this){
					try {
						wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				synchronized (this){
					try {
						wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				//展示初始手牌
//				showInitCards(initCards);
//				System.out.println(id+"已经渲染完初始手牌");

				boolean gameOver = false;
				int flag = 30;
//				while(flag-->0) {
//					System.out.println("客户端"+flag+"轮次准备进行");
//					byte[] bag = new byte[6];
//
//					try {
//						inputStream.read(bag);
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
//					clientBuffer.setMsg(bag);
//					synchronized (this){
		//				try {
		//					wait();
		//				} catch (InterruptedException e) {
		//					e.printStackTrace();
		//				}
		//			}
//
//					gameOver= bag[0] == -1 ? true : false;
//					if(gameOver) {
//						//游戏结束需要通知其他玩家
//						break;
//					}
//
//					//当前是我的轮次
//					if(bag[0] == id){
//						isMyTurn(bag,outputStream);
//					}
//
//					//当前不是我的轮次
//					if(bag[0] != id)
//					{
//						isNotMyTurn(inputStream,outputStream);
//					}
//				}
				//@end main
			}
		}

		/*
		JFrame jFrame = myFrame.getjFrame();
		//加入图片
		String url= "D:/java/workSpace/TestNetInterface/src/1.jpg";
		ImageIcon imageIcon = new ImageIcon(url);
		JLabel label=new JLabel(imageIcon);
		label.setSize(50,50);
		jFrame.add(label);
		*/
	}


	/**
	 * 当前不是是我的轮次，我需要完成的功能：
	 * 1、胡、杠、碰别人
	 * 2、接收别玩家的操作，并渲染（必须）,name当前的bag对我是无用的

	protected void isNotMyTurn(InputStream inputStream, OutputStream outputStream) {
		byte[] msg_r = new byte[6];
		try {
			inputStream.read(msg_r);
		} catch (IOException e) {
			e.printStackTrace();
		}

		//不管你有什么操作，我先给你显示
		showSomeoneMove(msg_r);

		//对显示的信息进行甄别，如果是别的玩家正常出牌，看是否执行操作
		if(msg_r[2] == 6 || msg_r[2] == 8){

			这里也不能用循环写，需要重写改结构了，事件无法冒泡过来
			Move move = myFrame.getMove_tpye();
			if(move == Move.h || move == Move.p || move == Move.g) {
				byte[] b = pOrHuOrGByOthers(move, msg_r);
				try {
					outputStream.write(b);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}*/

	/**
	 * 渲染某个玩家的行动
	 */
	private void showSomeoneMove(byte[] msg_r) {
		System.out.println(msg_r[1]+"号玩家进行了"+msg_r[2]+"的操作，操作对象为"+msg_r[5]+
				"牌为"+msg_r[4]);
	}


	/**
	 * 当前是我的轮次，我需要完成的功能：
	 * 1、自摸、暗杠
	 * 2、出牌
	 * 3、接收一张新牌（必须）

	protected void isMyTurn(byte[] bag, OutputStream outputStream){
		if(bag[2] == 7)
		{
			System.out.println(id+"接收到一张新牌"+bag[3]);
			Move move = myFrame.getMove_tpye();
			byte[] msg = null;
			这里也不能用循环写，需要重写改结构了，事件无法冒泡过来
			if(move == Move.c){
				界面产生出牌事件
				msg = putCard();
				//自己无需渲染，等下个轮次接收服务器数据包后会渲染的
			}
			else if(move == Move.h || move == Move.g){
				界面产生碰，胡事件
				if(move == Move.h){
					System.out.println("我选择hu");
					msg = new byte[]{1,(byte)id,1};
				}
				if(move == Move.g){
					System.out.println("我选择gang");
					msg = new byte[]{1,(byte)id,2};
				}
			}
			try {
				if(msg != null)
					outputStream.write(msg);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}*/

	/**
	 * 出牌
	 * @return

	protected byte[] putCard(){
		System.out.println("我选择出牌,id="+myFrame.getMove_card());

		return n;
	}*/


	/**
	 * 杠碰胡别人
	 * @param msg_r
	 */
	private byte[] pOrHuOrGByOthers(Move move, byte[] msg_r) {
		System.out.println(move.toString()+"别人的一张牌");
		//需要分情况讨论
		return new byte[]{msg_r[4],(byte)id,3};
	}


	/*
	 * 从服务器获取初始的13张
	 */
	protected byte[] get13(InputStream inputStream) {
		byte[] b = new byte[13];
		try {
			inputStream.read(b);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return b;
	}

}
