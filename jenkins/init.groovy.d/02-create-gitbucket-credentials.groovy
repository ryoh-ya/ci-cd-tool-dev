import com.cloudbees.plugins.credentials.*
import com.cloudbees.plugins.credentials.domains.*
import com.cloudbees.plugins.credentials.impl.*
import hudson.util.Secret
import jenkins.model.Jenkins

def credentialsId = "gitbucket-id" // ユニークなID
def username = System.getenv("GITBUCKET_USER") // 環境変数からユーザー名を取得
def password = System.getenv("GITBUCKET_PASS") // 環境変数からパスワードを取得
def description = "Username and password for ${credentialsId}" // 説明


if (username == null || username.trim().isEmpty()) {
    throw new IllegalArgumentException("環境変数 'GITBUCKET_USERNAME' が設定されていません。")
}

if (password == null || password.trim().isEmpty()) {
    throw new IllegalArgumentException("環境変数 'GITBUCKET_PASSWORD' が設定されていません。")
}

// UsernamePassword認証情報の作成
def userPassCredentials = new UsernamePasswordCredentialsImpl(
    CredentialsScope.GLOBAL,  // スコープをグローバルに設定
    credentialsId,            // 認証情報ID
    description,              // 説明
    username,                 // ユーザー名
    password                  // パスワード
)

// ドメインを指定 (_ はデフォルトドメイン)
def domain = Domain.global()

// 認証情報ストアの取得
def store = Jenkins.instance.getExtensionList('com.cloudbees.plugins.credentials.SystemCredentialsProvider')[0].getStore()


// 認証情報をストアに追加
store.addCredentials(domain, userPassCredentials)

println "ユーザー名とパスワードの認証情報が正常に登録されました！ ID: ${credentialsId} ユーザー名: ${username}"