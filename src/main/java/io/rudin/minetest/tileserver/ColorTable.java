package io.rudin.minetest.tileserver;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class ColorTable {

	public int load(InputStream input) {

		try (BufferedReader reader = new BufferedReader(new InputStreamReader(input, "UTF-8"))){

			int count = 0;
			while (true) {
				String line = reader.readLine();

				if (line == null)
					break;

				line = line.trim();
				
				if (line.startsWith("#") || line.isEmpty())
					continue;
				
				line = line.replace("\t", " ");
				
				String name = null;
				int r = -1, g = -1, b = -1;
				
				for (String part: line.split("[ ]")) {
					
					if (part.isEmpty())
						continue;
					
					if (name == null) {
						name = part;
						
					} else if (r < 0) {
						r = Integer.parseInt(part);
						
					} else if (g < 0) {
						g = Integer.parseInt(part);
						
					} else if (b < 0) {
						b = Integer.parseInt(part);
						
					}
					
				}
				
				if (name != null && r >= 0 && g >= 0 && b >= 0) {
					colorMap.put(name, new Color(r,g,b));
				}
				
			}
			
			return count;

		} catch (Exception e) {
			return -1;
		}
	}

	public Map<String, Color> getColorMap() {
		return colorMap;
	}

	private final Map<String, Color> colorMap = new HashMap<>();

	public static class Color {
		public Color(int r, int g,int b) {
			this.r = r;
			this.g = g;
			this.b = b;
		}

		public final int r,g,b;
	}

}
