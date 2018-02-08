package sftp;

/**
 * Created by sgu197 on 5/11/2017.
 */
public class FileMetaData {

    private int sno;
    private String filename;

    public FileMetaData(int sno, String filename) {
        this.sno = sno;
        this.filename = filename;
    }

    public int getSno() {
        return sno;
    }

    public void setSno(int sno) {
        this.sno = sno;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    @Override
    public String toString() {
        return "Sno: "+this.sno + " FileName: "+this.filename;
    }
}
