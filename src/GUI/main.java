package GUI;

import javax.swing.JFrame;
/*
 * This class is responsible for opening the frame to make the game run.
 */
public class main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		 window v = new window();
		v.setVisible(true);
		v.setSize(v.myImage.getWidth(),v.myImage.getHeight());
		v.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

}
