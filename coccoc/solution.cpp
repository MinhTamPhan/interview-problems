//
// Created by minhtam.
//
#include "bits/stdc++.h"
using namespace std;

uint64_t LIMIT_MEM = 40 * 4;
int K = 3;

typedef pair<string, int> TupleSI;

auto Comparator = [](TupleSI left, TupleSI right) {
    return left.first > right.first;
};

int scan(string input, uint64_t& fileSize) {
    int count = 0;
    uint64_t blockSize = 0;
    ifstream inputStream(input, ios::in);
    vector<string> lines;
    if (inputStream.is_open()) {
        vector<ofstream> outStreams(K);
        for (int i = 0; i < K; ++i) {
            string file("./tmp");
            file += to_string(i);
            outStreams[i].open(file, ios::out);
        }
        string line;
        while (!inputStream.eof()) {
            getline(inputStream, line);
            int length = line.length();
            if (blockSize + length < LIMIT_MEM) {
                lines.push_back(line);
                blockSize += length;
            } else {
                fileSize += blockSize;
                sort(lines.begin(), lines.end());
                count %= K; // avoid overflow if num line '>>' MAX_INT
                outStreams[count] << lines.size() << endl;
                for (auto &l : lines)
                    outStreams[count] << l << endl;
                lines.resize(1);
                lines[0] = line;
                blockSize = length;
                count++;
            }
        }
        fileSize += blockSize;
        sort(lines.begin(), lines.end());
        outStreams[count % K] << lines.size() << endl;
        for (auto &l : lines)
            outStreams[count % K] << l << endl;
        for (int i = 0; i < outStreams.size(); ++i)
            outStreams[i].close();
    } else {
        cout << "file: " << input << " not found\n";
        return -1;
    }
    inputStream.close();
    return 0;
}

void mergeRun(ifstream* inStreams, ofstream* outStreams, int numOutStream = 1){
    int numEmptyScan = 0, fileCount = 0;
    int64_t count[K];
    priority_queue<TupleSI, vector<TupleSI>, decltype(Comparator)> pq(Comparator);
    while (numEmptyScan < numOutStream) {
        int totalLine = 0;
        for (int i = 0; i < K; i++) {
			if (!inStreams[i].eof()){
				string line;
				getline(inStreams[i], line);
				if (!line.empty()) {
					count[i] = atoll(line.c_str());
					totalLine += count[i];
					getline(inStreams[i], line);
					pq.push(make_pair(line, i));
				} else {
					numEmptyScan++;
					count[i] = 0;
				}
			}
        }
        fileCount %= numOutStream;
        if (totalLine > 0) {
            if (numOutStream != 1)
                outStreams[fileCount] << to_string(totalLine) << endl;

            while (!pq.empty()) {
                pair<string, int> val = pq.top(); pq.pop();
                if (!inStreams[val.second].eof() && --count[val.second] > 0) {
                    string line;
                    getline(inStreams[val.second], line);
                    pq.push(make_pair(line, val.second));
                }
                outStreams[fileCount] << val.first << endl;
            }
            fileCount++;
        }
    }
}

void mergeKRun(uint64_t fileSize, string output) {
    string src = "./tmp", dest = "./swap";
    long totalByteSorted = LIMIT_MEM;
    ifstream* inputStreams = new ifstream[K];
    ofstream* outStreams = new ofstream[K];
    while (totalByteSorted < fileSize / K) {
        for (int i = 0; i < K; ++i) {
            string inFile(src); inFile += to_string(i);
            string outFile(dest); outFile += to_string(i);
            inputStreams[i].open(inFile, ios::in);
            outStreams[i].open(outFile, ios::out);
        }
        mergeRun(inputStreams, outStreams, K);
        swap(src, dest);
        totalByteSorted *= K;
        for (int i = 0; i < K; i++) {
            inputStreams[i].close();
            outStreams[i].close();
        }
    }
    outStreams[0].open(output, ios::out);
    for (int i = 0; i < K; ++i) {
        string inFile(src); inFile += to_string(i);
        inputStreams[i].open(inFile, ios::in);
    }
    mergeRun(inputStreams, outStreams);
    outStreams[0].close();
    for (int i = 0; i < K; ++i)
        inputStreams[i].close();

    delete[] inputStreams;
    delete[] outStreams;
}

void sortInMem(string input, string output) {
	ifstream inputStream(input, ios::in);
    vector<string> lines;
	string line;

 	while (!inputStream.eof()) {
        getline(inputStream, line);
		lines.push_back(line);
 	}
	inputStream.close();
	sort(lines.begin(), lines.end());
	ofstream outStreams(output, ios::out);
	for (auto &line: lines) {
		outStreams << line << endl;
	}
	outStreams.close();
}

bool checkResult(string correctOutput, string output) {
	ifstream correctStream(correctOutput, ios::in);
	ifstream outputStream(output, ios::in);
	string correctLine, outputLine;
	while (!correctStream.eof()) {
		getline(correctStream, correctLine);
		getline(outputStream, outputLine);
		if (correctLine != outputLine) {
			correctStream.close();
			outputStream.close();
			return false;
		}
	}
	correctStream.close();
	outputStream.close();
	return true;
}

int main(int argc, char ** argv) {
	if (argc < 4) {
		cout << "Usge: " << argv[0] << " [input] [output] [limit mem] [optional K way] [optional 0 not check output: 1 check output]" << endl;
		return -1;
	}
	if (argc > 5) K = atoi(argv[4]);
	int check = 0;
	if (argc == 6) check = atoi(argv[5]);
	LIMIT_MEM = atoll(argv[3]);
    uint64_t fileSize = 0;
	string input(argv[1]), output(argv[2]);
    int err = scan(input, fileSize);
	if (err != 0) {
		cout << "input not found" << endl;
		return -1;
	}

	if (fileSize > LIMIT_MEM) mergeKRun(fileSize, output);
	else sortInMem(input, output);

	if (check != 0){
		string correctOutput("./correctOut");
		sortInMem(input, correctOutput);
		bool isCorrect = checkResult(correctOutput, output);
		cout << (isCorrect ? "CORRECT :)" : "INCORRECT :(") << endl;
	}
    return 0;
}