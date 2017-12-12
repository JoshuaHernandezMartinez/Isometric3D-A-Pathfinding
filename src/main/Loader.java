package main;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Loader {
	
	public static BufferedImage loadImage(String path) {
		try {
			return ImageIO.read(Loader.class.getResource(path));
		}catch(IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
