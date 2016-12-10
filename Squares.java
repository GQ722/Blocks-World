/*
 * This class defines all the squares or "blocks"
 */

public class Squares
{
    //initialize with the empty string
    String square = "";

    //Constructor
    Squares(String square){
        this.square = square;
    }

    //Copy Constructor
    Squares(Squares square2){
        this.square = square2.square;
    }

}
