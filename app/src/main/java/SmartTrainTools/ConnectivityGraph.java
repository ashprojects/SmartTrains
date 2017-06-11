/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SmartTrainTools;


import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;

/**
 *
 * @author Ashish
 */
public class ConnectivityGraph implements Serializable{
    
    public WeightedGraph<String,DefaultWeightedEdge> wgraph;

    public static ConnectivityGraph readGraph(InputStream stream) {
        return readGraph(stream, null);
    }

    public static ConnectivityGraph readGraph(InputStream stream, ConnectivityGraph fallbackGraph) {
        ConnectivityGraph graph = new ConnectivityGraph();
        JSONParser parser = new JSONParser();

        try {
            JSONArray edges = (JSONArray) parser.parse(new InputStreamReader(stream));
            for (Object edge : edges) {
                JSONArray edge1 = (JSONArray) edge;
                graph.addEdge(edge1.get(0).toString(), edge1.get(1).toString(), Integer.parseInt(edge1.get(2).toString()));
            }
            return graph;
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            return fallbackGraph;
        }
    }
    public ConnectivityGraph(){
        wgraph = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
    }
    
    public void addStn(String stn){
        wgraph.addVertex(stn);
    }

    public WeightedGraph<String, DefaultWeightedEdge> getGraph() {
        return wgraph;
    }
    
    public void addEdge(String src, String des, int len){
        if (!wgraph.containsVertex(src)) {
            wgraph.addVertex(src);
        }
        if (!wgraph.containsVertex(des)) {
            wgraph.addVertex(des);
        }
        if(wgraph.containsEdge(src, des) || wgraph.containsEdge(des, src))
            return;

        DefaultWeightedEdge e = wgraph.addEdge(src, des);
        if (len >= 0) {
            wgraph.setEdgeWeight(e, len);
        }
    }
    
}
