package ch.epfl.moocprog;

import ch.epfl.moocprog.utils.Vec2d;
import static ch.epfl.moocprog.app.Context.getConfig;
import static ch.epfl.moocprog.config.Config.*;

public final class ToricPosition {
	
	private final Vec2d position;
	
	public ToricPosition() {
		
		this.position = new Vec2d(0, 0);
	}
	
	public ToricPosition(double x, double y) {
		
		this.position = clampedPosition(x, y);
	}

	public ToricPosition(Vec2d position) {
		
		this.position = clampedPosition(position.getX(), position.getY());
	}
	
	private static Vec2d clampedPosition(double x, double y) {
		double coordonnee = x;
		double ordonnee = y;
		
		if(coordonnee < 0) {
			do {
				coordonnee += getConfig().getInt(WORLD_WIDTH);
			}while(coordonnee < 0);
		}else if(coordonnee >= getConfig().getInt(WORLD_WIDTH)) {
			do {
				coordonnee -= getConfig().getInt(WORLD_WIDTH);
			}while(coordonnee >= getConfig().getInt(WORLD_WIDTH));
		}
		
		
		if(ordonnee < 0) {
			do {
				ordonnee += getConfig().getInt(WORLD_HEIGHT);
			}while(ordonnee < 0);
		}else if(ordonnee >= getConfig().getInt(WORLD_HEIGHT)) {
			do {
				ordonnee -= getConfig().getInt(WORLD_HEIGHT);
			}while(ordonnee >= getConfig().getInt(WORLD_HEIGHT));
		}
		
		return new Vec2d(coordonnee, ordonnee);
		
	}
	
	public ToricPosition add(ToricPosition that) {
		Vec2d toricPosition = this.position.add(that.position);
		return new ToricPosition(toricPosition);
	}
	
	public ToricPosition add(Vec2d vec) {
		Vec2d toricPosition = this.position.add(vec);
		return new ToricPosition(toricPosition);
	}
	
	public Vec2d toVec2d() {
		return this.position;
	}
	
	public Vec2d toricVector(ToricPosition that) {
		Vec2d[] vecteurs = new Vec2d[9];
		vecteurs[0] = that.toVec2d().add(new Vec2d(0, getConfig().getInt(WORLD_HEIGHT)));
		vecteurs[1] = that.toVec2d().add(new Vec2d(0, -getConfig().getInt(WORLD_HEIGHT)));
		vecteurs[2] = that.toVec2d().add(new Vec2d(getConfig().getInt(WORLD_WIDTH), 0));
		vecteurs[3] = that.toVec2d().add(new Vec2d(-getConfig().getInt(WORLD_WIDTH), 0));
		vecteurs[4] = that.toVec2d().add(new Vec2d(getConfig().getInt(WORLD_WIDTH), getConfig().getInt(WORLD_HEIGHT)));
		vecteurs[5] = that.toVec2d().add(new Vec2d(-getConfig().getInt(WORLD_WIDTH), getConfig().getInt(WORLD_HEIGHT)));
		vecteurs[6] = that.toVec2d().add(new Vec2d(getConfig().getInt(WORLD_WIDTH), -getConfig().getInt(WORLD_HEIGHT)));
		vecteurs[7] = that.toVec2d().add(new Vec2d(-getConfig().getInt(WORLD_WIDTH),-getConfig().getInt(WORLD_HEIGHT)));
		vecteurs[8] = that.toVec2d();
		
		double distance = Double.MAX_VALUE;
		int position = 0;
		
		for(int i = 0; i < vecteurs.length; ++i) {
			if(this.toVec2d().distance(vecteurs[i]) < distance) {
				distance = this.toVec2d().distance(vecteurs[i]);
				position = i;
			}
		}
		
		return vecteurs[position].minus(this.toVec2d());
	}
	
	public double toricDistance(ToricPosition that) {
		Vec2d vecteur = this.toricVector(that);
		
		return vecteur.length();
	}
	
	@Override
	public String toString() {
		return this.position.toString();
	}
	
}
