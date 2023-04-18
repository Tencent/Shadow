package javassist;

import javassist.convert.TransformCallExceptSuperCallToStatic;
import javassist.convert.TransformNewClassFix;

public class EnhancedCodeConverter extends CodeConverter {

    public void redirectMethodCallExceptSuperCallToStatic(CtMethod origMethod, CtMethod substMethod) throws CannotCompileException {
        transformers = new TransformCallExceptSuperCallToStatic(transformers, origMethod,
                substMethod);
    }

    public void replaceNew(CtClass oldClass, CtClass newClass) {
        transformers = new TransformNewClassFix(transformers, oldClass.getName(),
                newClass.getName());
    }
}
