package main;

import objects.CompositeGObject;
import objects.GObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

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
		if (gObjects.size() != 0) {
			CompositeGObject b = new CompositeGObject();
			gObjects.forEach(b::add);
			gObjects.clear();
			gObjects.add(b);
			repaint();
		}
	}
	
	public void deleteSelected() {
		gObjects = gObjects.stream().filter(gObject -> !gObject.isSelected()).collect(Collectors.toList());
		repaint();
	}
	
	public void clear() {
		gObjects.clear();
		repaint();
	}
	
	public int objectNumber() {
		return gObjects.size();
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
			x = e.getX();
			y = e.getY();
			gObjects.forEach(gObject -> {
				if (gObject.pointerHit(x, y)) {
					select = gObject;
					gObject.selected();
				}
			});
			
			// deselect left
			gObjects.stream().filter(gObject -> gObject.isSelected() && !gObject.equals(select)).forEach(GObject::deselected);
			
			repaint();
		}
		
		@Override
		public void mouseDragged(MouseEvent e) {
			if (select != null) {
				select.move(e.getX() - x, e.getY() - y);
				x = e.getX();
				y = e.getY();
				repaint();
			}
		}
	}
	
}