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
		int mark_id_max = 0, node_id_max = 0, edge_id_max = 0; //��Ŷ��Ǵ�0��ʼ
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
			//��������
			double Longitude = row.getCell(1).getNumericCellValue();
			//γ������
			double Latitude = row.getCell(2).getNumericCellValue();
			String name = row.getCell(3).getStringCellValue();
			//NTU��������
			double LonNtu=MapData.getNtu(String.valueOf(Longitude)); 
			//NTUγ������
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
			//��������
			double Longitude = row.getCell(1).getNumericCellValue();
			//γ������
			double Latitude = row.getCell(2).getNumericCellValue();
			//NTU��������
			double LonNtu=MapData.getNtu(String.valueOf(Longitude)); 
			//NTUγ������
			double LatNtu=MapData.getNtu(String.valueOf(Latitude)); 
			int centerStore = (int) row.getCell(3).getNumericCellValue();
			//�����Ǹ���
			int belongMark=(int) row.getCell(4).getNumericCellValue();
			//�ǲ���Ϊʡ��
			int provincialCapital=(int) row.getCell(5).getNumericCellValue();
			//�����Ǹ�ʡ
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
       //markv��nodevͨ������ѭ������ͨ��id��������ԭ��������ǰ������ţ����ڿ��Էŵ�����
		//�γɵ�markv�����Mark�ı��˳��һ��
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
		
		
		System.out.println("ȫ����Ϣ: " + markv.length + " ����, " + nodev.length + " ����, " + edgev.length + " ��.");

		return new MapData(markv, nodev, edgev);
	}

	public static double atof(String s)
	{
		return Double.valueOf(s).doubleValue();
	}

     /**��γ�Ƚ���NTU��γ�ȵı任*/
	public static double getNtu(String price) {
           double NTU;
		if (price.indexOf(".") != -1) {
			NTU=MapData.atof(price)*100000;
		} else {
			// ��С����Ĳ��ֽ����ݽ�ȡΪ�������ֺ�С�����������ֱַ�������������
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
