package ch.epfl.moocprog;

import static ch.epfl.moocprog.app.Context.getConfig;
import static ch.epfl.moocprog.config.Config.*;

public final class Anthill extends Positionable {
	
	private Uid anthillId;
	private double foodQuantity;
	private double anthillWorkerProb;
	
	public Anthill(ToricPosition position) {
		super(position);
		this.anthillId = Uid.createUid();
		this.foodQuantity = 0;
		this.anthillWorkerProb = getConfig().getDouble(ANTHILL_WORKER_PROB_DEFAULT);
	}
	
	public Anthill(ToricPosition position, double anthillWorkerProb) {
		super(position);
		this.anthillId = Uid.createUid();
		this.foodQuantity = 0;
		this.anthillWorkerProb = anthillWorkerProb;
	}
	
	public double getFoodQuantity() {
		return foodQuantity;
	}
	
	public Uid getAnthillId() {
		return anthillId;
	}

	void dropFood(double toDrop) {
		if(toDrop < 0 ) {
			throw new IllegalArgumentException();
		}
		foodQuantity += toDrop;
	}
	
	@Override
	public String toString() {
		
		String chaine = super.toString() + "\n";
		chaine += String.format("Quantity : %.2f", getFoodQuantity());
		
		return chaine;
	}
}
