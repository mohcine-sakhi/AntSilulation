package ch.epfl.moocprog;

import static ch.epfl.moocprog.app.Context.getConfig;
import static ch.epfl.moocprog.config.Config.*;

public final class Termite extends Animal{
	
	public Termite(ToricPosition position){
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
}
