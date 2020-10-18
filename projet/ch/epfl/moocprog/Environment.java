
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
		AntEnvironmentView, AntWorkerEnvironmentView, TermiteEnvironmentView {

	private FoodGenerator foodGenerator;
	private List<Food> foods;
	private List<Animal> animals;
	private List<Anthill> anthills;
	private List<Pheromone> pheromones;

	public Environment() {
		foodGenerator = new FoodGenerator();
		foods = new LinkedList<Food>();
		animals = new LinkedList<Animal>();
		anthills = new ArrayList<>();
		pheromones = new LinkedList<>();
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

	@Override
	public void addPheromone(Pheromone pheromone) {
		if (pheromone == null) {
			throw new IllegalArgumentException();
		}
		pheromones.add(pheromone);

	}

	public List<Double> getFoodQuantities() {

		return foods.stream().map(food -> food.getQuantity()).collect(Collectors.toList());
	}

	public List<ToricPosition> getAnimalsPosition() {

		return animals.stream().map(animal -> animal.getPosition()).collect(Collectors.toList());
	}

	public List<Double> getPheromonesQuantities() {
		return pheromones.stream().map(pheromone -> pheromone.getQuantity()).collect(Collectors.toList());
	}

	public void update(Time dt) {
		// generation de la nourriture
		foodGenerator.update(this, dt);

		// gestion des phéromones
		Iterator<Pheromone> pheromoneIterateur = pheromones.iterator();
		Pheromone pheromone;
		while (pheromoneIterateur.hasNext()) {
			pheromone = pheromoneIterateur.next();
			if (pheromone.isNegligible()) {
				pheromoneIterateur.remove();
			} else {
				pheromone.update(dt);
			}
		}

		// Gestion des animaux(génération puis mise à jour)
		for (Anthill anthill : anthills) {
			anthill.update(this, dt);
		}

		Iterator<Animal> animalIterateur = animals.iterator();
		Animal animal;
		while (animalIterateur.hasNext()) {
			animal = animalIterateur.next();
			if (animal.isDead()) {
				animalIterateur.remove();
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
		pheromones.forEach(environmentRenderer::renderPheromone);
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
		boolean success = anthill != null && (antWorker.getPosition()
				.toricDistance(anthill.getPosition()) <= getConfig().getDouble(ANT_MAX_PERCEPTION_DISTANCE));

		if (success) {
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

	@Override
	public double[] getPheromoneQuantitiesPerIntervalForAnt(ToricPosition position, double directionAngleRad,
			double[] angles) {

		if (position == null || angles == null) {
			throw new IllegalArgumentException();
		}

		double antSmellMaxDistance = getConfig().getDouble(ANT_SMELL_MAX_DISTANCE);
		double distance;
		double beta = 0;
		double[] quantities = new double[angles.length];
		double closetAngle;
		int index;
		for (Pheromone pheromone : pheromones) {
			distance = pheromone.getPosition().toricDistance(position);
			// pheromone non négligeable et à la poetée
			if (!pheromone.isNegligible() && distance <= antSmellMaxDistance) {
				beta = (position.toricVector(pheromone.getPosition())).angle() - directionAngleRad;

				closetAngle = Double.MAX_VALUE;
				index = 0;
				// chercher l index de l'angle le plus proche
				for (int i = 0; i < angles.length; ++i) {
					if (closestAngleFrom(angles[i], beta) < closetAngle) {
						closetAngle = closestAngleFrom(angles[i], beta);
						index = i;
					}
				}

				quantities[index] += pheromone.getQuantity();
			}
		}
		return quantities;
	}

	private static double normalizedAngle(double angle) {
		while (angle >= 2 * Math.PI) {
			angle -= 2 * Math.PI;
		}
		while (angle < 0) {
			angle += 2 * Math.PI;
		}

		return angle;
	}

	private static double closestAngleFrom(double angle, double target) {
		double diff = angle - target;
		diff = normalizedAngle(diff);

		if (diff <= 2 * Math.PI - diff) {
			return diff;
		} else {
			return 2 * Math.PI - diff;
		}
	}

	@Override
	public RotationProbability selectComputeRotationProbsDispatch(Ant ant) {
		if (ant == null) {
			throw new IllegalArgumentException();
		}
		return ant.computeRotationProbs(this);
	}

	@Override
	public void selectAfterMoveDispatch(Ant ant, Time dt) {
		if (ant == null) {
			throw new IllegalArgumentException();
		}
		ant.afterMoveAnt(this, dt);

	}

	@Override
	public void selectSpecificBehaviorDispatch(Termite termite, Time dt) {
		if (termite == null) {
			throw new IllegalArgumentException();
		}

		termite.seekForEnemies(this, dt);

	}

	@Override
	public void selectAfterMoveDispatch(Termite termite, Time dt) {
		if (termite == null) {
			throw new IllegalArgumentException();
		}
		termite.afterMoveTermite(this, dt);
	}

	@Override
	public RotationProbability selectComputeRotationProbsDispatch(Termite termite) {
		if (termite == null) {
			throw new IllegalArgumentException();
		}
		return termite.computeRotationProbs(this);

	}

	@Override
	public List<Animal> getVisibleEnemiesForAnimal(Animal from) {
		if (from == null) {
			throw new IllegalArgumentException();
		}
		// la liste des ennemis se trouvant une distance inférieur à ANIMAL_SIGHT_DISTANCE
		double AnimalSightDistance = getConfig().getDouble(ANIMAL_SIGHT_DISTANCE);
		return animals.stream()
			   .filter(animal -> (from.isEnemy(animal)
					   && from.getPosition().toricDistance(animal.getPosition()) <= AnimalSightDistance))
			   .collect(Collectors.toList());
		
	}

	@Override
	public boolean isVisibleFromEnemies(Animal from) {
		if (from == null) {
			throw new IllegalArgumentException();
		}
		// si on voit un ennemi donc on est vu par cet ennemi
		return this.getVisibleEnemiesForAnimal(from).size() > 0;
	}

}