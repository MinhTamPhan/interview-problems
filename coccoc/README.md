# CocCoc interview coding question

## Problem

We have 200Gb file filled with text lines divided by "\n". Our server has Linux on board, gcc, 8Gb
of RAM and unlimited hard disk space. Implement in Java/C++ the most efficient way to lexicographically
sort lines of this file from your point of view. Program should accept 3 parameters as command line
argument: input file name, output file name and memory limit in bytes (we will test with different memory
limits, not only 8Gb).

## My solution

My main idea base is [k-way merge algorithm](https://en.wikipedia.org/wiki/K-way_merge_algorithm):

![k-way merge algorithm](https://upload.wikimedia.org/wikipedia/commons/thumb/c/c0/Tournament_tree.png/1280px-Tournament_tree.png)

* step 1 (scan):
  - Scan input file, each time read n line fit memory limit call block(chuck).

  - Sort block by any algorithm.

  - Write block into K-file, block ith write into `i mod K` with format:
  ```
  n: number of line in this block
  ... n line
  ```
* step 2 (merge):

    - Read first line each block in K-file.

    - Find min, write file K-output, block ith write into `i mod K`.

    - Read 1 line in block contains min line, back to step find min and continue until block ith in K-file read all. DON'T READ LINE IN NEXT BLOCK IF CURRENT BLOCK INPROCESS

    - After once merge, block is sorted with size approximate `block size previous * K`

    - Continue until block size = input size

## My implementation

My implementation run failed if limit memory strict.

I assume limit memory mean each read file I don't read over limit memory. Memory alway available for heap with K line and k Integer if not throw `out of memory exception`

[c++ implement](./solution.cpp)

Build on `g++ (Ubuntu 4.8.5-4ubuntu8~16.04.1) 4.8.5`
Example run

```
g++ -std=c++11 ./solution.cpp -o sort.o
./sort.o ./Input_50 ./Out 160 5 1
```

[java implement](./ExternalSort.class)