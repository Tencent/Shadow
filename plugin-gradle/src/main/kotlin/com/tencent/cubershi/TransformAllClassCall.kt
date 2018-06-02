package com.tencent.cubershi

import javassist.CtClass
import javassist.bytecode.BadBytecode
import javassist.bytecode.CodeIterator
import javassist.bytecode.ConstPool
import javassist.bytecode.Opcode
import javassist.convert.Transformer


class TransformAllClassCall(
        next: Transformer?
        , val classname: String
        , val newClassname: String
) : Transformer(next) {

    /**
     * Modify INVOKEINTERFACE, INVOKESPECIAL, INVOKESTATIC and INVOKEVIRTUAL
     * so that a different method is invoked.  The class name in the operand
     * of these instructions might be a subclass of the target class specified
     * by `classname`.   This method transforms the instruction
     * in that case unless the subclass overrides the target method.
     */
    @Throws(BadBytecode::class)
    override fun transform(clazz: CtClass, pos: Int, iterator: CodeIterator,
                           cp: ConstPool): Int {
        var pos = pos
        val c = iterator.byteAt(pos)
        if (c == Opcode.INVOKEINTERFACE || c == Opcode.INVOKESPECIAL
                || c == Opcode.INVOKESTATIC || c == Opcode.INVOKEVIRTUAL) {
            val index = iterator.u16bitAt(pos + 1)

            try {
                val methodrefClassName = cp.getMethodrefClassName(index)
                val methodrefNameAndType = cp.getMethodrefNameAndType(index)
                val nameAndTypeName = cp.getNameAndTypeName(methodrefNameAndType)
                val memberNameAndType = cp.getMemberNameAndType(index)
                val nameAndTypeDescriptor = cp.getNameAndTypeDescriptor(memberNameAndType)

                if (methodrefClassName == classname) {
                    val log = StringBuilder()
                    log.append("In class:[").append(clazz.classFile.name)
                    log.append("],it calls ").append(printIndex(cp, index))

                    System.out.println(log.toString())


                    pos = replace(pos, iterator, nameAndTypeName, nameAndTypeDescriptor, cp)

                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return pos
    }

    @Throws(BadBytecode::class)
    private fun replace(pos: Int, iterator: CodeIterator,
                        methodname: Int,
                        nameAndTypeDescriptor: Int, cp: ConstPool): Int {
        val nt = cp.addNameAndTypeInfo(methodname, nameAndTypeDescriptor)
        val ci = cp.addClassInfo(newClassname)

        val newIndex = cp.addMethodrefInfo(ci, nt)

        System.out.println("new Method:" + printIndex(cp, newIndex))

        iterator.write16bit(newIndex, pos + 1)
        return pos
    }

    private fun printIndex(cp: ConstPool, index: Int): String {
        try {
            val methodrefClassName = cp.getMethodrefClassName(index)
            val methodrefNameAndType = cp.getMethodrefNameAndType(index)
            val nameAndTypeName = cp.getNameAndTypeName(methodrefNameAndType)
            val methodrefName = cp.getUtf8Info(nameAndTypeName)
            val memberNameAndType = cp.getMemberNameAndType(index)
            val nameAndTypeDescriptor = cp.getNameAndTypeDescriptor(memberNameAndType)
            val methodSignature = cp.getUtf8Info(nameAndTypeDescriptor)

            val methodStringBuilder = StringBuilder()
            methodStringBuilder.append(methodrefClassName)
            methodStringBuilder.append("#").append(methodrefName)
            methodStringBuilder.append(" ").append(methodSignature)

            return methodStringBuilder.toString()
        } catch (e: Exception) {
            e.printStackTrace()
            return ""
        }
    }
}