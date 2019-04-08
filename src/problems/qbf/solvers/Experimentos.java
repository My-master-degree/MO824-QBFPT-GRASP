package problems.qbf.solvers;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import problems.qbf.solvers.*;
import solutions.Solution;

public class Experimentos {
	
	
	
	public static void main(String[] args) throws IOException {
		
		new File("resultado.txt").createNewFile();
		File resultados  = new File("resultado.txt");
		FileOutputStream fos;
		fos = new FileOutputStream(resultados);	
		String[] intancesWords = new String[]{"020", "040", "060", "080", "100", "200", "400"};
		String r = "";
		
		//GRASP-QBF
		for (int i = 0; i < intancesWords.length; i++) {
			
			long startTime = System.currentTimeMillis();
			GRASP_QBF grasp = new GRASP_QBF(0.05, 1000, "instances/qbf"+intancesWords[i]);
			Solution<Integer> bestSol = grasp.solve();
			r += " qbf"+intancesWords[i] ;
			r += " maxVal = "+bestSol.cost.toString();
			
			System.out.println("R: "+r);
			System.out.println("maxVal = " + bestSol);
			long endTime   = System.currentTimeMillis();
			long totalTime = endTime - startTime;
			r+= " Time = "+(double)totalTime/(double)1000+" seg";
			System.out.println("Time = "+(double)totalTime/(double)1000+" seg");				
			
			r+= "\n";
	
		}
		
		for (int i = 0; i < intancesWords.length; i++) {
			
			long startTime = System.currentTimeMillis();
			Pop_GRASP_QBF grasp = new Pop_GRASP_QBF(0.05, 1000, "instances/qbf"+intancesWords[i]);
			Solution<Integer> bestSol = grasp.solve();
			r += " qbf"+intancesWords[i] ;
			r += " maxVal = "+bestSol.cost.toString();
			
			System.out.println("R: "+r);
			System.out.println("maxVal = " + bestSol);
			long endTime   = System.currentTimeMillis();
			long totalTime = endTime - startTime;
			r+= " Time = "+(double)totalTime/(double)1000+" seg";
			System.out.println("Time = "+(double)totalTime/(double)1000+" seg");				
			
			r+= "\n";
	
		}
			
		fos.write(r.getBytes());
		fos.close();
		
	}

}
