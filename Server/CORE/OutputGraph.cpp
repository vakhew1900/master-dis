#include "OutputGraph.h"
#include <sstream>

int OutputVertex::number = 1;


string vectorToString(vector<int>& v) {
	string res = "(";

	for (int i = 0; i < v.size(); i++) {

		res += to_string(v[i]);
		if (i != v.size() - 1) {
			res += ", ";
		}
	}

	res += ")";

	return res;
}

OutputGraph::OutputGraph(map<int, OutputVertex> vertices, set<pair<int, int>> edges)
{
	this->vertices = vertices;
	this->edges = edges;
}

OutputGraph::OutputGraph(Repository repository, Graph graph, LabelsGenerator labelGenerator)
{
	map<int, OutputVertex> vertices;
	this->edges = repository.edges;

	for (int vertexNumber : graph.GetVertexes()) {
		multiset<int> labelIds = graph.GetLabels(vertexNumber);
		vector<int> labels;

		for (int labelId : labelIds) {
			labels.push_back(labelGenerator.GetLabelByID(labelId));
		}

		sort(labels.begin(), labels.end());

		for (auto commit : repository.commits) {
			if (commit.number == vertexNumber) {
				OutputVertex outputVertex(commit, labels);
				vertices[vertexNumber] = outputVertex;
				break;
			}
		}
	}

	this->vertices = vertices;
	this->edges = edges;
}

string OutputGraph::toDot(string name)
{
	string result = "graph graph_" + name + "{\n";

	for (auto vertex : vertices) {
		result += vertex.second.toDot(name) + "\n";
	}

	result += createEdgesDot(name, this->edges);

	result + "}\n";

	return result;
}


string OutputGraph:: createEdgesDot(string name, set<pair<int,int>>& edges) {

	vector < vector<pair<int, int>>> branches;

	map<int, int> vertexOnBranch; // ключ вершина - значение ветки
	map<int, int> childs;

	for (auto edge : edges) {
		if (!vertexOnBranch.count(edge.first)) {
			vertexOnBranch[edge.first] = branches.size();
			vertexOnBranch[edge.second] = branches.size();
			childs[edge.first]++;
			branches.push_back({ edge });
		}
		else if (!childs.count(edge.first)) {
			int branchNumber = vertexOnBranch[edge.first];
			vertexOnBranch[edge.second] = branchNumber;
			childs[edge.first]++;
			branches[branchNumber].push_back(edge);
		}
		else {
			int branchNumber = branches.size();
			vertexOnBranch[edge.second] = branchNumber;
			childs[edge.first]++;
			branches.push_back({ edge });
		}
	}

	string result = "";

	for (int i = 0; i < branches.size(); i++) {
		string tmp = name + to_string(branches[i][0].first);
		for (int j = 0; j < branches[i].size(); j++) {
			tmp += "->";
			tmp += name + to_string(branches[i][0].second);
		}
		tmp += ";\n";
		result += tmp;
	}
	return result;
}


string OutputVertex::toDot(string name)
{

	std::stringstream stringStream;

	stringStream << name << this->number
		<< " [label=\"" << "Hash" << this->hash << "| " << "labels =" << vectorToString(this->labels)
		<< "| message=" << this->message << "\"];";

	return stringStream.str();
}


