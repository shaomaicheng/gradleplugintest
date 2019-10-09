package example.shaomai.buildsrc

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class AddTryCatchClassAdapter extends ClassVisitor {

    private String className
    private List<String> methodNames

    AddTryCatchClassAdapter(ClassWriter classWriter) {
        super(Opcodes.ASM5, classWriter)
        methodNames = new ArrayList<>()
    }

    @Override
    void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        className = name.replace("/", ".")
        super.visit(version, access, name, signature, superName, interfaces)
        println "add try catch class:  ${className}"
    }

    @Override
    MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        if (methodNames.contains(name)) {
            return new AddTryCatchAdviceAdapter(Opcodes.ASM5, super.visitMethod(
                    access, name, descriptor, signature, exceptions
            ), access, name, descriptor)
        }
        return super.visitMethod(access, name, descriptor, signature, exceptions)
    }
}