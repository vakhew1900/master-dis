#include "pch.h"
#include "CppUnitTest.h"
#include "../CORE/LabelsGenerator.h"

using namespace Microsoft::VisualStudio::CppUnitTestFramework;

namespace TEST_LABELS_GENERATOR
{
	TEST_CLASS(TEST_LABELS_GENERATOR)
	{
	public:

		TEST_METHOD(TestZeroLabel)
		{
			vector<string> merged_diff = {
				"diff",
			};

			Commit commit;

			Repository first;
			commit.diffs = { merged_diff };
			commit.number = 1;
			first.commits.insert(commit);

			Repository second;
			commit.diffs = { merged_diff };
			commit.number = 2;
			second.commits.insert(commit);

			LabelsGenerator labels_generator(first, second);

			auto labels = labels_generator.vertexex_to_labelsids;

			Assert::IsTrue(labels == make_pair(unordered_map<int, multiset<int>>(), std::unordered_map<int, multiset<int>>()));
		}


		TEST_METHOD(TestOneLabel)
		{
			vector<string> merged_diff = {
				"diff",
				"@@ - 1, 3 + 1, 3 @@ I AM SUPER GOOD CONTEXT",
				"+label"
			};

			Commit commit;

			Repository first;
			commit.diffs = { merged_diff };
			commit.number = 1;
			first.commits.insert(commit);

			Repository second;
			commit.diffs = { merged_diff };
			commit.number = 2;
			second.commits.insert(commit);

			LabelsGenerator labels_generator(first, second);

			auto labels = labels_generator.vertexex_to_labelsids;

			unordered_map<int, multiset<int>> expected_labels_first;
			expected_labels_first[1] = { 1 };

			unordered_map<int, multiset<int>> expected_labels_second;
			expected_labels_second[2] = {2};


			Assert::IsTrue(labels == make_pair(expected_labels_first, expected_labels_second));
		}
		TEST_METHOD(TestLabelsGenerator)
		{
			vector<string> merged_diff = {
				"diff --git a / aaa.txt b / aaa.txt",
				"index 04a0907..afc0d81 100644",
				"-- - a / aaa.txt",
				"++ + b / aaa.txt",
				"@@ - 1, 3 + 1, 3 @@ I AM SUPER GOOD CONTEXT",
				"{",
				"+ return 1;",
				"}",
				"diff --git a / aaa.txt b / aaa.txt",
				"index 04a0907..afc0d81 100644",
				"-- - a / aaa.txt",
				"++ + b / aaa.txt",
				"@@ - 1, 3 + 1, 3 @@ I AM SUPER GOOD CONTEXT",
				"{",
				"+return 2;",
				"}",
			};

			vector<string> splitted_diff_1 = {
				"diff --git a / aaa.txt b / aaa.txt",
				"index 04a0907..afc0d81 100644",
				"-- - a / aaa.txt",
				"++ + b / aaa.txt",
				"@@ - 1, 3 + 1, 3 @@ I AM SUPER GOOD CONTEXT",
				"{",
				"+ return 1;",
				"}",
			};
			vector<string> splitted_diff_2 = {
				"diff --git a / aaa.txt b / aaa.txt",
				"index 04a0907..afc0d81 100644",
				"-- - a / aaa.txt",
				"++ + b / aaa.txt",
				"@@ - 1, 3 + 1, 3 @@ I AM SUPER GOOD CONTEXT",
				"{",
				"+return 2;",
				"}",
			};

			Commit commit;

			Repository first;
			commit.diffs = { merged_diff };
			commit.number = 1;
			first.commits.insert(commit);

			Repository second;
			commit.diffs = { splitted_diff_1 };
			commit.number = 2;
			second.commits.insert(commit);
			commit.diffs = { splitted_diff_2 };
			commit.number = 3;
			second.commits.insert(commit);

			LabelsGenerator labels_generator(first, second);

			auto labels = labels_generator.vertexex_to_labelsids;


			unordered_map<int, multiset<int>> expected_labels_first;
			expected_labels_first[1] = { 1, 3 };

			unordered_map<int, multiset<int>> expected_labels_second;
			expected_labels_second[2] = { 2 };

			Assert::IsTrue(labels == make_pair(expected_labels_first, expected_labels_second));
		}
	};
}
