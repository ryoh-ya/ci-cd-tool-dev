import com.cloudbees.plugins.credentials.*
import com.cloudbees.plugins.credentials.domains.*
import com.cloudbees.plugins.credentials.impl.*
import org.jenkinsci.plugins.plaincredentials.*
import org.jenkinsci.plugins.plaincredentials.impl.*
import hudson.util.Secret
import jenkins.model.Jenkins

def secretTextId = "ssh-host-id" // ユニークなID
def secretPass = System.getenv("SSH_SERVER_HOST") // 環境変数からシークレット文字列を取得
def description = "Secret text for ${secretTextId}" // 説明

if (secretPass == null || secretPass.trim().isEmpty()) {
    throw new IllegalArgumentException("環境変数 'SSH_SERVER_HOST' が設定されていません。")
}


// Secret Text認証情報の作成
 def secretText = new StringCredentialsImpl(
        CredentialsScope.GLOBAL,
        secretTextId,
        description,
        Secret.fromString(secretPass)
    )

// ドメインを指定 (_ はデフォルトドメイン)
def domain = Domain.global()

// 認証情報ストアの取得
def store = Jenkins.instance.getExtensionList('com.cloudbees.plugins.credentials.SystemCredentialsProvider')[0].getStore()

// 認証情報をストアに追加
store.addCredentials(domain, secretText)

println "シークレットテキストが正常に登録されました！ ID: ${secretTextId} 設定値: ${secretPass}"
