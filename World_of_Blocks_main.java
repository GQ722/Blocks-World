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
 * Project: The program takes an initial and final states of the 7 blocks on 4 location
 * 		    and produces all the necessary states that are required to perform from initial state
 * 			to final state. A user must input their initial and final state in the text file of
 * 			of format .txt to successfully run the program.
 */

//World of Blocks class
public class World_of_Blocks_main {
    //algorithm to move the blocks
    static LinkedList<Relation_for_Blocks> trail(Actions_for_Blocks initialState, Actions_for_Blocks finalState) {

        //if the initial states validates with final state then return the same block; no work to do here!
        if (initialState.validates(finalState)) {
            System.out.println("The initial and final states are exactly the same, there is no actions to be taken.");
            System.out.println("NOOP");
            return new LinkedList<>();
        }

        //define a new sequence to track
        LinkedList<LinkedList<Relation_for_Blocks>> sequence = new LinkedList<>();
        //define a new visitedTrail to track all the visited blocks
        Set<LinkedList<Relation_for_Blocks>> visitedTrail = new HashSet<>();
        //define a new visitedSet to track the new set which has the new moved blocks
        Set<LinkedList<String>> visitedSet = new HashSet<>();

        //here we apply the actions for move0 (initial move)
        LinkedList<Relation_for_Blocks> move0 = initialState.actionsApplied();

        //here we initialize the move then call
        for (Relation_for_Blocks relation1 : move0) {
            LinkedList<Relation_for_Blocks> initialTrail = new LinkedList<>();
            //our first trail
            initialTrail.add(relation1);
            //add that to the sequence
            sequence.add(initialTrail);
            //and now track that as a visitedTrail
            visitedTrail.add(initialTrail);
        }

        //now there is something in the sequence, so as long as the
        //sequence is not empty, go in the loop and start arranging
        //squares and locations for the squares
        while (!sequence.isEmpty()) {
            //removing the squares in the sequence one by one
            LinkedList<Relation_for_Blocks> trail = sequence.pop();
            //make a new set for the current state and
            Actions_for_Blocks newSet = initialState.finalTrail(trail);

            //here we check the visited set. If the visited set contains
            //the current set then simply continue, if not we add the state
            //to the visited set in the Set
            if (visitedSet.contains(newSet.thisState())) {
                continue;
            } else {
                visitedSet.add(newSet.thisState());
            }

            //now call the validates method
            //to see if the new set validates
            //the final state
            if (newSet.validates(finalState)) {
                return trail;
            }

            //now we apply the actions method and save it to a LinkedList
            LinkedList<Relation_for_Blocks> trackRelation = newSet.actionsApplied();

            for (Relation_for_Blocks relation2 : trackRelation) {
                //making a new trail then we add the relations to the new trail and apply all the methods
                LinkedList<Relation_for_Blocks> newTrail = new LinkedList<>(trail);
                newTrail.add(relation2);
                if (!visitedTrail.contains(newTrail)) {
                    sequence.add(newTrail);
                    visitedTrail.add(newTrail);
                }
            }
        }
        return new LinkedList<>();
    }

    //printing all the states that makes up the final state
    static void finalPrint(Actions_for_Blocks initialize, LinkedList<Relation_for_Blocks> trl) {
        //as long as the trail matches all the relations start printing
        for (Relation_for_Blocks relation : trl) {
            String string = "";

            string = string + relation.relation1 + "[" + relation.relation2;

            //this handles printing of On[x:y] state where x and y are number of blocks defined in the file
            if (!relation.mode.equals("")) {
                string = string + ":" + relation.mode;
            }

            //call the actions method for initializing the relation
            initialize.actions(relation);
            //then print with ']' and separate by 'tab'
            string = string + "]\t New State => " + initialize.print();
            System.out.println(string);
        }
    }

    public static void main(String[] args) {

        /*
        Start calculating the time needed to execute the program when main function is called
        */
        long startTime = System.currentTimeMillis();

        long total = 0;
        for (int i = 0; i < 10000000; i++) {
            total = total + i;
        }

        //if file input is invalid
        if (args.length == 0) {
            System.out.println("Invalid file input, try again with .txt file!");
            System.out.println("Program must run from terminal.");
            return;
        }

        //read the file
        File f;
        BufferedReader br;

        //try/catch for reading the file
        try {
            f = new File(args[0]);
            br = new BufferedReader(new FileReader(f));
        } catch (FileNotFoundException e) {
            //if the file doesn't exist
            System.out.println("Your .txt file does not exist!");
            return;
        }

        //new action for blocks state
        Actions_for_Blocks state = new Actions_for_Blocks();
        //new action for blocks finalState
        Actions_for_Blocks finalState = new Actions_for_Blocks();

        int lineCheck = 0;
        String line;

        System.out.println("Reading the file [" + f + "]");
        System.out.println("Generating states may take a while depending on the number of blocks (currently supports up to 8 blocks).\n");

        //try/catch for reading the file line by line
        //if the input.txt file has a invalid char the program will break and generate error
        try {
            //as long as the line input is not null do the while loop
            while ((line = br.readLine()) != null) {
                //when the line matches the "NUMBER OF BLOCKS" change lineCheck to 1
                if (line.matches("Number of blocks: *")) {
                    lineCheck = 1;
                    continue;
                    //when the line matches the "DEFINE INITIAL STATE" change lineCheck to 2
                } else if (line.matches("Define initial state: *")) {
                    lineCheck = 2;
                    continue;
                    //when the line matches the "DEFINE FINAL STATE" change lineCheck to 3
                } else if (line.matches("Define final state: *")) {
                    lineCheck = 3;
                    continue;
                }
                //now look for lineCheck 1, 2 and 3
                if (!line.equals("")) {
                    switch (lineCheck) {
                        //when "Number of blocks" is match do the following (lineCheck 1)
                        case 1:
                            String[] blocks = line.split("\\s");   //split the NUMBER OF BLOCKS input with spaces
                            for (String string : blocks) {
                                state.makeBlock(string);
                                finalState.makeBlock(string);
                            }
                            break;
                        //when "Define initial blocks" is match do the following (lineCheck 2)
                        case 2:
                            state.analyze(line);
                            break;

                        //and finally when "Define final blocks" is match do the following (lineCheck 3)
                        case 3:
                            finalState.analyze(line);
                            break;
                    }
                }
            }
        //if something is wrong with the file print the following
        } catch (IOException io) {
            System.out.println("The file cannot be read, something went wrong.");
            return;
        }

        //calling the 'Action for Blocks" to make a new object
        Actions_for_Blocks finalSet = new Actions_for_Blocks(state);
        //call the algorithm trail to arrange from initial state to final state
        LinkedList<Relation_for_Blocks> finalTrail = trail(finalSet, finalState);
        //call the finalPrint to print the final results
        finalPrint(finalSet, finalTrail);

        //Final calculation to find the time taken to execute the entire program in a "double" precision
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        double seconds = elapsedTime / 1000.0; //convert milliseconds to seconds
        System.out.println("Total execution time: " + seconds + " sec\n");

        System.out.println("By: Devesh Patel");
        System.out.println("CS 4850 Foundation of Artificial Intelligence, \nWright State University");
;    }
}
