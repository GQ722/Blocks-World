/*
 * This class defines all the squares or "blocks"
 */

public class Squares
{
    String square = "";

    Squares(String square){
        this.square = square;
    }

    Squares(Squares repeat){
        this.square = repeat.square;
    }
}
