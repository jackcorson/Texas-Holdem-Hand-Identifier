import java.util.*;

class Cards {
    private Rank rank;
    private Suit suit;

    public Cards(Rank Rank, Suit Suit) {
        rank = Rank;
        suit = Suit;
    }

    public Rank getRank() {
        return rank;
    }
    public Suit getSuit() {
        return suit;
    }

    public enum Rank {
        TWO(2),
        THREE(3),
        FOUR(4),
        FIVE(5),
        SIX(6),
        SEVEN(7),
        EIGHT(8),
        NINE(9),
        TEN(10),
        JACK(11),
        QUEEN(12),
        KING(13),
        ACE(14);

        private int value;

        private Rank(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }

        @Override
        public String toString() {
            return name().toLowerCase();
    }

    }

    public enum Suit {
        HEARTS(1),
        DIAMONDS(2),
        SPADES(3),
        CLUBS(4);

        private int value;

        private Suit(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }

        @Override
        public String toString() {
            return name().toLowerCase();
    }
    }

}

class Deck {
    private ArrayList<Cards> deck = new ArrayList<>();

    public void deckMaker() {
        for (Cards.Rank rank : Cards.Rank.values()) {
            for (Cards.Suit suit : Cards.Suit.values()) {
                deck.add(new Cards(rank, suit));
            }
        }
    }

    public ArrayList<Cards> getCards() {
        return deck;
    }

    public ArrayList<Cards> shuffle() {
        Collections.shuffle(deck);
        return deck;
    }
}

class Player {
    // private int numPlayers;
    private ArrayList<Cards> playerCards = new ArrayList<>();;

    public Player() {}

    public int numCards() {
        return playerCards.size();
    }

    public void giveCards(Cards card) {
        playerCards.add(card);
    }

    public void showCards() {
        System.out.printf("%s of %s and %s of %s\n", playerCards.get(0).getRank(), playerCards.get(0).getSuit(), playerCards.get(1).getRank(), playerCards.get(1).getSuit());
    }

    public Cards getPlayerCard(int i) {
        return playerCards.get(i);
    }

}

class Board {
    private ArrayList<Cards> community = new ArrayList<>();

    public Board() {}

    public void communityCards(Cards card) {
            community.add(card);
    }

    public ArrayList<Cards> getComm() {
        return community;
    }

    public void getCommunityCards() {
        if (community.size() == 3) {    
            for(int i = 0; i < community.size(); i++) {
                System.out.printf("%s of %s\n", community.get(i).getRank(), community.get(i).getSuit());
            }
        }
        else if (community.size() == 4) {
            System.out.printf("%s of %s\n", community.get(3).getRank(), community.get(3).getSuit());
        }
        else {
            System.out.printf("%s of %s\n", community.get(4).getRank(), community.get(4).getSuit());
        }
    }

}

class EvalHand {
    //Used to determine the high cards of each hand
    private ArrayList<Cards> usedCards = new ArrayList<>();
    private ArrayList<Cards> playerCards = new ArrayList<>();
    private ArrayList<Cards> sortedArrayList = new ArrayList<>();
    private ArrayList<Cards> straightFlushHighCard = new ArrayList<>();
    private ArrayList<Cards> fullHouseEval = new ArrayList<>();
    private ArrayList<Cards> flushHighCard = new ArrayList<>();
    private ArrayList<Cards> straightHighCard = new ArrayList<>();
    private ArrayList<Cards> quadsEval = new ArrayList<>();
    private ArrayList<Cards> tripsEval = new ArrayList<>();
    private ArrayList<Cards> twoPairEval = new ArrayList<>();
    private ArrayList<Cards> onePairEval = new ArrayList<>();

    public EvalHand() {}

    public void addCommunityCards(Cards card) {
        usedCards.add(card);
    }

    public void addPlayerCards(Cards card) {
        playerCards.add(card);
    }

    public ArrayList<Cards> getPlayerCards() {
        return playerCards;
    }

    public ArrayList<Cards> getUsedCards() {
        return usedCards;
    }

    public ArrayList<Cards> getSortedArrayList() {
        return sortedArrayList;
    }

    public void sortCommunityAndPlayerCards() {
        int[] sortedArray = new int[7];
        ArrayList<Cards> duplicateArrayList = new ArrayList<>();
        //Making new array for sorting
        for(int i = 0; i < playerCards.size(); i++) {
            usedCards.add(playerCards.get(i));
        }

        for (int i = 0; i < usedCards.size(); i++) {
            sortedArray[i] = usedCards.get(i).getRank().getValue();
        }

        //Sorting the array
        for (int i = 0; i < sortedArray.length - 1; i++) {
            int minIndex = i;
            for (int j = i + 1; j < sortedArray.length; j++) {
                if (sortedArray[j] < sortedArray[minIndex]){
                    minIndex = j;
                }
            }

            int smallerNumber = sortedArray[minIndex];
            sortedArray[minIndex] = sortedArray[i];
            sortedArray[i] = smallerNumber;
        }

        for (int i = 0; i < sortedArray.length; i++) {
            for (int j = 0; j < usedCards.size(); j++) {
                if (usedCards.get(j).getRank().getValue() == sortedArray[i]) {
                    duplicateArrayList.add(usedCards.get(j));
                }
            }
        }

        //Remove duplicates
        for (Cards element : duplicateArrayList) {
            if (!sortedArrayList.contains(element)) {
                sortedArrayList.add(element);
            }
        }
    }

    public int handEvaluation() {
        boolean straightFlush = evalStraightFlush();
        boolean quads = evalQuads();
        boolean fullHouse = evalFullHouse();
        boolean flush = evalFlush();
        boolean straight = evalStraight();
        boolean trips = evalTrips();
        boolean twoPair = evalTwoPair();
        boolean onePair = evalOnePair();
        evalHighCard();

        if (straightFlush == true) {
            System.out.printf("%s high straight flush!\n", straightFlushHighCard.get(0).getRank());
            return 9;
        }
        else if (quads == true) {
            System.out.printf("Four of a kind with %ss!\n", quadsEval.get(0).getRank());
            return 8;
        }
        else if (fullHouse == true) {
            int first = 0;
            int last = 0;

            //two pairs and one three of a kind Ex: (2, 2, 3, 3, 9, 9, 9)
            if (fullHouseEval.size() == 7) {
                for (int j = 0; j < 2; j++) {
                    for (int k = 0; k < fullHouseEval.size(); k++) {
                        //Determining which card rank is the three of a kind and which is the two of a kind.  
                        if (fullHouseEval.get(2).getRank() == fullHouseEval.get(k).getRank() && j == 0) {
                            first++;
                        }
                        else if (fullHouseEval.get(fullHouseEval.size() - 1).getRank() == fullHouseEval.get(k).getRank() && j != 0) {
                            last++;
                        }
                    }
                }
                if (first == 2 && last == 3) {
                    System.out.printf("%ss full of %ss\n", fullHouseEval.get(2).getRank(), fullHouseEval.get(6).getRank());
                }
                else {
                    System.out.printf("%ss full of %ss\n", fullHouseEval.get(6).getRank(), fullHouseEval.get(2).getRank());
                }
            }
            else {
                for (int j = 0; j < 2; j++) {
                    for (int k = 0; k < fullHouseEval.size(); k++) {
                        if (fullHouseEval.get(0).getRank() == fullHouseEval.get(k).getRank() && j == 0) {
                            first++;
                        }
                        else if (fullHouseEval.get(fullHouseEval.size() - 1).getRank() == fullHouseEval.get(k).getRank() && j != 0) {
                            last++;
                        }
                    }
                }
                //If there are two three of a kinds, then the highest rank value comes last in (_ full of _).
                if ((first == 2 && last == 3) || (first == 3 && last == 3)) {
                    System.out.printf("%ss full of %ss\n", fullHouseEval.get(0).getRank(), fullHouseEval.get(fullHouseEval.size() - 1).getRank());
                }
                else if (first == 3 && last == 2) {
                    System.out.printf("%ss full of %ss\n", fullHouseEval.get(fullHouseEval.size() - 1).getRank(), fullHouseEval.get(0).getRank());
                }
            }
            return 7;
        }
        else if (flush == true) {
            System.out.printf("%s high flush!\n", flushHighCard.get(0).getRank());
            return 6;
        }
        else if (straight == true) {
            System.out.printf("%s high straight!\n", straightHighCard.get(0).getRank());
            return 5;
        }
        else if (trips == true) {
            System.out.printf("Three of a kind of %ss!\n", tripsEval.get(0).getRank());
            return 4;
        }
        else if (twoPair == true) {
            if (twoPairEval.size() == 6) {
                System.out.printf("Two pair of %ss and %ss!\n", twoPairEval.get(2).getRank(), twoPairEval.get(4).getRank());
            }
            else {
                System.out.printf("Two pair of %ss and %ss!\n", twoPairEval.get(0).getRank(), twoPairEval.get(3).getRank());
            }
            return 3;
        }
        else if (onePair == true) {
            System.out.printf("Pair of %ss!\n", onePairEval.get(0).getRank());
            return 2;
        }
        else {
            Cards.Rank highCard = Cards.Rank.TWO;
            for (int j = 0; j < sortedArrayList.size(); j++) {
                if (sortedArrayList.get(j).getRank().getValue() > highCard.getValue()) {
                    highCard = sortedArrayList.get(j).getRank();
                }
            }
            System.out.printf("%s high!\n", highCard);
            return 1;
        }
    }

    private boolean evalStraightFlush() {
        boolean straight = false;
        ArrayList<Cards> removeDup = new ArrayList<>();
        int count = 0;
        int net = 0;
        int suit = 0;
        //Removing duplicates to make counting easier
        for (int i = 0; i < sortedArrayList.size(); i++) {
            removeDup.add(sortedArrayList.get(i));
        }
        for (int i = 0; i < removeDup.size(); i++) {
            int countDup = 0;
            for (int j = 0; j < removeDup.size(); j++) {
                if (removeDup.get(i).getRank() == removeDup.get(j).getRank()) {
                    countDup++;
                    if (countDup > 1) {
                        removeDup.remove(j);
                    }
                }
            }
        }
        for (int i = 0; i < removeDup.size() - 1; i++) {
            if (removeDup.get(i).getRank().getValue() == removeDup.get(i + 1).getRank().getValue() - 1 && 
            removeDup.get(i).getSuit() == removeDup.get(i + 1).getSuit()) {
                count++;
                net += removeDup.get(i).getRank().getValue(); //See straighteval fn for net explanation
                suit = removeDup.get(i).getSuit().getValue(); //Determine current suit
                if (count >= 4) {
                    if (straightFlushHighCard.size() == 0) {
                        straightFlushHighCard.add(removeDup.get(i + 1));
                    }
                    else {
                        straightFlushHighCard.remove(0);
                        straightFlushHighCard.add(removeDup.get(i + 1));
                    }
                }
            }
            //If there is a straight with all the same suit values, there is a straight flush
            else if (count == 3 && net == 9 && removeDup.get(removeDup.size() - 1).getRank().getValue() == 14 && 
            removeDup.get(removeDup.size() - 1).getSuit().getValue() == suit) {
                count++;
            }
            else {
                count = 0;
            }
        }

        return straight;
    }

    private boolean evalQuads() {
        boolean quads = false;
        int counter = 0;
        for (int i = 0; i < sortedArrayList.size(); i++) {
            counter = 0;
            for (int j = 0; j < sortedArrayList.size(); j++) {
                if (sortedArrayList.get(i).getRank() == sortedArrayList.get(j).getRank()) {
                    counter++;
                }
            }
            //If four card ranks are the same, there are four of a kind
            if (counter == 4) {
                quads = true;
                quadsEval.add(sortedArrayList.get(i));
                break;
            }
        }
        return quads;
    }

    private boolean evalFullHouse() {
        boolean pair = false;
        boolean trips = false;
        boolean fullHouse = false;
        int tripsCount = 0;
        int counter;
        for (int i = 0; i < sortedArrayList.size(); i++) {
            counter = 0;
            for (int j = 0; j < sortedArrayList.size(); j++) {
                if (sortedArrayList.get(i).getRank() == sortedArrayList.get(j).getRank()) {
                    counter++;
                }
            }
            if (counter == 2) {
                pair = true;
                fullHouseEval.add(sortedArrayList.get(i));
            }
            else if (counter == 3) {
                tripsCount++;
                trips = true;
                fullHouseEval.add(sortedArrayList.get(i));
            }
        }
        /* 
        If there are three of a kind and a different pair, there is a full house.
        Can also have two three of a kinds, so if trips count is 6 and pair is false, 
        then there can still be a full house.
        */
        if ((trips == true && pair == true) || tripsCount == 6) {
            fullHouse = true;
        }
        return fullHouse;
    }

    private boolean evalFlush() {
        boolean flush = false;
        int counterHearts = 0;
        int counterDiamonds = 0;
        int counterSpades = 0;
        int counterClubs = 0;
        //If suits match, increment counter
        for (int i = 0; i < sortedArrayList.size(); i++) {
            if (sortedArrayList.get(i).getSuit().getValue() == 1) {
                counterHearts++;
            }
            else if (sortedArrayList.get(i).getSuit().getValue() == 2) {
                counterDiamonds++;
            }
            else if (sortedArrayList.get(i).getSuit().getValue() == 3) {
                counterSpades++;
            }
            else {
                counterClubs++;
            }
        }
        if (counterHearts >= 5 || counterDiamonds >= 5 || counterSpades >= 5 || counterClubs >= 5) {
            flush = true;
        }
        //hearts, diamonds, spades, clubs

        //Adds cards that are in flush to flushHighCard to determine the rank of the flush.
        if (counterHearts >= 5) {
            for (int i = 0; i < sortedArrayList.size(); i++) {
                if (sortedArrayList.get(i).getSuit().getValue() == 1) {
                    if (flushHighCard.size() == 0) {
                        flushHighCard.add(sortedArrayList.get(i));
                    }
                    else {
                        flushHighCard.remove(0);
                        flushHighCard.add(sortedArrayList.get(i));
                    }
                }
            }
        }
        else if (counterDiamonds >= 5) {
            for (int i = 0; i < sortedArrayList.size(); i++) {
                if (sortedArrayList.get(i).getSuit().getValue() == 2) {
                    if (flushHighCard.size() == 0) {
                        flushHighCard.add(sortedArrayList.get(i));
                    }
                    else {
                        flushHighCard.remove(0);
                        flushHighCard.add(sortedArrayList.get(i));
                    }
                }
            }
        }
        else if (counterSpades >= 5) {
            for (int i = 0; i < sortedArrayList.size(); i++) {
                if (sortedArrayList.get(i).getSuit().getValue() == 3) {
                    if (flushHighCard.size() == 0) {
                        flushHighCard.add(sortedArrayList.get(i));
                    }
                    else {
                        flushHighCard.remove(0);
                        flushHighCard.add(sortedArrayList.get(i));
                    }
                }
            }
        }
        else {
            for (int i = 0; i < sortedArrayList.size(); i++) {
                if (sortedArrayList.get(i).getSuit().getValue() == 4) {
                    if (flushHighCard.size() == 0) {
                        flushHighCard.add(sortedArrayList.get(i));
                    }
                    else {
                        flushHighCard.remove(0);
                        flushHighCard.add(sortedArrayList.get(i));
                    }
                }
            }
        }
        return flush;
    }

    private boolean evalStraight() {
        boolean straight = false;
        ArrayList<Cards> removeDup = new ArrayList<>();
        int count = 0;
        int net = 0;
        //Make identical arraylist
        for (int i = 0; i < sortedArrayList.size(); i++) {
            removeDup.add(sortedArrayList.get(i));
        }
        //Remove duplicate cards for counting purposes
        for (int i = 0; i < removeDup.size(); i++) {
            int countDup = 0;
            for (int j = 0; j < removeDup.size(); j++) {
                if (removeDup.get(i).getRank() == removeDup.get(j).getRank()) {
                    countDup++;
                    if (countDup > 1) {
                        removeDup.remove(j);
                    }
                }
            }
        }

        for (int i = 0; i < removeDup.size() - 1; i++) {
            //If card after current card is one value higher, increment count
            if (removeDup.get(i).getRank().getValue() == removeDup.get(i + 1).getRank().getValue() - 1) {
                count++;
                /* 
                net varaible to evaluate if there is a wheel straight (Ace, 2, 3, 4, 5).
                If net is equal to 9 (counting 2, 3, and 4) then there is possibility of wheel straight.
                */
                net += removeDup.get(i).getRank().getValue();
                if (count >= 4) { 
                    straight = true;
                    if (straightHighCard.size() == 0) {
                        straightHighCard.add(removeDup.get(i + 1));
                    }
                    else {
                        straightHighCard.remove(0);
                        straightHighCard.add(removeDup.get(i + 1));
                    }
                }
            }
            //If net is 9 and the last card (highest value card) is an ace (value == 14), then there is a wheel straight.
            else if (count == 3 && net == 9 && removeDup.get(removeDup.size() - 1).getRank().getValue() == 14) {
                count++;
            }
            else {
                count = 0;
            }
        }

        return straight;
    }

    private boolean evalTrips() {
        boolean trips = false;
        for (int i = 0; i < sortedArrayList.size(); i++) {
            int counter = 0;
            for (int j = 0; j < sortedArrayList.size(); j++) {
                if (sortedArrayList.get(i).getRank() == sortedArrayList.get(j).getRank() && j != i) {
                    counter++;
                }
            }
            /* 
            If current card matches with two other cards (count == 2) and it is
            known there is no full house, then there is three of a kind.
            */
            if (counter == 2) {
                tripsEval.add(sortedArrayList.get(i));
                trips = true;
                break;
            }
        }
        return trips;
    }

    private boolean evalTwoPair() {
        boolean twoPair = false;
        for (int i = 0; i < sortedArrayList.size(); i++) {
            int counter = 0;
            for (int j = 0; j < sortedArrayList.size(); j++) {
                if (sortedArrayList.get(i).getRank() == sortedArrayList.get(j).getRank() && j != i) {
                    counter++;
                }
            }
            //add cards that pair to twoPairEval to be sorted and determine which cards are in the two pair
            if (counter == 1) {
                twoPairEval.add(sortedArrayList.get(i));
            }
        }
        /* 
        Can have two pair where there are two different pairs of cards (count == 4) or where there 
        are three different pairs (count == 4). The latter is still a two pair of the two pairs with the higher ranks
        */
        if (twoPairEval.size() == 4 || twoPairEval.size() == 6) {
            twoPair = true;
        }
        return twoPair;
    }

    private boolean evalOnePair() {
        boolean onePair = false;
        //counter incremented every time two cards have the same rank value
        int counter = 0;
        for (int i = 0; i < sortedArrayList.size(); i++) {
            for (int j = 0; j < sortedArrayList.size(); j++) {
                if (sortedArrayList.get(i).getRank() == sortedArrayList.get(j).getRank() && j != i) {
                    counter++;
                    if (onePairEval.size() == 0) {
                        onePairEval.add(sortedArrayList.get(j));
                    }
                }
            }
        }
        //counter at 2 means two cards in the user's hand and the five cards on the board match (pair)
        if (counter == 2) {
            onePair = true;
        }
        return onePair;
    }

    private boolean evalHighCard() {
        boolean highCard = false;
        int counter = 0;
        for (int i = 0; i < sortedArrayList.size(); i++) {
            for (int j = 0; j < sortedArrayList.size(); j++) {
                if (sortedArrayList.get(i).getRank() == sortedArrayList.get(j).getRank() && !sortedArrayList.get(i).equals(sortedArrayList.get(j))) {
                    counter++;
                }
            }
        }
        //counter at 0 means no two cards in the user's hand and the five cards on the board match (pair)
        if (counter == 0) {
            highCard = true;
        }
        return highCard;
    }
}

public class NoLimitHoldem {
    public static void main(String[] args) {
        boolean keepPlaying = true;

        //Add number of players
        int numPlayers = players();

        while (keepPlaying == true) {
            int dealFromTop = 51;
            ArrayList<Player> player = new ArrayList<>();
            Board board = new Board();
            Deck deck = new Deck();
            deck.deckMaker();
            deck.shuffle();
      
            //Add players to player arraylist
            addPlayers(player, numPlayers);

            //Deals 2 cards to each player and removes thos cards from the deck
            dealFromTop = dealCards(player, numPlayers, deck, dealFromTop);

            System.out.println();

            //Shows player cards before the board comes out
            showCards(player, numPlayers);

            //Burn card
            dealFromTop--; 

            //Add flop cards
            dealFromTop = flop(dealFromTop, board, deck);

            System.out.println();

            //Adds community cards and player cards together for hand evaluation
            addCardsAndEval(player, board);

            //User decides wether or not to keep playing
            keepPlaying = keepPlaying();

        }
    }

    public static int players() {
        Scanner cin = new Scanner(System.in);
        boolean answer;
        int numPlayers;

        System.out.println();

        do {
            answer = true;
            System.out.print("Enter the number of players (Up to 23!) -> ");
            //Gets player amount with exception handling
            if (cin.hasNextInt()) { //Checks if input is a number
                numPlayers = cin.nextInt();
                if (numPlayers < 1 || numPlayers > 23) { //Ensure player amount is valid
                    System.out.println("\nPlease enter a number between 1 and 23\n");
                    answer = false;
                    cin.nextLine();
                }
            }
            else {
                numPlayers = 0;
                cin.nextLine();
                System.out.println("\nPlease enter a number\n");   
                answer = false;
            }
        } while (answer == false);
        
        return numPlayers;
    }

    public static boolean keepPlaying() {
        Scanner cin = new Scanner(System.in);
        boolean keepPlaying = false;
        boolean option;
        //Ask to keep playing or to terminate
        do {
            System.out.print("\nType 'yes' to keep playing and 'no' to terminate -> ");
            String play = cin.nextLine();
            if (play.equals("no")) {
                keepPlaying = false;
                option = true;
            }
            else if (play.equals("yes")) {
                keepPlaying = true;
                option = true;
            }
            else { //exception handling
                System.out.println("\nPlease enter either 'yes' or 'no' with no capital letters");
                option = false;
            }
        } while (option == false);

        return keepPlaying;
    }

    public static void addPlayers(ArrayList<Player> player, int numPlayers) {
        //Adds new players to arraylist
        for (int i = 0; i < numPlayers; i++) {
            player.add(new Player());
        }
    }

    public static int dealCards(ArrayList<Player> player, int numPlayers, Deck deck, int dealFromTop) {
        //Deal cards
        for (int k = 0; k < 2; k++) {  
            for(int j = 0; j < numPlayers; j++) {
                player.get(j).giveCards(deck.getCards().get(dealFromTop));
                dealFromTop--; //Move on to next card
            }  
        }

        return dealFromTop;
    }

    public static void showCards(ArrayList<Player> player, int numPlayers) {
        //Shows player cards without changing them
        for (int i = 0; i < numPlayers; i++) {
            System.out.printf("Player %d has: ", i + 1);
            player.get(i).showCards();
        }
    }

    public static int flop(int dealFromTop, Board board, Deck deck) {
        //Adding cards to community cards
        for (int i = 0; i < 3; i++) {
            board.communityCards(deck.getCards().get(dealFromTop));
            dealFromTop -= 1; //Technically burns a card at end of loop
        }
        System.out.println("\nThe board comes...\n");

        board.getCommunityCards(); //Show flop
        board.communityCards(deck.getCards().get(dealFromTop));
        board.getCommunityCards(); //Show turn
        dealFromTop -= 1; //burn card
        board.communityCards(deck.getCards().get(dealFromTop)); 
        board.getCommunityCards(); //Show river

        return dealFromTop;
    }

    public static void addCardsAndEval(ArrayList<Player> player, Board board) {
        ArrayList<ArrayList<Cards>> allHands = new ArrayList<>();
        //Adds player cards and community cards together for evaluation
        for (int i = 0; i < player.size(); i++) {
            EvalHand handEval = new EvalHand();
            for (int j = 0; j < board.getComm().size() + 1; j++) {
                //Add all cards from the board
                if (handEval.getUsedCards().size() <= 4) {
                    handEval.addCommunityCards(board.getComm().get(j));
                }
                //Add player cards
                else {
                    for (int k = 0; k < 2; k++) {
                        if (k == 0) {
                            Cards card = player.get(i).getPlayerCard(0);
                            handEval.addPlayerCards(card);
                        }
                        else {
                            Cards card1 = player.get(i).getPlayerCard(1);
                            handEval.addPlayerCards(card1);
                        }
                    }
                }
            }
            //Show what each player has when evaluated with the board
            System.out.printf("player %d has: ", i + 1);
            handEval.sortCommunityAndPlayerCards();
            allHands.add(handEval.getSortedArrayList());
            handEval.handEvaluation(); 
        }
    }
}