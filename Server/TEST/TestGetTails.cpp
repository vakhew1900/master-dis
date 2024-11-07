#include "pch.h"
#include "CppUnitTest.h"
#include "../CORE//ComparisonGraph.h"

using namespace Microsoft::VisualStudio::CppUnitTestFramework;

namespace TEST_GET_TAILS
{
	TEST_CLASS(TEST_GET_TAILS)
	{
	public:

		TEST_METHOD(NotVertex)
		{
			vector<pair<int, int>> list_of_edges = {};
			auto graph = Graph(list_of_edges, LabelsGenerator(), true);

			Assert::IsTrue(graph.GetTails(1) == multiset<int>());
		}

		TEST_METHOD(ZeroVertex)
		{
			unordered_map<int, multiset<int>> vertex_to_labels{{1, {1}}};
			LabelsGenerator labels_generator({ vertex_to_labels , unordered_map<int, multiset<int>>() }, unordered_map<int, int>());

			vector<pair<int, int>> list_of_edges = {};
			auto graph = Graph(list_of_edges, labels_generator, true);

			Assert::IsTrue(graph.GetTails(1) == multiset<int>());
		}

		TEST_METHOD(OneVertex)
		{
			vector<pair<int, int>> list_of_edges = { {1, 2} };
			auto graph = Graph(list_of_edges, LabelsGenerator(), true);

			Assert::IsTrue(graph.GetTails(1) == multiset<int>{2});
		}

		TEST_METHOD(SeveralVertexes)
		{
			vector<pair<int, int>> list_of_edges = { {1, 2}, {1, 3} };
			auto graph = Graph(list_of_edges, LabelsGenerator(), true);

			Assert::IsTrue(graph.GetTails(1) == multiset<int>{2, 3});
		}

		TEST_METHOD(MultiEdges) {
			vector<pair<int, int>> list_of_edges = { {1, 2}, {1, 2} };
			auto graph = Graph(list_of_edges, LabelsGenerator(), true);

			Assert::IsTrue(graph.GetTails(1) == multiset<int>{2, 2});
		}

		TEST_METHOD(LoopEdges) {
			vector<pair<int, int>> list_of_edges = { {1, 1} };
			auto graph = Graph(list_of_edges, LabelsGenerator(), true);

			Assert::IsTrue(graph.GetTails(1) == multiset<int>{1});
		}


		TEST_METHOD(MultiLoopEdges) {
			vector<pair<int, int>> list_of_edges = { {1, 1}, {1, 1} };
			auto graph = Graph(list_of_edges, LabelsGenerator(), true);

			Assert::IsTrue(graph.GetTails(1) == multiset<int>{1, 1});
		}
	};
}
