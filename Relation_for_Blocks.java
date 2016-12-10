/*
 * This class defines the relation for all the blocks
 * ABOVE, ON, CLEAR, TABLE
 */

//class Relation for Blocks
public class Relation_for_Blocks
{
    //variables for the relations
    String relation1;
    String relation2;
    String mode;

    //Constructor
    Relation_for_Blocks(String relation1, String relation2) {
        this.relation1 = relation1;
        this.relation2 = relation2;
        this.mode = "";
    }

    //Copy constructor
    Relation_for_Blocks(String relation1, String relation2, String mode) {
        this.relation1 = relation1;
        this.relation2 = relation2;
        this.mode = mode;
    }

}//end of Relation_for_Blocks class
