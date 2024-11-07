#include "pch.h"
#include "CppUnitTest.h"
#include "../CORE//ComparisonGraph.h"

using namespace Microsoft::VisualStudio::CppUnitTestFramework;

namespace TEST_GET_VERTEXES
{
	TEST_CLASS(TEST_GET_VERTEXES)
	{
	public:

		TEST_METHOD(ZeroVertexes)
		{
			vector<pair<int, int>> list_of_edges = {};
			auto graph = Graph(list_of_edges, LabelsGenerator(), true);

			Assert::IsTrue(graph.GetVertexes() == multiset<int>());
		}

		TEST_METHOD(OneVertex)
		{
			vector<pair<int, int>> list_of_edges = { {1, 1} };
			auto graph = Graph(list_of_edges, LabelsGenerator(), true);

			Assert::IsTrue(graph.GetVertexes() == multiset<int>{1});
		}

		TEST_METHOD(SeveralVertexes)
		{
			vector<pair<int, int>> list_of_edges = { {1, 2}, {2, 3} };
			auto graph = Graph(list_of_edges, LabelsGenerator(), true);

			Assert::IsTrue(graph.GetVertexes() == multiset<int>{1, 2, 3});
		}

		TEST_METHOD(MaxNumberNotEqualSize) {
			vector<pair<int, int>> list_of_edges = { {1, 2}, {2, 10} };
			auto graph = Graph(list_of_edges, LabelsGenerator(), true);

			Assert::IsTrue(graph.GetVertexes() == multiset<int>{1, 2, 10});
		}

		TEST_METHOD(NegativeVertexes) {
			vector<pair<int, int>> list_of_edges = { {-1, -2}, {-2, -3} };
			auto graph = Graph(list_of_edges, LabelsGenerator(), true);

			Assert::IsTrue(graph.GetVertexes() == multiset<int>{-1, -2, -3});
		}
	};
}
