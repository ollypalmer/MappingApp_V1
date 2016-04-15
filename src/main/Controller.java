package main;

import javax.swing.JFrame;

/**
 * Controller class for this application, run to begin
 * @author Oliver Palmer
 *
 */
public class Controller {

	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable(){
			public void run() {
				GUI  frame = new GUI();
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setSize(620,590);
				frame.setResizable(false);
				frame.setVisible(true);
			}
		});
	}
}
