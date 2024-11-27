#include "pch.h"
#include "CppUnitTest.h"
#include "../CORE//ComparisonGraph.h"

using namespace Microsoft::VisualStudio::CppUnitTestFramework;

namespace TEST_SORTED_VERTEXES_INFORMATION_WITH_TRANSFORMED_LABELS
{
	TEST_CLASS(TEST_SORTED_VERTEXES_INFORMATION_WITH_TRANSFORMED_LABELS)
	{
	public:


		TEST_METHOD(ZeroVertex)
		{
			auto graph = Graph(vector<pair<int, int>>(), LabelsGenerator(), true);
			Assert::IsTrue(
				graph.SortedVertexesInformationWithTransformedLabels() == 
				vector<Graph::VertexInformationWithTransformedLabels>{});
		}

		TEST_METHOD(OneVertex)
		{
			unordered_map<int, multiset<int>> vertex_to_labels{ {1, {1}} };
			unordered_map<int, int> ids_to_labels{ { 1, 1 } };

			LabelsGenerator labels_generator({ vertex_to_labels , unordered_map<int, multiset<int>>() }, ids_to_labels);

			vector<pair<int, int>> list_of_edges = {};
			auto graph = Graph(list_of_edges, labels_generator, true);

			Graph::VertexInformationWithTransformedLabels info;
			info.labels = { 1 };

			Assert::IsTrue(
				graph.SortedVertexesInformationWithTransformedLabels() ==
				vector<Graph::VertexInformationWithTransformedLabels>{info});
		}

		TEST_METHOD(SeveralVertex)
		{
			unordered_map<int, multiset<int>> vertex_to_labels{ {1, {1}}, {2, {2}} };
			unordered_map<int, int> ids_to_labels{ { 1, 1 }, {2, 2} };

			LabelsGenerator labels_generator({ vertex_to_labels , unordered_map<int, multiset<int>>() }, ids_to_labels);

			vector<pair<int, int>> list_of_edges = { {1, 2} };
			auto graph = Graph(list_of_edges, labels_generator, true);

			Graph::VertexInformationWithTransformedLabels first_info;
			first_info.labels = { 1 };
			first_info.tails = { {2} };
			Graph::VertexInformationWithTransformedLabels second_info;
			second_info.labels = { 2 };
			second_info.heads = { {1} };

			Assert::IsTrue(
				graph.SortedVertexesInformationWithTransformedLabels() ==
				vector<Graph::VertexInformationWithTransformedLabels>{first_info, second_info});
		}

		TEST_METHOD(SeveralTailsOnVertex)
		{
			unordered_map<int, multiset<int>> vertex_to_labels{ {1, {1}} , {2, {2}}, {3, {3}} };
			unordered_map<int, int> ids_to_labels{ { 1, 1 }, {2, 2}, {3, 3} };

			LabelsGenerator labels_generator({ vertex_to_labels , unordered_map<int, multiset<int>>() }, ids_to_labels);

			vector<pair<int, int>> list_of_edges = { {1, 2}, {1, 3} };
			auto graph = Graph(list_of_edges, labels_generator, true);

			Graph::VertexInformationWithTransformedLabels first_info;
			first_info.labels = { 1 };
			first_info.tails = { { 2 }, {3} };
			Graph::VertexInformationWithTransformedLabels second_info;
			second_info.labels = { 2 };
			second_info.heads = { {1} };
			Graph::VertexInformationWithTransformedLabels third_info;
			third_info.labels = { 3 };
			third_info.heads = { {1} };

			Assert::IsTrue(
				graph.SortedVertexesInformationWithTransformedLabels() ==
				vector<Graph::VertexInformationWithTransformedLabels>{first_info, second_info, third_info});
		}

		TEST_METHOD(SeveralHeadsOnVertex)
		{
			unordered_map<int, multiset<int>> vertex_to_labels{ {1, {1}} , {2, {2}}, {3, {3}} };
			unordered_map<int, int> ids_to_labels{ { 1, 1 }, {2, 2}, {3, 3} };

			LabelsGenerator labels_generator({ vertex_to_labels , unordered_map<int, multiset<int>>() }, ids_to_labels);

			vector<pair<int, int>> list_of_edges = { {2, 1}, {3, 1} };
			auto graph = Graph(list_of_edges, labels_generator, true);

			Graph::VertexInformationWithTransformedLabels first_info;
			first_info.labels = { 1 };
			first_info.heads = { { 2 }, {3} };
			Graph::VertexInformationWithTransformedLabels second_info;
			second_info.labels = { 2 };
			second_info.tails = { {1} };
			Graph::VertexInformationWithTransformedLabels third_info;
			third_info.labels = { 3 };
			third_info.tails = { {1} };

			auto aa = graph.SortedVertexesInformationWithTransformedLabels();

			Assert::IsTrue(
				graph.SortedVertexesInformationWithTransformedLabels() ==
				vector<Graph::VertexInformationWithTransformedLabels>{first_info, second_info, third_info});
		}

		TEST_METHOD(LabelIdNotEqualLabel)
		{
			unordered_map<int, multiset<int>> expected_vertex_to_labels{ {1, {1}}, {2, {2}} };
			unordered_map<int, int> expected_ids_to_labels{ { 1, 1 }, {2, 2} };

			LabelsGenerator expected_labels_generator({ expected_vertex_to_labels , unordered_map<int, multiset<int>>() }, expected_ids_to_labels);

			vector<pair<int, int>> expected_list_of_edges = { {1, 2} };
			auto expected = Graph(expected_list_of_edges, expected_labels_generator, true);

			unordered_map<int, multiset<int>> actual_vertex_to_labels{ {1, {3}}, {2, {4}} };
			unordered_map<int, int> actual_ids_to_labels{ {3, 1 }, {4, 2} };

			LabelsGenerator actual_labels_generator({ actual_vertex_to_labels , unordered_map<int, multiset<int>>() }, actual_ids_to_labels);

			vector<pair<int, int>> actual_list_of_edges = { {1, 2} };
			auto actual = Graph(actual_list_of_edges, actual_labels_generator, true);


		

			Assert::IsTrue(
				expected.SortedVertexesInformationWithTransformedLabels() == actual.SortedVertexesInformationWithTransformedLabels());
		}

		TEST_METHOD(LabelIdNotEqualLabel2)
		{
			unordered_map<int, multiset<int>> expected_vertex_to_labels{ {1, {1}}, {2, {2}} };
			unordered_map<int, int> expected_ids_to_labels{ { 1, 1 }, {2, 2} };

			LabelsGenerator expected_labels_generator({ expected_vertex_to_labels , unordered_map<int, multiset<int>>() }, expected_ids_to_labels);

			vector<pair<int, int>> expected_list_of_edges = { {1, 2} };
			auto expected = Graph(expected_list_of_edges, expected_labels_generator, true);

			unordered_map<int, multiset<int>> actual_vertex_to_labels{ {1, {3}}, {2, {4}} };
			unordered_map<int, int> actual_ids_to_labels{ {4, 1 }, {3, 2} };

			LabelsGenerator actual_labels_generator({ actual_vertex_to_labels , unordered_map<int, multiset<int>>() }, actual_ids_to_labels);

			vector<pair<int, int>> actual_list_of_edges = { {2, 1} }; 
			auto actual = Graph(actual_list_of_edges, actual_labels_generator, true);


			Assert::IsTrue(
				expected.SortedVertexesInformationWithTransformedLabels() == actual.SortedVertexesInformationWithTransformedLabels());
		}

		TEST_METHOD(LabelIdNotEqualLabel3)
		{
			unordered_map<int, multiset<int>> expected_vertex_to_labels{ {1, {1}}, {2, {2}} };
			unordered_map<int, int> expected_ids_to_labels{ { 1, 1 }, {2, 2} };

			LabelsGenerator expected_labels_generator({ expected_vertex_to_labels , unordered_map<int, multiset<int>>() }, expected_ids_to_labels);

			vector<pair<int, int>> expected_list_of_edges = { {1, 2} };
			auto expected = Graph(expected_list_of_edges, expected_labels_generator, true);

			unordered_map<int, multiset<int>> actual_vertex_to_labels{ {1, {3}}, {2, {4}} };
			unordered_map<int, int> actual_ids_to_labels{ {4, 1 }, {3, 2} };

			LabelsGenerator actual_labels_generator({ actual_vertex_to_labels , unordered_map<int, multiset<int>>() }, actual_ids_to_labels);

			vector<pair<int, int>> actual_list_of_edges = { {1, 2} }; // направленность ребра 
			auto actual = Graph(actual_list_of_edges, actual_labels_generator, true);


			Assert::IsTrue(
				expected.SortedVertexesInformationWithTransformedLabels() != actual.SortedVertexesInformationWithTransformedLabels());
		}

	};
}
