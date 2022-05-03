package com.itmsd.medical.payload.request;

import java.util.List;

public class SearchRequest {
	private String category;
	private String speciality;
	private Integer codePostal;
	private String ville;
	private List<String> langue;
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getSpeciality() {
		return speciality;
	}
	public void setSpeciality(String speciality) {
		this.speciality = speciality;
	}
	public Integer getCodePostal() {
		return codePostal;
	}
	public void setCodePostal(Integer codePostal) {
		this.codePostal = codePostal;
	}
	public String getVille() {
		return ville;
	}
	public void setVille(String ville) {
		this.ville = ville;
	}
	public List<String> getLangue() {
		return langue;
	}
	public void setLangue(List<String> langue) {
		this.langue = langue;
	}
	
}
