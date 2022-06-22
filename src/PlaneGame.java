import acm.graphics.*;
import acm.program.GraphicsProgram;
import acm.util.RandomGenerator;
import acm.util.SoundClip;

import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * Plane game class
 * 
 * @author Vladyslav Marchenko
 * @Files Samolet.java
 */

//To run this program you need to run PlaneGame.main() (on the left of the 18th line).
//Also, you need to stretch the game window a bit to the bottom and right so that you can see game conveniently.
public class PlaneGame extends GraphicsProgram {
	// constants for the world
	private final int WIDTH = 1600;
	private final int HEIGHT = 900;
	private final int DELAY = 6;
	private final int TANK_HEIGHT = 38;
	private final int TANK_WIDTH = 120;
	private final int CITY_HEIGHT = 75;
	private final int CITY_WIDTH = 100;
	private final int BOMB_HEIGHT = 40;
	private final int BOMB_WIDTH = 23;
	private final int PLANE_HEIGHT = 64;
	private final int PLANE_WIDTH = 160;
	private final double GRAVITY = 0.05;

	// objects' speed values
	private double planeSpeed =3;
	private double tankSpeed = 1;
	private double bombSpeed = 0;

	// other values
	private int bombsLeft = 10;
	private int targetsLeft = 6;
	private int colorCount = 1;
	private boolean planeAlive = true;

	// counters to stop gif animation
	private int gifCounter = 0;
	private int gifMax = 150;

	// background image
	private GImage background = new GImage("resources/background.png");

	// tanks and immovable targets
	private GImage tank1 = new GImage("resources/tankRIGHT.png");
	private GImage tank2 = new GImage("resources/tankRIGHT.png");
	private GImage tank3 = new GImage("resources/tankRIGHT.png");
	private GImage target1 = new GImage("resources/city1.png");
	private GImage target2 = new GImage("resources/city1.png");
	private GImage target3 = new GImage("resources/city1.png");

	// values which show the current image of the tanks and the plane
	private int image1;
	private int image2;
	private int image3;
	private int imagePlane = 1;

	// gifs and sounds
	private GImage explosion = new GImage("resources/explosion.gif");
	private SoundClip svist = new SoundClip("resources/svist.wav");
	private SoundClip expl = new SoundClip("resources/expl.wav");
	private SoundClip backMusic = new SoundClip("resources/backMusic.wav");
	private SoundClip winMusic = new SoundClip("resources/vika.wav");
	private SoundClip defeat = new SoundClip("resources/defeat.wav");

	// bomg image
	private GImage bomb = new GImage("resources/bomb.png");

	// plane image
	private GImage plane = new GImage("resources/planeRIGHT.png");

	// label which shows how many bombs there are left
	private GLabel bombsLabel = new GLabel("BOMBS LEFT: " + bombsLeft);

	// final screen in the end
	private GRect window = new GRect(WIDTH, HEIGHT);
	private GLabel result = new GLabel("");

	public void run() {
		this.setSize(WIDTH, HEIGHT);
		backMusic.setVolume(1);
		expl.setVolume(1);
		svist.setVolume(1);
		winMusic.setVolume(1);
		defeat.setVolume(1);
		backMusic.loop();
		background.setSize(WIDTH, HEIGHT);
		bombsLabel.setFont("vivan-50");
		bombsLabel.setLocation((WIDTH - bombsLabel.getWidth()) / 2,
				(HEIGHT - bombsLabel.getAscent()) / 2);
		result.setFont("vivan-70");
		result.setColor(Color.white);
		window.setFilled(true);
		bomb.setSize(BOMB_WIDTH, BOMB_HEIGHT);
		plane.setSize(PLANE_WIDTH, PLANE_HEIGHT);
		explosion.setSize(90, 90);
		add(background);
		add(bombsLabel);
		add(bomb);
		add(plane);
		addTargets();
		add(explosion);
		add(window);
		add(result);
		bomb.setVisible(false);
		explosion.setVisible(false);
		window.setVisible(false);
		result.setVisible(false);

		addKeyListeners();

		while (gameContinues() || explosion.isVisible()) {
			moveTank1();
			moveTank2();
			moveTank3();
			if (planeAlive) {
				movePlane();
			}
			if (bomb.isVisible()) {
				moveBomb();
			}
			if (bomb.getY() > HEIGHT || collisionExists()) {
				bomb.setVisible(false);
				bombSpeed = 0;
				bomb.setLocation(-100, -100);
			}
			if (gifCounter > 0) {
				gifCounter++;
			}
			if (gifCounter == gifMax) {
				explosion.setVisible(false);
				expl.stop();
				expl.rewind();
				gifCounter = 0;
			}
			if (colorCount % 10 == 0) {
				bombsLabel.setColor(rgen.nextColor());
			}
			colorCount++;
			if (!bomb.isVisible()) {
				svist.stop();
				svist.rewind();
			}
			pause(DELAY);
		}
		backMusic.stop();
		window.setVisible(true);
		if (targetsLeft == 0) {
			result.setLabel("YOU HAVE WON EZ!");
			winMusic.loop();
		} else {
			result.setLabel("YOU HAVE LOST");
			defeat.play();
		}
		result.setLocation((WIDTH - result.getWidth()) / 2,
				(HEIGHT - result.getAscent()) / 3);
		result.setVisible(true);
	}

	/**
	 * Check if bomb collides with any target and destroy such target with an
	 * explosion
	 * 
	 * @return True if collision exists at the moment
	 */
	private boolean collisionExists() {
		if (tank1.contains(bomb.getX(), bomb.getY() + BOMB_HEIGHT)
				|| tank1.contains(bomb.getX() + BOMB_WIDTH, bomb.getY()
						+ BOMB_HEIGHT)) {
			if (tank1.isVisible()) {
				targetsLeft--;
				explosion.setLocation(tank1.getX(), tank1.getY() - 60);
				explosion.setVisible(true);
				expl.stop();
				expl.rewind();
				expl.play();
				tank1.setVisible(false);
				tank1.move(500, 500);
				gifCounter = 1;
				remove(tank1);
				return true;
			}
		}
		if (tank2.contains(bomb.getX(), bomb.getY() + BOMB_HEIGHT)
				|| tank2.contains(bomb.getX() + BOMB_WIDTH, bomb.getY()
						+ BOMB_HEIGHT)) {
			if (tank2.isVisible()) {
				targetsLeft--;
				explosion.setLocation(tank2.getX(), tank2.getY() - 60);
				explosion.setVisible(true);
				expl.stop();
				expl.rewind();
				expl.play();
				tank2.setVisible(false);
				tank2.move(500, 500);
				gifCounter = 1;
				remove(tank2);
				return true;
			}
		}
		if (tank3.contains(bomb.getX(), bomb.getY() + BOMB_HEIGHT)
				|| tank3.contains(bomb.getX() + BOMB_WIDTH, bomb.getY()
						+ BOMB_HEIGHT)) {
			if (tank3.isVisible()) {
				targetsLeft--;
				explosion.setLocation(tank3.getX(), tank3.getY() - 60);
				explosion.setVisible(true);
				expl.stop();
				expl.rewind();
				expl.play();
				tank3.setVisible(false);
				tank3.move(500, 500);
				gifCounter = 1;
				remove(tank3);
				return true;
			}
		}
		if (target1.contains(bomb.getX(), bomb.getY() + BOMB_HEIGHT)
				|| target1.contains(bomb.getX() + BOMB_WIDTH, bomb.getY()
						+ BOMB_HEIGHT)) {
			if (target1.isVisible()) {
				targetsLeft--;
				explosion.setLocation(target1.getX(), target1.getY() - 60);
				explosion.setVisible(true);
				expl.stop();
				expl.rewind();
				expl.play();
				target1.setVisible(false);
				target1.move(500, 500);
				gifCounter = 1;
				remove(target1);
				return true;
			}
		}
		if (target2.contains(bomb.getX(), bomb.getY() + BOMB_HEIGHT)
				|| target2.contains(bomb.getX() + BOMB_WIDTH, bomb.getY()
						+ BOMB_HEIGHT)) {
			if (target2.isVisible()) {
				targetsLeft--;
				explosion.setLocation(target2.getX(), target2.getY() - 60);
				explosion.setVisible(true);
				expl.stop();
				expl.rewind();
				expl.play();
				target2.setVisible(false);
				target2.move(500, 500);
				gifCounter = 1;
				remove(target2);
				return true;
			}
		}
		if (target3.contains(bomb.getX(), bomb.getY() + BOMB_HEIGHT)
				|| target3.contains(bomb.getX() + BOMB_WIDTH, bomb.getY()
						+ BOMB_HEIGHT)) {
			if (target3.isVisible()) {
				targetsLeft--;
				explosion.setLocation(target3.getX(), target3.getY() - 60);
				explosion.setVisible(true);
				expl.stop();
				expl.rewind();
				expl.play();
				target3.setVisible(false);
				target3.move(500, 500);
				gifCounter = 1;
				remove(target3);
				return true;
			}
		}
		return false;
	}

	/**
	 * Move the bomb along with gravity
	 */
	private void moveBomb() {
		bomb.move(0, bombSpeed);
		if (bomb.getX() > WIDTH - BOMB_WIDTH) {
			bomb.move(WIDTH - BOMB_WIDTH - bomb.getX(), 0);
		}
		if (bomb.getX() < 0) {
			bomb.move(-bomb.getX(), 0);
		}
		bombSpeed += GRAVITY;
	}

	/**
	 * Move plane depending on where it is faced to. Turn plane around and move
	 * it a bit to the bottom if it is at the end of the world
	 */
	private void movePlane() {
		if (!plane.isVisible()) {
			return;
		}
		if (imagePlane == 1) {
			if (plane.getX() > WIDTH) {
				plane.setImage("planeLEFT.png");
				imagePlane = 2;
				plane.move(0, PLANE_HEIGHT);
			} else {
				plane.move(planeSpeed, 0);
			}
		} else if (imagePlane == 2) {
			if (plane.getX() < -PLANE_WIDTH) {
				plane.setImage("planeRIGHT.png");
				imagePlane = 1;
				plane.move(0, PLANE_HEIGHT);
			} else {
				plane.move(-planeSpeed, 0);
			}
		}
	}

	/**
	 * Move tank 1 depending on its direction. Turn it around at the end of the world
	 */
	private void moveTank1() {
		if (!tank1.isVisible()) {
			return;
		}
		if (image1 == 1) {
			if (tank1.getX() > WIDTH - TANK_WIDTH) {
				tank1.setImage("tankLEFT.png");
				image1 = 2;
			} else {
				tank1.move(tankSpeed, 0);
			}
		} else if (image1 == 2) {
			if (tank1.getX() < tankSpeed) {
				tank1.setImage("tankRIGHT.png");
				image1 = 1;
			} else {
				tank1.move(-tankSpeed, 0);
			}
		}
	}

	/**
	 * Move tank 2 depending on its direction. Turn it around at the end of the world
	 */
	private void moveTank2() {
		if (!tank2.isVisible()) {
			return;
		}
		if (image2 == 1) {
			if (tank2.getX() > WIDTH - TANK_WIDTH) {
				tank2.setImage("tankLEFT.png");
				image2 = 2;
			} else {
				tank2.move(tankSpeed, 0);
			}
		} else if (image2 == 2) {
			if (tank2.getX() < tankSpeed) {
				tank2.setImage("tankRIGHT.png");
				image2 = 1;
			} else {
				tank2.move(-tankSpeed, 0);
			}
		}
	}

	/**
	 * Move tank 3 depending on its direction. Turn it around at the end of the world
	 */
	private void moveTank3() {
		if (!tank3.isVisible()) {
			return;
		}
		if (image3 == 1) {
			if (tank3.getX() > WIDTH - TANK_WIDTH) {
				tank3.setImage("tankLEFT.png");
				image3 = 2;
			} else {
				tank3.move(tankSpeed, 0);
			}
		} else if (image3 == 2) {
			if (tank3.getX() < tankSpeed) {
				tank3.setImage("tankRIGHT.png");
				image3 = 1;
			} else {
				tank3.move(-tankSpeed, 0);
			}
		}
	}

	/**
	 * Check if the game is continuing
	 * 
	 * @return False if game is ended
	 */
	private boolean gameContinues() {
		if ((bombsLeft == 0 && !bomb.isVisible()) || targetsLeft == 0 || !planeAlive) {
			return false;
		}
		if(plane.getY() >= HEIGHT - CITY_HEIGHT - PLANE_HEIGHT){
			plane.scale(4);
			plane.setLocation((WIDTH - plane.getWidth())/2, HEIGHT*3/4 - plane.getHeight()/2);
			explosion.setSize(plane.getWidth(), plane.getHeight());
			explosion.setLocation(plane.getX(), plane.getY());
			explosion.setVisible(true);
			gifCounter = 1;
			expl.stop();
			expl.rewind();
			expl.loop();
			planeAlive = false;
			gifMax = 600;
			return false;
		}
		return true;
	}

	/**
	 * Drop a new bomb from an airplane by pressing any key
	 */
	public void keyTyped(KeyEvent e) {
		if (!bomb.isVisible() && bombsLeft > 0 && planeAlive) {
			bomb.setLocation(plane.getX() + PLANE_WIDTH / 2 - BOMB_WIDTH / 2,
					plane.getY() + PLANE_HEIGHT / 2);
			bomb.setVisible(true);
			svist.stop();
			svist.rewind();
			svist.play();
			bombsLeft--;
			bombsLabel.setLabel("BOMBS LEFT: " + bombsLeft);
		}
	}

	/**
	 * Add all targets on the ground in the beginning of the game
	 */
	private void addTargets() {
		image1 = rgen.nextInt(1, 2);
		image2 = rgen.nextInt(1, 2);
		image3 = rgen.nextInt(1, 2);

		if (image1 == 1) {
			tank1.setImage("tankRIGHT.png");
		} else {
			tank1.setImage("tankLEFT.png");
		}
		if (image2 == 1) {
			tank2.setImage("tankRIGHT.png");
		} else {
			tank2.setImage("tankLEFT.png");
		}
		if (image3 == 1) {
			tank3.setImage("tankRIGHT.png");
		} else {
			tank3.setImage("tankLEFT.png");
		}

		tank1.setSize(TANK_WIDTH, TANK_HEIGHT);
		tank2.setSize(TANK_WIDTH, TANK_HEIGHT);
		tank3.setSize(TANK_WIDTH, TANK_HEIGHT);

		target1.setSize(CITY_WIDTH, CITY_HEIGHT);
		target2.setSize(CITY_WIDTH, CITY_HEIGHT);
		target3.setSize(CITY_WIDTH, CITY_HEIGHT);

		add(target1, rgen.nextDouble(0, 400), HEIGHT - CITY_HEIGHT);
		add(target2, rgen.nextDouble(500, 1000), HEIGHT - CITY_HEIGHT);
		add(target3, rgen.nextDouble(1100, WIDTH - CITY_WIDTH), HEIGHT
				- CITY_HEIGHT);

		tank1.setVisible(true);
		tank2.setVisible(true);
		tank3.setVisible(true);

		target1.setVisible(true);
		target2.setVisible(true);
		target3.setVisible(true);

		add(tank1, rgen.nextDouble(0, 400), HEIGHT - TANK_HEIGHT);
		add(tank2, rgen.nextDouble(500, 1000), HEIGHT - TANK_HEIGHT);
		add(tank3, rgen.nextDouble(1100, WIDTH - TANK_WIDTH), HEIGHT
				- TANK_HEIGHT);
	}

	//Random Generator
	RandomGenerator rgen = RandomGenerator.getInstance();
}
