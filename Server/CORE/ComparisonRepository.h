#pragma once
#include <map>
#include <tuple>
#include <vector>
#include "Repository.h"
#include "Commit.h"

struct ComparisonRepository: public Repository {
public:
	enum State {
		NeedDelete,
		NeedAdd,
		NeedNothing,
	};
	vector<pair<int, State>> commit_number_to_state; 
	//это кажется не нужно, надо тупо размножить коммиты, но да ладно
	//map<int, State> commit_state;
	//map<pair<int, int>, State> edge_state;
};
