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
	//һ���������ڼ���S���ҽ�����Դ���ö�������·��������֪
      neighbor= new Set[map_data.nodes.length];//ÿһ�������������ڵ���ɸõ��һ�����ڼ���
      connected = new TreeSet<Integer>();//�൱��V-S
      solved = new TreeSet<Integer>();//�൱��S�����������·�Ľ⼯��
    double[] distance = new double[map_data.nodes.length];//��ʾ��Դ���������·������
     int[] prev = new int[map_data.nodes.length];
    //�Ե���б���
    for (int i = 0; i < map_data.nodes.length; i++){
      prev[i] = nodeId1;
      distance[i] = Double.POSITIVE_INFINITY;
      neighbor[i] = new TreeSet<Integer>();
    }
    for (int i = 0; i < map_data.edges.length; i++){
      int ida = map_data.edges[i].nodeId1;
      int idb = map_data.edges[i].nodeId2;
      neighbor[ida].add(new Integer(idb));//��˫����ͨͼ
      neighbor[idb].add(new Integer(ida));//��˫����ͨͼ
      if (ida == nodeId1 && idb != nodeId1){//���ʼ��nodeId1���ڵĵ���о������
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
      if(sel==null){         //��������Ϊ���һ������κε㶼û�����������׳��쳣
    	  return null;      //
      }                     //
      else{
      solved.add(sel);//���ӵ�������·
      connected.remove(sel);//����̵��Ǹ�ȥ����Ϊ�˷�ֹ������·
      
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

