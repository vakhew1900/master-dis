#include "pch.h"
#include "CppUnitTest.h"
#include "../CORE//ComparisonGraph.h"

using namespace Microsoft::VisualStudio::CppUnitTestFramework;

namespace TEST_ADD_EDGE
{
	TEST_CLASS(TEST_ADD_EDGE)
	{
	public:

		TEST_METHOD(NotVertex)
		{
			vector<pair<int, int>> list_of_edges = {};
			auto graph = Graph(list_of_edges, LabelsGenerator(), true);

			graph.AddEdge(1, 2);

			Assert::IsTrue(graph.GetTails(1) == multiset<int>{2});
		}

		TEST_METHOD(ZeroVertex)
		{
			unordered_map<int, multiset<int>> vertex_to_labels{ {1, {1}} };
			LabelsGenerator labels_generator({ vertex_to_labels , unordered_map<int, multiset<int>>() }, unordered_map<int, int>());

			vector<pair<int, int>> list_of_edges = {};
			auto graph = Graph(list_of_edges, labels_generator, true);

			graph.AddEdge(1, 2);

			Assert::IsTrue(graph.GetTails(1) == multiset<int>{2});
		}

		TEST_METHOD(OneVertex)
		{
			vector<pair<int, int>> list_of_edges = { {1, 2} };
			auto graph = Graph(list_of_edges, LabelsGenerator(), true);

			graph.AddEdge(1, 3);

			Assert::IsTrue(graph.GetTails(1) == multiset<int>{2, 3});
		}

		TEST_METHOD(SeveralVertexes)
		{
			vector<pair<int, int>> list_of_edges = { {1, 2}, {1, 3} };
			auto graph = Graph(list_of_edges, LabelsGenerator(), true);

			graph.AddEdge(1, 4);

			Assert::IsTrue(graph.GetTails(1) == multiset<int>{2, 3, 4});
		}

		TEST_METHOD(MultiEdges) {
			vector<pair<int, int>> list_of_edges = { {1, 2}, {1, 2} };
			auto graph = Graph(list_of_edges, LabelsGenerator(), true);


			graph.AddEdge(1, 2);

			Assert::IsTrue(graph.GetTails(1) == multiset<int>{2, 2, 2});
		}

		TEST_METHOD(LoopEdges) {
			vector<pair<int, int>> list_of_edges = {};
			auto graph = Graph(list_of_edges, LabelsGenerator(), true);

			graph.AddEdge(1, 1);

			Assert::IsTrue(graph.GetTails(1) == multiset<int>{1});
		}

		TEST_METHOD(LoopMultiEdges) {
			vector<pair<int, int>> list_of_edges = {};
			auto graph = Graph(list_of_edges, LabelsGenerator(), true);

			graph.AddEdge(1, 1);
			graph.AddEdge(1, 1);

			Assert::IsTrue(graph.GetTails(1) == multiset<int>{1, 1});
		}
	};
}
