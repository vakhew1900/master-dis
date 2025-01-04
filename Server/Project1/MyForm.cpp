#include "MyForm.h"
#include "../CORE/OutputGraph.h"
#include "../CORE/ComparisonRepositoryGenerator.h"
#include <msclr/marshal_cppstd.h>
#using <System.dll>
#include <msclr/marshal.h>
#include <string.h>
#include <thread>

using namespace System;
using namespace System::Windows::Forms;
using namespace msclr::interop;
using namespace System::Runtime::InteropServices;

void renumerate(set<Commit>& commits, set<pair<int, int>>& edges,
	map<int, ComparisonRepository::State>& commit_state,
	map<pair<int, int>, ComparisonRepository::State>& edge_state
) {

	while (true) {
		bool swapped = 0;
		for (auto elem : edges) {
			if (elem.first > elem.second) {
				cout << "elem: " << elem.first << " " << elem.second << endl;
				cout << endl;
				cout << endl;
				cout << endl;
				for (auto elem : edge_state) {
					cout << "elem1: " << elem.first.first << " " << elem.first.second << endl;
					cout << "elem2: " << elem.second << endl;
				}
				cout << endl;
				cout << endl;
				cout << endl;

				auto copy_edges = edges;
				auto copy_edge_state = edge_state;
				edge_state.clear();
				for (auto& edge : copy_edges) {
					cout << "elem: " << elem.first << " " << elem.second << endl;
					auto new_edge = edge;
					if (new_edge.first == elem.first) {
						new_edge.first = elem.second;
					}
					else if (new_edge.first == elem.second) {
						new_edge.first = elem.first;
					}
					if (new_edge.second == elem.first) {
						new_edge.second = elem.second;
					}
					else if (new_edge.second == elem.second) {
						new_edge.second = elem.first;
					}
					edge_state[new_edge] = copy_edge_state[edge];
					edges.erase(edge);
					edges.insert(new_edge);
				}

				for (auto& elem : edge_state) {
					cout << "elem1: " << elem.first.first << " " << elem.first.second << endl;
					cout << "elem2: " << elem.second << endl;
				}
				cout << endl;
				cout << endl;
				cout << endl;

				auto copy_commits = commits;
				for (auto &commit : copy_commits) {
					auto new_commit = commit;
					if (commit.number == elem.first) {
						new_commit.number = elem.second;
					}
					if (commit.number == elem.second) {
						new_commit.number = elem.first;
					}
					commits.erase(commit);
					commits.insert(new_commit);
				}

				swap(commit_state[elem.first], commit_state[elem.second]);

				swapped = 1;
				break;
			}
		}
		if (!swapped) break;
	}
}

void renumerate(set<Commit>& commits, set<pair<int, int>>& edges,
	vector<pair<int, ComparisonRepository::State>>& commit_state
) {

	while (true) {
		bool swapped = 0;
		for (auto elem : edges) {
			if (elem.first > elem.second) {
				cout << "elem: " << elem.first << " " << elem.second << endl;
				cout << endl;
				cout << endl;
				cout << endl;
				/*for (auto elem : edge_state) {
					cout << "elem1: " << elem.first.first << " " << elem.first.second << endl;
					cout << "elem2: " << elem.second << endl;
				}*/
				cout << endl;
				cout << endl;
				cout << endl;

				auto copy_edges = edges;
				//auto copy_edge_state = edge_state;
				//edge_state.clear();
				for (auto edge : copy_edges) {
					cout << "elem: " << elem.first << " " << elem.second << endl;
					auto new_edge = edge;
					if (new_edge.first == elem.first) {
						new_edge.first = elem.second;
					}
					else if (new_edge.first == elem.second) {
						new_edge.first = elem.first;
					}
					if (new_edge.second == elem.first) {
						new_edge.second = elem.second;
					}
					else if (new_edge.second == elem.second) {
						new_edge.second = elem.first;
					}
					//edge_state[new_edge] = copy_edge_state[edge];
					edges.erase(edge);
					edges.insert(new_edge);
				}

				/*for (auto elem : edge_state) {
					cout << "elem1: " << elem.first.first << " " << elem.first.second << endl;
					cout << "elem2: " << elem.second << endl;
				}*/
				cout << endl;
				cout << endl;
				cout << endl;

				auto copy_commits = commits;
				for (auto commit : copy_commits) {
					auto new_commit = commit;
					if (commit.number == elem.first) {
						new_commit.number = elem.second;
					}
					if (commit.number == elem.second) {
						new_commit.number = elem.first;
					}
					commits.erase(commit);
					commits.insert(new_commit);
				}

				swap(commit_state[elem.first], commit_state[elem.second]);

				swapped = 1;
				break;
			}
		}
		if (!swapped) break;
	}
}

System::Void Project1::MyForm::MyForm_Click(System::Object^ sender, System::EventArgs^ e) {
	if (first_click) {
		ComparisonRepositoryGenerator generator;
		//generator.cmp();
		auto comparsion_repository = generator.GenerateComparsionRepository();

		renumerate(comparsion_repository.commits, comparsion_repository.edges, comparsion_repository.commit_number_to_state);

		Graphics^ g = Graphics::FromHwnd(this->Handle);
		std::cout << (MousePosition.X);
		MousePosition.Y;

		map<int, int> number_delta;

		// todo для зеленых вершин нужны ренумерация для планарности
		// todo рёбер нужно научиться выводить его красным, если ребро надо удалить 
		// и зеленым, если оставить

		int commits_size = comparsion_repository.commits.size();

		for (auto vertex_to_state : comparsion_repository.commit_number_to_state) {
			ComparisonRepository::State state = vertex_to_state.second;
			Pen^ pen = (state == ComparisonRepository::State::NeedDelete
				? Pens::Red
				: (state == ComparisonRepository::State::NeedAdd ? Pens::Green : Pens::Black));

			auto vertex = comparsion_repository.GetCommitByNumber(vertex_to_state.first);
			int delta = vertex.delta;
			if (state == ComparisonRepository::State::NeedDelete) {
				delta = -delta;
				delta -= 20;
			}
			if (state == ComparisonRepository::State::NeedAdd) {
				delta += 20;
			}
			number_delta[vertex.number] = 100 + delta;
			g->DrawEllipse(pen, 100 + delta, (commits_size - vertex.number) * 20, 10, 10);

			int pixel = (commits_size - vertex.number);

			if (vertex.command_for_check_diff.size() > 15) {
				vertex.command_for_check_diff.erase(vertex.command_for_check_diff.size() - 12);
				cout << vertex.command_for_check_diff << endl;
			}

			System::String^ sys_str = marshal_as<System::String^>(vertex.command_for_check_diff);

			pixels_to_vertex[pixel] = sys_str;

			FontFamily fontFamily(L"Times New Roman");
			Brush^ blackBrush = gcnew SolidBrush(Color::Black);
			cout << endl;
			cout << (commits_size - vertex.number) << endl;
			RectangleF rect(200, (commits_size - vertex.number) * 20, 1000, 1000);
			String^ str = gcnew String(vertex.message.c_str());

			g->DrawString(str, DefaultFont, blackBrush, rect);

			g->DrawLine(Pens::Black, 0, (commits_size - vertex.number) * 20 + 15, 2000, (commits_size - vertex.number) * 20 + 15);
			g->DrawLine(Pens::Black, 0, (commits_size - vertex.number) * 20 + 15, 2000, (commits_size - vertex.number) * 20 + 15);
		}

		for (auto& edge : comparsion_repository.edges) {
			//ComparisonRepository::State state;//comparsion_repository.edge_state[edge];
			//Pen^ pen = (state == ComparisonRepository::State::NeedDelete
			//	? Pens::Red
			//	: (state == ComparisonRepository::State::NeedAdd ? Pens::Green : Pens::Black));

			Pen^ pen = Pens::Black;
			g->DrawLine(pen, number_delta[edge.first] + 5, (commits_size - edge.first) * 20, number_delta[edge.second] + 5, (commits_size - edge.second) * 20 + 10);
		}

		first_click = 0;
	}
	else {
		int pixel = MousePosition.Y - this->Location.Y - 26;
		cout << pixel << endl;
		auto aa = this->Location;

		if (pixels_to_vertex.ContainsKey(pixel / 20)) {
			for (int i = 0; i < 100; ++i) {
				cout << "\n";
			}
			cout << pixel << endl;
			if (pixels_to_vertex[pixel / 20]->Length == 0) {
				cout << "Первый коммит не имеет диффа" << endl;
			}
			else {
				Console::WriteLine(pixels_to_vertex[pixel / 20]);
				char* command = (char*)(void*)Marshal::StringToHGlobalAnsi(pixels_to_vertex[pixel / 20]);
				system("q");
				system(command);
			}
		}
	}
}

void generate(string actual_link, string expected_link) {
	ComparisonRepositoryGenerator generator; generator.cmp(actual_link, expected_link);
	system("pause");
	exit(0);
}

void timer() {
	using namespace std::this_thread;
	using namespace std::chrono;
	sleep_for(seconds(60));
	cout << "Программа не может найти ошибки, так как репозитории слишком большого размера\n";
	//system("pause");
	exit(0);
}

[STAThreadAttribute]
int main() {
	SetConsoleCP(1251);
	SetConsoleOutputCP(1251);
	string actual_link = "C:\\Users\\ochob\\OneDrive\\Рабочий стол\\vse\\diplom\\git\\VKR\\Project1\\really_test\\actual";
	string expected_link = "C:\\Users\\ochob\\OneDrive\\Рабочий стол\\vse\\diplom\\git\\VKR\\Project1\\really_test\\expected";
	cout << "Введите директорию с актуальным репозиторием, в формате: C:\\Users\\ochob\\OneDrive\\Рабочий стол\\vse\\diplom\\git\\VKR\\Project1\\really_test\\actual\n";
	getline(cin, actual_link);
	cout << "Введите директорию с ожидаемым репозиторием, в формате: C:\\Users\\ochob\\OneDrive\\Рабочий стол\\vse\\diplom\\git\\VKR\\Project1\\really_test\\expected\n";
	getline(cin, expected_link);
	ComparisonRepositoryGenerator generator; generator.cmp(actual_link, expected_link);
	system("pause");

	return 0;
	//std::thread thread(generate, actual_link, expected_link), thread2(timer);
	//thread.join();
	//thread2.join();
	//system("pause");
	//return 0;
	//Application::SetCompatibleTextRenderingDefault(false);
	//Application::EnableVisualStyles();
	//Project1::MyForm form;
	//Application::Run(% form);

	return 0;
}
