package ch.epfl.moocprog;

import static ch.epfl.moocprog.app.Context.getConfig;
import static ch.epfl.moocprog.config.Config.*;

import ch.epfl.moocprog.utils.Time;

public final class Termite extends Animal {

	public Termite(ToricPosition position) {
		super(position, getConfig().getInt(TERMITE_HP), getConfig().getTime(TERMITE_LIFESPAN));
	}

	@Override
	public void accept(AnimalVisitor visitor, RenderingMedia s) {
		visitor.visit(this, s);
	}

	@Override
	public double getSpeed() {

		return getConfig().getDouble(TERMITE_SPEED);
	}

	@Override
	public int getMinAttackStrength() {
		
		return getConfig().getInt(TERMITE_MIN_STRENGTH);
	}

	@Override
	public int getMaxAttackStrength() {
		
		return getConfig().getInt(TERMITE_MAX_STRENGTH);
	}

	@Override
	public Time getMaxAttackDuration() {
		
		return getConfig().getTime(TERMITE_ATTACK_DURATION);
	}

	protected void seekForEnemies(AnimalEnvironmentView env, Time dt) {
		this.move(env, dt);
		this.fight(env, dt);
	}

	@Override
	public void specificBehaviorDispatch(AnimalEnvironmentView env, Time dt) {
		env.selectSpecificBehaviorDispatch(this, dt);

	}

	@Override
	protected RotationProbability computeRotationProbsDispatch(AnimalEnvironmentView env) {
		return env.selectComputeRotationProbsDispatch(this);
	}

	protected final RotationProbability computeRotationProbs(AntEnvironmentView env) {
		return this.computeDefaultRotationProbs();
	}

	@Override
	protected void afterMoveDispatch(AnimalEnvironmentView env, Time dt) {
		env.selectAfterMoveDispatch(this, dt);

	}

	protected final void afterMoveTermite(AntEnvironmentView env, Time dt) {

	}

	@Override
	protected final boolean isEnemy(Animal animal) {
		if (animal == null) {
			throw new IllegalArgumentException();
		}
		return ! this.isDead() && ! animal.isDead() && animal.isEnemyDispatch(this);
	}

	@Override
	protected final boolean isEnemyDispatch(Termite other) {
		if (other == null) {
			throw new IllegalArgumentException();
		}
		return false;
	}

	@Override
	protected final boolean isEnemyDispatch(Ant other) {
		if (other == null) {
			throw new IllegalArgumentException();
		}
		return true;
	}

}