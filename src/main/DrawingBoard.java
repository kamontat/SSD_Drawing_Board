package main;

import objects.CompositeGObject;
import objects.GObject;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
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
	// rect when drag code by javadoc
	private Rectangle currentRect = null;
	private Rectangle rectToDraw = null;
	private Rectangle previousRectDrawn = new Rectangle();
	
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
	
	public void group() {
		if (gObjects.size() != 0 && currentRect != null) {
			CompositeGObject b = new CompositeGObject();
			List<GObject> remove = gObjects.stream().filter(gObject -> gObject.isInside(currentRect)).collect(Collectors.toList());
			// move from it to CompositeGObject
			remove.forEach(b::add);
			gObjects.removeAll(remove);
			
			gObjects.add(b);
			currentRect = null;
			repaint();
		}
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
		paintDragRect(g);
		paintObjects(g);
	}
	
	private void paintDragRect(Graphics g) {
		//If currentRect exists, paint a box on top.
		if (currentRect != null) {
			g.setColor(Color.BLUE);
			g.drawRect(rectToDraw.x, rectToDraw.y, rectToDraw.width - 1, rectToDraw.height - 1);
		}
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
	
	public void unGroup() {
		List<GObject> newList = new ArrayList<>();
		GObject removed = null;
		for (GObject gObject : gObjects) {
			if (gObject.getClass() == CompositeGObject.class) {
				newList.addAll(CompositeGObject.class.cast(gObject).unGroup());
				removed = gObject;
			}
		}
		if (removed != null) {
			gObjects.addAll(newList);
			gObjects.remove(removed);
		}
		repaint();
	}
	
	private class MAdapter extends MouseInputAdapter {
		private Point start, end;
		
		private void deselectAll() {
			target = null;
			end = new Point(0, 0);
			start = end.getLocation();
			
			gObjects.forEach(GObject::deselected);
			repaint();
		}
		
		public void mousePressed(MouseEvent e) {
			deselectAll();
			end = new Point(e.getX(), e.getY());
			start = end.getLocation();
			// select obj
			gObjects.forEach(gObject -> {
				if (gObject.pointerHit(end.x, end.y)) {
					target = gObject;
					gObject.selected();
				}
			});
			// deselect left
			gObjects.stream().filter(gObject -> gObject.isSelected() && !gObject.equals(target)).forEach(GObject::deselected);
			// rect drawer
			if (target == null) {
				currentRect = new Rectangle(end.x, end.y, 0, 0);
				updateDrawableRect(getWidth(), getHeight());
			}
			repaint();
		}
		
		public void mouseDragged(MouseEvent e) {
			if (target != null) {
				target.move(e.getX() - end.x, e.getY() - end.y);
				end = new Point(e.getX(), e.getY());
				repaint();
			} else {
				updateSize(e);
			}
		}
		
		public void mouseReleased(MouseEvent e) {
			if (target == null) {
				updateSize(e);
				// change to CompositeGObject
			}
		}
		
		private void updateSize(MouseEvent e) {
			int x = e.getX();
			int y = e.getY();
			currentRect.setSize(x - currentRect.x, y - currentRect.y);
			updateDrawableRect(getWidth(), getHeight());
			Rectangle totalRepaint = rectToDraw.union(previousRectDrawn);
			repaint(totalRepaint.x, totalRepaint.y, totalRepaint.width, totalRepaint.height);
		}
	}
	
	private void updateDrawableRect(int compWidth, int compHeight) {
		int x = currentRect.x;
		int y = currentRect.y;
		int width = currentRect.width;
		int height = currentRect.height;
		
		//Make the width and height positive, if necessary.
		int t[] = notNegative(x, width);
		x = t[0];
		width = t[1];
		t = notNegative(y, height);
		y = t[0];
		height = t[1];
		
		//The rectangle shouldn't extend past the drawing area.
		if ((x + width) > compWidth) {
			width = compWidth - x;
		}
		if ((y + height) > compHeight) {
			height = compHeight - y;
		}
		
		//Update rectToDraw after saving old value.
		if (rectToDraw != null) {
			previousRectDrawn.setBounds(rectToDraw.x, rectToDraw.y, rectToDraw.width, rectToDraw.height);
			rectToDraw.setBounds(x, y, width, height);
		} else {
			rectToDraw = new Rectangle(x, y, width, height);
		}
	}
	
	private int[] notNegative(int xy, int wh) {
		if (wh < 0) {
			wh = 0 - wh;
			xy = xy - wh + 1;
			if (xy < 0) {
				wh += xy;
				xy = 0;
			}
		}
		return new int[]{xy, wh};
	}
}