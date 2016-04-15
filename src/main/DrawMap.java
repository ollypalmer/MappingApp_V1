package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

/**
 * This class holds the observation data. Contains methods for interpreting it
 * and drawing a representation of this data to the JPanel
 * @author Oliver Palmer
 *
 */
public class DrawMap extends JPanel {

	private static final long serialVersionUID = 1L;
	private List<Observation> data;
	private int zoom = 3;
	private int width = 600;
	private int height = 500;
	private float lineX = 0;
	private float lineY = 0;
	
	public DrawMap() {
		setPreferredSize(new Dimension(width, height));
		setBackground(Color.WHITE);
		setBorder(BorderFactory.createEtchedBorder());
	}
	
	/**
	 * Adds different map data to the data set
	 * @param data
	 */
	public void draw(ArrayList<Observation> data) {
		this.data = data;
		repaint();
	}
	
	/**
	 * Adjusts the zoom value
	 * @param value
	 */
	public void zoom(int value){
		this.zoom = value;
		repaint();
	}
	
	/**
	 * Calculates the average X and Y position for a group of points
	 * @param points, the group to find the average of
	 */
	public void averagePoints(List<Observation> points) {
		float xAv = 0;
		float yAv = 0;
		
		for (Observation o : points) {
			xAv += o.getX();
			yAv += o.getY();
		}
		lineX = xAv / points.size();
		lineY = yAv / points.size();
	}
	
	/**
	 * Creates a list of grouped points. Points are grouped according to their similarity to nearby points
	 * then added to the master list of groups. Each group can then be averaged to establish the average
	 * X and Y position for that group.
	 * @param points, list of points to be sorted into groups
	 * @return List of point groups
	 */
	public ArrayList<ArrayList<Observation>> groupPoints(List<Observation> points) {
		
		ArrayList<ArrayList<Observation>> masterList = new ArrayList<ArrayList<Observation>>();
		ArrayList<Observation> group = null;
		
		for (Observation main : points) {
			if (main.getValue() == 1) {
				group = new ArrayList<Observation>();
				for (Observation comp : points) {
					if (comp.getValue() == 1) {
						
						// Calculates the difference between two points
						float xDiff = main.getX() - comp.getX();
						float yDiff = main.getY() - comp.getY();
						float heading = main.getHeading();
						if ((xDiff < 150 && xDiff > -150) && (yDiff < 150 && yDiff > -150)
								&& (comp.getHeading() > heading - 30 && comp.getHeading() < heading + 30)) {
							group.add(comp);
						} 
					}
				}
				masterList.add(group);
			}
		}
		return masterList;
	}
	
	/**
	 * Draws the contents of the data 
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		int centerX = width/2;
		int centerY = height/2;
		int lastX = centerX;
		int lastY = centerY;
		int x;
		int y;
		
		if (data != null) {
			
			g.drawOval(centerX-5, centerY-5, 10, 10);
			
			for (Observation o : data) {
				// Determines if the observation is of occupied space or unoccupied space
				if (o.getValue() == 0) {
					g.setColor(Color.RED);
					x = centerX + (Math.round(o.getX())) / zoom;
					y = centerY + (Math.round(o.getY())) / zoom;
					g.drawLine(lastX, lastY, x, y);
					lastX = x;
					lastY = y;
				} else if (o.getValue() == 1){
					g.setColor(Color.BLACK);
					x = centerX + (Math.round(o.getX())) / zoom;
					y = centerY + (Math.round(o.getY())) / zoom;
					g.drawOval(x - 4, y - 4, 8, 8);
				}
			}
			g.setColor(Color.BLACK);
			// Create list of point groups
			ArrayList<ArrayList<Observation>> mList = groupPoints(data);
			// Step through each list of point groups
			for (ArrayList<Observation> list : mList) {
				
				// Average each group
				averagePoints(list);
				float heading = list.get(0).getHeading();
				
				// Draw line at average position
				if ((heading < 45 && heading > -45) || ((heading < -135 && heading > -225) || (heading > 135 && heading < 225))) {
					g.drawLine(centerX + Math.round(lineX)/zoom, (centerY + Math.round(lineY)/zoom) + 50, centerX + Math.round(lineX)/zoom, (centerY + Math.round(lineY)/zoom) - 50);
				} else {
					g.drawLine((centerX + Math.round(lineX)/zoom) +50, centerY + Math.round(lineY)/zoom, (centerX + Math.round(lineX)/zoom)-50, centerY + Math.round(lineY)/zoom);
				}	
			}
		}
	}
}
