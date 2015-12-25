import java.util.Set;


public class MaxClique {
    
    public static void main(String[] args) {
        int size = 5;
        double possibility = 0.6;
        
        if (args.length == 2) {
            boolean succ = true;
            try {
                int newSize = Integer.parseInt(args[0]);
                if (newSize <= 0)
                    succ = false;
            } catch (NumberFormatException ex) {
                succ = false;
            }
            
            if (!succ) {
                System.out.println(String.format("Wrong size argument. Using default: %d", size));
            }
            
            succ = true;
            try {
                double newPoss = Double.parseDouble(args[1]);
                if (newPoss < 0.0001 || newPoss >= 0.9999)
                    succ = false;
            } catch (NumberFormatException ex) {
                succ = false;
            }
            
            if (!succ) {
                System.out.println(String.format("Wrong possibility argument. Using default: %f", possibility));
            }
        } else {
            System.out.println(String.format("Wrong arguments. Using default: size - %d, possibility - %f", size, possibility));
        }
        
        Graph graph = Graph.createByProbability(size, possibility);
        
        printGraph(graph);
        
        MaxCliqueFinder finder = new MaxCliqueFinder(graph);
        Set<Integer> maxClique = finder.find();
        System.out.println("Result:");
        System.out.println(maxClique);
        
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
