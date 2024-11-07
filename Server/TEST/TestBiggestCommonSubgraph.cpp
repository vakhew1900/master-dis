#include "pch.h"
#include "CppUnitTest.h"
#include "../CORE//ComparisonGraph.h"

using namespace Microsoft::VisualStudio::CppUnitTestFramework;

namespace TEST_BIGGEST_COMMON_SUBGRAPH
{
	TEST_CLASS(TEST_BIGGEST_COMMON_SUBGRAPH)
	{
	public:
		TEST_METHOD(BaseTest)
		{
			vector<pair<int, int>> list_of_edges_actual = { {1, 3}, {3, 2} };
			vector<pair<int, int>> list_of_edegs_expected = { {1, 2}, {2, 3} };

			unordered_map<int, multiset<int>> vertex_to_labels_first_graph{ {1, {1}}, {2, {2}}, {3, {3}} };
			unordered_map<int, multiset<int>> vertex_to_labels_second_graph{ {1, {4}}, {2, {5}}, {3, {6}} };
			unordered_map<int, int> labels_ids_to_labels{ {1, 1}, {2, 2}, {3, 3}, {4, 1}, {5, 2}, {6, 3} };

			LabelsGenerator labels_generator({ vertex_to_labels_first_graph , vertex_to_labels_second_graph }, labels_ids_to_labels);

			Graph expected(list_of_edegs_expected, labels_generator, true);
			Graph actual(list_of_edges_actual, labels_generator, false);

			const auto common_subgraph_labels = Graph::BiggestCommonSubgraph(expected, actual);

			pair<multiset<int>, multiset<int>> expected_subgraph_labels{ {1, 2}, {4, 5} };
			Assert::IsTrue(expected_subgraph_labels == common_subgraph_labels);
		}

		TEST_METHOD(EqualGraphs)
		{
			vector<pair<int, int>> list_of_edges_actual = { {1, 2}, {2, 3} };
			vector<pair<int, int>> list_of_edegs_expected = { {1, 2}, {2, 3} };

			unordered_map<int, multiset<int>> vertex_to_labels_first_graph{ {1, {1}}, {2, {2}}, {3, {3}} };
			unordered_map<int, multiset<int>> vertex_to_labels_second_graph{ {1, {4}}, {2, {5}}, {3, {6}} };
			unordered_map<int, int> labels_ids_to_labels{ {1, 1}, {2, 2}, {3, 3}, {4, 1}, {5, 2}, {6, 3} };

			LabelsGenerator labels_generator({ vertex_to_labels_first_graph , vertex_to_labels_second_graph }, labels_ids_to_labels);

			Graph expected(list_of_edegs_expected, labels_generator, true);
			Graph actual(list_of_edges_actual, labels_generator, false);

			const auto common_subgraph_labels = Graph::BiggestCommonSubgraph(expected, actual);

			pair<multiset<int>, multiset<int>> expected_subgraph_labels{ {1, 2, 3}, {4, 5, 6} };
			Assert::IsTrue(expected_subgraph_labels == common_subgraph_labels);
		}

		TEST_METHOD(ZeroIntersection)
		{
			vector<pair<int, int>> list_of_edges_actual = { {1, 3}, {3, 2} };
			vector<pair<int, int>> list_of_edegs_expected = { {1, 2}, {2, 3} };

			unordered_map<int, multiset<int>> vertex_to_labels_first_graph{ {1, {1}}, {2, {2}}, {3, {3}} };
			unordered_map<int, multiset<int>> vertex_to_labels_second_graph{ {1, {4}}, {2, {5}}, {3, {6}} };
			unordered_map<int, int> labels_ids_to_labels{ {1, 1}, {2, 2}, {3, 3}, {4, 4}, {5, 5}, {6, 6} };

			LabelsGenerator labels_generator({ vertex_to_labels_first_graph , vertex_to_labels_second_graph }, labels_ids_to_labels);

			Graph expected(list_of_edegs_expected, labels_generator, true);
			Graph actual(list_of_edges_actual, labels_generator, false);

			const auto common_subgraph_labels = Graph::BiggestCommonSubgraph(expected, actual);


			pair<multiset<int>, multiset<int>> expected_subgraph_labels{ {}, {} };
			Assert::IsTrue(expected_subgraph_labels == common_subgraph_labels);
		}

		TEST_METHOD(SeveralEqualsLabels)
		{
			vector<pair<int, int>> list_of_edges_actual = { {1, 3}, {3, 2} };
			vector<pair<int, int>> list_of_edegs_expected = { {1, 2}, {2, 3} };

			unordered_map<int, multiset<int>> vertex_to_labels_first_graph{ {1, {1}}, {2, {2}}, {3, {3, 7}} };
			unordered_map<int, multiset<int>> vertex_to_labels_second_graph{ {1, {4}}, {2, {5}}, {3, {6, 8, 9}} };
			unordered_map<int, int> labels_ids_to_labels{ {1, 1}, {2, 2}, {3, 3}, {4, 1}, {5, 2}, {6, 3}, {7, 3}, {8, 3}, {9, 3} };

			LabelsGenerator labels_generator({ vertex_to_labels_first_graph , vertex_to_labels_second_graph }, labels_ids_to_labels);

			Graph expected(list_of_edegs_expected, labels_generator, true);
			Graph actual(list_of_edges_actual, labels_generator, false);

			const auto common_subgraph_labels = Graph::BiggestCommonSubgraph(expected, actual);

			//todo чет здесь лажа как будто, такого быть точно не должно
			pair<multiset<int>, multiset<int>> expected_subgraph_labels{ {1, 3, 7}, {4, 6, 8} };
			Assert::IsTrue(expected_subgraph_labels == common_subgraph_labels);
		}

		TEST_METHOD(VertexSwapped)
		{
			vector<pair<int, int>> list_of_edges_actual = { {3, 2}, {2, 1} };
			vector<pair<int, int>> list_of_edegs_expected = { {1, 2}, {2, 3} };

			unordered_map<int, multiset<int>> vertex_to_labels_actual_graph{ {3, {1}}, {2, {2}}, {1, {3}} };
			unordered_map<int, multiset<int>> vertex_to_labels_expected_graph{ {1, {4}}, {2, {5}}, {3, {6}} };
			unordered_map<int, int> labels_ids_to_labels{ {1, 1}, {2, 2}, {3, 3}, {4, 1}, {5, 2}, {6, 3} };

			LabelsGenerator labels_generator({ vertex_to_labels_actual_graph, vertex_to_labels_expected_graph }, labels_ids_to_labels);

			Graph expected(list_of_edegs_expected, labels_generator, true);
			Graph actual(list_of_edges_actual, labels_generator, false);

			const auto common_subgraph_labels = Graph::BiggestCommonSubgraph(expected, actual);

			pair<multiset<int>, multiset<int>> expected_subgraph_labels{ {1, 2, 3} , {4, 5, 6} };
			Assert::IsTrue(expected_subgraph_labels == common_subgraph_labels);
		}

		TEST_METHOD(EmptyGraphs)
		{

			Graph expected(vector<pair<int, int>>(), LabelsGenerator(), true);
			Graph actual(vector<pair<int, int>>(), LabelsGenerator(), false);

			const auto common_subgraph_labels = Graph::BiggestCommonSubgraph(expected, actual);

			pair<multiset<int>, multiset<int>> expected_subgraph_labels{ {} , {} };
			Assert::IsTrue(expected_subgraph_labels == common_subgraph_labels);
		}

		TEST_METHOD(NegativeVertexesAndLabels)
		{
			vector<pair<int, int>> list_of_edges_actual = { {-1, -3}, {-3, -2} };
			vector<pair<int, int>> list_of_edegs_expected = { {-1, -2}, {-2, -3} };

			unordered_map<int, multiset<int>> vertex_to_labels_first_graph{ {-1, {-1}}, {-2, {-2}}, {-3, {-3}} };
			unordered_map<int, multiset<int>> vertex_to_labels_second_graph{ {-1, {-4}}, {-2, {-5}}, {-3, {-6}} };
			unordered_map<int, int> labels_ids_to_labels{ {-1, -1}, {-2, -2}, {-3, -3}, {-4, -1}, {-5, -2}, {-6, -3} };

			LabelsGenerator labels_generator({ vertex_to_labels_first_graph , vertex_to_labels_second_graph }, labels_ids_to_labels);

			Graph expected(list_of_edegs_expected, labels_generator, true);
			Graph actual(list_of_edges_actual, labels_generator, false);

			const auto common_subgraph_labels = Graph::BiggestCommonSubgraph(expected, actual);

			pair<multiset<int>, multiset<int>> expected_subgraph_labels{ {-1, -3}, {-4, -6} };
			Assert::IsTrue(expected_subgraph_labels == common_subgraph_labels);
		}
	};
}
