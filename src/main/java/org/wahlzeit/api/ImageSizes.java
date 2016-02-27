package org.wahlzeit.api;

public class ImageSizes {
	private String[] sizes;
	
	public void setSizes(String[] sizes) {
	    this.sizes = sizes;
	    }
	
	protected boolean isEmpty() {
		return sizes.length == 0;
	}
	
	public String[] getSizes() {
		return sizes;
	}

}