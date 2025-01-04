import jenkins.model.*
import hudson.model.*
import hudson.tasks.Shell

def jenkins = Jenkins.getInstance()
def jobName = "example-job"

if (!jenkins.getItem(jobName)) {
    def project = jenkins.createProject(FreeStyleProject.class, jobName)
    
    // シェルスクリプトをビルドステップに追加
    def shellStep = new Shell("echo Hello, Jenkins!")
    project.buildersList.add(shellStep)
    
    project.save() // 変更を保存
    println "Job '${jobName}' has been created with a build step."
} else {
    println "Job '${jobName}' already exists."
}