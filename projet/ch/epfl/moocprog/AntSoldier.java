package ch.epfl.moocprog;

import static ch.epfl.moocprog.app.Context.getConfig;
import static ch.epfl.moocprog.config.Config.*;

import ch.epfl.moocprog.utils.Time;

public final class AntSoldier extends Ant {

	public AntSoldier(ToricPosition position, Uid anthillId) {
		super(position, getConfig().getInt(ANT_SOLDIER_HP), getConfig().getTime(ANT_SOLDIER_LIFESPAN), anthillId);

	}

	public AntSoldier(ToricPosition position, Uid anthillId, AntRotationProbabilityModel probModel) {
		super(position, getConfig().getInt(ANT_SOLDIER_HP), getConfig().getTime(ANT_SOLDIER_LIFESPAN), anthillId,
				probModel);

	}

	@Override
	public double getSpeed() {
		return getConfig().getDouble(ANT_SOLDIER_SPEED);
	}
	@Override
	public int getMinAttackStrength() {
		
		return getConfig().getInt(ANT_SOLDIER_MIN_STRENGTH);
	}

	@Override
	public int getMaxAttackStrength() {
		
		return getConfig().getInt(ANT_SOLDIER_MAX_STRENGTH);
	}

	@Override
	public Time getMaxAttackDuration() {
		
		return getConfig().getTime(ANT_SOLDIER_ATTACK_DURATION);
	}

	protected void seekForEnemies(AntEnvironmentView env, Time dt) {
		this.move(env, dt);
		this.fight(env, dt);
	}

	@Override
	public void accept(AnimalVisitor visitor, RenderingMedia s) {
		visitor.visit(this, s);
	}

	@Override
	public void specificBehaviorDispatch(AnimalEnvironmentView env, Time dt) {
		// A ce moment là, on sait que l'on à affaire à un AntWorker.// Grâce à l'appel
		// suivant, on informe AnimalEnvironmentView de notre type !
		if (env == null) {
			throw new IllegalArgumentException();
		}
		env.selectSpecificBehaviorDispatch(this, dt);

	}

	

}