package view;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.Timer;

import main.Loader;

public class View extends JPanel{
	
	/**
	 * @Author Yami Studios
	 */
	private static final long serialVersionUID = 1L;

	private final int T_W = 64, T_H = 32;
	
	private int[][] tile_type = new int[][] {
		{7, 9, 9, 9, 9, 9, 9, 4},
		{8, 10, 10, 10, 10, 10, 10, 8},
		{8, 10, 2, 4, 10, 3, 10, 8},
		{8, 10, 10, 8, 10, 8, 10, 8},
		{8, 10, 10, 6, 9, 5, 10, 8},
		{8, 10, 10, 10, 10, 10, 10, 8},
		{8, 10, 10, 7, 4, 10, 10, 8},
		{6, 9, 9, 5, 6, 9, 9, 5}
	};
	
	private Node[][] maze;
	
	private BufferedImage groundSheet;
	private BufferedImage barricadeSheet;
	
	private BufferedImage crusader_idle_right;
	private BufferedImage crusader_idle_left;
	private BufferedImage crusader_idle_up;
	private BufferedImage crusader_idle_down;
	
	private BufferedImage crusader_walk_right;
	private BufferedImage crusader_walk_left;
	private BufferedImage crusader_walk_up;
	private BufferedImage crusader_walk_down;
	
	
	private BufferedImage[] barricades = new BufferedImage[10];
	
	// player idle sprites
	
	private BufferedImage[] player_idle_right = new BufferedImage[16];
	private BufferedImage[] player_idle_left = new BufferedImage[16];
	private BufferedImage[] player_idle_up = new BufferedImage[16];
	private BufferedImage[] player_idle_down = new BufferedImage[16];
	
	// player walk sprites
	
	private BufferedImage[] player_walk_right = new BufferedImage[15];
	private BufferedImage[] player_walk_left = new BufferedImage[15];
	private BufferedImage[] player_walk_up = new BufferedImage[15];
	private BufferedImage[] player_walk_down = new BufferedImage[15];
	
	private BufferedImage ground1;
	
	private ActionListener action;
	private Timer looper;
	
	// player animations
	
	private Animation player_idle_right_anim;
	private Animation player_idle_left_anim;
	private Animation player_idle_up_anim;
	private Animation player_idle_down_anim;
	
	private Animation player_walk_right_anim;
	private Animation player_walk_left_anim;
	private Animation player_walk_up_anim;
	private Animation player_walk_down_anim;
	
	private Animation currentAnimation;
	
	// player position
	
	private int x, y, px, py;
	
	private String dir;
	
	// smooth movement (interpolation)
	
	private boolean moving;
	
	private float t;
	
	private int x_start, y_start;
	private int x_end, y_end;
	
	private ArrayList<Node> path;
	
	// mouse coords to iso coors
	
	private int mouse_row, mouse_col;
	
	private boolean mouseInRange;
	
	public View() {
		
		// load ground
		
		groundSheet = Loader.loadImage("/32_flagstone_tiles.png");
		
		// load barricades
		
		barricadeSheet = Loader.loadImage("/barricade_tiles_64x32.png");
		
		// load animation sheets
		
		crusader_idle_right = Loader.loadImage("/crusader_idle/crusader_idle_right.png");
		crusader_idle_left = Loader.loadImage("/crusader_idle/crusader_idle_left.png");
		crusader_idle_up = Loader.loadImage("/crusader_idle/crusader_idle_up.png");
		crusader_idle_down = Loader.loadImage("/crusader_idle/crusader_idle_down.png");
		
		crusader_walk_right = Loader.loadImage("/crusader_walk/crusader_walk_right.png");
		crusader_walk_left = Loader.loadImage("/crusader_walk/crusader_walk_left.png");
		crusader_walk_up = Loader.loadImage("/crusader_walk/crusader_walk_up.png");
		crusader_walk_down = Loader.loadImage("/crusader_walk/crusader_walk_down.png");
		
		// cropping sprites
		
		barricades = crop(barricadeSheet, 64, 64, 0, 0, 3, 10);
		
		player_idle_right = crop(crusader_idle_right, 60, 48, 0, 0, 0, 16);
		player_idle_left = crop(crusader_idle_left, 60, 48, 0, 0, 0, 16);
		player_idle_up = crop(crusader_idle_up, 60, 48, 0, 0, 0, 16);
		player_idle_down = crop(crusader_idle_down, 60, 48, 0, 0, 0, 16);
		
		player_walk_right = crop(crusader_walk_right, 60, 48, 0, 0, 0, 15);
		player_walk_left = crop(crusader_walk_left, 60, 48, 0, 0, 0, 15);
		player_walk_up = crop(crusader_walk_up, 60, 48, 0, 0, 0, 15);
		player_walk_down = crop(crusader_walk_down, 60, 48, 0, 0, 0, 15);
		
		ground1 = groundSheet.getSubimage(0, 0, 64, 32);
		
		// create animations
		
		player_idle_right_anim = new Animation(player_idle_right);
		player_idle_left_anim = new Animation(player_idle_left);
		player_idle_up_anim = new Animation(player_idle_up);
		player_idle_down_anim = new Animation(player_idle_down);
		
		player_walk_right_anim = new Animation(player_walk_right);
		player_walk_left_anim = new Animation(player_walk_left);
		player_walk_up_anim = new Animation(player_walk_up);
		player_walk_down_anim = new Animation(player_walk_down);
		
		currentAnimation = player_idle_right_anim;
		dir = "right";
		
		x = 5;
		y = 1;
		px = x * T_W / 2 - y * T_W / 2;
		py = y * T_H / 2 + x * T_H / 2;
		
		moving = false;
		t = 0;
		
		maze = new Node[tile_type.length][tile_type[0].length];			
		
		path = new ArrayList<Node>();
		
		mouseInRange = false;
		
		for(int r = 0; r < maze.length; r++)
			for(int c = 0; c < maze[0].length; c++) {
				int tp = tile_type[r][c];
				boolean wall = tp == 10 ? false : true;
				maze[r][c] = new Node(wall, r, c, tp);
			}
		
		action = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				update();
				repaint();
			}
		};
		looper = new Timer(1000/60, action);
		looper.start();
	}
	
	private BufferedImage[] crop(BufferedImage sheet, int w, int h,
			int row, int col, int cols_per, int size) {
		BufferedImage[] frames = new BufferedImage[size];
		
		for(int i = 0; i < size; i++) {
			
			frames[i] = sheet.getSubimage(col * w, row * h,
					w, h);
			
			if(col == cols_per) {
				col = -1;
				row ++;
			}
			col ++;
			
		}
		
		return frames;
	}
	
	private void update() {
		currentAnimation.update();
		if(moving && !path.isEmpty()) {
			
			// interpolate player motion to the next node in the path
			
			x_start = x * T_W / 2 - y * T_W / 2;
			y_start = y * T_H / 2 + x * T_H / 2;
			
			int path_x = path.get(0).col;
			int path_y = path.get(0).row;
			
			x_end = path_x * T_W / 2 - path_y * T_W / 2;
			y_end = path_y * T_H / 2 + path_x * T_H / 2;
			
			px = lerp(t, x_start, x_end);
			py = lerp(t, y_start, y_end);
			
			t += 0.05f;
			
			if(t > 1) {
				t = 0;
				x = path_x;
				y = path_y;
				path.remove(0);
				setAnimation();
				
				if(path.isEmpty())
					moving = false;
				
			}
			
		}
		
		float x = MouseManager.mouse_x - 400;
		// hardcode some values to smooth the mouse projection (-327)
		float y = MouseManager.mouse_y - 327 + maze.length * T_H / 2;
		
		// equation to convert from mouse coords to tile coords
		
		mouse_row = (int) (y / T_H - x / T_W);
		mouse_col = (int) (x / T_W + y / T_H);
		
		if(mouse_row >= 0 && mouse_row < maze.length &&
				mouse_col >= 0 && mouse_col < maze[0].length)
			mouseInRange = true;
		else
			mouseInRange = false;
			
		
		if(MouseManager.left && mouseInRange && maze[mouse_row][mouse_col].tile == 10) {
			requestPath(maze[mouse_row][mouse_col]);
		}
		
	}
	
	// linear interpolation function
	
	private int lerp(float t, int start, int end) {
		return (int) (start + ((end - start) * t));
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, 800, 600);
		
		g.translate(400, 300 - maze.length * T_H / 2);
		
		for(int row = 0; row < maze.length; row++)
			for(int col = 0; col < maze[0].length; col ++) {
				
				drawTile(col, row, ground1, g);
				if(maze[row][col].tile != 10)
					drawTile(col, row, barricades[maze[row][col].tile], g);
			}
		
		g.drawImage(currentAnimation.getCurrentFrame(), px - T_W/2 + 2, py - 16, null);
		
		for(int i = 0; i < path.size(); i++) {
			Node n = path.get(i);
			drawTile(n.col, n.row, g, Color.BLUE);
		}
		
		g.setColor(Color.RED);
		
		g.drawString(" - > mouse_col: " + mouse_col, -400, -150);
		g.drawString(" - > mouse_row: " + mouse_row, -400, -125);
		
		if(mouseInRange) {
			int type = maze[mouse_row][mouse_col].tile; 
			Color c;
			if(type != 10 || mouse_row == y && mouse_col == x)
				c = Color.RED;
			else
				c = Color.GREEN;
			drawTile(mouse_col, mouse_row, g, c);
		}
		
	}
	
	private void drawTile(int x, int y, Graphics g, Color color) {
		
		int px = (x * T_W/2 - y * T_W / 2);
		int py = (y * T_H / 2 + x * T_H / 2);
		
		int[] xPoints = new int[] {px, px + T_W / 2, px, px - T_W / 2};
		int[] yPoints = new int[] {py, py + T_H / 2, py + T_H, py + T_H / 2};
		
		Graphics2D g2d = (Graphics2D)g;
		
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.25f));
		
		g2d.setColor(color);
		
		g2d.fillPolygon(xPoints, yPoints, 4);
		
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
		
	}
	private void drawTile(int x, int y, BufferedImage tile, Graphics g) {
		int iso_x = (x * T_W / 2 - y * T_W / 2);
		int iso_y = (y * T_H / 2 + x * T_H / 2);
		
		int diff_y = tile.getHeight() - T_H;
		
		g.drawImage(tile, iso_x - T_W/2, iso_y - diff_y, null);
		
	}
	
	private void requestPath(Node endNode) {
		path = findPath(maze[y][x], endNode);	
		if(path.isEmpty())
			return;
		moving = true;
		setAnimation();
	}
	
	private void setAnimation() {
		
		if(path.isEmpty()) {
			if(dir.equals("right")) 
				currentAnimation = player_idle_right_anim;
			if(dir.equals("left")) 
				currentAnimation = player_idle_left_anim;
			if(dir.equals("up")) 
				currentAnimation = player_idle_up_anim;
			if(dir.equals("down")) 
				currentAnimation = player_idle_down_anim;
			return;
		}
		
		int _x = path.get(0).col;
		int _y = path.get(0).row;
		
		if(x - _x > 0) {
			currentAnimation = player_walk_left_anim; 
			dir = "left";
		}else if(x - _x < 0) {
			currentAnimation = player_walk_right_anim;
			dir = "right";
		}else if(y - _y > 0) {
			currentAnimation = player_walk_up_anim;
			dir = "up";
		}else if(y - _y < 0) {
			currentAnimation = player_walk_down_anim;
			dir = "down";
		}
	}
	
	// A* pathfinding
	
	private ArrayList<Node> findPath(Node start, Node target){
		ArrayList<Node> open = new ArrayList<Node>();
		ArrayList<Node> closed = new ArrayList<Node>();
		
		open.add(start);
		
		while(!open.isEmpty()) {
			
			Node currentNode = open.get(0);
			
			for(int i = 1; i < open.size(); i++) {
				Node q = open.get(i);
				if(q.f_cost() < currentNode.f_cost() ||
						q.f_cost() == currentNode.f_cost() &&
						q.h_cost < currentNode.h_cost) {
					currentNode = q;
				}
			}
			
			open.remove(currentNode);
			closed.add(currentNode);
			
			if(currentNode.equals(target)) {
				return retracePath(start, target);
			}
			
			
			for(Node n: getNeighbours(currentNode)) {
				if(n.wall || closed.contains(n)) {
					continue;
				}
				
				int newCostToNeighbour  = currentNode.g_cost +
						getDistance(currentNode, n);
				if(newCostToNeighbour  < n.g_cost || !open.contains(n)) {
					n.g_cost = newCostToNeighbour ;
					n.h_cost = getDistance(n, target);
					n.parent = currentNode;
					
					if(!open.contains(n))
						open.add(n);
				}
			}
		}
		
		System.out.println("Path not found	");
		return null;
	}
	
	private ArrayList<Node> retracePath(Node start, Node end){
		
		ArrayList<Node> path= new ArrayList<Node>();
		
		Node currentNode = end;
		
		while(!currentNode.equals(start)) {
			path.add(currentNode);
			currentNode = currentNode.parent;
		}
		
		for(int i = 0; i < path.size()/2; i++) {
			
			Node n = path.get(i);
			path.set(i, path.get(path.size() - i - 1));
			path.set(path.size() - i - 1, n);
		}
		return path;
	}
	
	// this is manhattan distance
	
	private int getDistance(Node a, Node b) {
		
		int distX = Math.abs(a.col - b.col);
		int distY = Math.abs(a.row - b.row);
		
		if(distX > distY)
			return 14 * distY + 10 * (distX - distY);
		
		return 14 * distX + 10 * (distY - distX);
	}
	
	private ArrayList<Node> getNeighbours(Node n){
		ArrayList<Node> neighbours = new ArrayList<Node>();
		
		neighbours.add(maze[n.row - 1][n.col]);
		neighbours.add(maze[n.row + 1][n.col]);
		neighbours.add(maze[n.row][n.col - 1]);
		neighbours.add(maze[n.row][n.col + 1]);
		
		return neighbours;
	}
}
