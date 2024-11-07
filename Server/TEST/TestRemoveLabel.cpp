#include "pch.h"
#include "CppUnitTest.h"
#include "../CORE//ComparisonGraph.h"

using namespace Microsoft::VisualStudio::CppUnitTestFramework;

namespace TEST_REMOVE_LABELS
{
	TEST_CLASS(TEST_REMOVE_LABELS)
	{
	public:

		TEST_METHOD(NotVertex)
		{
			vector<pair<int, int>> list_of_edges = {};
			auto graph = Graph(list_of_edges, LabelsGenerator(), true);

			graph.RemoveLabel(1, 1);
			Assert::IsTrue(graph.GetLabels(1) == multiset<int>{});
		}

		TEST_METHOD(ZeroLabels)
		{
			unordered_map<int, multiset<int>> vertex_to_labels{ {1, {}} };
			LabelsGenerator labels_generator({ vertex_to_labels , unordered_map<int, multiset<int>>() }, unordered_map<int, int>());
			auto graph = Graph(vector<pair<int, int>>(), labels_generator, true);

			graph.RemoveLabel(1, 1);
			Assert::IsTrue(graph.GetLabels(1) == multiset<int>{});
		}

		TEST_METHOD(OneLabel)
		{
			unordered_map<int, multiset<int>> vertex_to_labels{ {1, {1}} };
			LabelsGenerator labels_generator({ vertex_to_labels , unordered_map<int, multiset<int>>() }, unordered_map<int, int>());
			auto graph = Graph(vector<pair<int, int>>(), labels_generator, true);

			graph.RemoveLabel(1, 1);
			Assert::IsTrue(graph.GetLabels(1) == multiset<int>{});
		}

		TEST_METHOD(SeveralLabels)
		{
			unordered_map<int, multiset<int>> vertex_to_labels{ {1, {1, 2}} };
			LabelsGenerator labels_generator({ vertex_to_labels , unordered_map<int, multiset<int>>() }, unordered_map<int, int>());
			auto graph = Graph(vector<pair<int, int>>(), labels_generator, true);

			graph.RemoveLabel(1, 2);
			Assert::IsTrue(graph.GetLabels(1) == multiset<int>{1});
		}

		TEST_METHOD(MultiLabels) {
			unordered_map<int, multiset<int>> vertex_to_labels{ {1, {1, 1}} };
			LabelsGenerator labels_generator({ vertex_to_labels , unordered_map<int, multiset<int>>() }, unordered_map<int, int>());
			auto graph = Graph(vector<pair<int, int>>(), labels_generator, true);

			graph.RemoveLabel(1, 1);

			//somnitelno, no okey
			Assert::IsTrue(graph.GetLabels(1) == multiset<int>{});
		}

		TEST_METHOD(NegativeLabel) {
			unordered_map<int, multiset<int>> vertex_to_labels{ {1, {-1}} };
			LabelsGenerator labels_generator({ vertex_to_labels , unordered_map<int, multiset<int>>() }, unordered_map<int, int>());
			auto graph = Graph(vector<pair<int, int>>(), labels_generator, true);

			graph.RemoveLabel(1, -1);

			Assert::IsTrue(graph.GetLabels(1) == multiset<int>{});
		}
	};
}
