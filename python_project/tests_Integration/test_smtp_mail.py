import pytest
import os
from lib.send_smtp_mail import send_email


def test_send_email():
    build_id = "test_build_id"
    project_id = "test_project_id"
    build_log_url = "https://example.com/logs"
    # メール内容
    subject = f"Build Failed: {build_id}"
    body = f"Build {build_id} failed for project {project_id}. Logs: {build_log_url}"
    to_email = "recipient@example.com"  # 送信先メールアドレス
    send_email(subject, body, to_email)
