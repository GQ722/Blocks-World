import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

/********************************************************************************************************************
This class defines and implements all the actions for the blocks require to move them
PICK_UP, PUT_DOWN, STACK, MOVE, UNSTACK, NOOP
**********************************************************************************************************************/

//class Action for Blocks
public class Actions_for_Blocks
{
    
    private Squares moves; //to generate moves for the relations

    HashMap<String, Squares> set;  //generating blocks

    private ArrayList<LinkedList<Squares>> location; //to generate a location

    //normal constructor
    Actions_for_Blocks() {
        moves = null;
        location = new ArrayList<>();
        set = new HashMap<>();
    }

    //copy constructor
    Actions_for_Blocks(Actions_for_Blocks square2) {
        this.location = new ArrayList<>();
        for(LinkedList<Squares> pile : square2.location){
            this.location.add(new LinkedList<>(pile));
        }

        if(square2.moves == null){
            this.moves = null;
        }else{
            this.moves = new Squares(square2.moves);
        }
        this.set = new HashMap<>(square2.set);
    }

    //create a block
    public void makeBlock(String block) {
        set.put(block, new Squares(block)); //use put to "put" the new block
    }

    //fetch a block
    public Squares getBlock(String block) {
        return set.get(block); //retrieve the block with "get"
    }

    /*******************************************************************************************************************
       Now we write algorithm for PICK_UP, PUT_DOWN, STACK, and UNSTACK actions
     *******************************************************************************************************************/

    //PICK_UP operation
    private void pick_up(String block) {
        //the pile and location
        for (LinkedList<Squares> pile: location) {
            //if the pile has a size (means its a stack)
            //and the last square is on the pile then do the operations
            if(pile.size() == 1 && pile.getLast().square.equals(block)) {
                moves = pile.getLast();
                location.remove(pile);
                return;
            }
        }
    }//end of pick_up

    //PUT_DOWN operation
    private void put_down(String block) {
        LinkedList<Squares> pile = new LinkedList<>();
        //you make a new linked list, add a new block in the pile, and then add the pile to the location
        pile.add(getBlock(block));
        location.add(pile);
        moves = null;
    }//end of put_down

    //STACK operation
    private void stack(String block, String mode) {
        for(LinkedList<Squares> pile: location) {
            if(pile.getLast().square.equals(mode)) {
                pile.add(getBlock(block));
                moves = null;
                return;
            }
        }
    }//end of stack

    //UNSTACK operation
    private void unstack(String block, String mode) {
        for(LinkedList<Squares> pile: location) {
            if(pile.size() == 1) {
                return;
            } else{
                if(pile.get(pile.size() - 2).square.equals(mode)) {
                    moves = pile.getLast();
                    pile.removeLast();
                    return;
                }
            }
        }//end of for loop
    }//end of unstack

    //now we start analyzing the actions
    //this comes from reading the file
    public void analyze(String string) {
        int endIndex = string.indexOf('[');

        //if there is nothing to move then do NOOP (NOOP = No Operation beyond this point)
        if(endIndex == -1) {
            moves = null;
        } else {
            String operation = string.substring(0, endIndex);
            String block = string.substring(endIndex + 1, string.length() - 1);

            switch(operation) {
                case "TABLE":
                    put_down(block);  //put down on table
                    break;
                case "CLEAR":
                    //when there a word clear in the text file, print clear
                    System.out.println("The text file has CLEAR relation");
                    break;
                case "ON":
                    String[] moveBlocks1 = block.split(":");
                    // when block is on other block, split "on" relation
                    stack(moveBlocks1[0], moveBlocks1[1]);
                    break;
                case "ABOVE":
                    String[] moveBlocks2 = block.split(":");
                    //when block is above other block, split "above" relation
                    stack(moveBlocks2[0], moveBlocks2[1]);
                    break;
            }
        }
    }//end of analyze

    //print the list of current state
    LinkedList<String> thisState() {
        LinkedList<String> string = new LinkedList<>();

        /*
        Using try/catch to handle 'Out Of Memory Error'

        The error occurs when there are too many blocks used

        For this program the highest number of blocks supported is 8 which are A B C D E F G H

        However, if we use 9 blocks then the system will take too long to calculate the blocks
        and eventually sends out OutOfMemoryError
        */
        try {
            // /now find each pile of the squares and print it
            for(LinkedList<Squares> pile: location) {
             Squares endSquare = null;

                //now find each squares in the pile and print it
                for(int stack = 0; stack < pile.size(); stack++) {
                    if(stack == 0) {
                         string.add("TABLE[" + pile.get(0).square + "]");
                    } else {
                        string.add("ON[" + pile.get(stack).square + "," + endSquare.square + "]");
                        string.add("ABOVE[" + pile.get(stack).square + "," + endSquare.square + "]");
                    }
                    endSquare = pile.get(stack);
                }
             string.add("CLEAR[" + endSquare.square + "]");
            }

            //if there is nothing to move
            if(moves == null) {
                string.add("NOOP");
              //if move has something stored then print the MOVE
            } else {
                string.add("MOVE[" + moves.square + "]" );
            }
        } catch(OutOfMemoryError mem) {
            //if our of memory error is found then print the following
            System.out.println("Too many blocks used, the program is out of memory!");
            //now immediately
            System.exit(1);
        }
        return string;
    }

    //Now it's time to use the actions to make the block move
    //The "Action taken: ..." must match the input file
    public void actions(Relation_for_Blocks relation) {

        switch(relation.relation1) {
            //when pick_up is called on relation
            case "Action taken: PICK_UP":
                pick_up(relation.relation2);
                break;
            //when put_down is called on relation
            case "Action taken: PUT_DOWN":
                put_down(relation.relation2);
                break;
            //when stack is called on relation
            case "Action taken: STACK":
                stack(relation.relation2, relation.mode);
                break;
            //when unstack is called on relation
            case "Action taken: UNSTACK":
                unstack(relation.relation2, relation.mode);
                break;
        }
    }//end of actions

    //finalTrail
    Actions_for_Blocks finalTrail(LinkedList<Relation_for_Blocks> trail) {
        Actions_for_Blocks newSet = new Actions_for_Blocks(this); //'this' relates to square2 in Squares.java class

        for(int i = 0; i < trail.size(); i++) {
            newSet.actions(trail.get(i));
        }
        return newSet;
    }//end of finalTrail

    //print all the actions applied
    LinkedList<Relation_for_Blocks> actionsApplied() {
        LinkedList<Relation_for_Blocks> relation = new LinkedList<>();
        //check if there is anything to move, if no,
        if(moves == null) {
            for(LinkedList<Squares> pile: location) {
                //here we combine pick up and unstack operation
                //
                if(pile.size() == 1) {
                    //pick up from the table ( NOT PILE )
                    relation.add(new Relation_for_Blocks("Action taken: PICK_UP", pile.getLast().square));
                } else {
                    //unstack the pile
                    relation.add(new Relation_for_Blocks("Action taken: UNSTACK", pile.getLast().square, pile.get(pile.size() - 2).square));
                }
            }
        }
        //if there is something to move then do that first
        else {
            //put down the block that is being moved
            relation.add(new Relation_for_Blocks("Action taken: PUT_DOWN", moves.square));
            for(LinkedList<Squares> pile: location) {
                //now when you put down on a pile, make it a stack
                relation.add(new Relation_for_Blocks("Action taken: STACK", moves.square, pile.getLast().square));
            }
        }
        return relation;
    }

    //lets check if the final set is validating with our stated final state
    public boolean validates(Actions_for_Blocks finalState) {
        Set<String> currentSet = new HashSet<>(thisState());
        Set<String> finalSet   = new HashSet<>(finalState.thisState());
        return currentSet.equals(finalSet);
    }

    //printing out the states
    String print() {
        String string = "";
        //now find each pile of the squares and print it
        for(LinkedList<Squares> pile: location) {
            Squares endSquare = null;

            //now find each squares in the pile and print it
            for(int stack = 0; stack < pile.size(); stack++) {
                if(stack == 0) {
                    //print out what is on the table
                    string = string + "TABLE[" + pile.get(0).square + "],\t\t";
                } else {
                    //print out what is on the block
                    string = string + "ON["    + pile.get(stack).square + ":" + endSquare.square + "], or " + "ABOVE[" + pile.get(stack).square + ":" + endSquare.square + "],\t\t";
                }
                endSquare = pile.get(stack);
            }
            //and finally print if the last block is clear
            string = string + "CLEAR[" + endSquare.square + "],\t";
        }

        //Print out all the moves possibility
        if(moves == null) {
            //if nothing to move print NOOP (No Operation)
            string = string + "NOOP \n";
        } else {
            //if there is a block to move print that block
            string = string + "MOVE[" + moves.square + "],\t NOOP\n";
        }
        return string;
    }// end of print
}//end of Actions for Blocks
