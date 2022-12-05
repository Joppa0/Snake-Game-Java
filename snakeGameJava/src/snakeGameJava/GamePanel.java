package snakeGameJava;
import java.awt.*;
import java.awt.event.*;
import java.lang.System.Logger;
import javax.swing.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import javax.swing.JPanel;
public class GamePanel extends JPanel implements ActionListener{
	static final int UNIT_SIZE = 25;
	static final int SCREEN_WIDTH = 600;
	static final int SCREEN_HEIGHT = 600;
	static final int MAX_WIDTH = SCREEN_WIDTH / UNIT_SIZE - 1;
	static final int MAX_HEIGHT = SCREEN_HEIGHT / UNIT_SIZE - 1;
	static final int GAME_UNITS = (SCREEN_WIDTH*SCREEN_HEIGHT)/UNIT_SIZE;
	static int DELAY = 70;

	int score;
	int highScore;
	int appleX;
	int appleY;
	
	List<Square> Snake = new ArrayList<Square>();
	
	private Square apple = new Square(0,0);
	
	String direction;
	
	boolean running = false;
	
	Timer timer;
	Random random;
	
	GamePanel(){
		random = new Random();
		this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
		this.setBackground(Color.black);
		this.setFocusable(true);
		this.addKeyListener(new MyKeyAdapter());
		
		startGame();
	}
	
	//Startar spelet
	public void startGame() {
		direction = "Right";
		
		//Skapar huvudet för ormen och lägger till det som index 0 i listan
		Square head = new Square(0,0);
		Snake.add(head);
		
		//Lägger till svansen
		for(int i = 0; i < 6; i++) {
			Square body = new Square(0,0);
			Snake.add(body);
		}
		
		running = true;
		
		//Ställer om värdet för DELAY och skapar en ny timer
		timer = new Timer(DELAY, this);
		timer.start();
		
		newApple();
	}
	
	//Ritar ut fönstret
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		draw(g);
	}
	
	public void draw(Graphics g) {
		//Ritar ut rutnätet
		if(running) {
			for(int i = 0; i < SCREEN_HEIGHT / UNIT_SIZE; i++) {
				g.drawLine(i*UNIT_SIZE, 0, i*UNIT_SIZE, SCREEN_HEIGHT);
				g.drawLine(0, i*UNIT_SIZE, SCREEN_WIDTH, i*UNIT_SIZE);
			}
			
			//Ritar ut äpplet
			g.setColor(Color.red);
			g.fillOval(appleX * UNIT_SIZE, appleY * UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);
			
			//Ritar ut varje individuell del av ormen
			for(int i = 0; i < Snake.size(); i++) {
				if(i == 0) {
					g.setColor(Color.green);
					g.fillRect(Snake.get(i).X * UNIT_SIZE, Snake.get(i).Y * UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);
				}
				else {
					g.setColor(new Color(45,180,0));
					g.fillRect(Snake.get(i).X * UNIT_SIZE, Snake.get(i).Y * UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);
				}
			}
			//Skriver ut score och high score
			g.setColor(Color.red);
			g.setFont( new Font("Ink Free",Font.BOLD, 40));
			FontMetrics metrics = getFontMetrics(g.getFont());
			g.drawString("Score: "+score, ((SCREEN_WIDTH - metrics.stringWidth("Score: "+score))/2) - 150, g.getFont().getSize());
			g.drawString("High Score: " + highScore, ((SCREEN_WIDTH - metrics.stringWidth("High Score: " + highScore))/2) + 150, g.getFont().getSize());
		}
		else {
			//Avslutar rundan om ormen kolliderat med sig själv eller väggen
			gameOver(g);
		}
	}
	
	//Placerar ett äpple inom slumpmässig ruta
	public void newApple() {
		boolean badSpawn;
		
		//Funktionen skapar ett slumpmässigt värde för ruta X och Y
		//Kollar sedan om ormen befinner sig i en av rutorna, funktionen fortsätter då
		do {
			badSpawn = false;

			appleX = ((int)(Math.random() * (MAX_WIDTH - 2)) + 2);
			appleY = ((int)(Math.random() * (MAX_HEIGHT - 2)) + 2);
			
			for(int i = 0; i < Snake.size(); i++) {
				if(Snake.get(i).X == appleX && Snake.get(i).Y == appleY) {
					badSpawn = true;
				}
			}
			//När en tillåten ruta hittats slutar funktionen att loopa
		}while (badSpawn == true);
		
		apple = new Square(appleX, appleY);
	}
	
	//Rör ormen
	public void move() {
		for(int i = Snake.size() - 1; i >= 0; i--) {
			//Huvudet är alltid index 0
			//Medför att index 0, huvudet, kollar efter riktning, och rör sig sedan
			if(Snake.get(i) == Snake.get(0)) {
				switch(direction) {
				case "Up":
					Snake.get(0).Y--;
					break;
				case "Down":
					Snake.get(0).Y++;
					break;
				case "Left":
					Snake.get(0).X--;
					break;
				case "Right":
					Snake.get(0).X++;
					break;
				}
			}
			//Alla andra index följer föregående
			else {
				Snake.get(i).X = Snake.get(i - 1).X;
				Snake.get(i).Y = Snake.get(i - 1).Y;
			}
		}
	}
	
	//Kollar om huvudet och äpplet är i samma ruta, lägger då till poäng och kroppsdel
	public void checkApple() {
		if((Snake.get(0).X == appleX) && (Snake.get(0).Y == appleY)) {
			
			Square body = new Square(0,0);
			body.X = Snake.get(Snake.size() - 1).X;
			body.Y = Snake.get(Snake.size() - 1).Y;
			Snake.add(body);
			
			score++;
			newApple();
			
			//Spelet blir snabbare och snabbare för varje äpple som äts
			DELAY--;
			timer.setDelay(DELAY);
		}
	}
	
	//Kollar om huvudet kolliderar med en vägg eller svansen
	public void checkCollisions() {
		for(int i = Snake.size() - 1; i > 0; i--) {
			if((Snake.get(0).X == Snake.get(i).X) && (Snake.get(0).Y == Snake.get(i).Y)) {
				running = false;
			}
		}
		if(Snake.get(0).X < 0) {
			running = false;
		}
		
		if(Snake.get(0).X > MAX_WIDTH) {
			running = false;
		}
		
		if(Snake.get(0).Y < 0) {
			running = false;
		}
		
		if(Snake.get(0).Y > MAX_HEIGHT) {
			running = false;
		}
		
		if(!running) {
			timer.stop();
		}
	}
	
	//Ritar ut Game Over skärm
	public void gameOver(Graphics g) {
		//Ställer in highScore
		if(score > highScore) {
			highScore = score;
		}
		
		g.setColor(Color.red);
		g.setFont( new Font("Ink Free",Font.BOLD, 40));
		FontMetrics metrics = getFontMetrics(g.getFont());
		g.drawString("Score: "+score, (SCREEN_WIDTH - metrics.stringWidth("Score: "+score))/2, g.getFont().getSize());
		
		g.drawString("Game Over", (SCREEN_WIDTH - metrics.stringWidth("Game Over"))/2, (SCREEN_HEIGHT/2) -25);
		g.drawString("Press Enter to restart", (SCREEN_WIDTH - metrics.stringWidth("Press Enter to restart"))/2, (SCREEN_HEIGHT/2) + 25);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(running) {
			move();
			checkApple();
			checkCollisions();
		}
		repaint();
	}
	
	//Hanterar knapptryckningar från användaren
	public class MyKeyAdapter extends KeyAdapter{
		@Override
		//Ställer in riktningen ormen ska röra sig i beroende på vilken knapp som tryckts ned
		public void keyPressed(KeyEvent e) {
			switch(e.getKeyCode()) {
			case KeyEvent.VK_LEFT:
				if(direction != "Right") {
					direction = "Left";
				}
				break;
			case KeyEvent.VK_RIGHT:
				if(direction != "Left") {
					direction = "Right";
				}
				break;
			case KeyEvent.VK_UP:
				if(direction != "Down") {
					direction = "Up";
				}
				break;
			case KeyEvent.VK_DOWN:
				if(direction != "Up") {
					direction = "Down";
				}
				break;
				
			//Startar om spelet genom att återställa alla värden till vad de var innan rundan började, 
			//samt kalla funktionerna startGame och repaint
			case KeyEvent.VK_ENTER:
				if(!running) {
					DELAY = 70;
					score = 0;

					Snake.clear();
					startGame();
					repaint();
				}
				break;
			}
		}
	}

}