package ch.epfl.moocprog;

import ch.epfl.moocprog.utils.Utils;

public class RotationProbability {
	
	private double[] angles;
	private double[] probabilities;
	
	
	
	public RotationProbability(double[] angles, double[] probabilities) {
		
		Utils.requireNonNull(angles);
		Utils.requireNonNull(probabilities);
		Utils.require(angles.length == probabilities.length);
		
		this.angles = angles.clone();
		this.probabilities = probabilities.clone();
	}

	public  double[] getAngles() {
		return angles.clone();
	}
	
	public double[] getProbabilities() {
		return probabilities.clone();
	}

}
