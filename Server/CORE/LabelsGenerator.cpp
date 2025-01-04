#include "LabelsGenerator.h"

using namespace std;

int LabelsGenerator::GetLabelByID(int label_id) const {
	//todo: is not good, but temporary okey.
	//return label_id;
	return labels_id_to_label.at(label_id);
}

int LabelsGenerator::GenerateLabelId(const StringLabel& string_label) {
	/*
	* Для каждого метки проверяем есть ли у нас уже такая метка
	* если есть то присваем ей номер метки иначе создаем новый номер метки
	* делаем сопостовление id метки и номера
	*/

	++id_counter;
	StringLabel copy = string_label;
	sort(copy.value.begin(), copy.value.end());
	if (string_label_to_label.find(copy) == string_label_to_label.end()) {
		string_label_to_label[copy] = ++label_counter;
	}
	//todo: is not good, but temporary okey.
	//return string_label_to_label[copy];
	labels_id_to_label[id_counter] = string_label_to_label[copy];

	return id_counter;
}

LabelsGenerator::LabelsGenerator(const Repository& first_repository, const Repository& second_repository) {
	//old: сначала тупо на диффах реализовать, оттестить, а потом уже можно реализовать реальные лэйблы
	//каждый коммит содержит файл
	//каждый файл содержит текст
	//каждый текст содержит строки
	//vector<string> - это одна строка метки
	//vector<vector<string>> - это одна метка - получается
	//получается есть сет меток где изначально за строку метки надо взять строку из первого репозитория,
	//а за метку надо взять набор таких строк из коммитов первого репа
	//далее - надо подготовить второй точно такой же сет для второго репа
	//после чего, пока это возможно надо обогащать первый реп метками
	//как первый реп будет уже до конца обогащен нужно будет взять соотвествие меток вершинам первого репа 
	//просто взяв снова сделав 2 сета и опустошать оба от меток с меньшим количеством строк до меток - с большим 
	//и вставляя все это в first_labels, second_labels, с учетом инкапсуляции LabelsGenerator

	vector<vector<vector<string>>> first_repository_files;
	vector<vector<vector<string>>> second_repository_files;
	// получаем список очищенныз ханков без мета информации
	for (const auto& commit : first_repository.commits) {
		first_repository_files.push_back(PrepareClear(commit.diffs));
	}
	for (const auto& commit : second_repository.commits) {
		second_repository_files.push_back(PrepareClear(commit.diffs));
	}
	
	// получаем строки с контекстом
	vector<StringLabel> labels_first_repository = GetStringLabels(first_repository_files);
	vector<StringLabel> labels_second_repository = GetStringLabels(second_repository_files);
	set<StringLabel> all_labels(labels_first_repository.begin(), labels_first_repository.end());

	bool found_label_splitting = 1;
	// пока присутствуют метки которые отсутствуют в сете всех меток
	int cnt = 0;
	while (found_label_splitting) { // а если различий нет?
		found_label_splitting = 0;
		cnt++;
		cout << cnt << "\n";
		for (const StringLabel& label_from_all : all_labels) {
			for (const StringLabel& label_from_second : labels_second_repository) {
				StringLabel common_part = CommonStringsWithContext(label_from_all, label_from_second);
				StringLabel difference_part_one = DifferenceStringsWithContext(label_from_all, label_from_second);
				StringLabel difference_part_two = DifferenceStringsWithContext(label_from_second, label_from_all);
				if (!all_labels.count(common_part) && !common_part.value.empty()) { // общая часть отстутсвует в сете всех меток
					found_label_splitting = 1;
					all_labels.insert(common_part);
				}

				if (!all_labels.count(difference_part_one) && !difference_part_one.value.empty()) { // разница 1 и 2 репозитория отсуствует в списке всех меток
					found_label_splitting = 1;
					all_labels.insert(difference_part_one);
				}

				if (!all_labels.count(difference_part_two) && !difference_part_two.value.empty()) { // разница 2 и 1 репозитория отсутсвует в списке всех меток
					found_label_splitting = 1;
					all_labels.insert(difference_part_two);
					StringLabel tmp = DifferenceStringsWithContext(label_from_second, label_from_all);
				}
				if (found_label_splitting) {
					break;
				}
			}
			if (found_label_splitting) {
				break;
			}
		}
	}

	cout << "label.size()" << all_labels.size() << "\n";
		vector<int> first_repository_commit_numbers = GetCommitNumbers(first_repository);
	vector<int> second_repository_commit_numbers = GetCommitNumbers(second_repository);

	unordered_map<int, multiset<int>> first_labels;
	unordered_map<int, multiset<int>> second_labels;

	for (const StringLabel& string_label : all_labels) { // для всех найденных меток
		if (string_label.value.empty()) {
			continue;
		}

		// добавляем к каждому коммиту 1 репозитория список id метки если она присутсвтует в нем
		for (int i = 0; i < labels_first_repository.size(); ++i) { 
			int commit_number = first_repository_commit_numbers[i];
			while (IsSubset(labels_first_repository[i].value, string_label.value)) { 
				RemoveSubset(labels_first_repository[i].value, string_label.value);
				first_labels[commit_number].insert(GenerateLabelId(string_label));
			}
		}

		// добавляем к каждому коммиту 2 репозитория список id метки если она присутсвтует в нем
		for (int i = 0; i < labels_second_repository.size(); ++i) {
			int commit_number = second_repository_commit_numbers[i];

			while (IsSubset(labels_second_repository[i].value, string_label.value)) {
				RemoveSubset(labels_second_repository[i].value, string_label.value);
				second_labels[commit_number].insert(GenerateLabelId(string_label));
			}
		}
	}

	// возвращаем список  id меток для первого и второго репозитория
	vertexex_to_labelsids = { first_labels , second_labels };
}


LabelsGenerator::LabelsGenerator() {

}


LabelsGenerator::LabelsGenerator(pair<unordered_map<int, multiset<int>>, unordered_map<int, multiset<int>>> vertexex_to_labelsids,
	unordered_map<int, int> labels_id_to_label) {
	this->vertexex_to_labelsids = vertexex_to_labelsids;
	this->labels_id_to_label = labels_id_to_label;
}

/* Получает из текста git diff отдельные ханки без мета информции
*/
HunkStringWithContext LabelsGenerator::PrepareClear(const vector<vector<string>>& commit_diff) {
	//тут еще на ханки разбивается
	vector<vector<string>> result;
	int index = -1;
	bool diff_closed = 1;

	for (auto commit_diff : commit_diff) {
		for (const auto& str : commit_diff) {
			if (str.size() == 0) {
				continue;
			}
			//diff ..
			if (str[0] == 'd') {
				diff_closed = 0;
				++index;
				result.push_back(vector<string>());
				continue;
			}
			//hunk
			if (str[0] == '@') {
				if (diff_closed) {
					++index;
					result.push_back(vector<string>());
					;
				}
				diff_closed = 1;
				string cleared_str;
				int count_sobaka = 0;
				for (const auto& elem : str) {
					if (count_sobaka == 4) {
						cleared_str += elem;
					}
					if (elem == '@') {
						count_sobaka++;
					}
				}
				result[index].push_back(cleared_str);
				continue;
			}
			if (!diff_closed) {
				continue;
			}

			result[index].push_back(str);
			int a = 5;
		}
	}

	return result;
}

/*
* Получает список меток из ханков.
*/
vector<LabelsGenerator::StringLabel> LabelsGenerator::GetStringLabels(const vector<HunkStringWithContext>& repository_hunks) {
	//тут отношение коммит - коммит, так как лэйбл здесь один большой для всего коммита
	vector<StringLabel> result;

	for (const auto& commit : repository_hunks) {
		StringLabel label;
		for (const auto& hunk : commit) {
			for (int i = 0; i < hunk.size(); ++i) {
				StringWithContext string_with_context;
				if (hunk[i][0] == '+' || hunk[i][0] == '-') { // если строка является изменением 
					for (int j = max(i - 3, 0); j <= min(i + 3, ((int) hunk.size()) - 1); ++j) { // то добавляем в строку с контекстом помимо самой строки еще три сверзу и сниху
						string_with_context.push_back(hunk[j]);
					}
					label.value.push_back(string_with_context);
				}
			}
		}
		result.push_back(label);
	}

	return result;
}


/* Возвращает список строк которые содержатся в обоих строковых заметках
*/
LabelsGenerator::StringLabel LabelsGenerator::CommonStringsWithContext(const StringLabel& first, const StringLabel& second) {
	LabelsGenerator::StringLabel result;

	set<StringWithContext> all_strings;

	map<StringWithContext, int> first_map;
	for (const auto& elem : first.value) {
		first_map[elem]++;
		all_strings.insert(elem);
	}
	map<StringWithContext, int> second_map;
	for (const auto& elem : second.value) {
		second_map[elem]++;
		all_strings.insert(elem);
	}

	for (auto elem : all_strings) {
		int count_common = min(first_map[elem], second_map[elem]);
		for (int i = 0; i < count_common; ++i){
			result.value.push_back(elem);
		}
	}
	
	return result;
}

/* Возвращает список строк которые содержатся в первой строковой метке и не содержится во второй
*/
LabelsGenerator::StringLabel LabelsGenerator::DifferenceStringsWithContext(const StringLabel& first, const StringLabel& second) {
	LabelsGenerator::StringLabel result;

	map<StringWithContext, int> first_map;
	for (const auto& elem : first.value) {
		first_map[elem]++;
	}
	map<StringWithContext, int> second_map;
	for (const auto& elem : second.value) {
		second_map[elem]++;
	}

	for (auto elem : first.value) { //
		second_map[elem]--;
		if(second_map[elem] < 0) {
			result.value.push_back(elem);
		}
	}

	return result;
}

/* Проверяет содержит ли значение подмножество
* 
*/
bool LabelsGenerator::IsSubset(const vector<vector<string>>& value, const vector<vector<string>>& subset) {
	multiset<vector<string>> value_set(value.begin(), value.end());

	for (auto subset_item : subset) {
		if (value_set.count(subset_item)) {
			value_set.erase(value_set.find(subset_item));
		}
		else {
			return false;
		}
	}

	return true;
}

/* Удаление подмножества
* @param value - значение откуда удалаляется множество
* @param subset - множество
*/
void LabelsGenerator::RemoveSubset(vector<vector<string>>& value, const vector<vector<string>>& subset) {
	multiset<vector<string>> value_set(value.begin(), value.end());

	for (auto subset_item : subset) {
		if (value_set.count(subset_item)) {
			value_set.erase(value_set.find(subset_item));
		}
	}

	value = vector<vector<string>>(value_set.begin(), value_set.end());
	return;
}


vector<int> LabelsGenerator::GetCommitNumbers(const Repository& repository) {
	vector<int> result;

	for (const auto& commit : repository.commits) {
		result.push_back(commit.number);
	}

	return result;
}
