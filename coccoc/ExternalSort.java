import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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
public class ExternalSort {

	public static final String charsetName = StandardCharsets.US_ASCII.name();
	long limitMem;
	File input;
	File output;
	int k = 3;
	String srcFile = "./tmp";
	String destFile = "./swap";

	public ExternalSort(String inputPath, String outputPath, long limitMem) {
		this.limitMem = limitMem;
		this.input = new File(inputPath);
		this.output = new File(outputPath);
		System.err.println(input.length());
	}

	// scan input, split file into run leng < limitMem
	void scan() {
		int count = 0;

		try {
			Scanner sc = new Scanner(input, charsetName);
			List<PrintWriter> writers = new ArrayList<>();
			for (int i = 0; i < k; i++) {
				OutputStreamWriter fout = new OutputStreamWriter(
						new FileOutputStream(
								new File(srcFile + i), false),
						 charsetName);
				writers.add(new PrintWriter(fout));
			}
			List<String> lines = new ArrayList<>();
			long totalByte = 0;
			String line = "";
			while (sc.hasNextLine()) {
				line = sc.nextLine();
				int length = line.length();
				if (totalByte + length < limitMem) {
					lines.add(line);
					totalByte += length;
				} else {
					lines.sort(Comparator.comparing(String::toString));
					count %= k;
					write(writers.get(count), lines);
					lines = new ArrayList<>();
					totalByte = length;
					lines.add(line);
					count++;
				}
			}
			if (totalByte < limitMem) {
				lines.sort(Comparator.comparing(String::toString));
				write(writers.get(count % k), lines);
			}

			sc.close();
			for (int i = 0; i < k; i++) {
				writers.get(i).close();
			}
		} catch (FileNotFoundException ex) {
			System.err.println(ex.getMessage());
		} catch (UnsupportedEncodingException ex) {
			Logger.getLogger(ExternalSort.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	private void write(PrintWriter printWriter, List<String> lines) {

		printWriter.println(lines.size());
		for (int i = 0; i < lines.size(); i++) {
			printWriter.println(lines.get(i));
		}

	}

	public class Tuple {

		String val;
		int idx;

		Tuple(String val, int idx) {
			this.val = val;
			this.idx = idx;
		}

		@Override
		public String toString() {
			return val;
		}

	}

	void MergeRun(List<Scanner> scaners, List<PrintWriter> writers) {
		int numEmptyScan = 0, fileCount = 0;
		int count[] = new int[k];
		PriorityQueue<Tuple> pq = new PriorityQueue<>(Comparator.comparing(Tp::toString));
		while (numEmptyScan < k) {
			int totalLine = 0;
			for (int i = 0; i < k; i++) {
				Scanner scanner = scaners.get(i);
				if (scanner.hasNext()) {
					String nextLine = scanner.nextLine();
					count[i] = Integer.valueOf(nextLine);
					totalLine += count[i];
					pq.add(new Tuple(scanner.nextLine(), i));
				} else {
					numEmptyScan++;
				}
			}
			fileCount %= k;
			if (totalLine > 0) {
				PrintWriter printWriter = writers.get(fileCount);
				if (writers.size() > 1)
					printWriter.println(totalLine + "");

				while (!pq.isEmpty()) {
					Tp poll = pq.poll();
					String val = poll.val;
					Scanner runScan = scaners.get(poll.idx);
					if (runScan.hasNext() && --count[poll.idx] > 0) {
						pq.add(new Tuple(runScan.nextLine(), poll.idx));
					}
					printWriter.println(val);
				}
				fileCount++;
			}
		}

	}

//	mergesort

	void MergeKRun() {
		long totalByteSorted = limitMem;
		List<Scanner> scaners = new ArrayList<>();
		List<PrintWriter> writers = new ArrayList<>();
		try {
			while (totalByteSorted < input.length() / k) {
				for (int i = 0; i < k; i++) {
					scaners.add(new Scanner(new File(srcFile + i), charsetName));
					OutputStreamWriter fout = new OutputStreamWriter(
							new FileOutputStream(
									new File(destFile + i), false),
							 charsetName);
					writers.add(new PrintWriter(fout));
				}
				MergeRun(scaners, writers);
				// swap src and dest
				String tmp = srcFile;
				srcFile = destFile;
				destFile = tmp;
				totalByteSorted *= k;

				for (int i = 0; i < k; i++) {
					scaners.get(i).close();
					writers.get(i).close();
				}
				scaners = new ArrayList<>();
				writers = new ArrayList<>();
			}
			for (int i = 0; i < k; i++) {
				scaners.add(new Scanner(new File(srcFile + i)));
			}

			FileOutputStream fout = new FileOutputStream(output, false);
			writers.add(new PrintWriter(fout));
			MergeRun(scaners, writers);
			writers.get(0).close();

		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public static void main(String[] args) {
		ExternalSort externalSort = new ExternalSort("./Input", "./Out", 40 * 4);
		externalSort.scan();
		externalSort.MergeKRun();
	}
}