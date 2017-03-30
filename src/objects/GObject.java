package objects;

import java.awt.*;

public abstract class GObject {
	
	protected boolean selected;
	
	protected int x;
	protected int y;
	protected int width;
	protected int height;
	
	public GObject(int x, int y, int width, int height) {
		this.selected = false;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public boolean pointerHit(int pointerX, int pointerY) {
		return (x < pointerX && pointerX < x + width) && (y < pointerY && pointerY < y + height);
	}
	
	public boolean isSelected() {
		return selected;
	}
	
	public void selected() {
		selected = true;
	}
	
	public void deselected() {
		selected = false;
	}
	
	
	public void move(int dX, int dY) {
		this.x += dX;
		this.y += dY;
	}
	
	public final void paint(Graphics g) {
		/* Example of template method pattern */
		paintObject(g);
		paintRegion(g);
		paintLabel(g);
	}
	
	public void paintRegion(Graphics g) {
		/* Set color */
		Color color = selected ? Color.red: Color.black;
		g.setColor(color);
		/* Set size */
		int size = selected ? 3: 1;
		/* Draw a region */
		Graphics2D g2d = (Graphics2D) g;
		g2d.setStroke(new BasicStroke(size));
		g2d.drawRect(x, y, width, height);
	}
	
	public abstract void paintObject(Graphics g);
	
	public abstract void paintLabel(Graphics g);
	
	public boolean isInside(Shape shape) {
		return shape.getBounds().intersects(x, y, width, height);
	}
	
	protected int getRightTop() {
		return x + width;
	}
	
	protected int getLeftBottom() {
		return y + height;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		
		GObject gObject = (GObject) o;
		
		if (x != gObject.x) return false;
		if (y != gObject.y) return false;
		if (width != gObject.width) return false;
		return height == gObject.height;
	}
	
	@Override
	public int hashCode() {
		int result = (selected ? 1: 0);
		result = 31 * result + x;
		result = 31 * result + y;
		result = 31 * result + width;
		result = 31 * result + height;
		return result;
	}
	
	@Override
	public String toString() {
		return this.getClass() + "{" + "selected=" + selected + ", x=" + x + ", y=" + y + ", width=" + width + ", height=" + height + '}';
	}
}
