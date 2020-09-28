package ch.epfl.moocprog;

import static ch.epfl.moocprog.app.Context.getConfig;
import static ch.epfl.moocprog.config.Config.*;


public final class AntSoldier extends Ant {

	public AntSoldier(ToricPosition position, Uid anthillId) {
		super(position, getConfig().getInt(ANT_SOLDIER_HP), getConfig().getTime(ANT_SOLDIER_LIFESPAN), anthillId);
		
	}
	
	@Override
	public double getSpeed() {
		return getConfig().getDouble(ANT_SOLDIER_SPEED);
	}

	@Override
	public void accept(AnimalVisitor visitor, RenderingMedia s) {
		visitor.visit(this, s);
	}

	

}
