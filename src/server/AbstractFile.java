package server;

import java.util.Objects;

class AbstractFile {
    private final String name;
    private final String content;

    AbstractFile(String name, String content) {
        this.name = name;
        this.content = content;
    }

    String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractFile that = (AbstractFile) o;
        return Objects.equals(getName(), that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }

    @Override
    public String toString() {
        return content;
    }
}
