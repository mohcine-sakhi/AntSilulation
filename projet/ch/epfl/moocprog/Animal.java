package ch.epfl.moocprog;

import ch.epfl.moocprog.random.UniformDistribution;
import ch.epfl.moocprog.utils.Time;
import ch.epfl.moocprog.utils.Vec2d;

import static ch.epfl.moocprog.app.Context.getConfig;
import static ch.epfl.moocprog.config.Config.*;

public abstract class Animal extends Positionable{
	
	private double direction;
	private int hitpoints;
	private Time lifespan;
	
	public Animal(ToricPosition position, int hitpoints, Time lifespan) {
		super(position);
		this.hitpoints = hitpoints;
		this.lifespan = lifespan;
		this.direction = UniformDistribution.getValue(0, 2 * Math.PI);
	}

	public final double getDirection() {
		return direction;
	}
	
	public final void setDirection(double angle) {
		this.direction = angle;
	}
	
	public final boolean isDead() {
		return (hitpoints <= 0 || lifespan.toSeconds() <= 0);
	}
	
	public final int getHitpoints() {
		return hitpoints;
	}
	
	public final Time getLifespan() {
		return lifespan;
	}
	
	public abstract double getSpeed();
	
	@Override
	public String toString() {
		
		String chaine = super.toString() + "\n";
		chaine += String.format("Speed : %.1f", getSpeed()) + "\n";
		chaine += String.format("HitPoints : %d", getHitpoints()) + "\n";
		chaine += String.format("LifeSpan : %.6f", getLifespan().toSeconds());
		
		return chaine;
	}
	
	protected final void move(Time dt) {
		//le vecteur de déplacement
		Vec2d deplacement = this.getPosition().toVec2d()
												.fromAngle(getDirection())
												.scalarProduct(getSpeed() * dt.toSeconds());
		
		//Ajouter le deplacement a la position actuelle
		this.setPosition(this.getPosition().add(deplacement));
		
	}
	
	public void update(AnimalEnvironmentView env, Time dt) {
		Time time = dt.times(getConfig().getDouble(ANIMAL_LIFESPAN_DECREASE_FACTOR));
		this.lifespan = this.lifespan.minus(time);
		
		if(! this.isDead()) {
			this.move(dt);
		}
		
	}
	
	public abstract void accept(AnimalVisitor visitor, RenderingMedia s);
	
	

}
