package ahmadadam96.comicbookmovies;

/**
 * Created by ahmad on 2017-03-19.
 */

public class MovieCode {
    private String mCode;
    private String mUniverse;
    MovieCode(String code, String universe){
        mCode = code;
        mUniverse = universe;
    }
    public String getCode(){
        return mCode;
    }
    public String getUniverse(){
        return mUniverse;
    }
}
