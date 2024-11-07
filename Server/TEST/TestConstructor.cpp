#include "pch.h"
#include "CppUnitTest.h"
#include "../CORE//ComparisonGraph.h"

using namespace Microsoft::VisualStudio::CppUnitTestFramework;

namespace TEST_GRAPH_CONSTRUCTOR
{
	TEST_CLASS(TEST_GRAPH_CONSTRUCTOR)
	{
	public:

		Graph BuildBasicGraphForTests() {
			unordered_map<int, multiset<int>> vertex_to_labels{ {1, {1}}, {2, {2}}, {3, {3}} };
			LabelsGenerator labels_generator({ vertex_to_labels , unordered_map<int, multiset<int>>() }, unordered_map<int, int>());

			vector<pair<int, int>> list_of_edges = { {1, 2}, {2, 3} };
			return Graph(list_of_edges, labels_generator, true);
		}

		TEST_METHOD(TestVertexesConstructor)
		{
			multiset<int> expected_vertexes = { 1, 2, 3 };
			Assert::IsTrue(BuildBasicGraphForTests().GetVertexes() == expected_vertexes);
		}

		TEST_METHOD(TestLabelsConstructor)
		{
			Assert::IsTrue(BuildBasicGraphForTests().GetLabels(1) == multiset<int>{1});
			Assert::IsTrue(BuildBasicGraphForTests().GetLabels(2) == multiset<int>{2});
			Assert::IsTrue(BuildBasicGraphForTests().GetLabels(3) == multiset<int>{3});
		}

		TEST_METHOD(TestEdgesConstructor)
		{
			Assert::IsTrue(BuildBasicGraphForTests().GetTails(1) == multiset<int>{2});
			Assert::IsTrue(BuildBasicGraphForTests().GetTails(2) == multiset<int>{3});
			Assert::IsTrue(BuildBasicGraphForTests().GetTails(3) == multiset<int>{});
		}

		TEST_METHOD(ZeroVertexes) {
			auto graph = Graph(vector<pair<int, int>>{}, LabelsGenerator(), true);

			Assert::IsTrue(graph.GetVertexes().size() == 0);
		}

		TEST_METHOD(ZeroEdges) {
			unordered_map<int, multiset<int>> vertex_to_labels{ {1, {1}}, {2, {2}}, {3, {3}} };
			LabelsGenerator labels_generator({ vertex_to_labels , unordered_map<int, multiset<int>>() }, unordered_map<int, int>());
			auto graph = Graph(vector<pair<int, int>>{}, labels_generator, true);

			Assert::IsTrue(graph.GetVertexes() == multiset<int>{1, 2, 3});
		}

		TEST_METHOD(ZeroLabels) {
			vector<pair<int, int>> list_of_edges = { {1, 2}, {2, 3} };
			auto graph = Graph(list_of_edges, LabelsGenerator(), true);

			Assert::IsTrue(graph.GetVertexes() == multiset<int>{1, 2, 3});
		}

		TEST_METHOD(ZeroVertexNumber) {
			vector<pair<int, int>> list_of_edges = { {0, 1}, {1, 2} };
			auto graph = Graph(list_of_edges, LabelsGenerator(), true);

			Assert::IsTrue(graph.GetVertexes() == multiset<int>{0, 1, 2});
			Assert::IsTrue(graph.GetTails(0) == multiset<int>{1});
		}

		TEST_METHOD(NotSequenceVertexNumbers) {	
			vector<pair<int, int>> list_of_edges = { {0, 7}, {7, 2} };
			auto graph = Graph(list_of_edges, LabelsGenerator(), true);

			Assert::IsTrue(graph.GetVertexes() == multiset<int>{0, 7, 2});
			Assert::IsTrue(graph.GetTails(0) == multiset<int>{7});
		}

		TEST_METHOD(NegativeVertexNumber) {
			vector<pair<int, int>> list_of_edges = { {-1, -2}, {-2, -3} };
			auto graph = Graph(list_of_edges, LabelsGenerator(), true);

			Assert::IsTrue(graph.GetVertexes() == multiset<int>{-1, -2, -3});
			Assert::IsTrue(graph.GetTails(-1) == multiset<int>{-2});
		}

		TEST_METHOD(CycledGraph) {
			vector<pair<int, int>> list_of_edges = { {1, 2}, {2, 3}, {3, 1} };
			auto graph = Graph(list_of_edges, LabelsGenerator(), true);

			Assert::IsTrue(graph.GetVertexes() == multiset<int>{1, 2, 3});
			Assert::IsTrue(graph.GetTails(1) == multiset<int>{2});
			Assert::IsTrue(graph.GetTails(2) == multiset<int>{3});
			Assert::IsTrue(graph.GetTails(3) == multiset<int>{1});
		}

		TEST_METHOD(SeveralLabelsInOneVertex) {
			unordered_map<int, multiset<int>> vertex_to_labels{ {1, {1, 11}}, {2, {2, 22}}, {3, {3, 33}} };
			LabelsGenerator labels_generator({ vertex_to_labels , unordered_map<int, multiset<int>>() }, unordered_map<int, int>());
			auto graph = Graph(vector<pair<int, int>>{}, labels_generator, true);

			Assert::IsTrue(graph.GetVertexes() == multiset<int>{1, 2, 3});
			Assert::IsTrue(graph.GetLabels(1) == multiset<int>{1, 11});
			Assert::IsTrue(graph.GetLabels(2) == multiset<int>{2, 22});
			Assert::IsTrue(graph.GetLabels(3) == multiset<int>{3, 33});
		}

		TEST_METHOD(SeveralEdgesInOneVertex) {
			vector<pair<int, int>> list_of_edges = { {1, 2}, {1, 3}, {2, 1}, {2, 3}, {3, 1}, {3, 2} };
			auto graph = Graph(list_of_edges, LabelsGenerator(), true);

			Assert::IsTrue(graph.GetVertexes() == multiset<int>{1, 2, 3});
			Assert::IsTrue(graph.GetTails(1) == multiset<int>{2, 3});
			Assert::IsTrue(graph.GetTails(2) == multiset<int>{1, 3});
			Assert::IsTrue(graph.GetTails(3) == multiset<int>{1, 2});
		}
	};
}
