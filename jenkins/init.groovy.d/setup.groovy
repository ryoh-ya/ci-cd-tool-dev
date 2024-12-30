import jenkins.model.*
import hudson.security.*
import jenkins.security.*


// 環境変数からユーザー名とパスワードを取得

def systemEnv = System.getenv()
def username = systemEnv.getOrDefault("JENKINS_USER", "admin") // 環境変数がなければ "admin" を使用
def password = systemEnv.getOrDefault("JENKINS_PASS", "admin123") // 環境変数がなければ "admin123" を使用



// 管理者ユーザーの作成
def instance = Jenkins.getInstance()
def hudsonRealm = new HudsonPrivateSecurityRealm(false)
hudsonRealm.createAccount("admin", "admin123") // ユーザー名: admin, パスワード: admin123
instance.setSecurityRealm(hudsonRealm)


// 認可戦略を設定
def strategy = new FullControlOnceLoggedInAuthorizationStrategy()
strategy.setAllowAnonymousRead(false) // 匿名ユーザーの読み取りアクセスを無効化
instance.setAuthorizationStrategy(strategy)

// 管理者ユーザーを取得
def adminUser = instance.getUser("admin")
def tokenProperty = adminUser.getProperty(ApiTokenProperty.class)

instance.save()

// 新しいトークンを生成
def tokenName = "setup-token" // トークン名を設定
def newToken = tokenProperty.tokenStore.generateNewToken(tokenName)


// トークンの値を取得
def tokenValue = newToken.plainValue
// トークンの値をログに出力
println "Generated API Token for admin: ${tokenValue}"
def envFile = new File("/var/jenkins_home/.env.setup")
envFile.text = """
export JENKINS_AUTH=admin:${tokenValue}
export JENKINS_URL=http://localhost:8080
alias jenkins-cli="java -jar /var/jenkins_home/jenkins-cli.jar -s \$JENKINS_URL -auth \$JENKINS_AUTH"
"""
// ログに出力（デバッグ用）
println "Environment file created with API token and alias(jenkins-cli)."


