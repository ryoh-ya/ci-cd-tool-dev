import com.cloudbees.plugins.credentials.*
import com.cloudbees.plugins.credentials.domains.*
import com.cloudbees.plugins.credentials.impl.*
import hudson.util.Secret
import jenkins.model.Jenkins

// 認証情報の設定
def credentialsId = "ssl-cert-id" // ユニークなID
def description = "SSL Certificate for XYZ"
def keyStorePassword = "your_keystore_password" // キーストアのパスワード

// キーストアファイルをBase64エンコードした文字列
def keyStoreBase64 = """
BASE64_ENCODED_KEYSTORE
""".stripIndent().trim()

// Base64文字列をバイト配列にデコード
def keyStoreBytes = keyStoreBase64.decodeBase64()

// キーストア認証情報の作成
def certificateCredentials = new CertificateCredentialsImpl(
    CredentialsScope.GLOBAL,       // スコープ (GLOBAL または SYSTEM)
    credentialsId,                 // ID
    description,                   // 説明
    new CertificateCredentialsImpl.UploadedKeyStoreSource(keyStoreBytes, Secret.fromString(keyStorePassword))
)

// ドメインを指定 (_ はデフォルトドメイン)
def domain = Domain.global()

// 認証情報ストアの取得
def store = Jenkins.instance.getExtensionList('com.cloudbees.plugins.credentials.SystemCredentialsProvider')[0].getStore()

// 認証情報をストアに追加
store.addCredentials(domain, certificateCredentials)

println "認証情報が正常に登録されました！ ID: ${credentialsId}"