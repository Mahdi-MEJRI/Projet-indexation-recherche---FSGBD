package fr.miage.fsgbd;

public class TestLong implements Executable<Long>, java.io.Serializable {
    public boolean execute(Long long1, Long long2) {
        return (long1 < long2);
    }
}
