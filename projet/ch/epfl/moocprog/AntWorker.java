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

	public AntWorker(ToricPosition position, Uid anthillId, AntRotationProbabilityModel probModel) {
		super(position, getConfig().getInt(ANT_WORKER_HP), getConfig().getTime(ANT_WORKER_LIFESPAN), anthillId,
				probModel);
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
		// la fourmi ne transporte rien
		if (this.getFoodQuantity() == 0) {
			// chercher la source de nourriture la plus proche
			Food food = env.getClosestFoodForAnt(this);
			if (food != null) {
				// puiser de la nourriture
				foodQuantity = food.takeQuantity(getConfig().getDouble(ANT_MAX_FOOD));
				// Faire un demi tour
				double angle = this.getDirection() + Math.PI;
				if (angle >= 2 * Math.PI) {
					angle -= 2 * Math.PI;
				}

				this.setDirection(angle);
			}
		}
		// la fourmi ne transporte rien on a pas fait un else pour prendre le cas ou la
		// fourmi vient juste
		// de puiser de la nouriture
		if (this.getFoodQuantity() != 0) {
			// chercher à déposer la nourriture transportée
			if (env.dropFood(this)) {
				// déposer la nouriture la nourriture
				foodQuantity = 0;
				// Faire un demi tour
				double angle = this.getDirection() + Math.PI;
				if (angle >= 2 * Math.PI) {
					angle -= 2 * Math.PI;
				}

				this.setDirection(angle);
			}
		}
		this.move(env, dt);
	}

	@Override
	public void accept(AnimalVisitor visitor, RenderingMedia s) {
		visitor.visit(this, s);
	}

	@Override
	public void specificBehaviorDispatch(AnimalEnvironmentView env, Time dt) {
		if (env == null) {
			throw new IllegalArgumentException();
		}
		// A ce moment là, on sait que l'on à affaire à un AntWorker.
		// Grâce à l'appel suivant, on informe AnimalEnvironmentView de notre type !
		env.selectSpecificBehaviorDispatch(this, dt);

	}

}
