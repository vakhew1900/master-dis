#pragma once
#include <unordered_set>
#include <unordered_map>
#include <map>
#include <vector>
#include <string>
#pragma once
#include <algorithm>
#include "Repository.h"

using namespace std;

using StringWithContext = vector<string>;
using HunkStringWithContext = vector<vector<string>>;

class LabelsGenerator
{
public:
	struct StringLabel {
		vector<StringWithContext> value;

		friend bool operator < (const StringLabel& a, const StringLabel& b) {
			if (a.value.size() != b.value.size()) {
				return a.value.size() < b.value.size();
			}

			return a.value < b.value;
		}

		bool operator == (const StringLabel& other) {
			return this->value == other.value;
		}
	};

	LabelsGenerator();
	//mock for tests
	LabelsGenerator(pair<unordered_map<int, multiset<int>>, unordered_map<int, multiset<int>>> vertexex_to_labelsids,
					unordered_map<int, int> labels_id_to_label);
	LabelsGenerator(const Repository& first_repository, const Repository& second_repository);
	int GetLabelByID(int label_id) const;
	int GenerateLabelId(const StringLabel& string_label);
	//pair<unordered_map<int, multiset<int>>, unordered_map<int, multiset<int>>> GenerateLabels(const Repository& first_repository, const Repository& second_repository);

	//todo: not ok
	pair<unordered_map<int, multiset<int>>, unordered_map<int, multiset<int>>> vertexex_to_labelsids;

private:
	int id_counter = 0;
	int label_counter = 0;
	unordered_map<int, int> labels_id_to_label;

	static HunkStringWithContext PrepareClear(const vector<vector<string>>& commit_diff);
	static vector<StringLabel> GetStringLabels(const vector<HunkStringWithContext>& repository_hunks);
	static StringLabel CommonStringsWithContext(const StringLabel& first, const StringLabel& second);
	static StringLabel DifferenceStringsWithContext(const StringLabel& first, const StringLabel& second);
	static bool IsSubset(const vector<vector<string>>& value, const vector<vector<string>>& subset);
	static void RemoveSubset(vector<vector<string>>& value, const vector<vector<string>>& subset);
	static vector<int> GetCommitNumbers(const Repository& repository);

	map<StringLabel, int> string_label_to_label;
};

