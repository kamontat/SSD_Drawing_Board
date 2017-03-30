package objects;

import java.awt.Color;
import java.awt.Graphics;
import java.util.*;

public class CompositeGObject extends GObject {
	
	private List<GObject> gObjects;
	
	public CompositeGObject() {
		super(0, 0, 0, 0);
		gObjects = new ArrayList<>();
		
	}
	
	public void add(GObject gObject) {
		gObjects.add(gObject);
		recalculateRegion();
	}
	
	public void remove(GObject gObject) {
		gObjects.removeIf(gObject1 -> gObject1.equals(gObject));
	}
	
	@Override
	public void move(int dX, int dY) {
		super.move(dX, dY);
		gObjects.forEach(g -> g.move(dX, dY));
	}
	
	public void recalculateRegion() {
		int minX = gObjects.stream().min(Comparator.comparingInt(o -> o.x)).get().x;
		int minY = gObjects.stream().min(Comparator.comparingInt(o -> o.y)).get().y;
		int maxX = gObjects.stream().max(Comparator.comparingInt(GObject::getRightTop)).get().getRightTop();
		int maxY = gObjects.stream().max(Comparator.comparingInt(GObject::getLeftBottom)).get().getLeftBottom();
		
		super.x = minX;
		super.y = minY;
		super.width = maxX - minX;
		super.height = maxY - minY;
	}
	
	@Override
	public void paintObject(Graphics g) {
		gObjects.forEach(gObj -> gObj.paintObject(g));
	}
	
	@Override
	public void paintLabel(Graphics g) {
		g.drawString("Grouped", super.x, super.y + super.height + g.getFont().getSize());
	}
	
}
