import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

/*
 * This class defines and implements all the actions for the blocks require to move them
 * PICK_UP, PUT_DOWN, STACK, MOVE, UNSTACK, NOOP
 */

//class Action for Blocks
public class Actions_for_Blocks
{
    private ArrayList<LinkedList<Squares>> solid;
    private Squares moves;

    HashMap<String, Squares> set;  //generating blocks

    Actions_for_Blocks() {
        moves = null;
        solid = new ArrayList<LinkedList<Squares>>();
        set = new HashMap<String, Squares>();
    }

    Actions_for_Blocks(Actions_for_Blocks repeat) {
        this.solid = new ArrayList<LinkedList<Squares>>();
        for(LinkedList<Squares> pile : repeat.solid){
            this.solid.add(new LinkedList<Squares>(pile));
        }

        if(repeat.moves == null){
            this.moves = null;
        }else{
            this.moves = new Squares(repeat.moves);
        }
        this.set = new HashMap<String, Squares> (repeat.set);

    }

    //create a block
    void makeBlock(String block) {
        set.put(block, new Squares(block));
    }

    //fetch a block
    Squares getBlock(String block) {
        return set.get(block);
    }

    //PICK_UP operation
    void pick_up(String block) {
        for (LinkedList<Squares> pile: solid) {
            if(pile.size() == 1 && pile.getLast().square.equals(block)) {
                moves = pile.getLast();
                solid.remove(pile);
                return;
            }
        }
    }//end of pick_up

    //PUT_DOWN operation
    void put_down(String block) {
        LinkedList<Squares> pile = new LinkedList<Squares>();
        pile.add(getBlock(block));
        solid.add(pile);
        moves = null;
    }//end of put_down

    //STACK operation
    void stack(String block, String mode) {
        for(LinkedList<Squares> pile: solid) {
            if(pile.getLast().square.equals(mode)) {
                pile.add(getBlock(block));
                moves = null;
                return;
            }
        }
    }//end of stack

    //UNSTACK operation
    void unstack(String block, String mode) {
        for(LinkedList<Squares> pile: solid) {
            if(pile.size() == 1) {
                //return;
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
    void analyze(String string) {
        int endIndex = string.indexOf('[');

        //if there is nothing to move then do NOOP
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
                    break;			  //when clear do nothing
                case "ON":
                    String[] moveBlocks = block.split(":");
                    // when block is on other block, simply split that "on" relation
                    stack(moveBlocks[0], moveBlocks[1]);
                    break;
                case "MOVE":
                    moves = getBlock(block);
                    break;
            }
        }
    }//end of analyze

    //printing out the states
    String print() {
        String string = "";
        //now find each pile of the squares and print it
        for(LinkedList<Squares> pile: solid) {
            Squares endBlock = null;

            //now find each squares in the pile and print it
            for(int stack = 0; stack < pile.size(); stack++) {
                if(stack == 0) {
                    //print out what is on the table
                    string = string + "TABLE[" + pile.get(0).square + "],\t";
                } else {
                    //print out what is on the block
                    string = string + "ON["    + pile.get(stack).square + ":" + endBlock.square + "],\t";
                }
                endBlock = pile.get(stack);
            }
            //and finally print if the last block is clear
            string = string + "CLEAR[" + endBlock.square + "],\t";
        }

        //Print out all the moves possibility
        if(moves == null) {
            //if nothing to move print NOOP (No Operation)
            string = string + "NOOP";
        } else {
            //if there is a block to move print that block
            string = string + "MOVE[" + moves.square + "]";
        }
        return string;
    }// end of print

    //print the list of current state
    LinkedList<String> thisState() {
        LinkedList<String> string = new LinkedList<String>();

        //now find each pile of the squares and print it
        for(LinkedList<Squares> pile: solid) {
            Squares endBlock = null;

            //now find each squares in the pile and print it
            for(int stack = 0; stack < pile.size(); stack++) {
                if(stack == 0) {
                    string.add("TABLE[" + pile.get(0).square + "]");
                } else {
                    string.add("ON[" + pile.get(stack).square + "," + endBlock.square + "]");
                }

                endBlock = pile.get(stack);
            }
            string.add("CLEAR[" + endBlock.square + "]");
        }

        if(moves == null) {
            string.add("NOOP");
        } else {
            string.add("MOVE[" + moves.square + "]");
        }
        return string;
    }

    //Now it's time to use the actions to make the block move
    void actions(Relation_for_Blocks relation) {

        switch(relation.relation1) {
            case "PICK_UP":
                pick_up(relation.relation2);
                break;
            case "PUT_DOWN":
                put_down(relation.relation2);
                break;
            case "STACK":
                stack(relation.relation2, relation.mode);
                break;
            case "UNSTACK":
                unstack(relation.relation2, relation.mode);
                break;
        }
    }//end of actions

    Actions_for_Blocks finalTrail(LinkedList<Relation_for_Blocks> trail) {
        Actions_for_Blocks newSet = new Actions_for_Blocks(this);

        for(int i = 0; i < trail.size(); i++) {
            newSet.actions(trail.get(i));
        }
        return newSet;
    }//end of finalTrail

    //print all the actions applied
    LinkedList<Relation_for_Blocks> actionsApplied() {
        LinkedList<Relation_for_Blocks> relation = new LinkedList<Relation_for_Blocks>();
        //check if there is anything to move, if no,
        if(moves == null) {
            for(LinkedList<Squares> pile: solid) {
                if(pile.size() == 1) {
                    relation.add(new Relation_for_Blocks("PICK_UP", pile.getLast().square));
                } else {
                    relation.add(new Relation_for_Blocks("UNSTACK", pile.getLast().square, pile.get(pile.size() - 2).square));
                }
            }
        }
        //if there is something to move then do that first
        else {
            relation.add(new Relation_for_Blocks("PUT_DOWN", moves.square));
            for(LinkedList<Squares> pile: solid) {
                relation.add(new Relation_for_Blocks("STACK", moves.square, pile.getLast().square));
            }
        }

        return relation;
    }

    //lets check if the final set is validating with our stated final state
    boolean validates(Actions_for_Blocks finalState) {
        Set<String> currentSet = new HashSet<String>(thisState());
        Set<String> finalSet   = new HashSet<String>(finalState.thisState());
        return currentSet.equals(finalSet);

    }
}//end of Actions for Blocks
