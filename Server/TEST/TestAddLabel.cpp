 #include "pch.h"
#include "CppUnitTest.h"
#include "../CORE//ComparisonGraph.h"

using namespace Microsoft::VisualStudio::CppUnitTestFramework;

namespace TEST_ADD_LABELS
{
	TEST_CLASS(TEST_ADD_LABELS)
	{
	public:

		TEST_METHOD(NotVertex)
		{
			vector<pair<int, int>> list_of_edges = {};
			auto graph = Graph(list_of_edges, LabelsGenerator(), true);

			graph.AddLabel(1, 1);
			Assert::IsTrue(graph.GetLabels(1) == multiset<int>{1});
		}

		TEST_METHOD(ZeroLabels)
		{
			unordered_map<int, multiset<int>> vertex_to_labels{ {1, {}} };
			LabelsGenerator labels_generator({ vertex_to_labels , unordered_map<int, multiset<int>>() }, unordered_map<int, int>());
			auto graph = Graph(vector<pair<int, int>>(), labels_generator, true);

			graph.AddLabel(1, 1);
			Assert::IsTrue(graph.GetLabels(1) == multiset<int>{1});
		}

		TEST_METHOD(OneLabel)
		{
			unordered_map<int, multiset<int>> vertex_to_labels{ {1, {1}} };
			LabelsGenerator labels_generator({ vertex_to_labels , unordered_map<int, multiset<int>>() }, unordered_map<int, int>());
			auto graph = Graph(vector<pair<int, int>>(), labels_generator, true);

			graph.AddLabel(1, 2);
			Assert::IsTrue(graph.GetLabels(1) == multiset<int>{1, 2});
		}

		TEST_METHOD(SeveralLabels)
		{
			unordered_map<int, multiset<int>> vertex_to_labels{ {1, {1, 2}} };
			LabelsGenerator labels_generator({ vertex_to_labels , unordered_map<int, multiset<int>>() }, unordered_map<int, int>());
			auto graph = Graph(vector<pair<int, int>>(), labels_generator, true);

			graph.AddLabel(1, 3);
			Assert::IsTrue(graph.GetLabels(1) == multiset<int>{1, 2, 3});
		}

		TEST_METHOD(MultiLabels) {
			unordered_map<int, multiset<int>> vertex_to_labels{ {1, {1, 1}} };
			LabelsGenerator labels_generator({ vertex_to_labels , unordered_map<int, multiset<int>>() }, unordered_map<int, int>());
			auto graph = Graph(vector<pair<int, int>>(), labels_generator, true);

			graph.AddLabel(1, 1);
			Assert::IsTrue(graph.GetLabels(1) == multiset<int>{1, 1, 1});
		}

		TEST_METHOD(NegativeLabels) {
			unordered_map<int, multiset<int>> vertex_to_labels{ {1, {}} };
			LabelsGenerator labels_generator({ vertex_to_labels , unordered_map<int, multiset<int>>() }, unordered_map<int, int>());
			auto graph = Graph(vector<pair<int, int>>(), labels_generator, true);

			graph.AddLabel(1, -1);
			Assert::IsTrue(graph.GetLabels(1) == multiset<int>{-1});
		}
	};
}
