package ch.epfl.moocprog;

import ch.epfl.moocprog.random.UniformDistribution;
import ch.epfl.moocprog.utils.Time;
import ch.epfl.moocprog.utils.Utils;
import ch.epfl.moocprog.utils.Vec2d;

import static ch.epfl.moocprog.app.Context.getConfig;
import static ch.epfl.moocprog.config.Config.*;

public abstract class Animal extends Positionable {

	private double direction;
	private int hitpoints;
	private Time lifespan;
	private Time rotationDelay;

	public Animal(ToricPosition position, int hitpoints, Time lifespan) {
		super(position);
		this.hitpoints = hitpoints;
		this.lifespan = lifespan;
		this.direction = UniformDistribution.getValue(0, 2 * Math.PI);
		this.rotationDelay = Time.ZERO;
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
		//appel a eftermove
		this.afterMoveDispatch(env, dt);

	}

	public final void update(AnimalEnvironmentView env, Time dt) {
		// multiplier le temps dt par la valeur parametrable
		Time time = dt.times(getConfig().getDouble(ANIMAL_LIFESPAN_DECREASE_FACTOR));
		this.lifespan = this.lifespan.minus(time);

		if (!this.isDead()) {
			this.specificBehaviorDispatch(env, dt);
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
		return Utils.pickValue(this.computeRotationProbsDispatch(env).getAngles(), this.computeRotationProbsDispatch(env).getProbabilities());
	}

	public abstract void accept(AnimalVisitor visitor, RenderingMedia s);

	protected abstract void specificBehaviorDispatch(AnimalEnvironmentView env, Time dt);
	protected abstract RotationProbability computeRotationProbsDispatch(AnimalEnvironmentView env);
	protected abstract void afterMoveDispatch(AnimalEnvironmentView env, Time dt);

}
