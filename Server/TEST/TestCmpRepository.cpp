#include "pch.h"


#include "CppUnitTest.h"
#include "../CORE/ComparisonRepositoryGenerator.h"

using namespace Microsoft::VisualStudio::CppUnitTestFramework;

namespace TEST_CMP_REPOSITORIES
{
	TEST_CLASS(TEST_CMP_REPOSITORIES)
	{
	public:



		TEST_METHOD(RepositoryIsOneCommitOneCommonCommitWithOneDiffs)
		{

			vector<string> firstDiff = {
				"diff",
				"@@ - 1, 3 + 1, 3 @@ I AM SUPER GOOD CONTEXT",
				"+label"
			};
			
			Commit commit = Commit("actual hash", { firstDiff });
			Commit commit2 = Commit("expected hash 2", { firstDiff });

			Repository actualRepository({ commit }, {});
			Repository expectedRepository({ commit2 }, {});

			ComparisonRepositoryGenerator generator;
			ErrorCommitRepository actualErrorCommitRepository = generator.cmp(expectedRepository, actualRepository);
			ErrorCommitRepository expectedErrorCommitRepository;

			Assert::IsTrue(expectedErrorCommitRepository == actualErrorCommitRepository);
		}

		TEST_METHOD(RepositoryIsOneCommitDifferentsCommitWithOneDiffs)
		{

			vector<string> firstDiff = {
				"diff",
				"@@ - 1, 3 + 1, 3 @@ I AM SUPER GOOD CONTEXT",
				"+label"
			};

			vector<string> secondDiff = {
				"diff",
				"@@ - 1, 3 + 1, 3 @@ I AM SUPER GOOD CONTEXT",
				"+ other label"
			};

			Commit commit = Commit("actual hash", { firstDiff });
			Commit commit2 = Commit("expected hash 2", { secondDiff });

			Repository actualRepository({ commit }, {});
			Repository expectedRepository({ commit2 }, {});

			ComparisonRepositoryGenerator generator;
			ErrorCommitRepository actualErrorCommitRepository = generator.cmp(expectedRepository, actualRepository);
			ErrorCommitRepository expectedErrorCommitRepository({}, { commit2 }, { commit });

			Assert::IsTrue(expectedErrorCommitRepository == actualErrorCommitRepository);
		}

		TEST_METHOD(OneCommonCommitWithSeveralDiffs)
		{

			vector<string> firstDiff = {
				"diff",
				"@@ - 1, 3 + 1, 3 @@ I AM SUPER GOOD CONTEXT",
				"+label"
			};

			vector<string> secondDiff = {
				"diff",
				"@@ - 1, 3 + 1, 3 @@ I AM SUPER GOOD CONTEXT",
				"+ other label"
			};

			Commit commit = Commit("actual hash", { firstDiff,  secondDiff });
			Commit commit2 = Commit("expected hash 2", { firstDiff, secondDiff });

			Repository actualRepository({ commit }, {});
			Repository expectedRepository({ commit2 }, {});

			ComparisonRepositoryGenerator generator;
			ErrorCommitRepository actualErrorCommitRepository = generator.cmp(expectedRepository, actualRepository);
			ErrorCommitRepository expectedErrorCommitRepository;

			Assert::IsTrue(expectedErrorCommitRepository == actualErrorCommitRepository);
		}


		TEST_METHOD(OneDifferentCommitWithSeveralDiffs)
		{

			vector<string> firstDiff = {
				"diff",
				"@@ - 1, 3 + 1, 3 @@ I AM SUPER GOOD CONTEXT",
				"+label"
			};

			vector<string> secondDiff = {
				"diff",
				"@@ - 1, 3 + 1, 3 @@ I AM SUPER GOOD CONTEXT",
				"+ other label"
			};

			vector<string> thirdDiff = {
			"diff --git a / aaa.txt b / aaa.txt",
			"index 04a0907..afc0d81 100644",
			"-- - a / aaa.txt",
			"++ + b / aaa.txt",
			"@@ - 1, 3 + 1, 3 @@ I AM SUPER GOOD CONTEXT",
			"{",
			"+ return 1;",
			"}",
			};
			
			Commit commit = Commit("actual hash", { firstDiff,  secondDiff });
			Commit commit2 = Commit("expected hash 2", { firstDiff, thirdDiff });

			{
				Repository actualRepository({ commit }, {});
				Repository expectedRepository({ commit2 }, {});

				ComparisonRepositoryGenerator generator;
				ErrorCommitRepository actualErrorCommitRepository = generator.cmp(expectedRepository, actualRepository);

				ErrorCommitRepository expectedErrorCommitRepository({ commit }, {}, {});
				Assert::IsTrue(expectedErrorCommitRepository == actualErrorCommitRepository);
			}

			{
				Repository actualRepository({ commit2 }, {});
				Repository expectedRepository({ commit }, {});

				ComparisonRepositoryGenerator generator;
				ErrorCommitRepository actualErrorCommitRepository = generator.cmp(expectedRepository, actualRepository);

				ErrorCommitRepository expectedErrorCommitRepository({ commit2 }, {}, {});
				Assert::IsTrue(expectedErrorCommitRepository == actualErrorCommitRepository);
			}
		}

	};

}