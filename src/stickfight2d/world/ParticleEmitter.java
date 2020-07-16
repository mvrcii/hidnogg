package stickfight2d.world;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import stickfight2d.GameLoop;
import stickfight2d.Main;
import stickfight2d.controllers.CameraController;
import stickfight2d.enums.DirectionType;
import stickfight2d.enums.ParticleType;
import stickfight2d.interfaces.ParticleOwner;

import java.util.*;
import java.lang.Math;

public class ParticleEmitter extends GameObject {

    private final GameObject gameObject;
    private final ParticleType particleType;

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

    public ParticleEmitter(ParticleType particleType, GameObject gameObject, int x, int y, int amount, int decayTime, int speed, int speedRandomness, int angle, int angleRandomness, String color, int size) {
        super(x, y, null);
        this.gameObject = gameObject;
        this.totalTime = decayTime;
        this.amount = amount;
        this.speed = speed;
        this.speedRandomness = speedRandomness;
        this.angle = Math.toRadians(angle);
        this.angleRandomness = angleRandomness;
        this.color = color;
        this.size = size;
        this.particleType = particleType;
        this.particles = new ArrayList<>();
    }


    @Override
    public void update(long diffMillis) {
        // Spawning particles until the amount and totalTime is 0
        if (amount > 0 && totalTime > 0) {
            int count = (int) (amount * diffMillis) / totalTime;
            emit(diffMillis, count);
            amount -= count;
            totalTime -= diffMillis;
        }

        // Check to remove dead particles
        if (particles.size() > 0) {
            ListIterator<ParticleObject> itr = particles.listIterator();

            while (itr.hasNext()) {

                ParticleObject p = itr.next();
                p.update(diffMillis);

                if (p.y > Main.canvas.getHeight() || !p.alive) {
                    itr.remove();
                }
                //particles.removeIf(particle -> (particle.y > Main.canvas.getHeight() || !particle.alive));

            }
        }else{  // Remove Particle Emitter from GameObjects when finished
            GameLoop.currentLevel.removeGameObject(this);
        }

        // Instantly clear all the particles if map state changes
        if(gameObject != null){
            if(ParticleOwner.class.isAssignableFrom(gameObject.getClass())){
                if(((ParticleOwner) gameObject).isClearCondition()){
                    this.particles.clear();
                    GameLoop.currentLevel.removeGameObject(this);
                }
            }
        }

    }

    @Override
    public void draw(GraphicsContext gc) {
        for (ParticleObject particle: particles) {
            particle.draw(gc);
        }
    }

    public void emit(long diffMillis, int count) {
        Random rng = new Random();

        for (int i = 0; i < count; i++) {
            //TODO BUG: No continuous degree possible
            //TODO BUG: Particles left from emitter behave slightly differently than right from it

            double rngAngle = randomAngleInvert(angle + Math.toRadians(rng.nextInt(angleRandomness) - angleRandomness / 2.0));//randomAngleInvert
            double rngSpeed = (speed+continuousRng((totalTime+ (count-i)*diffMillis /(double)count)/150.0)*speedRandomness);//(continuousRng((totalTime)/100.0)*speedRandomness)

            particles.add(new ParticleObject(particleType, x, y, Math.cos(rngAngle) * rngSpeed, Math.sin(rngAngle) * rngSpeed, 1000,color,size));
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

