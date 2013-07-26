package gov.fcc.mmba.convert;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

public class GeoConvert {
	public static final String INTERNAL_DELIMITER = " | ";
	public static final String INTERNAL_DESIGNATOR = "=";

	public static GeoJSONFeature getFeature(String jsonText) {
		JsonParser parser = new JsonParser();

		JsonObject jsobj = (JsonObject) parser.parse(jsonText);
		GeoJSONFeature feature = new GeoJSONFeature();
		JsonArray tempCoordinates = new JsonArray();

		JsonElement datetime = jsobj.get("datetime");
		JsonElement _sourceip = jsobj.get("_sourceip");
		JsonElement sim_operator_code = jsobj.get("sim_operator_code");
		JsonElement app_version_name = jsobj.get("app_version_name");
		JsonElement submission_type = jsobj.get("submission_type");

		JsonArray metrics = (JsonArray) jsobj.get("metrics");
		JsonArray tests = (JsonArray) jsobj.get("tests");
		//JsonArray conditions = (JsonArray) jsobj.get("conditions");

		for (Object temp : metrics) {
			JsonObject tempjsobj = (JsonObject) temp;

			if (tempjsobj.has("longitude") && tempjsobj.has("latitude")) {
				JsonArray tempArray = new JsonArray();
				tempArray.add(new JsonPrimitive(tempjsobj.get("longitude").getAsNumber()));
				tempArray.add(new JsonPrimitive(tempjsobj.get("latitude").getAsNumber()));

				tempCoordinates.add(tempArray);
			}
		}
		GeoJSONGeometry geometry = new GeoJSONGeometry("MultiPoint", tempCoordinates);
		feature.addGeometry(geometry);

		JsonObject aggregated = new JsonObject();
		StringBuilder sb = new StringBuilder();

		int getCount = 0;
		int postCount = 0;
		int latencyCount = 0;

		aggregated.add("datetime", datetime);
		aggregated.add("sourceip", _sourceip);
		aggregated.add("sim_operator_code", sim_operator_code);
		aggregated.add("app_version_name", app_version_name);
		aggregated.add("submission_type", submission_type);

		for (Object temp : tests) {
			sb = new StringBuilder();
			JsonObject tempJSObj = (JsonObject) temp;
			String testType = tempJSObj.get("type").getAsString();

			if (testType.equals("JHTTPGETMT")) {
				sb.append("target").append(INTERNAL_DESIGNATOR).append(tempJSObj.get("target")).append(INTERNAL_DELIMITER);
				sb.append("bytes_sec").append(INTERNAL_DESIGNATOR).append(tempJSObj.get("bytes_sec").getAsInt()*0.000008).append(INTERNAL_DELIMITER);
				sb.append("success").append(INTERNAL_DESIGNATOR).append(tempJSObj.get("success")).append(INTERNAL_DELIMITER);
				sb.append("transfer_bytes").append(INTERNAL_DESIGNATOR).append(tempJSObj.get("transfer_bytes").getAsNumber()).append(INTERNAL_DELIMITER);
				sb.append("transfer_time").append(INTERNAL_DESIGNATOR).append(tempJSObj.get("transfer_time").getAsNumber());
				aggregated.add(testType + " (" + (++getCount) + ")", new JsonPrimitive(sb.toString()));
			}
			else if (testType.equals("JHTTPPOSTMT")) {
				sb.append("target").append(INTERNAL_DESIGNATOR).append(tempJSObj.get("target")).append(INTERNAL_DELIMITER);
				sb.append("bytes_sec").append(INTERNAL_DESIGNATOR).append(tempJSObj.get("bytes_sec").getAsInt()*0.000008).append(INTERNAL_DELIMITER);
				sb.append("success").append(INTERNAL_DESIGNATOR).append(tempJSObj.get("success")).append(INTERNAL_DELIMITER);
				sb.append("transfer_bytes").append(INTERNAL_DESIGNATOR).append(tempJSObj.get("transfer_bytes").getAsNumber()).append(INTERNAL_DELIMITER);
				sb.append("transfer_time").append(INTERNAL_DESIGNATOR).append(tempJSObj.get("transfer_time").getAsNumber());
				aggregated.add(testType + " (" + (++postCount) + ")", new JsonPrimitive(sb.toString()));
			}
			else if (testType.equals("JUDPLATENCY")) {
				sb.append("target").append(INTERNAL_DESIGNATOR).append(tempJSObj.get("target")).append(INTERNAL_DELIMITER);
				sb.append("success").append(INTERNAL_DESIGNATOR).append(tempJSObj.get("success")).append(INTERNAL_DELIMITER);
				sb.append("rtt_avg").append(INTERNAL_DESIGNATOR).append(tempJSObj.get("rtt_avg").getAsInt()/1000).append(INTERNAL_DELIMITER);
				sb.append("rtt_min").append(INTERNAL_DESIGNATOR).append(tempJSObj.get("rtt_min").getAsInt()/1000).append(INTERNAL_DELIMITER);
				sb.append("rtt_max").append(INTERNAL_DESIGNATOR).append(tempJSObj.get("rtt_max").getAsInt()/1000).append(INTERNAL_DELIMITER);
				sb.append("received_packets").append(INTERNAL_DESIGNATOR).append(tempJSObj.get("received_packets").getAsNumber()).append(INTERNAL_DELIMITER);
				sb.append("lost_packets").append(INTERNAL_DESIGNATOR).append(tempJSObj.get("lost_packets").getAsNumber());
				aggregated.add(testType + " (" + (++latencyCount) + ")", new JsonPrimitive(sb.toString()));
			}
		}

		int gsmCount = 0;
		int cdmaCount = 0;
		int networkCount = 0;

		for (Object temp : metrics) {
			sb = new StringBuilder();
			JsonObject tempJSObj = (JsonObject) temp;
			String testType = tempJSObj.get("type").getAsString();
			if (testType.equals("gsm_cell_location")) {
				sb.append("gsm_signal_strength").append(INTERNAL_DESIGNATOR).append(tempJSObj.get("signal_strength").getAsNumber());
				aggregated.add(testType + " (" + (++gsmCount) + ")", new JsonPrimitive(sb.toString()));
			}
			else if (testType.equals("cdma_cell_location")) {
				sb.append("dbm").append(INTERNAL_DESIGNATOR).append(tempJSObj.get("dbm"));
				aggregated.add(testType + " (" + (++cdmaCount) + ")", new JsonPrimitive(sb.toString()));
			}
			else if (testType.equals("network_data")) {
				sb.append("network_type").append(INTERNAL_DESIGNATOR).append(tempJSObj.get("network_type")).append(INTERNAL_DELIMITER);
				sb.append("active_network_type").append(INTERNAL_DESIGNATOR).append(tempJSObj.get("active_network_type")).append(INTERNAL_DELIMITER);
				sb.append("network_operator_name").append(INTERNAL_DESIGNATOR).append(tempJSObj.get("network_operator_name"));
				aggregated.add(testType + " (" + (++networkCount) + ")", new JsonPrimitive(sb.toString()));
			}
		}

		feature.addProperties(aggregated);

		return feature;
	}

	private static String readFile(FileReader fileIn) {
		BufferedReader in = new BufferedReader(fileIn);
		StringBuilder sb = new StringBuilder();
		try {
			String temp = in.readLine();
			while (temp != null) {
				sb.append(temp);
				temp = in.readLine();
			}
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sb.toString();
	}

	private static void explore(File start, GeoJSONFeatureCollection collection) throws FileNotFoundException {
		FileReader fileIn;
		String jsonText = "";
		File[] fileList = start.listFiles();

		for (int i = 0; i < fileList.length; i++) {
			if (fileList[i].isFile()) {
				fileIn = new FileReader(fileList[i]);
				jsonText = readFile(fileIn);
				GeoJSONFeature feature = getFeature(jsonText);
				collection.addFeature(feature);
			}
			else if (fileList[i].isDirectory()) {
				explore(fileList[i], collection);
			}
		}

		return;
	}

	public static void convertInDir(String pathName, String destination) {
		try {
			File folder = new File(pathName);
			GeoJSONFeatureCollection collection = new GeoJSONFeatureCollection();

			explore(folder, collection);
			collection.addCRS("urn:ogc:def:crs:OGC:1.3:CRS84");
			System.out.println(collection.toString());

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 *  { "type": "Feature",
	 *    "geometry": {
	 *        "type": "LineString",
	 *        "coordinates": [
	 *           [102.0, 0.0], [103.0, 1.0], [104.0, 0.0], [105.0, 1.0]
	 *           ]
	 *       }
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		try {
			File folder = new File("examples");
			GeoJSONFeatureCollection collection = new GeoJSONFeatureCollection();

			explore(folder, collection);
			collection.addCRS("urn:ogc:def:crs:OGC:1.3:CRS84");
			System.out.println(collection.toString());

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}

}
