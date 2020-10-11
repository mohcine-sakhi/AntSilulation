package ch.epfl.moocprog;

import ch.epfl.moocprog.utils.Time;
import ch.epfl.moocprog.utils.Vec2d;

import static ch.epfl.moocprog.app.Context.getConfig;
import static ch.epfl.moocprog.config.Config.*;

public abstract class Ant extends Animal {

	private Uid anthillId;
	// postion de la formi à sa création
	private ToricPosition lastPos;

	private AntRotationProbabilityModel probModel;

	public Ant(ToricPosition position, int hitpoints, Time lifespan, Uid anthillId) {
		super(position, hitpoints, lifespan);
		this.anthillId = anthillId;
		this.lastPos = this.getPosition();
		this.probModel = new PheromoneRotationProbabilityModel();
	}

	public Ant(ToricPosition position, int hitpoints, Time lifespan, Uid anthillId,
			AntRotationProbabilityModel probModel) {
		super(position, hitpoints, lifespan);
		this.anthillId = anthillId;
		this.lastPos = this.getPosition();
		this.probModel = probModel;
	}

	public final Uid getAnthillId() {
		return anthillId;
	}

	private final void spreadPheromones(AntEnvironmentView env) {
		ToricPosition currentPos = this.getPosition();

		double distance = currentPos.toricDistance(lastPos);
		// System.out.println("distance : " +distance);

		// nombre d'instances à créer
		int instances = (int) (distance * getConfig().getDouble(ANT_PHEROMONE_DENSITY));
		// System.out.println("instances : "+instances);

		// récupérer le vecteur de déplacement fait par la fourmi pour savoir ou placer
		// les pheromones
		Vec2d vecteurToric = lastPos.toricVector(currentPos).scalarProduct(1.0 / instances);

		Pheromone pheromone;
		double quantity = getConfig().getDouble(ANT_PHEROMONE_ENERGY);

		for (int i = 1; i <= instances; ++i) {
			lastPos = lastPos.add(vecteurToric);
			pheromone = new Pheromone(lastPos, quantity);
			env.addPheromone(pheromone);

		}
	}

	@Override
	protected final RotationProbability computeRotationProbsDispatch(AnimalEnvironmentView env) {
		return env.selectComputeRotationProbsDispatch(this);
	}

	protected final RotationProbability computeRotationProbs(AntEnvironmentView env) {
		return this.probModel.computeRotationProbs(super.computeDefaultRotationProbs(), this.getPosition(), this.getDirection(), env);
	}

	protected final void afterMoveAnt(AntEnvironmentView env, Time dt) {
		this.spreadPheromones(env);
	}

	@Override
	protected final void afterMoveDispatch(AnimalEnvironmentView env, Time dt) {
		env.selectAfterMoveDispatch(this, dt);
	}

}
