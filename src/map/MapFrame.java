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
	/**时间：默认为8小时*/
	static final String Time_PARAM="-d 0 -h 8 -m 0 -s 0";
	/**地震震级：默认为6级*/
    static final String Mag_PARAM = "-M 6";
    /**道路中断情况，以Mark为标记，Node没有名称不好表示 */
    static final String From = "中断起始点";
    static final String To = "中断终点";
    /**时速:默认为60km/h*/
    static final String Rate_PARAM = "-r 60";
	/**背景色*/
	private final Color background = Color.white;
	/**一般地名上字的颜色*/
	private final Color foreground = Color.blue; 
	/**中央地名上字的颜色*/
	private final Color foreCenterStoreground = Color.red; 
	/**一般地名正方形标记色品红色 */
	private final Color mark_color = Color.magenta; 
	/**中央储备库正方形标记色黄色 */
	private final Color markCenterStore_color = Color.yellow; 
	
	/**画出相应最短路的颜色*/
	private final Color path_color = Color.red; 
	/**根据Edge的道路级别进行着色，如高速、一级公路、二级公路*/
	private final Color[] edge_colors = {
    new Color(0x000000), new Color(0x404040), new Color(0x808080), new Color(0xA0A0A0), new Color(0xC0C0C0), new Color(0xC0C0C0)
	};

    /**读入的文本的地图的总体数据变量*/
	private MapData map_data; 
	/**地图的最左边*/
	private double map_left; 
	/**地图的最顶部*/
	private double  map_top; 
    /**图片显示的画面的高度常量,如果不拖动，全屏幕地图看不到。本机器屏幕分辨率1366*768像素*/
	private int map_image_height = 1500;
	/**图片显示的画面的宽度变量*/
	private int map_image_width; 
    /**图片变量*/
	private Image map_image; 
	/**比例尺度*/
	private double map_image_scale; 	
	/**获得实际的宽度*/
	double map_width;
	/**获得实际的高度*/
	double map_height;
	
   /**窗口的左上角在图片上的x坐标*/
	private int screen_left; 
	/**窗口的左上角在图片上的y坐标*/
	private int screen_top; 
    /**最短路*/
	private ShortestPathSolver solver; 
	/**从MapData得到的图，来求k-top最短路*/
	private static Graph graph;
	
	private int node_id1 = -1, node_id2 = -1; 
	/**从起始点到终点所经历的点的Id.path[0] = node_id1并且path[n] = node_id2*/
	private int[] path; //
	/**去重之后的城市序号*/
	private int[] markId;
	private JLabel l;
	private ImageIcon ic;
		
	double min_x = Double.MAX_VALUE, max_x = -Double.MAX_VALUE; 
	double min_y = Double.MAX_VALUE, max_y = -Double.MAX_VALUE;
	
	/**支持向量机所选用的参数*/
	private Parameter param;
	
	/**分类别中的point点与Toy中类似*/
	Vector<point> point_list = new Vector<point>();	
	
	final static Color colors[] =
	{ /**黑色*/
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

	/**初始化程序init()*/
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
		/** 创建地图图像*/
		createMapImage();
	    ic=new ImageIcon(map_image);
		l.setIcon(ic);
		con.add(l,BorderLayout.CENTER);
		//con.add(new JScrollPane(l), BorderLayout.CENTER);
		l.setOpaque(true); // JLabel默认是透明的,所以直接setBackground()不能设置其背景颜色,但setForceground不用
		JPanel p = new JPanel();
		GridBagLayout gridbag = new GridBagLayout();
		p.setLayout(gridbag);
		
		/*********** 增加按钮与文本框 *************************/
		Button disVrp = new Button("受灾点救援路径");
		Button storeCover = new Button("储备库时间覆盖状况");
		Button cityBeCovered = new Button("地级市时间被覆盖状况");
		Button disPredict = new Button("受灾点需求预测");
		Button clear = new Button("清空");
		Button pathInterrupt = new Button("道路可靠性选择");
		final TextField time_line = new TextField(Time_PARAM);
		final TextField mag_line = new TextField(Mag_PARAM);
		final TextField from = new TextField(From);
		final TextField to = new TextField(To);
		final TextField rate_line = new TextField(Rate_PARAM);
		Label arrive = new Label("到");
		
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
		/******************文件操作**********************************/
		Menu menuFile = new Menu("文件") ;
		MenuBar menuBar = new MenuBar() ;
		menuBar.add(menuFile) ;

		MenuItem newItem = new MenuItem("新建") ;
		MenuItem openItem = new MenuItem("打开") ;
		MenuItem closeItem = new MenuItem("关闭") ;
		MenuItem exitItem = new MenuItem("退出") ;

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
		
		/******************编辑操作**********************************/
		Menu editorFile = new Menu("编辑") ;
		menuBar.add(editorFile) ;

		MenuItem disVrpM = new MenuItem("受灾点救援路径") ;
		MenuItem storeCoverM = new MenuItem("储备库时间覆盖善") ;
		MenuItem cityBeCoveredM = new MenuItem("地级市时间被覆盖状况") ;
		MenuItem disPredictM = new MenuItem("受灾点需求预测") ;
		MenuItem province = new MenuItem("各省市之间时间与距离") ;
	

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
		
		/*****************视图**********************************/
		Menu viewEditor = new Menu("视图") ;
		menuBar.add(viewEditor) ;
		MenuItem saveImage = new MenuItem("保存当前图片") ;
		MenuItem saveCoverImage = new MenuItem("保存中央储备库覆盖范围") ;
		MenuItem saveCoverTwoImage = new MenuItem("保存二级中央储备库覆盖范围");
		MenuItem cluster = new MenuItem("聚类") ;
		MenuItem partition = new MenuItem("分区") ;
		MenuItem piex=new MenuItem("分辨率");
		
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
		
		/******************帮助**********************************/
		Menu helpFile = new Menu("帮助") ;
		menuBar.add(helpFile) ;

		MenuItem helpDocItem = new MenuItem("帮助文档") ;
		MenuItem helpItem = new MenuItem("关于 emergencyMap") ;

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
				JOptionPane.showMessageDialog(null, "禁止商业应用,如有需要，请联系：" +
						"buruguo09@mails.gucas.ac.cn", "关于 emergencyMap", JOptionPane.PLAIN_MESSAGE);
			}
		}) ;

		helpFile.add(helpDocItem) ;
		helpFile.add(helpItem) ;
        setMenuBar(menuBar) ;	// 菜单是需要通过此方法增加的
        
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
		
         //补充退出程序时，在内存中依然存在的问题
		 addWindowListener(new WindowAdapter() {
		      public void windowClosing(WindowEvent e)
		      { exit();}});
		      
		this.enableEvents(AWTEvent.MOUSE_EVENT_MASK);
		/************************************/

		//起始窗口左上角相对图像的坐标
		screen_left = (map_image_width - getSize().width) >> 1;
		screen_top = (map_image_height - getSize().height) >> 1;

		// Dijkstra
		solver = new Dijkstra(map_data);
          //要在import_from_file方法中建立双向连通图
	    graph = new VariableGraph(map_data);
		    

		// test
//		System.out.println("Testing batch processing of top-k shortest paths!");
//		YenTopKShortestPathsAlg yenAlg = new YenTopKShortestPathsAlg(graph);
//		List<Path> shortest_paths_list = yenAlg.get_shortest_paths(graph.get_vertex(4), graph.get_vertex(5), 5);
//         int[] i=shortest_paths_list.get(3).getInt();
//		 System.out.println(shortest_paths_list.get(3));
//		 System.out.println(":"+shortest_paths_list);
//		 System.out.println(yenAlg.get_result_list().size());
			
			
		addMouseListener(this);      //鼠标的触点
		addMouseMotionListener(this); //鼠标的移动
		addMouseWheelListener(this);//鼠标的滑轮操作

	}

   public void paint(Graphics g) {
	   //super.paint(g) ; 
     //图像的左上角位于该图形上下文坐标空间的 (x, y)
	g.drawImage(map_image, -screen_left, -screen_top, this);


	}
	public void update(Graphics g) {
		paint(g);
	}
	private Point prev_p; 
    /**鼠标按键按下并拖动时调用(使地图可以移动）*/
    public void mouseDragged(MouseEvent e) {  
    	
		final int left_max = map_image_width - getSize().width; 
		final int top_max = map_image_height - getSize().height; 
		screen_left -= e.getX() - prev_p.x;//e.getX() - prev_p.x为正，为向左移动距离;e.getX() - prev_p.x为负，为向右移动距离
		screen_left = Math.max(0, screen_left);
		//往左移动到图片只剩窗口宽度时，不能再移动
		//即窗口左上角相对图像的水平坐标要大于0（由screen_left确定），最大不超过left_max
		screen_left = Math.min(left_max, screen_left);

		screen_top -= e.getY() - prev_p.y;
		screen_top = Math.max(0, screen_top);
		//往上移动到图片只剩窗口高度时，不能再移动
		screen_top = Math.min(top_max, screen_top);
	//	System.out.print("screen_left:"+screen_left+"   screen_top:"+screen_top+"\n");
		prev_p = e.getPoint();
		//如果移动的话，只剩下最后一条路，如果在多条路径时，那么path最好改成二维数组，那样在扩大缩水的时候也可以用到
		//redrawMapImage();
		repaint();
	}
      /**鼠标光标移动但无按键按下时调用*/
	public void mouseMoved(MouseEvent e) {
		prev_p = e.getPoint();
		//点在小窗口的坐标
	}

	/**点击点操作*/
	public void mouseClicked(MouseEvent e) {
		//点击第一个点时node_id1<0,点击部分赋值给第一个点，再点击第二个点时，因为第一个点已经为大于0的，所以赋值给第二个点，
		//当再点击时，node_id1与node_id2都大于0，所以都赋值为-1
		if (node_id1 < 0) {
			node_id1 = getNearestNode(e.getPoint());
			
			
		}
		else if (node_id2 < 0) {
			node_id2 = getNearestNode(e.getPoint());
			path = solver.getShortestPath(node_id1, node_id2);
		}
		else {
			//画相应最短路时这两个属性同时用上
			node_id1 = node_id2 = -1;
			path = null;
		}

		redrawMapImage();
		repaint();
	}

	/**重画地图区域*/
	private void redrawMapImage() {
		Graphics g = map_image.getGraphics();
		g.setColor(background);
		g.fillRect(0, 0, map_image_width, map_image_height);//以图片的大小被一个矩形框框的颜色填充
		drawEdges(g);
		drawPath(g);
		drawMarks(g);

		g.dispose();//销毁程序中指定的图形界面资源，对数据资源不产生影响

	}

	/**画边线的函数*/
	private void drawEdges(Graphics g) {
		Node[] nodes = map_data.nodes;
	    Edge[] edges = map_data.edges;

		for (int i = 0; i < edges.length; ++i) {
			Edge e = edges[i];
		    Node n1 = nodes[e.nodeId1], n2 = nodes[e.nodeId2];
			
			g.setColor(edge_colors[e.type - 1]);//根据道路级别进行颜色的设置
			g.drawLine(transformX(n1.x), transformY(n1.y), transformX(n2.x), transformY(n2.y));
		}
	}
	
	/** 画出相应的最短路*/
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
		
		//	System.out.print(marks[nodes[node_id1].belongMark].name+"到"+ marks[nodes[node_id2].belongMark].name+"距离为"+length/1000+"km");
			}
	}

	/**画地名的标识*/
	private void drawMarks(Graphics g) {
		Mark[] marks = map_data.marks;

		for (int i = 0; i < marks.length; i++) {
			Mark m = marks[i];
			int x = transformX(m.x);
			int y = transformY(m.y);
			//Mark加入centerStore后进行的操作
			if (m.centerStore == 1) {
				g.setColor(markCenterStore_color);// 中央储备库地名小矩形的颜色
				g.drawRect(x - 3, y - 3, 7, 7);// x-1,y-1是绘制矩形的坐标，3，3是绘制矩形的宽度和高度
				g.setColor(foreCenterStoreground);// 地名标签的颜色
				g.drawString(m.name, x + 2, y - 2);// 在x+2，y-2的地方显示标签

			} else {
				g.setColor(mark_color);// 地名小矩形的颜色
				g.drawRect(x - 1, y - 1, 3, 3);// x-1,y-1是绘制矩形的坐标，3，3是绘制矩形的宽度和高度
				g.setColor(foreground);// 地名标签的颜色
				g.drawString(m.name, x + 2, y - 2);// 在x+2，y-2的地方显示标签
			}
		}
	}

	/**创建地图的初步轮廓涉及到边缘线的对象*/
	private void createMapImage() {
		Node[] nodes = map_data.nodes;

		for (int i = 0; i < nodes.length; ++i) {
			min_x = Math.min(min_x, nodes[i].x);
			max_x = Math.max(max_x, nodes[i].x);
			min_y = Math.min(min_y, nodes[i].y);
			max_y = Math.max(max_y, nodes[i].y);
		}
		//因为在MapData加载数据时已经把经纬度转化为NTU经纬度，所以为真实的宽高度
	   map_width = max_x - min_x;//获得实际的宽度
	   map_height = max_y - min_y;//获得实际的高度
       //这样做的标记是为了是以左下角为基准来进行坐标设定
		map_left = min_x;//地图的最左边		
		map_top = max_y;//地图的最顶部


		System.out.println("地图实际尺寸: " + map_width/1000 + "km x " + map_height/1000 + "km");
		//显示的比例尺度，前一个变量是显示的窗口的高度，后一个变量是地图的高度
		map_image_scale = (double)map_image_height / map_height;  
		map_image_width = (int)(map_width * map_image_scale);  //根据以上的比例来确定地图实际占用的宽度，且是动态的
		map_image_height+=150;//图片的宽度和高度同时加150，以使得右边和下边的坐标能显示
		map_image_width+=150;		
		map_image = createImage(map_image_width, map_image_height);
		redrawMapImage();    //画出了地图的总体框架后，再来重画地图的背景，点线边缘
		System.out.println("地图图像实际占用尺寸: " + map_image_width + " x " + map_image_height + "像素");
	}
	

   //以下四个方法+50和减50的作用就是把最左端和最上端的顶点在图片上显示出来
   /**根据实际x水平位置，比例尺度来计算地图上的x水平位置
    * @param x 实际x水平位置*/
	private int transformX(double x) {
		return (int)((x - map_left) * map_image_scale)+50;
	}
	private int transformY(double y) {
		return (int)((map_top - y) * map_image_scale)+80;
	}
       /**根据地图上的水平位置（x+screen_left），比例尺度来计算实际x水平位置
	    * @param x 在小窗口上的水平位置*/
	private double screen2mapX(int x) {
		return map_left + (x-50 + screen_left) / map_image_scale;
	}
	private double screen2mapY(int y) {
		return map_top - (y-80 + screen_top) / map_image_scale;
	}
    /**得到点击点最近的坐标*/
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
//		System.out.print("需要打印的点id"+id+"\n");
		return id;
	}
	private void clear(){
        node_id2=node_id1 = -1;
        path=null;
        redrawMapImage();
		repaint();
		
	}
	/**受灾点救援路径 */
	void disVrp_clicked() {
		Graphics g = map_image.getGraphics();
		g.setColor(background);
		g.fillRect(0, 0, map_image_width, map_image_height);//以图片的大小被一个矩形框框的颜色填充
		Mark[] marks =map_data.marks;
		Node[] nodes = map_data.nodes;
		
        if(node_id1<0){
        	 JOptionPane.showMessageDialog(null, "没有选择城市，请先选择城市！", "提示",
                     JOptionPane.INFORMATION_MESSAGE, null);
        }
        else if  (node_id2 < 0) {
			drawEdges(g);
			Vector<Vector> set=new Vector<Vector>();
			Vector<String> shortPathName=new Vector<String>();
			String length;
			String disName="受灾点";
			String centerStoreName="中央储备库";
			String kil="中央储备库到受灾点的距离";
			String city="中央储备库到受灾点经过的城市";
			shortPathName.addElement(disName);
			shortPathName.addElement(centerStoreName);
			shortPathName.addElement(kil);
			shortPathName.addElement(city);
			set.addElement(shortPathName);

			for (int i = 0; i < nodes.length; i++) {
				if (nodes[i].centerStore == 1) {
					/*****************在图上表示出*************************/
					path = solver.getShortestPath(nodes[i].id,node_id1 );
					if(path==null)               //这一句为一个城市和任何城市都没有连接的情况
					{  disName=marks[nodes[node_id1].belongMark].name;
				       centerStoreName=marks[nodes[i].belongMark].name;
						System.out.print("中央储备库："+centerStoreName+"与"+disName+"没有连接\n");
					}else{
					g.setColor(path_color);
					g.fillRect(transformX(nodes[node_id1].x) - 3, transformY(nodes[node_id1].y) - 3, 7, 7);
					g.fillRect(transformX(nodes[i].x) - 3, transformY(nodes[i].y) - 3, 7, 7);
					
					if (path != null) {
						for (int j = 1; j < path.length; ++j) {
							Node n1 = nodes[path[j-1]], n2 = nodes[path[j]];
							//把路上所有点都标记出来，如果更细的话，一些Node点并不是全标出来，只是现在简单化，代表城市
							g.fillRect(transformX(n1.x) - 3, transformY(n1.y) - 3, 7, 7);
							g.fillRect(transformX(n2.x) - 3, transformY(n2.y) - 3, 7, 7);
							
							g.drawLine(transformX(n1.x), transformY(n1.y), transformX(n2.x), transformY(n2.y));
						}
					/*****************除到Excel用到的操作*************************/
						//去除路上node相邻重复城市的集合
					markId=solver.deleteMarkRepeatId(path);
					 shortPathName=new Vector<String>();
					//此点所属城市的类别，当然可以精确到乡镇，nodes[node_id1].belongMark，我们精确到市 
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
				MapData.saveFile(set,"中央储备库到受灾点"+marks[nodes[node_id1].belongMark].name+"路径");
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
	/**储备库时间覆盖状况
	 * @param  args1 时间
	 * @param  args2 速度*/
	void StorCover_clicked(String args1,String args2) {
		Graphics g = map_image.getGraphics();
		g.setColor(background);
		g.fillRect(0, 0, map_image_width, map_image_height);//以图片的大小被一个矩形框框的颜色填充
		Mark[] marks =map_data.marks;
		Node[] nodes = map_data.nodes;
	    if(node_id1<0){
        	 JOptionPane.showMessageDialog(null, "没有选择中央储备库，请先选择中央储备库！", "提示",
                     JOptionPane.INFORMATION_MESSAGE, null);
        }
        else if (node_id1>0){
        	if(nodes[node_id1].centerStore!=1)        		
        	{ JOptionPane.showMessageDialog(null, "所选择的城市不是中央储备库，请选择中央储备库！", "提示",
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
    				second = 8 * 60 * 60;    //如果不设时间参数,默认为8小时;
    			} else {
    				second = ReadParam.totalSecone(args1);
    				timeString=ReadParam.totalTime(args1);
    				
    			}
    			if (args2 == null) {
    				speed = 60*1000/3600;    //如果不设速度参数,默认为60km/h;
    			} else {
    				speed = ReadParam.totalSpeed(args2);
    				rateString= ReadParam.rateString(args2);
    			}
    			Vector<Vector> set=new Vector<Vector>();
    			Vector<String> shortPathName=new Vector<String>();
    			String centerStoreName="中央储备库";
    			String cityName="地级市";
    			String dis="中央储备库到地级市之间距离";
    			String city="中央储备库到受灾点经过的城市";
    			shortPathName.addElement(centerStoreName);
    			shortPathName.addElement(cityName);
    			shortPathName.addElement(dis);
    			shortPathName.addElement(city);
    			set.addElement(shortPathName);
               double upperRate=second*speed;  //给定时间窗下、给定速度下中央储备库能够到达的城市集合
               int length;
    			for (int i = 0; i < nodes.length; i++) {   				
    			     /*****************在图上表示出*************************/
    					path = solver.getShortestPath(node_id1,nodes[i].id);
					if (path == null) // 这一句为一个城市和任何城市都没有连接的情况
					{
						centerStoreName = marks[nodes[node_id1].belongMark].name;
						cityName= marks[nodes[i].belongMark].name;
						System.out.print("中央储备库：" + centerStoreName + "与"
								+ cityName + "没有连接\n");
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
    					/*****************除到Excel用到的操作*************************/
    						//去除路上node相邻重复城市的集合
    					markId=solver.deleteMarkRepeatId(path);
    					 shortPathName=new Vector<String>();
    					//此点所属城市的类别，当然可以精确到乡镇，nodes[node_id1].belongMark，我们精确到市 
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
    				MapData.saveFile(set,marks[nodes[node_id1].belongMark].name+timeString+"以"+rateString+"km每小时到达的城市");
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
	/**地级市时间被覆盖状况
	 * @param  args1 时间
	 * @param  args2 速度*/
	void cityBeCovered_clicked(String args1,String args2) {
		Graphics g = map_image.getGraphics();
		g.setColor(background);
		g.fillRect(0, 0, map_image_width, map_image_height);//以图片的大小被一个矩形框框的颜色填充
		Mark[] marks =map_data.marks;
		Node[] nodes = map_data.nodes;
		
        if(node_id1<0){
        	 JOptionPane.showMessageDialog(null, "没有选择城市，请先选择城市！", "提示",
                     JOptionPane.INFORMATION_MESSAGE, null);
        }
        else if  (node_id2 < 0) {
			drawEdges(g);
			 int second;
			 double speed;
		    String timeString = null;
			String rateString = null;
 			if (args1 == null) {
 				second = 8 * 60 * 60;    //如果不设时间参数,默认为8小时;
 			} else {
 				second = ReadParam.totalSecone(args1);
 				timeString=ReadParam.totalTime(args1);
			
			}
			if (args2 == null) {
				speed = 60*1000/3600;    //如果不设速度参数,默认为60km/h;
			} else {
				speed = ReadParam.totalSpeed(args2);
				rateString=ReadParam.rateString(args2);
			}
			Vector<Vector> set=new Vector<Vector>();
			Vector<String> shortPathName=new Vector<String>();
			String cityName="地级市";
			String centerStoreName="中央储备库";
			String dis="中央储备库到地级市之间距离";
			shortPathName.addElement(cityName);
			shortPathName.addElement(centerStoreName);
			shortPathName.addElement(dis);
			set.addElement(shortPathName);
           double upperRate=second*speed;  //给定时间窗下、给定速度下中央储备库能够到达的城市集合
           int length;
			for (int i = 0; i < nodes.length; i++) {
				if (nodes[i].centerStore == 1) {
					/*****************在图上表示出*************************/
					path = solver.getShortestPath(nodes[i].id,node_id1 );
					if(path==null)               //这一句为一个城市和任何城市都没有连接的情况
					{  cityName=marks[nodes[node_id1].belongMark].name;
				      centerStoreName=marks[nodes[i].belongMark].name;
						System.out.print("中央储备库："+centerStoreName+"与"+cityName+"没有连接\n");
					} else {
						length = solver.getShortestPathLength(nodes, path);
						if (length < upperRate) {
					g.setColor(path_color);
					g.fillRect(transformX(nodes[node_id1].x) - 3, transformY(nodes[node_id1].y) - 3, 7, 7);
					g.fillRect(transformX(nodes[i].x) - 3, transformY(nodes[i].y) - 3, 7, 7);
					
					if (path != null) {
						for (int j = 1; j < path.length; ++j) {
							Node n1 = nodes[path[j-1]], n2 = nodes[path[j]];
							//把路上所有点都标记出来，如果更细的话，一些Node点并不是全标出来，只是现在简单化，代表城市
							g.fillRect(transformX(n1.x) - 3, transformY(n1.y) - 3, 7, 7);
							g.fillRect(transformX(n2.x) - 3, transformY(n2.y) - 3, 7, 7);
							
							g.drawLine(transformX(n1.x), transformY(n1.y), transformX(n2.x), transformY(n2.y));
						}
					/*****************除到Excel用到的操作*************************/
						//去除路上node相邻重复城市的集合
					markId=solver.deleteMarkRepeatId(path);
					 shortPathName=new Vector<String>();
					//此点所属城市的类别，当然可以精确到乡镇，nodes[node_id1].belongMark，我们精确到市 
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
				MapData.saveFile(set,marks[nodes[node_id1].belongMark].name+"在"+timeString+"内以"+rateString+"km每小时被那些中央储备库所覆盖");
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
			speed = 60*1000/3600;    //如果不设速度参数,默认为60km/h;
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
			MapData.saveFile(setDistance, "各省市之间的距离（单位m)");
			MapData.saveFile(setTime, "各省市之间的时间（单位s)");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
    System.out.print("各省市之间的距离与时间存储到EXCEL完毕");
	};
	void disPredict_clicked() {
	}

	void pathInterrupt_clicked() {
		
		Graphics g = map_image.getGraphics();
		g.setColor(background);
		g.fillRect(0, 0, map_image_width, map_image_height);//以图片的大小被一个矩形框框的颜色填充
		Mark[] marks =map_data.marks;
		Node[] nodes = map_data.nodes;
		
//		int x0=transformX(min_x);
		int x1=transformX(max_x);
//		int y0=transformY(max_y);
		int y1=transformY(min_y);
		for(int i=0;i<10;i++){
			g.setColor(colors[i]);
			g.drawLine(x1-310, y1+20*i-600, x1-295, y1+20*i-600);
			g.drawString("选取第"+(i+1)+"st条最短路",x1-294,y1+20*i-600);
		}
		
		
        if(node_id1<0){
        	 JOptionPane.showMessageDialog(null, "没有选择城市，请先选择城市！", "提示",
                     JOptionPane.INFORMATION_MESSAGE, null);
        }
        else if  (node_id2 < 0) {
			drawEdges(g);
			Vector<Vector> set=new Vector<Vector>();
			Vector<String> shortPathName=new Vector<String>();
			String length;
			String disName="受灾点";
			String centerStoreName="中央储备库";
			String kil="中央储备库到受灾点的距离";
			String city="中央储备库到受灾点经过的城市";
			shortPathName.addElement(disName);
			shortPathName.addElement(centerStoreName);
			shortPathName.addElement(kil);
			shortPathName.addElement(city);
			set.addElement(shortPathName);

			for (int i = 0; i < nodes.length; i++) {
				if (nodes[i].centerStore == 1) {
					/*****************在图上表示出*************************/

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
					if(path==null)               //这一句为一个城市和任何城市都没有连接的情况
					{  disName=marks[nodes[node_id1].belongMark].name;
				       centerStoreName=marks[nodes[i].belongMark].name;
						System.out.print("中央储备库："+centerStoreName+"与"+disName+"没有连接\n");
					}else{
					g.setColor(Color.RED);
					g.fillRect(transformX(nodes[node_id1].x) - 15, transformY(nodes[node_id1].y) - 15, 30, 30);
					g.setColor(colors[index]);
					g.fillRect(transformX(nodes[i].x) - 3, transformY(nodes[i].y) - 3, 7, 7);
					
					if (path != null) {
						for (int j = 1; j < path.length; ++j) {
							Node n1 = nodes[path[j-1]], n2 = nodes[path[j]];
							//把路上所有点都标记出来，如果更细的话，一些Node点并不是全标出来，只是现在简单化，代表城市
							g.fillRect(transformX(n1.x) - 3, transformY(n1.y) - 3, 7, 7);
							g.fillRect(transformX(n2.x) - 3, transformY(n2.y) - 3, 7, 7);
							
							g.drawLine(transformX(n1.x), transformY(n1.y), transformX(n2.x), transformY(n2.y));
						}
					/*****************除到Excel用到的操作*************************/
						//去除路上node相邻重复城市的集合
					markId=solver.deleteMarkRepeatId(path);
					 shortPathName=new Vector<String>();
					//此点所属城市的类别，当然可以精确到乡镇，nodes[node_id1].belongMark，我们精确到市 
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
				MapData.saveFile(set,"中央储备库到受灾点"+marks[nodes[node_id1].belongMark].name+"路径");
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
	

    /**退出操作*/	
	public void exit() {
	    Object[] options = { "确定", "取消" };
	    JOptionPane pane2 = new JOptionPane("真想退出吗?", JOptionPane.QUESTION_MESSAGE,
	        JOptionPane.YES_NO_OPTION, null, options, options[1]);
	    JDialog dialog = pane2.createDialog(this, "提示");
	    dialog.setVisible(true);
	    Object selectedValue = pane2.getValue();
	    if (selectedValue == null || selectedValue == options[1]) {
	      setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // 这个是关键

	    } else if (selectedValue == options[0]) {
	      setDefaultCloseOperation(EXIT_ON_CLOSE);
	    }
	  }

	
	public void mousePressed(MouseEvent e) {

	}
	public void mouseReleased(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}//鼠标进入时调用
	public void mouseExited(MouseEvent e) {}//鼠标离开时调用
    /**挽救当前图像*/
	public void save(Image image) throws IOException {
		int w = image.getWidth(this);
		int h = image.getHeight(this);

		// 首先创建一个BufferedImage变量，因为ImageIO写图片用到了BufferedImage变量。
		BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_3BYTE_BGR);

		// 再创建一个Graphics变量，用来画出来要保持的图片，及上面传递过来的Image变量
		Graphics g = bi.getGraphics();
		try {
			g.drawImage(image, 0, 0, null);

			// 将BufferedImage变量写入文件中。
			ImageIO.write(bi, "jpg", new File("outImage"+File.separator+"image.jpg"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**保存中央储备库覆盖点的图像*/
	public void saveCover(Image image) {
		Mark[] marks =map_data.marks;
		Node[] nodes = map_data.nodes;
		String cityName="地级市";
		String centerStoreName="中央储备库";
		int w = image.getWidth(this);
		int h = image.getHeight(this);
		// 首先创建一个BufferedImage变量，因为ImageIO写图片用到了BufferedImage变量。
		BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_3BYTE_BGR);
		// 再创建一个Graphics变量，用来画出来要保持的图片，及上面传递过来的Image变量
		Graphics g = bi.getGraphics();
		g.setColor(background);
		g.fillRect(0, 0, map_image_width, map_image_height);//以图片的大小被一个矩形框框的颜色填充
		//此循环表示在地图上的中央储备库和地级市的点（地名省去）
		for (int i = 0; i < marks.length; i++) {
			Mark m = marks[i];
			int x = transformX(m.x);
			int y = transformY(m.y);
			//Mark加入centerStore后进行的操作
			if (m.centerStore == 1) {
				g.setColor(markCenterStore_color);// 中央储备库地名小矩形的颜色
				g.drawRect(x - 3, y - 3, 7, 7);// x-1,y-1是绘制矩形的坐标，3，3是绘制矩形的宽度和高度
				g.setColor(foreCenterStoreground);// 地名标签的颜色
				g.drawString(m.name, x + 2, y - 2);// 在x+2，y-2的地方显示标签

			}else{
				g.setColor(mark_color);// 地名小矩形的颜色
				g.drawRect(x - 1, y - 1, 3, 3);// x-1,y-1是绘制矩形的坐标，3，3是绘制矩形的宽度和高度
			}
		}
		//画中央储备库和地级市之间的线 i代表城市；j代表中央储备库；
		for (int i = 0; i < nodes.length; i++) {
			int length=Integer.MAX_VALUE;
			int centerId=Integer.MAX_VALUE;
			for (int j = 0; j < nodes.length; j++) {
				if (nodes[j].centerStore == 1) {
					/***************** 在图上表示出 *************************/
					path = solver.getShortestPath(nodes[i].id, nodes[j].id);
					if (path == null) // 这一句为一个城市和任何城市都没有连接的情况
					{
						cityName = marks[nodes[i].belongMark].name;
						centerStoreName = marks[nodes[j].belongMark].name;
						System.out.print("中央储备库：" + centerStoreName + "与"
								+ cityName + "没有连接\n");
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
			
		//存入图片
		try {

			// 将BufferedImage变量写入文件中。
			ImageIO.write(bi, "jpg", new File("outImage"+File.separator+"coverImage.jpg"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		g.dispose();   //销毁程序中指定的图形界面资源，对数据资源不产生影响

		
	}
	/**保存二级中央储备库覆盖点的图像及存储数据*/
	public void saveTwoCover(Image image) {
		Mark[] marks =map_data.marks;
		Node[] nodes = map_data.nodes;
		String centerStoreName="中央储备库";
		Vector<Vector> set=new Vector<Vector>();
		Vector<String> name=new Vector<String>();
		String cityName="地级市";
		String centerOne="一级中央储备库";
		String centerTwo="二级中央储备库";
		String pn="人口数(万人)";
		String gdp="GDP(亿元)";
		name.addElement(cityName);
		name.addElement(centerOne);
		name.addElement(centerTwo);
		name.addElement(pn);
		name.addElement(gdp);
		set.addElement(name);
		int w = image.getWidth(this);
		int h = image.getHeight(this);
		// 首先创建一个BufferedImage变量，因为ImageIO写图片用到了BufferedImage变量。
		BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_3BYTE_BGR);
		// 再创建一个Graphics变量，用来画出来要保持的图片，及上面传递过来的Image变量
		Graphics g = bi.getGraphics();
		g.setColor(background);
		g.fillRect(0, 0, map_image_width, map_image_height);//以图片的大小被一个矩形框框的颜色填充
		//此循环表示在地图上的中央储备库和地级市的点（地名省去）
		for (int i = 0; i < marks.length; i++) {
			Mark m = marks[i];
			int x = transformX(m.x);
			int y = transformY(m.y);
			//Mark加入centerStore后进行的操作
			if (m.centerStore == 1) {
				g.setColor(markCenterStore_color);// 中央储备库地名小矩形的颜色
				g.drawRect(x - 3, y - 3, 7, 7);// x-1,y-1是绘制矩形的坐标，3，3是绘制矩形的宽度和高度
				g.setColor(foreCenterStoreground);// 地名标签的颜色
				g.drawString(m.name, x + 2, y - 2);// 在x+2，y-2的地方显示标签

			}else{
				g.setColor(mark_color);// 地名小矩形的颜色
				g.drawRect(x - 1, y - 1, 3, 3);// x-1,y-1是绘制矩形的坐标，3，3是绘制矩形的宽度和高度
			}
		}
		//画中央储备库和地级市之间的线 i代表城市；j代表中央储备库；
		for (int i = 0; i < nodes.length; i++) {
			int centerId1=Integer.MAX_VALUE;
			int centerId2=Integer.MAX_VALUE;
			NodeLength n = null;
			Vector<NodeLength> v=new Vector<NodeLength>();
			for (int j = 0; j < nodes.length; j++) {
				if (nodes[j].centerStore == 1) {
					/***************** 在图上表示出 *************************/
					path = solver.getShortestPath(nodes[i].id, nodes[j].id);
					if (path == null) // 这一句为一个城市和任何城市都没有连接的情况
					{
						cityName = marks[nodes[i].belongMark].name;
						centerStoreName = marks[nodes[j].belongMark].name;
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
				//找到前面的最小的两个长度的元素
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
			MapData.saveFile(set,"地级市的联合救援");
		} catch (Exception e) {
			e.printStackTrace();
		}
		//存入图片
		try {

			// 将BufferedImage变量写入文件中。
			ImageIO.write(bi, "jpg", new File("outImage"+File.separator+"coverTwoImage.jpg"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		g.dispose();   //销毁程序中指定的图形界面资源，对数据资源不产生影响
      System.out.print("存储完成");
		
	}	
	
	/**进行聚类*/
	public void cluster(Image image){
		
		Graphics g = image.getGraphics();
		g.setColor(background);
		g.fillRect(0, 0, map_image_width, map_image_height);//以图片的大小被一个矩形框框的颜色填充
	
		int x0=transformX(min_x);
		int x1=transformX(max_x);
		int y0=transformY(max_y);
		int y1=transformY(min_y);
		//顶点部分为(x0-5,y1+5)
		
		// 设置画笔颜色
		g.setColor(Color.BLUE);
		// 画坐标
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
			//Mark加入centerStore后进行的操作
				g.setColor(colors[m.category]);// 地名小矩形的颜色
				g.drawRect(x - 1, y - 1, 3, 3);// x-1,y-1是绘制矩形的坐标，3，3是绘制矩形的宽度和高度
				g.setColor(foreground);// 地名标签的颜色
				//g.drawString(m.name, x + 2, y - 2);// 在x+2，y-2的地方显示标签
		}
	
		repaint();
		System.out.print("分区图片优化完毕，可以保存！");
	
	}

	/**根据图片进行分区 */
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
		g.fillRect(0, 0, map_image_width, map_image_height);//以图片的大小被一个矩形框框的颜色填充
		
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
//		/**进行交叉验证，选取最优的C和g*/
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
		System.out.print("分区图片优化完毕，可以保存！");
	
	}
	/**调整图形的分辨率*/
	public void piexImage(){
		
	}
	public static void main(String[] argv)
	{ 
		     MapFrame m=new MapFrame("中科院研究生院应急管理中心");
		     //m.setIconImage(Image);
		     /**建立的Frame可视化，否则createMapImage()创建map_image的时候为空*/	     
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