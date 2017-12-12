package view;

import java.awt.image.BufferedImage;

public class Animation {
	
	private final int SPEED = 40;
	
	private BufferedImage[] frames;
	private int length;
	private int index;
	private long t, dt;
	
	public Animation(BufferedImage[] frames) {
		this.frames = frames;
		length = frames.length;
		index = 0;
		t = System.currentTimeMillis();
		dt = 0;
	}
	
	public void update() {
		dt += System.currentTimeMillis() - t;
		t = System.currentTimeMillis();
		
		if(dt > SPEED) {
			index ++;
			dt = 0;
			if(index == length) {
				index = 0;
			}
		}
	}
	
	public BufferedImage getCurrentFrame() {
		return frames[index];
	}
	
	public void reset() {
		index = 0;
		t = System.currentTimeMillis();
		dt = 0;
	}
	
}
