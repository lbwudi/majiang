package others.abstractLevel;

import others.implAbstractLevel.Card;
import others.implAbstractLevel.Player;

public interface PlayerInterface {
	/*
	 * 增加一张新牌
	 */
	public int addCard(Card[] p, Card card);

	/*
	 * 出一张牌，重新排序
	 */
	public Card putCard();

	/*
	 * 从自己的牌堆里选择一张
	 */
	public int selectOneFromUsefulLength();

	/*
	 * 玩家触发碰事件
	 */
	public boolean isPeng();

	/*
	 * 玩家触发杠事件
	 */
	public boolean isGang();

	/*
	 * 玩家触发胡事件
	 */
	public boolean isSucceed();

	public boolean pPeng(Player player, Card card);

	public boolean pGang(Player player, Card card);

	public boolean pHu(Player player, Card card);

	public Card[] getpCards();

	public void setpCards(int[] card);

	public int getPosition();

	public void setPosition(int i);

	public String getName();
}
