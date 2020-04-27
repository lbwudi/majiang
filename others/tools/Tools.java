package others.tools;

import others.implAbstractLevel.Card;

public class Tools {
	
	public static Card[] intToCard(int[] arr) {
		Card[] card = new Card[arr.length];
		int j=0;
		for(int i : arr) {
			card[j++] = Card.toCard(i);
		}
		return card;
	}
	
	
	/*
	 * �������ݲ���,��һ��{a b c ......}ת��������
	 */
	public static int[] parse(String str) {
		String[] split = str.split(" ");
		int[] a = new int[split.length];
		int i = 0;
		for(String s : split) {
			//System.out.println(s);
			a[i++] = Integer.parseInt(s);
		}
		return a;
	}
	
	public static void show(String name, Card[] pCards) {
		StringBuilder sb = new StringBuilder(name+":");
		for(int i=0; i<pCards.length && pCards[i].getId()!=50; i++) {
			sb.append("["+i+" "+Card.toNature(pCards[i].getId())+"],");
		}
		System.out.println(sb.toString());
	}
}
