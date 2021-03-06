package object;
import java.util.Collections;

import construction.Room;

public class Door extends Object {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3336059814609687158L;
	private Key[] workingKeys;
	private Room accessible;
	private boolean locked;
	
	public Door(String name, String description, Key[] workingKeys, boolean locked, Room accessible) {
		super(false, name, description, Collections.emptyList(), new String[0]);
		this.workingKeys = workingKeys;
		this.locked = locked;
		this.accessible = accessible;;
	}

	public Key[] getWorkingKeys() {
		return workingKeys;
	}

	public void setWorkingKeys(Key[] workingKeys) {
		this.workingKeys = workingKeys;
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	public Room getAccessible() {
		return accessible;
	}

	public void setAccessible(Room accessible) {
		this.accessible = accessible;
	}

}
