#include "pch.h"
#include "CppUnitTest.h"
#include "../CORE/ComparisonRepositoryGenerator.h"


using namespace Microsoft::VisualStudio::CppUnitTestFramework;

namespace TEST_MADE_LIST_OF_EDGES_FROM_LIST_OF_EDGES_COMMIT
{
	TEST_CLASS(TEST_MADE_LIST_OF_EDGES_FROM_LIST_OF_EDGES_COMMIT)
	{
	public:

		TEST_METHOD(CommitGraphIsBamboo)
		{
			ComparisonRepositoryGenerator comparisonRepositoryGenerator;

			vector<pair<Commit, Commit>> list_of_edges_diff =
			{
				make_pair(Commit("init commit"), Commit("1 commit")),
				make_pair(Commit("1 commit"), Commit("2 commit")),
				make_pair(Commit("2 commit"), Commit("3 commit"))
			};

			vector<pair<int, int>> expectedEdges = { {1, 2}, {2, 3}, {3, 4} };
			vector<pair<int, int>> actualEdges = 
				comparisonRepositoryGenerator.MadeListOfEdgesFromListOfEdgesCommit(list_of_edges_diff);

			Assert::IsTrue(expectedEdges == actualEdges);
		}


		TEST_METHOD(CommitGraphIsEmpty)
		{
			ComparisonRepositoryGenerator comparisonRepositoryGenerator;

			vector<pair<Commit, Commit>> list_of_edges_diff =
			{
			};

			vector<pair<int, int>> expectedEdges = {};
			vector<pair<int, int>> actualEdges =
				comparisonRepositoryGenerator.MadeListOfEdgesFromListOfEdgesCommit(list_of_edges_diff);

			Assert::IsTrue(expectedEdges == actualEdges);
		}

		TEST_METHOD(CommitGraphHasTwoBranch)
		{
			ComparisonRepositoryGenerator comparisonRepositoryGenerator;

			vector<pair<Commit, Commit>> list_of_edges_diff =
			{
				make_pair(Commit("init commit"), Commit("1 commit")),
				make_pair(Commit("1 commit"), Commit("2 commit")),
				make_pair(Commit("1 commit"), Commit("other-branch-commit-1")),
				make_pair(Commit("other-branch-commit-1"), Commit("other-branch-commit-2")),
				make_pair(Commit("other-branch-commit-2"), Commit("other-branch-commit-3")),
				make_pair(Commit("2 commit"), Commit("3 commit"))
			};

			vector<pair<int, int>> expectedEdges = { {1, 2}, {2, 3}, {2, 4}, {4, 5}, {5, 6 }, {3, 7} };
			vector<pair<int, int>> actualEdges =
				comparisonRepositoryGenerator.MadeListOfEdgesFromListOfEdgesCommit(list_of_edges_diff);

			Assert::IsTrue(expectedEdges == actualEdges);
		}

		TEST_METHOD(CommitGraphHasMergeComit)
		{
			ComparisonRepositoryGenerator comparisonRepositoryGenerator;

			vector<pair<Commit, Commit>> list_of_edges_diff =
			{
				make_pair(Commit("init commit"), Commit("1 commit")),
				make_pair(Commit("1 commit"), Commit("2 commit")),
				make_pair(Commit("1 commit"), Commit("other-branch-commit-1")),
				make_pair(Commit("other-branch-commit-1"), Commit("other-branch-commit-2")),
				make_pair(Commit("other-branch-commit-2"), Commit("other-branch-commit-3")),
				make_pair(Commit("2 commit"), Commit("3 commit")),
				make_pair(Commit("3 commit"), Commit("merge-commit")),
				make_pair(Commit("other-branch-commit-3"), Commit("merge-commit")),
			};

			vector<pair<int, int>> expectedEdges = { {1, 2}, {2, 3}, {2, 4}, {4, 5}, {5, 6 }, {3, 7}, {7, 8}, {6, 8} };
			vector<pair<int, int>> actualEdges =
				comparisonRepositoryGenerator.MadeListOfEdgesFromListOfEdgesCommit(list_of_edges_diff);

			Assert::IsTrue(expectedEdges == actualEdges);
		}
	};
}