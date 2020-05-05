package liu.client;

import others.abstractLevel.ClientInf;
import liu.Move;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;

public class Conn extends Thread{
	private static byte n = 0;

	private ClientBuffer clientBuffer = null;
	protected int id = -1;
	private OutputStream out;

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

			setOut(outputStream);
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



			/*
			获取游戏开始变量
			 */
			boolean canStart = false;
			int read = 0;
			try {
				read = inputStream.read();

			} catch (IOException e) {
				e.printStackTrace();
			}
			byte[] b = new byte[7]; b[0] = (byte)read;

			clientBuffer.setMsg(b);


			if (read == 1)
				canStart = true;
			/*
			加锁是为了防止界面还没有拿走值，而又从服务器读了一个，发生值的覆盖
			 */
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

			int a = -1;
			try {
				a = inputStream.read();
			} catch (IOException e) {
				e.printStackTrace();
			}

			this.id = a;

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

				byte[] initCards = get13(inputStream);

				clientBuffer.setMsg(initCards);
				synchronized (this){
					try {
						wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				/*
				客户端进入游戏流程，直至一方胡牌，游戏结束
				 */
				int i=0;
				while(!clientBuffer.getGameOver() && i++<300) {
					/*
					从服务器读一个数据包
					 */
					byte[] bag = new byte[7];
					try {
						inputStream.read(bag);
					} catch (IOException e) {
						e.printStackTrace();
					}

					while(clientBuffer.getMsg()!=null){;}
					clientBuffer.setMsg(bag);
					synchronized (this){
						try {
							wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}

				}
				//@end main

				try {
					inputStream.close();
					outputStream.close();
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		/*
		JFrame jFrame = myFrame.getjFrame();
		//加入图片
		String url= "D:/java/workSpace/TestNetInterface/src/？.jpg";
		ImageIcon imageIcon = new ImageIcon(url);
		JLabel label=new JLabel(imageIcon);
		label.setSize(50,50);
		jFrame.add(label);
		*/
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

	public OutputStream getOut() {
		return out;
	}

	public void setOut(OutputStream out) {
		this.out = out;
	}
}
