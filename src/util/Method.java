package util;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;


import algorithm.Dijkstra;
import algorithm.ShortestPathSolver;

import map.MapData;
import object.Mark;
import object.Node;

public class Method {
	
	private static ShortestPathSolver solver; 
	private static int[] path; 
	
//	/**根据离每个地级市最近的三个中央储备库为三个指标；离地级市最近的系数
//	 * 赋值1，第二近的系数赋值为0.75，第三近的系数赋值为0.5，计算距离时，
//	 * 两个地级地距离在前三项基础上再加上是不是同一个行政省市*/
//	public static void cluster(MapData map_data) {
//		/**************读取所有数据的向量*******************/
//		// Dijkstra
//		solver = new Dijkstra(map_data);
//		Mark[] marks = map_data.marks;
//		Node[] nodes = map_data.nodes;
//		
//		List<SamplePoint> points = new ArrayList<SamplePoint>();
//		// 读取数据文件，初始化样本点
//		int linecount = 0;
//		String cityName = "";
//
//		for (int i = 0; i < nodes.length; i++) {
//			int centerId1=Integer.MAX_VALUE;
//			int centerId2=Integer.MAX_VALUE;
//			int centerId3=Integer.MAX_VALUE;
//			NodeLength n = null;
//			Vector<NodeLength> v=new Vector<NodeLength>();
//
//			
//			for (int j = 0; j < nodes.length; j++) {
//				if (nodes[j].centerStore == 1) {
//					/***************** 在图上表示出 *************************/
//					path = solver.getShortestPath(nodes[i].id, nodes[j].id);
//					if (path == null) // 这一句为一个城市和任何城市都没有连接的情况
//					{
//						cityName = marks[nodes[i].belongMark].name;
//						String centerStoreName = marks[nodes[j].belongMark].name;
//						System.out.print("中央储备库：" + centerStoreName + "与"
//								+ cityName + "没有连接\n");
//					} else {
//						n=new NodeLength();
//						n.length=solver.getShortestPathLength(nodes, path);
//						n.id=j;
//                        v.add(n);
//					}
//				}
//			}
//			if (v.size() != 0) {
//				//找到前面的最小的前三个长度的元素
//				for (int k = 0; k < 3; k++) {
//					for (int l = k + 1; l < v.size(); l++) {
//						if (v.get(k).length > v.get(l).length) {
//							n = v.get(k);
//							v.set(k, v.get(l));
//							v.set(l, n);
//						}
//					}
//				}
//				//此为nodes的序号[]，与其中的ID号相同，在MapData就进行排序;
////				for (int j = 0; j < nodes.length; j++) {
////					if (nodes[j].id != j){
////						System.out.print("wrong");
////					}
////				}
//				centerId1 = v.get(0).id;  
//				centerId2 = v.get(1).id;
//				centerId3 = v.get(2).id;
//			}
//			    //计算总的维数 //省份那一个不算维数，但是算距离的时候加进去
//                int k=0;
// 				for (int j = 0; j < nodes.length; j++) {
//					if (nodes[j].centerStore == 1) {
//						   k++;						
//					}			
//				}
// 				float[] values = new float[k];
// 				int l=0;
//				for (int j = 0; j < nodes.length; j++) {
//					if (nodes[j].centerStore == 1) {
//						   
//						if(j==centerId1){
//							values[l++]=1;
//						}else if (j==centerId2){
//							values[l++]=0.75f;
//						}else if (j==centerId3){
//							values[l++]=0.5f;
//						}else{
//							values[l++]=0;
//						}
//						
//					}			
//				}
//				SamplePoint point = new SamplePoint((++linecount), values);
//				points.add(point);
//				System.out.println(point);
//	        }
//		/*******************************************************8*/
//		// 设置初始化聚类
//		Group[] groups = new Group[2];
//		int idx = 0;
//		groups[idx] = new Group(idx);
//		groups[idx].setMeanPoint(points.get(0));
//		idx++;
//
//		groups[idx] = new Group(idx);
//		groups[idx].setMeanPoint(points.get(74));
//		idx++;
//
//		int k = 8;
//		int minNumThres = 3;
//		float std_deviationThres = 1;
//		float minDistanceThres = 0.5f;
//		int maxMergeNumsThres = 1;
//		int maxIters = Integer.MAX_VALUE;
//
//		// 开始聚类
//		ISODATA isodata = new ISODATA(groups, points, k, minNumThres, std_deviationThres,
//				minDistanceThres, maxMergeNumsThres, maxIters);
//		isodata.run();
//		}
//	
	
	/**打印到Excel分类的分量*/
	public static void cluster(MapData map_data) {
		// Dijkstra
		solver = new Dijkstra(map_data);
		Mark[] marks = map_data.marks;
		Node[] nodes = map_data.nodes;
		Vector<Vector> setDistance = new Vector<Vector>();
		Vector<String> distance = new Vector<String>();
		String cityName = "";
		distance.addElement(cityName);
		for (int i = 0; i < nodes.length; i++) {
			if (nodes[i].centerStore == 1) {
				distance.addElement(marks[nodes[i].belongMark].name);

			}
		}
		setDistance.addElement(distance);

		for (int i = 0; i < nodes.length; i++) {
			int centerId1=Integer.MAX_VALUE;
			int centerId2=Integer.MAX_VALUE;
			int centerId3=Integer.MAX_VALUE;
			NodeLength n = null;
			Vector<NodeLength> v=new Vector<NodeLength>();
			
			distance = new Vector<String>();
			distance.addElement(marks[nodes[i].belongMark].name);
			
			for (int j = 0; j < nodes.length; j++) {
				if (nodes[j].centerStore == 1) {
					/***************** 在图上表示出 *************************/
					path = solver.getShortestPath(nodes[i].id, nodes[j].id);
					if (path == null) // 这一句为一个城市和任何城市都没有连接的情况
					{
						cityName = marks[nodes[i].belongMark].name;
						String centerStoreName = marks[nodes[j].belongMark].name;
						System.out.print("中央储备库：" + centerStoreName + "与"
								+ cityName + "没有连接\n");
					} else {
						n=new NodeLength();
						n.length=solver.getShortestPathLength(nodes, path);
						n.id=j;
                        v.add(n);
					}
				}
			}
			if (v.size() != 0) {
				//找到前面的最小的前三个长度的元素
				for (int k = 0; k < 3; k++) {
					for (int l = k + 1; l < v.size(); l++) {
						if (v.get(k).length > v.get(l).length) {
							n = v.get(k);
							v.set(k, v.get(l));
							v.set(l, n);
						}
					}
				}
				//此为nodes的序号[]，与其中的ID号相同，在MapData就进行排序;
//				for (int j = 0; j < nodes.length; j++) {
//					if (nodes[j].id != j){
//						System.out.print("wrong");
//					}
//				}
				centerId1 = v.get(0).id;  
				centerId2 = v.get(1).id;
				centerId3 = v.get(2).id;
			}

				for (int j = 0; j < nodes.length; j++) {
					if (nodes[j].centerStore == 1) {
						   
						if(j==centerId1){
							distance.addElement(String.valueOf(1));
						}else if (j==centerId2){
							distance.addElement(String.valueOf(0.75));
						}else if (j==centerId3){
							distance.addElement(String.valueOf(0.5));
						}else{
							distance.addElement(String.valueOf(0));
						}
						
					}			
				}
				distance.addElement(String.valueOf(nodes[i].province));
				setDistance.addElement(distance);
	    }
		
		try {
				MapData.saveFile(setDistance, "除去城市之间距离以后的向量距离");

			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.print("cluster前期数据处理完毕");

		}
	/**字符串反转*/
	 public static String reverse(String s){
	        int pos=0;
	        StringBuilder sb=new StringBuilder();
	        for(int i=0;i<s.length();i++){
	            char c=s.charAt(i);
	            if(c==' '){
	             pos=0;
	            }
	            sb.insert(pos, c);
	            if(c!=' '){
	             pos++;
	            }
	        }
	        return sb.toString();
	    }
}
