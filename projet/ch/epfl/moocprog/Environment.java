package ch.epfl.moocprog;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import static ch.epfl.moocprog.app.Context.getConfig;
import static ch.epfl.moocprog.config.Config.*;

import ch.epfl.moocprog.gfx.EnvironmentRenderer;
import ch.epfl.moocprog.utils.Time;

public final class Environment implements FoodGeneratorEnvironmentView, AnimalEnvironmentView{
	
	private FoodGenerator foodGenerator;
	private List<Food> foods;
	private List<Animal> animals;
	
	public Environment() {
		foodGenerator = new FoodGenerator();
		foods = new LinkedList<Food>();
		animals = new LinkedList<Animal>();
	}
 
	@Override
	public void addFood(Food food) {
		if(food == null) {
			throw new IllegalArgumentException();
		}
		foods.add(food);
		
	}
	
	public void addAnimal(Animal animal) {
		if(animal == null) {
			throw new IllegalArgumentException();
		}
		animals.add(animal);
	}
	
	public void addAnthill(Anthill anthill) {
		
	}
	
	public List<Double> getFoodQuantities(){
		
		return foods.stream()
					.map(food -> food.getQuantity())
					.collect(Collectors.toList());
	}
	
	public List<ToricPosition> getAnimalsPosition(){
		
		return animals.stream()
					  .map(animal -> animal.getPosition())
					  .collect(Collectors.toList());
	}
	
	public void update(Time dt){
		// generation de la nourriture
		foodGenerator.update(this, dt);
		
		// Gestion des animaux
		Iterator<Animal> iterateur = animals.iterator();
		while(iterateur.hasNext()) {
			Animal animal = iterateur.next();
			if(animal.isDead()) {
				iterateur.remove();
			}else {
				animal.update(this, dt);
			}
		}
		
		//nettoyage des instances de nourriture avec une quantité nulle.
		foods.removeIf(food -> food.getQuantity() <= 0);
	}
	
	//Elle est destinée à procéder au rendu graphique des constituants
	//del’environnement
	
	public void renderEntities(EnvironmentRenderer environmentRenderer) {
		foods.forEach(environmentRenderer::renderFood);
		animals.forEach(environmentRenderer::renderAnimal);
	}
	
	public int getWidth() {
		return getConfig().getInt(WORLD_WIDTH);
	}
	
	public int getHeight() {
		return getConfig().getInt(WORLD_HEIGHT);
	}
	
	

}
