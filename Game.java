import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Game implements Serializable {
	private static final long serialVersionUID = 557779407485802525L;
	private ArrayList<Room> rooms = new ArrayList<Room>();
	private Player player;
	List<Character> emptyCharacters = new ArrayList<Character>();
	List<Object> emptyObjects = new ArrayList<Object>();
	Object nullObject = new Object(false, null, null, null, null, 0, 0);

	public Game() {
		this.init();
	}

	public void init() {
		int[] REPLICAS = { 1, 1, 1, 1, 1, 1, 1, 1 };
		// TODO add in a character creator when the player first initialises the game.
		this.player = new Player("Player", 0, 0, 20, 0, null, null, null, null, 20, emptyObjects, REPLICAS, null, null,
				null);
		Object bed = new Object(false, "Bed", "It is your bed.", null, null, 400, 0);
		String[] combinable = {"Torch"};
		Object battery = new Object(true, "Battery", "It is a battery.", null, combinable, 1, 0);
		List<Object> parts = new ArrayList<Object>();
		parts.add(battery);
		Object torch = new Object(true, "Torch", "It is a torch.", parts, null, 10, 0);
		List<Object> contents = new ArrayList<Object>();
		contents.add(torch);
		contents.add(bed);
		Room first = new Room(contents, emptyCharacters,
				"You are in your bedroom. It's rather spartan with the only feature being a single bed against the back wall.",
				1, null, null);
		player.setCurrentRoom(first);
		ArrayList<Room> rooms = new ArrayList<Room>();

		// TODO Clean up this method to add rooms, objects and characters separately.
		rooms.add(first);
		setRooms(rooms);
		playGame();
	}

	public void playGame() {
		try {
			Utilities.saveGame(Menu.getCurrentFile(), this, false);
		} catch (IOException e1) {
			System.out.println("Autosave failed.");
		}
		boolean playing = true;
		boolean moved = true;
		while (playing) {
			if (moved) {
				System.out.println(player.getCurrentRoom().getDescription());
				if (player.getCurrentRoom().getContents().size() > 0) {
					System.out.println("Around you, you can see: ");
					for (int i = 0; i < player.getCurrentRoom().getContents().size(); i++) {
						String currentName = player.getCurrentRoom().getContents().get(i).getName();
						System.out.print("> ");
						if (currentName.startsWith("[aeiou]")) {
							System.out.print("An ");
						} else {
							System.out.print("A ");
						}
						System.out.println(currentName.toLowerCase());
					}
					System.out.println();
				} else {
					System.out.println("There is nothing in this room.");
				}
				if (player.getCurrentRoom().getCharacters().size() > 0) {
					System.out.println("There are others with you. You see: ");
					for (int i = 0; i < player.getCurrentRoom().getCharacters().size(); i++) {
						System.out.println(player.getCurrentRoom().getCharacters().get(i).getName());
					}
				} else {
					System.out.println("There is no one else here.");
				}
				moved = false;
			}
			String userIn = Utilities.StrInput();
			String[] inputArr = userIn.split(" ");
			String verb = inputArr[0];
			String verbObject = "";
			for (int i = 1; i < inputArr.length; i++) {
				verbObject = inputArr[i] + " ";
			}
			verbObject = verbObject.toLowerCase().trim();
			switch (verb) {
			case ("quit"):
				Utilities.testQuit();
				break;
			case ("save"):
				Menu.showSaves();
				System.out.println("Input the names of the file you wish to save in: ");
				String name = Utilities.StrInput();
				File file = new File("SaveGames/" + name + ".gme");
				try {
					Utilities.saveGame(file, this, true);
				} catch (IOException e) {
					System.out.println("Save failed.");
				}
				break;
			case ("get"):
				for (int i = 0; i < player.getCurrentRoom().getContents().size();) { // Checks if player wants to
																							// get meta-objects in a
																							// room.
					Object current = player.getCurrentRoom().getContents().get(i);
					if (current.getName().toLowerCase().equals(verbObject) && current.isInventoriable()
							&& (player.getInventorySize() > player.getTotalInventoryWeight() + current.getWeight())) {
						player.addToInventory(current); // If the object is what they are trying to get, and it is able
														// to be picked up, they can, as long as it isn't too heavy.
						player.getCurrentRoom().getContents().remove(current); // Removes the object from the room.
						System.out.println("You pick the " + current.getName().toLowerCase() + " up.");
						break;
					} else if (current.getParts() != null && current.getParts().size() > 0) { // Checks if they want to
																								// get sub-objects.
						for (i = 0; i < current.getParts().size(); i++) {
							Object currentPart = current.getParts().get(i);
							if (currentPart.getName().toLowerCase().equals(verbObject) && currentPart.isInventoriable()
									&& (player.getInventorySize() > player.getTotalInventoryWeight()
											+ currentPart.getWeight())) {
								player.addToInventory(currentPart);
								current.getParts().remove(currentPart); // Removes the object from the parts of the
																		// meta-object.
								System.out.println("You pick the " + currentPart.getName().toLowerCase() + " up.");
							}
						}
						break;
					} else {
						System.out.println("You couldn't pick that up.");
						break;
					}
				}
				break;
			case ("use"):
				String actStr = "", objStr = "";
				boolean complete = false, successful = false;
				for(int i = 1; i < inputArr.length; i++) {
					if (!complete && !inputArr[i].equals("on")) {
						actStr = actStr + inputArr[i] + " ";
					} else if (!complete) {
						complete = true;
					} else if (complete && !inputArr[i].equals("on")) {
						objStr = objStr + inputArr[i] + " ";
					}
				}
				actStr = actStr.trim();
				objStr = objStr.trim();
				Object actObj = nullObject, objObj = nullObject;
				for(int i = 0; i < player.getInventory().size(); i++) {
					if (player.getInventory().get(i).getName().toLowerCase().equals(actStr)) {
						actObj = player.getInventory().get(i);
					} else if (player.getInventory().get(i).getName().toLowerCase().equals(objStr)) {
						objObj = player.getInventory().get(i);
					}
				}
				if(!actObj.equals(nullObject) && !actObj.getCombinable().equals(null) && actObj.getCombinable().length > 0) {
					for(int i = 0; i < actObj.getCombinable().length; i++) {
						if(!objObj.equals(nullObject) && actObj.getCombinable()[i].toLowerCase().equals(objStr)) {
							objObj.getParts().add(actObj);
							player.getInventory().remove(actObj);
							successful = true;
						}
					}
				}
				if(successful) System.out.println(actStr.substring(0, 1).toUpperCase() + actStr.substring(1) + " and " + objStr + " successfully combined.");
				// Allows use of keys on doors etc. to solve puzzles or whatever else.
				// TODO Will need a different way of handling input to check if the two objects
				// can be used on each other.
				// This will also require a change to the object class to determine which
				// objects can interact with others as well as possible states like locked and
				// unlocked etc.
				// TODO PRIORITY Also need to determine which objects can be used on others.
				break;
			case ("engage"):
				// TODO Initiates a fight.
				break;
			case ("examine"):
				boolean found = false;
				for (int i = 0; i < player.getCurrentRoom().getContents().size(); i++) {
					Object current = player.getCurrentRoom().getContents().get(i);
					if (current.getName().toLowerCase().equals(verbObject)) {
						System.out.println(current.getDescription());
						if(current.getParts() != null && current.getParts().size() > 0) {
							for(i = 0; i < current.getParts().size(); i++) {
								System.out.println("You see it contains: ");
								String currentName = current.getParts().get(i).getName();
								System.out.print("> ");
								if (currentName.startsWith("[aeiou]")) {
									System.out.print("An ");
								} else {
									System.out.print("A ");
								}
								System.out.println(currentName.toLowerCase());
							}
							}
						found = true;
						break;
					} else if (current.getParts() != null && current.getParts().size() > 0) {
						for (i = 0; i < current.getParts().size(); i++) {
							Object currentPart = current.getParts().get(i);
							if (currentPart.getName().toLowerCase().equals(verbObject)) {
								System.out.println(currentPart.getDescription());
								found = true;
								break;
							}
						}
					}
				}

				// TODO able to examine sub parts of objects.
				if (verbObject.equals("inventory")) {
					System.out.println("In your inventory you have: ");
					for (int i = 0; i < player.getInventory().size(); i++) {
						String currentName = player.getInventory().get(i).getName();
						System.out.print("> ");
						if (currentName.startsWith("[aeiou]")) {
							System.out.print("An ");
						} else {
							System.out.print("A ");
						}
						System.out.println(currentName.toLowerCase());
						// TODO case for if amount of ammunition in inventory.
					}
					found = true;
					break;
				}
				if (!found)
					System.out.println("You couldn't find that to look at.");
				break;
			case ("go"):
				switch (verbObject) {
				case ("up"):
					if (player.getCurrentRoom().getAccess()[0]) {
						player.setCurrentRoom(player.getCurrentRoom().getRooms()[0]);
						moved = true;
					}
					break;
				case ("down"):
					if (player.getCurrentRoom().getAccess()[1]) {
						player.setCurrentRoom(player.getCurrentRoom().getRooms()[1]);
						moved = true;
					}
					break;
				case ("left"):
					if (player.getCurrentRoom().getAccess()[2]) {
						player.setCurrentRoom(player.getCurrentRoom().getRooms()[2]);
						moved = true;
					}
					break;
				case ("right"):
					if (player.getCurrentRoom().getAccess()[3]) {
						player.setCurrentRoom(player.getCurrentRoom().getRooms()[3]);
						moved = true;
					}
					break;
				case ("forwards"):
					if (player.getCurrentRoom().getAccess()[4]) {
						player.setCurrentRoom(player.getCurrentRoom().getRooms()[4]);
						moved = true;
					}
					break;
				case ("backwards"):
					if (player.getCurrentRoom().getAccess()[5]) {
						player.setCurrentRoom(player.getCurrentRoom().getRooms()[5]);
						moved = true;
					}
					break;
				case ("in"):
					if (player.getCurrentRoom().getAccess()[6]) {
						player.setCurrentRoom(player.getCurrentRoom().getRooms()[6]);
						moved = true;
					}
					break;
				case ("out"):
					if (player.getCurrentRoom().getAccess()[7]) {
						player.setCurrentRoom(player.getCurrentRoom().getRooms()[7]);
						moved = true;
					}
					break;
				}
				// TODO add more verbs.
			}

	}

	}

	public ArrayList<Room> getRooms() {
		return rooms;
	}

	public void setRooms(ArrayList<Room> rooms) {
		this.rooms = rooms;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}
}
