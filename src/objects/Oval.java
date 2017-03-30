package objects;

import java.awt.*;

public class Oval extends GObject {
	
	private Color color;
	
	public Oval(int x, int y, int width, int height, Color color) {
		super(x, y, width, height);
		this.color = color;
	}
	
	@Override
	public void paintObject(Graphics g) {
		g.setColor(color);
		g.fillOval(super.x, super.y, super.width, super.height);
	}
	
	@Override
	public void paintLabel(Graphics g) {
		g.drawString("Oval", this.x, this.y + height + g.getFont().getSize());
	}
}
