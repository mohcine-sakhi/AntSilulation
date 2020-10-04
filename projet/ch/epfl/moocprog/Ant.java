package ch.epfl.moocprog;

import ch.epfl.moocprog.utils.Time;
import ch.epfl.moocprog.utils.Vec2d;

import static ch.epfl.moocprog.app.Context.getConfig;
import static ch.epfl.moocprog.config.Config.*;

public abstract class Ant extends Animal {

	private Uid anthillId;
	// postion de la formi à sa création
	private ToricPosition lastPos;

	public Ant(ToricPosition position, int hitpoints, Time lifespan, Uid anthillId) {
		super(position, hitpoints, lifespan);
		this.anthillId = anthillId;
		this.lastPos = this.getPosition();
	}

	public final Uid getAnthillId() {
		return anthillId;
	}

	private final void spreadPheromones(AntEnvironmentView env) {
		ToricPosition currentPos = this.getPosition();
		double distance = currentPos.toricDistance(lastPos);
		//récupérer le vecteur de déplacement fait par la fourmi
		Vec2d vecteurToric = lastPos.toricVector(currentPos);
		// nombre d'instances à créer
		int instances = (int) (distance * getConfig().getDouble(ANT_PHEROMONE_DENSITY));
		Pheromone pheromone;
		double quantity = getConfig().getDouble(ANT_PHEROMONE_ENERGY);
		for(int i = 1; i <= instances; ++i) {
			pheromone = new Pheromone(new ToricPosition(vecteurToric.scalarProduct(i)), quantity);
//			System.out.println("last "+ lastPos);
//			System.out.println(new ToricPosition(vecteurToric.scalarProduct(i)));
			env.addPheromone(pheromone);
		}
	}

}
