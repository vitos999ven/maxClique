

import java.security.InvalidParameterException;
import java.util.Random;


public final class Graph {
    
    private static final Random rand = new Random();
    
    /*
    * Количество вершин    
    */
    private final int size;
    
    /*
    * Матрица смежности
    */
    private final int[][] adjacencyMatrix;

    private Graph(int size, int[][] adjacencyMatrix) {
        this.size = size;
        this.adjacencyMatrix = adjacencyMatrix;
    } 
    
    public int getSize() {
        return size;
    }

    public int[][] getAdjacencyMatrix() {
        return adjacencyMatrix;
    }
    
    public static Graph createByProbability(int size, double probability) {
        if (size <= 0 || probability < 0.0001 || probability > 0.9999) {
            throw new InvalidParameterException();
        }
        
        int[][] adjacencyMatrix = new int[size][size];
        
        for (int i = 0; i < size - 1; ++i) {
            for (int j = i + 1; j < size; ++j) {
                int value = ((rand.nextDouble() - probability < 0.0001) ? 1 : 0);
                adjacencyMatrix[i][j] = value;
                adjacencyMatrix[j][i] = value;
            }
        }
        
        return new Graph(size, adjacencyMatrix);
    }
    
}
