package ch.epfl.moocprog;

import static ch.epfl.moocprog.app.Context.getConfig;
import static ch.epfl.moocprog.config.Config.*;

public class PheromoneRotationProbabilityModel implements AntRotationProbabilityModel {

	@Override
	public RotationProbability computeRotationProbs(RotationProbability movementMatrix, ToricPosition position,
			double directionAngle, AntEnvironmentView env) {

		double[] I = movementMatrix.getAngles();
		double[] P = movementMatrix.getProbabilities();
		double[] Q = env.getPheromoneQuantitiesPerIntervalForAnt(position, directionAngle, I);
		
		int alpha = getConfig().getInt(ALPHA);
		double beta = getConfig().getDouble(BETA_D);
		double QZero = getConfig().getDouble(Q_ZERO);
		double somme = 0;
		
		//calcul de la detection
		double[] D = new double[Q.length];
		
		for(int i = 0; i < D.length; ++i) {
			D[i] = 1.0 / ( 1.0 + Math.exp( -beta * (Q[i] - QZero) ) );
		}
		
		//calcul du nouveau tableau de probabilités
		double[] Pprime = new double[P.length];
		
		for(int i = 0; i < D.length; ++i) {
			Pprime[i] = P[i] * Math.pow( D[i], alpha );
			somme += Pprime[i];
		}
		
		for(int i = 0; i < D.length; ++i) {
			Pprime[i] /= somme;
			
		}
		
		
		return new RotationProbability(I, Pprime);
	}

}
