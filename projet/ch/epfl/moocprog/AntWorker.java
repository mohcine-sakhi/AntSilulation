package ch.epfl.moocprog;

import static ch.epfl.moocprog.app.Context.getConfig;
import static ch.epfl.moocprog.config.Config.*;

public final class AntWorker extends Ant{
	
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

	@Override
	public void accept(AnimalVisitor visitor, RenderingMedia s) {
		visitor.visit(this, s);
	}
}
