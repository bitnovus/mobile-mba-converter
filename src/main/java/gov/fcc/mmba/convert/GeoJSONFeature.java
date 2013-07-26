package gov.fcc.mmba.convert;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class GeoJSONFeature {
	private JsonObject feature;

	public void addProperties(JsonArray input) {
		feature.add("properties", input);
	}
	
	public void addProperties(JsonObject input) {
		feature.add("properties", input);
	}
	
	public void addGeometry(GeoJSONGeometry input) {
		feature.add("geometry", input.getJsonObject());
	}
	
	public GeoJSONFeature() {
		feature = new JsonObject();
		feature.add("type", new JsonPrimitive("Feature"));
	}
	
	public JsonObject getAsJSONObject() {
		return feature;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
