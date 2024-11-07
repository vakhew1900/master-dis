#include "pch.h"
#include "CppUnitTest.h"
#include "../CORE/GitLogHandler.h"

using namespace Microsoft::VisualStudio::CppUnitTestFramework;

namespace TEST_GENERATE_LIST_OF_EDGES
{
	TEST_CLASS(TEST_GENERATE_LIST_OF_EDGES)
	{
	public:

		TEST_METHOD(NotVertex)
		{
			GitLogHandler git_log(vector<string>({}));

			Assert::IsTrue(git_log.GenerateListOfEdges() == vector<pair<string, string>>());
		}

		TEST_METHOD(OneVertex)
		{
			GitLogHandler git_log(vector<string>({ "* commit 3af748da299c05b54759199bf243ff190fd94840", "a"}));

			Assert::IsTrue(git_log.GenerateListOfEdges() == vector<pair<string, string>>());
		}


		TEST_METHOD(SeveralVertexes)
		{
			GitLogHandler git_log(vector<string>({
				"* commit h1",
				"* commit h2", 
				"a" 
				}));

			Assert::IsTrue(git_log.GenerateListOfEdges() == vector<pair<string, string>>{{"h2", "h1"}});
		}

		TEST_METHOD(SeveralBranches)
		{
			GitLogHandler git_log(vector<string>({
				"* commit h1",
				"* commit h2",
				"|\\",
				"| * commit h3",
				"* commit h4",
				"aaaaaaaaaaaaaaaaaa",
				}));

			Assert::IsTrue(git_log.GenerateListOfEdges() == vector<pair<string, string>>{{"h4", "h2"}, {"h3", "h2"}, {"h2", "h1"}});
		}

		TEST_METHOD(Merge)
		{
			GitLogHandler git_log(vector<string>({
				"* commit h1",
				"* commit h2",
				"|\\",
				"| * commit h3",
				"* | commit h4",
				"|/ ",
				"* commit h5",
				"aaaaaaaaaaaaaaaaaa",
				}));

			Assert::IsTrue(git_log.GenerateListOfEdges() == vector<pair<string, string>>{{"h5", "h3"}, {"h5", "h4"}, {"h4", "h2"}, {"h3", "h2"}, {"h2", "h1"}});
		}


		TEST_METHOD(MultiTest)
		{
			GitLogHandler git_log(vector<string>({
				"* commit h1",
				"* commit h2",
				"|\\",
				"| * commit h3",
				"| |\\",
				"| | * commit h6",
				"* | commit h4",
				"|/ ",
				"* commit h5",
				"aaaaaaaaaaaaaaaaaa",
				}));

			Assert::IsTrue(git_log.GenerateListOfEdges() == vector<pair<string, string>>{{"h5", "h3"}, { "h5", "h4" }, { "h4", "h2" }, {"h6", "h3"}, {"h3", "h2"}, {"h2", "h1"}});
		}
	};
}
