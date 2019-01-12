package Thread;

import java.util.ArrayList;

import Algorithm.Algorithm;
import GUI.Map;
import GUI.window;
import Geom.Point3D;
import Robot.Fruit;
import Robot.Game;
import Robot.Packman;
import Robot.Play;

/*
 * This class is responsible for the Thread of the automatic play.
 */
public class TreadPlayAutomatic extends Thread{

	public Play play1 ;
	public Game game ;
	public Game gameCopy ;
	public window w;
	public Algorithm a;
	public ArrayList<Point3D> path;
	/*
	 * Copy constructor for values
	 *  @param pl - current play object
	 *  @param gam- current game object
	 *  @param copy-copy of the game
	 *  @param window- the current window of the game
	 *  @ a - the current algorithm of the game
	 */
	public TreadPlayAutomatic(Play pl, Game gam, Game copy, window w, Algorithm a) {
		this.play1 = pl;
		this.game = gam;
		this.gameCopy = copy;
		this.w = w;
		this.a = a;
		this.path = new ArrayList<Point3D>();
	}
	/*
	 *This function is responsible for running the game automatically with the Threads, and updating the locations of the game
	 */
	public void run() {

		play1.start();
		while(play1.isRuning() && this.w.game.getTargets().size() != 0) {
			Point3D fruit = new Point3D(a.closesFruit(this.w.game));
			fruit = new Point3D(Map.coordsToPixel(this.w.getW(),this.w.getH() , fruit.x(), fruit.y()));
			System.out.println("fruit; "+fruit);
			path = new ArrayList<Point3D>(a.createPath(fruit, this.w.game));
			path.add(fruit);
			System.out.println("size:"+path.size());
			for (int i = 0; i < path.size() ; i++) {
				System.out.println("path1: "+path.get(i));
				this.w.setAzimuth(this.w.azimuth(path.get(i).x(), path.get(i).y())); 
				play1.rotate(this.w.getAzimuth());this.w.game = new Game();
				ArrayList<String> board_data = play1.getBoard();
				for (int i1 = 0; i1 < board_data.size(); i1++) {

					if(board_data.get(i1).charAt(0) == 'P') {
						Packman p = new Packman(board_data.get(i1));
						this.w.game.add(p);
					}

					if(board_data.get(i1).charAt(0) == 'G') {
						Packman g = new Packman(board_data.get(i1));
						this.w.game.addGhost(g);
					}
					if(board_data.get(i1).charAt(0) == 'M') {
						Packman m = new Packman(board_data.get(i1));
						this.w.game.setPlayer(m);
					}

					if(board_data.get(i1).charAt(0) == 'F') {
						Fruit f  = new Fruit(board_data.get(i1));
						this.w.game.add(f);
					}
					for (int j = 0; j < gameCopy.sizeB(); j++) {
						this.w.game.add(gameCopy.getBox(j));
					}
				}

				this.w.repaint();
				try {
					sleep(50);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}



