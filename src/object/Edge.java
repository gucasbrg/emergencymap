package object;

/** 边 */
public class Edge {
	/** ID#号 */
	public  int id;
	/** 道路级别（1-6） */
	public  int type;
	/** 两端顶点ID */
	public  int nodeId1, nodeId2;

	public Edge(int id, int type, int nodeId1, int nodeId2) {
		this.id = id;
		this.type = type;
		this.nodeId1 = nodeId1;
		this.nodeId2 = nodeId2;
	}

	public int getId() {
		return id;
	}

	public int getType() {
		return type;
	}

	public int getNodeId1() {
		return nodeId1;
	}

	public int getNodeId2() {
		return nodeId2;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setType(int type) {
		this.type = type;
	}

	public void setNodeId1(int nodeId1) {
		this.nodeId1 = nodeId1;
	}

	public void setNodeId2(int nodeId2) {
		this.nodeId2 = nodeId2;
	}
	
	
}