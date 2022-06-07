package test.override;

class ArgBase {

}

class Arg extends ArgBase {

}

class NewArg extends ArgBase {

}

class SuperSuper {
    protected void ss1() {

    }

    final protected void methodCannotOverride() {

    }
}

class Super extends SuperSuper {
    protected void s1(Arg arg) {

    }

    protected void s2(Arg arg) {

    }
}

class NewSuper extends SuperSuper {
    protected void s1(NewArg newArg) {

    }
}

class Foo extends Super {

    @Override
    protected void ss1() {
        super.ss1();
    }

    @Override
    protected void s1(Arg arg) {
        super.s1(arg);
    }

    @Override
    protected void s2(Arg arg) {
        super.s2(arg);
    }
}

class Bar extends Foo {
    @Override
    protected void ss1() {
        super.ss1();
    }

    @Override
    protected void s1(Arg arg) {
        super.s1(arg);
    }

    @Override
    protected void s2(Arg arg) {
        super.s2(arg);
    }
}

/**
 * 这个类通过Foo类型调用其父类SuperSuper类型的方法，
 * 所以这个类本身并没有引用SuperSuper类型。
 */
class UseFooAsSuperSuper {
    void useFooAsSuperSuper() {
        Foo foo = new Foo();
        foo.methodCannotOverride();
    }
}
