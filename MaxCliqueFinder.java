

import java.util.ArrayList;
import java.util.Collection;
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
    
    
    private List<List<Integer> > adjacentVertices;
    
    private int stepsNum = 0;
    
    public MaxCliqueFinder(Graph graph) {
        this.graph = graph;
    }
    
    public int getLastFindStepsNum() {
        return stepsNum;
    }
    
    public Set<Integer> findBasic() {
        return find((R, No) -> {
            maxClique(R, No);
        });
    }
    
    public Set<Integer> findWithColorSort() {
        return find((R, No) -> {
            maxCliqueWithColorSort(R, No);
        });
    }
    
    public Set<Integer> findByMCD(double limit) {
        return find((R, No) -> {
            maxCliqueDyn(R, No, new HashMap<>(), new HashMap<>(), 1, limit);
        });
    }
    
    private Set<Integer> find(FindHandler handler) {
        stepsNum = 0;
        Q.clear();
        Qmax.clear();
        List<Integer> RList =  new ArrayList<>(graph.getSize());
        List<Integer> degrees = new ArrayList<>(graph.getSize());
        
        for (int i = 1; i <= graph.getSize(); ++i) {
            RList.add(i);
            degrees.add(getDegree(i));
        }
        
        
        RList.sort((first, second) -> {
            return degrees.get(second - 1) - degrees.get(first - 1);
        });
        
        Map<Integer, Integer> R = new HashMap<>(graph.getSize());
        
        for (int i = 1; i <= RList.size(); ++i) {
            R.put(i, RList.get(i - 1));
        }
        
        int maxDegree = Collections.max(degrees);
        
        Map<Integer, Integer> No = new HashMap<>(graph.getSize());
        
        for (int i = 1; i <= maxDegree; ++i) {
            No.put(i, i + 1); 
        }
        
        for (int i = maxDegree + 1; i <= graph.getSize(); ++i) {
            No.put(i, maxDegree + 1); 
        }
        
        createAdjacentVertices();
        
        handler.process(R, No);
        
        return Qmax;
    }
    
    private int getDegree(int vertexIndex) {
        return IntStream.of(graph.getAdjacencyMatrix()[vertexIndex - 1]).sum();
    }
    
    private int getDegree(int vertexIndex, Map<Integer, Integer> R) {
        int degreeSum = 0;
        int[] array = graph.getAdjacencyMatrix()[vertexIndex - 1];
        
        for (int i = 0; i < array.length; ++i) {
            if (!R.containsValue(i + 1)) {
                continue;
            }
            
            degreeSum += array[i];
        }
        
        return degreeSum;
    }
    
    private void maxClique(Map<Integer, Integer> R, Map<Integer, Integer> No) {
        ++stepsNum;
        while (!R.isEmpty()) {
            
            int vertexIndex = getVertexIndexWithMaxNo(R, No);
            int vertex = R.remove(vertexIndex);
            
            if (Q.size() + No.get(vertexIndex) > Qmax.size()) {
                Q.add(vertex);
                Map<Integer, Integer> intersection = createIntersection(R, adjacentVertices.get(vertex - 1));
                
                if (!intersection.isEmpty()) {
                    Map<Integer, Integer> newNo = new HashMap<>(intersection.size());
                    makeNumberSort(intersection, newNo);
                    maxClique(intersection, newNo);
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
    
    private void maxCliqueWithColorSort(Map<Integer, Integer> R, Map<Integer, Integer> No) {
        ++stepsNum;
        while (!R.isEmpty()) {
            int vertexIndex = getVertexIndexWithMaxNo(R, No);
            int vertex = R.remove(vertexIndex);
            
            if (Q.size() + No.get(vertexIndex) > Qmax.size()) {
                Q.add(vertex);
                Map<Integer, Integer> intersection = createIntersection(R, adjacentVertices.get(vertex - 1));
                
                if (!intersection.isEmpty()) {
                    Map<Integer, Integer> newNo = new HashMap<>(intersection.size());
                    makeColorSort(intersection, newNo);
                    maxCliqueWithColorSort(intersection, newNo);
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
    
    private void maxCliqueDyn(
            Map<Integer, Integer> R, 
            Map<Integer, Integer> No,
            Map<Integer, Integer> S,
            Map<Integer, Integer> oldS,
            int level,
            double limit) {
        S.put(level, S.getOrDefault(level, 0) + S.getOrDefault(level - 1, 0) - oldS.getOrDefault(level, 0));
        oldS.put(level, S.getOrDefault(level - 1, 0));
        
        Map<Integer, Integer> degrees = new HashMap<>();
        List<Integer> fakeR = new ArrayList<>();
        while (!R.isEmpty()) {
            int vertexIndex = getVertexIndexWithMaxNo(R, No);
            int vertex = R.remove(vertexIndex);
            
            if (Q.size() + No.get(vertexIndex) > Qmax.size()) {
                Q.add(vertex);
                Map<Integer, Integer> intersection = createIntersection(R, adjacentVertices.get(vertex - 1));
                
                if (!intersection.isEmpty()) {
                    if ((double)S.getOrDefault(level, 0)/stepsNum < limit) {
                        degrees.clear();
                        fakeR.clear();
                        for (Map.Entry<Integer, Integer> entry : intersection.entrySet()) {
                            degrees.put(entry.getValue(), getDegree(entry.getValue(), intersection));
                            fakeR.add(entry.getValue());
                        }
                        
                        Collections.sort(fakeR, (first, second) -> {
                            return degrees.get(second) - degrees.get(first);
                        });
                        
                        for (int i = 0; i < intersection.size(); ++i) {
                            intersection.replace(i + 1, fakeR.get(i));
                        }
                    }
                    Map<Integer, Integer> newNo = new HashMap<>(intersection.size());
                    makeColorSort(intersection, newNo);
                    S.put(level, S.getOrDefault(level, 0) + 1);
                    ++stepsNum;
                    maxCliqueWithColorSort(intersection, newNo);
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
    
    public int getVertexIndexWithMaxNo(Map<Integer, Integer> R, Map<Integer, Integer> No) {
        int vertexIndex = 0;
        Map.Entry<Integer, Integer> maxEntry = null;
	for (Map.Entry<Integer, Integer> entry : No.entrySet())
        {
            if (!R.containsKey(entry.getKey())) {
                continue;
            }
            
            if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0) {
                maxEntry = entry;
            }
        }
        
        if (maxEntry != null) {
            vertexIndex = maxEntry.getKey();
        }
        return vertexIndex;
    }
    
    public Map<Integer, Integer> createIntersection(Map<Integer, Integer> first, List<Integer> second) {
        Map<Integer, Integer> intersection = new HashMap<>();
        return createIntersection(first, second, intersection);
    }
    
    public Map<Integer, Integer> createIntersection(Map<Integer, Integer> first, List<Integer> second, Map<Integer, Integer> intersection) {
        int i = 1;
       
        for (Map.Entry<Integer, Integer> entry : first.entrySet())
            if (second.contains(entry.getValue())) {
                intersection.put(i, entry.getValue());
                ++i;
            }
            

        return intersection;
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
    
    public void makeNumberSort(Map<Integer, Integer> R, Map<Integer, Integer> No) {
        if (R == null || No == null) {
            return;
        }
        
        Map<Integer, Integer> fakeNo = new HashMap<>();
        
        int maxNo = 0;
        Map<Integer, List<Integer> > C = new HashMap<>();
        
        List<Integer> intersection = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : R.entrySet()) {
            int vertex = entry.getValue();
            int k = 1;
            
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
            
            fakeNo.put(vertex, k);
            Ck.add(vertex);
        }
        
        int i = 1;
        for (Map.Entry<Integer, List<Integer>> entry : C.entrySet()) {
            for (int j = 0; j < entry.getValue().size(); ++j) {
                int vertex = entry.getValue().get(j);
                R.put(i, vertex);
                No.put(i, fakeNo.get(vertex));
                ++i;
            }
        }
    }
    
    public void makeColorSort(Map<Integer, Integer> R, Map<Integer, Integer> No) {
        if (R == null || No == null) {
            return;
        }
        
        int maxNo = 0;
        int minK = Qmax.size() - Q.size() + 1;
        if (minK <= 0) minK = 1;
        
        int j = 1;
        
        Map<Integer, List<Integer> > C = new HashMap<>();
        
        List<Integer> intersection = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : R.entrySet()) {
            int vertex = entry.getValue();
            int k = 1;
            
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
            
            Ck.add(vertex);
            
            if (k < minK) {
                R.replace(j, vertex);
                ++j;
            }
        }
        
        No.put(j - 1, 0);
        
        for (int k = minK; k <= maxNo; ++k) {
            List<Integer> Ck = C.get(k);
            for (int g = 0; g < Ck.size(); ++g) {
                R.put(j, Ck.get(g));
                No.put(j, k);
                ++j;
            }
        }
    }
    
    private interface FindHandler {
        public void process(Map<Integer, Integer> R, Map<Integer, Integer> No);
    }
}
