
package ch.epfl.moocprog;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import static ch.epfl.moocprog.app.Context.getConfig;
import static ch.epfl.moocprog.config.Config.*;

import ch.epfl.moocprog.gfx.EnvironmentRenderer;
import ch.epfl.moocprog.utils.Time;
import ch.epfl.moocprog.utils.Utils;

public final class Environment implements FoodGeneratorEnvironmentView, AnimalEnvironmentView, AnthillEnvironmentView,
		AntEnvironmentView, AntWorkerEnvironmentView {

	private FoodGenerator foodGenerator;
	private List<Food> foods;
	private List<Animal> animals;
	private List<Anthill> anthills;

	public Environment() {
		foodGenerator = new FoodGenerator();
		foods = new LinkedList<Food>();
		animals = new LinkedList<Animal>();
		anthills = new ArrayList<>();
	}

	@Override
	public void addFood(Food food) {
		if (food == null) {
			throw new IllegalArgumentException();
		}
		foods.add(food);

	}

	// @Override
	public void addAnimal(Animal animal) {
		if (animal == null) {
			throw new IllegalArgumentException();
		}
		animals.add(animal);
	}

	public void addAnthill(Anthill anthill) {
		if (anthill == null) {
			throw new IllegalArgumentException();
		}
		anthills.add(anthill);
	}

	@Override
	public void addAnt(Ant ant) {
		this.addAnimal(ant);
	}

	public List<Double> getFoodQuantities() {

		return foods.stream().map(food -> food.getQuantity()).collect(Collectors.toList());
	}

	public List<ToricPosition> getAnimalsPosition() {

		return animals.stream().map(animal -> animal.getPosition()).collect(Collectors.toList());
	}

	public void update(Time dt) {
		// generation de la nourriture
		foodGenerator.update(this, dt);

		for (Anthill anthill : anthills) {
			anthill.update(this, dt);
		}

		// Gestion des animaux
		Iterator<Animal> iterateur = animals.iterator();
		while (iterateur.hasNext()) {
			Animal animal = iterateur.next();
			if (animal.isDead()) {
				iterateur.remove();
			} else {
				animal.update(this, dt);
			}
		}

		// nettoyage des instances de nourriture avec une quantité nulle.
		foods.removeIf(food -> food.getQuantity() <= 0);
	}

	// Elle est destinée à procéder au rendu graphique des constituants
	// del’environnement

	public void renderEntities(EnvironmentRenderer environmentRenderer) {
		foods.forEach(environmentRenderer::renderFood);
		animals.forEach(environmentRenderer::renderAnimal);
		anthills.forEach(environmentRenderer::renderAnthill);
	}

	public int getWidth() {
		return getConfig().getInt(WORLD_WIDTH);
	}

	public int getHeight() {
		return getConfig().getInt(WORLD_HEIGHT);
	}

	@Override
	public Food getClosestFoodForAnt(AntWorker antWorker) {
		if (antWorker == null) {
			throw new IllegalArgumentException();
		}
		// recuper la food la plus proche
		Food food = Utils.closestFromPoint(antWorker, foods);

		if (food != null && antWorker.getPosition().toricDistance(food.getPosition()) <= getConfig()
				.getDouble(ANT_MAX_PERCEPTION_DISTANCE)) {
			return food;
		}
		return null;
	}

	@Override
	public boolean dropFood(AntWorker antWorker) {
		// recuperer la fourmiliere de notre fourmi
		if (antWorker == null) {
			throw new IllegalArgumentException();
		}
		Anthill anthill = null;
		for (Anthill val : anthills) {
			if (val.getAnthillId().equals(antWorker.getAnthillId())) {
				anthill = val;
				break;
			}
		}
		boolean success = anthill != null && (antWorker.getPosition().toricDistance(anthill.getPosition()) <= getConfig()
				.getDouble(ANT_MAX_PERCEPTION_DISTANCE));
		
		if(success) {
			anthill.dropFood(antWorker.getFoodQuantity());
		}
		return success;
	}

	@Override
	public void selectSpecificBehaviorDispatch(AntWorker antWorker, Time dt) {
		// Grâce à la surchage des méthode, cette méthode// sera appelée par AntWorker
		// dans son specificBehaviorDispatch.// Ici, on est libre d'appeler n'importe
		// quelle méthode non privée de// AntWorker, et lui passer n'importe quelle
		// paramètre, en particulier// une AntWorkerEnvironmentView !
		if (antWorker == null) {
			throw new IllegalArgumentException();
		}
		antWorker.seekForFood(this, dt);
	}

	@Override
	public void selectSpecificBehaviorDispatch(AntSoldier antSoldier, Time dt) {
		if (antSoldier == null) {
			throw new IllegalArgumentException();
		}
		antSoldier.seekForEnemies(this, dt);

	}

}
