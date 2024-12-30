import jenkins.model.*
import org.jenkinsci.plugins.workflow.job.*
import org.jenkinsci.plugins.workflow.cps.*

// Jenkinsインスタンスを取得
def jenkins = Jenkins.getInstance()

// ジョブ名を指定
def jobName = this.args[0]

// Pipelineスクリプトのパスを指定
def pipelineScriptPath = this.args[1]

// ジョブが存在しない場合に作成
if (jenkins.getItem(jobName) == null) {
    println "Creating Pipeline job: ${jobName}"

    // Pipelineジョブを作成
    def job = new WorkflowJob(jenkins, jobName)
    def pipelineScript = new File(pipelineScriptPath).text
    def definition = new CpsFlowDefinition(pipelineScript, true)
    job.setDefinition(definition)

    // ジョブを保存
    jenkins.add(job, jobName)
    job.save()
    println "Pipeline job '${jobName}' has been created with Jenkinsfile from ${pipelineScriptPath}."
} else {
    println "Pipeline job '${jobName}' already exists."
}
