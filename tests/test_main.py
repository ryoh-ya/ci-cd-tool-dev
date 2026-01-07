import pytest
from io import StringIO
import sys
from src.main import hello_world


def test_hello_world_output(capsys):
    """Test that hello_world prints the correct message."""
    hello_world()
    captured = capsys.readouterr()
    assert captured.out == "Hello, World!\n"


def test_hello_world_no_return():
    """Test that hello_world returns None."""
    result = hello_world()
    assert result is None
