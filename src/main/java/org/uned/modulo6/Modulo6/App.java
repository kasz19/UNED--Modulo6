package org.uned.modulo6.Modulo6;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import edu.uci.ics.jung.graph.SparseGraph;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws ClassNotFoundException, IOException
    {
    	System.out.println("Starting ...");
    	
    	if(args == null || args.length == 0){
    		System.out.println("ERROR EN LA LECTURA DE DATOS - DEBE INTRODUCIR UNA URL O UNA RUTA A UN FICHERO DE EXTENSIÓN .txt");
        	return;
    	}
    	
        String path = args[0];
        
        HashMap<String, Node> nodes = new HashMap<String, Node>();
        
        if(path.startsWith("http")){
        	nodes = Modulo6Service.getWordsFormUrl(path);
        }
        else{
        	nodes = Modulo6Service.getWordsFormPath(path);
        }
        
        if(nodes == null || nodes.isEmpty()) {
        	System.out.println("ERROR EN LA LECTURA DE DATOS - NO SE HA RECUPERADO INFORMACIÓN");
        	return;
        }
        
        ArrayList<Node> nodesWithEdges = Modulo6Service.calculateEdges(nodes);

        Modulo6Service.writeToFile(nodesWithEdges);
        
        ArrayList<KeyPhrase> keyPhrases = Modulo6Service.getKeyPhrases(nodesWithEdges);
        
        Modulo6Service.writeToFile(keyPhrases);
        
        System.out.println("Finished.");
    }
}
