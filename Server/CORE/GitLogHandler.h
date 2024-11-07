#pragma once
#include <iostream>
#include <queue>
#include <set>
#include <fstream>
#include <string>
#include <vector>
#include <map>
#include "Commit.h"

using namespace std;

/*!
    \brief Класс для обработки логов репозитория
*/
class GitLogHandler {
public:
    /*! \brief Конструктор класса по имени директории
    *   \param[in] directory_name - имя директории
    */
    GitLogHandler(const string& directory_name);

    /*! \brief Конструктор класса по имени директории
    *   \param[in] directory_name - имя директории
    */
    GitLogHandler(const vector<string> log);

    /*! \brief Функция генерации списка ребер из лога
    *   \return список дуг
    */
    vector<pair<string, string>> GenerateListOfEdges() const;

    /*! \brief Сгенерировать коммит для каждого хэша коммита
    *   \return хэш-дифф
    */
    map<string, Commit> GenerateCommitForEveryHash() const;

private:
    //! Лог из гит репозитория
    vector<string> git_log;

    //! Имя директории, где располагается репозиторий
    string directoryName;

    //! Ребро между двумя коммитами (коммиты задаются своими хешами)
    using Edge = pair<string, string>;

    //! Список рёбер
    using ListOfEdges = vector<Edge>;

    class TextPoint;

    queue<GitLogHandler::TextPoint> InitializeQueueForTextSearch() const;

    void TextBFSIteration(GitLogHandler::ListOfEdges& list_of_edges, set<TextPoint>& visited_points, queue<TextPoint>& q) const;

    void TransitionByText(const TextPoint& current_point, GitLogHandler::ListOfEdges& list_of_edges, queue<TextPoint>& q) const;

    map<string, Commit> InitializeCommitToHashMap(const ListOfEdges& list_of_edges) const;

    void FillDiffForCommits(const ListOfEdges& list_of_edges, map<string, Commit>& hash_to_commit) const;

    void FillMessageForCommits(map<string, Commit>& hash_to_commit) const;

    /*! Точка в тексте (не идеальное название типа)
        Вызвано это название тем, что хочется хранить позицию, где потенциально
        может быть ребро или вершина коммита, и знать при этом информацию о
        предыдущем коммите, чтобы коммиты соединить.
        Точка - это в том, смысле, что у нас системой координат является не
        только текст, но и коммиты вокруг.
    */
    class TextPoint {
    private:
        tuple<string, int, int> value;

    public:
        TextPoint(const std::string& commit_hash, const int row, const int col) {
            this->value = { commit_hash, row, col };
        }

        string GetPreviousCommitHash() const {
            return get<0>(value);
        }

        int GetRow() const {
            return get<1>(value);
        }

        int GetCol() const {
            return get<2>(value);
        }

        friend bool operator < (const TextPoint& a, const TextPoint& b) {
            return a.value < b.value;
        }

        friend bool operator == (const TextPoint& a, const TextPoint& b) {
            return a.value == b.value;
        }
    };
};
