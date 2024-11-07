#include "pch.h"
#include "CppUnitTest.h"
#include "../CORE//ComparisonGraph.h"

using namespace Microsoft::VisualStudio::CppUnitTestFramework;

namespace TEST
{
	TEST_CLASS(TEST)
	{
	public:

		/*TEST_METHOD(TestMethod2)
		{
			//тут экспешн, так как нет генератора
			/*vector<pair<int, int>> list_of_edges_actual = {{1, 3}, {3, 2}};
			vector<pair<int, int>> list_of_edegs_expected = { {1, 2}, {2, 3} };
			Graph expected(list_of_edegs_expected);
			Graph actual(list_of_edges_actual);
			auto common_subraf = Graph::BiggestCommonSubgraph(list_of_edegs_expected, list_of_edges_actual);

			ComparisonGraph comparison_graph(expected, actual, common_subraf);

			comparison_graph.GetVertexes();

			Assert::IsTrue(false);
			//Assert::IsTrue(common_subraf.vertexes == expected_vertexes);
		}*/
	};
}
