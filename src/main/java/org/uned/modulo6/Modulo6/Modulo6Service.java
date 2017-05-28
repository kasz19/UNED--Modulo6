package org.uned.modulo6.Modulo6;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseGraph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.VisualizationImageServer;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


public class Modulo6Service {

	public static HashMap<String, Node> getWordsFormPath(String path) throws ClassNotFoundException, IOException {
		String line;
		HashMap<String, Node> resul = new HashMap<String, Node>();
		Node node = null;
		ArrayList<String> taggedWords = new ArrayList<>();
		// Initialize the tagger
        MaxentTagger tagger = new MaxentTagger("taggers/left3words-wsj-0-18.tagger");
        
		try  {
			InputStream fis = new FileInputStream(path);
		    InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
		    BufferedReader br = new BufferedReader(isr);
		    int countWords = 0;
		    while ((line = br.readLine()) != null) {
		        String [] words = line.split(" ");
		        for(int i = 0; i < words.length; i++){
		        	String word = words[i].replaceAll("[^A-Za-z]", "").toLowerCase(); // Eliminamos todo lo que no sea alfabetico
		        	String wordTagged = tagger.tagString(word);
					String tag = wordTagged.substring(wordTagged.indexOf("/") + 1).trim();
					taggedWords.add(wordTagged);
					if(!isAdjectiveOrNoun(tag)){
						continue;
					}
		        	node = resul.containsKey(word) ? resul.get(word) : new Node(++countWords);
					node.setAppearances(node.getAppearances()+1);
					node.setWordType(ValidWordType.valueOf(tag));
					node.setWord(word);
					resul.put(word, node);
		        }
		    }
		}
		catch(Exception e){}
		
		printTaggedWords(taggedWords);
		return resul;
	}

	public static HashMap<String, Node> getWordsFormUrl(String url) throws IOException, ClassNotFoundException {
		StringBuffer sb = getStringFromUrl(url);
		String [] totalWords = sb.toString().split(" ");
		HashMap<String, Node> resul = new HashMap<String, Node>();
		Node node = null;
		ArrayList<String> taggedWords = new ArrayList<>();
		// Initialize the tagger
        MaxentTagger tagger = new MaxentTagger("taggers/left3words-wsj-0-18.tagger");
		
		for(int i = 0; i < totalWords.length; i++)
		{
			String word = totalWords[i];
			word = word.replaceAll("[^A-Za-z]", "").toLowerCase(); // Eliminamos todo lo que no sea alfabetico
			String wordTagged = tagger.tagString(word);
			taggedWords.add(wordTagged);
			String tag = wordTagged.substring(wordTagged.indexOf("/") + 1).trim();
			if(!isAdjectiveOrNoun(tag)){
				continue;
			}
			
			node = resul.containsKey(word) ? resul.get(word) : new Node(i + 1);
			node.setAppearances(node.getAppearances()+1);
			node.setWordType(ValidWordType.valueOf(tag));
			node.setWord(word);
			resul.put(word, node);
		}
		printTaggedWords(taggedWords);
		return resul;
	}

	private static void printTaggedWords(ArrayList<String> taggedWords) {
			 try{
		            PrintWriter writer = new PrintWriter("TAGGED_TEXT.txt.result", "UTF-8");
		            writer.println("Tagged Words Using left3words-wsj-0-18.tagger");
		            taggedWords.forEach((key) -> writer.println(key)) ; 
		            writer.close();
		        } catch (IOException e) { }
		
	}

	private static boolean isAdjectiveOrNoun(String tag) {
		for(ValidWordType wt : ValidWordType.values()){
			if(tag.equals(wt.name())){
				return true;
			}
		}
		return false;
	}

	private static StringBuffer getStringFromUrl(String urlStr) throws IOException {
		Document doc = Jsoup.connect(urlStr).get();
		return new StringBuffer(Jsoup.parse(doc.text()).text());
	}

	public static ArrayList<Node> calculateEdges(HashMap<String, Node> nodes) {
	 Iterator<Entry<String, Node>> it = nodes.entrySet().iterator();
	 ArrayList<Node> resul = new ArrayList<Node>();
	 
	 while (it.hasNext()) 
	 {
	     Map.Entry<String, Node> pair = it.next();
	     Node n = pair.getValue();
	     resul.add(n);
	     it.remove();
	 }
	 
	 for(Node n : resul){
		 HashMap<Node, Integer> edges = new HashMap<Node, Integer>();
		 for(Node nAux : resul){
			 if(nAux.getWord().equals(n.getWord())) continue;
		  	 int weight = nAux.getAppearances() < n.getAppearances() ? nAux.getAppearances() : n.getAppearances();
		  	 edges.put(nAux, weight);
		 }
		 n.setEdges(edges);
	 }
	 
	 Collections.sort(resul, new Comparator<Node>() 
     {
         public int compare(Node o1, Node o2) 
         {
             return o1.getId().compareTo(o2.getId());
         }
     }
     );
	 
	return resul;
	
	}

	public static void writeToFile(ArrayList<Node> nodesWithEdges) {
		 try{

	            PrintWriter writer = new PrintWriter("MODULO6_OUTPUT.txt", "UTF-8");
	            writer.println("*vertices <# of vertices> ");
	            for(Node node : nodesWithEdges){
	            	writer.println(node.getId() + " \"" + node.getWord() + "\"" );
	            }
	            writer.println("*edges");
	            for(Node node : nodesWithEdges){
	            	 node.getEdges().forEach
	            	 (
	            		(k,v) -> writer.println(node.getId() + " " + k.getId() + " " + v.intValue())
	            	 );
	            }
	            writer.close();
	        } catch (IOException e) {
	           // do something
	        }
	}
	
	public static void writeToFile(List<KeyPhrase> keys) {
		 try{
	            PrintWriter writer = new PrintWriter("IDENTIFICADORES.txt.result", "UTF-8");
	            writer.println("keyphrases");
	            keys.forEach((key) -> writer.println(key.getWord1() + " " + key.getWord2() + " " + key.getWeight() )) ; 
	            writer.close();
	        } catch (IOException e) {
	           // do something
	        }
	}
	
	public static ArrayList<KeyPhrase> getKeyPhrases(ArrayList<Node> nodesWithEdges) {
		int maxWeight = -1;
		Node maxNodeA = null;
		Node maxNodeB = null;
		ArrayList<Integer> topWeights = new ArrayList<>();
		ArrayList<KeyPhrase> keyPhrases = new ArrayList<>();
		
		for(int i = 0; i < 6; i++){
			for(Node node : nodesWithEdges){
		       	Set<Node> edgesWeight = node.getEdges().keySet();
		       	for(Node n : edgesWeight){
		       		int weight = node.getEdges().get(n);
		       		if(!topWeights.contains(weight) && weight > maxWeight){
		       			maxWeight = weight;
		       			maxNodeA = n;
		       			maxNodeB = node;
		       		}
		       	}
		     }
			if(maxWeight == -1){
				break;
			}
			topWeights.add(maxWeight);
			KeyPhrase kp = new KeyPhrase();
			kp.setWeight(maxWeight);
			kp.setWord1(maxNodeA.getWord());
			kp.setWord2(maxNodeB.getWord());
			keyPhrases.add(kp);
			maxWeight = -1;
		}
		return keyPhrases;
	}

}
	
