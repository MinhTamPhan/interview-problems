//
// Created by minhtam on 22/10/2020.
//
#include <bits/stdc++.h>
using namespace std;

uint64_t LIMIT_MEM = 40 * 4;
uint64_t K = 3;
typedef pair<string, int> TupleSI;
auto Comparator = [](TupleSI left, TupleSI right) {
    return left.first > right.first;
};

int scan(string input, uint64_t& fileSize) {
    int count = 0;
    uint64_t blockSize = 0;
    ifstream inputStream(input, ios::in);
    vector<string> lines;
    // main idea: loop all line in file, split into block fit limit mem,
    // write blocks into k file with step k block
    if (inputStream.is_open()) {
        vector<ofstream> outStreams(K);
        // init k outStreams
        for (int i = 0; i < K; ++i) {
            string file("../tmp");
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
        // close out stream
        for (int i = 0; i < outStreams.size(); ++i)
            outStreams[i].close();
    } else {
        cout << "file: " << input << " not found\n";
        return -1;
    }
    // close in stream
    inputStream.close();
    return 0;
}

void mergeRun(ifstream* inStreams, ofstream* outStreams, int numOutStream = 1){
    int numEmptyScan = 0, fileCount = 0;
    uint64_t count[numOutStream];
    priority_queue<TupleSI, vector<TupleSI>, decltype(Comparator)> pq(Comparator);
    while (numEmptyScan < numOutStream) {
        int totalLine = 0;
        for (int i = 0; i < K; i++) {
            string line;
            getline(inStreams[i], line);
            if (!line.empty()) {
                count[i] = atoll(line.c_str());
                totalLine += count[i];
                getline(inStreams[i], line);
                pq.push(make_pair(line, i));
            } else {
                numEmptyScan++;
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

void MergeKRun(uint64_t fileSize, string output) {
    string src = "../tmp", dest = "../swap";
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

int main(int argc, char ** argv) {
    uint64_t fileSize = 0;
    scan("../Input", fileSize);
    cout << "fileSize " << fileSize << endl;
    MergeKRun(fileSize, "../Output");
    return 0;
}