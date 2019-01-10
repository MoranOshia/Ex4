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
import Geom.Point3D;
import Robot.Fruit;
import Robot.Game;
import Robot.Packman;
import Robot.Play;


public class window extends JFrame implements MouseListener{

	public BufferedImage myImage, cherryImg, packmanImg, ghostImg, playerImg;
	MenuBar menuBar;
	Menu menu;
	Point3D  pPlayer = new Point3D(0, 0); 
	String type="";
	Play play1 = new Play();
	Game game = new Game();
	int w ;
	int h;
	
	boolean first = true;
	double azimuth = 0;




	public window() {
		initGUI();
		this.addMouseListener(this);
	}

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

		play.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				t();
			}
		});


		setPlayer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				type="M";
			}
		});


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
				repaint();

			}
		});

		playAutomatic.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				playAot();

			}
		});
		
		clearGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				game.clear();
			}
		});
	}


	public double getAzimuth() {
		return azimuth;
	}

	public void setAzimuth(double azimuth) {
		this.azimuth = azimuth;
	}

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

		



	}

	@Override
	public void mouseClicked(MouseEvent arg) {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}
	public int getW()
	{
		return this.getWidth();
	}
	public int getH()
	{
		return this.getHeight();
	}

	public double azimuth(int x, int y) {
		Point3D p = new Point3D(Map.PixelToCoords(this.getWidth(), this.getHeight(), x, y));
		MyCoords m = new MyCoords();
		double ans = m.azimuth_elevation_dist(game.getPlayer().getLocation(), p)[0];
		return ans;
	}
	public void playAot() {
		Game gameCopy = new Game(game);
		Algorithm a = new Algorithm(play1, this.game, gameCopy, getW(), getH());
		ArrayList<Point3D> path = new ArrayList<Point3D>();

		play1.start();
		while(a.getPlay().isRuning()) {
			
			Point3D fruit = new Point3D(a.closesFruit(this.game));
			fruit = new Point3D(Map.coordsToPixel(this.getW(),this.getH() , fruit.x(), fruit.y()));
			path = new ArrayList<Point3D>(a.createPath(fruit, this.game));
			
			for (int i = 0; i < path.size(); i++) {
				System.out.println(path.get(i));
				this.azimuth = azimuth((int)path.get(i).x(),(int) path.get(i).y());
//				TreadsClass tr = new TreadsClass(this.play1,this.game,gameCopy,this);
//				tr.start();
				play1.rotate(getAzimuth());
				repaint();
				
			}
		}

		
		


	}
	public void t() {


		Game gameCopy = new Game(game);
		TreadsClass tr = new TreadsClass(this.play1,this.game,gameCopy,this);
		tr.start();


	}
	public class TreadsClass extends Thread{
		private Play play1 ;
		private Game game ;
		private Game gameCopy ;
		private window w;

		public TreadsClass(Play pl, Game gam, Game copy, window w) {
			this.play1 = pl;
			this.game = gam;
			this.gameCopy = copy;
			this.w = w;
		}
		public void run() {
			play1.start();
			while(play1.isRuning()) {
				
				play1.rotate(getAzimuth());


				this.w.game = new Game();
				ArrayList<String> board_data = play1.getBoard();
				for (int i = 0; i < board_data.size(); i++) {

					if(board_data.get(i).charAt(0) == 'P') {
						Packman p = new Packman(board_data.get(i));
						this.w.game.add(p);
					}
					
					if(board_data.get(i).charAt(0) == 'G') {
						Packman g = new Packman(board_data.get(i));
						this.w.game.addGhost(g);
					}
					if(board_data.get(i).charAt(0) == 'M') {
						Packman m = new Packman(board_data.get(i));
						this.w.game.setPlayer(m);
					}

					if(board_data.get(i).charAt(0) == 'F') {
						Fruit f  = new Fruit(board_data.get(i));
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
