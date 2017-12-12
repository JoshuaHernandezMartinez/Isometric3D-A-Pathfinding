package view;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MouseManager extends MouseAdapter{
	
	public static boolean left;
	public static int mouse_x, mouse_y;
	
	
	public MouseManager(){
		left = false;
		mouse_x = 0;
		mouse_y = 0;
		
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON1)
			left = true;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON1)
			left = false;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		mouse_x = e.getX();
		mouse_y = e.getY();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		mouse_x = e.getX();
		mouse_y = e.getY();
	}
}
