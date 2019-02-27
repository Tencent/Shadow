package test;

public class MethodRedirectToStatic {

    public static void main(String[] args) {
        System.out.println(new MethodRedirectToStatic().test());
    }

    int add(int a, int b) {
        return a + b;
    }

    public int test() {
        return add(1, 2);
    }
}

class MethodRedirectToStatic2 {
    public static int add2(MethodRedirectToStatic target, int a, int b) {
        return target.add(a * 10, b * 10);
    }
}
