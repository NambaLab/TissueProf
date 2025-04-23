package Util;
import java.util.ArrayList;
import java.util.List;


public class Combinations {

	
	public int Combination(int n , int r) {
	    if (r < 0 || r > n || n >128) return 0;  // Invalid cases
		    int result = 1;
		    for (int i = 1; i <= r; i++) {
		        result = result * (n - r + i) / i;
		    }
	    return result;
	}

	
	public class ChannelCombinations{
	
	   public List<List<List<Integer>>> generateCombinations(int n, int maxR) {
	        List<List<List<Integer>>> result = new ArrayList<>();
	        
	        // Generate combinations for each r from maxR down to 2
	        for (int r = maxR; r >= 1; r--) {
	            List<List<Integer>> combinationsForR = new ArrayList<>();
	            if (r <= n) {  // Only generate if r <= n
	                backtrack(combinationsForR, new ArrayList<>(), 0, n, r);
	            }
	            result.add(combinationsForR);
	        }
	        
	        return result;
	    }
	    
	    private void backtrack(List<List<Integer>> result, 
	                                 List<Integer> current, 
	                                 int start, 
	                                 int n, 
	                                 int r) {
	        if (current.size() == r) {
	            result.add(new ArrayList<>(current));
	            return;
	        }
	        
	        for (int i = start; i < n; i++) {
	            current.add(i);
	            backtrack(result, current, i + 1, n, r);
	            current.remove(current.size() - 1);
	        }
	    }
	}
		
	
		
		
		
		
}
		
		
		
			
			
			
	
			




