#include "pch.h"
#include "CppUnitTest.h"
#include "../CORE//ComparisonGraph.h"

using namespace Microsoft::VisualStudio::CppUnitTestFramework;

namespace TEST_SUBGRAPH
{
	TEST_CLASS(TEST_SUBGRAPH)
	{
	public:
		TEST_METHOD(EmptySubgraph)
		{
			vector<pair<int, int>> list_of_edegs_expected = { {1, 2}, {2, 3} };

			unordered_map<int, multiset<int>> vertex_to_labels_first_graph{ {1, {1}}, {2, {2}}, {3, {3}} };
			unordered_map<int, multiset<int>> vertex_to_labels_second_graph{ {1, {4}}, {2, {5}}, {3, {6}} };
			unordered_map<int, int> labels_ids_to_labels{ {1, 1}, {2, 2}, {3, 3}, {4, 1}, {5, 2}, {6, 3} };

			LabelsGenerator labels_generator({ vertex_to_labels_first_graph , vertex_to_labels_second_graph }, labels_ids_to_labels);

			Graph graph(list_of_edegs_expected, labels_generator, true);

			Graph subgraph = graph.Subgraph(graph.AllLabels());

			Assert::IsTrue(subgraph.GetVertexes().size() == 0);
		}


		TEST_METHOD(AllSubgraph)
		{
			vector<pair<int, int>> list_of_edegs_expected = { {1, 2}, {2, 3} };

			unordered_map<int, multiset<int>> vertex_to_labels_first_graph{ {1, {1}}, {2, {2}}, {3, {3}} };
			unordered_map<int, multiset<int>> vertex_to_labels_second_graph{ {1, {4}}, {2, {5}}, {3, {6}} };
			unordered_map<int, int> labels_ids_to_labels{ {1, 1}, {2, 2}, {3, 3}, {4, 1}, {5, 2}, {6, 3} };

			LabelsGenerator labels_generator({ vertex_to_labels_first_graph , vertex_to_labels_second_graph }, labels_ids_to_labels);

			Graph graph(list_of_edegs_expected, labels_generator, true);

			Graph subgraph = graph.Subgraph({});

			Assert::IsTrue(subgraph.GetVertexes().size() == 3);
		}

		TEST_METHOD(TypeSubrgraph)
		{
			vector<pair<int, int>> list_of_edegs_expected = { {1, 2}, {2, 3} };

			unordered_map<int, multiset<int>> vertex_to_labels_first_graph{ {1, {1}}, {2, {2}}, {3, {3}} };
			unordered_map<int, multiset<int>> vertex_to_labels_second_graph{ {1, {4}}, {2, {5}}, {3, {6}} };
			unordered_map<int, int> labels_ids_to_labels{ {1, 1}, {2, 2}, {3, 3}, {4, 1}, {5, 2}, {6, 3} };

			LabelsGenerator labels_generator({ vertex_to_labels_first_graph , vertex_to_labels_second_graph }, labels_ids_to_labels);

			Graph graph(list_of_edegs_expected, labels_generator, true);

			auto f = graph.AllLabels();
			Graph subgraph = graph.Subgraph({1});

			Assert::IsTrue(subgraph.GetVertexes() == std::multiset<int>({2, 3}));
		}
	};
}
