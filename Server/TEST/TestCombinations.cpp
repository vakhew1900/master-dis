 #include "pch.h"
#include "CppUnitTest.h"
#include "../CORE//ComparisonGraph.h"

using namespace Microsoft::VisualStudio::CppUnitTestFramework;

namespace TEST_COMBINATIONS
{
	TEST_CLASS(TEST_COMBINATIONS)
	{
	public:

		TEST_METHOD(ZeroOnZero)
		{
			vector<multiset<int>> combinations;
			Graph::Combinations(0, 0, combinations);
			Assert::IsTrue(combinations == vector<multiset<int>>(1)); //really good
		}


		TEST_METHOD(ZeroOnMany)
		{
			vector<multiset<int>> combinations;
			Graph::Combinations(4, 0, combinations);
			Assert::IsTrue(combinations == vector<multiset<int>>{{}});
		}


		TEST_METHOD(OneOnOne)
		{
			vector<multiset<int>> combinations;
			Graph::Combinations(1, 1, combinations);
			Assert::IsTrue(combinations == vector<multiset<int>>{{0}});
		}


		TEST_METHOD(OneOnMany)
		{
			vector<multiset<int>> combinations;
			Graph::Combinations(4, 1, combinations);
			Assert::IsTrue(combinations == vector<multiset<int>>{{0}, {1}, { 2 }, { 3 }});
		}


		TEST_METHOD(SeveralOnMany)
		{
			vector<multiset<int>> combinations;
			Graph::Combinations(4, 2, combinations);
			Assert::IsTrue(combinations == vector<multiset<int>>{{0, 1}, { 0, 2 }, { 0, 3 }, { 1, 2 }, { 1, 3 }, { 2, 3 }});
		}

		TEST_METHOD(AllOnMany)
		{
			vector<multiset<int>> combinations;
			Graph::Combinations(4, 4, combinations);
			Assert::IsTrue(combinations == vector<multiset<int>>{{0, 1 , 2 , 3 }});
		}

		TEST_METHOD(KBiggerN)
		{
			vector<multiset<int>> combinations;
			Graph::Combinations(4, 5, combinations);
			Assert::IsTrue(combinations == vector<multiset<int>>{});
		}

		TEST_METHOD(NegativeK)
		{
			vector<multiset<int>> combinations;
			Graph::Combinations(4, -1, combinations);
			Assert::IsTrue(combinations == vector<multiset<int>>{});
		}
	};
}
