package org.uned.modulo6.Modulo6;

import java.util.Comparator;
import java.util.HashMap;

public class Node implements Comparator<Node> {
	
	private String word = "";
	private ValidWordType wordType;
	private int appearances = 0;
	private int id;
	private HashMap<Node, Integer> edges;
	
	public Node(int i) {
		this.id = i;
	}
	public String getWord() {
		return word;
	}
	public void setWord(String word) {
		this.word = word;
	}
	public HashMap<Node, Integer> getEdges() {
		return edges;
	}
	public void setEdges(HashMap<Node, Integer> edges) {
		this.edges = edges;
	}
	public int getAppearances() {
		return appearances;
	}
	public void setAppearances(int appearances) {
		this.appearances = appearances;
	}
	public ValidWordType getWordType() {
		return wordType;
	}
	public void setWordType(ValidWordType wordType) {
		this.wordType = wordType;
	}
	
	@Override
	public boolean equals(Object other){
		return (other instanceof Node && ((Node)other).getWord().equals(this.getWord()));
	}
	
	@Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + word.hashCode();
        return result;
    }
	public Integer getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int compare(Node o1, Node o2) {
		return o1.getId().compareTo(o2.getId());
	}
	

	
}
