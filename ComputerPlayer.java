//A class to play against a human 
//It will probably play like the biggest idiot known to man kind, so be nice. 


//Player takes a AbaloneGraph Object for construction.
//For each turn, the updated graph should be passed to the Player to keep track of its nodes and positions
//Calling the get move method provides the nodes for the next computer move
import java.util.ArrayList;

public class ComputerPlayer {
    private ArrayList<Node> computerNodes;
    private ArrayList<Node> edgePieces;
    private AbaloneGraph graph;

    public ComputerPlayer(AbaloneGraph g)
    {
        computerNodes = new ArrayList<Node>(14);
        edgePieces = new ArrayList<Node>(computerNodes.size());
        graph = g;
        for(int i=0; i<91; i++)
        {
            if(g.getNode(i).getColor()==2)
            {
                computerNodes.add(g.getNode(i));
                if(g.getNode(i).bordersEdge() && !g.getNode(i).isEdge())
                {
                    edgePieces.add(g.getNode(i));
                }
            }      
        }
        edgePieces.trimToSize();
        // System.out.println("edge piece size 1: " + edgePieces.size());
    }

    //Takes the current state of graph and iterates throguh adding its players to an ArrayList and its edgePieces to and Array List
    public void updatePlayers(AbaloneGraph g)
    {
        computerNodes.clear();
        edgePieces.clear();
        for(int i=0; i<91; i++)
        {
            if(g.getNode(i).getColor()==2)
            {
                computerNodes.add(g.getNode(i));
                if(g.getNode(i).bordersEdge() && !g.getNode(i).isEdge())
                {
                    edgePieces.add(g.getNode(i));
                }
            }   
        }
        computerNodes.trimToSize();
        edgePieces.trimToSize();
        // System.out.println("comp piece size 2: " + computerNodes.size());
        // System.out.println("edge piece size 2: " + edgePieces.size());
    }

    //Returns the nodes and direction for the next move
    //The system to determine moves is to prioritise edge pieces and move them from the edge
    //The moves are ranked as follows:
    // 1. If its an edge piece, borders a white piece, and can push the white piece
    // 2. If its an edge piece, borders a white piece, and can move away from the white piece
    // 3. If its an edge piece and can move 
    // 4. Any other single piece that can move 
    //The int[] returned is ordered as follows: [first node ID from graph, destination piece ID from graph, direction]
    public int[] getMove()
    {
        // System.out.println("comp piece size 3: " + computerNodes.size());
        // System.out.println("edge piece size 3: " + edgePieces.size());
        ArrayList<Node> priority = new ArrayList<Node>(edgePieces.size());
        ArrayList<Node> secondary = new ArrayList<Node>(edgePieces.size());
        //Adds edge pieces that border a white piece or can inline move into priority
        for(int i=0; i<edgePieces.size(); i++)
        {
            boolean priorityNode = false;
            for(int j=1; j<12; j+=2)
            {
                Node piece1 = edgePieces.get(i);
                Node piece2 = edgePieces.get(i).getSibling(j);
                int direction = graph.getDirection(piece1, piece2);
                if(piece1 != null && piece2.getColor()==1)
                    priorityNode = true;
                else if(graph.destination(piece1, piece2, direction)!=null)
                    priorityNode = true;
            }
            if(priorityNode)
                priority.add(edgePieces.get(i));
            else 
                secondary.add(edgePieces.get(i));
        }
        priority.trimToSize();
        secondary.trimToSize();
        System.out.println("prio size 1: " + priority.size());
        System.out.println("sec size 1: " + secondary.size());
        //Removes nodes from the priority list unless it can push a white piece or can move
        for(int i=0; i<priority.size(); i++)
        {
            boolean remove = true;
            for(int j=1; j<12; j+=2)
            {
                Node piece1 = priority.get(i);
                Node piece2 = priority.get(i).getSibling(j);
                if((piece2 != null) && (piece2.getColor()==0) && !piece2.isEdge())
                    remove = false;
                else if((piece2 != null) && (piece2.getColor()==1) && !piece2.isEdge())
                {
                    int direction = graph.getDirection(piece1, piece2);
                    Node dest = graph.destination(piece1, piece2, direction);
                    if(dest!=null)
                        remove=false;
                }
                else if(piece2!=null && piece2.getColor()==2 && !piece2.isEdge())
                {
                    int direction = graph.getDirection(piece1, piece2);
                    Node dest = graph.destination(piece1, piece2, direction);
                    if(dest!=null)
                        remove=false;
                }
            }

            if(remove)
                priority.remove(i);
        }

        for(int i=0; i<priority.size(); i++)
        {
            System.out.print(priority.get(i) + " ");
        }
        System.out.println("prio size 2: " + priority.size());
        System.out.println("sec size 2: " + secondary.size());

        //Goes through the priority nodes and returns a move based on the ranking system
        if(priority.size()!=0)
        {
            //Checks to see if any of the priority nodes can push a white node or inline move away from edge and returns possible move
            for(int i=0; i<priority.size(); i++)
            {
                for(int j=1; j<12; j+=2)
                {   
                    Node piece1 = priority.get(i);
                    Node piece2 = piece1.getSibling(j);
                    int dir = graph.getDirection(piece1, piece2);
                    if((piece2 != null) && (piece2.getColor()==1))
                    {
                        int direction = j;
                        Node dest = graph.destination(priority.get(i), priority.get(i).getSibling(j), direction);
                        if(dest!=null)
                        {
                            System.out.println("return 1");
                            int[] move = {priority.get(i).getID(), dest.getID(), direction};
                            return move;

                        }
                    }
                    else if(graph.destination(piece1, piece2, dir)!=null)
                    {
                        int[] move = {piece1.getID(), graph.destination(piece1, piece2, dir).getID(), dir};
                    }
                }
            }   
            //Finds which node can move and moves it 
            for(int i=0; i<priority.size(); i++)
            {
                for(int j=1; j<12; j+=2)
                {
                    if(priority.get(i).getSibling(j)!=null && priority.get(i).getSibling(j).getColor()==0 && !priority.get(i).getSibling(j).isEdge())
                    {
                        int direction = graph.getDirection(priority.get(i), priority.get(i).getSibling(j));
                        Node dest = graph.destination(priority.get(i), priority.get(i).getSibling(j), direction);
                        int[] move = {priority.get(i).getID(), dest.getID(), direction};
                        System.out.println("return 2");
                        return move;
                    }
                }
            }  
            
            System.out.println("return 3");
        }
        //If there are no priority nodes, select the first node from the secondary nodes and move it in a direction it can go
        else if(secondary.size()!=0)
        {
            System.out.println("in second statement");
            for(int i=0; i<secondary.size(); i++)
            {
                for(int j=1; j<12; j+=2)
                {
                    if(secondary.get(i).getSibling(j)!=null && secondary.get(i).getSibling(j).getColor()==0 && !secondary.get(i).getSibling(j).isEdge())
                    {
                        int direction = j;
                        System.out.println("direction: " + direction);
                        Node dest = graph.destination(secondary.get(i), secondary.get(i).getSibling(j), direction);
                        int[] move = {secondary.get(i).getID(), dest.getID(), direction};
                        return move;
                    }
                }
            }

        }
        //If there are no priority nodes, the first available single move for a non edge piece 
        else 
        {
            System.out.println("in else");
            for(int i=0; i<computerNodes.size(); i++)
            {
                for(int j=1; j<12; j+=2)
                {
                    if(computerNodes.get(i).getSibling(j)!=null && computerNodes.get(i).getSibling(j).getColor()==0)
                    {
                        int direction = graph.getDirection(computerNodes.get(i), computerNodes.get(i).getSibling(j));
                        Node dest = graph.destination(computerNodes.get(i), computerNodes.get(i).getSibling(j), direction);
                        int[] move = {computerNodes.get(i).getID(), dest.getID(), direction};
                        return move;
                    }
                }
            }
        }

        //If no move was found, return all -1
        int[] move = {-1, -1, -1};
        return move;

    }



    public String toString()
    {
        String s = "";
        for(int i=0; i<computerNodes.size(); i++)
        {
            if(computerNodes.get(i)!=null)
                s += computerNodes.get(i).getID() + " ";
        }

        return s;
    }

}
