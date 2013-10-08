package algorithm;
import java.util.Vector;

import object.Node;

import map.MapData;

public abstract class ShortestPathSolver {
	public static MapData map_data; 

	protected ShortestPathSolver(MapData map_data) {
		this.map_data = map_data;
	}
	 /** �����������ڶ˵�ID�õ����������ڶ˵�ľ���*/
	  public static  double getLength(int n1, int n2)
	  {
	    Node nd1 = map_data.nodes[n1];
	    Node nd2 = map_data.nodes[n2];
	    double xd = nd1.x - nd2.x, yd = nd1.y - nd2.y;
	    return Math.sqrt(xd*xd + yd*yd);
	  }
	/**���������˵�ID�õ������������˵����·������ID���
	 * @param nodeId1 ��ʼNode��ID
	 * @param nodeId2 δ��Node��ID*/
	abstract public int[] getShortestPath(int nodeId1, int nodeId2);
	
	/**ȥ������belongMark��ͬ�ı��
	 * ��1,1,1,2,2,2,2,2,7,7,1,5,5,5,0 ת���1,2,7,1,5,0
	 * ���������ִ����⣬��ΪNode��Markһ���� */
	public int[] deleteMarkRepeatId(int[] path) {
		Vector<Integer> allNodeId = new Vector<Integer>();
		for (int i = 0; i < path.length; i++) {
			allNodeId.add(path[i]);
		}
		Vector<Integer> newNums = new Vector<Integer>();
		int temp = allNodeId.get(0);
		newNums.add(temp);
		for (int i = 1; i < allNodeId.size(); i++) {
			if (allNodeId.get(i) == temp) {
				continue;
			} else {
				newNums.add(allNodeId.get(i));
				temp = allNodeId.get(i);
			}
		}
		int size = newNums.size();
		Integer[] b = newNums.toArray(new Integer[size]);
		int[] MarkId = new int[size];
		for (int i = 0; i < size; i++) {
			MarkId[i] = b[i].intValue();

		}
		return MarkId;

	}

	/**�õ����·�ĳ���,��λΪ��*/
	 public int  getShortestPathLength(Node[] nodes,int path[]){
		 int length=0;
		for (int i = 1; i < path.length; ++i) {
			Node n1 = nodes[path[i-1]], n2 = nodes[path[i]];
			length+=getLength(n1.id, n2.id);
		}
		return length;
	 }
	 /**�õ����·ʱ��*/
	 public double  getShortestTime(int distance,int rate){
	 	double length=distance/rate;
	 	return length;

	 }
	 
}


