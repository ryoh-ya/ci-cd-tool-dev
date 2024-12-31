import pytest
from lib.generate_coverage import GenerateCoverage


@pytest.fixture
def generate_coverage():
    return GenerateCoverage("tests/pytest-coverage.txt", "tests/table.md")


def test_print_coverage(generate_coverage):
    generate_coverage.print_table()


def test_save_coverage(generate_coverage):
    generate_coverage.save_table()
