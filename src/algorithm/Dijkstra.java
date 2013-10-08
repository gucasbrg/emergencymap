package algorithm;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;


import map.MapData;


/**Dijkstra Algorithm*/
public class Dijkstra extends ShortestPathSolver {
	 Set neighbor[];
	 Set<Integer> connected;
	 Set<Integer> solved;
	public Dijkstra(MapData map_data) {
    super(map_data);
  }

  

  public int[] getShortestPath(int nodeId1, int nodeId2){
	//一个顶点属于集合S当且仅当从源到该顶点的最短路径长度已知
      neighbor= new Set[map_data.nodes.length];//每一个点与其它相邻点组成该点的一个相邻集合
      connected = new TreeSet<Integer>();//相当于V-S
      solved = new TreeSet<Integer>();//相当于S所包括的最短路的解集合
    double[] distance = new double[map_data.nodes.length];//表示从源到顶点最短路径长度
     int[] prev = new int[map_data.nodes.length];
    //对点进行遍历
    for (int i = 0; i < map_data.nodes.length; i++){
      prev[i] = nodeId1;
      distance[i] = Double.POSITIVE_INFINITY;
      neighbor[i] = new TreeSet<Integer>();
    }
    for (int i = 0; i < map_data.edges.length; i++){
      int ida = map_data.edges[i].nodeId1;
      int idb = map_data.edges[i].nodeId2;
      neighbor[ida].add(new Integer(idb));//建双向连通图
      neighbor[idb].add(new Integer(ida));//建双向连通图
      if (ida == nodeId1 && idb != nodeId1){//与初始点nodeId1相邻的点进行距离计算
        connected.add(new Integer(idb));
        distance[idb] = getLength(ida, idb);
      }
      else if (idb == nodeId1 && ida != nodeId1){
        connected.add(new Integer(ida));
        distance[ida] = getLength(ida, idb);
      }
    }
    solved.add(new Integer(nodeId1));
    distance[nodeId1] = 0;
    
    for (Integer nodeId2_obj = new Integer(nodeId2); !solved.contains(nodeId2_obj); ){
      Integer sel = null;
      
      for (Iterator<Integer> i = connected.iterator(); i.hasNext(); ){
        Integer val = (Integer)i.next();
        if (sel == null || distance[val.intValue()] < distance[sel.intValue()]){
          sel = val;
        }
      }
      if(sel==null){         //加这三句为如果一个点和任何点都没有相连，会抛出异常
    	  return null;      //
      }                     //
      else{
      solved.add(sel);//增加点进入最短路
      connected.remove(sel);//把最短的那个去掉，为了防止产生回路
      
      // renew distance
      for (Iterator<Integer> i = neighbor[sel.intValue()].iterator(); i.hasNext(); ){
        Integer val = (Integer)i.next();
        if (!solved.contains(val)){
          connected.add(val);
          double dtmp = distance[sel.intValue()] + getLength(sel.intValue(), val.intValue());
          if (dtmp < distance[val.intValue()]){
            distance[val.intValue()] = dtmp;
            prev[val.intValue()] = sel.intValue();
          }
        }
      }
      }
    }
    
    int size = 1;
    for (int i = nodeId2; i != nodeId1; i = prev[i]){
      size++;
    }
    
    int[] answer = new int[size];
    for (int i = nodeId2; i != nodeId1; i = prev[i]){
      size--;
      answer[size] = i;
    }
    answer[0] = nodeId1;
    
    return answer;
  }
  
  
	
}

