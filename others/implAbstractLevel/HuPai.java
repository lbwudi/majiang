package others.implAbstractLevel;

public class HuPai{

	private Card[] card;

	public HuPai(Card[] card) {
		this.card = card;
	}

	public boolean isSuccess() {
		// 处理已公示的碰和杠
		int[] publicedP_G = getPublicedP_G();
		// t[0] 杠 ， 1 碰 ， 2 顺 ， 3 对
		int[] total = new int[4];
		total[0] += publicedP_G[0];
		total[1] += publicedP_G[1];

		boolean f = sumTotal(total);
		// 根据已公示的杠进行查表，判断是否可以胡牌，见table.txt
		if (f) {
			switch (publicedP_G[0]) {
				case 0:
					if (total[1] + total[2] == 4 && total[3] == 1) {
						return true;
					}
					break;
				case 1:
					if (total[1] + total[2] == 3 && total[3] == 1) {
						return true;
					}
					break;
				case 2:
					if (total[1] + total[2] == 2 && total[3] == 1) {
						return true;
					}
					break;
				case 3:
					if (total[1] + total[2] == 1 && total[3] == 1) {
						return true;
					}
					break;
			}
		}
		return false;
	}


	/*
	 * 获取已公示的碰和杠
	 */
	private int[] getPublicedP_G() {
		int[] r = new int[2];
		for(int i=0; i<card.length; i++) {
			switch(card[i].getLay()) {
				case 1:
					r[1]++; i+=2; break;	//peng++
				case 2:
					r[0]++; i+=3; break;	//gang++
			}
		}
		return r;
	}

	/*
	 * 对玩家的完整手牌综合分类
	 * @param card为玩家原始牌组
	 * @param total t[0] 杠 ， 1 碰 ， 2 顺 ， 3 对
	 */
	public boolean sumTotal(int[] total) {
		int[] typeData = null;
		boolean r = true;
		//风发中单独处理
		for(int i=0; i<3; i++) {
			typeData = washData(i, total);
			boolean sum = sum(typeData, total);
			//System.out.println("sum="+sum);
		}

		//风发中处理
		int[] other = washData(3, total);
		int[] arr = new int[6];
		for(int i=0; i<other.length; i++) {
			//[27,32] 风中发
			switch(other[i] % 9) {
				case 0: arr[0]+=1;break;
				case 1: arr[1]+=1;break;
				case 2: arr[2]+=1;break;
				case 3: arr[3]+=1;break;
				case 4: arr[4]+=1;break;
				case 5: arr[5]+=1;break;
			}
		}

		for(int i=0; i<arr.length; i++) {
			switch(arr[i]) {
				case 2:total[3]+=1;break;
				case 3:total[1]+=1;break;
			}
			if(arr[i] == 1) {
				r = false;
			}
		}


		return r;
	}

	/*
	 * 清洗数据
	 * @param  type=[0-2] 万，条，柄  type=3 发中风
	 * @param  dividedCard，已经分好类的数据
	 * @return 返回程序当前判定的某一类数据（无冗余）
	 */
	public int[] washData(int type, int[] total) {
		if(type > 3) {
			return new int[] {};
		}

		int[][] tmp = divide(total);
		int end = 0;
		for(; end< tmp[type].length; end++) {
			if(tmp[type][end] == 50)
				break;
		}
		end -= 1;	//最后一个有效数据的下标

		int[] r = new int[end+1];
		for(int i=0; i<r.length; i++) {
			r[i] = tmp[type][i];
		}
		return r;
	}


	/*
	 * @param card为玩家手牌，且此处是默认---胡的牌,并且要排除card.lay != 0的元素（已公示）
	 * @param total t[0] 杠 ， 1 碰 ， 2 顺 ， 3 对
	 * @return tmp	t0， t1，t2 都是1-9数据，t3风发中（值属于[27,32]）
	 * 				t0， t1，t2,t3长度都为17，
	 * 				t0， t1，t2,t3存在冗余数据，冗余数据的值都为50
	 */
	private int[][] divide(int[] total) {
		int[][] tmp = new int[4][17];
		int z=0,j=0,k=0,h=0;

		for(int i=0; i<card.length; i++) {
			//排除card.lay != 0的元素（已公示）
			if(card[i].getLay() == 0) {
				if(card[i].getId()/9 == 0){   //万
					tmp[0][z++] = card[i].getId()%9+1;
				} else if(card[i].getId()/9 == 1) { //条
					tmp[1][j++] = card[i].getId()%9+1;
				} else if(card[i].getId()/9 == 2) { //柄
					tmp[2][k++] = card[i].getId()%9+1;
				} else {	//风发中
					//27-32  +1
					//顺子牌都小于10
					tmp[3][h++] = card[i].getId();
				}
			}
		}
		return tmp;
	}

	/*
	 * 找到所有碰
	 * @param arr默认已经排好序，且元素值在1~9之间，且已公示的不在arr中
	 * @param total t[0] 杠 ， 1 碰 ， 2 顺 ， 3 对
	 */
	public boolean sum(int[] arr, int[] total) {
		//t[0] 杠 ， 1 碰 ， 2 顺 ， 3 对
		//int[] total = new int[4];
		//初始化桶
		int[] box = new int[11];	//box[0] and box[10]恒为 0
		boolean flag = false;
		for(int i=0; i<arr.length; i++) {
			box[arr[i]]++;
		}

		//end 初始化桶

		//遍历桶,分别统计个数
		for(int i=1; i<box.length-1; i++) {
			//此时一定为碰
			if(box[i] == 3 && box[i+1]==0 && box[i-1]==0) {	//0和10为0，所以成立
				total[1]++;	    //碰数量+1
				box[i] -= 3;	//不再考虑
				//continue;	//i+1，执行下次判断
			} else if(box[i] == 2 && box[i+1]==0 && box[i-1]==0){
				total[3]++;	 //对数量+1
				box[i] -= 2; //不再考虑
				//continue;	//i+1，执行下次判断
			} else {
				if(!flag) {
					/*
					 * @input 一个原始桶
					 * @output 顺子优先标记后的桶
					 * 1~9中只存在一种111 3 4 5 这样碰不和顺连在一起的情况
					 *
					 * 不断拿出一张和整体去匹配，最后剩下不是对子则错误
					 * 要求：不能改变原始桶
					 */
					for(int j=0; j<box[i]; j++) {
						int tmp[] = seqFirst(box, i, j+1);
						if(tmp[4] == -1) {
							//存在冗余
							int[] pf = pengFirst(box, i, j+1);
							//box[i]+1-1 啥也不做
							if(pf[4] != -1) {
								//无冗余，需要记录pf中每种分类的个数，最后查表
								for(int z=0; z<total.length; z++) {
									total[z] += pf[z];
								}
								flag = true;

								if(flag) {
									if(box[i] == 1) {
										flag = false;
									} else {
										switch(box[i]-j-1) {
											case 2:
												total[3]+=1;break;
											case 3:
												total[1]+=1;break;
										}
									}
								}
								break;
							}
						} else {
							//无冗余，需要记录tmp中每种分类的个数，最后查表
							for(int z=0; z<total.length; z++) {
								total[z] += tmp[z];
							}
							flag = true;

							boolean b = true;
							//判断还未解封的
							if(flag && b) {
								if(box[i] == 1) {
									b = false;
								} else {
									switch(box[i]-j-1) {
										case 2:
											total[3]+=1;
											//数值控制的不对
											break;
										case 3:
											total[1]+=1;
											//数值控制的不对
											break;
									}
								}
							}
							break;
						}
						//封印中还残留一些东西
					}
				}
			}
		}
		return flag;
	}

	/*
	 * @param box 一个原始桶
	 * @param index 当前拿出来和整体比较的牌下标
	 * @return 记录每种个数，最后汇总，查表
	 * 顺子优先,碰次之标记
	 * 要求：不能改变原始桶
	 */
	public int[] seqFirst(int[] box, int index, int index_value) {

		int[] cpBox = box.clone();
		cpBox[index] = index_value;	//逐次拿出,index_value决定当前有几张

		//num[0] 杠 ， 1 碰 ， 2 顺 ， 3 对  ,4: -1:已经出错无需查表
		int[] num = new int[5];

		//先找顺子，并在桶中删除他们
		shun(cpBox, num);
		//找出碰，并在桶中删除他们
		peng(cpBox, num);
		//如果存在单独的一张牌,则不可能胡牌
		dui(cpBox, num);

		return num;
	}


	public int[] pengFirst(int[] box, int index, int index_value) {
		int[] cpBox = box.clone();
		cpBox[index] = index_value;	//逐次拿出,index_value决定当前有几张

		//num[0] 杠 ， 1 碰 ， 2 顺 ， 3 对  ,4: -1:已经出错无需查表
		int[] num = new int[5];

		//先找出碰，并在桶中删除他们
		peng(cpBox, num);
		//再找顺子，并在桶中删除他们
		shun(cpBox, num);
		//如果存在单独的一张牌,则不可能胡牌
		dui(cpBox, num);

		return num;
	}


	public void peng(int[] cpBox, int[] num) {
		for(int i=1; i<cpBox.length-1; i++) {
			if(cpBox[i] == 3) {
				num[1]++;
				cpBox[i] -= 3;
			}
		}
	}

	public void shun(int[] cpBox, int[] num) {
		//再找顺子，并在桶中删除他们
		for(int i=1; i<cpBox.length-1-2; i++) {	//顺子在i=7即可结束
			if(cpBox[i] !=0 && cpBox[i+1]!=0 && cpBox[i+2]!=0) {
				num[2]++;
				cpBox[i]--; cpBox[i+1]--; cpBox[i+2]--;
				i--;
			}
		}
	}

	public void dui(int[] cpBox, int[] num) {
		//如果存在单独的一张牌,则不可能胡牌
		for(int i=1; i<cpBox.length-1; i++) {
			if(cpBox[i] == 1) {
				num[4] = -1;
				break;
			}
			if(cpBox[i] == 2) {
				num[3]++;
			}
		}
	}

	public Card[] getCard() {
		return card;
	}

	public void setCard(Card[] card) {
		this.card = card;
	}

}
