package others.implAbstractLevel;

import java.util.ArrayList;
import java.util.Random;

import others.testImpAbstract.TestLocalGame;

public class Start {
	public static void main(String[] args) {
		Player p1 = new Player("p1");
		Player p2 = new Player("p2");
		Player p3 = new Player("p3");
		Player p4 = new Player("p4");
		Player[] ps = new Player[] { p1, p2, p3, p4 };
//实际情况，没有模拟过
//		Game g = new Game(new ArrayList<>(33), new Random(), ps);
//		g.procedue();

		//测试用例
		TestLocalGame g = new TestLocalGame(new ArrayList<>(33), new Random(), ps);
		g.procedue();
//			for(Player p : ps) {
//				p.show();
//				g.dispatchCard(p,g.newRandCard());
//				p.show();
//			}
//			g.restOfCards();
	}
}
