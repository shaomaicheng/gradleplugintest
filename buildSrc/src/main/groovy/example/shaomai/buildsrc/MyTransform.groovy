package example.shaomai.buildsrc

import com.android.build.api.transform.Context
import com.android.build.api.transform.DirectoryInput
import com.android.build.api.transform.JarInput
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformException
import com.android.build.api.transform.TransformInput
import com.android.build.api.transform.TransformInvocation
import com.android.build.api.transform.TransformOutputProvider
import com.android.build.gradle.internal.pipeline.TransformManager
import org.gradle.api.Project

import java.util.function.Consumer

class MyTransform extends Transform {
    private Project project;

    public MyTransform(Project project) {
        this.project = project;
    }

    @Override
    public String getName() {
        return "MyTransform";
    }

    @Override
    public Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS;
    }

    @Override
    public Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT;
    }

    @Override
    public boolean isIncremental() {
        return false;
    }

    @Override
    public void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        this.transform(transformInvocation.getContext(), transformInvocation.getInputs(),
                transformInvocation.getReferencedInputs(),
                transformInvocation.getOutputProvider(),
                transformInvocation.isIncremental());
    }

    @Override
    public void transform(Context context, Collection<TransformInput> inputs, Collection<TransformInput> referencedInputs, TransformOutputProvider outputProvider, boolean isIncremental) throws IOException, TransformException, InterruptedException {
        System.out.println("======进入transform======");
        inputs.forEach(new Consumer<TransformInput>() {
            @Override
            public void accept(TransformInput transformInput) {
                transformInput.getDirectoryInputs().forEach(new Consumer<DirectoryInput>() {
                    @Override
                    public void accept(DirectoryInput directoryInput) {
                        String directoryInputName = directoryInput.getName();
                        File file = directoryInput.getFile();
                        String filePath = file.getAbsolutePath();
                        Set<QualifiedContent.ContentType> contentTypes = directoryInput.getContentTypes();
                        System.out.println("====directoryInputName: " + directoryInputName + "==== filePath:" + filePath + "======contentTypes:"+contentTypes.toString()+"=====");

                        InjectPools.inject(filePath, project);

                    }
                });

                // jar
                for (JarInput jarInput : transformInput.getJarInputs()) {
                    String name = jarInput.getName();
                    String jarPath = jarInput.getFile().getAbsolutePath();
                    Set<QualifiedContent.ContentType> contentTypes = jarInput.getContentTypes();
                    System.out.println("===JarName: " + name + "==== filePath:" + jarPath + "======contentTypes:"+contentTypes.toString()+"=====");
                }
            }
        });
        System.out.println("======结束transform======");
    }
}
