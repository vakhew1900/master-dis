#pragma once
#include "Commit.h"
#include "Repository.h"
#include <string>
#include <vector>
#include <map>
#include "Graph.h"

using namespace std;

struct OutputVertex : public Commit
{
	static int number;
	vector<int> labels;

	OutputVertex(string hash, vector<vector<string>> diffs, int number, string message, vector<int> labels) {
		this->hash = hash;
		this->diffs = diffs;
		this->number = number;
		this->message = message;
		this->labels = labels;
	}

	OutputVertex(Commit commit, vector<int> labels) {
		this->hash = commit.hash;
		this->diffs = commit.diffs;
		this->number = commit.number;
		this->message = commit.message;
		this->labels = labels;
	}

	OutputVertex() {

	}
	string toDot(string name);
};


class OutputGraph
{
	map<int, OutputVertex> vertices;

	set<pair<int, int>> edges;
	string createEdgesDot(string name, set<pair<int, int>>& edges);
public:
	OutputGraph(map<int, OutputVertex> vertices, set<pair<int, int>> edges);

	OutputGraph(Repository repository, Graph graph, LabelsGenerator labelGenerator);

	string toDot(string name);
};

