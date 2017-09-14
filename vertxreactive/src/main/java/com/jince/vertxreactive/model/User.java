package com.jince.vertxreactive.model;

import java.io.Serializable;

import io.vertx.core.json.JsonObject;

public class User implements Serializable {
	public User(JsonObject json) {
		this.name = json.getString("name");
		this.email = json.getString("email");
		this.id = json.getString("_id");
	}

	public User(String name, String email) {
		this.name = name;
		this.email = email;
		this.id = "";
	}

	public User(String id, String name, String email) {
		this.name = name;
		this.email = email;
		this.id = id;
	}

	private String name;
	private String id;

	public String getId() {
		return id;
	}

	public User setId(String id) {
		this.id = id;
		return this;
	}

	public String getName() {
		return name;
	}

	public User setName(String name) {
		this.name = name;
		return this;
	}

	public String getEmail() {
		return email;
	}

	public User setEmail(String email) {
		this.email = email;
		return this;
	}

	private String email;
}
