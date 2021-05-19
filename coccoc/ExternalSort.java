import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Scanner;

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

    private long limitMem;
    private File input;
    private File output;
    private int k = 3;
    private String srcFile = "./tmp";
    private String destFile = "./swap";

    public ExternalSort(String inputPath, String outputPath, long limitMem, int k) {
        this.limitMem = limitMem;
        this.input = new File(inputPath);
        this.output = new File(outputPath);
        this.k = k;
    }

    void scan() {
        int count = 0;
        try {
            Scanner sc = new Scanner(input);
            List<PrintWriter> writers = new ArrayList<>();
            for (int i = 0; i < k; i++) {
                OutputStreamWriter fout = new OutputStreamWriter(
                        new FileOutputStream(
                                new File(srcFile + i), false));
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
        PriorityQueue<Tuple> pq = new PriorityQueue<>(Comparator.comparing(Tuple::toString));
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
                    Tuple poll = pq.poll();
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

    void MergeKRun() {
        long totalByteSorted = limitMem;
        List<Scanner> scaners = new ArrayList<>();
        List<PrintWriter> writers = new ArrayList<>();
        try {
            while (totalByteSorted < input.length() / k) {
                for (int i = 0; i < k; i++) {
                    scaners.add(new Scanner(new File(srcFile + i)));
                    OutputStreamWriter fout = new OutputStreamWriter(
                            new FileOutputStream(
                                    new File(destFile + i), false));
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

    void sortingInMem(String inputPath, String outputPath) {
        List<String> lines = new ArrayList<>();
        Scanner sc;
        try {
            sc = new Scanner(new File(inputPath));
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
            OutputStreamWriter outStream = new OutputStreamWriter(
                    new FileOutputStream(new File(outputPath), false));
            PrintWriter printWriter = new PrintWriter(outStream);
            for (int i = 0; i < lines.size(); i++){
                printWriter.println(lines.get(i));
            }
            printWriter.close();
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    boolean checkCorrectness(String outputPath) {
        Scanner correctOut, out;
        try {
            correctOut = new Scanner(new File("./correctOut"));
            out = new Scanner(new File(outputPath));
            int i = 0;
            while (correctOut.hasNextLine()) {
                i ++;
                if (!out.hasNextLine()) return false;
                String correctLine = correctOut.nextLine();
                String outLine = out.nextLine();
                if (!correctLine.equals(outLine)) {
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

    public static void main(String[] args) {
        if (args.length < 3) {
            System.err.println(String.format("Usge: [input] [output] [limit mem] [optional K way] [optional 0 not check output: 1 check output]"));
            System.exit(-1);
        }
        long limitMem = Long.valueOf(args[2]);
        int k = Integer.valueOf(args[3]);
        k = (k <= 3) ? 3 : k;
        ExternalSort externalSort = new ExternalSort(args[0], args[1], limitMem, k);
        if (limitMem > externalSort.input.length()) {
            externalSort.scan();
            externalSort.MergeKRun();
        } else {
            externalSort.sortingInMem(externalSort.input.getPath(), externalSort.output.getPath());
        }
        if (args.length == 5 && args[4].equals("1")) {
            externalSort.sortingInMem(externalSort.input.getPath(), "./correctOut");
            boolean isCorrect = externalSort.checkCorrectness(externalSort.output.getPath());
            System.out.println(isCorrect ? "CORRECT :)" : "INCORRECT :(");
        }
    }
}