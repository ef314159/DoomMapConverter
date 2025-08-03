public class DoomMap {
	String id;
	
	public DoomMap(String id,
			byte[] things,
			byte[] linedefs,
			byte[] sidedefs,
			byte[] vertices,
			byte[] ssectors,
			byte[] sectors) {
		this.id = id;
	}
	
	public DoomMap(String id, byte[] textmap) {
		this.id = id;
	}
	
	public Linedef[] getLinedefs() {
		return null;
	}
}
