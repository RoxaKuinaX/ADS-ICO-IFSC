package CleitonRasta;

import robocode.*;
//import java.awt.Color;

/**
 * OlhaAPedra - a robot by (your name here)
 */
public class OlhaAPedra extends Robot {

	String alvo = null;

	/**
	 * run: comportamento principal
	 */
	public void run() {

		// setColors(Color.red,Color.blue,Color.green);

		while(true) {

			// Se não tiver alvo, caça outro robô
			if(alvo == null) {

				turnGunRight(360);

				// Movimento procurando inimigos
				ahead(100);
				turnRight(45);
			}
			else {

				// Continua escaneando o alvo
				turnGunRight(360);
			}
		}
	}

	/**
	 * Quando encontrar um robô
	 */
	public void onScannedRobot(ScannedRobotEvent e) {

		// Define alvo
		alvo = e.getName();

		// Mira no inimigo
		turnGunRight(getHeading() - getGunHeading() + e.getBearing());

		// Atira
		fire(3);

		// Movimento de strafing (movimento lateral)
		turnRight(e.getBearing() + 90);

		// Anda lateralmente
		ahead(150);
	}

	/**
	 * Quando o alvo morrer
	 */
	public void onRobotDeath(RobotDeathEvent e) {

		// Procura outro alvo
		if(e.getName().equals(alvo)) {
			alvo = null;
		}
	}

	/**
	 * Quando levar tiro
	 */
	public void onHitByBullet(HitByBulletEvent e) {

		// Desvia
		turnRight(90);
		ahead(100);
	}

	/**
	 * Quando bater na parede
	 */
	public void onHitWall(HitWallEvent e) {

		back(50);
		turnRight(90);
	}
}
