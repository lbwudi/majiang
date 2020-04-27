package others.implAbstractLevel;

public class Card {
	private int id ;

	//0:未公示 1：公示的碰 2：公示的杠
	private int lay ;

	public Card(int id) {
		this.id = id;
		lay = 0;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}


	public int getLay() {
		return lay;
	}

	public void setLay(int lay) {
		this.lay = lay;
	}

	public static Card toCard(int i) {
		return new Card(i);
	}
	/*
	 * 0-32
	 * [0,8] 万 ，[9-17] 条 ，[18-26] 柄， [27,32] 风中发
	 */
	public static String toNature(int cid) {
		String name = null;
		if(cid/9 == 0){
			name = cid+1+"万";
		} else if(cid/9 == 1) {
			name = (cid%9)+1+"条";
		} else if(cid/9 == 2) {
			name = (cid%9)+1+"柄";
		} else {
			String s = null;
			switch(cid%9) {
				case 0: s = "东";break;
				case 1: s = "西";break;
				case 2: s = "南";break;
				case 3: s = "北";break;
				case 4: s = "中";break;
				case 5: s = "发";break;
			}
			name = s;
		}
		return name;
	}

}
