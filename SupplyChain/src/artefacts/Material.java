package artefacts;

public class Material {
	
	static int count;
	
	private int id;
	private String name;
	
	public Material(String name){
		this.id = count++;
		this.name = name;
	}
	
	public String getName(){
		return this.name;
	}
	
	public int getId(){
		return this.id;
	}
	

}
