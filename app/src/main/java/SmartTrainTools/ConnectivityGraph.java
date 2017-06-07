/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SmartTrainTools;


import org.jgrapht.*;
import java.io.Serializable;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

/**
 *
 * @author Ashish
 */
public class ConnectivityGraph implements Serializable{
    
    public WeightedGraph<String,DefaultWeightedEdge> wgraph;
    
    public ConnectivityGraph(){
        wgraph=new SimpleWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);
    }
    
    public void addStn(String stn){
        wgraph.addVertex(stn);
    }

    public WeightedGraph<String, DefaultWeightedEdge> getGraph() {
        return wgraph;
    }
    
    public void addEdge(String src, String des, int len){
        if(wgraph.containsEdge(src, des) || wgraph.containsEdge(des, src))
            return;
        
        //System.out.println(src+"--"+des+":-"+len);
        try {
            DefaultWeightedEdge e= (DefaultWeightedEdge) wgraph.addEdge(src, des);
            if(len<0)
                System.out.println("NEGATIVEEEEEEE");
            else 
                wgraph.setEdgeWeight(e, len);
        
        
        } catch (Exception E ){
            System.out.println("Except: "+src+des+"-"+len);
        }
    }
    
}
