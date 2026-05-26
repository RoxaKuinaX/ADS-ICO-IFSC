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

        while (true) {
            setTurnRadarRight(999999);
            execute();
        }
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        
        // === RADAR ===
        
        double radarTurn = getHeadingRadians()
                + e.getBearingRadians()
                - getRadarHeadingRadians();
        setTurnRadarRightRadians(2 * Utils.normalRelativeAngle(radarTurn));

        // === MIRA COM PREDIÇÃO ===
        
        double bulletPower = Math.min(3.0, getEnergy());
        double distance = e.getDistance();
        double time = distance / (20 - 3 * bulletPower);

        double futureX = getX()
                + Math.sin(e.getBearingRadians() + getHeadingRadians()) * distance
                + Math.sin(e.getHeadingRadians()) * e.getVelocity() * time;
        double futureY = getY()
                + Math.cos(e.getBearingRadians() + getHeadingRadians()) * distance
                + Math.cos(e.getHeadingRadians()) * e.getVelocity() * time;

        double absAngle = Math.atan2(futureX - getX(), futureY - getY());
        setTurnGunRightRadians(
                Utils.normalRelativeAngle(absAngle - getGunHeadingRadians())
        );

        // === MOVIMENTO COM DESVIO DE PAREDE ===
        
        double wallMargin = 80;
        boolean nearWall = getX() < wallMargin
                || getX() > getBattleFieldWidth() - wallMargin
                || getY() < wallMargin
                || getY() > getBattleFieldHeight() - wallMargin;

        if (nearWall) {
            double angleToCenter = Math.atan2(
                    getBattleFieldWidth() / 2 - getX(),
                    getBattleFieldHeight() / 2 - getY()
            );
            setTurnRightRadians(
                    Utils.normalRelativeAngle(angleToCenter - getHeadingRadians())
            );
        } else {
            setTurnRight(e.getBearing() + 90);
        }

        if (Math.random() > 0.85 || getDistanceRemaining() < 20) {
            direcao *= -1;
        }
        setAhead((120 + Math.random() * 80) * direcao);

        // === TIRO SEGURO ===
        
        if (getGunHeat() == 0 && Math.abs(getGunTurnRemaining()) < 3) {
            double power;
            if (distance < 150)      power = 3.0;
            else if (distance < 300) power = 2.0;
            else                     power = 1.0;

            power = Math.min(power, getEnergy() / 4);
            if (power >= 0.1) fire(power);
        }
    }

    public void onHitByBullet(HitByBulletEvent e) {
        direcao *= -1;
        setTurnRight(90 - e.getBearing());
        setAhead(150 * direcao);
        execute();
    }

    public void onHitWall(HitWallEvent e) {
        direcao *= -1;
        setBack(100);
        setTurnRight(90);
        execute();
    }

    public void onHitRobot(HitRobotEvent e) {
        fire(3);
        setBack(50);
        execute();
    }
}
