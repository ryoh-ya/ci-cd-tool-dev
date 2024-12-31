import jenkins.model.*
import org.jenkinsci.plugins.workflow.job.*
import org.jenkinsci.plugins.workflow.cps.*

// Jenkinsインスタンスを取得
def jenkins = Jenkins.getInstance()

// 複数Pipelineの定義（ジョブ名とJenkinsfileのパスを設定）
def pipelines = [
    [name: "Pipeline1", scriptPath: "/var/jenkins_home/pipelines/Jenkinsfile1"],
    [name: "Pipeline2", scriptPath: "/var/jenkins_home/pipelines/Jenkinsfile2"],
    [name: "Pipeline3", scriptPath: "/var/jenkins_home/pipelines/Jenkinsfile3"]
]

// 各Pipelineジョブを登録
pipelines.each { pipeline ->
    if (jenkins.getItem(pipeline.name) == null) {
        println "Creating Pipeline job: ${pipeline.name}"

        // Pipelineジョブを作成
        def job = new WorkflowJob(jenkins, pipeline.name)
        def pipelineScript = new File(pipeline.scriptPath).text
        def definition = new CpsFlowDefinition(pipelineScript, true)
        job.setDefinition(definition)

        // ジョブを保存
        jenkins.add(job, pipeline.name)
        job.save()
        println "Pipeline job '${pipeline.name}' has been created with Jenkinsfile from ${pipeline.scriptPath}."
    } else {
        println "Pipeline job '${pipeline.name}' already exists."
    }
}
