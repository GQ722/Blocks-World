import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

/*
 * Name:    Devesh Patel
 * Class:   CS 4850 Foundation of AI
 * Date:    12/03/26
 * Project: The program takes an initial and final states of the seven blocks with 4 location
 * 		    and produces all the necessary states that are required to reach from initial state
 * 			to final state. A user must input their initial and final state in the text file of
 * 			of format .txt to successfully run the program.
 */

//World of Blocks class
public class World_of_Blocks_main
{
    //algorithm to move the blocks
    static LinkedList<Relation_for_Blocks> trail (Actions_for_Blocks initialState, Actions_for_Blocks finalState) {

        //if the initial states validates with final state then return the same block; no work to do here!
        if(initialState.validates(finalState)) {
            return new LinkedList<Relation_for_Blocks>();
        }

        //define a new sequence to track
        LinkedList<LinkedList<Relation_for_Blocks>> sequence     = new LinkedList<LinkedList<Relation_for_Blocks>>();
        //define a new visitedTrail to track all the visited blocks
        Set<LinkedList<Relation_for_Blocks>>        visitedTrail = new HashSet<LinkedList<Relation_for_Blocks>>();
        //define a new visitedSet to track the new set which has the new moved blocks
        Set<LinkedList<String>> 			        visitedSet   = new HashSet<LinkedList<String>>();

        //here we apply the actions for move0 (initial move)
        LinkedList<Relation_for_Blocks> move0 = initialState.actionsApplied();

        for(Relation_for_Blocks relation1: move0) {
            LinkedList<Relation_for_Blocks> initialTrail = new LinkedList<Relation_for_Blocks>();
            initialTrail.add(relation1);
            sequence.add(initialTrail);
            visitedTrail.add(initialTrail);
        }

        while(!sequence.isEmpty()) {
            LinkedList<Relation_for_Blocks> trail = sequence.pop();
            Actions_for_Blocks newSet = initialState.finalTrail(trail);

            if(visitedSet.contains(newSet.thisState())) {
                continue;
            } else {
                visitedSet.add(newSet.thisState());
            }

            if(newSet.validates(finalState)) {
                return trail;
            }

            LinkedList<Relation_for_Blocks> trackRelation = newSet.actionsApplied();

            for(Relation_for_Blocks relation2: trackRelation) {
                LinkedList<Relation_for_Blocks> newTrail = new LinkedList<Relation_for_Blocks>(trail);
                newTrail.add(relation2);
                if(!visitedTrail.contains(newTrail)) {
                    sequence.add(newTrail);
                    visitedTrail.add(newTrail);
                }
            }
        }
        return new LinkedList<Relation_for_Blocks>();
    }

    //printing all the states that makes up the final state
    static void finalPrint(Actions_for_Blocks initial, LinkedList<Relation_for_Blocks> trl) {
        for(Relation_for_Blocks relation: trl) {
            String string = "";

            string = string + relation.relation1 + "[" + relation.relation2;

            if(!relation.mode.equals("")) {
                string = string + ":" + relation.mode;
            }

            initial.actions(relation);
            string = string + "]\t:" + initial.print();
            System.out.println(string);
        }
    }

    public static void main(String[] args) {

        //if file in put is invalid
        if(args.length == 0) {
            System.out.println("Invalid File Input, try again with .txt file!");
            return;
        }

        //read the file
        File f;
        BufferedReader br;

        //try/catch for reading the file
        try{
            f = new File(args[0]);
            br = new BufferedReader(new FileReader(f));
        } catch(FileNotFoundException e) {
            System.out.println("Your .txt file does not exist!");
            return;
        }

        //new action for blocks state
        Actions_for_Blocks state = new Actions_for_Blocks();
        //new action for blocks finalState
        Actions_for_Blocks finalState = new Actions_for_Blocks();

        int chk = 0;
        String line;

        System.out.println("Reading the file " + f);
        System.out.println("This may take a while depending on the number of blocks.");

        try{
            while((line = br.readLine()) != null) {

                if(line.matches("NUMBER OF BLOCKS: *")) {
                    chk = 1;
                    continue;
                } else if(line.matches("DEFINE INITIAL STATE: *")) {
                    chk = 2;
                    continue;
                } else if(line.matches("DEFINE FINAL STATE: *")) {
                    chk = 3;
                    continue;
                }

                if (!line.equals("")) {
                    switch(chk) {
                        case 1:
                            String[] blocks = line.split("\\s");
                            for (String string: blocks ) {
                                state.makeBlock(string);
                                finalState.makeBlock(string);
                            }
                            break;
                        case 2:
                            state.analyze(line);
                            break;
                        case 3:
                            finalState.analyze(line);
                            break;
                    }
                }
            }
        } catch(IOException io) {
            System.out.println("The file cannot be read, something went wrong.");
            return;
        }

        //new actions for blocks finalSet
        Actions_for_Blocks finalSet = new Actions_for_Blocks(state);
        LinkedList<Relation_for_Blocks> finalTrail = trail(finalSet, finalState);
        finalPrint(finalSet, finalTrail);
    }
}

