package dk.easv.bll.bot;

import dk.easv.bll.field.IField;
import dk.easv.bll.game.IGameState;
import dk.easv.bll.move.IMove;
import dk.easv.bll.move.Move;

import javax.swing.plaf.nimbus.State;
import java.util.Objects;


public class LocalPrioritisedListBot implements IBot {


    private static final String BOTNAME = "Local Prio ListBot";
    // Moves {row, col} in order of preferences. {0, 0} at top-left corner
    protected int[][] preferredMoves = {
            {1, 1}, //Center
            {0, 0}, {2, 2}, {0, 2}, {2, 0},  //Corners ordered across
            {0, 1}, {2, 1}, {1, 0}, {1, 2}}; //Outer Middles ordered across

    /**
     * Makes a turn. Edit this method to make your bot smarter.
     * A bot that uses a local prioritised list algorithm, in order to win any local board,
     * and if all boards are available for play, it'll run a on the macroboard,
     * to select which board to play in.
     *
     * @return The selected move we want to make.
     */
    @Override
    public IMove doMove(IGameState state) {
        //Find macroboard to play in
        for (int[] move : preferredMoves)
        {
            if(state.getField().getMacroboard()[move[0]][move[1]].equals(IField.AVAILABLE_FIELD))
            {
                //find move to play
                for (int[] selectedMove : preferredMoves)
                {
                    int x = move[0]*3 + selectedMove[0];
                    int y = move[1]*3 + selectedMove[1];
                    if(state.getField().getBoard()[x][y].equals(IField.EMPTY_FIELD))
                    {
                        return new Move(x,y);
                    }
                }
            }
        }

        //NOTE: Something failed, just take the first available move I guess!
        return state.getField().getAvailableMoves().get(0);
    }

    @Override
    public String getBotName() {
        return BOTNAME;
    }

    private int minimax(IGameState state, char[][] grid, int depth, Boolean isMax) {
        String[][] board = state.getField().getBoard();
        int score = evaluate(board, grid);

        // If Maximizer has won the game
        // return his/her evaluated score
        if (score == 10) {
            return score;
        }

        // If Minimizer has won the game
        // return his/her evaluated score
        if (score == -10) {
            return score;
        }

        // If there are no more moves and
        // no winner then it is a tie
        if (hasMovesLeft(grid) == false) {
            return 0;
        }

        // If this maximizer's move
        if (isMax) {
            int best = -1000;

            // Traverse all cells
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    // Check if cell is empty
                    if (grid[i][j] == ' ') {
                        // Make the move
                        grid[i][j] = this.player;

                        // Call minimax recursively and choose
                        // the maximum value
                        best = Math.max(best, minimax(board, grid, depth + 1, false));

                        // Undo the move
                        grid[i][j] = ' ';
                    }
                }
            }
            return best;
        }
        // If this minimizer's move
        else {
            int best = 1000;

            // Traverse all cells
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    // Check if cell is empty
                    if (grid[i][j] == ' ') {
                        // Make the move
                        grid[i][j] = board.getOpposingPlayer(this.player);

                        // Call minimax recursively and choose
                        // the minimum value
                        best = Math.min(best, minimax(board, grid, depth + 1, true));

                        // Undo the move
                        grid[i][j] = ' ';
                    }
                }
            }
            return best;
        }
    }

    /*public IMove bestMove(IGameState state) {
        String[][] board = state.getField().getBoard();
        // AI to make its turn
        int bestScore = -999999;
        int[] move;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                // Is the spot available?
                if (Objects.equals(board[i][j], IField.EMPTY_FIELD)) {
                    int score = minimax(board, 0, false);
                    board[i][j] = '';
                    if (score > bestScore) {
                        bestScore = score;
                        move = new int[]{i, j};
                    }
                }
            }
        }

    }*/

    private int evaluate(String[][] board, char[][] grid) {
        // Check for X or O victory.
        if (checkForWin(this.player, grid)) {
            return 10;
        } else if (checkForWin(board.getOpposingPlayer(this.player) ,grid)) {
            return -10;
        } else {
            return 0;
        }


    }
    public boolean checkForWin(char player, char[][] grid){
        if (grid[0][0] == player && grid[0][1] == player && grid[0][2] == player) {return true;}  //check rows
        if (grid[1][0] == player && grid[1][1] == player && grid[1][2] == player) {return true;}
        if (grid[2][0] == player && grid[2][1] == player && grid[2][2] == player) {return true;}
        if (grid[0][0] == player && grid[1][0] == player && grid[2][0] == player) {return true;}  //check columns
        if (grid[0][1] == player && grid[1][1] == player && grid[2][1] == player) {return true;}
        if (grid[0][2] == player && grid[1][2] == player && grid[2][2] == player) {return true;}
        if (grid[0][0] == player && grid[1][1] == player && grid[2][2] == player) {return true;}  //check diagonals
        if (grid[0][2] == player && grid[1][1] == player && grid[2][0] == player) {return true;}
        return false;
    }

    static Boolean hasMovesLeft(char board[][]) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == ' ') {
                    return true;
                }
            }
        }
        return false;
    }
}