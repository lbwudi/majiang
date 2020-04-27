package others.testImpAbstract;
import java.util.ArrayList;
import java.util.Random;

import others.implAbstractLevel.Card;
import others.implAbstractLevel.Game;
import others.implAbstractLevel.Player;
import others.tools.Tools;

public class TestGame extends Game{

	public TestGame(ArrayList<String> c, Random rd, Player[] p) {
		super(c, rd, p);
	}

	public TestGame(ArrayList<String> c, Random rd) {
		super(c, rd);
	}

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

				// 玩家i误操作，只能出牌
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
		//测试胡牌，碰牌和杠牌
		String[] str = new String[] {
				"0 1 2 3 4 5 6 7 8 9 10 11 12" ,
				"0 0 1 2 3 4 5 6 6 6 12 13 14" ,
				"1 2 3 3 4 5 10 11 12 13 14 15 16" ,
				"9 10 11 12 13 14 15 16 17 20 21 22 23"};
		for (int i = 0; i < ps.length; i++) {
			ps[i].setpCards(initPCards(str[i]));
		}
	}

	/*
	 * 生成初始手牌
	 */
	public int[] initPCards(String str) {
		System.out.println("new initPCards");

		//分配手牌
		int[] parse = new int[13];
		parse = Tools.parse(str);
		for(int j=0;j<13;j++) {
			System.out.print(Card.toNature(parse[j])+"  ");
		}
		System.out.println();

		return parse;
	}


}
