package ch.epfl.moocprog;

import static ch.epfl.moocprog.app.Context.getConfig;
import static ch.epfl.moocprog.config.Config.*;

import ch.epfl.moocprog.utils.Time;


public final class Pheromone extends Positionable {
	private double quantity;

	public Pheromone(ToricPosition position, double quantity) {
		super(position);
		this.quantity = quantity;
	}
	
	public double getQuantity() {
		return quantity;
	}
	
	public boolean isNegligible() {
		return this.getQuantity() < getConfig().getDouble(PHEROMONE_THRESHOLD);
	}
	
	public void update(Time dt) {
		if(! isNegligible()) {
			quantity -= dt.toSeconds() * getConfig().getDouble(PHEROMONE_EVAPORATION_RATE);
			if(quantity < 0) {
				quantity = 0;
			}
		}
	}
}
