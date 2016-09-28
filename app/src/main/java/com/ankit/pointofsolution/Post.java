package com.ankit.pointofsolution;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class Post {

	@SerializedName("id")
	public long ID;
	public String role;
	public String userId;
	public String storeName;
	@SerializedName("date")
	public Date dateCreated;
	public String body;
	
	public List<Tag> tags;
	
	public Post() {
		
	}
}
