package ch.epfl.moocprog;

import static ch.epfl.moocprog.app.Context.getConfig;
import static ch.epfl.moocprog.config.Config.*;

import ch.epfl.moocprog.random.UniformDistribution;
import ch.epfl.moocprog.utils.Time;

public final class Anthill extends Positionable {

	private Uid anthillId;
	private double foodQuantity;
	private double anthillWorkerProb;
	private Time time;

	public Anthill(ToricPosition position) {
		super(position);
		this.anthillId = Uid.createUid();
		this.foodQuantity = 0;
		this.anthillWorkerProb = getConfig().getDouble(ANTHILL_WORKER_PROB_DEFAULT);
		time = Time.ZERO;
	}

	public Anthill(ToricPosition position, double anthillWorkerProb) {
		super(position);
		this.anthillId = Uid.createUid();
		this.foodQuantity = 0;
		this.anthillWorkerProb = anthillWorkerProb;
		time = Time.ZERO;
	}

	public double getFoodQuantity() {
		return foodQuantity;
	}

	public Uid getAnthillId() {
		return anthillId;
	}

	public void dropFood(double toDrop) {
		if (toDrop < 0) {
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

	public void update(AnthillEnvironmentView env, Time dt) {

		time = time.plus(dt);
		Time anthillSpawnDelay = getConfig().getTime(ANTHILL_SPAWN_DELAY);

		double probabilite;
		Ant ant;
		

		while (time.minus(anthillSpawnDelay).isPositive()) {

			this.time = time.minus(anthillSpawnDelay);

			probabilite = UniformDistribution.getValue(0, 1);

			if (probabilite <= this.anthillWorkerProb) {
				ant = new AntWorker(this.getPosition(), this.getAnthillId());
			} else {
				ant= new AntSoldier(this.getPosition(), this.getAnthillId());
			}
			
			env.addAnt(ant);
		}
	}
}
