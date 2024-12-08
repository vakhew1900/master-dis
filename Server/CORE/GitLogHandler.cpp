#include "GitLogHandler.h"
#include <windows.h>
#include <string>

GitLogHandler::ListOfEdges GitLogHandler::GenerateListOfEdges()
const {
    if (git_log.size() == 0) {
        return GitLogHandler::ListOfEdges();
    }

    //! Здесь будет происходить генерация списка рёбер графа.
    //! Список рёбер генерируется с помощью обхода в ширину.

    //! Результат выполнения функции
    GitLogHandler::ListOfEdges result;
    //! Множество посещенных точек
    set<TextPoint> visited_points;
    //! Очередь из точек для обхода
    queue<TextPoint> q = InitializeQueueForTextSearch();

    //! Пока очередь не пуста
    while (!q.empty()) {
        TextBFSIteration(result, visited_points, q);
    }

    /*! Это некоторый костыль сделано по той причине, что
        в гит вершины едут в тексте снизу вверх, а обход
        происходит сверху вниз
    */
    reverse(result.begin(), result.end());
    return result;
}

GitLogHandler::GitLogHandler(const string& directoryName) {
    this->directoryName = directoryName;

    string queryLog = "cd " + directoryName + " && git log --graph --all > gitlog.txt";


    if (system(queryLog.c_str()) != 0) {
        system("pause");
        exit(0);
    }

    //! Запись лога репозитория в определенный файл
    ifstream input(directoryName + "\\gitlog.txt");
    string line;
    while (getline(input, line)) {
        git_log.push_back(line);
    }

    reverse(git_log.begin(), git_log.end());

    for (int i = 0; i < git_log.size(); i++) {
        for (int j = 0; j < git_log[i].size(); j++) {
            if (git_log[i][j] == '/') {
                git_log[i][j] = '\\';
            }
            else if (git_log[i][j] == '\\') {
                git_log[i][j] = '/';
            }
        }
    }
}

GitLogHandler::GitLogHandler(const vector<string> log) {
    git_log = log;
}

map<string, Commit> GitLogHandler::GenerateCommitForEveryHash() const {
    ListOfEdges list_of_edges = GenerateListOfEdges();
    map<string, Commit> result = InitializeCommitToHashMap(list_of_edges);

    FillDiffForCommits(list_of_edges, result);
    FillMessageForCommits(result);

    return result;
}

map<string, Commit> GitLogHandler::InitializeCommitToHashMap(const ListOfEdges& list_of_edges) const {
    map<string, Commit> result;

    for (auto elem : list_of_edges) {
        result[elem.first] = Commit(elem.first);
        result[elem.second] = Commit(elem.second);
    }

    return result;
}

void GitLogHandler::FillDiffForCommits(const ListOfEdges& list_of_edges, map<string, Commit>& hash_to_commit) const {
    for (auto elem : list_of_edges) {
        //! Запрос для получения диффа по определённому ребру и запись его в определенный файл
        string queryLog = "cd " + directoryName + " && git diff " + elem.first + " " + elem.second + " > gitlog.txt";
        system(queryLog.c_str());

        //! Получение диффа по ребру из файла
        vector<string> diff;
        ifstream input(directoryName + "\\gitlog.txt");
        string line;
        //! пропускаем 2 первые строки, так как там уникальный index
        while (getline(input, line)) {
            diff.push_back(line);
        }

        //! Запись диффа и информации о команде для его получения в мапу
        hash_to_commit[elem.second].diffs.push_back(diff);
        sort(hash_to_commit[elem.second].diffs.begin(), hash_to_commit[elem.second].diffs.end());
        hash_to_commit[elem.second].command_for_check_diff = queryLog;
    }

    for (auto& elem : hash_to_commit) {
        if (elem.second.diffs.empty()) {
            string queryLog = "cd " + directoryName + " && git diff " + elem.first + " 4b825dc642cb6eb9a060e54bf8d69288fbee4904 > gitlog.txt";
            system(queryLog.c_str());

            vector<string> diff;
            ifstream input(directoryName + "\\gitlog.txt");
            string line;
            //! пропускаем 2 первые строки, так как там уникальный index
            while (getline(input, line)) {
                diff.push_back(line);
            }

            //! Запись диффа и информации о команде для его получения в мапу
            elem.second.diffs.push_back(diff);
            sort(elem.second.diffs.begin(), elem.second.diffs.end());
            elem.second.command_for_check_diff = queryLog;
        }
    }
}

void GitLogHandler::FillMessageForCommits(map<string, Commit>& hash_to_commit) const {
    for (auto& elem : hash_to_commit) {
        //! Запрос для получения сообщения по коммиту
        string query_message = "cd " + directoryName + " && git log --format=%B -n 1 " + elem.first + " > gitlog.txt";
        system(query_message.c_str());

        //! Чтения сообщения коммита и запись его в поле message
        ifstream input(directoryName + "\\gitlog.txt");
        getline(input, elem.second.message);
    }
}

queue<GitLogHandler::TextPoint> GitLogHandler::InitializeQueueForTextSearch() const {
    //! Начальная строка для обхода в тексте
    int begin_str = 0;
    //! Начальный столбец для обхода в тексте
    int begin_stb = 0;
    //! Хэш коммита, из которого идем в обходе (изначально пуст)
    string begin_hash = "";
    //! Результат выполнения функции
    queue<TextPoint> result;

    for (int i = 0; i < git_log.size(); i++) {
        if (git_log[begin_str][begin_stb] != '*') {
            begin_str++;
        }
        else {
            break;
        }
    }


    //! Кладём в очередь начальную точку
    TextPoint begin_point(begin_hash, begin_str, begin_stb);
    result.push(begin_point);

    return result;

}

void GitLogHandler::TextBFSIteration(ListOfEdges& list_of_edges, set<TextPoint>& visited_points, queue<TextPoint>& q) const {
    //! Текущая точка из очереди
    TextPoint current_point = q.front();
    q.pop();

    //! Если текущая точка посещена - пропускам итерацию
    if (visited_points.count(current_point)) {
        return;
    }
    //! Обозначаем текущую точку как посещённую
    visited_points.insert(current_point);

    TransitionByText(current_point, list_of_edges, q);
}

void GitLogHandler::TransitionByText(const TextPoint& current_point, GitLogHandler::ListOfEdges& list_of_edges, queue<TextPoint>& q) const {
    //! Давайте я попрошу прощения за эту функцию в целом,
    //! и не буду это делать над каждой строчкой в дальнейшем.

    const string& cur_hash = current_point.GetPreviousCommitHash();
    const int cur_row = current_point.GetRow();
    const int cur_col = current_point.GetCol();

    if (cur_row == 93) {
        cout << "ffff";
    }

    if (cur_row >= this->git_log.size()) {
        return;
    }

    //! '*' - является указателем на коммит.
    if (git_log[cur_row][cur_col] == '*') {
        //! Определяем хеш коммита по строке в с ним
        string new_hash = git_log[cur_row].substr(git_log[cur_row].find("commit") + 7);

        //! Если это не первый коммит - проводим ребро между текущим коммитом и новым
        if (cur_hash != "") {
            //! Направление ребра здесь не случайно, так как в гит коммиты идут снизу вверх,
            //! а мы их обрабатываем сверху вниз
            list_of_edges.push_back({ cur_hash, new_hash });
        }

        //! Переходим вверх для поиска следующих вершин
        q.push({ new_hash, cur_row + 1, cur_col });
    }

    //! '|' - является указателем на то, что есть коммиты сверху
    if (git_log[cur_row][cur_col] == '|') {
        //! Переходим по тексту вверх
        q.push({ cur_hash, cur_row + 1, cur_col });
    }

    //! '\\' - является указателем на то, что есть коммиты сверху-слева
    if (git_log[cur_row][cur_col + 1] == '\\') {
        //! Переходим по тексту вверх-влево
        q.push({ cur_hash, cur_row + 1, cur_col + 2 });
    }

    //! '/' - является указателем на то, что есть коммиты сверху-слева
    if (cur_col >= 2 && git_log[cur_row][cur_col - 1] == '/') {
        //! Переходим по тексту вверх-вправо
        q.push({ cur_hash, cur_row + 1, cur_col - 2 });
    }
}
