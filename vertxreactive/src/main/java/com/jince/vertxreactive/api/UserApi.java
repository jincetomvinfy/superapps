package com.jince.vertxreactive.api;

import java.util.List;
import java.util.stream.Collectors;

import com.jince.vertxreactive.model.User;
import com.jince.vertxreactive.util.Util;

import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.ext.mongo.MongoClient;
import io.vertx.rxjava.ext.web.Router;
import io.vertx.rxjava.ext.web.RoutingContext;

public class UserApi extends AbstractVerticle {
	private MongoClient mongoClient;
	public static final String COLLECTION = "users";

	@Override
	public void start() {
		Router router = Router.router(vertx);
		mongoClient = MongoClient.createShared(vertx, config());

		router.get("/user").handler(this::getAll);
		router.post("/user").handler(this::addOne);
		router.get("/user/:id").handler(this::getOne);
		router.put("/user/:id").handler(this::updateOne);
		router.delete("/user/:id").handler(this::deleteOne);

		vertx.createHttpServer().requestHandler((router::accept)).listen(config().getInteger("http.port", 8080));
	}

	@Override
	public void stop() throws Exception {
		mongoClient.close();
	}

	private void addOne(RoutingContext routingContext) {
		final User user = Json.decodeValue(routingContext.getBodyAsString(), User.class);

		mongoClient.insert(COLLECTION, Util.convertToJsonObject(user),
				r -> routingContext.response().setStatusCode(201)
						.putHeader("content-type", "application/json; charset=utf-8")
						.end(Json.encodePrettily(user.setId(r.result()))));
	}

	private void getOne(RoutingContext routingContext) {
		final String id = routingContext.request().getParam("id");
		if (id == null) {
			routingContext.response().setStatusCode(400).end();
		} else {
			mongoClient.findOne(COLLECTION, new JsonObject().put("_id", id), null, ar -> {
				if (ar.succeeded()) {
					if (ar.result() == null) {
						routingContext.response().setStatusCode(404).end();
						return;
					}
					User User = new User(ar.result());
					System.out.println("Returning : " + Json.encodePrettily(User));
					routingContext.response().setStatusCode(200)
							.putHeader("content-type", "application/json; charset=utf-8")
							.end(Json.encodePrettily(User));
				} else {
					routingContext.response().setStatusCode(404).end();
				}
			});
		}
	}

	private void updateOne(RoutingContext routingContext) {
		final String id = routingContext.request().getParam("id");
		JsonObject json = routingContext.getBodyAsJson();
		if (id == null || json == null) {
			routingContext.response().setStatusCode(400).end();
		} else {
			mongoClient.update(COLLECTION, new JsonObject().put("_id", id), // Select
																			// a
																			// unique
																			// document
					// The update syntax: {$set, the json object containing the
					// fields to update}
					new JsonObject().put("$set", json), v -> {
						if (v.failed()) {
							routingContext.response().setStatusCode(404).end();
						} else {
							routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
									.end(Json.encodePrettily(
											new User(id, json.getString("name"), json.getString("origin"))));
						}
					});
		}
	}

	private void deleteOne(RoutingContext routingContext) {
		String id = routingContext.request().getParam("id");
		if (id == null) {
			routingContext.response().setStatusCode(400).end();
		} else {
			mongoClient.removeOne(COLLECTION, new JsonObject().put("_id", id),
					ar -> routingContext.response().setStatusCode(204).end());
		}
	}

	private void getAll(RoutingContext routingContext) {
		mongoClient.find(COLLECTION, new JsonObject(), results -> {
			List<JsonObject> objects = results.result();
			List<User> users = objects.stream().map(User::new).collect(Collectors.toList());
			routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
					.end(Json.encodePrettily(users));
		});
	}
}