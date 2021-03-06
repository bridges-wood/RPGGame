package construction;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import object.Container;
import object.Key;
import object.Object;
import object.Terminal;
import object.Usable;
import weapon.Ammunition;
import weapon.Bullet;
import weapon.FerrousProjectile;
import weapon.Magazine;
import weapon.PlasmaCell;
import weapon.Weapon;

public class Map implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2735214656381464211L;
	public static List<Object> objects = new ArrayList<Object>();
	public static List<Room> rooms = new ArrayList<Room>();

	public static void main(String[] args) {
		Map running = new Map();
		running.createMap();
	}

	@SuppressWarnings("unchecked")
	private void createMap() {
		boolean creating = true;

		while (creating) {
			System.out.print("Menu -- Type: ");
			String Class = Utilities.StrInput().toLowerCase();
			switch (Class) {
			case ("room"):
				rooms.add(createRoom());
				break;
			case ("existing"):
				for (Room i : rooms) {
					System.out.println(i.getName());
				}
				String roomChoice = Utilities.StrInput();
				for (Room i : rooms) {
					if (i.getName().toLowerCase().equals(roomChoice.toLowerCase())) {
						rooms.add(i);
					}
				}
				break;
			case ("save"):
				save(rooms, "room");
				save(objects, "object");
				break;
			case ("load"):
				rooms = (List<Room>) load("StatRes/RoomRepository.rep");
			case ("quit"):
				save(rooms, "room");
				save(objects, "object");
				creating = false;
				break;
			}
		}
	}

	private Room createRoom() {
		System.out.print("Name: ");
		String name = Utilities.StrInput();
		System.out.println("Contents: ");
		List<Object> contents = createSubObjects();
		for (Object i : contents) {
			if (!objects.contains(i)) {
				objects.add(i);
			}
		}
		System.out.println("Characters: ");
		List<construction.Character> characters = createCharacters();
		System.out.println("Description: ");
		String description = Utilities.StrInput();
		System.out.println("Rooms: ");
		Room[] connections = new Room[8];
		Arrays.fill(connections, null);
		for (Room room : rooms) {
			System.out.println(room.getName());
			if (Utilities.StrInput().equals("y")) {
				System.out.print("Location: ");
				try {
					connections[Integer.parseInt(Utilities.StrInput())] = room;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		System.out.println("Access: ");
		boolean[] access = new boolean[8];
		Arrays.fill(access, false);
		boolean empty = true;
		for (Room ob : connections) {
			if (ob != null) {
				empty = false;
				break;
			}
		}
		if (!empty) {
			for (int i = 0; i < access.length; i++) {
				System.out.println(connections[i].getDescription());
				if (Utilities.StrInput().equals("true")) {
					access[i] = true;
				}
			}
		}
		return new Room(name, contents, characters, description, connections, access);
	}

	private List<Character> createCharacters() {
		List<Character> temp = new ArrayList<Character>();
		System.out.println("Number of characters: ");
		int numChars = Integer.parseInt(Utilities.StrInput());
		for (int i = 0; i < numChars; i++) {
			System.out.print("Name: ");
			String name = Utilities.StrInput();
			System.out.print("Equipped: ");
			Weapon equipped = createWeapon();
			System.out.print("Inventory: ");
			List<Object> inventory = createSubObjects();
			temp.add(new Character(name, equipped, inventory));
		}
		return temp;
	}

	@SuppressWarnings("unchecked")
	public static List<Object> createSubObjects() {
		boolean creating = true;
		List<object.Object> toReturn = new ArrayList<object.Object>();
		while (creating) {
			System.out.print("Object: ");
			String Class = Utilities.StrInput().toLowerCase();
			switch (Class) {
			case ("container"):
				toReturn.add(createContainer());
				break;
			case ("key"):
				toReturn.add(createKey());
				break;
			case ("object"):
				toReturn.add(createObject());
				break;
			case ("terminal"):
				toReturn.add(createTerminal());
				break;
			case ("usable"):
				toReturn.add(createUsable());
				break;
			case ("magazine"):
				toReturn.add(createMagazine());
				break;
			case ("weapon"):
				toReturn.add(createWeapon());
				break;
			case ("existing"):
				toReturn.addAll(existingRoom());
				break;
			case ("save"):
				save(objects, "object");
				break;
			case ("load"):
				objects = (List<Object>) load("StatRes/ObjectRepository.rep");
			case ("quit"):
				creating = false;
				break;
			}
		}
		return toReturn;
	}

	private static List<Object> existingRoom() {
		for (Room room : rooms) {
			System.out.println(room.getName());
		}
		while (true) {
			Room selection = null;
			String choice = Utilities.StrInput().toLowerCase();
			for (Room room : rooms) {
				if (room.getName().toLowerCase().equals(choice)) {
					selection = room;
					break;
				}
			}
			if (!selection.equals(null)) {
				System.out.println("Room found! \n Contents:");
				List<Object> tempList = selection.getContents();
				for (Object object : tempList) {
					System.out.println(object.getName());
				}
				System.out.println("Modify contents? [Y/n]");
				String modify = Utilities.StrInput().toLowerCase();
				if (modify.equals("y")) {
					selection.setContents(modifyContents(tempList));
				}
				System.out.println("Confirm selection [Y/n]");
				String confirm = Utilities.StrInput().toLowerCase();
				if (confirm.equals("y")) {
					break;
				}
			}

		}
		return null;
	}

	private static List<Object> modifyContents(List<Object> roomContents) {
		while (true) {
			System.out.println("Object to be modified: ");
			String toBeModified = Utilities.StrInput().toLowerCase();
			Object mod = null;
			for (Object object : roomContents) {
				if (object.getName().toLowerCase().equals(toBeModified)) {
					mod = object;
					roomContents.remove(object);
				}
			}
			if (!mod.equals(null)) {
				int selection = -1;
				switch (mod.getClass().getSimpleName()) {
				case ("Container"):
					System.out.println("Attributes: ");
					System.out.println("1. Name");
					System.out.println("2. Description");
					System.out.println("3. Parts");
					System.out.println("4. Locked");
					System.out.println("5. Usable ");
					selection = Utilities.handleIntInput(1, 5);
					roomContents.add(Container.modifyContainer(selection, (Container) mod));
					break;
				case ("Key"):
					System.out.println("Attributes: ");
					System.out.println("1. Name");
					System.out.println("2. Description");
					selection = Utilities.handleIntInput(1, 2);
					roomContents.add(Key.modifyKey(selection, (Key) mod));
					break;
				case ("Object"):
					System.out.println("Attributes: ");
					System.out.println("1. Inventoriable");
					System.out.println("2. Combinable");
					System.out.println("3. Name");
					System.out.println("4. Description");
					System.out.println("5. Parts");
					selection = Utilities.handleIntInput(1, 5);
					roomContents.add(Object.modifyObject(selection, (Object) mod));
					break;
				case ("Terminal"):
					System.out.println("Attributes: ");
					System.out.println("1. Name");
					System.out.println("2. Description");
					System.out.println("3. Difficulty");
					System.out.println("4. Users");
					mod = modifyObject(Utilities.StrInput().toLowerCase(), "Terminal", mod);
					break;
				case ("Usable"):
					System.out.println("Attributes: ");
					System.out.println("1. Inventoriable");
					System.out.println("2. Name");
					System.out.println("3. Description");
					System.out.println("4. Parts");
					System.out.println("5. Generic use statement");
					System.out.println("6. Object state");
					System.out.println("7. Specific uses");
					mod = modifyObject(Utilities.StrInput().toLowerCase(), "Usable", mod);
					break;
				case ("Magazine"):
					System.out.println("Attributes: ");
					System.out.println("1. Name");
					System.out.println("2. Description");
					System.out.println("3. Rounds");
					mod = modifyObject(Utilities.StrInput().toLowerCase(), "Magazine", mod);
					break;
				case ("Weapon"):
					System.out.println("Attributes: ");
					System.out.println("1. Inventoriable");
					System.out.println("2. Name");
					System.out.println("3. Description");
					System.out.println("4. Parts");
					mod = modifyObject(Utilities.StrInput().toLowerCase(), "Weapon", mod);
				}

			}
			System.out.println("Finished? [Y/n]");
			if (Utilities.StrInput().toLowerCase().equals("y")) {
				break;
			}
		}

		return roomContents;
	}

	@SuppressWarnings("unchecked")
	private static <T extends Object> T modifyObject(String attribute, String type, Object object) {
		int selection = -1;
		try {
			selection = Integer.parseInt(attribute);
		} catch (Exception e) {
			System.out.println("Incorrect input format.");
			return (T) object;
		}
		switch (type) {
		case ("Usable"):
			switch (selection) {
			case 1:
				System.out.println("1. Inventoriable");
			case 2:
				System.out.println("2. Name");
			case 3:
				System.out.println("3. Description");
			case 4:
				System.out.println("4. Parts");
			case 5:
				System.out.println("5. Generic use statement");
			case 6:
				System.out.println("6. Object state");
			case 7:
				System.out.println("7. Specfic uses");
			}
			break;
		case ("Magazine"):
			switch (selection) {
			case 1:
				System.out.println("1. Name");
			case 2:
				System.out.println("2. Description");
			case 3:
				System.out.println("3. Rounds");
			}
			break;
		case ("Weapon"):
			switch (selection) {
			case 1:
				System.out.println("1. Inventoriable");
			case 2:
				System.out.println("2. Name");
			case 3:
				System.out.println("3. Description");
			case 4:
				System.out.println("4. Parts");
			}
		}
		return (T) object;
	}

	public static List<?> load(String file) {
		List<?> existing = Collections.emptyList();
		try {
			FileInputStream fileStream = new FileInputStream(new File(file));
			ObjectInputStream objectStream = new ObjectInputStream(fileStream);
			existing = (List<?>) objectStream.readObject();
			objectStream.close();
			fileStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return existing;
	}

	private static void save(List<?> toSave, String type) {
		if (type.equals("object")) {
			try {
				FileOutputStream fileOut = new FileOutputStream(new File("StatRes/ObjectRepository.rep"));
				ObjectOutputStream out = new ObjectOutputStream(fileOut);
				out.writeObject(toSave);
				out.close();
				fileOut.close();
				System.out.println("Data saved in: StatRes/ObjectRepository.rep");
			} catch (IOException i) {
				i.printStackTrace();
			}
		} else if (type.equals("room")) {
			try {
				FileOutputStream fileOut = new FileOutputStream(new File("StatRes/RoomRepository.rep"));
				ObjectOutputStream out = new ObjectOutputStream(fileOut);
				out.writeObject(toSave);
				out.close();
				fileOut.close();
				System.out.println("Data saved in: StatRes/RoomRepository.rep");
			} catch (IOException i) {
				i.printStackTrace();
			}
		}
	}

	private static Weapon createWeapon() {

		System.out.print("Do you want this to be null? [y/n]: ");
		String confirm = Utilities.StrInput();
		if (confirm.toLowerCase().equals("y")) {
			return new Weapon(false, "", "", Collections.emptyList());
		}
		System.out.print("Do you want to use an existing weapon? [y/n]: ");
		confirm = Utilities.StrInput();
		if (confirm.toLowerCase().equals("y")) {
			System.out.println("Weapons: ");
			for (Object i : objects) {
				if (i.getClass().getSimpleName().equals("Weapon")) {
					System.out.println(i.getName());
				}
			}
			System.out.println("Selection: ");
			String selection = Utilities.StrInput();
			for (Object i : objects) {
				if (i.getName().toLowerCase().equals(selection.toLowerCase())) {
					return (Weapon) (i);
				}
			}
		}
		System.out.print("Inventoriable: ");
		boolean inv = Boolean.parseBoolean(Utilities.StrInput());
		System.out.print("Name: ");
		String name = Utilities.StrInput();
		System.out.print("Description: ");
		String description = Utilities.StrInput();
		System.out.print("Parts? [y/n]: ");
		confirm = Utilities.StrInput();
		List<object.Object> parts = Collections.emptyList();
		if (confirm.toLowerCase().equals("y")) {
			parts = createSubObjects();
		}
		return new Weapon(inv, name, description, parts);
	}

	private static Magazine createMagazine() {
		System.out.print("Name: ");
		String name = Utilities.StrInput();
		System.out.print("Description: ");
		String description = Utilities.StrInput();
		System.out.print("Rounds: ");
		int rounds = Integer.parseInt(Utilities.StrInput());
		Ammunition ammunition = createAmmunition();
		return new Magazine(name, description, rounds, ammunition);
	}

	private static Ammunition createAmmunition() {
		System.out.print("Type: ");
		String type = Utilities.StrInput().toLowerCase();
		switch (type) {
		case ("bullet"):
			return (createBullet());
		case ("ferrous"):
			return (createFerrousProjectile());
		case ("plasma"):
			return (createPlasmaCell());
	}
	return null;
}

private static Bullet createBullet() {
	System.out.print("Name: ");
	String name = Utilities.StrInput();
	System.out.print("Calibre");
	Float calibre = Float.parseFloat(Utilities.StrInput());
	return new Bullet(name, calibre);
}

private static PlasmaCell createPlasmaCell() {
	System.out.print("Name: ");
	String name = Utilities.StrInput();
	System.out.print("Calibre");
	Float calibre = Float.parseFloat(Utilities.StrInput());
	return new PlasmaCell(name, calibre);
}

private static FerrousProjectile createFerrousProjectile() {
		System.out.print("Name: ");
		String name = Utilities.StrInput();
		System.out.print("Calibre");
		Float calibre = Float.parseFloat(Utilities.StrInput());
		return new FerrousProjectile(name, calibre);
	}

	private static Usable createUsable() {
		System.out.print("Inventoriable: ");
		boolean inv = Boolean.parseBoolean(Utilities.StrInput());
		System.out.print("Name: ");
		String name = Utilities.StrInput();
		System.out.print("Description: ");
		String description = Utilities.StrInput();
		System.out.print("Parts? [y/n]: ");
		String confirm = Utilities.StrInput();
		List<Object> parts = new ArrayList<Object>();
		if (confirm.toLowerCase().equals("y")) {
			parts = createSubObjects();
		}
		List<String> combinable = new ArrayList<String>();
		if (inv) {
			System.out.println("Combinable: ");
			while (true) {
				System.out.println("Object name: ");
				String choice = Utilities.StrInput();
				if (choice.equals("quit")) {
					break;
				} else {
					combinable.add(choice);
				}
			}
		}
		System.out.print("Generic use statement: ");
		String genericUse = Utilities.StrInput();
		System.out.print("Object state: ");
		String objectState = Utilities.StrInput();
		System.out.println("Specific Uses: ");
		HashMap<Object, String> specificUses = new HashMap<Object, String>();
		for (Object i : objects) {
			System.out.println(i.getName());
		}
		while (true) {
			System.out.println("Object: ");
			String object = Utilities.StrInput();
			Object tempRef = null;
			if (object.equals("quit")) {
				break;
			}
			for (Object i : objects) {
				if (i.getName().toLowerCase().equals(object.toLowerCase())) {
					tempRef = i;
				}
			}
			if (!tempRef.equals(null)) {
				System.out.print("Description of use: ");
				String use = Utilities.StrInput();
				specificUses.put(tempRef, use);
			}
		}

		return new Usable(inv, name, description, parts,
				Arrays.copyOf(combinable.toArray(), combinable.size(), String[].class), genericUse, objectState,
				specificUses);
	}

	private static Terminal createTerminal() {
		System.out.print("Name: ");
		String name = Utilities.StrInput();
		System.out.print("Description: ");
		String description = Utilities.StrInput();
		System.out.print("Difficulty: ");
		int difficulty = Integer.parseInt(Utilities.StrInput());
		System.out.print("Number of users: ");
		int numUsers = Integer.parseInt(Utilities.StrInput());
		boolean[] locked = new boolean[numUsers];
		String[] logNames = new String[numUsers];
		String[] logs = new String[numUsers];
		String[] usernames = new String[numUsers];
		String[] passwords = new String[numUsers];
		for (int i = 0; i < numUsers; i++) {
			System.out.println("Locked: ");
			locked[i] = Boolean.parseBoolean(Utilities.StrInput());
			System.out.println("Log name: ");
			logNames[i] = Utilities.StrInput();
			System.out.println("Log: ");
			logs[i] = Utilities.StrInput();
			System.out.println("Username: ");
			usernames[i] = Utilities.StrInput();
			System.out.println("Password: ");
			passwords[i] = Utilities.StrInput();
		}

		return new Terminal(name, description, difficulty, locked, logNames, logs, usernames, passwords);
	}

	private static Object createObject() {
		System.out.print("Inventoriable: ");
		boolean inv = Boolean.parseBoolean(Utilities.StrInput());
		System.out.print("Name: ");
		String name = Utilities.StrInput();
		System.out.print("Description: ");
		String description = Utilities.StrInput();
		System.out.print("Parts? [y/n]: ");
		String confirm = Utilities.StrInput();
		List<object.Object> parts = Collections.emptyList();
		if (confirm.toLowerCase().equals("y")) {
			parts = createSubObjects();
		}
		System.out.println("Combinable: ");
		List<String> combinable = new ArrayList<String>();
		while (true) {
			System.out.println("Object name: ");
			String choice = Utilities.StrInput();
			if (choice.equals("quit")) {
				break;
			} else {
				combinable.add(choice);
			}
		}
		return new Object(inv, name, description, parts,
				Arrays.copyOf(combinable.toArray(), combinable.size(), String[].class));
	}

	private static Key createKey() {
		System.out.print("Name: ");
		String name = Utilities.StrInput();
		System.out.print("Description: ");
		String description = Utilities.StrInput();
		return new Key(name, description);
	}

	private static Container createContainer() {
		System.out.print("Name: ");
		String name = Utilities.StrInput();
		System.out.print("Description: ");
		String description = Utilities.StrInput();
		System.out.print("Parts? [y/n]: ");
		String confirm = Utilities.StrInput();
		List<object.Object> parts = Collections.emptyList();
		if (confirm.toLowerCase().equals("y")) {
			parts = createSubObjects();
		}
		System.out.println("Locked: ");
		boolean locked = Boolean.parseBoolean(Utilities.StrInput());
		System.out.println("Working keys: ");
		List<Key> workingKeys = new ArrayList<Key>();
		while (true) {
			System.out.println("Key name: ");
			String choice = Utilities.StrInput();
			if (choice.equals("quit")) {
				break;
			} else {
				for (Object i : objects) {
					if (i.getClass().getSimpleName().equals("Key")) {
						Key key = (Key) i;
						if (i.getName().toLowerCase().equals(choice.toLowerCase())) {
							workingKeys.add(key);
						}
					}
				}
			}
		}
		return new Container(name, description, parts, locked, (Key[]) workingKeys.toArray());
	}

}
