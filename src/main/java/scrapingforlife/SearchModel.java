package scrapingforlife;

public class SearchModel {

	private String name;
	private String email;
	private String database;
	private String taxonomy;
	private String fixedMods;
	private String variableMods;
	private String peptideTolValue;
	private String peptideTolUnity;
	private String peakListFolderPath;
	private String peakListResultPath;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getDatabase() {
		return database;
	}
	public void setDatabase(String database) {
		this.database = database;
	}
	
	public String getTaxonomy() {
		return taxonomy;
	}
	public void setTaxonomy(String taxonomy) {
		this.taxonomy = taxonomy;
	}
	
	public String getFixedMods() {
		return fixedMods;
	}
	public void setFixedMods(String fixedMods) {
		this.fixedMods = fixedMods;
	}
	
	public String getVariableMods() {
		return variableMods;
	}
	public void setVariableMods(String variableMods) {
		this.variableMods = variableMods;
	}
	
	public String getPeptideTolValue() {
		return peptideTolValue;
	}
	public void setPeptideTolValue(String peptideTolValue) {
		this.peptideTolValue = peptideTolValue;
	}
	public String getPeptideTolUnity() {
		return peptideTolUnity;
	}
	public void setPeptideTolUnity(String peptideTolUnity) {
		this.peptideTolUnity = peptideTolUnity;
	}
	public String getPeakListFolderPath() {
		return this.peakListFolderPath;
	}
	public void setPeakListFolderPath(String peakListFolderPath) {
		this.peakListFolderPath = peakListFolderPath;
	}
	public String getPeakListResultPath() {
		return peakListResultPath;
	}
	public void setPeakListResultPath(String peakListResultPath) {
		this.peakListResultPath = peakListResultPath;
	}
}
