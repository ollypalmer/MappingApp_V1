package main;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * This is the frame of the application. It is responsible for loading the data from a file
 * and interaction with the slider.
 * @author Oliver Palmer
 *
 */
public class GUI extends JFrame {
	
	private static final long serialVersionUID = 1L;
	private DrawMap drawMap;
	private JButton load;
	private JLabel sliderLabel = new JLabel("Zoom", JLabel.CENTER);
	private JSlider zoomSlider;
	private ArrayList<Observation> data;
	
	public GUI(){
		super("Map Viewer");
		
		// Load Button
		load = new JButton("Load");
		load.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				load();
			}
		});
		
		// Zoom Slider
		zoomSlider = new JSlider(JSlider.HORIZONTAL, 1, 8, 3);
		zoomSlider.setMajorTickSpacing(1);
		zoomSlider.setPaintTicks(true);
		zoomSlider.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent e){
				zoomSlider = (JSlider)e.getSource();
				drawMap.zoom((int)zoomSlider.getValue());
			}
		});
		
		drawMap = new DrawMap();
		
		// Layout Configuration
		Container contentPane = this.getContentPane();
		
		contentPane.setLayout(new GridBagLayout());
		GridBagConstraints gc = new GridBagConstraints();
		gc.gridx = 0;
		gc.gridy = 0;
		gc.gridwidth = 3;
		contentPane.add(drawMap, gc);
		
		gc.gridx = 0;
		gc.gridy = 1;
		gc.gridwidth = 2;
		contentPane.add(sliderLabel, gc);
		
		gc.gridx = 0;
		gc.gridy = 2;
		gc.gridwidth = 2;
		contentPane.add(zoomSlider, gc);
		
		gc.gridx = 2;
		gc.gridy = 1;
		gc.gridwidth = 1;
		gc.gridheight = 2;
		gc.anchor = GridBagConstraints.EAST;
		gc.insets = new Insets(0, 0, 0, 5);
		contentPane.add(load, gc);
	}
	
	/**
	 * Used to load the data collected by the EV3 into the program. Steps through each line of the .csv
	 * file adding the data contained to an array list of observations. This array list is then added
	 * to the mapping JPanel for rendering.
	 */
	public void load() {
		JFileChooser fc = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("TEXT FILES", "txt", "text", "csv");
		fc.setFileFilter(filter);
		int returnVal = fc.showOpenDialog(GUI.this);
		BufferedReader br = null;

		try {
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				
				data = new ArrayList<Observation>();
				
				br = new BufferedReader(new FileReader(file));
				String split = ",\\s*";
				String line = "";
				@SuppressWarnings("unused")
				String headerLine = br.readLine();
				
				while ((line = br.readLine()) != null) {
					
					String[] points = line.split(split);
					data.add(new Observation(Float.parseFloat(points[0]), Float.parseFloat(points[1]), Float.parseFloat(points[2]), Float.parseFloat(points[3])));
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) { }
			}
		}
		if (data != null) {
			drawMap.draw(data);
		}
	}
}
