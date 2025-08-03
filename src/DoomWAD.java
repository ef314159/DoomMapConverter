import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class DoomWAD {
	File wad;
	List<DoomMap> maps;
	
	byte[] data;
	int lumps, dirPos;
	
	public DoomWAD(String filename) {
		maps = new ArrayList<DoomMap>();
		
		try {
			data = Files.readAllBytes(Paths.get(filename));
			
			lumps = readInt(data, 4);
			dirPos = readInt(data, 8);

			//System.out.println(lumps);
			//System.out.println(dirPos);
			
			for (int i = 0; i < lumps; ++i) {
				
				String name = getLumpName(i);
				
				//System.out.println(name);
				
				if (name.equals("THINGS")) {
					DoomMap newMap = loadDoomMap(i);
					if (newMap != null) maps.add(newMap);
				}
				
				if (name.equals("TEXTMAP")) {
					DoomMap newMap = loadUDMFMap(i);
					if (newMap != null) maps.add(newMap);
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
			//TODO
		}
	}
	
	public List<DoomMap> getMaps() {
		return maps;
	}
	
	public DoomMap loadDoomMap(int lump) {
		try {
			String mapName = getLumpName(lump-1);
			
			byte[] things = null, linedefs = null, sidedefs = null, vertices = null, ssectors = null, sectors = null;
			
			things = getLump(lump);
			
			int i = 1;
			String nextLumpName;
			
			while (lump+i < lumps && isMapLump(mapName, nextLumpName = getLumpName(lump+i))) {
				if (nextLumpName.equals("LINEDEFS")) linedefs = getLump(lump+i);
				if (nextLumpName.equals("SIDEDEFS")) sidedefs = getLump(lump+i);
				if (nextLumpName.equals("VERTEXES")) vertices = getLump(lump+i);
				if (nextLumpName.equals("SSECTORS")) ssectors = getLump(lump+i);
				if (nextLumpName.equals("SECTORS")) sectors = getLump(lump+i);
				i++;
			}
			
			DoomMap map = new DoomMap(mapName, things, linedefs, sidedefs, vertices, ssectors, sectors);
			return map;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public DoomMap loadUDMFMap(int lump) {
		try {
			String mapName = getLumpName(lump-1);
			byte[] textmap = getLump(lump);
			
			DoomMap map = new DoomMap(mapName, textmap);
			return map;
		} catch (Exception e) {
			return null;
		}
	}
	
	public static int readInt(byte[] arr, int startPos) {
		return arr[startPos] + (arr[startPos+1]<<8) + (arr[startPos+2]<<16) + (arr[startPos+3]<<24);
	}
	
	public String getLumpName(int lump) {
		StringBuilder name = new StringBuilder();

		for (int j = 0; j < 8; ++j) {
			name.append((char)data[dirPos+lump*16 + 8 + j]);
		}
		
		return name.toString().toUpperCase().trim();
	}
	
	public byte[] getLump(int lump) {
		int pos = readInt(data, dirPos+lump*16);
		int size = readInt(data, dirPos+lump*16+4);
		
		//System.out.println("Lump " + getLumpName(lump) + " has pos " + pos + ", size " + size);
		
		return Arrays.copyOfRange(data, pos, pos+size);
	}
	
	public boolean isMapLump(String mapID, String name) {
		return Set.of(
				"LINEDEFS",
				"SIDEDEFS",
				"VERTEXES",
				"SEGS",
				"SSECTORS",
				"NODES",
				"SECTORS",
				"REJECT",
				"BLOCKMAP",
				"SCRIPTS",
				"LEAFS",
				"LIGHTS",
				"MACROS",
				"GL_"+mapID,
				"GL_VERT",
				"GL_SEGS",
				"GL_SSECT",
				"GL_NODES",
				"GL_PVS",
				"BEHAVIOR",
				"DIALOGUE",
				"ZNODES",
				"ENDMAP"
				).contains(name);
	}
}