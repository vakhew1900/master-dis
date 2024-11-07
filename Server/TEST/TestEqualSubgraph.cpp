#include "pch.h"
#include "CppUnitTest.h"
#include "../CORE//ComparisonGraph.h"

using namespace Microsoft::VisualStudio::CppUnitTestFramework;

namespace TEST_EQUAL_SUBGRAPH
{
	TEST_CLASS(TEST_EQUAL_SUBGRAPH)
	{
	public:
		TEST_METHOD(NotEquals)
		{
			vector<pair<int, int>> list_of_edges_actual = { {1, 3}, {3, 2} };
			vector<pair<int, int>> list_of_edegs_expected = { {1, 2}, {2, 3} };

			unordered_map<int, multiset<int>> vertex_to_labels_first_graph{ {1, {1}}, {2, {2}}, {3, {3}} };
			unordered_map<int, multiset<int>> vertex_to_labels_second_graph{ {1, {4}}, {2, {5}}, {3, {6}} };
			unordered_map<int, int> labels_ids_to_labels{ {1, 1}, {2, 2}, {3, 3}, {4, 1}, {5, 2}, {6, 3} };

			LabelsGenerator labels_generator({ vertex_to_labels_first_graph , vertex_to_labels_second_graph }, labels_ids_to_labels);

			Graph expected(list_of_edegs_expected, labels_generator, true);
			Graph actual(list_of_edges_actual, labels_generator, false);

			auto equals = Graph::EqualSubgraphs(expected, actual, std::multiset<int>{1, 2, 3, 4, 5, 6});

			Assert::IsTrue(equals == make_pair(std::multiset<int>(), std::multiset<int>()));
		}


		TEST_METHOD(NotEqualsPart)
		{
			vector<pair<int, int>> list_of_edges_actual = { {1, 3}, {3, 2} };
			vector<pair<int, int>> list_of_edegs_expected = { {1, 2}, {2, 3} };

			unordered_map<int, multiset<int>> vertex_to_labels_first_graph{ {1, {1}}, {2, {2}}, {3, {3}} };
			unordered_map<int, multiset<int>> vertex_to_labels_second_graph{ {1, {4}}, {2, {5}}, {3, {6}} };
			unordered_map<int, int> labels_ids_to_labels{ {1, 1}, {2, 2}, {3, 3}, {4, 1}, {5, 2}, {6, 3} };

			LabelsGenerator labels_generator({ vertex_to_labels_first_graph , vertex_to_labels_second_graph }, labels_ids_to_labels);

			Graph expected(list_of_edegs_expected, labels_generator, true);
			Graph actual(list_of_edges_actual, labels_generator, false);

			auto equals = Graph::EqualSubgraphs(expected, actual, std::multiset<int>{1, 5});

			Assert::IsTrue(equals == make_pair(std::multiset<int>(), std::multiset<int>()));
		}

		
		TEST_METHOD(Equals)
		{
			vector<pair<int, int>> list_of_edges_actual = { {1, 3}, {3, 2} };
			vector<pair<int, int>> list_of_edegs_expected = { {1, 2}, {2, 3} };

			unordered_map<int, multiset<int>> vertex_to_labels_first_graph{ {1, {1}}, {2, {2}}, {3, {3}} };
			unordered_map<int, multiset<int>> vertex_to_labels_second_graph{ {1, {4}}, {2, {5}}, {3, {6}} };
			unordered_map<int, int> labels_ids_to_labels{ {1, 1}, {2, 2}, {3, 3}, {4, 1}, {5, 2}, {6, 3} };

			LabelsGenerator labels_generator({ vertex_to_labels_first_graph , vertex_to_labels_second_graph }, labels_ids_to_labels);

			Graph expected(list_of_edegs_expected, labels_generator, true);
			Graph actual(list_of_edges_actual, labels_generator, false);

			auto equals = Graph::EqualSubgraphs(expected, actual, std::multiset<int>{3, 6}); // неочевидно что нужно указывать id для двух элементов но да ладно

			Assert::IsTrue(equals != make_pair(std::multiset<int>(), std::multiset<int>()));
		}


		TEST_METHOD(EqualsPart)
		{
			vector<pair<int, int>> list_of_edges_actual = { {1, 3}, {3, 2} };
			vector<pair<int, int>> list_of_edegs_expected = { {1, 2}, {2, 3} };

			unordered_map<int, multiset<int>> vertex_to_labels_first_graph{ {1, {1}}, {2, {2}}, {3, {3}} };
			unordered_map<int, multiset<int>> vertex_to_labels_second_graph{ {1, {4}}, {2, {5}}, {3, {6}} };
			unordered_map<int, int> labels_ids_to_labels{ {1, 1}, {2, 2}, {3, 3}, {4, 1}, {5, 2}, {6, 3} };

			LabelsGenerator labels_generator({ vertex_to_labels_first_graph , vertex_to_labels_second_graph }, labels_ids_to_labels);

			Graph expected(list_of_edegs_expected, labels_generator, true);
			Graph actual(list_of_edges_actual, labels_generator, false);

			auto equals = Graph::EqualSubgraphs(expected, actual, std::multiset<int>{2, 3, 5, 6});

			Assert::IsTrue(equals != make_pair(std::multiset<int>(), std::multiset<int>())); // странный конечно результат теста в том плане что правильнее наверное написать сами подотрезки
		}

		TEST_METHOD(EqualsButNotEqualByNumbers)
		{
			vector<pair<int, int>> list_of_edges_actual = { {3, 2}, {2, 1} };
			vector<pair<int, int>> list_of_edegs_expected = { {1, 2}, {2, 3} };

			unordered_map<int, multiset<int>> vertex_to_labels_actual_graph{ {3, {1}}, {2, {2}}, {1, {3}} };
			unordered_map<int, multiset<int>> vertex_to_labels_expected_graph{ {1, {4}}, {2, {5}}, {3, {6}} };
			unordered_map<int, int> labels_ids_to_labels{ {1, 1}, {2, 2}, {3, 3}, {4, 1}, {5, 2}, {6, 3} };

			LabelsGenerator labels_generator({ vertex_to_labels_actual_graph, vertex_to_labels_expected_graph }, labels_ids_to_labels);

			Graph expected(list_of_edegs_expected, labels_generator, true);
			Graph actual(list_of_edges_actual, labels_generator, false);

			auto equals = Graph::EqualSubgraphs(expected, actual, std::multiset<int>{2, 3, 5, 6});

			Assert::IsTrue(equals != make_pair(std::multiset<int>(), std::multiset<int>()));
		}
	};
}
