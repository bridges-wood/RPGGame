package weapon;
import java.io.Serializable;
import java.util.List;

import object.Object;

public class Missile extends Ammunition implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3683823027006195785L;

	public Missile(String name, List<Object> parts, int value, int weight, float calibre, String damageType, String description) {
		super("missile", parts, value, weight, calibre, "Kinetic", null);
		// TODO Auto-generated constructor stub
	}
	
}