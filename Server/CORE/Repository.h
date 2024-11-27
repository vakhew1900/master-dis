#pragma once
#include <iostream>
#include <vector>
#include <set>
#include "Commit.h"

using namespace std;

class Repository{
public:

	set<Commit> commits;

	Repository() {

	}

	Repository(set<Commit> commits, set<pair<int, int>> edges) {
		this->commits = commits;
		this->edges = edges;
	}

	const Commit& GetCommitByNumber(int number){
		for (auto& elem : commits) {
			if (elem.number == number) {
				return elem;
			}
		}
	}
	
	set<pair<int, int>> edges;
};
