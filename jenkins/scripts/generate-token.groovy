import jenkins.model.*
import hudson.model.*
import jenkins.security.*

// Jenkinsインスタンスを取得
def instance = Jenkins.getInstance()

// 管理者ユーザーを取得
def adminUser = instance.getUser("admin")
def tokenProperty = adminUser.getProperty(ApiTokenProperty.class)

// 新しいトークンを生成
def tokenName = "cli-token" // トークン名を設定
def newToken = tokenProperty.tokenStore.generateNewToken(tokenName)

// トークンの値を取得
def tokenValue = newToken.plainValue

// トークンを出力
println "Generated API Token: ${tokenValue}"