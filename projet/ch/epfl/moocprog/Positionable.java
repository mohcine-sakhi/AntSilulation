package ch.epfl.moocprog;

public class Positionable {
	
	private ToricPosition position;

	public Positionable(ToricPosition position) {
		
		this.position = position;
	}
	
	public Positionable() {
		this.position = new ToricPosition(0, 0);
	}
	
	public ToricPosition getPosition() {
		return this.position;
	}
	
	protected final void setPosition(ToricPosition position) {
		this.position = position;
	}
	
	@Override
	public String toString() {
		return this.position.toString();
	}
}
