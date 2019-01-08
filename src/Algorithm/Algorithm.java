package Algorithm;
import java.util.ArrayList;

import Coords.GeoBox;
import Coords.LatLonAlt;
import GUI.Map;
import GUI.MyCoords;
import GUI.window;
import Geom.Point3D;
import Robot.Fruit;
import Robot.Game;
import Robot.Packman;
import Robot.Play;
import graph.Graph;
import graph.Node;


public class Algorithm {

	//ArrayList<Fruit> fruit=new ArrayList<Fruit>();
	ArrayList<Point3D> pointsBoxes= new ArrayList<Point3D>();
	Play play;
	Game game;
	private Game gameCopy;
	int w,h;

	public Algorithm() {
		pointsBoxes= new ArrayList<Point3D>();
		play=new Play();
		game=new Game();
		gameCopy=new Game();
		w=0;
		h=0;
	}
	public Algorithm(Play play,Game gam, Game copy,int mapWidth, int mapHeight) {
		ArrayList<Point3D> pointsBoxes= new ArrayList<Point3D>();
		this.play = play;
		this.game = gam;
		this.gameCopy = copy;
		this.w=mapWidth;
		this.h=mapHeight;
	}

	public void GetpointsOfBoxes()
	{
		GeoBox box;
		Point3D p=new Point3D(0,0,0);
		pointsBoxes.add(p);
		for (int i = 0; i < gameCopy.sizeB(); i++) {

			box=new GeoBox(gameCopy.getBox(i));
			p=new Point3D(box.getMin().x()+5, box.getMax().y()+5);
			pointsBoxes.add(p);
			p=new Point3D(box.getMin().x()+5, box.getMin().y()+5);
			pointsBoxes.add(p);
			p=new Point3D(box.getMax().x()+5, box.getMax().y()+5);
			pointsBoxes.add(p);
			p=new Point3D(box.getMax().x()+5, box.getMin().y()+5);
			pointsBoxes.add(p);

		}
	}
	public Point3D closesFruit(Game game)
	{
		Point3D pFruit=new Point3D(game.getTarget(0).getLocation().lat(),game.getTarget(0).getLocation().lon());
		Point3D pPlayer=new Point3D(game.getPlayer().getLocation().lat(),game.getPlayer().getLocation().lon());
		double minDis=distanceMeter(pPlayer,pFruit);
		double tempDis=0;

		for (int i = 1; i <game.sizeT(); i++) {

			tempDis=distanceMeter(pPlayer,new Point3D(game.getTarget(i).getLocation().lat(),game.getTarget(i).getLocation().lon()));
			if(tempDis<minDis) {
				minDis=tempDis;
				pFruit=new Point3D(game.getTarget(i).getLocation().lat(),game.getTarget(i).getLocation().lon());
			}
		}		
		return pFruit;
	}
	public ArrayList<Point3D> createPath()
	{
		ArrayList<Point3D> path=new ArrayList<Point3D>();
		game=updateGame(play.getBoard());
		Graph G = new Graph(); 
		String source = "player";
		String target = "fruit";
		G.add(new Node(source));
		Point3D pPlayer=new Point3D(game.getPlayer().getLocation().lat(),game.getPlayer().getLocation().lon());
		//createEdges(G,source,pPlayer);
		for(int i=0;i<pointsBoxes.size();i++) {

			Node d = new Node(""+i);
			G.add(d);

		}
		G.add(new Node(target)); 
		createEdges(G,source,pPlayer);
		createEdges(G,target,pPlayer);
		for(int i=0;i<pointsBoxes.size();i++) {

			createEdges(G,""+i,this.pointsBoxes.get(i));
		}

		return path;

	}
	public void createEdges(Graph g,String start,Point3D p)
	{
		ArrayList<Integer> pointPlayerSee =new ArrayList<Integer>();
		pointPlayerSee=pointsPlayerSee(p, this.pointsBoxes);
		if(pointPlayerSee==null)
		{
			return;
		}
		else {
			for (int i = 0; i <pointPlayerSee.size(); i++) {
				
				g.addEdge(start,""+pointPlayerSee.get(i),p.distance2D(this.pointsBoxes.get(pointPlayerSee.get(i))));
				
			}
		}
	}
	public double distanceMeter(Point3D p1,Point3D p2)
	{
		double dis=0;
		Point3D p1C=new Point3D(Map.PixelToCoords(w,h, p1.x(), p1.y()));
		Point3D p2C=new Point3D(Map.PixelToCoords(w,h, p2.x(), p2.y()));
		MyCoords m=new MyCoords();
		dis=m.distance3d(p1C, p2C);

		return dis;
	}
	public Game updateGame(ArrayList<String> board_data)
	{
		Game g=new Game();
		for (int i = 0; i < board_data.size(); i++) {

			if(board_data.get(i).charAt(0) == 'P') {
				Packman p = new Packman(board_data.get(i));
				g.add(p);
			}

			if(board_data.get(i).charAt(0) == 'G') {
				Packman b = new Packman(board_data.get(i));
				g.addGhost(b);
			}
			if(board_data.get(i).charAt(0) == 'M') {
				Packman m = new Packman(board_data.get(i));
				g.setPlayer(m);
			}

			if(board_data.get(i).charAt(0) == 'F') {
				Fruit f  = new Fruit(board_data.get(i));
				g.add(f);
			}
			for (int j = 0; j < gameCopy.sizeB(); j++) {
				g.add(gameCopy.getBox(j));
			}
		}
		this.game=new Game(g);
		return g;
	}
	private ArrayList<Integer> pointsPlayerSee(Point3D player,ArrayList<Point3D> pointsBoxes) {

		ArrayList<Integer> pointPlayerSee =new ArrayList<Integer>();
		this.game=updateGame(play.getBoard());
		for (int i = 0; i < pointsBoxes.size(); i++) {
			boolean flag=true;
			for(int j =0; j<game.sizeB();j++)
			{

				if(canISee(game.getBox(j),player,pointsBoxes.get(i))==false)
				{	
					flag=false;	
				}
			}
			if(flag==true)
			{
				pointPlayerSee.add(i);
			}
		}


		return pointPlayerSee;
	}
	private boolean canISee(GeoBox box, Point3D player,Point3D target) {

		double yPlayer = player.y();
		double yTarg = target.y();
		double xPlayer = player.x();
		double xTarg = target.x();

		double m = (yTarg - yPlayer) / (xTarg - xPlayer);
		double n = yTarg - (m * xTarg);

		if (xPlayer <= box.getMin().x() && box.getMin().x()  <= xTarg || xTarg <= box.getMin().x()  && box.getMin().x()  <= xPlayer) {


			double y = m * (box.getMin().x() ) + n;

			if (box.getMin().y() <= y && y <= box.getMax().y()|| box.getMax().y() <= y && y <= box.getMin().y()) {
				return false;
			}
		}

		else 
		{
			if (yPlayer <= box.getMin().y() && box.getMin().y() <= yTarg || yTarg <= box.getMin().y() && box.getMin().y() <= yPlayer) {

				double x = (box.getMin().y() - n) / m;
				if (box.getMin().x()  <= x && x <=box.getMax().x()  || box.getMax().x() <= x && x <= box.getMin().x()) {
					return false;
				}
			}
		}
		return true;
	}


}



