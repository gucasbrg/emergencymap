package util;
/**���Ծ�γ�Ƚ��л��֣����Ƚ����趨*/
public class point {
	public point(double x2, double y2, byte value)
	{
		this.x = x2;
		this.y = y2;
		this.value = value;
	}
	/**x����ˮƽλ�ã�y��������λ��*/
	public double x;
	public double y;
	/**value�������*/
	public byte value;
	
	
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
	public byte getValue() {
		return value;
	}
	public void setValue(byte value) {
		this.value = value;
	}
	
}