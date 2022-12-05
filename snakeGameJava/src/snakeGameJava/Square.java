package snakeGameJava;

public class Square {
	//En klass för alla fyrkanter i spelet; ormens huvud, kropp och äpplen
	
	public int X;
	
	public int getX() { return X; }
		
	public void setX(int XValue) {
		this.X = XValue;
	}
	
	public int Y;
	
	public int getY() { return Y; }
		
	public void setY(int YValue) {
		this.Y = YValue;
	}
	
	public Square(int X, int Y) {
		X = 0;
		Y = 0;
	}
}