import com.cloudbees.plugins.credentials.*
import com.cloudbees.plugins.credentials.domains.*
import jenkins.model.Jenkins

// 認証情報ストアの取得
def store = Jenkins.instance.getExtensionList('com.cloudbees.plugins.credentials.SystemCredentialsProvider')[0].getStore()

// デフォルトドメインを取得
def domain = Domain.global()

// 認証情報リストを取得
def credentialsList = store.getCredentials(domain)

// XML形式で出力
def writer = new StringWriter()
def xml = new groovy.xml.MarkupBuilder(writer)

xml.credentials {
    credentialsList.each { credential ->
        // 各種認証情報に応じたXMLフォーマット
        if (credential instanceof com.cloudbees.plugins.credentials.impl.CertificateCredentialsImpl) {
            xml.'com.cloudbees.plugins.credentials.impl.CertificateCredentialsImpl' {
                scope(credential.scope)
                id(credential.id)
                description(credential.description)
                keyStoreSource(class: credential.keyStoreSource.class.name) {
                    if (credential.keyStoreSource instanceof com.cloudbees.plugins.credentials.impl.CertificateCredentialsImpl.UploadedKeyStoreSource) {
                        keyStoreBytes(credential.keyStoreSource.keyStoreBytes.encodeBase64().toString())
                        password(credential.keyStoreSource.password.plainText)
                    }
                }
            }
        } else if (credential instanceof com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl) {
            xml.'com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl' {
                scope(credential.scope)
                id(credential.id)
                description(credential.description)
                username(credential.username)
                password(credential.password.plainText)
            }
        } else if (credential instanceof com.cloudbees.plugins.credentials.impl.StringCredentialsImpl) {
            xml.'com.cloudbees.plugins.credentials.impl.StringCredentialsImpl' {
                scope(credential.scope)
                id(credential.id)
                description(credential.description)
                secret(credential.secret.plainText)
            }
        } else {
            xml.'unknown' {
                id(credential.id)
                description(credential.description)
            }
        }
    }
}

// XMLをファイルに保存
def outputFile = new File("exported-credentials.xml")
outputFile.text = writer.toString()

println "認証情報をXML形式でエクスポートしました: ${outputFile.absolutePath}"
