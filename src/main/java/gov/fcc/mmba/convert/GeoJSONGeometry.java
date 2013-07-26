package gov.fcc.mmba.convert;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class GeoJSONGeometry {
	private JsonObject geometry;


	public GeoJSONGeometry(String type, JsonArray tempCoordinates) {
		geometry = new JsonObject();
		geometry.add("type", new JsonPrimitive(type));
		geometry.add("coordinates", tempCoordinates);
	}
	
	public JsonObject getJsonObject() {
		return geometry;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
