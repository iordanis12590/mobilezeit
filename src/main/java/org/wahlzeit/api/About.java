package org.wahlzeit.api;

public class About {
	String description;
	
	public About() {
	}
	
	public About(String description) {
		super();
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
}
