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

	/*
	 * 分配特殊的手牌
	 */
	public int[] disptcher(int i) {
		//测试胡牌，碰牌和杠牌
		String[] str = new String[] {
				"0 1 2 3 4 5 6 7 8 9 10 11 12" ,
				"0 0 1 2 3 4 5 6 6 6 12 13 14" ,
				"1 2 3 3 4 5 10 11 12 13 14 15 16" ,
				"9 10 11 12 13 14 15 16 17 20 21 22 23"};

		return initPCards(str[i]);
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
