
public class Ammunition extends Object {//TODO Kinetic(Bullets), Plasma Cells, Power Cells, Missiles, Ferrous projectiles.
	private float calibre;
	private String damageType; //Corrosive, Kinetic, Energy, Thermal, Radiation.
	
	public Ammunition(String name, Object[] parts, 
			int value, int weight, float calibre, String damageType, String description) {
		super(true, name, description, null, value, 0);
		this.calibre = calibre;
		this.damageType = damageType;
	}
	
	public float getCalibre() {
		return calibre;
	}
	
	public void setCalibre(float calibre) {
		this.calibre = calibre;
	}
	
	public String getDamageType() {
		return damageType;
	}
	
	public void setDamageType(String damageType) {
		this.damageType = damageType;
	}
}