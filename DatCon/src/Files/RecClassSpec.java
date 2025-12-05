package src.Files;


public class RecClassSpec extends RecSpec {

    Class<src.DatConRecs.Record> recClass = null;

    int lengths[] = null;

    @SuppressWarnings("unchecked")
    public RecClassSpec(Class<?> recClass, int id, int length) {
        super(id, length);
        this.recClass = (Class<src.DatConRecs.Record>) recClass;
        if (length == -1) {
            setRecType(RecType.STRING);
        }
    }

    @SuppressWarnings("unchecked")
    public RecClassSpec(Class<?> recClass, int id, int... lengths) {
        super(id, -1);
        this.recClass = (Class<src.DatConRecs.Record>) recClass;
        this.lengths = new int[lengths.length];
        for (int i = 0; i < lengths.length; i++) {
            this.lengths[i] = lengths[i];
        }
    }

    public boolean lengthOK(int l) {
        if (getRecType() == RecType.STRING) {
            return true;
        }
        if (l == getLength()) {
            return true;
        }
        if (lengths != null) {
            for (int i = 0; i < lengths.length; i++) {
                if (l == lengths[i]) {
                    return true;
                }
            }
        }
        return false;
    }

    public Class<src.DatConRecs.Record> getRecClass() {
        return recClass;
    }

    public String toString() {
        return recClass.getName();
    }

}
