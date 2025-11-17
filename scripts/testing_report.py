import argparse
import csv
import re
from pathlib import Path
from textwrap import indent


def parse_args():
    parser = argparse.ArgumentParser(description="Genera un reporte Gherkin y resumen de cobertura para el módulo indicado.")
    parser.add_argument("--module", required=True, help="Ruta del módulo (por ejemplo: admin/year)")
    parser.add_argument("--output", required=True, help="Ruta del archivo .md destino para el reporte generado")
    return parser.parse_args()


def collect_tests(module: str):
    """
    Recopila pruebas dentro de src/test/java filtrando controllers/services/dtos del módulo especificado.
    Retorna una lista de tuplas (feature, [scenarios]).
    """
    test_root = Path("src/test/java")
    module_path_fragment = module.replace(".", "/")
    results = []

    for path in test_root.rglob("*.java"):
        if module_path_fragment not in path.as_posix():
            continue

        stem = path.stem
        if not any(token in stem for token in ("Service", "Controller", "Dto")):
            continue

        content = path.read_text(encoding="utf-8")

        class_display = re.search(r'@DisplayName\("([^"]+)"\)\s*class\s+\w+', content)
        feature = class_display.group(1) if class_display else stem

        scenarios = []
        for match in re.finditer(r'@DisplayName\("([^"]+)"\)\s*(?:public\s+)?(?:void|class)\s+[A-Za-z0-9_]+', content):
            scenarios.append(match.group(1))

        if scenarios:
            results.append((feature, scenarios))

    return results


def coverage_for(module: str):
    """
    Calcula la cobertura global y del módulo utilizando el CSV generado por JaCoCo.
    """
    csv_path = Path("target/site/jacoco/jacoco.csv")
    keys = [
        "INSTRUCTION_MISSED",
        "INSTRUCTION_COVERED",
        "BRANCH_MISSED",
        "BRANCH_COVERED",
        "LINE_MISSED",
        "LINE_COVERED",
        "COMPLEXITY_MISSED",
        "COMPLEXITY_COVERED",
        "METHOD_MISSED",
        "METHOD_COVERED",
    ]

    totals = {k: 0 for k in keys}
    module_totals = {k: 0 for k in keys}

    with csv_path.open(encoding="utf-8") as f:
        reader = csv.DictReader(f)
        for row in reader:
            for key in keys:
                totals[key] += int(row[key])
            if row["PACKAGE"].startswith(f"trinity.play2learn.backend.{module.replace('/', '.')}"):
                for key in keys:
                    module_totals[key] += int(row[key])

    def pct(covered: int, missed: int) -> float:
        total = covered + missed
        return 0.0 if total == 0 else round(covered / total * 100, 2)

    overall = {
        "Instrucciones": pct(totals["INSTRUCTION_COVERED"], totals["INSTRUCTION_MISSED"]),
        "Ramas": pct(totals["BRANCH_COVERED"], totals["BRANCH_MISSED"]),
        "Líneas": pct(totals["LINE_COVERED"], totals["LINE_MISSED"]),
        "Complejidad": pct(totals["COMPLEXITY_COVERED"], totals["COMPLEXITY_MISSED"]),
        "Métodos": pct(totals["METHOD_COVERED"], totals["METHOD_MISSED"]),
    }

    module_cov = {
        "Instrucciones": pct(module_totals["INSTRUCTION_COVERED"], module_totals["INSTRUCTION_MISSED"]),
        "Ramas": pct(module_totals["BRANCH_COVERED"], module_totals["BRANCH_MISSED"]),
        "Líneas": pct(module_totals["LINE_COVERED"], module_totals["LINE_MISSED"]),
        "Complejidad": pct(module_totals["COMPLEXITY_COVERED"], module_totals["COMPLEXITY_MISSED"]),
        "Métodos": pct(module_totals["METHOD_COVERED"], module_totals["METHOD_MISSED"]),
    }

    return overall, module_cov


def write_report(module: str, tests, coverage, output_path: Path):
    overall, module_cov = coverage
    lines = [
        f"# Reporte de pruebas — módulo {module}",
        "",
        "## Cobertura de código (JaCoCo)",
        "",
        "### Proyecto completo",
        *(f"- {label}: {value} %" for label, value in overall.items()),
        "",
        f"### Módulo `{module}`",
        *(f"- {label}: {value} %" for label, value in module_cov.items()),
        "",
        "## Escenarios (services/controllers/dtos)",
    ]

    for feature, scenarios in tests:
        lines.append(f"Feature: {feature}")
        for scenario in scenarios:
            lines.append(indent(f"Scenario: {scenario}", "  "))
        lines.append("")

    output_path.parent.mkdir(parents=True, exist_ok=True)
    output_path.write_text("\n".join(lines), encoding="utf-8")


def main():
    args = parse_args()
    module = args.module.strip("/")
    tests = collect_tests(module)
    coverage = coverage_for(module)
    output_file = Path(args.output)
    write_report(module, tests, coverage, output_file)
    print(f"Reporte generado en {output_file}")


if __name__ == "__main__":
    main()

