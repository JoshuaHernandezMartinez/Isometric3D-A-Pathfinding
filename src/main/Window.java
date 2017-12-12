package main;

import javax.swing.JFrame;

import view.MouseManager;
import view.View;

public class Window extends JFrame{
	
	/**
	 *  @Author : Yami Studios
	 */
	private static final long serialVersionUID = 1L;
	private View view;
	private MouseManager mouseManager;
	
	public Window() {
		super("Isometric 3D Test by Yami Studios");
		setSize(800, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setResizable(false);
		
		view = new View();
		mouseManager = new MouseManager();
		
		add(view);
		addMouseListener(mouseManager);
		addMouseMotionListener(mouseManager);
		
	}
	
	public static void main(String[] args) {
		new Window().setVisible(true);
	}

}
