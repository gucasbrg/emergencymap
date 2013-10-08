package object;

/**��ͼ�ϵ���Ϣ���������� */
public class Mark {
	/**ID#��*/
	public int id; 
	/**ˮƽλ�� ����ֱλ��*/
	public double x, y; 
	/**��������*/
	public String name;
    /**�Ƿ������봢���⣬��Ϊ1������Ϊ0.�ڻ�drawMarks����ʱ�ܹ��õ�*/
	public int centerStore;
	/**�õ������˿���*/
	public double peopleNumber;
	/**�õ�����GDP*/
	public double gdp ;
	/**���*/
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
