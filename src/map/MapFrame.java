package map;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Label;
import java.awt.Point;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.ibm.perf.parameter.Data;
import com.ibm.perf.parameter.Model;
import com.ibm.perf.parameter.Parameter;
import com.ibm.perf.svm.Predict;

import edu.asu.emit.qyan.alg.control.YenTopKShortestPathsAlg;
import edu.asu.emit.qyan.alg.model.Graph;
import edu.asu.emit.qyan.alg.model.Path;
import edu.asu.emit.qyan.alg.model.VariableGraph;

import object.Edge;
import object.Mark;
import object.Node;

import algorithm.Dijkstra;
import algorithm.ShortestPathSolver;

import util.NodeLength;
import util.ReadParam;
import util.point;


public class MapFrame extends JFrame implements MouseListener,MouseMotionListener, MouseWheelListener{
	/**ʱ�䣺Ĭ��Ϊ8Сʱ*/
	static final String Time_PARAM="-d 0 -h 8 -m 0 -s 0";
	/**�����𼶣�Ĭ��Ϊ6��*/
    static final String Mag_PARAM = "-M 6";
    /**��·�ж��������MarkΪ��ǣ�Nodeû�����Ʋ��ñ�ʾ */
    static final String From = "�ж���ʼ��";
    static final String To = "�ж��յ�";
    /**ʱ��:Ĭ��Ϊ60km/h*/
    static final String Rate_PARAM = "-r 60";
	/**����ɫ*/
	private final Color background = Color.white;
	/**һ��������ֵ���ɫ*/
	private final Color foreground = Color.blue; 
	/**����������ֵ���ɫ*/
	private final Color foreCenterStoreground = Color.red; 
	/**һ����������α��ɫƷ��ɫ */
	private final Color mark_color = Color.magenta; 
	/**���봢���������α��ɫ��ɫ */
	private final Color markCenterStore_color = Color.yellow; 
	
	/**������Ӧ���·����ɫ*/
	private final Color path_color = Color.red; 
	/**����Edge�ĵ�·���������ɫ������١�һ����·��������·*/
	private final Color[] edge_colors = {
    new Color(0x000000), new Color(0x404040), new Color(0x808080), new Color(0xA0A0A0), new Color(0xC0C0C0), new Color(0xC0C0C0)
	};

    /**������ı��ĵ�ͼ���������ݱ���*/
	private MapData map_data; 
	/**��ͼ�������*/
	private double map_left; 
	/**��ͼ�����*/
	private double  map_top; 
    /**ͼƬ��ʾ�Ļ���ĸ߶ȳ���,������϶���ȫ��Ļ��ͼ����������������Ļ�ֱ���1366*768����*/
	private int map_image_height = 1500;
	/**ͼƬ��ʾ�Ļ���Ŀ�ȱ���*/
	private int map_image_width; 
    /**ͼƬ����*/
	private Image map_image; 
	/**�����߶�*/
	private double map_image_scale; 	
	/**���ʵ�ʵĿ��*/
	double map_width;
	/**���ʵ�ʵĸ߶�*/
	double map_height;
	
   /**���ڵ����Ͻ���ͼƬ�ϵ�x����*/
	private int screen_left; 
	/**���ڵ����Ͻ���ͼƬ�ϵ�y����*/
	private int screen_top; 
    /**���·*/
	private ShortestPathSolver solver; 
	/**��MapData�õ���ͼ������k-top���·*/
	private static Graph graph;
	
	private int node_id1 = -1, node_id2 = -1; 
	/**����ʼ�㵽�յ��������ĵ��Id.path[0] = node_id1����path[n] = node_id2*/
	private int[] path; //
	/**ȥ��֮��ĳ������*/
	private int[] markId;
	private JLabel l;
	private ImageIcon ic;
		
	double min_x = Double.MAX_VALUE, max_x = -Double.MAX_VALUE; 
	double min_y = Double.MAX_VALUE, max_y = -Double.MAX_VALUE;
	
	/**֧����������ѡ�õĲ���*/
	private Parameter param;
	
	/**������е�point����Toy������*/
	Vector<point> point_list = new Vector<point>();	
	
	final static Color colors[] =
	{ /**��ɫ*/
	  new Color(0,0,0),
	  new Color(0,120,120),
	  new Color(120,120,0),
	  new Color(120,0,120),
	  new Color(0,200,200),
	  new Color(200,200,0),
	  new Color(200,0,200),
	  new Color(0,250,250),
	  new Color(250,250,0),
	  new Color(250,0,250)	  
	};
	
	public MapFrame(String string) {
      super(string);
	}

	/**��ʼ������init()*/
	public void init() {
	   try {
		map_data = MapData.loadFile("data"+File.separator+"data.xls");
	} catch (Exception e) {
		e.printStackTrace();
	}

		Container con=getContentPane();
		BorderLayout layout = new BorderLayout();
		con.setLayout(layout);
		l=new JLabel();
		/** ������ͼͼ��*/
		createMapImage();
	    ic=new ImageIcon(map_image);
		l.setIcon(ic);
		con.add(l,BorderLayout.CENTER);
		//con.add(new JScrollPane(l), BorderLayout.CENTER);
		l.setOpaque(true); // JLabelĬ����͸����,����ֱ��setBackground()���������䱳����ɫ,��setForceground����
		JPanel p = new JPanel();
		GridBagLayout gridbag = new GridBagLayout();
		p.setLayout(gridbag);
		
		/*********** ���Ӱ�ť���ı��� *************************/
		Button disVrp = new Button("���ֵ��Ԯ·��");
		Button storeCover = new Button("������ʱ�串��״��");
		Button cityBeCovered = new Button("�ؼ���ʱ�䱻����״��");
		Button disPredict = new Button("���ֵ�����Ԥ��");
		Button clear = new Button("���");
		Button pathInterrupt = new Button("��·�ɿ���ѡ��");
		final TextField time_line = new TextField(Time_PARAM);
		final TextField mag_line = new TextField(Mag_PARAM);
		final TextField from = new TextField(From);
		final TextField to = new TextField(To);
		final TextField rate_line = new TextField(Rate_PARAM);
		Label arrive = new Label("��");
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1;
		c.gridwidth = 1;
		gridbag.setConstraints(disVrp,c);
		gridbag.setConstraints(storeCover,c);
		gridbag.setConstraints(cityBeCovered,c);
		gridbag.setConstraints(disPredict,c);
		gridbag.setConstraints(clear,c);
		gridbag.setConstraints(pathInterrupt,c);
		gridbag.setConstraints(arrive,c);
		c.weightx = 5;
		c.gridwidth = 5;
		gridbag.setConstraints(time_line,c);
		gridbag.setConstraints(mag_line,c);
		gridbag.setConstraints(from,c);
		gridbag.setConstraints(to,c);
		gridbag.setConstraints(rate_line,c);
		
		p.add(disVrp);
		p.add(storeCover);
		p.add(cityBeCovered);
		p.add(disPredict);
		p.add(pathInterrupt);
		p.add(clear);
		p.add(from);
		p.add(arrive);
		p.add(to);
		p.add(time_line);
		p.add(rate_line);
		p.add(mag_line);
		con.add(p,BorderLayout.SOUTH);
		/******************�ļ�����**********************************/
		Menu menuFile = new Menu("�ļ�") ;
		MenuBar menuBar = new MenuBar() ;
		menuBar.add(menuFile) ;

		MenuItem newItem = new MenuItem("�½�") ;
		MenuItem openItem = new MenuItem("��") ;
		MenuItem closeItem = new MenuItem("�ر�") ;
		MenuItem exitItem = new MenuItem("�˳�") ;

		newItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
			
			}
		}) ;

		openItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				
			}
		}) ;

		closeItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
			
			}
		}) ;

		exitItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){				
			}
		}) ;
		menuFile.add(newItem) ;
		menuFile.add(openItem) ;
		menuFile.add(closeItem) ;
		menuFile.add(exitItem) ;
		
		/******************�༭����**********************************/
		Menu editorFile = new Menu("�༭") ;
		menuBar.add(editorFile) ;

		MenuItem disVrpM = new MenuItem("���ֵ��Ԯ·��") ;
		MenuItem storeCoverM = new MenuItem("������ʱ�串����") ;
		MenuItem cityBeCoveredM = new MenuItem("�ؼ���ʱ�䱻����״��") ;
		MenuItem disPredictM = new MenuItem("���ֵ�����Ԥ��") ;
		MenuItem province = new MenuItem("��ʡ��֮��ʱ�������") ;
	

		disVrpM.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				disVrp_clicked();
			}
		}) ;
		province.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				provinceTimeanDis(rate_line.getText());
			}
		}) ;
		
		editorFile.add(disVrpM) ;
		editorFile.add(storeCoverM) ;
		editorFile.add(cityBeCoveredM) ;
		editorFile.add(disPredictM) ;
		editorFile.add(province) ;
		
		/*****************��ͼ**********************************/
		Menu viewEditor = new Menu("��ͼ") ;
		menuBar.add(viewEditor) ;
		MenuItem saveImage = new MenuItem("���浱ǰͼƬ") ;
		MenuItem saveCoverImage = new MenuItem("�������봢���⸲�Ƿ�Χ") ;
		MenuItem saveCoverTwoImage = new MenuItem("����������봢���⸲�Ƿ�Χ");
		MenuItem cluster = new MenuItem("����") ;
		MenuItem partition = new MenuItem("����") ;
		MenuItem piex=new MenuItem("�ֱ���");
		
		saveImage.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				try {
					save(map_image);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}) ;
		
		saveCoverImage.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				saveCover(map_image);
			}
		}) ;
		saveCoverTwoImage.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				saveTwoCover(map_image);
			}
     	}) ;
		cluster.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				cluster(map_image);
			}
     	}) ;
		partition.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				try {
					partition(map_image);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
     	}) ;
		piex.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				piexImage();
			}
		}) ;
		viewEditor.add(saveImage);
		viewEditor.add(saveCoverImage);
		viewEditor.add(saveCoverTwoImage);
		viewEditor.add(cluster);	
		viewEditor.add(partition) ;
		viewEditor.add(piex);
		
		/******************����**********************************/
		Menu helpFile = new Menu("����") ;
		menuBar.add(helpFile) ;

		MenuItem helpDocItem = new MenuItem("�����ĵ�") ;
		MenuItem helpItem = new MenuItem("���� emergencyMap") ;

		helpDocItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				String s = null;
				try {
					s = new File("").getCanonicalPath()+"\\doc\\index.html";
				} catch (IOException e2) {
					e2.printStackTrace();
				}    

				try {
					Runtime.getRuntime().exec("explorer "+s);
				} catch (IOException e1) {
					e1.printStackTrace();
				}		       
			}
		}) ;

		helpItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				JOptionPane.showMessageDialog(null, "��ֹ��ҵӦ��,������Ҫ������ϵ��" +
						"buruguo09@mails.gucas.ac.cn", "���� emergencyMap", JOptionPane.PLAIN_MESSAGE);
			}
		}) ;

		helpFile.add(helpDocItem) ;
		helpFile.add(helpItem) ;
        setMenuBar(menuBar) ;	// �˵�����Ҫͨ���˷������ӵ�
        
	//	getContentPane().add(p,BorderLayout.SOUTH);
		disVrp.addActionListener(new ActionListener()
		{ public void actionPerformed (ActionEvent e)
		  { disVrp_clicked(); }});
		
		storeCover.addActionListener(new ActionListener()
		{ public void actionPerformed (ActionEvent e)
		  { StorCover_clicked(time_line.getText(),rate_line.getText()); }});

		cityBeCovered.addActionListener(new ActionListener()
		{ public void actionPerformed (ActionEvent e)
		  { cityBeCovered_clicked(time_line.getText(),rate_line.getText()); }});

		disPredict.addActionListener(new ActionListener()
		{ public void actionPerformed (ActionEvent e)
		  { disPredict_clicked(); }});

		pathInterrupt.addActionListener(new ActionListener()
		{ public void actionPerformed (ActionEvent e)
		  { pathInterrupt_clicked(); }});
		clear.addActionListener(new ActionListener()
		{ public void actionPerformed (ActionEvent e)
		  { clear_clicked(); }});
		storeCoverM.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
			StorCover_clicked(time_line.getText(),rate_line.getText());
			}
		}) ;

		cityBeCoveredM.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				cityBeCovered_clicked(time_line.getText(),rate_line.getText());
			}
		}) ;

		disPredictM.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){				
				disPredict_clicked();}
		}) ;
		
         //�����˳�����ʱ�����ڴ�����Ȼ���ڵ�����
		 addWindowListener(new WindowAdapter() {
		      public void windowClosing(WindowEvent e)
		      { exit();}});
		      
		this.enableEvents(AWTEvent.MOUSE_EVENT_MASK);
		/************************************/

		//��ʼ�������Ͻ����ͼ�������
		screen_left = (map_image_width - getSize().width) >> 1;
		screen_top = (map_image_height - getSize().height) >> 1;

		// Dijkstra
		solver = new Dijkstra(map_data);
          //Ҫ��import_from_file�����н���˫����ͨͼ
	    graph = new VariableGraph(map_data);
		    

		// test
//		System.out.println("Testing batch processing of top-k shortest paths!");
//		YenTopKShortestPathsAlg yenAlg = new YenTopKShortestPathsAlg(graph);
//		List<Path> shortest_paths_list = yenAlg.get_shortest_paths(graph.get_vertex(4), graph.get_vertex(5), 5);
//         int[] i=shortest_paths_list.get(3).getInt();
//		 System.out.println(shortest_paths_list.get(3));
//		 System.out.println(":"+shortest_paths_list);
//		 System.out.println(yenAlg.get_result_list().size());
			
			
		addMouseListener(this);      //���Ĵ���
		addMouseMotionListener(this); //�����ƶ�
		addMouseWheelListener(this);//���Ļ��ֲ���

	}

   public void paint(Graphics g) {
	   //super.paint(g) ; 
     //ͼ������Ͻ�λ�ڸ�ͼ������������ռ�� (x, y)
	g.drawImage(map_image, -screen_left, -screen_top, this);


	}
	public void update(Graphics g) {
		paint(g);
	}
	private Point prev_p; 
    /**��갴�����²��϶�ʱ����(ʹ��ͼ�����ƶ���*/
    public void mouseDragged(MouseEvent e) {  
    	
		final int left_max = map_image_width - getSize().width; 
		final int top_max = map_image_height - getSize().height; 
		screen_left -= e.getX() - prev_p.x;//e.getX() - prev_p.xΪ����Ϊ�����ƶ�����;e.getX() - prev_p.xΪ����Ϊ�����ƶ�����
		screen_left = Math.max(0, screen_left);
		//�����ƶ���ͼƬֻʣ���ڿ��ʱ���������ƶ�
		//���������Ͻ����ͼ���ˮƽ����Ҫ����0����screen_leftȷ��������󲻳���left_max
		screen_left = Math.min(left_max, screen_left);

		screen_top -= e.getY() - prev_p.y;
		screen_top = Math.max(0, screen_top);
		//�����ƶ���ͼƬֻʣ���ڸ߶�ʱ���������ƶ�
		screen_top = Math.min(top_max, screen_top);
	//	System.out.print("screen_left:"+screen_left+"   screen_top:"+screen_top+"\n");
		prev_p = e.getPoint();
		//����ƶ��Ļ���ֻʣ�����һ��·������ڶ���·��ʱ����ôpath��øĳɶ�ά���飬������������ˮ��ʱ��Ҳ�����õ�
		//redrawMapImage();
		repaint();
	}
      /**������ƶ����ް�������ʱ����*/
	public void mouseMoved(MouseEvent e) {
		prev_p = e.getPoint();
		//����С���ڵ�����
	}

	/**��������*/
	public void mouseClicked(MouseEvent e) {
		//�����һ����ʱnode_id1<0,������ָ�ֵ����һ���㣬�ٵ���ڶ�����ʱ����Ϊ��һ�����Ѿ�Ϊ����0�ģ����Ը�ֵ���ڶ����㣬
		//���ٵ��ʱ��node_id1��node_id2������0�����Զ���ֵΪ-1
		if (node_id1 < 0) {
			node_id1 = getNearestNode(e.getPoint());
			
			
		}
		else if (node_id2 < 0) {
			node_id2 = getNearestNode(e.getPoint());
			path = solver.getShortestPath(node_id1, node_id2);
		}
		else {
			//����Ӧ���·ʱ����������ͬʱ����
			node_id1 = node_id2 = -1;
			path = null;
		}

		redrawMapImage();
		repaint();
	}

	/**�ػ���ͼ����*/
	private void redrawMapImage() {
		Graphics g = map_image.getGraphics();
		g.setColor(background);
		g.fillRect(0, 0, map_image_width, map_image_height);//��ͼƬ�Ĵ�С��һ�����ο�����ɫ���
		drawEdges(g);
		drawPath(g);
		drawMarks(g);

		g.dispose();//���ٳ�����ָ����ͼ�ν�����Դ����������Դ������Ӱ��

	}

	/**�����ߵĺ���*/
	private void drawEdges(Graphics g) {
		Node[] nodes = map_data.nodes;
	    Edge[] edges = map_data.edges;

		for (int i = 0; i < edges.length; ++i) {
			Edge e = edges[i];
		    Node n1 = nodes[e.nodeId1], n2 = nodes[e.nodeId2];
			
			g.setColor(edge_colors[e.type - 1]);//���ݵ�·���������ɫ������
			g.drawLine(transformX(n1.x), transformY(n1.y), transformX(n2.x), transformY(n2.y));
		}
	}
	
	/** ������Ӧ�����·*/
	private void drawPath(Graphics g) {
		Node[] nodes = map_data.nodes;
        double length = 0;
		g.setColor(path_color);

		if (node_id1 >= 0) { 
			g.fillRect(transformX(nodes[node_id1].x) - 3, transformY(nodes[node_id1].y) - 3, 7, 7);
		}

		if (node_id2 >= 0) { 
			g.fillRect(transformX(nodes[node_id2].x) - 3, transformY(nodes[node_id2].y) - 3, 7, 7);
		}
		
		if (path != null) {
			for (int i = 1; i < path.length; ++i) {
				Node n1 = nodes[path[i-1]], n2 = nodes[path[i]];
				g.drawLine(transformX(n1.x), transformY(n1.y), transformX(n2.x), transformY(n2.y));
				length+=solver.getLength(n1.id, n2.id);
				
			}
		
		//	System.out.print(marks[nodes[node_id1].belongMark].name+"��"+ marks[nodes[node_id2].belongMark].name+"����Ϊ"+length/1000+"km");
			}
	}

	/**�������ı�ʶ*/
	private void drawMarks(Graphics g) {
		Mark[] marks = map_data.marks;

		for (int i = 0; i < marks.length; i++) {
			Mark m = marks[i];
			int x = transformX(m.x);
			int y = transformY(m.y);
			//Mark����centerStore����еĲ���
			if (m.centerStore == 1) {
				g.setColor(markCenterStore_color);// ���봢�������С���ε���ɫ
				g.drawRect(x - 3, y - 3, 7, 7);// x-1,y-1�ǻ��ƾ��ε����꣬3��3�ǻ��ƾ��εĿ�Ⱥ͸߶�
				g.setColor(foreCenterStoreground);// ������ǩ����ɫ
				g.drawString(m.name, x + 2, y - 2);// ��x+2��y-2�ĵط���ʾ��ǩ

			} else {
				g.setColor(mark_color);// ����С���ε���ɫ
				g.drawRect(x - 1, y - 1, 3, 3);// x-1,y-1�ǻ��ƾ��ε����꣬3��3�ǻ��ƾ��εĿ�Ⱥ͸߶�
				g.setColor(foreground);// ������ǩ����ɫ
				g.drawString(m.name, x + 2, y - 2);// ��x+2��y-2�ĵط���ʾ��ǩ
			}
		}
	}

	/**������ͼ�ĳ��������漰����Ե�ߵĶ���*/
	private void createMapImage() {
		Node[] nodes = map_data.nodes;

		for (int i = 0; i < nodes.length; ++i) {
			min_x = Math.min(min_x, nodes[i].x);
			max_x = Math.max(max_x, nodes[i].x);
			min_y = Math.min(min_y, nodes[i].y);
			max_y = Math.max(max_y, nodes[i].y);
		}
		//��Ϊ��MapData��������ʱ�Ѿ��Ѿ�γ��ת��ΪNTU��γ�ȣ�����Ϊ��ʵ�Ŀ�߶�
	   map_width = max_x - min_x;//���ʵ�ʵĿ��
	   map_height = max_y - min_y;//���ʵ�ʵĸ߶�
       //�������ı����Ϊ���������½�Ϊ��׼�����������趨
		map_left = min_x;//��ͼ�������		
		map_top = max_y;//��ͼ�����


		System.out.println("��ͼʵ�ʳߴ�: " + map_width/1000 + "km x " + map_height/1000 + "km");
		//��ʾ�ı����߶ȣ�ǰһ����������ʾ�Ĵ��ڵĸ߶ȣ���һ�������ǵ�ͼ�ĸ߶�
		map_image_scale = (double)map_image_height / map_height;  
		map_image_width = (int)(map_width * map_image_scale);  //�������ϵı�����ȷ����ͼʵ��ռ�õĿ�ȣ����Ƕ�̬��
		map_image_height+=150;//ͼƬ�Ŀ�Ⱥ͸߶�ͬʱ��150����ʹ���ұߺ��±ߵ���������ʾ
		map_image_width+=150;		
		map_image = createImage(map_image_width, map_image_height);
		redrawMapImage();    //�����˵�ͼ�������ܺ������ػ���ͼ�ı��������߱�Ե
		System.out.println("��ͼͼ��ʵ��ռ�óߴ�: " + map_image_width + " x " + map_image_height + "����");
	}
	

   //�����ĸ�����+50�ͼ�50�����þ��ǰ�����˺����϶˵Ķ�����ͼƬ����ʾ����
   /**����ʵ��xˮƽλ�ã������߶��������ͼ�ϵ�xˮƽλ��
    * @param x ʵ��xˮƽλ��*/
	private int transformX(double x) {
		return (int)((x - map_left) * map_image_scale)+50;
	}
	private int transformY(double y) {
		return (int)((map_top - y) * map_image_scale)+80;
	}
       /**���ݵ�ͼ�ϵ�ˮƽλ�ã�x+screen_left���������߶�������ʵ��xˮƽλ��
	    * @param x ��С�����ϵ�ˮƽλ��*/
	private double screen2mapX(int x) {
		return map_left + (x-50 + screen_left) / map_image_scale;
	}
	private double screen2mapY(int y) {
		return map_top - (y-80 + screen_top) / map_image_scale;
	}
    /**�õ���������������*/
	private int getNearestNode(Point p) {
		Node[] nodes = map_data.nodes;
		double x = screen2mapX(p.x), y = screen2mapY(p.y);
		double min_d_sq = Double.MAX_VALUE;
		int id = 0;
		
		for (int i = 0; i < nodes.length; ++i) {
			double dx = nodes[i].x - x;
			double dy = nodes[i].y - y;
			double d_sq = dx * dx + dy * dy;
			
			if (d_sq < min_d_sq) {
				min_d_sq = d_sq;
				id = i;
			}
		}	
//		System.out.print("��Ҫ��ӡ�ĵ�id"+id+"\n");
		return id;
	}
	private void clear(){
        node_id2=node_id1 = -1;
        path=null;
        redrawMapImage();
		repaint();
		
	}
	/**���ֵ��Ԯ·�� */
	void disVrp_clicked() {
		Graphics g = map_image.getGraphics();
		g.setColor(background);
		g.fillRect(0, 0, map_image_width, map_image_height);//��ͼƬ�Ĵ�С��һ�����ο�����ɫ���
		Mark[] marks =map_data.marks;
		Node[] nodes = map_data.nodes;
		
        if(node_id1<0){
        	 JOptionPane.showMessageDialog(null, "û��ѡ����У�����ѡ����У�", "��ʾ",
                     JOptionPane.INFORMATION_MESSAGE, null);
        }
        else if  (node_id2 < 0) {
			drawEdges(g);
			Vector<Vector> set=new Vector<Vector>();
			Vector<String> shortPathName=new Vector<String>();
			String length;
			String disName="���ֵ�";
			String centerStoreName="���봢����";
			String kil="���봢���⵽���ֵ�ľ���";
			String city="���봢���⵽���ֵ㾭���ĳ���";
			shortPathName.addElement(disName);
			shortPathName.addElement(centerStoreName);
			shortPathName.addElement(kil);
			shortPathName.addElement(city);
			set.addElement(shortPathName);

			for (int i = 0; i < nodes.length; i++) {
				if (nodes[i].centerStore == 1) {
					/*****************��ͼ�ϱ�ʾ��*************************/
					path = solver.getShortestPath(nodes[i].id,node_id1 );
					if(path==null)               //��һ��Ϊһ�����к��κγ��ж�û�����ӵ����
					{  disName=marks[nodes[node_id1].belongMark].name;
				       centerStoreName=marks[nodes[i].belongMark].name;
						System.out.print("���봢���⣺"+centerStoreName+"��"+disName+"û������\n");
					}else{
					g.setColor(path_color);
					g.fillRect(transformX(nodes[node_id1].x) - 3, transformY(nodes[node_id1].y) - 3, 7, 7);
					g.fillRect(transformX(nodes[i].x) - 3, transformY(nodes[i].y) - 3, 7, 7);
					
					if (path != null) {
						for (int j = 1; j < path.length; ++j) {
							Node n1 = nodes[path[j-1]], n2 = nodes[path[j]];
							//��·�����е㶼��ǳ����������ϸ�Ļ���һЩNode�㲢����ȫ�������ֻ�����ڼ򵥻����������
							g.fillRect(transformX(n1.x) - 3, transformY(n1.y) - 3, 7, 7);
							g.fillRect(transformX(n2.x) - 3, transformY(n2.y) - 3, 7, 7);
							
							g.drawLine(transformX(n1.x), transformY(n1.y), transformX(n2.x), transformY(n2.y));
						}
					/*****************����Excel�õ��Ĳ���*************************/
						//ȥ��·��node�����ظ����еļ���
					markId=solver.deleteMarkRepeatId(path);
					 shortPathName=new Vector<String>();
					//�˵��������е���𣬵�Ȼ���Ծ�ȷ������nodes[node_id1].belongMark�����Ǿ�ȷ���� 
					 disName=marks[nodes[node_id1].belongMark].name;
				     centerStoreName=marks[nodes[i].belongMark].name;
				    length = solver.getShortestPathLength(nodes, path)/1000+"km";
					shortPathName.addElement(disName);
					shortPathName.addElement(centerStoreName);
					shortPathName.addElement(length);
					for(int m=0;m<markId.length;m++){
						shortPathName.addElement(marks[markId[m]].name);												
						}
					set.addElement(shortPathName);
					}
					}
				}
		}
			try {
				MapData.saveFile(set,"���봢���⵽���ֵ�"+marks[nodes[node_id1].belongMark].name+"·��");
			} catch (Exception e) {
				e.printStackTrace();
			}
			drawMarks(g);
			repaint();
			g.dispose();
		}
		else {
			clear();
		}

	}
	/**������ʱ�串��״��
	 * @param  args1 ʱ��
	 * @param  args2 �ٶ�*/
	void StorCover_clicked(String args1,String args2) {
		Graphics g = map_image.getGraphics();
		g.setColor(background);
		g.fillRect(0, 0, map_image_width, map_image_height);//��ͼƬ�Ĵ�С��һ�����ο�����ɫ���
		Mark[] marks =map_data.marks;
		Node[] nodes = map_data.nodes;
	    if(node_id1<0){
        	 JOptionPane.showMessageDialog(null, "û��ѡ�����봢���⣬����ѡ�����봢���⣡", "��ʾ",
                     JOptionPane.INFORMATION_MESSAGE, null);
        }
        else if (node_id1>0){
        	if(nodes[node_id1].centerStore!=1)        		
        	{ JOptionPane.showMessageDialog(null, "��ѡ��ĳ��в������봢���⣬��ѡ�����봢���⣡", "��ʾ",
               JOptionPane.INFORMATION_MESSAGE, null);
			   clear();
			}
            else if (node_id2 < 0) {      	
    			drawEdges(g);
    			 int second;
    			 double speed;
    			 String timeString = null;
    			 String rateString = null;
    			if (args1 == null) {
    				second = 8 * 60 * 60;    //�������ʱ�����,Ĭ��Ϊ8Сʱ;
    			} else {
    				second = ReadParam.totalSecone(args1);
    				timeString=ReadParam.totalTime(args1);
    				
    			}
    			if (args2 == null) {
    				speed = 60*1000/3600;    //��������ٶȲ���,Ĭ��Ϊ60km/h;
    			} else {
    				speed = ReadParam.totalSpeed(args2);
    				rateString= ReadParam.rateString(args2);
    			}
    			Vector<Vector> set=new Vector<Vector>();
    			Vector<String> shortPathName=new Vector<String>();
    			String centerStoreName="���봢����";
    			String cityName="�ؼ���";
    			String dis="���봢���⵽�ؼ���֮�����";
    			String city="���봢���⵽���ֵ㾭���ĳ���";
    			shortPathName.addElement(centerStoreName);
    			shortPathName.addElement(cityName);
    			shortPathName.addElement(dis);
    			shortPathName.addElement(city);
    			set.addElement(shortPathName);
               double upperRate=second*speed;  //����ʱ�䴰�¡������ٶ������봢�����ܹ�����ĳ��м���
               int length;
    			for (int i = 0; i < nodes.length; i++) {   				
    			     /*****************��ͼ�ϱ�ʾ��*************************/
    					path = solver.getShortestPath(node_id1,nodes[i].id);
					if (path == null) // ��һ��Ϊһ�����к��κγ��ж�û�����ӵ����
					{
						centerStoreName = marks[nodes[node_id1].belongMark].name;
						cityName= marks[nodes[i].belongMark].name;
						System.out.print("���봢���⣺" + centerStoreName + "��"
								+ cityName + "û������\n");
					} else {
						length = solver.getShortestPathLength(nodes, path);
    					if(length<upperRate)
    					{
    					g.setColor(path_color);
    					g.fillRect(transformX(nodes[node_id1].x) - 3, transformY(nodes[node_id1].y) - 3, 7, 7);
    					g.fillRect(transformX(nodes[i].x) - 3, transformY(nodes[i].y) - 3, 7, 7);
    					
    					if (path != null) {
    						for (int j = 1; j < path.length; ++j) {
    							Node n1 = nodes[path[j-1]], n2 = nodes[path[j]];
    							g.drawLine(transformX(n1.x), transformY(n1.y), transformX(n2.x), transformY(n2.y));
    						}
    					/*****************����Excel�õ��Ĳ���*************************/
    						//ȥ��·��node�����ظ����еļ���
    					markId=solver.deleteMarkRepeatId(path);
    					 shortPathName=new Vector<String>();
    					//�˵��������е���𣬵�Ȼ���Ծ�ȷ������nodes[node_id1].belongMark�����Ǿ�ȷ���� 
    					 centerStoreName=marks[nodes[node_id1].belongMark].name;
    					 cityName=marks[nodes[i].belongMark].name;
    					shortPathName.addElement(centerStoreName);
    					shortPathName.addElement(cityName);
    					shortPathName.addElement( length/1000+"km"); 
    					for(int m=0;m<markId.length;m++){
    						shortPathName.addElement(marks[markId[m]].name);												
    						}
    					set.addElement(shortPathName);
    					}
    				
    		}}}
    			try {
    				MapData.saveFile(set,marks[nodes[node_id1].belongMark].name+timeString+"��"+rateString+"kmÿСʱ����ĳ���");
    			} catch (Exception e) {
    				e.printStackTrace();
    			}
    			drawMarks(g);
    			repaint();
    			g.dispose();
    		}
    		else {
    			clear();
       		
    		}       	
        }
	}
	/**�ؼ���ʱ�䱻����״��
	 * @param  args1 ʱ��
	 * @param  args2 �ٶ�*/
	void cityBeCovered_clicked(String args1,String args2) {
		Graphics g = map_image.getGraphics();
		g.setColor(background);
		g.fillRect(0, 0, map_image_width, map_image_height);//��ͼƬ�Ĵ�С��һ�����ο�����ɫ���
		Mark[] marks =map_data.marks;
		Node[] nodes = map_data.nodes;
		
        if(node_id1<0){
        	 JOptionPane.showMessageDialog(null, "û��ѡ����У�����ѡ����У�", "��ʾ",
                     JOptionPane.INFORMATION_MESSAGE, null);
        }
        else if  (node_id2 < 0) {
			drawEdges(g);
			 int second;
			 double speed;
		    String timeString = null;
			String rateString = null;
 			if (args1 == null) {
 				second = 8 * 60 * 60;    //�������ʱ�����,Ĭ��Ϊ8Сʱ;
 			} else {
 				second = ReadParam.totalSecone(args1);
 				timeString=ReadParam.totalTime(args1);
			
			}
			if (args2 == null) {
				speed = 60*1000/3600;    //��������ٶȲ���,Ĭ��Ϊ60km/h;
			} else {
				speed = ReadParam.totalSpeed(args2);
				rateString=ReadParam.rateString(args2);
			}
			Vector<Vector> set=new Vector<Vector>();
			Vector<String> shortPathName=new Vector<String>();
			String cityName="�ؼ���";
			String centerStoreName="���봢����";
			String dis="���봢���⵽�ؼ���֮�����";
			shortPathName.addElement(cityName);
			shortPathName.addElement(centerStoreName);
			shortPathName.addElement(dis);
			set.addElement(shortPathName);
           double upperRate=second*speed;  //����ʱ�䴰�¡������ٶ������봢�����ܹ�����ĳ��м���
           int length;
			for (int i = 0; i < nodes.length; i++) {
				if (nodes[i].centerStore == 1) {
					/*****************��ͼ�ϱ�ʾ��*************************/
					path = solver.getShortestPath(nodes[i].id,node_id1 );
					if(path==null)               //��һ��Ϊһ�����к��κγ��ж�û�����ӵ����
					{  cityName=marks[nodes[node_id1].belongMark].name;
				      centerStoreName=marks[nodes[i].belongMark].name;
						System.out.print("���봢���⣺"+centerStoreName+"��"+cityName+"û������\n");
					} else {
						length = solver.getShortestPathLength(nodes, path);
						if (length < upperRate) {
					g.setColor(path_color);
					g.fillRect(transformX(nodes[node_id1].x) - 3, transformY(nodes[node_id1].y) - 3, 7, 7);
					g.fillRect(transformX(nodes[i].x) - 3, transformY(nodes[i].y) - 3, 7, 7);
					
					if (path != null) {
						for (int j = 1; j < path.length; ++j) {
							Node n1 = nodes[path[j-1]], n2 = nodes[path[j]];
							//��·�����е㶼��ǳ����������ϸ�Ļ���һЩNode�㲢����ȫ�������ֻ�����ڼ򵥻����������
							g.fillRect(transformX(n1.x) - 3, transformY(n1.y) - 3, 7, 7);
							g.fillRect(transformX(n2.x) - 3, transformY(n2.y) - 3, 7, 7);
							
							g.drawLine(transformX(n1.x), transformY(n1.y), transformX(n2.x), transformY(n2.y));
						}
					/*****************����Excel�õ��Ĳ���*************************/
						//ȥ��·��node�����ظ����еļ���
					markId=solver.deleteMarkRepeatId(path);
					 shortPathName=new Vector<String>();
					//�˵��������е���𣬵�Ȼ���Ծ�ȷ������nodes[node_id1].belongMark�����Ǿ�ȷ���� 
				    cityName=marks[nodes[node_id1].belongMark].name;
					centerStoreName=marks[nodes[i].belongMark].name;
					shortPathName.addElement(cityName);
					shortPathName.addElement(centerStoreName);
					shortPathName.addElement(length/1000+"km"); 
					for(int m=0;m<markId.length;m++){
						shortPathName.addElement(marks[markId[m]].name);												
						}
					set.addElement(shortPathName);
					}
					}}
				}
		}
			try {
				MapData.saveFile(set,marks[nodes[node_id1].belongMark].name+"��"+timeString+"����"+rateString+"kmÿСʱ����Щ���봢����������");
			} catch (Exception e) {
				e.printStackTrace();
			}
			drawMarks(g);
			repaint();
			g.dispose();
		}
		else {
			clear();
		}
		
        }
	
	void provinceTimeanDis(String args) {

		Mark[] marks = map_data.marks;
		Node[] nodes = map_data.nodes;
        double speed; 
		if (args == null) {
			speed = 60*1000/3600;    //��������ٶȲ���,Ĭ��Ϊ60km/h;
		} else {
			speed = ReadParam.totalSpeed(args);
		}
		Vector<Vector> setDistance = new Vector<Vector>();
		Vector<Vector> setTime = new Vector<Vector>();
		Vector<String> distance = new Vector<String>();
		Vector<String> time = new Vector<String>();
		String cityName = "";
		distance.addElement(cityName);
		time.addElement(cityName);
		for (int i = 0; i < nodes.length; i++) {
			if (nodes[i].provincialCapital == 1) {
				distance.addElement(marks[nodes[i].belongMark].name);
				time.addElement(marks[nodes[i].belongMark].name);
				
			}
		}
		setDistance.addElement(distance);
		setTime.addElement(time);
		
		int length;
		for (int i = 0; i < nodes.length; i++) {
			if (nodes[i].provincialCapital == 1) {
				distance = new Vector<String>();
				time = new Vector<String>();
				distance.addElement(marks[nodes[i].belongMark].name);
				time.addElement(marks[nodes[i].belongMark].name);
				
				for (int j = 0; j < nodes.length; j++) {
					if (nodes[j].provincialCapital == 1) {
						length = solver.getShortestPathLength(nodes, solver.getShortestPath(nodes[i].id, nodes[j].id));
						distance.addElement(String.valueOf(length));
						speed = ReadParam.totalSpeed(args);
						time.addElement(String.valueOf(length/speed));
					}

				}
				setDistance.addElement(distance);
				setTime.addElement(time);
			}
		}
		try {
			MapData.saveFile(setDistance, "��ʡ��֮��ľ��루��λm)");
			MapData.saveFile(setTime, "��ʡ��֮���ʱ�䣨��λs)");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
    System.out.print("��ʡ��֮��ľ�����ʱ��洢��EXCEL���");
	};
	void disPredict_clicked() {
	}

	void pathInterrupt_clicked() {
		
		Graphics g = map_image.getGraphics();
		g.setColor(background);
		g.fillRect(0, 0, map_image_width, map_image_height);//��ͼƬ�Ĵ�С��һ�����ο�����ɫ���
		Mark[] marks =map_data.marks;
		Node[] nodes = map_data.nodes;
		
//		int x0=transformX(min_x);
		int x1=transformX(max_x);
//		int y0=transformY(max_y);
		int y1=transformY(min_y);
		for(int i=0;i<10;i++){
			g.setColor(colors[i]);
			g.drawLine(x1-310, y1+20*i-600, x1-295, y1+20*i-600);
			g.drawString("ѡȡ��"+(i+1)+"st�����·",x1-294,y1+20*i-600);
		}
		
		
        if(node_id1<0){
        	 JOptionPane.showMessageDialog(null, "û��ѡ����У�����ѡ����У�", "��ʾ",
                     JOptionPane.INFORMATION_MESSAGE, null);
        }
        else if  (node_id2 < 0) {
			drawEdges(g);
			Vector<Vector> set=new Vector<Vector>();
			Vector<String> shortPathName=new Vector<String>();
			String length;
			String disName="���ֵ�";
			String centerStoreName="���봢����";
			String kil="���봢���⵽���ֵ�ľ���";
			String city="���봢���⵽���ֵ㾭���ĳ���";
			shortPathName.addElement(disName);
			shortPathName.addElement(centerStoreName);
			shortPathName.addElement(kil);
			shortPathName.addElement(city);
			set.addElement(shortPathName);

			for (int i = 0; i < nodes.length; i++) {
				if (nodes[i].centerStore == 1) {
					/*****************��ͼ�ϱ�ʾ��*************************/

					YenTopKShortestPathsAlg yenAlg = new YenTopKShortestPathsAlg(graph);
					List<Path> shortest_paths_list = yenAlg.get_shortest_paths(graph.get_vertex(nodes[i].id), graph.get_vertex(node_id1), 10);
			        Random rv=new Random();
			        int index=rv.nextInt(9);
					path=shortest_paths_list.get(index).getInt();
//					int[] i=shortest_paths_list.get(3).getInt();
//					 System.out.println(shortest_paths_list.get(3));
//					 System.out.println(":"+shortest_paths_list);
//					 System.out.println(yenAlg.get_result_list().size());
//					
//					path = solver.getShortestPath(nodes[i].id,node_id1 );
					if(path==null)               //��һ��Ϊһ�����к��κγ��ж�û�����ӵ����
					{  disName=marks[nodes[node_id1].belongMark].name;
				       centerStoreName=marks[nodes[i].belongMark].name;
						System.out.print("���봢���⣺"+centerStoreName+"��"+disName+"û������\n");
					}else{
					g.setColor(Color.RED);
					g.fillRect(transformX(nodes[node_id1].x) - 15, transformY(nodes[node_id1].y) - 15, 30, 30);
					g.setColor(colors[index]);
					g.fillRect(transformX(nodes[i].x) - 3, transformY(nodes[i].y) - 3, 7, 7);
					
					if (path != null) {
						for (int j = 1; j < path.length; ++j) {
							Node n1 = nodes[path[j-1]], n2 = nodes[path[j]];
							//��·�����е㶼��ǳ����������ϸ�Ļ���һЩNode�㲢����ȫ�������ֻ�����ڼ򵥻����������
							g.fillRect(transformX(n1.x) - 3, transformY(n1.y) - 3, 7, 7);
							g.fillRect(transformX(n2.x) - 3, transformY(n2.y) - 3, 7, 7);
							
							g.drawLine(transformX(n1.x), transformY(n1.y), transformX(n2.x), transformY(n2.y));
						}
					/*****************����Excel�õ��Ĳ���*************************/
						//ȥ��·��node�����ظ����еļ���
					markId=solver.deleteMarkRepeatId(path);
					 shortPathName=new Vector<String>();
					//�˵��������е���𣬵�Ȼ���Ծ�ȷ������nodes[node_id1].belongMark�����Ǿ�ȷ���� 
					 disName=marks[nodes[node_id1].belongMark].name;
				     centerStoreName=marks[nodes[i].belongMark].name;
				    length = solver.getShortestPathLength(nodes, path)/1000+"km";
					shortPathName.addElement(disName);
					shortPathName.addElement(centerStoreName);
					shortPathName.addElement(length);
					for(int m=0;m<markId.length;m++){
						shortPathName.addElement(marks[markId[m]].name);												
						}
					set.addElement(shortPathName);
					}
					}
				}
		}
			try {
				MapData.saveFile(set,"���봢���⵽���ֵ�"+marks[nodes[node_id1].belongMark].name+"·��");
			} catch (Exception e) {
				e.printStackTrace();
			}
			drawMarks(g);
			repaint();
			g.dispose();
		}
		else {
			clear();
		}


	}
	void clear_clicked() {
		clear();
	}
	

    /**�˳�����*/	
	public void exit() {
	    Object[] options = { "ȷ��", "ȡ��" };
	    JOptionPane pane2 = new JOptionPane("�����˳���?", JOptionPane.QUESTION_MESSAGE,
	        JOptionPane.YES_NO_OPTION, null, options, options[1]);
	    JDialog dialog = pane2.createDialog(this, "��ʾ");
	    dialog.setVisible(true);
	    Object selectedValue = pane2.getValue();
	    if (selectedValue == null || selectedValue == options[1]) {
	      setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // ����ǹؼ�

	    } else if (selectedValue == options[0]) {
	      setDefaultCloseOperation(EXIT_ON_CLOSE);
	    }
	  }

	
	public void mousePressed(MouseEvent e) {

	}
	public void mouseReleased(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}//������ʱ����
	public void mouseExited(MouseEvent e) {}//����뿪ʱ����
    /**��ȵ�ǰͼ��*/
	public void save(Image image) throws IOException {
		int w = image.getWidth(this);
		int h = image.getHeight(this);

		// ���ȴ���һ��BufferedImage��������ΪImageIOдͼƬ�õ���BufferedImage������
		BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_3BYTE_BGR);

		// �ٴ���һ��Graphics����������������Ҫ���ֵ�ͼƬ�������洫�ݹ�����Image����
		Graphics g = bi.getGraphics();
		try {
			g.drawImage(image, 0, 0, null);

			// ��BufferedImage����д���ļ��С�
			ImageIO.write(bi, "jpg", new File("outImage"+File.separator+"image.jpg"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**�������봢���⸲�ǵ��ͼ��*/
	public void saveCover(Image image) {
		Mark[] marks =map_data.marks;
		Node[] nodes = map_data.nodes;
		String cityName="�ؼ���";
		String centerStoreName="���봢����";
		int w = image.getWidth(this);
		int h = image.getHeight(this);
		// ���ȴ���һ��BufferedImage��������ΪImageIOдͼƬ�õ���BufferedImage������
		BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_3BYTE_BGR);
		// �ٴ���һ��Graphics����������������Ҫ���ֵ�ͼƬ�������洫�ݹ�����Image����
		Graphics g = bi.getGraphics();
		g.setColor(background);
		g.fillRect(0, 0, map_image_width, map_image_height);//��ͼƬ�Ĵ�С��һ�����ο�����ɫ���
		//��ѭ����ʾ�ڵ�ͼ�ϵ����봢����͵ؼ��еĵ㣨����ʡȥ��
		for (int i = 0; i < marks.length; i++) {
			Mark m = marks[i];
			int x = transformX(m.x);
			int y = transformY(m.y);
			//Mark����centerStore����еĲ���
			if (m.centerStore == 1) {
				g.setColor(markCenterStore_color);// ���봢�������С���ε���ɫ
				g.drawRect(x - 3, y - 3, 7, 7);// x-1,y-1�ǻ��ƾ��ε����꣬3��3�ǻ��ƾ��εĿ�Ⱥ͸߶�
				g.setColor(foreCenterStoreground);// ������ǩ����ɫ
				g.drawString(m.name, x + 2, y - 2);// ��x+2��y-2�ĵط���ʾ��ǩ

			}else{
				g.setColor(mark_color);// ����С���ε���ɫ
				g.drawRect(x - 1, y - 1, 3, 3);// x-1,y-1�ǻ��ƾ��ε����꣬3��3�ǻ��ƾ��εĿ�Ⱥ͸߶�
			}
		}
		//�����봢����͵ؼ���֮����� i������У�j�������봢���⣻
		for (int i = 0; i < nodes.length; i++) {
			int length=Integer.MAX_VALUE;
			int centerId=Integer.MAX_VALUE;
			for (int j = 0; j < nodes.length; j++) {
				if (nodes[j].centerStore == 1) {
					/***************** ��ͼ�ϱ�ʾ�� *************************/
					path = solver.getShortestPath(nodes[i].id, nodes[j].id);
					if (path == null) // ��һ��Ϊһ�����к��κγ��ж�û�����ӵ����
					{
						cityName = marks[nodes[i].belongMark].name;
						centerStoreName = marks[nodes[j].belongMark].name;
						System.out.print("���봢���⣺" + centerStoreName + "��"
								+ cityName + "û������\n");
					} else {
						if (solver.getShortestPathLength(nodes, path) < length) {
							length = solver.getShortestPathLength(nodes, path);
							centerId = j;
						}
					}
				}
			}
			if (centerId != Integer.MAX_VALUE) {
				g.setColor(path_color);
				g.drawLine(transformX(nodes[i].x), transformY(nodes[i].y),
						transformX(nodes[centerId].x),
						transformY(nodes[centerId].y));
			}
		}
			
		//����ͼƬ
		try {

			// ��BufferedImage����д���ļ��С�
			ImageIO.write(bi, "jpg", new File("outImage"+File.separator+"coverImage.jpg"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		g.dispose();   //���ٳ�����ָ����ͼ�ν�����Դ����������Դ������Ӱ��

		
	}
	/**����������봢���⸲�ǵ��ͼ�񼰴洢����*/
	public void saveTwoCover(Image image) {
		Mark[] marks =map_data.marks;
		Node[] nodes = map_data.nodes;
		String centerStoreName="���봢����";
		Vector<Vector> set=new Vector<Vector>();
		Vector<String> name=new Vector<String>();
		String cityName="�ؼ���";
		String centerOne="һ�����봢����";
		String centerTwo="�������봢����";
		String pn="�˿���(����)";
		String gdp="GDP(��Ԫ)";
		name.addElement(cityName);
		name.addElement(centerOne);
		name.addElement(centerTwo);
		name.addElement(pn);
		name.addElement(gdp);
		set.addElement(name);
		int w = image.getWidth(this);
		int h = image.getHeight(this);
		// ���ȴ���һ��BufferedImage��������ΪImageIOдͼƬ�õ���BufferedImage������
		BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_3BYTE_BGR);
		// �ٴ���һ��Graphics����������������Ҫ���ֵ�ͼƬ�������洫�ݹ�����Image����
		Graphics g = bi.getGraphics();
		g.setColor(background);
		g.fillRect(0, 0, map_image_width, map_image_height);//��ͼƬ�Ĵ�С��һ�����ο�����ɫ���
		//��ѭ����ʾ�ڵ�ͼ�ϵ����봢����͵ؼ��еĵ㣨����ʡȥ��
		for (int i = 0; i < marks.length; i++) {
			Mark m = marks[i];
			int x = transformX(m.x);
			int y = transformY(m.y);
			//Mark����centerStore����еĲ���
			if (m.centerStore == 1) {
				g.setColor(markCenterStore_color);// ���봢�������С���ε���ɫ
				g.drawRect(x - 3, y - 3, 7, 7);// x-1,y-1�ǻ��ƾ��ε����꣬3��3�ǻ��ƾ��εĿ�Ⱥ͸߶�
				g.setColor(foreCenterStoreground);// ������ǩ����ɫ
				g.drawString(m.name, x + 2, y - 2);// ��x+2��y-2�ĵط���ʾ��ǩ

			}else{
				g.setColor(mark_color);// ����С���ε���ɫ
				g.drawRect(x - 1, y - 1, 3, 3);// x-1,y-1�ǻ��ƾ��ε����꣬3��3�ǻ��ƾ��εĿ�Ⱥ͸߶�
			}
		}
		//�����봢����͵ؼ���֮����� i������У�j�������봢���⣻
		for (int i = 0; i < nodes.length; i++) {
			int centerId1=Integer.MAX_VALUE;
			int centerId2=Integer.MAX_VALUE;
			NodeLength n = null;
			Vector<NodeLength> v=new Vector<NodeLength>();
			for (int j = 0; j < nodes.length; j++) {
				if (nodes[j].centerStore == 1) {
					/***************** ��ͼ�ϱ�ʾ�� *************************/
					path = solver.getShortestPath(nodes[i].id, nodes[j].id);
					if (path == null) // ��һ��Ϊһ�����к��κγ��ж�û�����ӵ����
					{
						cityName = marks[nodes[i].belongMark].name;
						centerStoreName = marks[nodes[j].belongMark].name;
						System.out.print("���봢���⣺" + centerStoreName + "��"
								+ cityName + "û������\n");
					} else {
						n=new NodeLength();
						n.length=solver.getShortestPathLength(nodes, path);
						n.id=j;
                        v.add(n);
					}
				}
			}
			if (v.size() != 0) {
				//�ҵ�ǰ�����С���������ȵ�Ԫ��
				for (int k = 0; k < 2; k++) {
					for (int l = k + 1; l < v.size(); l++) {
						if (v.get(k).length > v.get(l).length) {
							n = v.get(k);
							v.set(k, v.get(l));
							v.set(l, n);
						}
					}
				}
				centerId1 = v.get(0).id;  
				centerId2 = v.get(1).id;
			}
			if (centerId1 != Integer.MAX_VALUE&&centerId2 != Integer.MAX_VALUE) {
				g.setColor(path_color);
				g.drawLine(transformX(nodes[i].x), transformY(nodes[i].y),
						transformX(nodes[centerId1].x),
						transformY(nodes[centerId1].y));
				g.setColor(Color.blue);
				g.drawLine(transformX(nodes[i].x), transformY(nodes[i].y),
						transformX(nodes[centerId2].x),
						transformY(nodes[centerId2].y));
				name=new Vector<String>();
				name.addElement(marks[nodes[i].belongMark].name);
				name.addElement(marks[nodes[centerId1].belongMark].name);
				name.addElement(marks[nodes[centerId2].belongMark].name);
				name.addElement(Double.toString(marks[nodes[i].belongMark].peopleNumber));
				name.addElement(Double.toString(marks[nodes[i].belongMark].gdp));
				set.addElement(name);
			}
		}
		try {
			MapData.saveFile(set,"�ؼ��е����Ͼ�Ԯ");
		} catch (Exception e) {
			e.printStackTrace();
		}
		//����ͼƬ
		try {

			// ��BufferedImage����д���ļ��С�
			ImageIO.write(bi, "jpg", new File("outImage"+File.separator+"coverTwoImage.jpg"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		g.dispose();   //���ٳ�����ָ����ͼ�ν�����Դ����������Դ������Ӱ��
      System.out.print("�洢���");
		
	}	
	
	/**���о���*/
	public void cluster(Image image){
		
		Graphics g = image.getGraphics();
		g.setColor(background);
		g.fillRect(0, 0, map_image_width, map_image_height);//��ͼƬ�Ĵ�С��һ�����ο�����ɫ���
	
		int x0=transformX(min_x);
		int x1=transformX(max_x);
		int y0=transformY(max_y);
		int y1=transformY(min_y);
		//���㲿��Ϊ(x0-5,y1+5)
		
		// ���û�����ɫ
		g.setColor(Color.BLUE);
		// ������
		g.drawLine(x0-5, y0, x0-5, y1+5);
		g.setColor(Color.RED);
		g.drawString("Y", x0+2, y0+3);

		g.setColor(Color.BLUE);
		g.drawLine(x0-5, y1+5, x1+5, y1+5);
		g.setColor(Color.RED);
		g.drawString("X", x1+10, y1+5);
		Mark[] marks =map_data.marks;

		for (int i = 0; i < marks.length; i++) {
			Mark m = marks[i];
			int x = transformX(m.x);
			int y = transformY(m.y);
			//Mark����centerStore����еĲ���
				g.setColor(colors[m.category]);// ����С���ε���ɫ
				g.drawRect(x - 1, y - 1, 3, 3);// x-1,y-1�ǻ��ƾ��ε����꣬3��3�ǻ��ƾ��εĿ�Ⱥ͸߶�
				g.setColor(foreground);// ������ǩ����ɫ
				//g.drawString(m.name, x + 2, y - 2);// ��x+2��y-2�ĵط���ʾ��ǩ
		}
	
		repaint();
		System.out.print("����ͼƬ�Ż���ϣ����Ա��棡");
	
	}

	/**����ͼƬ���з��� */
	public void partition(Image image) {
//		int sizeOfSearch=11;
//		double maxcorrect = 0f;
		param = new Parameter();
		// default values
		param.svmType = Parameter.C_SVC;
		param.kernelType = Parameter.RBF;
		param.degree = 3;
		param.gamma = 0.5;
		param.r = 0;
		param.nu = 0.5;
		param.cacheSize = 40;
		param.C = 100000;
		param.stopCriteria = 1e-3;
		param.epsilon = 0.1;
		param.shrinking = 1;
		param.nr_weight = 0;
		param.weight_label = new int[0];
		param.weight = new double[0];

		Graphics g = image.getGraphics();
		g.setColor(background);
		g.fillRect(0, 0, map_image_width, map_image_height);//��ͼƬ�Ĵ�С��һ�����ο�����ɫ���
		
		int XLEN=map_image_width-150;
		int YLEN=map_image_height-150;
		
		Mark[] marks =map_data.marks;
		for(int i=0;i<marks.length;i++){
			point_list.addElement(new point((double)transformX(marks[i].x)/XLEN,(double)transformY(marks[i].y)/YLEN,marks[i].category));
		}
		
		// build problem
		Data data = new Data();
		data.l = point_list.size();
		data.y = new double[data.l];
		
		data.x =  new com.ibm.perf.parameter.Node [data.l][2];
		for(int i=0;i<data.l;i++)
		{
			point p = point_list.elementAt(i);
			data.x[i][0] =  new com.ibm.perf.parameter.Node();
			data.x[i][0].index = 1;
			data.x[i][0].value = p.x;
			data.x[i][1] =  new com.ibm.perf.parameter.Node();
			data.x[i][1].index = 2;
			data.x[i][1].value = p.y;
			data.y[i] = p.value;
		}
//		/**���н�����֤��ѡȡ���ŵ�C��g*/
//		for (int c = 0; c < sizeOfSearch; c++) {
//			// this is the power of the gamma parameter 2^g
//			for (int k = 0; k < sizeOfSearch; k++) {
//				int cVal = 2 * c - 5;
//				int gVal = 2 * k - 5;
//				double cost = Math.pow(2, cVal);
//				double gamma = Math.pow(2, gVal);
////				param.C = cost;
////				param.gamma = gamma;
//				double correct = 0f;
//				correct = GridParameter.doCrossValidation(data, param, 10);
//				if (correct > maxcorrect) {
//					maxcorrect = correct;
//					param.C = cost;
//					param.gamma = gamma;
//
//				}
//			}
//		}
//          System.out.print("param.c="+param.C+"param.gamma="+param.gamma); 
		 //build model & classify
		Model model = Predict.svmTrain(data, param);
		com.ibm.perf.parameter.Node[] x = new com.ibm.perf.parameter.Node[2];
		x[0] = new com.ibm.perf.parameter.Node();
		x[1] = new com.ibm.perf.parameter.Node();
		x[0].index = 1;
		x[1].index = 2;

		int x0=transformX(min_x);
		int x1=transformX(max_x);
		int y0=transformY(max_y);
		int y1=transformY(min_y);
		Graphics window_gc = getGraphics();
		for (int i = x0-10; i < x1+50; i++)
			for (int j = y0-20; j < y1+10 ; j++) {
				x[0].value = (double) i /XLEN;
				x[1].value = (double) j /YLEN;
				int d = (int)Predict.svmPredict(model, x);
				g.setColor(colors[d]);
				window_gc.setColor(colors[d]);
				g.drawLine(i,j,i,j);
				window_gc.drawLine(i,j,i,j);
		}
		
//		for (int i = 0; i < XLEN; i++)
//			for (int j = 0; j < YLEN ; j++) {
//				x[0].value = (double) i /XLEN;
//				x[1].value = (double) j /YLEN;
//				int d = (int)Predict.svmPredict(model, x);
//				g.setColor(colors[d]);
//				window_gc.setColor(colors[d]);
//				g.drawLine(i,j,i,j);
//				window_gc.drawLine(i,j,i,j);
//		}
	

		drawMarks(g);
		repaint();
		System.out.print("����ͼƬ�Ż���ϣ����Ա��棡");
	
	}
	/**����ͼ�εķֱ���*/
	public void piexImage(){
		
	}
	public static void main(String[] argv)
	{ 
		     MapFrame m=new MapFrame("�п�Ժ�о���ԺӦ����������");
		     //m.setIconImage(Image);
		     /**������Frame���ӻ�������createMapImage()����map_image��ʱ��Ϊ��*/	     
		     Image icon=m.getToolkit().getImage("icon"+File.separator+"gucas.jpg");
	         m.setIconImage(icon);
	         m.setVisible(true);
		     m.init();
		   
		     m.setSize(1000,600);
	         m.setLocation(100, 100);
	}
	
	public void mouseWheelMoved(MouseWheelEvent e) {
	}

} 