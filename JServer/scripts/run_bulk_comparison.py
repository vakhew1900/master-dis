import os
import subprocess
import json
from pathlib import Path

def main():
    # Определяем пути относительно скрипта
    script_dir = Path(__file__).parent
    pairs_file = script_dir / "pairs.json"
    project_root = script_dir.parent.parent.parent
    
    # Путь к собранному fat-JAR
    jar_path = project_root / "target/JServer-1.0-SNAPSHOT-jar-with-dependencies.jar"
    if not jar_path.exists():
        print(f"Error: JAR not found at {jar_path}. Build the project with 'mvn clean package' first.")
        return

    # Путь к Java 17+ (предпочтительно из JetBrains Runtime)
    java_bin = r"C:\Program Files\JetBrains\IntelliJ IDEA 2025.3.3\jbr\bin\java.exe"
    if not os.path.exists(java_bin):
        java_bin = "java" # Fallback to system Java

    # Проверяем, существует ли файл с парами
    if not pairs_file.exists():
        print(f"Error: Pairs file not found at {pairs_file}. Cannot run comparisons.")
        return

    # Читаем пары из JSON
    with open(pairs_file, 'r', encoding='utf-8') as f:
        data_pairs = json.load(f)

    # Основная папка для отчетов
    reports_base_dir = project_root / "bulk_reports"
    reports_base_dir.mkdir(exist_ok=True)
    
    print(f"Found {len(data_pairs)} pairs to compare from {pairs_file.name}.")

    for i, pair in enumerate(data_pairs):
        student = pair['student']
        reference = pair['reference']
        
        # Создаем отдельную папку для каждой пары
        pair_report_dir = reports_base_dir / f"example-№-{i}"
        pair_report_dir.mkdir(parents=True, exist_ok=True)
        
        output_path = pair_report_dir / "report.html"
        
        print(f"
[{i+1}/{len(data_pairs)}] Comparing into {pair_report_dir.name}:")
        
        cmd = [
            java_bin,
            "-jar", str(jar_path.absolute()),
            student,
            reference,
            str(output_path.absolute())
        ]
        
        try:
            process = subprocess.run(cmd, capture_output=True, text=True, encoding='utf-8', errors='replace')
            if process.returncode == 0:
                print(f"  SUCCESS: Report generated.")
            else:
                print(f"  ERROR (code {process.returncode}):")
                print("--- STDOUT ---")
                print(process.stdout)
                print("--- STDERR ---")
                print(process.stderr)
        except Exception as e:
            print(f"  FAILED to execute: {e}")

if __name__ == "__main__":
    main()
