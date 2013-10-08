package map;
import java.io.*;
import java.util.*;

import object.Edge;
import object.Mark;
import object.Node;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;


public class MapData {
	public final Mark[] marks;
	public final Node[] nodes;
	public final Edge[] edges;

	public MapData(Mark[] marks, Node[] nodes, Edge[] edges) {
		this.marks = marks;
		this.nodes = nodes;
		this.edges = edges;
	}

	public static MapData loadFile(String filename) throws Exception
	{
		int mark_id_max = 0, node_id_max = 0, edge_id_max = 0; //序号都是从0开始
		POIFSFileSystem file =new POIFSFileSystem(new FileInputStream(filename)); 
		HSSFWorkbook workbook = new HSSFWorkbook(file);

		HSSFSheet mark=workbook.getSheet("mark");
		HSSFSheet node=workbook.getSheet("node");		
	    HSSFSheet edge=workbook.getSheet("edge");

		Vector<Mark> marks = new Vector<Mark>();
		Vector<Node> nodes = new Vector<Node>();
		Vector<Edge> edges = new Vector<Edge>();
		Iterator markRows;
		Iterator nodeRows;
		Iterator edgeRows;
		HSSFRow row = null;
		markRows = mark.rowIterator();
		while (markRows.hasNext()) {
			row = (HSSFRow) markRows.next();
			int id = (int) row.getCell(0).getNumericCellValue();
			//经度坐标
			double Longitude = row.getCell(1).getNumericCellValue();
			//纬度坐标
			double Latitude = row.getCell(2).getNumericCellValue();
			String name = row.getCell(3).getStringCellValue();
			//NTU经度坐标
			double LonNtu=MapData.getNtu(String.valueOf(Longitude)); 
			//NTU纬度坐标
			double LatNtu=MapData.getNtu(String.valueOf(Latitude)); 
			//System.out.println(LonNtu+"      "+LatNtu);
			int centerStore = (int) row.getCell(4).getNumericCellValue();
			
			double peopleNumber=row.getCell(5).getNumericCellValue();
			
			double gdp=row.getCell(6).getNumericCellValue();
			
			byte category=(byte) row.getCell(7).getNumericCellValue();

			marks.addElement(new Mark(id, LonNtu, LatNtu, name,centerStore,peopleNumber,gdp,category));
			mark_id_max = Math.max(id, mark_id_max);
		}
		nodeRows = node.rowIterator();
		while (nodeRows.hasNext()) {
			row = (HSSFRow) nodeRows.next();
			int id = (int) row.getCell(0).getNumericCellValue();
			//经度坐标
			double Longitude = row.getCell(1).getNumericCellValue();
			//纬度坐标
			double Latitude = row.getCell(2).getNumericCellValue();
			//NTU经度坐标
			double LonNtu=MapData.getNtu(String.valueOf(Longitude)); 
			//NTU纬度坐标
			double LatNtu=MapData.getNtu(String.valueOf(Latitude)); 
			int centerStore = (int) row.getCell(3).getNumericCellValue();
			//属于那个市
			int belongMark=(int) row.getCell(4).getNumericCellValue();
			//是不是为省会
			int provincialCapital=(int) row.getCell(5).getNumericCellValue();
			//属于那个省
			int province=(int)row.getCell(6).getNumericCellValue();
			nodes.addElement(new Node(id, LonNtu, LatNtu,centerStore,belongMark,provincialCapital,province));
			node_id_max = Math.max(id, mark_id_max);

		}
		edgeRows = edge.rowIterator();
		while (edgeRows.hasNext()) {
			row = (HSSFRow) edgeRows.next();
			int id = (int) row.getCell(0).getNumericCellValue();
			int type = (int) row.getCell(1).getNumericCellValue();
			int node1 = (int) row.getCell(2).getNumericCellValue();
			int node2 = (int) row.getCell(3).getNumericCellValue();

			edges.addElement(new Edge(id, type, node1, node2));
			edge_id_max = Math.max(id, edge_id_max);
		}
		

		Mark[] markv = new Mark[mark_id_max + 1];
		Node[] nodev = new Node[node_id_max + 1];
		Edge[] edgev = new Edge[edge_id_max + 1];
       //markv和nodev通过下面循环可以通过id进行排序，原来可能在前的最大标号，现在可以放到后面
		//形成的markv数组和Mark的标号顺序一致
		for (int i = 0; i < markv.length; ++i) {
			Mark m = (Mark)marks.elementAt(i);
			markv[m.id] = m;
		}

		for (int i = 0; i < nodev.length; ++i) {
			Node n = (Node)nodes.elementAt(i);
			nodev[n.id] = n;
		}

		for (int i = 0; i < edgev.length; ++i) {
			Edge e = (Edge)edges.elementAt(i);
			edgev[e.id] = e;
		}
		
		
		System.out.println("全部信息: " + markv.length + " 地名, " + nodev.length + " 顶点, " + edgev.length + " 边.");

		return new MapData(markv, nodev, edgev);
	}

	public static double atof(String s)
	{
		return Double.valueOf(s).doubleValue();
	}

     /**经纬度进行NTU经纬度的变换*/
	public static double getNtu(String price) {
           double NTU;
		if (price.indexOf(".") != -1) {
			NTU=MapData.atof(price)*100000;
		} else {
			// 以小数点的部分将数据截取为整数部分和小数部分两部分分别处理两部分数据
			String[] str = price.split("//.");
			NTU=(MapData.atof(str[0])+MapData.atof(str[1])/60)*100000;

		}
		return NTU;

	}

	public static void saveFile(Vector<Vector> set, String string) throws Exception {
		HSSFWorkbook workbook = new HSSFWorkbook();
		FileOutputStream out = new FileOutputStream("outData"+File.separator+string+".xls");	
		HSSFSheet mark=workbook.createSheet(string);
		HSSFRow row = mark.createRow(set.size()); 
		for(int i=0;i<set.size();i++)
		{ row = mark.createRow(i);
		  for(int j=0;j<set.elementAt(i).size();j++)
		  {
			row.createCell(j).setCellValue((String)set.elementAt(i).elementAt(j));
		}
		}

		workbook.write(out);
		out.flush();
		out.close();
	}
}
