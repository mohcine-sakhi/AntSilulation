package ch.epfl.moocprog;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import ch.epfl.moocprog.utils.Time;

public final class Environment implements FoodGeneratorEnvironmentView{
	
	private FoodGenerator foodGenerator;
	private List<Food> foods;
	
	public Environment() {
		foodGenerator = new FoodGenerator();
		foods = new LinkedList<Food>();
	}
 
	@Override
	public void addFood(Food food) {
		if(food == null) {
			throw new IllegalArgumentException();
		}
		foods.add(food);
		
	}
	
	public List<Double> getFoodQuantities(){
		
		return foods.stream().map(food -> food.getQuantity()).collect(Collectors.toList());
	}
	
	public void update(Time dt){
		foodGenerator.update(this, dt);
		foods.removeIf(food -> food.getQuantity() <= 0);
	}

}
