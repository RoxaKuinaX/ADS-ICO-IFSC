package CleitonRasta;

import robocode.*;
import robocode.util.Utils;
import java.awt.Color;

public class OlhaAPedra extends AdvancedRobot {

    int direcao = 1;

    public void run() {

        // ===== VISUAL =====
        setBodyColor(Color.BLACK);
        setGunColor(Color.RED);
        setRadarColor(Color.WHITE);
        setBulletColor(Color.GREEN);

        // ===== SISTEMA INDEPENDENTE =====
        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);

        while(true) {

            // Radar infinito
            setTurnRadarRight(999999);

            execute();
        }
    }

    public void onScannedRobot(ScannedRobotEvent e) {

        // =========================
        // RADAR TRAVADO NO INIMIGO
        // =========================

        double radarTurn =
                getHeadingRadians()
                + e.getBearingRadians()
                - getRadarHeadingRadians();

        setTurnRadarRightRadians(
                2 * Utils.normalRelativeAngle(radarTurn)
        );

        // =========================
        // MIRA MELHORADA
        // =========================

        // Predição simples do movimento inimigo
        double bulletPower = Math.min(3.0, getEnergy());

        double enemyVelocity = e.getVelocity();

        double enemyHeading = e.getHeadingRadians();

        double distance = e.getDistance();

        double time = distance / (20 - 3 * bulletPower);

        double futureX =
                getX()
                + Math.sin(e.getBearingRadians() + getHeadingRadians())
                * distance
                + Math.sin(enemyHeading) * enemyVelocity * time;

        double futureY =
                getY()
                + Math.cos(e.getBearingRadians() + getHeadingRadians())
                * distance
                + Math.cos(enemyHeading) * enemyVelocity * time;

        double absAngle =
                Math.atan2(
                        futureX - getX(),
                        futureY - getY()
                );

        double gunTurn =
                Utils.normalRelativeAngle(
                        absAngle - getGunHeadingRadians()
                );

        setTurnGunRightRadians(gunTurn);

        // =========================
        // DESVIO DE BALAS
        // =========================

        // Movimento lateral
        setTurnRight(e.getBearing() + 90);

        // Troca direção aleatoriamente
        if(Math.random() > 0.90) {

            direcao *= -1;
        }

        // Movimento curto e imprevisível
        setAhead((120 + Math.random() * 80) * direcao);

        // =========================
        // TIRO PRECISO
        // =========================

        // Só atira quando a arma estiver alinhada
        if(getGunHeat() == 0
                && Math.abs(getGunTurnRemaining()) < 3) {

            // Tiro mais forte perto
            if(distance < 150) {

                fire(3);

            } else if(distance < 300) {

                fire(2);

            } else {

                fire(1);
            }
        }
    }

    public void onHitByBullet(HitByBulletEvent e) {

        // Desvio agressivo ao tomar tiro
        direcao *= -1;

        setTurnRight(90 - e.getBearing());

        setAhead(150 * direcao);
    }

    public void onHitWall(HitWallEvent e) {

        direcao *= -1;

        back(100);

        turnRight(90);
    }

    public void onHitRobot(HitRobotEvent e) {

        // Explode inimigo próximo
        fire(3);

        back(50);
    }
}