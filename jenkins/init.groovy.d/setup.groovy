import jenkins.model.*
import hudson.security.*


// 管理者ユーザーの作成
def instance = Jenkins.getInstance()
def hudsonRealm = new HudsonPrivateSecurityRealm(false)
hudsonRealm.createAccount("admin", "admin123") // ユーザー名: admin, パスワード: admin123
instance.setSecurityRealm(hudsonRealm)


// 認可戦略を設定
def strategy = new FullControlOnceLoggedInAuthorizationStrategy()
strategy.setAllowAnonymousRead(false) // 匿名ユーザーの読み取りアクセスを無効化
instance.setAuthorizationStrategy(strategy)

instance.save()