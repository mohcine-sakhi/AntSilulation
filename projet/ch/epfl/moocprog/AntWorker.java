package ch.epfl.moocprog;

import static ch.epfl.moocprog.app.Context.getConfig;
import static ch.epfl.moocprog.config.Config.*;

import ch.epfl.moocprog.utils.Time;

public final class AntWorker extends Ant {

	private double foodQuantity;

	public AntWorker(ToricPosition position, Uid anthillId) {
		super(position, getConfig().getInt(ANT_WORKER_HP), getConfig().getTime(ANT_WORKER_LIFESPAN), anthillId);
		this.foodQuantity = 0;
	}

	public double getFoodQuantity() {
		return foodQuantity;
	}

	@Override
	public double getSpeed() {
		return getConfig().getDouble(ANT_WORKER_SPEED);
	}

	@Override
	public String toString() {

		String chaine = super.toString() + "\n";
		chaine += String.format("Quantity : %.2f", getFoodQuantity());

		return chaine;
	}

	protected void seekForFood(AntWorkerEnvironmentView env, Time dt) {
		this.move(dt);
	}

	@Override
	public void accept(AnimalVisitor visitor, RenderingMedia s) {
		visitor.visit(this, s);
	}

	@Override
	public void specificBehaviorDispatch(AnimalEnvironmentView env, Time dt) {
		// A ce moment là, on sait que l'on à affaire à un AntWorker.
		// Grâce à l'appel suivant, on informe AnimalEnvironmentView de notre type !
		if (env == null) {
			throw new IllegalArgumentException();
		}
		env.selectSpecificBehaviorDispatch(this, dt);

	}

}
