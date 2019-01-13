package Algorithm;
import java.util.ArrayList;


import Coords.GeoBox;
import Coords.LatLonAlt;
import Coords.Map;
import Coords.MyCoords;
import GUI.window;
import Geom.Point3D;
import Robot.Fruit;
import Robot.Game;
import Robot.Packman;
import Robot.Play;
import graph.Graph;
import graph.Graph_Algo;
import graph.Node;
/*
 * This class is responsible for the algorithm of the automatic play 
 */

public class Algorithm {

	ArrayList<Point3D> pointsBoxes= new ArrayList<Point3D>();
	Play play;
	Game game;
	private Game gameCopy;
	int w,h;
	/*
	 * Default constructor
	 */
	public Algorithm() {
		pointsBoxes= new ArrayList<Point3D>();
		play=new Play();
		game=new Game();
		gameCopy=new Game();
		w=0;
		h=0;
	}
	/*
	 * Copy constructor
	 *  @param play - current play object
	 *  @param gam- current game object
	 *  @param copy-copy of the game
	 *  @param mapWidth -the width of the current game
	 *  @param mapHeight-the height of the current game
	 */
	public Algorithm(Play play,Game gam, Game copy,int mapWidth, int mapHeight) {

		this.play = play;
		this.game = gam;
		this.gameCopy = new Game(copy);
		this.w=mapWidth;
		this.h=mapHeight;

		pointsBoxes= new ArrayList<Point3D>();
		GetpointsOfBoxes(copy);
	}
	/*
	 * Get play function.
	 * @return play - the play object
	 */
	public Play getPlay() {
		return play;
	}
	/*
	 * Set play function.
	 * @param play- the play we want to set to the algorithm
	 */
	public void setPlay(Play play) {
		this.play = play;
	}
	/*
	 * The function is responsible for insert close points to the corners of the boxes for the game we get to the value
	 * of the variable pointsBoxes of the class.
	 * @param - c current game
	 */
	public void GetpointsOfBoxes(Game c)
	{

		Point3D p=new Point3D(0,0,0);
		for (int i = 0; i < c.sizeB(); i++) {

			Point3D pMax = new Point3D(Map.coordsToPixel(w, h, c.getBox(i).getMax().x(), c.getBox(i).getMax().y()));
			Point3D pMin = new Point3D( Map.coordsToPixel(w, h, c.getBox(i).getMin().x(), c.getBox(i).getMin().y()));
			p=new Point3D(pMin.x()-10, pMax.y()-10);

			pointsBoxes.add(p);
			p=new Point3D(pMin.x()-10, pMin.y()+10);

			pointsBoxes.add(p);
			p=new Point3D(pMax.x()+10, pMax.y()-10);
			pointsBoxes.add(p);

			p=new Point3D(pMax.x()+10, pMin.y()+10);
			pointsBoxes.add(p);
		}
	}
	/*
	 * The function is responsible for return the closes fruit to the player in the current game we are getting in the function.
	 * @param  game- current game 
	 * @return pFruit - the closes fruit to the player
	 */
	public Point3D closesFruit(Game game)
	{

		Point3D pFruit=new Point3D(game.getTarget(0).getLocation().lat(),game.getTarget(0).getLocation().lon());
		Point3D pPlayer=new Point3D(game.getPlayer().getLocation().lat(),game.getPlayer().getLocation().lon());
		MyCoords m=new MyCoords();
		double minDis=m.distance3d(pPlayer,pFruit);
		double tempDis=0;

		for (int i = 1; i <game.sizeT(); i++) {

			tempDis=m.distance3d(pPlayer,new Point3D(game.getTarget(i).getLocation().lat(),game.getTarget(i).getLocation().lon()));
			if(tempDis<minDis) {
				minDis=tempDis;
				pFruit=new Point3D(game.getTarget(i).getLocation().lat(),game.getTarget(i).getLocation().lon());
			}
		}		
		return pFruit;
	}
	/*
	 * The function is responsible creating the path the fruit that the most close to the player by the algorithm "Dijkstra".
	 * @param  windowG- current game 
	 * @param pFruit - the closes fruit to the player
	 * @return path - the path to the fruit
	 */
	public ArrayList<Point3D> createPath(Point3D pFruit, Game windowG)
	{
		ArrayList<Point3D> path=new ArrayList<Point3D>();
		game=new Game(windowG);
		Graph G = new Graph();
		String source = "player";
		String target = "fruit";
		G.add(new Node(source));
		Packman pack = new Packman(play.getBoard().get(0));
		Point3D pPlayer=new Point3D( pack.getLocation().x(), pack.getLocation().y());
		pointsBoxes.add(pFruit);

		for(int i=0;i<pointsBoxes.size()-1;i++) {
			Node d = new Node(""+i);
			G.add(d);
		}
		G.add(new Node(target)); 
		createEdges(G,source,pPlayer);

		for(int i=0;i<pointsBoxes.size()-1;i++) {
			createEdges(G,""+i,this.pointsBoxes.get(i));
		}


		Graph_Algo.dijkstra(G, source);


		Node b = G.getNodeByName(target);
		//System.out.println(b);
		ArrayList<String> shortestPath = b.getPath();
		for(int i=1;i<shortestPath.size();i++) {
			
			path.add(pointsBoxes.get(Integer.parseInt(shortestPath.get(i))));

		}
		pointsBoxes.remove(pFruit);
		return path;

	}
	/*
	 * This function is responsible for creating edges to all the points of the boxes and the player and the fruits.
	 * all by getting the graph, the string of the point and the point.
	 * @param Graph G - the graph for the game
	 * @param String start - the name of the point we check
	 * @param Point3D p - the point we create edges for.
	 */

	public void createEdges(Graph G,String start,Point3D p)
	{
		ArrayList<Integer> pointPlayerSee ;
		pointPlayerSee= new ArrayList<Integer>(pointsPlayerSee(p, this.pointsBoxes));
		if(pointPlayerSee!=null) {
			for (int i = 0; i <pointPlayerSee.size(); i++) {
				if(pointPlayerSee.get(i)==(pointsBoxes.size()-1)) {
					G.addEdge(start,"fruit",distanceMeter(p,pointsBoxes.get(pointPlayerSee.get(i))));
				}
				else {
					G.addEdge(start,""+pointPlayerSee.get(i),distanceMeter(p,pointsBoxes.get(pointPlayerSee.get(i))));
				}
			}
		}
	}
	/*
	 * This function is calculating distance of two point by meters.
	 * @param p1- first point
	 * @param p2-second point 
	 * @return dis- the distance between them.
	 */
	public double distanceMeter(Point3D p1,Point3D p2)
	{
		double dis=0;
		Point3D p1C=new Point3D(Map.PixelToCoords(w,h, p1.x(), p1.y()));
		Point3D p2C=new Point3D(Map.PixelToCoords(w,h, p2.x(), p2.y()));
		MyCoords m=new MyCoords();
		dis=m.distance3d(p1C, p2C);
		return dis;
	}
	/*
	 * This function is updating the game by the board data (ArrayList of String for the Play object)
	 * @param ArrayList<String> board_data -board data for play
	 * @return g- the update game.
	 */
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
	/*
	 * This function is making a ArrayList<Integer> which had the points that the player can see i.e. 
	 * that not blocked by the boxes.
	 * @param Point3D player - the point of the player
	 * @param ArrayList<Point3D> pointsBoxes - the points of the boxes
	 * return pointPlayerSee- array of the index of the point that the player sees.
	 */
	private ArrayList<Integer> pointsPlayerSee(Point3D player,ArrayList<Point3D> pointsBoxes) {

		ArrayList<Integer> pointPlayerSee =new ArrayList<Integer>();
		for (int i = 0; i < pointsBoxes.size(); i++) {
			boolean flag=true;
			for(int j =0; j<gameCopy.sizeB();j++)
			{
				if(CheckisBoxBlock(gameCopy.getBox(j),player,pointsBoxes.get(i))==false)
				{	
					flag=false;
				}
			}

			if(flag==true) pointPlayerSee.add(i);
		}
		return pointPlayerSee;
	}

	/*
	 * This function is return true or false if a specific box is blocking point to other point.
	 * @param GeoBox box- box we check if block
	 * @param Point3D player - first point
	 * @param Point3D target - second point
	 * @return true/false if blocked.
	 */
	public boolean CheckisBoxBlock(GeoBox box,Point3D player,Point3D target){

		double yPlayer = player.y();
		double yTarg = target.y();
		double xPlayer = player.x();
		double xTarg = target.x();

		double m = (yTarg - yPlayer) / (xTarg - xPlayer);
		double n = yTarg - (m * xTarg);

		Point3D pMax = new Point3D(Map.coordsToPixel(w, h, box.getMax().x(), box.getMax().y()));
		Point3D pMin = new Point3D( Map.coordsToPixel(w, h, box.getMin().x(), box.getMin().y()));
		Point3D pMaxMin=new Point3D(pMax.x(), pMin.y());
		Point3D pMinMax=new Point3D(pMin.x(), pMax.y());

		//pMin pMinMax
		if (xPlayer <=pMin.x() && pMin.x()  <= xTarg || xTarg <= pMin.x()  && pMin.x()  <= xPlayer) {

			double y = m * (pMin.x()) + n;

			if (pMin.y() >= y && y >= pMinMax.y()|| pMinMax.y() <= y && y <= pMin.y()) {
				return false;
			}
		}


		//pMax pMaxMin
		if (xPlayer <=pMax.x() && pMax.x()  <= xTarg || xTarg <= pMax.x()  && pMax.x()  <= xPlayer) {

			double y = m * (pMax.x()) + n;

			if (pMax.y() <= y && y <= pMaxMin.y()|| pMaxMin.y() <= y && y <= pMax.y()) {
				return false;
			}
		}

		//pMin pMaxMin
		if (yPlayer <= pMin.y() && pMin.y() <= yTarg || yTarg <= pMin.y() && pMin.y() <= yPlayer) {

			double x = (pMin.y() - n) / m + xPlayer;
			if (pMin.x()  <= x && x <=pMaxMin.x()  || pMaxMin.x() <= x && x <= pMin.x()) {
				return false;
			}
		}

		//pMax pMinMax point
		if (yPlayer <= pMax.y() && pMax.y() <= yTarg || yTarg <= pMax.y() && pMax.y() <= yPlayer) {

			double x = (pMax.y() - n) / m + xPlayer;
			if (pMax.x()  >= x && x >=pMinMax.x()  || pMinMax.x() <= x && x <= pMax.x()) {
				return false;
			}
		}

		return true;
	}

//	public static void main(String[] args) {
//
//		Algorithm a =new Algorithm();
//		Play p=new Play("C:\\Users\\moran\\eclipse-workspace\\Ex4_v0.2\\Ex4_OOP\\data\\Ex4_OOP_example5.csv");
//		Game g=new Game("C:\\Users\\moran\\eclipse-workspace\\Ex4_v0.2\\Ex4_OOP\\data\\Ex4_OOP_example5.csv");
//	
//		a =new Algorithm(p,g,g,1433,642);
//		//Point3D fruit = new Point3D(g.getTarget(0));
//		fruit = new Point3D(Map.coordsToPixel(1433,642,fruit.x(), fruit.y()));
//		 ArrayList<Point3D> path = new ArrayList<Point3D>(a.createPath(fruit, g));
//		
//		
//	}
}



