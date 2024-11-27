#include "pch.h"


#include "CppUnitTest.h"
#include "../CORE/ComparisonRepositoryGenerator.h"

using namespace Microsoft::VisualStudio::CppUnitTestFramework;

namespace TEST_CMP_REPOSITORIES
{
	TEST_CLASS(TEST_CMP_REPOSITORIES)
	{
	public:

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

		vector<string> fourthDiff = {
			"diff --git a / aaa.txt b / aaa.txt",
		"index 04a0907..afc0d81 100644",
		"-- - a / aaa.txt",
		"++ + b / aaa.txt",
		"@@ - 1, 3 + 1, 3 @@ I AM SUPER GOOD CONTEXT",
		"{",
		"+ return 2;",
		"}",
		};

		TEST_METHOD(RepositoryIsOneCommitOneCommonCommitWithOneDiffs)
		{

			
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


		TEST_METHOD(RepositoryIsBambooAndCommitsHaveOneDiffs)
		{

			Commit commit = Commit("actual hash", { firstDiff}, 1);
			Commit commit2 = Commit("actual hash 2", { secondDiff}, 2);
			Commit commit3 = Commit("actual hash 3", { thirdDiff }, 3);

			Commit expectedCommit = Commit("expected hash", { firstDiff }, 1);
			Commit expectedCommit2 = Commit("expected hash 2", { secondDiff }, 2);
			Commit expectedCommit3 = Commit("expected hash 3", { thirdDiff }, 3);

			{
				Repository actualRepository({ commit,commit2, commit3 }, { {1, 2}, {2, 3} });
				Repository expectedRepository({ expectedCommit, expectedCommit, commit3 }, { {1, 2}, { 2, 3 } });

				ComparisonRepositoryGenerator generator;
				ErrorCommitRepository actualErrorCommitRepository = generator.cmp(expectedRepository, actualRepository);

				ErrorCommitRepository expectedErrorCommitRepository({}, {}, {});
				Assert::IsTrue(expectedErrorCommitRepository == actualErrorCommitRepository);
			}
		}

		TEST_METHOD(RepositoryIsBambooAndCommitsHaveOneDiffsRepositoryHasDifferentStructure) // показать ОА
		{

			Commit commit = Commit("actual hash", { firstDiff }, 1);
			Commit commit2 = Commit("actual hash 2", { secondDiff }, 2);
			Commit commit3 = Commit("actual hash 3", { thirdDiff }, 3);

			Commit expectedCommit = Commit("expected hash", { firstDiff }, 1);
			Commit expectedCommit2 = Commit("expected hash 2", { secondDiff }, 2);
			Commit expectedCommit3 = Commit("expected hash 3", { thirdDiff }, 3);

			{
				Repository actualRepository({ commit,commit2, commit3 }, { {1, 2}, {2, 3} });
				Repository expectedRepository({ expectedCommit, expectedCommit2, expectedCommit3 }, { {1, 3}, {3, 2} });

				ComparisonRepositoryGenerator generator;
				ErrorCommitRepository actualErrorCommitRepository = generator.cmp(expectedRepository, actualRepository);

				ErrorCommitRepository expectedErrorCommitRepository({}, {expectedCommit3}, {commit2});
				Assert::IsTrue(expectedErrorCommitRepository == actualErrorCommitRepository);
			}
		}

		TEST_METHOD(RepositoryIsBambooAndCommitsHaveOneDiffsRepositoryHasDifferentCommit) // показать ОА
		{

			Commit commit = Commit("actual hash", { firstDiff }, 1);
			Commit commit2 = Commit("actual hash 2", { secondDiff }, 2);
			Commit commit3 = Commit("actual hash 3", { thirdDiff }, 3);
			Commit commit4 = Commit("actual hash 4", { fourthDiff }, 4);

			Commit expectedCommit = Commit("expected hash", { firstDiff }, 1);
			Commit expectedCommit2 = Commit("expected hash 2", { secondDiff }, 2);
			Commit expectedCommit3 = Commit("expected hash 3", { thirdDiff }, 3);
			Commit expectedCommit4 = Commit("expected hash 4", { fourthDiff }, 4);

			// Замена последнего элемента
			{
				Repository actualRepository({ commit,commit2, commit3 }, { {1, 2}, {2, 3} });
				Repository expectedRepository({ expectedCommit, expectedCommit2, expectedCommit4 }, { {1, 2}, {2, 4} });

				ComparisonRepositoryGenerator generator;
				ErrorCommitRepository actualErrorCommitRepository = generator.cmp(expectedRepository, actualRepository);

				ErrorCommitRepository expectedErrorCommitRepository({}, { expectedCommit4 }, { commit3 });
				Assert::IsTrue(expectedErrorCommitRepository == actualErrorCommitRepository);
			}

			// Замена промежуточного коммита
			{
				Repository actualRepository({ commit,commit2, commit3 }, { {1, 2}, {2, 3} });
				Repository expectedRepository({ expectedCommit, expectedCommit4, expectedCommit3 }, { {1, 4}, {4, 3} });

				ComparisonRepositoryGenerator generator;
				ErrorCommitRepository actualErrorCommitRepository = generator.cmp(expectedRepository, actualRepository);

				ErrorCommitRepository expectedErrorCommitRepository({}, { expectedCommit4 }, { commit2 });
				Assert::IsTrue(expectedErrorCommitRepository == actualErrorCommitRepository);
			}


			// Замена первого элемента
			{
				Repository actualRepository({ commit,commit2, commit3 }, { {1, 2}, {2, 3} });
				Repository expectedRepository({ expectedCommit4, expectedCommit2, expectedCommit3 }, { {4, 2}, {2, 3} });

				ComparisonRepositoryGenerator generator;
				ErrorCommitRepository actualErrorCommitRepository = generator.cmp(expectedRepository, actualRepository);

				ErrorCommitRepository expectedErrorCommitRepository({}, { expectedCommit4 }, { commit });
				Assert::IsTrue(expectedErrorCommitRepository == actualErrorCommitRepository);
			}
		}


		TEST_METHOD(RepositoryIsBambooAndCommitsHaveOneDiffMissCommit) // показать ОА
		{

			Commit commit = Commit("actual hash", { firstDiff }, 1);
			Commit commit2 = Commit("actual hash 2", { secondDiff }, 2);
			Commit commit3 = Commit("actual hash 3", { thirdDiff }, 3);
			Commit commit4 = Commit("actual hash 4", { fourthDiff }, 4);

			Commit expectedCommit = Commit("expected hash", { firstDiff }, 1);
			Commit expectedCommit2 = Commit("expected hash 2", { secondDiff }, 2);
			Commit expectedCommit3 = Commit("expected hash 3", { thirdDiff }, 3);
			Commit expectedCommit4 = Commit("expected hash 4", { fourthDiff }, 4);

			// Пропуск последнего элемента
			{
				Repository actualRepository({ commit,commit2 }, { {1, 2}, });
				Repository expectedRepository({ expectedCommit, expectedCommit2, expectedCommit3 }, { {1, 2}, {2, 3} });

				ComparisonRepositoryGenerator generator;
				ErrorCommitRepository actualErrorCommitRepository = generator.cmp(expectedRepository, actualRepository);

				ErrorCommitRepository expectedErrorCommitRepository({}, { expectedCommit3 }, {});
				Assert::IsTrue(expectedErrorCommitRepository == actualErrorCommitRepository);
			}

			// Пропуск промежуточного коммита
			{
				Repository actualRepository({ commit, commit3 }, { {1, 3}, });
				Repository expectedRepository({ expectedCommit, expectedCommit2, expectedCommit3 }, { {1, 2}, {2, 3} });

				ComparisonRepositoryGenerator generator;
				ErrorCommitRepository actualErrorCommitRepository = generator.cmp(expectedRepository, actualRepository);

				ErrorCommitRepository expectedErrorCommitRepository({}, { expectedCommit2 }, {});
				Assert::IsTrue(expectedErrorCommitRepository == actualErrorCommitRepository);
			}


			// Пропуск первого элемента
			{
				Repository actualRepository({ commit2, commit3 }, { {2, 3}, });
				Repository expectedRepository({ expectedCommit, expectedCommit2, expectedCommit3 }, { {1, 2}, {2, 3} });

				ComparisonRepositoryGenerator generator;
				ErrorCommitRepository actualErrorCommitRepository = generator.cmp(expectedRepository, actualRepository);

				ErrorCommitRepository expectedErrorCommitRepository({}, { expectedCommit }, {});
				Assert::IsTrue(expectedErrorCommitRepository == actualErrorCommitRepository);
			}
		}

		TEST_METHOD(RepositoryIsBambooAndCommitsHaveOneDiffExtraCommit) // показать ОА
		{

			Commit commit = Commit("actual hash", { firstDiff }, 1);
			Commit commit2 = Commit("actual hash 2", { secondDiff }, 2);
			Commit commit3 = Commit("actual hash 3", { thirdDiff }, 3);
			Commit commit4 = Commit("actual hash 4", { fourthDiff }, 4);

			Commit expectedCommit = Commit("expected hash", { firstDiff }, 1);
			Commit expectedCommit2 = Commit("expected hash 2", { secondDiff }, 2);
			Commit expectedCommit3 = Commit("expected hash 3", { thirdDiff }, 3);
			Commit expectedCommit4 = Commit("expected hash 4", { fourthDiff }, 4);

			// Пропуск последнего элемента
			{
				Repository actualRepository({ commit,commit2, commit3 }, { {1, 2}, {2, 3} });
				Repository expectedRepository({ expectedCommit, expectedCommit2 }, { {1, 2}});

				ComparisonRepositoryGenerator generator;
				ErrorCommitRepository actualErrorCommitRepository = generator.cmp(expectedRepository, actualRepository);

				ErrorCommitRepository expectedErrorCommitRepository({}, {}, {commit3});
				Assert::IsTrue(expectedErrorCommitRepository == actualErrorCommitRepository);
			}

			// Пропуск промежуточного коммита
			{
				Repository actualRepository({ commit,commit2, commit3 }, { {1, 2}, {2, 3} });
				Repository expectedRepository({ expectedCommit, expectedCommit3 }, { {1, 3} });

				ComparisonRepositoryGenerator generator;
				ErrorCommitRepository actualErrorCommitRepository = generator.cmp(expectedRepository, actualRepository);

				ErrorCommitRepository expectedErrorCommitRepository({}, {}, { commit2});
				Assert::IsTrue(expectedErrorCommitRepository == actualErrorCommitRepository);
			}


			// Пропуск первого элемента
			{
				Repository actualRepository({ commit,commit2, commit3 }, { {1, 2}, {2, 3} });
				Repository expectedRepository({ expectedCommit2, expectedCommit3 }, { {2, 3} });

				ComparisonRepositoryGenerator generator;
				ErrorCommitRepository actualErrorCommitRepository = generator.cmp(expectedRepository, actualRepository);

				ErrorCommitRepository expectedErrorCommitRepository({}, {}, { expectedCommit });
				Assert::IsTrue(expectedErrorCommitRepository == actualErrorCommitRepository);
			}
		}

	};

}