import com.cloudbees.plugins.credentials.*
import com.cloudbees.plugins.credentials.domains.*
import com.cloudbees.jenkins.plugins.sshcredentials.impl.*
import hudson.util.Secret
import jenkins.model.Jenkins

// def systemEnv = System.getenv()
// def sshUser = systemEnv.getOrDefault("SSH_USER", "user") 
// def privateKey  = systemEnv.getOrDefault("SSH_PRIVATE_KEY", "") 
def sshUser = System.getenv("SSH_USER")
def privateKey = System.getenv("SSH_PRIVATE_KEY")

if (sshUser == null || sshUser.trim().isEmpty()) {
    throw new IllegalArgumentException("環境変数 'SSH_USER' が設定されていません。")
}

if (privateKey == null || privateKey.trim().isEmpty()) {
    throw new IllegalArgumentException("環境変数 'SSH_PRIVATE_KEY' が設定されていません。")
}

// 認証情報の設定
def credentialsId = "ssl-cert-id" // ユニークなID
def description = "SSH private key for ${sshUser}"

// // キーストアファイルをBase64エンコードした文字列
// def keyStoreBase64 = """
// BASE64_ENCODED_KEYSTORE
// """.stripIndent().trim()

// Base64文字列をバイト配列にデコード
// def keyStoreBytes = keyStoreBase64.decodeBase64()


// SSH認証情報の作成
def sshCredentials = new BasicSSHUserPrivateKey(
    CredentialsScope.GLOBAL,       // スコープ (GLOBAL または SYSTEM)
    credentialsId,                 // ID
    sshUser,                       // SSHユーザー名
    new BasicSSHUserPrivateKey.DirectEntryPrivateKeySource(privateKey), // プライベートキー
    null,                          // パスフレーズ (必要に応じて設定)
    description                    // 説明
)

// ドメインを指定 (_ はデフォルトドメイン)
def domain = Domain.global()

// 認証情報ストアの取得
def store = Jenkins.instance.getExtensionList('com.cloudbees.plugins.credentials.SystemCredentialsProvider')[0].getStore()

// 認証情報をストアに追加
store.addCredentials(domain, sshCredentials)

println "認証情報が正常に登録されました！ ID: ${credentialsId}"