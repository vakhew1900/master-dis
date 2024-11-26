#pragma once
#include "graph.h"
#include <queue>
#include <chrono>
#include <ctime>

void InsertIfNotFound(multiset<int>& st, int value) {
    if (st.count(value)) {
        return;
    }
    st.insert(value);
    return;
}

Graph::Graph() {
}

Graph::Graph(const vector<pair<int, int>>& list_of_edges) {
    unordered_map<int, multiset<int>> vertex_to_labels;

    for (const auto& edge : list_of_edges) {
        vertex_to_labels[edge.first] = { edge.first };
        vertex_to_labels[edge.second] = { edge.second };
    }

    const auto result = Graph(list_of_edges, vertex_to_labels);

    this->labels_list = result.labels_list;
    this->tails_list = result.tails_list;
    this->vertexes = result.vertexes;
}

Graph::Graph(const vector<pair<int, int>>& list_of_edges,
    const unordered_map<int, multiset<int>>& vertex_to_labels) {
    for (const auto& edge : list_of_edges) {
        AddEdge(edge.first, edge.second);
    }
    for (const auto& vertex_and_labels : vertex_to_labels) {
        for (const auto& label : vertex_and_labels.second) {
            AddLabel(vertex_and_labels.first, label);
        }
    }
}

Graph::Graph(const vector<pair<int, int>>& list_of_edges,
    const LabelsGenerator& labels_generator, const bool is_first) {
    for (const auto& edge : list_of_edges) {
        AddEdge(edge.first, edge.second);
    }
    this->labels_generator = labels_generator;
    if (is_first) { // возвращает список id в зависимости от того на каком какой этот сейчас граф
        for (const auto& vertex_and_labels : labels_generator.vertexex_to_labelsids.first) {
            for (const auto& label : vertex_and_labels.second) {
                AddLabel(vertex_and_labels.first, label);
            }
        }
    }
    else {
        for (const auto& vertex_and_labels : labels_generator.vertexex_to_labelsids.second) {
            for (const auto& label : vertex_and_labels.second) {
                AddLabel(vertex_and_labels.first, label);
            }
        }
    }
}

int Graph::GetNumberVertexes() const {
    return static_cast<int>(vertexes.size());
}

multiset<int> Graph::GetTails(const int vertex) const {
    if (tails_list.find(vertex) == tails_list.end()) {
        return multiset<int>();
    }
    return tails_list.at(vertex);
}

multiset<int> Graph::GetLabels(const int vertex) const {
    if (labels_list.find(vertex) == labels_list.end()) {
        return multiset<int>();
    }
    return labels_list.at(vertex);
}

multiset<int> Graph::GetVertexes() const {
    return vertexes;
}

multiset<int> Graph::AllLabels() const {
    multiset<int> result;
    for (const auto& vertex_and_label : labels_list) {
        for (const auto& label : vertex_and_label.second) {
            result.insert(label);
        }
    }
    return result;
}

int Graph::Root() const {
    //планируется, что граф имеет один рут, поэтому если их будет несколько будет cout с еррором и выход
    
    multiset<int> candidates = vertexes;

    for (const auto& vertex_and_tails: tails_list) {
        for (const auto& tail : vertex_and_tails.second) {
            candidates.erase(tail);
        }
    }

    if (candidates.size() != 1) {
        cout << "Graph has not one root";
        exit(1);
    }

    return *candidates.begin();
}

void Graph::AddEdge(const int from, const int to) {
    InsertIfNotFound(vertexes, from);
    InsertIfNotFound(vertexes, to);
    tails_list[from].insert(to);
}

void Graph::RemoveEdge(const int from, const int to) {
    tails_list[from].erase(to);
}

void Graph::AddLabel(const int vertex, const int label) {
    InsertIfNotFound(vertexes, vertex);
    labels_list[vertex].insert(label);
}

void Graph::RemoveLabel(const int vertex, const int label) {
    labels_list[vertex].erase(label);
}

unordered_map<int, bool>  Graph::ReachableArray(int from) const {
    unordered_map<int, bool> result;

    unordered_map<int, bool> visited;
    queue<int> q;
    q.push(from);

    while (!q.empty()) {
        const auto& cur_vertex = q.front();
        q.pop();
        result[cur_vertex] = true;

        for (const auto& tail : GetTails(cur_vertex)) {
            if (!visited[tail]) {
                visited[tail] = true;
                q.push(tail);
            }
        }
    }

    return result;
}

unordered_map<int, unordered_map<int, bool>> Graph::ReachableMatrix() const {
    unordered_map<int, unordered_map<int, bool>> result;

    for (const auto& from : vertexes) {
        result[from] = ReachableArray(from);
    }

    return result;
}

Graph Graph::Subgraph(const multiset<int>& labels) const {
    Graph subgraph = *this;
    //todo не очень круто, что здесь удаляемые лейблы, а не нормальные
   

    // удалить метки из мультимножетсва меток в сете пар метки вершины
    for (auto& vertex_to_label: subgraph.labels_list) { 
        for (auto label : labels) {
            vertex_to_label.second.erase(label);
        }
    }

    {
        //const auto& reachable_matrix = ReachableMatrix();

        multiset<int> vertexes_copy = subgraph.vertexes;

        for (auto vertex : vertexes_copy) {
            if (subgraph.labels_list[vertex].empty()) { // вершина не содержит больше меток (пустая)
                subgraph.labels_list.erase(vertex);
                subgraph.vertexes.erase(vertex);
                //todo надо соптимайзить эту штуку, но в будущем
                for (const auto& u: vertexes) {  // соединить ее родителей с ее предками
                    if (GetTails(u).count(vertex)) {
                        for (auto v : GetTails(vertex)){
                            if (subgraph.vertexes.count(u) && subgraph.vertexes.count(v))
                            subgraph.AddEdge(u, v);
                        }
                    }
                }
            }
        }
    }

    for (auto vertex: vertexes) {
        if (!subgraph.vertexes.count(vertex)) {  // подграф не содержит вершину
            subgraph.tails_list.erase(vertex); // удалить связь с родителями вершины
            for (const auto& u: vertexes) {
                if (subgraph.GetTails(u).count(vertex)) {
                    subgraph.RemoveEdge(u, vertex);
                    if (subgraph.tails_list[u].empty()) {
                        subgraph.tails_list.erase(u);
                    }
                }
            }
        }
    }

    return subgraph;
}

pair<multiset<int>, multiset<int>> Graph::EqualSubgraphs(const Graph& first_graph, const Graph& second_graph,
    multiset<int>& labels) {

    Graph first_subgraph = first_graph.Subgraph(labels);
    Graph second_subgraph = second_graph.Subgraph(labels);

    vector<VertexInformationWithTransformedLabels> first_array = first_subgraph.SortedVertexesInformationWithTransformedLabels();
    vector<VertexInformationWithTransformedLabels> second_array = second_subgraph.SortedVertexesInformationWithTransformedLabels();

    if (first_array == second_array) {
        return {first_subgraph.AllLabels(), second_subgraph.AllLabels()};
    }

    return { {}, {} };
}

//! Возвращает все норм лябэли
pair<multiset<int>, multiset<int>> Graph::BiggestCommonSubgraph(
    const Graph& first_graph, const Graph& second_graph) {

    auto start = std::chrono::system_clock::now();

    const auto first_graph_all_labels = first_graph.AllLabels();
    const auto second_graph_all_labels = second_graph.AllLabels();


    // создаем общий список всех меток
    multiset<int> all_labels;

    for (auto label : first_graph_all_labels) {
        all_labels.insert(label);
    }

    for (auto label : second_graph_all_labels) {
        all_labels.insert(label);
    }

    /*for (auto label : first_graph_all_labels) {
        cout << label << endl;
    }

    cout << endl;

    for (auto label : second_graph_all_labels) {
        cout << label << endl;
    }

    cout << endl;

    for (auto label : all_labels) {
        cout << label << endl;
    }*/

    vector<int> all_labels_vector(all_labels.begin(), all_labels.end());

    const int max_possible_count_error = all_labels.size();

   // проверяем различные комбинации 
    for (int count_error = 0; count_error < max_possible_count_error; ++count_error) { // идем по ходу увелечения возможных ошибок
        vector<multiset<int>> combinations;
        Combinations(max_possible_count_error, max_possible_count_error - count_error, combinations);

        for (auto& combination : combinations) {
            auto end = std::chrono::system_clock::now();
            std::chrono::duration<double> elapsed_seconds = end - start;
            if (elapsed_seconds.count() > 45) {
                //TODO заменить на выкидывание ошибки
                cout << "Программа не может найти ошибки, так как их слишком много\n";
                system("pause");
                exit(0);
            }
            //combination = { 1, 2, 4, 5 };
            multiset<int> current_labels; // список удаляемых заметок
            for (int i = 0; i < max_possible_count_error; ++i){
                if (!combination.count(i)) {
                    current_labels.insert(all_labels_vector[i]);
                }
            }
            
            if (EqualSubgraphs(first_graph, second_graph, current_labels) != make_pair(multiset<int>(), multiset<int>())) {
                return EqualSubgraphs(first_graph, second_graph, current_labels);
            }

        }
    }

    return make_pair(multiset<int>(), multiset<int>());
}


vector<Graph::VertexInformationWithTransformedLabels> Graph::SortedVertexesInformationWithTransformedLabels() const {
    vector<Graph::VertexInformationWithTransformedLabels> result;

    unordered_map<int, int> vertex_to_result_index;

    auto copy_labels_list = labels_list;

    for (auto& vertex_and_labels : copy_labels_list) {
        multiset<int> new_labels;
        for (auto elem: vertex_and_labels.second) {
            new_labels.insert(labels_generator.GetLabelByID(elem));
        }
        vertex_and_labels.second = new_labels;
    }

    for (const auto& vertex_and_labels : copy_labels_list) {
        Graph::VertexInformationWithTransformedLabels vertex_information;
        vertex_information.labels = vertex_and_labels.second;

        if (tails_list.find(vertex_and_labels.first) != tails_list.end()) {
            if (tails_list.find(vertex_and_labels.first) != tails_list.end())
            for (const auto& next : tails_list.at(vertex_and_labels.first)) {
                vertex_information.tails.push_back(copy_labels_list.at(next));
            }
        }

        result.push_back(vertex_information);

        vertex_to_result_index[vertex_and_labels.first] = result.size() - 1;
    }

    for (const auto& vertex : vertexes) {
        if (tails_list.find(vertex) != tails_list.end()) {
            for (const auto& next : tails_list.at(vertex)) {
                result[vertex_to_result_index[next]].heads.push_back(multiset<int>(copy_labels_list.at(vertex).begin(), copy_labels_list.at(vertex).end()));
            }
        }
    }

    sort(result.begin(), result.end());
  
    return result;
}

bool Graph::ContainsVertex(int vertex) const {
    return vertexes.count(vertex);
}

bool Graph::ComplexEqualGraphs(const Graph& first, const Graph& second) {
    return first.SortedVertexesInformationWithTransformedLabels() == second.SortedVertexesInformationWithTransformedLabels();
}


void Graph::Combinations(
    const int n, const int k, vector<multiset<int>>& list_combinations) {
    multiset<int> current_combination;
    int cur_n = 0;
    int cur_k = 0;
    Combinations(n, k, list_combinations, current_combination, cur_n, cur_k);
}

void Graph::Combinations(
    const int n, const int k, vector<multiset<int>>& list_combinations,
    multiset<int>& current_combination, int cur_n, int cur_k) {

    if (cur_k > k) {
        return;
    }

    if (cur_n - cur_k > n - k) {
        return;
    }

    if (cur_n == n || cur_k == k) {
        list_combinations.push_back(current_combination);
        return;
    }

    current_combination.insert(cur_n);

    Combinations(n, k, list_combinations, current_combination, cur_n + 1, cur_k + 1);

    current_combination.erase(cur_n);


    Combinations(n, k, list_combinations, current_combination, cur_n + 1, cur_k);

    return;
}

ostream& operator<<(ostream& o, const Graph& cur) {
    o << "Vertexes: " << cur.vertexes.size() << "\n";

    for (auto u: cur.vertexes) {
        o << u << ":";
        o << '(';
        for (auto v : cur.tails_list.at(u)) {
            o << v;
            o << ',';
        }
        o << ')';
        o << '\n';
    }
    return o;
}
