#include "pch.h"
#include "CppUnitTest.h"
#include "../CORE//ComparisonGraph.h"

using namespace Microsoft::VisualStudio::CppUnitTestFramework;

namespace TEST_ALL_LABELS
{
	TEST_CLASS(TEST_ALL_LABELS)
	{
	public:

		TEST_METHOD(NotVertexes)
		{
			vector<pair<int, int>> list_of_edges = {};
			auto graph = Graph(list_of_edges, LabelsGenerator(), true);

			Assert::IsTrue(graph.AllLabels() == multiset<int>{});
		}

		TEST_METHOD(ZeroLabels)
		{
			unordered_map<int, multiset<int>> vertex_to_labels{ {1, {}} };
			LabelsGenerator labels_generator({ vertex_to_labels , unordered_map<int, multiset<int>>() }, unordered_map<int, int>());
			auto graph = Graph(vector<pair<int, int>>(), labels_generator, true);

			Assert::IsTrue(graph.AllLabels() == multiset<int>{});
		}

		TEST_METHOD(OneVertexWithLabel)
		{
			unordered_map<int, multiset<int>> vertex_to_labels{ {1, {1}} };
			LabelsGenerator labels_generator({ vertex_to_labels , unordered_map<int, multiset<int>>() }, unordered_map<int, int>());
			auto graph = Graph(vector<pair<int, int>>(), labels_generator, true);

			Assert::IsTrue(graph.AllLabels() == multiset<int>{1});
		}

		TEST_METHOD(SeveralVertexesWithoutLabelsIntersect)
		{
			unordered_map<int, multiset<int>> vertex_to_labels{ {1, {1, 2}}, {2, {3, 4}} };
			LabelsGenerator labels_generator({ vertex_to_labels , unordered_map<int, multiset<int>>() }, unordered_map<int, int>());
			auto graph = Graph(vector<pair<int, int>>(), labels_generator, true);

			Assert::IsTrue(graph.AllLabels() == multiset<int>{1, 2, 3, 4});
		}

		TEST_METHOD(SeveralVertexesWithLabelsIntersect) {
			unordered_map<int, multiset<int>> vertex_to_labels{ {1, {1, 2}}, {2, {2, 3}} };
			LabelsGenerator labels_generator({ vertex_to_labels , unordered_map<int, multiset<int>>() }, unordered_map<int, int>());
			auto graph = Graph(vector<pair<int, int>>(), labels_generator, true);

			Assert::IsTrue(graph.AllLabels() == multiset<int>{1, 2, 2, 3});
		}
	};
}
