package object;

/** 顶点 */
public class Node {
	/** ID# */
	public int id;
	/** 水平位置、垂直位置 */
	public double x, y;
	/**是否是中央储备库，是为1、不是为0.因为Node点比Mark多，标记是在算最短路时用到*/
	public int centerStore;
	//是不是中央储备库各属于那个城市不一样； 很多点可能属于某个城市，但是只有一个点属于中央储备库
	/**因为要用很多的Node刻画路的情况，有的Node有Mark道路的信息，有的Node有道路的信息，
	 * 该点在那个城市，所以找路径的时候，就可以标出经过那些城市*/
	public int belongMark;
	
	/**是否为省会*/
	public int provincialCapital;
	
	/**省份*/
	public int province;

	public Node(int id, double x, double y, int centerStore, int belongMark,
			int provincialCapital, int province) {
		super();
		this.id = id;
		this.x = x;
		this.y = y;
		this.centerStore = centerStore;
		this.belongMark = belongMark;
		this.provincialCapital = provincialCapital;
		this.province = province;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public int getCenterStore() {
		return centerStore;
	}

	public void setCenterStore(int centerStore) {
		this.centerStore = centerStore;
	}

	public int getBelongMark() {
		return belongMark;
	}

	public void setBelongMark(int belongMark) {
		this.belongMark = belongMark;
	}

	public int getProvincialCapital() {
		return provincialCapital;
	}

	public void setProvincialCapital(int provincialCapital) {
		this.provincialCapital = provincialCapital;
	}

	public int getProvince() {
		return province;
	}

	public void setProvince(int province) {
		this.province = province;
	} 




	
}

