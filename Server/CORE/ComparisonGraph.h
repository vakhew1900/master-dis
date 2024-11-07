#pragma once
#include "Graph.h"

using namespace std;

class ComparisonGraph: public Graph
{
public:
	ComparisonGraph() {};
	ComparisonGraph(const Graph& expected, const Graph& actual, const Graph& greater_common_subragh);
	unordered_map<int, pair<int, int>> vertex_to_expected_and_actual;
};

