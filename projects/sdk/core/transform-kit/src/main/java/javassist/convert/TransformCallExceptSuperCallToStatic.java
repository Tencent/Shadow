package javassist.convert;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.CodeIterator;
import javassist.bytecode.ConstPool;

public class TransformCallExceptSuperCallToStatic extends TransformCallToStatic {
    public TransformCallExceptSuperCallToStatic(Transformer next, CtMethod origMethod, CtMethod substMethod) {
        super(next, origMethod, substMethod);
    }

    // COPY FROM TransformCall
    @Override
    public int transform(CtClass clazz, int pos, CodeIterator iterator, ConstPool cp) throws BadBytecode {
        int c = iterator.byteAt(pos);
        if (c == INVOKEINTERFACE || c == INVOKESTATIC || c == INVOKEVIRTUAL) { // THE ONLY DIFFERENCE WITH TransformCall
            int index = iterator.u16bitAt(pos + 1);
            String cname = cp.eqMember(methodname, methodDescriptor, index);
            if (cname != null && matchClass(cname, clazz.getClassPool())) {
                int ntinfo = cp.getMemberNameAndType(index);
                pos = match(c, pos, iterator,
                        cp.getNameAndTypeDescriptor(ntinfo), cp);
            }
        }

        return pos;
    }

    // COPY FROM TransformCall
    private boolean matchClass(String name, ClassPool pool) {
        if (classname.equals(name))
            return true;

        try {
            CtClass clazz = pool.get(name);
            CtClass declClazz = pool.get(classname);
            if (clazz.subtypeOf(declClazz))
                try {
                    CtMethod m = clazz.getMethod(methodname, methodDescriptor);
                    return m.getDeclaringClass().getName().equals(classname);
                } catch (NotFoundException e) {
                    // maybe the original method has been removed.
                    return true;
                }
        } catch (NotFoundException e) {
            return false;
        }

        return false;
    }
}
