package ch.epfl.moocprog;

public final class Food extends Positionable{
	
	private double quantity;

	public Food(ToricPosition position, double quantity) {
		super(position);
		if(quantity < 0) {
			this.quantity = 0;
		}else {
			this.quantity = quantity;
		}
	}

	public double getQuantity() {
		return quantity;
	}
	
	public double takeQuantity(double quantity) {
		if(quantity < 0) {
			throw new IllegalArgumentException("La quantité à prélever est négative");
		}else if(this.quantity >= quantity) {
					this.quantity -= quantity;
					return quantity;
			  }else {
					quantity = this.quantity;
					this.quantity = 0;
					return quantity;
			  		}
	}

	@Override
	public String toString() {
		String chaine = super.toString() + "\n";
		chaine += String.format("Quantity : %.2f", getQuantity());
		
		return chaine;
	}
	
	
	
	
}
