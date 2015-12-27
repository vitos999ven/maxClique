

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;


public class MaxClique {
    
    private static int size = 100;
    private static double probability = 0.6;
    private static double limit = 0.6;
    private static boolean printGraph = false;
    
    public static void configurate() {
        Properties prop = new Properties();
        
        try {
            InputStream inputStream = new FileInputStream("config.properties");
            prop.load(inputStream);
        } catch (IOException e) {
            System.out.println(String.format("Configuration file not found. Using default: size - %d, probability - %f, print - %b", size, probability, printGraph));
            return;
        }

        String sizeProperty = prop.getProperty("size");
        if (sizeProperty == null) {
            System.out.println(String.format("Property 'size' not found! Using default: %d", size));
        }else{
            boolean succ = true;
            try {
                int newSize = Integer.parseInt(sizeProperty);
                if (newSize <= 0)
                    succ = false;
                else
                    size = newSize;
            } catch (NumberFormatException ex) {
                succ = false;
            }
            
            if (!succ) {
                System.out.println(String.format("Wrong 'size' property. Using default: %d", size));
            }
        }
        
        
        String probabilityProperty = prop.getProperty("probability");
        if (probabilityProperty == null) {
            System.out.println(String.format("Property 'probability' not found! Using default: %f", probability));
        }else{
            boolean succ = true;
            try {
                double newProb = Double.parseDouble(probabilityProperty);
                if (newProb < 0.0001 || newProb > 1)
                    succ = false;
                else
                    probability = newProb;
            } catch (NumberFormatException ex) {
                succ = false;
            }
            
            if (!succ) {
                System.out.println(String.format("Wrong 'probability' property. Using default: %f", probability));
            }
        }
        
        String limitProperty = prop.getProperty("limit");
        if (limitProperty == null) {
            System.out.println(String.format("Property 'limit' not found! Using default: %f", limit));
        }else{
            boolean succ = true;
            try {
                double newLim = Double.parseDouble(limitProperty);
                if (newLim < 0.0001 || newLim > 1)
                    succ = false;
                else
                    limit = newLim;
            } catch (NumberFormatException ex) {
                succ = false;
            }
            
            if (!succ) {
                System.out.println(String.format("Wrong 'limit' property. Using default: %f", limit));
            }
        }
        
        String printProperty = prop.getProperty("print");
        if (printProperty == null) {
            System.out.println(String.format("Property 'print' not found! Using default: %b", printGraph));
        }else{
            try {
                printGraph = Boolean.parseBoolean(printProperty);
            } catch (NumberFormatException ex) {
                System.out.println(String.format("Wrong 'print' property. Using default: %d", size));
            }
        }
    }
    
    public static void main(String[] args) {
        configurate();
        
        Graph graph = Graph.createByProbability(size, probability);
        
        if (printGraph)
            printGraph(graph);
        
        MaxCliqueFinder finder = new MaxCliqueFinder(graph);
        Set<Integer> maxClique = finder.findBasic();
        int stepsMC = finder.getLastFindStepsNum();
        Set<Integer> maxCliqueCS = finder.findWithColorSort();
        int stepsMCCS = finder.getLastFindStepsNum();
        Set<Integer> maxCliqueDyn = finder.findByMCD(limit);
        int stepsMCD = finder.getLastFindStepsNum();
        
        System.out.println("MaxClique:");
        System.out.println(maxClique);
        System.out.println(String.format("Steps: %d", stepsMC));
        System.out.println("MaxClique with ColorSort:");
        System.out.println(maxCliqueCS);
        System.out.println(String.format("Steps: %d", stepsMCCS));
        System.out.println("MaxCliqueDyn:");
        System.out.println(maxCliqueDyn);
        System.out.println(String.format("Steps: %d", stepsMCD));
        
    }
    
    public static void printGraph(Graph graph) {
        if (graph == null) {
           return;
        }
        
        StringBuilder builder = new StringBuilder();
        
        builder.append("Graph:\n");
        
        int[][] matrix = graph.getAdjacencyMatrix();
        
        for (int i = 0; i < graph.getSize(); ++i) {
            for (int j = 0; j < graph.getSize(); ++j) {
                builder.append(matrix[i][j]).append(" ");
            }
            
            builder.append("\n");
        }
        
        System.out.println(builder);
    }
    
}
