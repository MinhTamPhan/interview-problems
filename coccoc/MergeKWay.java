
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author minhtam
 */
public class MergeKWay {

	void writeFile(String outfile, List<String> lines) {
		try {
			File fout = new File(outfile);
			FileOutputStream fos = new FileOutputStream(fout, true);

			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

			for (int i = 0; i < lines.size(); i++){
				bw.write(lines.get(i));
				bw.newLine();
			}
			bw.close();
			
			//close the file  
		} catch (IOException ex) {
			Logger.getLogger(MergeKWay.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	public class Tp {
		String val;
		int idx;
		Tp(String val, int idx) {
			this.val = val;
			this.idx = idx;
		}

		@Override
		public String toString() {
			return val;
		}
		
	}
	
	void sort(String inputPath) {
		int kWay = 3;
		Scanner sc, s1, s2, s3;
		try {
			String p1 = "./test/outLine";
			sc = new Scanner(new File(inputPath));
			int count = 0;
			while(sc.hasNextLine()) {
				List<String> lines = new ArrayList<>();
				for (int j = 0; j < kWay; j++) {
					if (sc.hasNextLine())
						lines.add(sc.nextLine());
				}
				lines.sort(Comparator.comparing(String::toString));
				writeFile(p1 + (count % 3), lines);
				count++;
			}
			sc.close();
			s1 = new Scanner(new File(p1 + 0));
			s2 = new Scanner(new File(p1 + 1));
			s3 = new Scanner(new File(p1 + 2));
			
			String p2 = "./test/out2t";
			for (int i = 0; i < 3; i++) {
				int is1 = 1, is2 = 1, is3 = 1;
				PriorityQueue<Tp> pq = new PriorityQueue<>(Comparator.comparing(Tp::toString));
				pq.add(new Tp(s1.nextLine(), 0));
				pq.add(new Tp(s2.nextLine(), 1));
				pq.add(new Tp(s3.nextLine(), 2));
				while(!pq.isEmpty()) {
					Tp poll = pq.poll();
					String val = poll.val;
					if (s1.hasNext() && poll.idx == 0 && is1 ++ < 3) pq.add(new Tp(s1.nextLine(), 0));
					if (s2.hasNext() && poll.idx == 1 && is2 ++ < 3) pq.add(new Tp(s2.nextLine(), 1));
					if (s3.hasNext() && poll.idx == 2 && is3 ++ < 3) pq.add(new Tp(s3.nextLine(), 2));
					writeFile( p2 + i, Arrays.asList(val));
				}
			}
			s1.close();
			s2.close();
			s3.close();
			s1 = new Scanner(new File(p2 + 0));
			s2 = new Scanner(new File(p2 + 1));
			s3 = new Scanner(new File(p2 + 2));
			PriorityQueue<Tp> pq = new PriorityQueue<>(Comparator.comparing(Tp::toString));
			pq.add(new Tp(s1.nextLine(), 0));
			pq.add(new Tp(s2.nextLine(), 1));
			pq.add(new Tp(s3.nextLine(), 2));
			while(!pq.isEmpty()) {
				Tp poll = pq.poll();
				String val = poll.val;
				if (poll.idx == 0 && s1.hasNext()) pq.add(new Tp(s1.nextLine(), 0));
				if (poll.idx == 1 && s2.hasNext()) pq.add(new Tp(s2.nextLine(), 1));
				if (poll.idx == 2 && s3.hasNext()) pq.add(new Tp(s3.nextLine(), 2));
				writeFile( "./test/out", Arrays.asList(val));
			}
			s1.close();
			s2.close();
			s3.close();
		} catch (FileNotFoundException ex) {
			System.out.println(ex.getMessage());
		}
	}
	
	public static void main(String[] args) {
		MergeKWay mergeKWay = new MergeKWay();
		String in = "./test/Input_50";
//		mergeKWay.sortingInMem(in);
//		mergeKWay.sort(in);
		boolean checkCorrectness = mergeKWay.checkCorrectness("./test/Out");
		System.err.println("checkCorrectness " + (checkCorrectness ? "True" : "False"));
	}
	
	void sortingInMem(String inputPath) {
		List<String> lines = new ArrayList<>();
		Scanner sc;
		try {
			sc = new Scanner(new File(inputPath), StandardCharsets.US_ASCII.name());
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				lines.add(line);
			}
			lines.sort(Comparator.comparing(String::toString));
			sc.close();
		} catch (FileNotFoundException ex) {
			System.out.println(ex.getMessage());
		}
		
		try {
			File fout = new File("./test/correctOut");
			FileOutputStream fos = new FileOutputStream(fout, false);

			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

			for (int i = 0; i < lines.size(); i++){
				bw.write(lines.get(i));
				bw.newLine();
			}
//			bw.write(lines.get(lines.size() - 1));
			bw.close();
			

			System.out.println("Writing successful"); 
			//close the file  
		} catch (IOException ex) {
			Logger.getLogger(MergeKWay.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	boolean checkCorrectness(String outputPath) {
		Scanner correctOut, out;
		try {
			correctOut = new Scanner(new File("./test/correctOut"));
			out = new Scanner(new File(outputPath));
			int i = 0;
			while (correctOut.hasNextLine()) {
				i ++;
				if (!out.hasNextLine()) return false;
				String correctLine = correctOut.nextLine();
				String outLine = out.nextLine();
				if (!correctLine.equals(outLine)) {
					System.err.println("checkCorrectness false " + i);
					return false;
				}
			}
			correctOut.close();
			out.close();
		} catch (FileNotFoundException ex) {
			System.out.println(ex.getMessage());
			return false;
		}
		return true;
	}
}
