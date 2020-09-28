package ch.epfl.moocprog;

import ch.epfl.moocprog.utils.Time;

public abstract class Ant extends Animal{
	
	private Uid anthillId;
	
	public Ant(ToricPosition position, int hitpoints, Time lifespan, Uid anthillId) {
		super(position, hitpoints, lifespan);
		this.anthillId = anthillId;
	}

	public final Uid getAnthillId() {
		return anthillId;
	}

	

}
