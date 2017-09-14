package com.jince.vertxreactive.util;

import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;

public class Util {
	public static String convertToJsonString(Object object) {

		return convertToJsonObject(object).toString();
	}

	public static JsonObject convertToJsonObject(Object object) {

		return new JsonObject(Json.encode(object));
	}
}
