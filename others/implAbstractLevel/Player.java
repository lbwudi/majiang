package others.implAbstractLevel;

import java.util.Scanner;

import others.abstractLevel.PlayerInterface;
import others.tools.Tools;

public class Player implements PlayerInterface{
	private Card[] pCards = new Card[17];
	private String name = null;
	private int position = 0;
	private boolean succeed=false;
	private boolean peng=false;
	private boolean gang=false;

	public Player(String name) {
		this.name = name;
		initPCards();
	}

	public static void main(String[] args) {

	}

	/*
	 * 初始化手牌
	 */
	private void initPCards(){
		for(int i=0; i<pCards.length; i++) {
			pCards[i] = new Card(50);
		}
	}


	/*
	 * 是否胡牌,玩家点击胡牌按钮，需要调动该方法
	 */
	public void isHu(Card[] card, Player p) {
		this.setSucceed(new HuPai(card).isSuccess());
	}


	/*
	 * 正式展示玩家手牌
	 */
	public void show(Card[] card) {
		System.out.print(name+"----");
		for(String i : toShow(card)) {
			System.out.print(i+"  ");
		}
		System.out.println();
	}

	/*
	 * 玩家碰牌
	 */
	public boolean pPeng(Player player, Card havePutCard) {
		// 设置玩家手牌，完成碰操作
		boolean peng = player.canPeng(havePutCard.getId());
		if (peng) {
			System.out.println("成功碰到" + Card.toNature(havePutCard.getId()));

			// 重置为初始值
			player.setPeng(false);
			// 把这张牌添加到手牌中
			int j = player.addCard(player.getpCards(), havePutCard);
			//设置setLay标志位为1
			for(int i=0; i<3; i++) {
				player.getpCards()[i+j].setLay(1);
			}
		} else {
			System.out.println("您不具备资格");
		}
		return peng;
	}

	public boolean pGang(Player player, Card card) {
		System.out.println("杠这张牌"+Card.toNature(card.getId()));
		// 设置玩家手牌，完成杠操作
		boolean gang = player.canGang(card.getId());
		if (gang) {
			System.out.println("成功杠");

			// 重置为初始值
			player.setGang(false);

			// 把这张牌添加到手牌中
			int j = player.addCard(player.getpCards(), card);
			//设置setLay标志位为2
			for(int i=0; i<4; i++) {
				player.getpCards()[i+j].setLay(2);
			}
		} else {
			System.out.println("您不具备资格");
		}
		return gang;
	}

	public boolean pHu(Player player, Card card) {
		boolean b = false;
		Card[] cpCards = player.getpCards().clone();
		player.addCard(cpCards, new Card(card.getId()));
		player.show(cpCards);

		//传入一个copy数组，如果失败，原数组没有改变
		boolean huPai = new HuPai(cpCards).isSuccess();
		if (huPai) {
			System.out.println("胡牌了====================");
			b = true;
			// 把这张牌添加到手牌中
			player.addCard(player.getpCards(), card);
		} else {
			System.out.println("你尚未胡牌");
		}
		return b;
	}


	/*
	 * 玩家碰牌
	 */
	public boolean canPeng(int card) {
		Card[] cpCards = pCards.clone();
		addCard(cpCards, new Card(card));

		int count=0;
		for(Card c : cpCards) {
			if(c.getId() == card) {
				System.out.println(count+"次相等");
				c.setLay(1);
				if(++count == 3) {
					break;
				}
			}
		}
		return count==3?true:false;
	}

	/*
	 * 玩家杠牌
	 */
	public boolean canGang(int card) {
		Card[] cpCards = pCards.clone();
		addCard(cpCards, new Card(card));
		int count=0;
		for(Card c : cpCards) {
			if(c.getId() == card) {
				c.setLay(2);
				if(++count == 4) {
					break;
				}
			}
		}
		return count==4?true:false;
	}

	/*
	 * 玩家胡牌
	 */
	public boolean canHu(int card) {
		Card[] cpCards = pCards.clone();
		addCard(cpCards, new Card(card));
		boolean success = new HuPai(cpCards).isSuccess();
		return success;
	}

	/*
	 * 把数组转化成自然语言
	 * 0-32都要+1表示
	 */
	private String[] toShow(Card[] card) {
		String[] namedCards = new String[currentUsedLen(card)];
		int index = 0;
		for(int i=0; i<currentUsedLen(card); i++) {
			if(card[i].getId()/9 == 0){
				namedCards[index] = card[i].getId()+1+"万";
			} else if(card[i].getId()/9 == 1) {
				namedCards[index] = (card[i].getId()%9)+1+"条";
			} else if(card[i].getId()/9 == 2) {
				namedCards[index] = (card[i].getId()%9)+1+"柄";
			} else {
				String s = null;
				switch(card[i].getId()%9) {
					case 0: s = "东";break;
					case 1: s = "西";break;
					case 2: s = "南";break;
					case 3: s = "北";break;
					case 4: s = "中";break;
					case 5: s = "发";break;
				}
				namedCards[index] = s;
			}
			index++;
		}
		return namedCards;
	}


	public Card[] getpCards() {
		return pCards;
	}

	/*
	 * 设置玩家初始手牌,排好序
	 */
	public void setpCards(int[] card_id) {
		for(int i=0; i<card_id.length; i++) {
			int current = card_id[i];
			int j=i;
			for(; j>0; j--) {
				if(current < pCards[j-1].getId()) {
					pCards[j] = pCards[j-1];
				} else {
					break;
				}
			}
			pCards[j] = new Card(card_id[i]);
		}
	}

	/*
	 * 增加一张新牌
	 */
	public int addCard(Card[] p, Card card) {
		int j=0;
		for(; j<p.length-1; j++) {
			if(p[j].getId()>=card.getId()) {
				break;
			}
		}
		for(int i=p.length-2; i>=j; i--) {
			p[i+1] = p[i];
		}
		p[j] = card;
		return j;
	}


	/*
	 * 出一张牌，重新排序
	 */
	public Card putCard() {
		int i = selectOneFromUsefulLength();
		//先保存好值，返回用
		Card c = pCards[i];
		//从牌组删除c
		for(int j=i+1; j<pCards.length; j++) {
			pCards[j-1] = pCards[j];
		}
		return c;
	}

	public int selectOneFromUsefulLength() {
		int index = 0;
		Tools.show(name, pCards);

		System.out.println("请在下行选择出一张牌");
		Scanner in = new Scanner(System.in);
		index = in.nextInt();
		while(pCards[index].getLay() != 0) {
			System.out.println("当前牌已公示，请重新选择");
			selectOneFromUsefulLength();
		}

		return index;
	}

	/*
	 * 当前有效牌的长度
	 */
	private int currentUsedLen(Card[] card) {
		int len = 0;
		for(Card c : card) {
			if(c.getId() != 50)
				len++;
		}
		//System.out.println("==="+len);
		return len;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isSucceed() {
		System.out.println("请输入你是否要胡牌:1、是  2、不是");
		Scanner in = new Scanner(System.in);
		int i = in.nextInt();

		return i==1? true:false;
	}

	public void setSucceed(boolean succeed) {
		this.succeed = succeed;
	}

	public boolean isPeng() {
		System.out.println("请输入你是否要碰:1、是  2、不是");
		Scanner in = new Scanner(System.in);
		int i = in.nextInt();

		return i==1? true:false;
	}

	public void setPeng(boolean peng) {
		this.peng = peng;
	}

	public boolean isGang() {
		System.out.println("请输入你是否要杠:1、是  2、不是");
		Scanner in = new Scanner(System.in);
		int i = in.nextInt();

		return i==1? true:false;
	}

	public void setGang(boolean gang) {
		this.gang = gang;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}


}
