#pragma once
#include <iostream>
#include <unordered_set>
#include <unordered_map>
#include <vector>
#include <set>
#include "LabelsGenerator.h"

using namespace std;

class Graph {
public:
    //конструкторы
    Graph();

    Graph(const vector<pair<int, int>>& list_of_edges);

    Graph(const vector<pair<int, int>>& list_of_edges,
        const unordered_map<int, multiset<int>>& vertex_to_labels);

    Graph(const vector<pair<int, int>>& list_of_edges,
        const LabelsGenerator& labels_generator, const bool is_first);

    //разные геттеры
    int GetNumberVertexes() const;

    multiset<int> GetTails(const int vertex) const;

    multiset<int> GetLabels(const int vertex) const;

    multiset<int> GetVertexes() const;

    //не тривиальные геттеры

    multiset<int> AllLabels() const;

    int Root() const;

    //тривиальные алгоритмы
    void AddEdge(const int u, const int v);
    void RemoveEdge(const int u, const int v);
    void AddLabel(const int vertex, const int label);
    void RemoveLabel(const int vertex, const int label);
    bool ContainsVertex(int vertex) const;
    static bool ComplexEqualGraphs(const Graph& first, const Graph& second);

    //нетривиальные алгоритмы
    //todo алгос не нужен этот
    unordered_map<int, unordered_map<int, bool>> ReachableMatrix() const;
    unordered_map<int, bool> ReachableArray(int from) const;

    /*! \brief Функция получения подграфа. NB: важно понимать, что понятие подграфа в данной задаче весьма специфично,
    *   в данном графе остаются только требуемые вершины, а также дуги между ними,
    *   также добавляется дуга между вершинами from и to добавляется, если в исходном под графе
    *   был путь из вершины from в вершину to, и все вершины в пути между from и to удалены
    *   \param[in] labels - метки вершин входящие в подграф
    *   \return подграф
    */
    Graph Subgraph(const multiset<int>& labels) const;

    /*! \brief Сравнение для двух графов их подрафы по заданному набору меток вершин
    *   \param[in] first_graph - первый граф
    *   \param[in] second_graph - второй граф
    *   \param[in] vertexes - метки вершин, входящие в подграф
    *   \return true если подграфы равны, false - иначе
    */
    static pair<multiset<int>, multiset<int>> EqualSubgraphs(const Graph& first_graph, const Graph& second_graph,
        multiset<int>& labels);

    /*! \brief Наибольший общий подграф из 2 графов
    *   \param[in] first_graph - первый граф
    *   \param[in] second_graph - второй граф
    *   \return вершины, входящие в подграф
    */
    static pair<multiset<int>, multiset<int>> BiggestCommonSubgraph(const Graph& first_graph, const Graph& second_graph);

    friend ostream& operator<<(ostream& o, const Graph& cur);

    /*! \brief Функция генерации всех возможных сочнтаний из n по k
    *   \param[in] n - конечное количество рассмотренных элементов для сочетания
    *   \param[in] k - конечное количество взятых элементов для сочетания
    *   \param[out] list_combinations - список сочетаний из n по k
    */
    static void Combinations(
        const int n, const int k, vector<multiset<int>>& list_combinations);

    struct VertexInformationWithTransformedLabels {
        vector<multiset<int>> tails;
        vector<multiset<int>> heads;
        multiset<int> labels;
        int number;

        friend bool operator < (const VertexInformationWithTransformedLabels& a, const VertexInformationWithTransformedLabels& b) {
            if (a.labels != b.labels) {
                return a.labels < b.labels;
            }
            if (a.heads != b.heads) {
                return a.heads < b.heads;
            }
            return a.tails < b.tails;
        }

        friend bool operator == (const VertexInformationWithTransformedLabels& a, const VertexInformationWithTransformedLabels& b) {
            return a.labels == b.labels && a.tails == b.tails && a.heads == b.heads;
        }
    };

    vector<VertexInformationWithTransformedLabels> SortedVertexesInformationWithTransformedLabels() const;

private:
    //! Идентификаторы вершин
    multiset<int> vertexes;
    unordered_map<int, multiset<int>> tails_list;
    unordered_map<int, multiset<int>> labels_list;
    LabelsGenerator labels_generator;

    /*! \brief Рекурсивная функция генерации всех возможных сочнтаний из n по k
    *   \param[in] n - конечное количество рассмотренных элементов для сочетания
    *   \param[in] k - конечное количество взятых элементов для сочетания 
    *   \param[out] list_combinations - список сочетаний из n по k
    *   \param[in] current_combination - текущее сочетание (пустое изначально)
    *   \param[in] cur_n - текущее количество рассмотренных элементов для сочетания (изначально 0)
    *   \param[in] cur_k - текущее количество взятых элементов для сочетания (изначально 0)
    */
    static void Combinations(
        const int n, const int k, vector<multiset<int>>& list_combinations,
        multiset<int>& current_combination, int cur_n = 0, int cur_k = 0);

    vector<int> RecoveryLabelIds();
};
