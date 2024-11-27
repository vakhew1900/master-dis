#pragma once
#include <iostream>
#include <vector>

using namespace std;

struct Commit {
	vector<vector<string>> diffs;
	string hash;
	string message;
	int number;
	int delta = 0; //TODO: branches
	string command_for_check_diff;

	Commit(string hash) {
		this->hash = hash;
	}

	Commit(string hash, vector<vector<string>> diffs) {
		this->hash = hash;
		this->diffs = diffs;
	}

	Commit() {

	}

	friend bool operator < (const Commit& a, const Commit& b) {
		return a.hash < b.hash;
	}
};
