package f3

class BootStrap {

    def init = { servletContext ->
        System.setProperty("org.bytedeco.javacpp.maxbytes", "0");
        System.setProperty("org.bytedeco.javacpp.maxphysicalbytes", "0");
    }
    def destroy = {
    }
}
