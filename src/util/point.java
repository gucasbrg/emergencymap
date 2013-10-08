package util;
/**先以经纬度进行划分，，先进行设定*/
public class point {
	public point(double x2, double y2, byte value)
	{
		this.x = x2;
		this.y = y2;
		this.value = value;
	}
	/**x代表水平位置，y代表竖起位置*/
	public double x;
	public double y;
	/**value代表类别*/
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