package ch.epfl.moocprog;

import ch.epfl.moocprog.utils.Time;
import static ch.epfl.moocprog.app.Context.getConfig;
import static ch.epfl.moocprog.config.Config.*;

import ch.epfl.moocprog.random.NormalDistribution;
import ch.epfl.moocprog.random.UniformDistribution;

public final class FoodGenerator {

	private Time time;
	
	public FoodGenerator(){
		time = Time.ZERO;
	}
	
	void update(FoodGeneratorEnvironmentView env, Time dt){
		time = time.plus(dt);
		Time foodGeneratorDelay = getConfig().getTime(FOOD_GENERATOR_DELAY);
		Food food;
		ToricPosition position;
		double quantiteMax = getConfig().getDouble(NEW_FOOD_QUANTITY_MAX);
		double quantiteMin = getConfig().getDouble(NEW_FOOD_QUANTITY_MIN);
		double quantite;
		int abscisse = getConfig().getInt(WORLD_WIDTH);
		int ordonnee = getConfig().getInt(WORLD_HEIGHT);
		double x;
		double y;
		
		
		while(time.minus(foodGeneratorDelay).isPositive()) {
			
			time = time.minus(foodGeneratorDelay);
			
			quantite = UniformDistribution.getValue(quantiteMin, quantiteMax);
			
			x = NormalDistribution.getValue(abscisse / 2.0, abscisse * abscisse / 16.0);
			y = NormalDistribution.getValue(ordonnee / 2.0, ordonnee * ordonnee / 16.0);
			position = new ToricPosition(x, y);
			
			food = new Food(position, quantite);
			
			env.addFood(food);
			
		}
	}
}
