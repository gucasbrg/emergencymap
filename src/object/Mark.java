package object;

/**地图上的信息（地名）等 */
public class Mark {
	/**ID#号*/
	public int id; 
	/**水平位置 ，垂直位置*/
	public double x, y; 
	/**地名名称*/
	public String name;
    /**是否是中央储备库，是为1、不是为0.在画drawMarks（）时能够用到*/
	public int centerStore;
	/**该地区的人口数*/
	public double peopleNumber;
	/**该地区的GDP*/
	public double gdp ;
	/**类别*/
	public byte category; 

	public Mark(int id, double x, double y, String name,int centerStore,double peopleNumber,double gdp,byte category) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.name = name;
		this.centerStore=centerStore;
		this.peopleNumber=peopleNumber;
		this.gdp=gdp;
		this.category=category;
	}

	public int getId() {
		return id;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public String getName() {
		return name;
	}

	public int getCenterStore() {
		return centerStore;
	}

	public double getPeopleNumber() {
		return peopleNumber;
	}

	public double getGdp() {
		return gdp;
	}

	public byte getCategory() {
		return category;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setX(double x) {
		this.x = x;
	}

	public void setY(double y) {
		this.y = y;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setCenterStore(int centerStore) {
		this.centerStore = centerStore;
	}

	public void setPeopleNumber(double peopleNumber) {
		this.peopleNumber = peopleNumber;
	}

	public void setGdp(double gdp) {
		this.gdp = gdp;
	}

	public void setCategory(byte category) {
		this.category = category;
	}
	
}
