import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.IntStream;


public class MaxCliqueFinder {
    
    private final Graph graph;
    
    private final Set<Integer> Q = new TreeSet<>();
    private final Set<Integer> Qmax = new TreeSet<>();
    
    private int maxDegree = 0; 
    
    private List<List<Integer> > adjacentVertices;
    
    public MaxCliqueFinder(Graph graph) {
        this.graph = graph;
    }
    
    public Set<Integer> find() {
        List<Integer> R = new ArrayList<>(graph.getSize());
        List<Integer> degrees = new ArrayList<>(graph.getSize());
        
        for (int i = 1; i <= graph.getSize(); ++i) {
            R.add(i);
            degrees.add(getDegree(i));
        }
        
        
        R.sort((first, second) -> {
            return degrees.get(first - 1) - degrees.get(second - 1);
        });
        
        maxDegree = Collections.max(degrees);
        
        List<Integer> No = new ArrayList<>(Collections.nCopies(graph.getSize(), 0));
        
        for (int i = 0; i < maxDegree; ++i) {
            No.set(R.get(i) - 1, i + 1); 
        }
        
        for (int i = maxDegree; i < graph.getSize(); ++i) {
            No.set(R.get(i) - 1, maxDegree + 1); 
        }
        
        createAdjacentVertices();
        
        expand(R, No);
        
        return Qmax;
    }
    
    private int getDegree(int vertexIndex) {
        return IntStream.of(graph.getAdjacencyMatrix()[vertexIndex - 1]).sum();
    }
    
    private void expand(List<Integer> R, List<Integer> No) {
        while (!R.isEmpty()) {
            
            int vertexIndex = getVertexIndexWithMaxNo(R, No);
            int vertex = R.get(vertexIndex);
            R.remove(vertexIndex);
            
            if (Q.size() + No.get(vertexIndex) > Qmax.size()) {
                Q.add(vertex);
                List<Integer> intersection = createIntersection(R, adjacentVertices.get(vertex - 1));
                
                if (!intersection.isEmpty()) {
                    List<Integer> newNo = new ArrayList<>(Collections.nCopies(intersection.size(), 0));
                    makeNumberSort(intersection, newNo);
                    expand(intersection, newNo);
                } else if (Q.size() > Qmax.size()) {
                    Qmax.clear();
                    Qmax.addAll(Q);
                } 
                
                Q.remove(vertex);
            } else {
                return;
            }
        }
    }
    
    public int getVertexIndexWithMaxNo(List<Integer> R, List<Integer> No) {
        int maxNo = 0;
	int vertexIndex = 0;
        
	for (int i = 0; i < R.size(); ++i) {
            if (No.get(i) > maxNo) {
                vertexIndex = i;
                maxNo = No.get(i);
            }
        }
        
        return vertexIndex;
    }
    
    public List<Integer> createIntersection(List<Integer> first, List<Integer> second) {
        List<Integer> intersection = new ArrayList<>();
        return createIntersection(first, second, intersection);
    }
    
    public List<Integer> createIntersection(List<Integer> first, List<Integer> second, List<Integer> intersection) {
        first.stream().filter((t) -> (second.contains(t))).forEach((t) -> {
            intersection.add(t);
        });

        return intersection;
    }
    
    public void createAdjacentVertices() {
        adjacentVertices = new ArrayList<>(graph.getSize());
        int[][] matrix = graph.getAdjacencyMatrix();
        
        for (int i = 0; i < graph.getSize(); ++i) {
            List<Integer> vertices = new ArrayList<>();
            adjacentVertices.add(vertices);
            for (int j = 0; j < graph.getSize(); ++j) {
                if (matrix[i][j] == 1) {
                    vertices.add(j + 1);
                }
            }
        }
    }
    
    public void makeNumberSort(List<Integer> R, List<Integer> No) {
        if (R == null || No == null || R.size() != No.size()) {
            return;
        }
        
        int maxNo = 0;
        Map<Integer, List<Integer> > C = new HashMap<>();
        
        int rIndex = 0;
        while(rIndex < R.size()) {
            int vertex = R.get(rIndex);
            int k = 1;
            
            List<Integer> intersection = new ArrayList<>();
            while (maxNo >= k) {
                intersection.clear();
                if (createIntersection(C.get(k), adjacentVertices.get(vertex - 1), intersection).isEmpty()) {
                    break;
                }
                
                ++k;
            }
            
            if (k > maxNo) {
		maxNo = k;
            }
            
            List<Integer> Ck = C.get(k);
            if (Ck == null) {
                Ck = new ArrayList<>();
                C.put(k, Ck);
            }
            
            No.set(R.indexOf(vertex), k);
            Ck.add(vertex);
            
            ++rIndex;
        }
        
        int i = 0;
        for (Map.Entry<Integer, List<Integer>> entry : C.entrySet()) {
            for (int j = 0; j < entry.getValue().size(); ++j) {
                R.set(i, entry.getValue().get(j));
                ++i;
            }
        }
    }
}
