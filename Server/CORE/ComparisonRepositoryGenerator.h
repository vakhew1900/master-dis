#pragma once
#include <iostream>
#include <vector>
#include "ComparisonRepository.h"
#include "OutputGraph.h"

using namespace std;

class ErrorCommitRepository {
private:
    vector<Commit> errorCommits;
    vector<Commit> missCommits;
    vector<Commit> extraCommits;

public:

    ErrorCommitRepository() {};
    ErrorCommitRepository(vector<Commit> errorCommits, vector<Commit> missCommits, vector<Commit> extraCommits) {
        this->errorCommits = errorCommits;
        this->missCommits = missCommits;
        this->extraCommits = extraCommits;
    }

    void addErrorCommit(Commit commit);
    void addMissCommit(Commit commit);
    void addExtraCommit(Commit commit);
    vector<Commit> getExtraCommits() const;
    vector<Commit> getMissCommits() const;
    vector<Commit> getErrorCommits() const;
    bool operator == (const ErrorCommitRepository& other);
};



class ComparisonRepositoryGenerator {
public:
    /*! \brief Сгенерировать репозиторий сравнения
    *   \return Репозиторий сравнения
    */
    ComparisonRepository GenerateComparsionRepository();

    /*! \brief Сгенерировать репозиторий сравнения
    *   \param[in] actual_repository - актуальный репозиторий
    *   \param[in] expected_repository - ожидаемый репозиторий
    *   \return Репозиторий сравнения
    */
    ComparisonRepository GenerateComparsionRepository(
        Repository& actual_repository, Repository& expected_repository);

    void cmp(string, string);

    ErrorCommitRepository cmp(Repository& expected, Repository& actual);

    /*! \brief Создать список ребер для номеров дифов
    *   \param[in] list_of_edges_diff - список ребер для дифов
    *   \param[in] second_list_of_edges - дифф-номер
    *   \return список ребер
    */
    vector<pair<int, int>> MadeListOfEdgesFromListOfEdgesCommit(
        vector<pair<Commit, Commit>>& list_of_edges_diff);

private:
    map<vector<string>, int> diff_to_number;
    map<Commit, int> commit_to_number;
    int last_number = 0;


    /*! \brief Сгенерировать репозиторий по ссылке на него
    */
    Repository GenerateRepositoryFromGitLink(const string& link);

    /*! \brief Создать список рёбер из репозитория
    *   \param[in] link_on_rep - ссылка на репозиторий
    *   \return список рёбер
    */
    vector<pair<Commit, Commit>> CreatelistOfEdgesCommitFromLinkOnRep(
        const string& link_on_rep);

    /*! \brief Определить номер коммита
    *   \param[in] коммит
    *   \return номер
    */
    int DetermineNumberByCommit(Commit& commit);

    void GenerateVertexesForComparisonRepository(
        ComparisonRepository& result_repository, const Repository& actual_repository, Repository& expected_repository) const;

    void GenerateEdgesForComparisonRepository(
        ComparisonRepository& result_repository, const Repository& actual_repository, Repository& expected_repository) const;
};
