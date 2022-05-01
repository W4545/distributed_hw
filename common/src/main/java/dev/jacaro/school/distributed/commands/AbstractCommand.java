package dev.jacaro.school.distributed.commands;

public abstract class AbstractCommand<T> {

    private final String match;

    public AbstractCommand(String match) {
        this.match = match;
    }

    public void matchAndExecute(String string) {
        if (string.equals(match))
            execute();
    }

    protected abstract void execute();
}
