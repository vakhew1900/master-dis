import os
import argparse

def find_git_repos(root_dir):
    git_repos = []
    for root, dirs, files in os.walk(root_dir):
        if '.git' in dirs:
            git_repos.append(os.path.abspath(root))
            # Пропускаем содержимое .git, чтобы ускорить поиск и не находить мусор
            dirs.remove('.git') 
    return git_repos

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Поиск всех Git-репозиториев в указанной папке.")
    parser.add_argument("path", help="Путь к директории для поиска")
    
    args = parser.parse_args()
    
    if not os.path.exists(args.path):
        print(f"Ошибка: Путь '{args.path}' не существует.")
    elif not os.path.isdir(args.path):
        print(f"Ошибка: '{args.path}' не является директорией.")
    else:
        repos = find_git_repos(args.path)
        if repos:
            for repo in repos:
                print(repo)
        else:
            print("Git-репозитории не найдены.")
