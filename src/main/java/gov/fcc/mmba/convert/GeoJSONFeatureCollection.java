package gov.fcc.mmba.convert;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class GeoJSONFeatureCollection {
	private JsonObject features;
	private JsonArray featureList;

	public GeoJSONFeatureCollection() {
		super();
		features = new JsonObject();
		featureList = new JsonArray();
		features.add("type", new JsonPrimitive("FeatureCollection"));
	}
	
	public void addFeature(GeoJSONFeature inputGeoJSONFeature) {
		featureList.add(inputGeoJSONFeature.getAsJSONObject());
		features.add("features", featureList);
	}
	
	public String toString() {
		return features.toString();
	}
	
	public JsonObject getAsJsonObject() {
		return features;
	}
	
	public void addCRS(String crs_name) {
		JsonObject crs = new JsonObject();
		crs.add("type", new JsonPrimitive("name"));
		JsonObject crs_obj = new JsonObject();
		crs_obj.add("name", new JsonPrimitive(crs_name));
		crs.add("properties", crs_obj);
		features.add("crs", crs);
	}
}
