package objects;

import java.awt.*;

public class Rect extends GObject {
	
	private Color color;
	
	public Rect(int x, int y, int width, int height, Color color) {
		super(x, y, width, height);
		this.color = color;
	}
	
	@Override
	public void paintObject(Graphics g) {
		g.setColor(color);
		g.fillRect(super.x, super.y, super.width, super.height);
	}
	
	@Override
	public void paintLabel(Graphics g) {
		g.drawString("Rect", this.x, this.y + height + g.getFont().getSize());
	}
}
