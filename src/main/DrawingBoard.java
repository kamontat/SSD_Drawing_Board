package main;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;

import objects.*;

public class DrawingBoard extends JPanel {
	
	private MouseAdapter mouseAdapter;
	private List<GObject> gObjects;
	private GObject target;
	
	private int gridSize = 10;
	
	public DrawingBoard() {
		gObjects = new ArrayList<>();
		mouseAdapter = new MAdapter();
		addMouseListener(mouseAdapter);
		addMouseMotionListener(mouseAdapter);
		setPreferredSize(new Dimension(800, 600));
	}
	
	public void addGObject(GObject gObject) {
		gObjects.add(gObject);
		repaint();
	}
	
	public void groupAll() {
		CompositeGObject b = new CompositeGObject();
		gObjects.forEach(b::add);
		gObjects.clear();
		gObjects.add(b);
		
		repaint();
	}
	
	public void deleteSelected() {
		// TODO: Implement this method.
	}
	
	public void clear() {
		// TODO: Implement this method.
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		paintBackground(g);
		paintGrids(g);
		paintObjects(g);
	}
	
	private void paintBackground(Graphics g) {
		g.setColor(Color.white);
		g.fillRect(0, 0, getWidth(), getHeight());
	}
	
	private void paintGrids(Graphics g) {
		g.setColor(Color.lightGray);
		int gridCountX = getWidth() / gridSize;
		int gridCountY = getHeight() / gridSize;
		for (int i = 0; i < gridCountX; i++) {
			g.drawLine(gridSize * i, 0, gridSize * i, getHeight());
		}
		for (int i = 0; i < gridCountY; i++) {
			g.drawLine(0, gridSize * i, getWidth(), gridSize * i);
		}
	}
	
	private void paintObjects(Graphics g) {
		for (GObject go : gObjects) {
			go.paint(g);
		}
	}
	
	class MAdapter extends MouseAdapter {
		private GObject select;
		private int x;
		private int y;
		
		private void deselectAll() {
			select = null;
			x = 0;
			y = 0;
			gObjects.forEach(GObject::deselected);
			repaint();
		}
		
		@Override
		public void mousePressed(MouseEvent e) {
			deselectAll();
			x = e.getX() - x;
			y = e.getY() - y;
			gObjects.forEach(gObject -> {
				if (gObject.pointerHit(x, y)) {
					select = gObject;
					gObject.selected();
				}
			});
			repaint();
		}
		
		@Override
		public void mouseDragged(MouseEvent e) {
			x = e.getX() - x;
			y = e.getY() - y;
			if (select != null) {
				select.move(x, y);
				repaint();
			}
		}
	}
	
}