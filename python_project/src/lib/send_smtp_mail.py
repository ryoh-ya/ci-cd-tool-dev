
import os
import smtplib
from email.mime.text import MIMEText


def send_email(subject, body, to_email):
    # Mailtrap SMTPサーバーの設定
    smtp_server = "sandbox.smtp.mailtrap.io"
    smtp_port = 587
    smtp_username = "1e5cb876295cc2"  # Mailtrapの認証情報
    smtp_password = "60bf7edf7cd552"

    # メールの作成
    msg = MIMEText(body)
    msg["Subject"] = subject
    msg["From"] = "no-reply@example.com"  # 送信元メールアドレス
    msg["To"] = to_email

    # メール送信
    try:
        with smtplib.SMTP(smtp_server, smtp_port) as server:
            server.starttls()  # セキュア接続
            server.login(smtp_username, smtp_password)
            server.sendmail(msg["From"], [to_email], msg.as_string())
        print("Email sent successfully")
    except Exception as e:
        print(f"Failed to send email: {e}")


if __name__ == "__main__":
    # 環境変数から情報を取得
    build_id = os.getenv("BUILD_ID")
    project_id = os.getenv("PROJECT_ID")
    build_log_url = os.getenv("BUILD_LOG_URL")

    # メール内容
    subject = f"Build Failed: {build_id}"
    body = f"Build {build_id} failed for project {project_id}. Logs: {build_log_url}"
    to_email = "recipient@example.com"  # 送信先メールアドレス

    # メール送信
    send_email(subject, body, to_email)
