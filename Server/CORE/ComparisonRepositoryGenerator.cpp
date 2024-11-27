#include "ComparisonRepositoryGenerator.h"
#include "GitLogHandler.h"
#include "ComparisonRepository.h"
#include "Graph.h"
#include "LabelsGenerator.h"
#include <Windows.h>

ComparisonRepository ComparisonRepositoryGenerator::GenerateComparsionRepository() {
    setlocale(LC_ALL, "Russian");

    //! Ссылка на репозитории
    //! Todo: вынести их на чтение
    //string actual_link = "test2\\rep1";
    //string expected_link = "test2\\rep2";
    string actual_link = "oatest\\actual";
    string expected_link = "oatest\\expected";
    //cmp();
    //! Актуальный репозиторий
    Repository actual_repository = GenerateRepositoryFromGitLink(actual_link);

    //! Ожидаемый репозиторий
    Repository expected_repository = GenerateRepositoryFromGitLink(expected_link);

    return GenerateComparsionRepository(actual_repository, expected_repository);
}

ComparisonRepository ComparisonRepositoryGenerator::GenerateComparsionRepository(
    Repository& actual_repository, Repository& expected_repository) {
    //! Результат сравнения репозиториев
    ComparisonRepository result;

    //! Генерируем вершины для результата сравнения репозиториев
    GenerateVertexesForComparisonRepository(result, actual_repository, expected_repository);

    //! Генерируем рёбра для результата сравнения репозиториев
    GenerateEdgesForComparisonRepository(result, actual_repository, expected_repository);

    return result;
}

Repository ComparisonRepositoryGenerator::GenerateRepositoryFromGitLink(const string& link) {
    //! Создаем список рёбер между коммитами
    auto list_of_edges_diff = CreatelistOfEdgesCommitFromLinkOnRep(link);

    //! Нумеруем коммиты, создавая при этом список из номеров
    const auto list_of_edges = MadeListOfEdgesFromListOfEdgesCommit(list_of_edges_diff);

    //! Результат выполнения функции
    Repository result;

    //! Добавляем в структуру данных репозитория рёбра
    for (auto elem : list_of_edges) {
        result.edges.insert(elem);
    }

    //! Добавляем в структуру данных репозитория коммиты между рёбрами
    for (auto elem : list_of_edges_diff) {
        result.commits.insert(elem.first);
        result.commits.insert(elem.second);
    }

    return result;
}

void ComparisonRepositoryGenerator::GenerateVertexesForComparisonRepository(
    ComparisonRepository& result_repository, const Repository& actual_repository, Repository& expected_repository) const {
    // мерж делаю тупо, в нем без разницы планарность, но тогда надо делать ребро над вершиной
    // если вершина соединяется с вершиной на другой ветке, то это будет 2 палки
    // надо сделать новый граф (ComparisonGraph) и у него сделать для каждой вершины соответсвие в актуальном и ожидаемом графе
    // по этому графу довольно легко можно построить будет (ComparisonRepository)
    // надо бы еще хранить номер ветки, чтобы вычислять дельту в коммите
    //TODO: тот  алгоритм
    /*
    //! Добавляем коммиты актуального репозитория
    for (auto vertex : actual_repository.commits) {
        result_repository.commits.insert(vertex);
    }

    //! Добавляем коммиты ожидаемого репозитория
    for (auto vertex : expected_repository.commits) {
        result_repository.commits.insert(vertex);
    }

    //! Проходим по коммитам
    for (auto& vertex : result_repository.commits) {
        //! Если коммита нет в актульном репозитории - его необходимо добавить
        if (!actual_repository.commits.count(vertex)) {
            result_repository.commit_state[vertex.number] =
                ComparisonRepository::State::NeedAdd;
            continue;
        }

        //! Если коммита нет в ожидаемом репозитории - его необходимо удалить
        if (!expected_repository.commits.count(vertex)) {
            result_repository.commit_state[vertex.number] =
                ComparisonRepository::State::NeedDelete;
            continue;
        }

        //! В противном случае с коммитом ничего не нужно делать
        result_repository.commit_state[vertex.number] =
            ComparisonRepository::State::NeedNothing;
    }
    */
}

void ComparisonRepositoryGenerator::GenerateEdgesForComparisonRepository(
    ComparisonRepository& result_repository, const Repository& actual_repository, Repository& expected_repository) const {
    //TODO: ребра в зависимости от вершины (мб будут вычислены тем же алгоритмом)
    /*
    //! Добавляем рёбра актуального репозитория
    for (auto edge : actual_repository.edges) {
        result_repository.edges.insert(edge);
    }

    //! Добавляем рёбра ожидаемого репозитория
    for (auto edge : expected_repository.edges) {
        result_repository.edges.insert(edge);
    }

    //! Проходим по рёбрам
    for (auto edge : result_repository.edges) {
        //! Если ребра нет в актульном репозитории - его необходимо добавить
        if (!actual_repository.edges.count(edge)) {
            result_repository.edge_state[edge] =
                ComparisonRepository::State::NeedAdd;
            continue;
        }

        //! Если ребра нет в ожидаемом репозитории - его необходимо добавить
        if (!expected_repository.edges.count(edge)) {
            result_repository.edge_state[edge] =
                ComparisonRepository::State::NeedDelete;
            continue;
        }

        //! В противном случае с ребром ничего не нужно делать
        result_repository.edge_state[edge] = ComparisonRepository::State::NeedNothing;
    }
    */
}

vector<pair<Commit, Commit>> ComparisonRepositoryGenerator::CreatelistOfEdgesCommitFromLinkOnRep(
    const string& link_on_rep) {
    //! Обработчик логов репозитория
    GitLogHandler git_log(link_on_rep);

    //! Дифф коммита по его хэшу
    map<string, Commit> diff = git_log.GenerateCommitForEveryHash();

    //! Список рёбер между хэшами
    const auto list_of_edges = git_log.GenerateListOfEdges();

    //! Список рёбер между коммитами
    vector<pair<Commit, Commit>> list_of_edges_commit;

    //! Заполнение списка рёбер между хэшами
    for (const auto& edge : list_of_edges) {
        list_of_edges_commit.push_back({ 
            Commit(diff[edge.first]), 
            Commit(diff[edge.second])
        });
    }

    return list_of_edges_commit;
}


vector<pair<int, int>> ComparisonRepositoryGenerator::MadeListOfEdgesFromListOfEdgesCommit(
    vector<pair<Commit, Commit>>& list_of_edges_diff) {

    //! Создаем список ребер между номерами коммитов из списка рёбер между коммитами
    vector<pair<int, int>> res;
    for (auto& list_of_edges_diff_item : list_of_edges_diff) {
        res.push_back({
            DetermineNumberByCommit(list_of_edges_diff_item.first),
            DetermineNumberByCommit(list_of_edges_diff_item.second)
            });
    }

    return res;
}

int ComparisonRepositoryGenerator::DetermineNumberByCommit(Commit& commit) {
    //! Если нет номер соответствующего данному коммиту,
    //! то возвращаем увеличенный номер, иначе - 
    //! уже существующий номер коммита
    if (commit_to_number[commit] == 0) {
        commit_to_number[commit] = ++last_number;
    }
    return commit.number = commit_to_number[commit];
}

void ComparisonRepositoryGenerator::cmp(string actual_link, string expected_link) {
    setlocale(LC_ALL, "Russian");

    // получить репозитории
    const auto actual_repository = GenerateRepositoryFromGitLink(actual_link);
    const auto expected_repository = GenerateRepositoryFromGitLink(expected_link);

    const vector<pair<int, int>> actual_repository_edges(actual_repository.edges.begin(), actual_repository.edges.end());
    const vector<pair<int, int>> expectedl_repository_edges(expected_repository.edges.begin(), expected_repository.edges.end());

    LabelsGenerator labels_generator(actual_repository, expected_repository);

    string queryLog = "del \"" + actual_link + "\\gitlog.txt\"";
    system(queryLog.c_str());
    queryLog = "del \"" + expected_link + "\\gitlog.txt\"";
    system(queryLog.c_str());

    const auto actual_graph = Graph(actual_repository_edges, labels_generator, true); //, labels.first);
    const auto expected_graph = Graph(expectedl_repository_edges, labels_generator, false);//, labels.second);


    //я вроде даже до сюда раздебажил
    const auto& labels_ids = Graph::BiggestCommonSubgraph((actual_graph), (expected_graph));

    //cout << res_graph;
    int count_errors = 0;
    for (const auto vertex : actual_graph.GetVertexes()) {
        const auto& vertex_labels = actual_graph.GetLabels(vertex);

        if (vertex_labels.empty()) {
            continue;
        }

        int count_labels_in_result = 0;

        for (const auto vertex_label : vertex_labels) {
            if (labels_ids.first.count(vertex_label)) {
                count_labels_in_result++;
            }
        }

        if (count_labels_in_result == 0) {
            for (const auto& commit : actual_repository.commits) {
                if (commit.number == vertex) {
                    ++count_errors;
                    cout << "В исходном репозитории находится лишний коммит с хэшем: " << commit.hash << "\n";
                }
            }
        }
        else if (count_labels_in_result < vertex_labels.size()) {
            for (const auto& commit : actual_repository.commits) {
                if (commit.number == vertex) {
                    ++count_errors;
                    cout << "В исходном репозитории коммит с хэшем: " << commit.hash << " содержит ошибки\n";
                }
            }
        }
    }

    for (const auto vertex : expected_graph.GetVertexes()) {
        const auto& vertex_labels = expected_graph.GetLabels(vertex);

        if (vertex_labels.empty()) {
            continue;
        }

        int count_labels_in_result = 0;

        for (const auto vertex_label : vertex_labels) {
            if (labels_ids.second.count(vertex_label)) {
                count_labels_in_result++;
            }
        }

        if (count_labels_in_result == 0) {
            for (const auto& commit : expected_repository.commits) {
                if (commit.number == vertex) {
                    ++count_errors;
                    cout << "В исходном репозитории не хватает вершины с хэшем: " << commit.hash << "\n";
                }
            }
        }
    }

    if (count_errors == 0) {
        cout << "Ошибок найдено не было\n";
    }
}


ErrorCommitRepository ComparisonRepositoryGenerator::cmp(Repository& expected_repository, Repository& actual_repository) {

    const vector<pair<int, int>> actual_repository_edges(actual_repository.edges.begin(), actual_repository.edges.end());
    const vector<pair<int, int>> expectedl_repository_edges(expected_repository.edges.begin(), expected_repository.edges.end());

    LabelsGenerator labels_generator(actual_repository, expected_repository);

    const auto actual_graph = Graph(actual_repository_edges, labels_generator, true); //, labels.first);
    const auto expected_graph = Graph(expectedl_repository_edges, labels_generator, false);//, labels.second);

    const auto& labels_ids = Graph::BiggestCommonSubgraph((actual_graph), (expected_graph));

    int count_errors = 0;
    ErrorCommitRepository result;

    for (const auto vertex : actual_graph.GetVertexes()) {
        const auto& vertex_labels = actual_graph.GetLabels(vertex);

        if (vertex_labels.empty()) {
            continue;
        }

        int count_labels_in_result = 0;

        for (const auto vertex_label : vertex_labels) {
            if (labels_ids.first.count(vertex_label)) {
                count_labels_in_result++;
            }
        }

        if (count_labels_in_result == 0) {
            for (const auto& commit : actual_repository.commits) {
                if (commit.number == vertex) {
                    ++count_errors;
                    result.addExtraCommit(commit);
                }
            }
        }
        else if (count_labels_in_result < vertex_labels.size()) {
            for (const auto& commit : actual_repository.commits) {
                if (commit.number == vertex) {
                    ++count_errors;
                    result.addErrorCommit(commit);
                }
            }
        }
    }

    for (const auto vertex : expected_graph.GetVertexes()) {
        const auto& vertex_labels = expected_graph.GetLabels(vertex);

        if (vertex_labels.empty()) {
            continue;
        }

        int count_labels_in_result = 0;

        for (const auto vertex_label : vertex_labels) {
            if (labels_ids.second.count(vertex_label)) {
                count_labels_in_result++;
            }
        }

        if (count_labels_in_result == 0) {
            for (const auto& commit : expected_repository.commits) {
                if (commit.number == vertex) {
                    ++count_errors;
                    result.addMissCommit(commit);
                }
            }
        }
    }
    
    return result;
}

void ErrorCommitRepository::addErrorCommit(Commit commit)
{
    this->errorCommits.push_back(commit);
}

void ErrorCommitRepository::addMissCommit(Commit commit)
{
    this->missCommits.push_back(commit);
}

void ErrorCommitRepository::addExtraCommit(Commit commit) // Исправлено: убрано 's'
{
    this->extraCommits.push_back(commit);
}

vector<Commit> ErrorCommitRepository::getExtraCommits() const
{
    return this->extraCommits;
}

vector<Commit> ErrorCommitRepository::getMissCommits() const
{
    return this->missCommits;
}

vector<Commit> ErrorCommitRepository::getErrorCommits() const
{
    return this->errorCommits;
}


set<string> setOfCommitHash(const vector<Commit> commits) {
    set<string> result;
    for (auto commit : commits) {
        result.insert(commit.hash);
    }
    return result;
}

bool ErrorCommitRepository::operator==(const ErrorCommitRepository& other)
{
    auto errors = setOfCommitHash(this->getErrorCommits());
    auto miss = setOfCommitHash(this->getMissCommits());
    auto extra = setOfCommitHash(this->getExtraCommits());

    auto otherErrors = setOfCommitHash(other.getErrorCommits());
    auto otherMiss = setOfCommitHash(other.getMissCommits());
    auto otherExtra = setOfCommitHash(other.getExtraCommits());
    return errors == otherErrors && miss == otherMiss && otherExtra == otherExtra;
}