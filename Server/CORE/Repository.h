#pragma once
#include <iostream>
#include <vector>
#include <set>
#include "Commit.h"

using namespace std;

class Repository{
public:
	set<Commit> commits;

	const Commit& GetCommitByNumber(int number){
		for (auto& elem : commits) {
			if (elem.number == number) {
				return elem;
			}
		}
	}
	
	set<pair<int, int>> edges;
};
