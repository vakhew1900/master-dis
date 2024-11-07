#include "ComparisonGraph.h"

ComparisonGraph::ComparisonGraph(const Graph& expected, const Graph& actual, const Graph& greater_common_subragh) {
	//greater_common_subragh.vertexes.find()
	//todo: тут пока без мержей, так как мержи все сломают нахрен
	//todo: пока есть сложности связанные с коммитом с ошибкой поэтому пока любой коммит, у которого хотя бы одна метка гуд будет норм
	unordered_set<int> unvisited_vertexes;
	unordered_map<int, int> vertex_to_gcs_vertex;
	unordered_map<int, int> vertex_to_prev;
	unordered_map<int, string> vertex_to_graph; //todo enum
	unordered_map<int, int> vertex_to_count_next_visited;
	unordered_map<int, int> vertex_to_branch;
	//вершина здесь имеет номер актуальный для неё только в графе результирующем

	int cur_vertex = 0;
	int cur_branch = 0;

	//добавить вершины из 0 в корень
	cur_vertex++;
	unvisited_vertexes.insert(cur_vertex);
	vertex_to_gcs_vertex[cur_vertex] = expected.Root();
	vertex_to_prev[cur_vertex] = 0;
	vertex_to_graph[cur_vertex] = "expected";
	cur_vertex++;
	unvisited_vertexes.insert(cur_vertex);
	vertex_to_gcs_vertex[cur_vertex] = actual.Root();
	vertex_to_prev[cur_vertex] = 0;
	vertex_to_graph[cur_vertex] = "actual";

	ComparisonGraph result;

	while (!unvisited_vertexes.empty()) {
		unordered_map<int, vector<int>> unvisited_vertexes_in_gcs;
		for (auto elem : unvisited_vertexes) {
			unvisited_vertexes_in_gcs[vertex_to_gcs_vertex[elem]].push_back(elem);
		}
		bool fnd = 0;
		for (auto elem : unvisited_vertexes_in_gcs) {
			if (elem.second.size() == 2) {
				int vertex_number = elem.second[0];
				if (vertex_to_prev[vertex_number] != 0)
				result.AddEdge(vertex_to_prev[vertex_number], vertex_number);
				result.vertex_to_expected_and_actual[vertex_number] = {elem.first, elem.first};
				if (vertex_to_count_next_visited[vertex_to_prev[vertex_number]] == 0) {
					vertex_to_branch[vertex_number] = vertex_to_branch[vertex_to_prev[vertex_number]];
				}
				else {
					vertex_to_branch[vertex_number] = ++cur_branch;
				}
				vertex_to_count_next_visited[vertex_to_prev[vertex_number]]++;

				for (auto next : actual.GetTails(elem.first)) {
					cur_vertex++;
					unvisited_vertexes.insert(cur_vertex);
					vertex_to_gcs_vertex[cur_vertex] = next;
					vertex_to_prev[cur_vertex] = elem.first;
					vertex_to_graph[cur_vertex] = "actual";
				}

				for (auto next : expected.GetTails(elem.first)) {
					cur_vertex++;
					unvisited_vertexes.insert(cur_vertex);
					vertex_to_gcs_vertex[cur_vertex] = next;
					vertex_to_prev[cur_vertex] = elem.first;
					vertex_to_graph[cur_vertex] = "expected";
				}

				unvisited_vertexes.erase(elem.second[0]);
				unvisited_vertexes.erase(elem.second[1]);
				fnd = 1;
				break;
			}
		}
		if (fnd) {
			continue;
		}
		for (auto elem : unvisited_vertexes_in_gcs) {
			if (!greater_common_subragh.GetVertexes().count(elem.first)) {
				int vertex_number = elem.second[0];
				if (vertex_to_prev[vertex_number] != 0)
				result.AddEdge(vertex_to_prev[vertex_number], vertex_number);
				if (vertex_to_graph[vertex_number] == "expected") {
					result.vertex_to_expected_and_actual[vertex_number] = { elem.first, -1 };
				}
				else {
					result.vertex_to_expected_and_actual[vertex_number] = { -1, elem.first };
				}
				if (vertex_to_count_next_visited[vertex_to_prev[vertex_number]] == 0) {
					vertex_to_branch[vertex_number] = vertex_to_branch[vertex_to_prev[vertex_number]];
				}
				else {
					vertex_to_branch[vertex_number] = ++cur_branch;
				}
				vertex_to_count_next_visited[vertex_to_prev[vertex_number]]++;

				if (vertex_to_graph[vertex_number] == "expected") {
					for (auto next : expected.GetTails(elem.first)) {
						cur_vertex++;
						unvisited_vertexes.insert(cur_vertex);
						vertex_to_gcs_vertex[cur_vertex] = next;
						vertex_to_prev[cur_vertex] = elem.first;
						vertex_to_graph[cur_vertex] = "expected";
					}
				}
				else {
					for (auto next : actual.GetTails(elem.first)) {
						cur_vertex++;
						unvisited_vertexes.insert(cur_vertex);
						vertex_to_gcs_vertex[cur_vertex] = next;
						vertex_to_prev[cur_vertex] = elem.first;
						vertex_to_graph[cur_vertex] = "actual";
					}
				}

				unvisited_vertexes.erase(elem.second[0]);
				int f = 7;
				break;
			}
		}
	}

	*this = result;
}