package others.testImpAbstract;

import others.implAbstractLevel.Card;
import others.implAbstractLevel.HuPai;
import others.tools.Tools;

public class TestHuPai {

	public static void main(String[] args) {

		String s = "1 1 1 5 6 7 8 8 8 9 9";//0 2 1 1
//		 s = "1 1 1 5 5 5 5 6 7 7 7 7";		//0 3 1 0
//		 s = "3 4 5 5 5 5 6 6 6 7 8 9 9 9";	//0 2 2 1
//		 s = "2 3 4 4 4 4 5 5 5 6 6 6 7 7"; //0 0 4 1
//		 s = "2 3 4 4 4 4 5 5 5 6 7 7 8 9";	//0 1 3 1
//		 s = "1 3 4";
		//0 杠 ， 1 碰 ， 2 顺 ， 3 对
		//temp.test_m1(temp.parse(s));

		//[0,8] 万 ，[9-18] 条 ，[19-27] 柄， [28,33] 风中发
		s = "3 4 5 9 10 11 19 20 21 28 28 28 29 29 33 33";
		int[] arr = Tools.parse(s);
		Card[] card = new Card[arr.length];
		for(int i=0; i<arr.length; i++) {
			Card c = new Card(arr[i]);
			c.setLay(0);
			card[i] = c;
		}
		HuPai temp = new HuPai(card);

		System.out.println(temp.isSuccess());
	}


}
