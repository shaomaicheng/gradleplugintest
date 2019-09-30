package example.shaomai.buildsrc

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils
import org.gradle.api.Project

class AsmTransform extends  Transform {

    private Project project

    AsmTransform(Project project) {
        this.project = project
    }

    @Override
    String getName() {
        return 'asmtransform'
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void transform(Context context, Collection<TransformInput> inputs, Collection<TransformInput> referencedInputs, TransformOutputProvider outputProvider, boolean isIncremental) throws IOException, TransformException, InterruptedException {
        println '==========进去第二个transform=========='
        inputs.each {
            it.directoryInputs.each {
                println "第二个transform里面的class文件路径: ${it.file.absolutePath}"  // 应该是 build文件夹下面 MyTransform 的输出目录
                def dist = outputProvider.getContentLocation(it.name, it.contentTypes, it.scopes, Format.DIRECTORY)
                FileUtils.copyDirectory(it.getFile(), dist)
            }
            it.jarInputs.each {
                println "第二个transform里面的jar文件路径：${it.file.absolutePath}"
                def dist = outputProvider.getContentLocation(it.name, it.contentTypes, it.scopes, Format.JAR)
                FileUtils.copyFile(it.getFile(), dist)
            }
        }
        println '==========结束第二个transform=========='
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        this.transform(
                transformInvocation.context,
                transformInvocation.inputs,
                transformInvocation.referencedInputs,
                transformInvocation.outputProvider,
                transformInvocation.incremental
        )
    }
}