import pytest
from lib.calculator import Calculator


@pytest.fixture
def calculator():
    return Calculator()


def test_add(calculator):
    assert calculator.add(2, 3) == 5
    assert calculator.add(-1, 1) == 0


def test_subtract(calculator):
    assert calculator.subtract(10, 5) == 5
    assert calculator.subtract(0, 5) == -5


def test_multiply(calculator):
    assert calculator.multiply(3, 4) == 12
    assert calculator.multiply(0, 5) == 0


def test_divide(calculator):
    assert calculator.divide(10, 2) == 5
    with pytest.raises(ValueError):
        calculator.divide(10, 0)
