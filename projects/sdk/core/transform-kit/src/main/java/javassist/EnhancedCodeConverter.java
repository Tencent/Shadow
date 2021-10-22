package javassist;

import javassist.convert.TransformCallExceptSuperCallToStatic;

public class EnhancedCodeConverter extends CodeConverter {

    public void redirectMethodCallExceptSuperCallToStatic(CtMethod origMethod, CtMethod substMethod) throws CannotCompileException {
        transformers = new TransformCallExceptSuperCallToStatic(transformers, origMethod,
                substMethod);
    }
}
