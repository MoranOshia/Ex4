package GUI;

import java.awt.Color;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileNameExtensionFilter;

import Algorithm.Algorithm;
import Coords.GeoBox;
import Coords.LatLonAlt;
import Coords.Map;
import Coords.MyCoords;
import Geom.Point3D;
import Robot.Fruit;
import Robot.Game;
import Robot.Packman;
import Robot.Play;
import Sql.SqlGame;
import Thread.ThreadPlay;
import Thread.TreadPlayAutomatic;
/*
 * This class is responsible for the GUI of the game.
 */

public class window extends JFrame implements MouseListener{

	public BufferedImage myImage, cherryImg, packmanImg, ghostImg, playerImg;
	MenuBar menuBar;
	Menu menu;
	Point3D  pPlayer = new Point3D(0, 0); 
	String type="";
	Play play1 = new Play();
	public Game game = new Game();
	public Game gameCopy;
	int w ;
	int h;

	boolean first = true;
	double azimuth = 0;



	/*
	 * This function is sent the menu and add the listener to the mouse in the frame.
	 */
	public window() {
		initGUI();
		this.addMouseListener(this);
	}
	/*
	 * This function is setting the menu of the game.
	 */
	public void initGUI() {
		Menu menu = new Menu("Menu"); 
		Menu clear = new Menu ("Clear");
		Menu run = new Menu ("Run");
		MenuItem openCsv = new MenuItem("Open Csv");
		MenuItem clearGame = new MenuItem("Clear Game");
		MenuItem play = new MenuItem("Play");
		MenuItem playAutomatic = new MenuItem("Play automaic");
		MenuItem setPlayer = new MenuItem("set Player");


		MenuBar menuBar = new MenuBar();

		this.setMenuBar(menuBar);
		menuBar.add(menu);
		menuBar.add(run);
		menuBar.add(clear);
		clear.add(clearGame);
		menu.add(openCsv);
		menu.add(setPlayer);
		run.add(play);
		run.add(playAutomatic);



		try {
			myImage = ImageIO.read(new File("Ariel1.png"));
			//			cherryImg = ImageIO.read(new File("cherry.png"));
			//			packmanImg = ImageIO.read(new File("packman.png"));
			//			ghostImg = ImageIO.read(new File("ghost.png"));
			//			playerImg = ImageIO.read(new File("player.png"));
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		/*
		 * This function is running the game by Clicks of the player on the frame.
		 * @param ActionListener
		 */
		play.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				play();
			}
		});

		/*
		 * Set the Player to know where to locate him on the frame.
		 * @param ActionListener
		 */
		setPlayer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				type="M";
			}
		});
		/*
		 * Open a frame to choose a csv file.
		 * @param ActionListener
		 */

		openCsv.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				JFrame frame = new JFrame();
				JFileChooser chooser= new JFileChooser();
				chooser.setAcceptAllFileFilterUsed(false);
				FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV file", "csv");
				chooser.setFileFilter(filter);
				int returnVal = chooser.showOpenDialog(frame);
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					System.out.println("You chose to open this file: " +
							chooser.getSelectedFile());
				}
				String NameFile=""+chooser.getSelectedFile();
				//check if we need
				play1 = new Play(NameFile);
				play1.setIDs(305050437,313292633);
				game=new Game(NameFile);
				gameCopy = new Game(game);
				repaint();

			}
		});
		/*
		 * This function is running the game automatically.
		 * @param ActionListener
		 */
		playAutomatic.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				playAot();

			}
		});
		/*
		 *  This function is clear the board.
		 * @param ActionListener 
		 */
		clearGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				game.clear();
			}
		});
	}

	/*
	 * Get function for azimuth 
	 * @return azimuth - the angle between 2 points.
	 */
	public double getAzimuth() {
		return azimuth;
	}
	/*
	 * Set function for azimuth get an azimuth value and set him as the azimuth of the class 
	 * @param azimuth
	 */
	public void setAzimuth(double azimuth) {
		this.azimuth = azimuth;
	}
	/**
	 * This function in painting the game for the csv the user chose to the board.
	 * @param g
	 */
	public void paint(Graphics g)
	{


		int w = this.getWidth();
		int h = this.getHeight();

		g.drawImage(myImage,0, 0, w, h, this);

		g.setColor(Color.pink);
		Point3D m = Map.coordsToPixel(w, h, game.getPlayer().getLocation().x(), game.getPlayer().getLocation().y());
		g.fillOval((int)m.x(),(int) m.y(), 30, 30);



		for (int i = 0; i < game.getRobots().size(); i++) {
			Point3D p = new Point3D( Map.coordsToPixel(w, h, game.getPackman(i).getLocation().lat(), game.getPackman(i).getLocation().lon()));

			g.setColor(Color.yellow);
			g.fillOval((int)p.x(), (int)p.y(), 30, 30);
		}

		for (int i = 0; i < game.getGhosts().size(); i++) {
			Point3D p = new Point3D( Map.coordsToPixel(w, h, game.getGhosts(i).getLocation().lat(), game.getGhosts(i).getLocation().lon()));

			g.setColor(Color.RED);
			g.fillOval((int)p.x(), (int)p.y(), 30, 30);
		}

		for (int i = 0; i < game.getTargets().size(); i++) {
			Point3D p = new Point3D( Map.coordsToPixel(w, h, game.getTarget(i).getLocation().lat(), game.getTarget(i).getLocation().lon()));

			g.setColor(Color.green);
			g.fillOval((int)p.x(), (int)p.y(), 10, 10);
		}

		for (int i = 0; i < game.sizeB(); i++) {
			Point3D pMax = new Point3D( Map.coordsToPixel(w, h, game.getBox(i).getMax().lat(), game.getBox(i).getMax().lon()));
			Point3D pMin = new Point3D( Map.coordsToPixel(w, h, game.getBox(i).getMin().lat(), game.getBox(i).getMin().lon()));

			g.setColor(Color.black);
			g.fillRect((int)pMin.x(), (int)pMax.y(), (int)Math.abs(pMax.x()-pMin.x()),(int)Math.abs(pMax.y()-pMin.y()));
		}

		String s =play1.getStatistics();
		g.setColor(Color.white);
		g.setFont(new Font("Arial Rounded MT Bold", Font.PLAIN, 20)); 
		g.drawString(s, 10, h-10);
//		SqlGame sg= new SqlGame(s, 1);

	}

	@Override
	public void mouseClicked(MouseEvent arg) {
		
		System.out.println("mouse Clicked");
		System.out.println("("+ arg.getX() + "," + arg.getY() +")");

		if(type.equals("M") && first == true) {
			pPlayer = new Point3D(arg.getX(), arg.getY());
			Point3D p = new Point3D( Map.PixelToCoords(this.getWidth(), this.getHeight(), pPlayer.x(), pPlayer.y()));
			play1.setInitLocation(p.x(), p.y());
			game.getPlayer().setLocation(new LatLonAlt(p.x(), p.y(), p.z()));
			first = false;

		}
		this.azimuth = azimuth(arg.getX(), arg.getY());
		repaint();	



	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		

	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}
	/*
	 * Get function for the width of the frame 
	 * @return this.getWidth() the width of the frame.
	 */
	public int getW()
	{
		return this.getWidth();
	}
	/*
	 * Get function for the height of the frame 
	 * @return this.getHeight() the height of the frame.
	 */
	public int getH()
	{
		return this.getHeight();
	}
	/*
	 * This function get x,y the location of the point the player want to get in the board.
	 * and calculate the angle between them.
	 * @param x lacation x
	 * @param y lacation y
	 * @return ans - the angle
	 */
	public double azimuth(double x, double y) {
		Point3D p = new Point3D(Map.PixelToCoords(this.getWidth(), this.getHeight(), x, y));
		MyCoords m = new MyCoords();
		double ans = m.azimuth_elevation_dist(game.getPlayer().getLocation(), p)[0];
		return ans;
	}
	/*
	 * the function is responsible for the Thread on the automatic play for the game.
	 */
	public void playAot() {

		Algorithm a = new Algorithm(play1, this.game, this.gameCopy, getW(), getH());

		TreadPlayAutomatic tra = new TreadPlayAutomatic(this.play1,this.game,this.gameCopy,this,a);
		tra.start();

	}
	/*
	 * the function is responsible for the Thread on the user control play in the game.
	 */
	public void play() {

		ThreadPlay tr = new ThreadPlay(this.play1,this.game,this.gameCopy,this);
		tr.start();

	}
	
}
