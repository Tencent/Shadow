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
