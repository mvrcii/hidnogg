package stickfight2d.world;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import stickfight2d.Main;
import stickfight2d.controllers.CameraController;
import stickfight2d.enums.DirectionType;

import java.util.*;
import java.lang.Math;

public class ParticleEmitter extends GameObject {

    private PlayerObject playerObject;

    private int amount;
    private int speed;
    private int speedRandomness;
    private double angle;
    private int angleRandomness;
    private boolean mirrored = true;
    private final List<ParticleObject> particles;
    private int totalTime;
    private String color;
    private int size;

    public ParticleEmitter(PlayerObject playerObject, int x, int y, DirectionType direction, int amount, int time, int speed, int speedRandomness, int angle, int angleRandomness,String color,int size) {
        super(x, y, direction);
        this.playerObject = playerObject;
        this.totalTime = time;
        this.amount = amount;
        this.speed = speed;
        this.speedRandomness = speedRandomness;
        this.angle = Math.toRadians(angle);
        this.angleRandomness = angleRandomness;
        this.color=color;
        this.size=size;
        particles = new ArrayList<ParticleObject>();
    }

    public void emit(long diffMillis, int count) {
        Random rng = new Random();
        SplittableRandom rand = new SplittableRandom();
        Point2D drawPoint = CameraController.getInstance().convertWorldToScreen(x, y);

        for (int i = 0; i < count; i++) {
            //TODO BUG: No continuous degree possible
            //TODO BUG: Particles left from emitter behave slightly differently than right from it
            //double rngAngle = randomAngleInvert(1.0+(i*0.1));
            //double rngAngle = rand.nextDouble((Math.PI/4.0),(3.0*Math.PI)/4.0);
            double rngAngle = randomAngleInvert(angle + Math.toRadians(rng.nextInt(angleRandomness) - angleRandomness / 2.0));//randomAngleInvert
            //double rngAngle = randomAngleInvert(angle+Math.toRadians(continuousRng((totalTime+ (count-i)*diffMillis /(double)count)/10.0)*angleRandomness));

            //double rngSpeed = speed;
            //double rngSpeed = Math.max(5,speed+rng.nextInt(speedRandomness)-speedRandomness/2);
            double rngSpeed = (speed+continuousRng((totalTime+ (count-i)*diffMillis /(double)count)/150.0)*speedRandomness);//(continuousRng((totalTime)/100.0)*speedRandomness)

            //System.out.println(rngAngle);
            particles.add(new ParticleObject(x, y, Math.cos(rngAngle) * rngSpeed, Math.sin(rngAngle) * rngSpeed, 1000,color,size));
        }
    }

    @Override
    public void update(long diffMillis) {
        //TODO Combine overlapping particles and combine direction vector to reduce particle count
        if (amount > 0 && totalTime > 0) {
            int count = (int) (amount * diffMillis) / totalTime;
            emit(diffMillis, count);
            amount -= count;
            totalTime -= diffMillis;
        }

        if (particles.size() > 0) {
            Iterator<ParticleObject> itr = particles.iterator();
            while (itr.hasNext()) {
                ParticleObject p = itr.next();
                p.update(diffMillis);
                if (p.y > Main.canvas.getHeight() || !p.alive) {
                    itr.remove();
                }
            }
        }
    }

    @Override
    public void draw(GraphicsContext gc) {
        for (ParticleObject p : particles) {
            p.draw(gc);
        }
    }

    private double continuousRng(double x) {
        //https://www.geogebra.org/graphing/yzgxvd8q adjusted so it gives continuous pseudo random numbers between 0 and 1
        return 0.5 + 0.169 * (Math.sin(x) + Math.sin(Math.E * x) + Math.sin(Math.PI * x));
    }

    private double randomAngleInvert(double angle) {
        Random rng = new Random();
        if (mirrored && rng.nextBoolean()) {
            return (Math.PI - angle) % (2 * Math.PI);
        } else {
            return angle;
        }
    }
}

