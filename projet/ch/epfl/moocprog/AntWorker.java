package ch.epfl.moocprog;

public final class AntWorker extends Ant{
	
	public AntWorker(ToricPosition position) {
		super(position);
	}

	@Override
	public void accept(AnimalVisitor visitor, RenderingMedia s) {
		visitor.visit(this, s);
	}

	@Override
	public double getSpeed() {
		return 0;
	}
}
