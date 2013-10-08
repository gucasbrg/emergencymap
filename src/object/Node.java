package object;

/** ���� */
public class Node {
	/** ID# */
	public int id;
	/** ˮƽλ�á���ֱλ�� */
	public double x, y;
	/**�Ƿ������봢���⣬��Ϊ1������Ϊ0.��ΪNode���Mark�࣬������������·ʱ�õ�*/
	public int centerStore;
	//�ǲ������봢����������Ǹ����в�һ���� �ܶ���������ĳ�����У�����ֻ��һ�����������봢����
	/**��ΪҪ�úܶ��Node�̻�·��������е�Node��Mark��·����Ϣ���е�Node�е�·����Ϣ��
	 * �õ����Ǹ����У�������·����ʱ�򣬾Ϳ��Ա��������Щ����*/
	public int belongMark;
	
	/**�Ƿ�Ϊʡ��*/
	public int provincialCapital;
	
	/**ʡ��*/
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

