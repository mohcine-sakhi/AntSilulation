package ch.epfl.moocprog;

import ch.epfl.moocprog.random.UniformDistribution;
import ch.epfl.moocprog.utils.Time;
import ch.epfl.moocprog.utils.Utils;
import ch.epfl.moocprog.utils.Vec2d;

import static ch.epfl.moocprog.app.Context.getConfig;
import static ch.epfl.moocprog.config.Config.*;

import java.util.List;

public abstract class Animal extends Positionable {

	private double direction;
	private int hitpoints;
	private Time lifespan;
	private Time rotationDelay;
	private Time attackDuration;
	public enum State { IDLE, ESCAPING, ATTACK };
	private State state;
	


	public Animal(ToricPosition position, int hitpoints, Time lifespan) {
		super(position);
		this.hitpoints = hitpoints;
		this.lifespan = lifespan;
		this.direction = UniformDistribution.getValue(0, 2 * Math.PI);
		this.rotationDelay = Time.ZERO;
		this.attackDuration = Time.ZERO;
		this.state = State.IDLE;
	}
	
	public final State getState() {
		return state;
	}
	
	public final void setState(State newState) {
		this.state = newState;
	}

	public final double getDirection() {
		return direction;
	}

	public final void setDirection(double angle) {
		this.direction = angle;
	}

	public final boolean isDead() {
		return (this.getHitpoints() <= 0 || this.getLifespan().toSeconds() <= 0);
	}

	public final int getHitpoints() {
		return hitpoints;
	}

	public final Time getLifespan() {
		return lifespan;
	}

	public abstract double getSpeed();
	public abstract int getMinAttackStrength();
	public abstract int getMaxAttackStrength();
	public abstract Time getMaxAttackDuration();

	@Override
	public String toString() {

		String chaine = super.toString() + "\n";
		chaine += String.format("Speed : %.1f", getSpeed()) + "\n";
		chaine += String.format("HitPoints : %d", getHitpoints()) + "\n";
		chaine += String.format("LifeSpan : %.6f", getLifespan().toSeconds()) + "\n";
		chaine += String.format("State: ", getState());

		return chaine;
	}

	protected final void move(AnimalEnvironmentView env, Time dt) {
		this.rotationDelay = this.rotationDelay.plus(dt);
		Time animalNextGenerationDelay = getConfig().getTime(ANIMAL_NEXT_ROTATION_DELAY);

		// ne faire la rotation qu apres undelai parametrable
		while (this.rotationDelay.minus(animalNextGenerationDelay).isPositive()) {

			this.rotationDelay = this.rotationDelay.minus(animalNextGenerationDelay);
			double gamma = this.rotate(env);
			this.setDirection(this.getDirection() + gamma);
		}
		// le vecteur de déplacement
		Vec2d deplacement = Vec2d.fromAngle(getDirection()).scalarProduct(getSpeed() * dt.toSeconds());

		// Ajouter le deplacement a la position actuelle
		this.setPosition(this.getPosition().add(deplacement));
		// appel a eftermove
		this.afterMoveDispatch(env, dt);

	}

	public final void update(AnimalEnvironmentView env, Time dt) {
		// Gestion de la duree de vie
		Time time = dt.times(getConfig().getDouble(ANIMAL_LIFESPAN_DECREASE_FACTOR));
		this.lifespan = this.lifespan.minus(time);
		
		//gestion du comportement selon l'etat de l'animal 
				if(!this.isDead()) {
					
					switch(state) {
						case ATTACK : if(this.canAttack()) {
										this.fight(env, dt);
									  }else {
										  this.setState(State.ESCAPING);;
										  this.attackDuration = Time.ZERO;
									  }
									  break;
									  
						case ESCAPING : this.escape(env, dt);
										break; 
										
						default : this.specificBehaviorDispatch(env, dt);
					}
				
				}

	}
	public final boolean canAttack() {
		return (! state.equals(State.ESCAPING) && this.attackDuration.compareTo(this.getMaxAttackDuration()) <= 0);
	}
	
	private final void escape(AnimalEnvironmentView env, Time dt) {
		this.move(env, dt);
		if(! env.isVisibleFromEnemies(this)) {
			this.setState(State.IDLE);
		}
	}
	
	protected final void fight(AnimalEnvironmentView env, Time dt) {
		//recuperer l ennemi le plus proche
		List<Animal> ennemisVisibles = env.getVisibleEnemiesForAnimal(this);
		
		Animal animalEnnemiLePlusProche = Utils.closestFromPoint(this, ennemisVisibles);
		
		if(animalEnnemiLePlusProche != null) {
			animalEnnemiLePlusProche.setState(State.ATTACK);
			if(! this.getState().equals(State.ATTACK)){
				this.setState(State.ATTACK);
			}
			// infliger les dégats
			animalEnnemiLePlusProche.hitpoints -= UniformDistribution.getValue(getMinAttackStrength(), getMaxAttackStrength());
			this.attackDuration = this.attackDuration.plus(dt);
			
		}else {
			this.attackDuration = Time.ZERO;
			if(this.getState().equals(State.ATTACK)) {
				this.setState(State.ESCAPING);
			}
		}
			
	}

	protected final RotationProbability computeDefaultRotationProbs() {
		double[] angles = { -180, -100, -55, -25, -10, 0, 10, 25, 55, 100, 180 };

		for (int i = 0; i < angles.length; ++i) {
			angles[i] = Math.toRadians(angles[i]);
		}

		double[] probabilities = { 0.0000, 0.0000, 0.0005, 0.0010, 0.0050, 0.9870, 0.0050, 0.0010, 0.0005, 0.0000,
				0.0000 };

		return new RotationProbability(angles, probabilities);
	}

	private double rotate(AnimalEnvironmentView env) {
		return Utils.pickValue(this.computeRotationProbsDispatch(env).getAngles(),
				this.computeRotationProbsDispatch(env).getProbabilities());
	}

	public abstract void accept(AnimalVisitor visitor, RenderingMedia s);

	protected abstract void specificBehaviorDispatch(AnimalEnvironmentView env, Time dt);

	protected abstract RotationProbability computeRotationProbsDispatch(AnimalEnvironmentView env);

	protected abstract void afterMoveDispatch(AnimalEnvironmentView env, Time dt);

	protected abstract boolean isEnemy(Animal animal);

	protected abstract boolean isEnemyDispatch(Termite termite);

	protected abstract boolean isEnemyDispatch(Ant othanter);

}