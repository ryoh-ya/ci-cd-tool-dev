import jenkins.model.*
import org.jenkinsci.plugins.workflow.job.*
import org.jenkinsci.plugins.workflow.cps.*
import java.nio.file.*

// Jenkinsインスタンスを取得
def jenkins = Jenkins.getInstance()

// Jenkinsfileが格納されているディレクトリ
def pipelineDir = new File("/var/jenkins_home/pipelines")

// ディレクトリをスキャンして各Jenkinsfileをジョブに登録
pipelineDir.listFiles().each { file ->
    if (file.isFile() && file.name.endsWith("Jenkinsfile")) {
        def jobName = file.name.replace("Jenkinsfile", "")        
        if (jobName.endsWith("_")){
            jobName = jobName.substring(0, jobName.lastIndexOf("_"))
        } 
        if (jobName == ""){
            jobName = "default"
        }

        if (jenkins.getItem(jobName) == null) {
            println "Creating Pipeline job: ${jobName}"
            // Pipelineジョブを作成
            def job = new WorkflowJob(jenkins, jobName)
            def pipelineScript = file.text
            def definition = new CpsFlowDefinition(pipelineScript, true)
            job.setDefinition(definition)

            // ジョブを保存
            jenkins.add(job, jobName)
            job.save()
            println "Pipeline job '${jobName}' has been created with Jenkinsfile from ${file.path}."
        } else {
            println "Pipeline job '${jobName}' already exists."
        }
    }
}
