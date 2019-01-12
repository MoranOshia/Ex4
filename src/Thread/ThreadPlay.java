package Thread;

import java.util.ArrayList;

import GUI.window;
import Robot.Fruit;
import Robot.Game;
import Robot.Packman;
import Robot.Play;
/*
 * This class is responsible for the Thread of the users play.
 */
public class ThreadPlay extends Thread{
	private Play play1 ;
	private Game game ;
	private Game gameCopy ;
	private window w;
	/*
	 * Copy constructor for values
	 *  @param pl - current play object
	 *  @param gam- current game object
	 *  @param copy-copy of the game
	 *  @param window- the current window of the game
	 */
	public ThreadPlay(Play pl, Game gam, Game copy, window w) {
		this.play1 = pl;
		this.game = gam;
		this.gameCopy = copy;
		this.w = w;
	}
	/*
	 *This function is responsible for running the game automatically with the Threads, and updating the locations of the game
	 */
	public void run() {
		play1.start();
		while(play1.isRuning()) {

			play1.rotate(this.w.getAzimuth());


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