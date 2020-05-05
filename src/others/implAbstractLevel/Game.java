package others.implAbstractLevel;

import java.util.ArrayList;
import java.util.Random;
import others.tools.Tools;

public class Game  {
	public ArrayList<String> cards = null;
	public Random rdm = null;
	public Player[] ps = null;

	public Game() {

	}

	public Game(ArrayList<String> c, Random rd) {
		cards = c;
		rdm = rd;
		this.initCards(c);
	}

	/*
	 *@Param c =size33的ArrayList
	 *@Param rd new Random()
	 *@Param p 玩家数组
	 */
	public Game(ArrayList<String> c, Random rd, Player[] p) {
		cards = c;
		rdm = rd;
		ps = p;
		this.initCards(c);
	}


	/*
	 * 模拟游戏的顺序流程(默认4人游戏)
	 */

	public void procedue() {
		int flag = 0;
		boolean gameOver = false;
		Card havePutCard = null;

		//为玩家分发手牌
		distcher();

		while (flag < 100 && !gameOver) {
			flag++;
			for (int i = 0; i < 4 && !gameOver; i++) {
				//随机一张牌
				int n = newRandCard();
				dispatchCard(ps[i], Card.toCard(n));

				System.out.println(ps[i].getName() + "分到了" + Card.toNature(n));
				Tools.show(ps[i].getName(), ps[i].getpCards());

				// i接到牌，1、胡牌 2、暗杠牌
				// 玩家i申请胡牌
				if (ps[i].isSucceed()) {
					if(pHu(ps[i], Card.toCard(n))) {
						gameOver = true;
						break;
					}
				}

				// 玩家i申请暗杠
				else if (ps[i].isGang()) {
					pGang(ps[i], Card.toCard(n));
				}

				// 玩家i要碰牌
				else if (ps[i].isPeng() && havePutCard != null) {
					pPeng(ps[i], havePutCard);
					// 碰完后顺序会改变,改为上家揭牌
					if(i!=0)
						i-=2;
					else{
						i=3;
					}
				}

				// 玩家i无操作，只能出牌
				else {
					Card card = ps[i].putCard();
					// 当前牌已打出，保存，别人用
					havePutCard = card;

					System.out.println(ps[i].getName() + "打出了" + Card.toNature(card.getId()));

					// i出牌，放炮，其他人胡牌
					for (int j = 0; j < 4; j++) {
						if(j == i) {
							continue;
						}

						Tools.show(ps[j].getName(), ps[j].getpCards());

						// 其他玩家利用上个玩家的牌来杠
						if (ps[j].isGang()) {
							pGang(ps[j], havePutCard);
							break;
						}

						// 其他玩家要碰牌
						else if (ps[j].isPeng() && havePutCard != null) {
							pPeng(ps[j], havePutCard);
							// 碰完后顺序会改变,改为上家揭牌
							if(i!=0)
								i-=2;
							else{
								i=3;
							}
							break;
						}

						// 其他玩家要胡牌
						else if (ps[j].isSucceed()) {
							if(pHu(ps[j], havePutCard)) {
								gameOver = true;
								break;
							}
						}
						else {;	}
					}
				}
			}
		}
	}

	/*
	 * 把牌分配给四个玩家
	 */
	public void distcher() {
		for (int i = 0; i < ps.length; i++) {
			ps[i].setpCards(initPCards());
		}
	}

	/*
	 * 玩家碰牌
	 */
	public void pPeng(Player player, Card havePutCard) {
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
	}

	public void pGang(Player player, Card card) {
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
	}

	public boolean pHu(Player player, Card card) {
		boolean b = false;
		Card[] cpCards = player.getpCards().clone();
		player.addCard(cpCards, new Card(card.getId()));
		player.show(cpCards);

		//传入一个copy数组，如果失败，原数组没有改变
		boolean huPai = isHu(cpCards, player);
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
	 * 分发玩家一张牌
	 */

	public void dispatchCard(Player p, Card card) {
		p.addCard(p.getpCards(), card);
	}

	/*
	 * 其他人是否胡牌
	 */

	public boolean isHu(Card[] card, Player p) {
		return new HuPai(card).isSuccess();
	}

	/*
	 * 生成初始手牌
	 */

	public int[] initPCards() {
		int[] cards = new int[13];
		for (int i = 0; i < 13; i++) {
			cards[i] = newRandCard();
		}
		return cards;
	}

	public void initCards(ArrayList<String> c) {
		for (int i = 0; i < 32; i++) {
			c.add(i + ",4");
		}
	}

	/*
	 * 生成随机派
	 */
	public int newRandCard() {
		int r = rdm.nextInt(cards.size());
		String s = cards.get(r);
		String[] strs = s.split(",");
		int live = Integer.parseInt(strs[1]);
		live = live - 1;

		if (live != 0) {
			cards.set(r, strs[0] + "," + live);
		} else {
			cards.remove(r);
		}
		return Integer.parseInt(strs[0]);
	}

	/*
	 * 查看剩下有哪些牌
	 */
	public void restOfCards(ArrayList<String> c) {
		for (int i = 0; i < c.size(); i++) {
			System.out.print(c.get(i) + "  ");
		}
		System.out.println();
	}

	public ArrayList<String> getCards() {
		return cards;
	}

	public void setCards(ArrayList<String> cards) {
		this.cards = cards;
	}

	public Random getRdm() {
		return rdm;
	}

	public void setRdm(Random rdm) {
		this.rdm = rdm;
	}

}
