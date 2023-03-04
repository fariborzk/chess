import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        RegisterMenu registerMenu = RegisterMenu.getInstance();
        registerMenu.run();
    }

    public static class RegisterMenu extends Menu {
        private static RegisterMenu Instance = null;
        private RegisterController controller;
        private RegisterMenu() {
            this.controller = RegisterController.getInstance();
        }
        public static RegisterMenu getInstance() {
            if (RegisterMenu.Instance == null)
                RegisterMenu.setInstance(new RegisterMenu());
            return RegisterMenu.Instance;
        }
        private static void setInstance(RegisterMenu registerMenu) {
            Instance = registerMenu;
        }
        public void run() {
            boolean running = true;
            while (running) {
                running = false;
                String Input = this.getInput();
                String[] InputList = Input.split("[\\s,]+");
                switch (InputList[0]) {
                    case ("register"):
                        this.registerUser(InputList);
                        break;
                    case ("remove"):
                        this.removeUser(InputList);
                        break;
                    case ("login"):
                        this.loginUser(InputList);
                        break;
                    case ("list_users"):
                        this.showList();
                        break;
                    case ("help"):
                        this.getHelp();
                        break;
                    case ("exit"):
                        this.exit();
                        break;
                    default:
                        System.out.println(Message.INVALID_COMMAND);
                        running = true;
                }
            }
            return;

        }
        protected void getHelp() {
            System.out.println("register [username] [password]");
            System.out.println("login [username] [password]");
            System.out.println("remove [username] [password]");
            System.out.println("list_users");
            System.out.println("help");
            System.out.println("exit");
            this.run();
            return;
        }
        private void registerUser(String[] InputList) {
            Message message = this.controller.handleRegister(InputList);
            System.out.println(message);
            this.run();
            return;
        }
        private void loginUser(String[] InputList) {
            Message message = this.controller.handleLogin(InputList);
            System.out.println(message);
            if (message != Message.LOGIN_SUCCESSFUL)
                this.run();
            else
                MainMenu.getInstance().run();
            return;
        }
        private void removeUser(String[] InputList) {
            Message message = this.controller.handleRemove(InputList);
            if (message == Message.REMOVED_SUCCESSFUL)
                System.out.println(message.toString().replaceAll("\\[\\]", InputList[1]));
            else
                System.out.println(message);
            this.run();
            return;
        }
        private void exit(){
            System.out.println("program ended");
            System.exit(0);
        }
        private void showList() {
            Menu.showUsers();
            this.run();
            return;
        }
    }

    public abstract static class Menu{
        private static final Scanner scanner = new Scanner(System.in);
        protected static Scanner getScanner() {
            return Menu.scanner;
        }
        protected String getInput() {
            return Menu.getScanner().nextLine().trim();
        }
        protected static void showUsers() {
            ArrayList<String> users = new ArrayList<>();
            for(User user : Controller.getUsers())
                users.add(user.getUsername());
            Collections.sort(users);
            for (String username : users)
                System.out.println(username);;
        }
        public abstract void run();
        protected abstract void getHelp();
    }

    public static class MainMenu extends Menu {
        private static MainMenu Instance = null;
        private MainController controller;
        private MainMenu() { this.controller = MainController.getInstance(); }
        public static MainMenu getInstance() {
            if (MainMenu.Instance == null)
                MainMenu.setInstance(new MainMenu());
            return MainMenu.Instance;
        }
        private static void setInstance(MainMenu mainMenu) { MainMenu.Instance = mainMenu; }
        public void run() {
            boolean running = true;
            while (running) {
                running = false;
                String Input = this.getInput();
                String[] InputList = Input.split("\\s+");
                switch (InputList[0]) {
                    case ("new_game"):
                        this.start(InputList);
                        break;
                    case ("scoreboard"):
                        this.showScoreboard();
                        break;
                    case ("list_users"):
                        this.showList();
                        break;
                    case ("help"):
                        this.getHelp();
                        break;
                    case ("logout"):
                        this.logout();
                        break;
                    default:
                        System.out.println(Message.INVALID_COMMAND);
                        running = true;
                }
            }
            return;
        }
        protected void getHelp() {
            System.out.println("new_game [username] [limit]");
            System.out.println("scoreboard");
            System.out.println("list_users");
            System.out.println("help");
            System.out.println("logout");
            this.run();
            return;
        }
        private void start(String[] InputList) {
            Message message = this.controller.handleStart(InputList);
            if (message == Message.NEW_GAME_STARTED) {
                String mess = message.toString().replaceAll("first", Controller.getFirstLoggedInUser().getUsername());
                mess = mess.replaceAll("second", Controller.getSecondLoggedInUser().getUsername());
                mess = mess.replaceAll("\\[limit\\]", InputList[2]);
                System.out.println(mess);
                GameMenu.getInstance().run();
            }
            else {
                System.out.println(message);
                this.run();
            }
            return;
        }
        private void showScoreboard() {
            for (User user : Controller.getSortedUsers()) {
                System.out.println(user.getUsername() + " " + user.getScore() + " " + user.getNumOfWins() +
                        " " + user.getNumOfDraws() + " " + user.getNumOfLosses());
            }
            this.run();
            return;
        }
        private void logout() {
            Controller.setFirstLoggedInUser(null);
            System.out.println(Message.LOGOUT_SUCCESSFUL);
            RegisterMenu.getInstance().run();
            return;
        }
        private void showList() {
            Menu.showUsers();
            this.run();
            return;
        }
    }

    public static class GameMenu extends Menu {
        private static GameMenu Instance = null;
        private GameController controller;
        private GameMenu() { this.controller = GameController.getInstance(); }
        public static GameMenu getInstance() {
            if (GameMenu.Instance == null)
                GameMenu.setInstance(new GameMenu());
            return GameMenu.Instance;
        }
        private static void setInstance(GameMenu gameMenu) { GameMenu.Instance = gameMenu; }
        public void run() {
            boolean running = true;
            while (running) {
                running = false;
                String Input = this.getInput();
                String[] InputList = Input.split("[\\s,]");
                switch (InputList[0]) {
                    case ("select"):
                        this.select(InputList);
                        running = true;
                        break;
                    case ("deselect"):
                        this.deselect();
                        running = true;
                        break;
                    case ("move"):
                        this.move(InputList);
                        running = true;
                        break;
                    case ("next_turn"):
                        if (InputList.length == 1)
                            this.nextTurn();
                        else
                            Invalid();
                        running = true;
                        break;
                    case ("show_turn"):
                        if (InputList.length == 1)
                            this.showTurn();
                        else
                            Invalid();
                        running = true;
                        break;
                    case ("undo"):
                        if (InputList.length == 1)
                            this.undo();
                        else
                            Invalid();
                        running = true;
                        break;
                    case ("undo_number"):
                        if (InputList.length == 1)
                            this.undoNumber();
                        else
                            Invalid();
                        running = true;
                        break;
                    case ("show_moves"):
                        if (InputList.length == 1 || (InputList.length == 2 && InputList[1].equals("-all")))
                            this.showMoves(InputList);
                        else
                            Invalid();
                        running = true;
                        break;
                    case ("show_killed"):
                        if (InputList.length == 1 || (InputList.length == 2 && InputList[1].equals("-all")))
                            this.showKilled(InputList);
                        else
                            Invalid();
                        running = true;
                        break;
                    case ("show_board"):
                        if (InputList.length == 1)
                            this.showBoard();
                        else
                            Invalid();
                        running = true;
                        break;
                    case ("help"):
                        if (InputList.length == 1)
                            this.getHelp();
                        else
                            Invalid();
                        running = true;
                        break;
                    case ("forfeit"):
                        if (InputList.length == 1)
                            this.forfeit();
                        else
                            Invalid();
                        running = true;
                        break;
                    default:
                        Invalid();
                        running = true;
                }
            }
            return;
        }
        protected void getHelp() {
            System.out.println("select [x],[y]");
            System.out.println("deselect");
            System.out.println("move [x],[y]");
            System.out.println("next_turn");
            System.out.println("show_turn");
            System.out.println("undo");
            System.out.println("undo_number");
            System.out.println("show_moves [-all]");
            System.out.println("show_killed [-all]");
            System.out.println("show_board");
            System.out.println("help");
            System.out.println("forfeit");
            return;
        }
        private void select(String[] InputList) {
            Message message = this.controller.handleSelect(InputList);
            System.out.println(message);
            return;
        }
        private void showTurn() {
            System.out.println(this.controller.showTurn());

            return;
        }
        private void showMoves(String[] InputList) {
            ArrayList<String> moves = this.controller.getMoves();
            if (InputList.length == 2) {
                for (int i = 0; i < moves.size(); i++) {
                    System.out.println(moves.get(i));
                }
            }
            else {
                int turn = Controller.getGame().getTurn();

                for (int i = 0; i < moves.size(); i++) {
                    if (i % 2 == turn - 1)
                        System.out.println(moves.get(i));
                }
            }
            return;
        }
        private void showKilled(String[] InputList) {
            HashMap<Integer, String> kills = this.controller.getKills();
            if (InputList.length == 2) {
                for (int i = 1; i < kills.size() + 1; i++) {
                    System.out.println(kills.get(i).substring(2));
                }
            }
            else {
                String turn = String.valueOf(Controller.getGame().getTurn());
                for (int i = 1; i < kills.size() + 1; i++) {
                    if (!kills.get(i).substring(0, 1).equals(turn))
                        System.out.println(kills.get(i).substring(2));
                }
            }

            return;
        }
        private void showBoard() {
            Piece[][] board = Controller.getGame().getPieces();
                for (int i = 0; i < 8; i++) {
                    for (int j = 0; j < 8; j++) {
                        if (board[i][j] == null)
                            System.out.print("  |");
                        else
                            System.out.print(board[i][j].getName() + "|");
                    }
                    System.out.println("");
                }
            return;
        }
        private void deselect() {
            Message message = this.controller.handleDeselect();
            System.out.println(message);
            return;
        }
        private void forfeit() {
            String message = this.controller.forfeit();
            System.out.println(message);
            Controller.startNewGame();
            MainMenu.getInstance().run();
            return;
        }
        private void undo() {
            Message message = this.controller.handleUndo();
            System.out.println(message);
            return;
        }
        private void undoNumber() {
            System.out.println(this.controller.showUndoNum());
            return;
        }
        private void move(String[] InputList) {
            Message message = this.controller.handleMove(InputList);
            System.out.println(message);
            return;
        }
        protected void Invalid() {
            System.out.println(Message.INVALID_COMMAND);
            return;
        }
        private void nextTurn() {
            Message message = this.controller.nextTurn();
            if (message == Message.MOVE_YOUR_TURN || message ==  Message.TURNED) {
                System.out.println(message);
            }
            else {
                System.out.println(Message.TURNED);
                if (message == Message.DRAW)
                    System.out.println(message);
                else {
                    String mess = message.toString().replaceAll("\\[username]", Controller.getGame().getWinner().getUsername());
                    mess = mess.replaceAll("\\[black/white]", (Controller.getGame().getWinner() == Controller.getGame().getFirstPlayer() ? "white" : "black"));
                    System.out.println(mess);
                }
                Controller.startNewGame();
                MainMenu.getInstance().run();
            }
            return;
        }

    }

    public static class Rook extends Piece{
        public Rook(boolean IsWhite, int x, int y) {
            super("R", IsWhite, x, y);
        }
        public boolean canMoves(int x, int y) {
            if (x == this.getX())
                return this.canMovesInY(x, y);
            else if (y == this.getY())
                return this.canMovesInX(x, y);
            return false;
        }



    }

    public static class User implements Comparable<User> {
        private static ArrayList<User> users = new ArrayList<>();
        private String username;
        private String password;
        private int numOfWins;
        private int numOfDraws;
        private int numOfLosses;
        private int score;
        public User(String username, String password) {
            this.username = username;
            this.password = password;
            users.add(this);
        }
        public int getScore() { return this.score; }
        public static ArrayList<User> getUsers() { return User.users; }
        public static ArrayList<User> getSortedUsers() {
            Collections.sort(User.users);
            return User.users;
        }
        public static User getUser(String username) {
            for (User user : users) {
                if (user.username.equals(username))
                    return user;
            }
            return null;
        }
        public String getUsername() {
            return this.username;
        }
        public String getPassword() {
            return this.password;
        }
        public int getNumOfWins() {
            return this.numOfWins;
        }
        public int getNumOfDraws() {
            return this.numOfDraws;
        }
        public int getNumOfLosses() {
            return this.numOfLosses;
        }
        public void incrementDraws() {
            this.numOfDraws++;
            this.score++;
        }
        public void incrementLosses() { this.numOfLosses++; }
        public void incrementWins() { this.numOfWins++;}
        public static void removeUser(String username) {
            users.remove(User.getUser(username));
        }
        public int compareTo(User user) {
            if (this.getScore() > user.getScore())
                return -1;
            if (this.getScore() < user.getScore())
                return 1;
            if (this.getNumOfWins() > user.getNumOfWins())
                return -1;
            if (this.getNumOfWins() < user.getNumOfWins())
                return 1;
            if (this.getNumOfDraws() > user.getNumOfDraws())
                return -1;
            if (this.getNumOfDraws() < user.getNumOfDraws())
                return 1;
            if (this.getNumOfLosses() < user.getNumOfLosses())
                return -1;
            if (this.getNumOfLosses() > user.getNumOfLosses())
                return 1;
            return this.getUsername().compareTo(user.getUsername());
        }
        public static void getDraws(User firstUser, User secondUser) {
            firstUser.incrementDraws();
            secondUser.incrementDraws();
        }
        public static void getForfeit(User Winner, User Loser) {
            Winner.score += 2;
            Winner.numOfWins ++;
            Loser.score--;
            Loser.numOfLosses++;
        }
        public static void getWin(User Winner, User Loser) {
            Winner.score += 3;
            Winner.numOfWins ++;
            Loser.numOfLosses++;
        }
    }

    public abstract static class Piece {
        private final String name;
        private final boolean IsWhite;
        private int x;
        private int y;
        public Piece(String name, boolean IsWhite, int x, int y) {
            this.name = name;
            this.IsWhite = IsWhite;
            this.x = x;
            this.y = y;
        }
        public String getName() { return this.name + (IsWhite ? "w" : "b"); }
        public String getIcon() { return this.name; }
        public boolean getColor() { return this.IsWhite; }
        public abstract boolean canMoves(int x, int y);
        public int getX() { return this.x; }
        public int getY() { return this.y; }
        public void setX(int x) { this.x = x; }
        public void setY(int y) { this.y = y; }
        public boolean canHit(int x, int y) {
            if (Game.getGame().getPieces()[y][x] == null)
                return false;
            return true;
        }
        public static int ConvertY(int y){ return 8 - y; }
        protected boolean canMovesInY(int x, int y) {
            if (Game.getGame().getPieces()[y][x] != null && Game.getGame().getPieces()[y][x].getColor() == this.getColor())
                return false;
            for (int i = Math.min(y, this.getY()) + 1; i < Math.max(y, this.getY()); i++) {
                if (Game.getGame().getPieces()[i][x] != null)
                    return false;
            }
            return true;
        }
        protected boolean canMovesInX(int x, int y) {
            if (Game.getGame().getPieces()[y][x] != null && Game.getGame().getPieces()[y][x].getColor() == this.getColor() )
                return false;
            for (int i = Math.min(x, this.getX()) + 1; i < Math.max(x, this.getX()); i++) {
                if (Game.getGame().getPieces()[y][i] != null)
                    return false;
            }
            return true;
        }
        protected boolean canMovesInDiagonal(int x, int y) {
            int c = 1;
            int Y = Math.max(this.getY(), y);
            if (Game.getGame().getPieces()[y][x] != null && Game.getGame().getPieces()[y][x].getColor() == this.getColor() )
                return false;
            for (int i = Math.min(this.getX(), x) + 1; i < Math.max(x, this.getX()); i++) {
                if (Game.getGame().getPieces()[Y - c][i] != null)
                    return false;
                c++;
            }
            return true;
        }
        protected boolean canMoveInSubDiagonal(int x, int y) {
            int c = 1;
            int Y = Math.min(this.getY(), y);
            if (Game.getGame().getPieces()[y][x] != null && Game.getGame().getPieces()[y][x].getColor() == this.getColor() )
                return false;
            for (int i = Math.min(this.getX(), x) + 1; i < Math.max(this.getX(), x); i++) {
                if (Game.getGame().getPieces()[Y + c][i] != null)
                    return false;
                c++;
            }
            return true;
        }
        public void ApplyMAndH(int x, int y, boolean canHit) {
            Game.getGame().setDoesMoved(true);
            Game.getGame().setX(x);
            Game.getGame().setY(y);
            if (canHit)
                Game.getGame().setRemovedPiece(Game.getGame().getPieces()[y][x]);
            Game.getGame().getPieces()[Game.getGame().getSelectedPiece().getY()][Game.getGame().getSelectedPiece().getX()] = null;
            Game.getGame().getPieces()[y][x] = Game.getGame().getSelectedPiece();
            Game.getGame().writeMoveAndKill();
        }
    }

    public static class Queen extends Piece {
        public Queen(boolean IsWhite, int x, int y) {
            super("Q", IsWhite, x, y);
        }
        public boolean canMoves(int x, int y) {
            if (x == this.getX())
                return this.canMovesInY(x, y);
            else if (y == this.getY())
                return this.canMovesInX(x, y);
            else if (Math.abs(x - this.getX()) == Math.abs(y - this.getY()))
                return this.canMovesInDiagonal(x, y) || this.canMoveInSubDiagonal(x, y);
            return false;
        }
    }

    public static class Pawn extends Piece {
        public Pawn(boolean IsWhite, int x, int y) {
            super("P", IsWhite, x, y);
        }
        public boolean canMoves(int x, int y) {
            if (this.getY() == 1 && !this.getColor() && y == 3 && this.getX() == x && Game.getGame().getPieces()[y][x] == null)
                return true;
            else if (this.getY() == 6 && this.getColor() && y == 4 && this.getX() == x && Game.getGame().getPieces()[y][x] == null)
                return true;
            else if (x == this.getX() && ((y - this.getY() == 1 && !this.getColor()) || (y - this.getY() == -1 && this.getColor())) && Game.getGame().getPieces()[y][x] == null)
                return true;
            else if (Math.abs(x - this.getX()) == 1 && y - this.getY() == 1 && !this.getColor() && Game.getGame().getPieces()[y][x] != null && Game.getGame().getPieces()[y][x].getColor())
                return true;
            else if (Math.abs(x - this.getX()) == 1 && y - this.getY() == -1 && this.getColor() && Game.getGame().getPieces()[y][x] != null && !Game.getGame().getPieces()[y][x].getColor())
                return true;
            return false;
        }
    }

    public static class King extends Piece {
        public King(boolean IsWhite, int x, int y) {
            super("K", IsWhite, x, y);
        }
        public boolean canMoves(int x, int y) {
            if (Game.getGame().getPieces()[y][x] != null && this.getColor() == Game.getGame().getPieces()[y][x].getColor())
                return false;
            if (Math.abs(y - this.getY()) < 2 && Math.abs(x - this.getX()) < 2 && Math.abs(y - this.getY()) + Math.abs(x - this.getX()) != 0) {
                return true;
            }
            return false;
        }
    }

    public static class Knight extends Piece {
        public Knight(boolean IsWhite, int x, int y) {
            super("N", IsWhite, x, y);
        }
        public boolean canMoves(int x, int y) {
            if (Game.getGame().getPieces()[y][x] != null && Game.getGame().getPieces()[y][x].getColor() == this.getColor())
                return false;
            if (Math.abs(x - this.getX()) == 2 && Math.abs(y - this.getY()) == 1)
                return true;
            if (Math.abs(x - this.getX()) == 1 && Math.abs(y - this.getY()) == 2)
                return true;
            return false;
        }
    }

    public static class Game {
        private static Game Instance = null;
        private int turn;
        private int limits;
        private final User firstPlayer;
        private final User secondPlayer;
        private Piece selectedPiece;
        private boolean doesMoved;
        private boolean doseKingHit;
        private User Winner;
        private User Loser;
        private HashMap<Integer, String> Hits;
        private ArrayList<String> Moves;
        private Piece[][] pieces;
        private int firstUserUndoes;
        private int secondUserUndoes;
        private int X;
        private int Y;
        private boolean doesAnyUndo;
        private Piece removedPiece;
        public Game(int limits, User firstPlayer, User secondPlayer) {
            this.pieces = new Piece[8][8];
            this.firstPlayer = firstPlayer;
            this.secondPlayer = secondPlayer;
            this.limits = limits;
            this.turn = 1;
            this.firstUserUndoes = 2;
            this.secondUserUndoes = 2;
            this.Moves = new ArrayList<String>();
            this.Hits = new HashMap<Integer, String>();
            this.selectedPiece = null;
            this.setPieces();
            Instance = this;
        }
        public int getX() { return this.X; }
        public int getY() { return this.Y; }
        public void setX(int X) { this.X = X; }
        public void setY(int Y) { this.Y = Y; }
        public static Game getGame() { return Game.Instance; }
        public User getWinner() {
            return Winner;
        }
        public boolean getDoesAnyUndo() { return this.doesAnyUndo; }
        public int getFirstUserUndoes() { return this.firstUserUndoes; }
        public int getSecondUserUndoes() { return this.secondUserUndoes; }
        public User getLoser() { return this.Loser; }
        public static void setGame(Game game) { Game.Instance = game; }
        public Piece[][] getPieces() { return this.pieces; }
        public void  setRemovedPiece(Piece pieces) { this.removedPiece = pieces; }
        public int getLimits() { return this.limits; }
        public Piece getRemovedPiece() { return this.removedPiece; }
        public User getFirstPlayer() { return this.firstPlayer; }
        public User getSecondPlayer() { return  this.secondPlayer; }
        public int getTurn() { return this.turn; }
        public Piece getSelectedPiece() { return selectedPiece; }
        public void setSelectedPiece(Piece piece) { this.selectedPiece = piece; }
        public boolean getDoesMoved() { return this.doesMoved; }
        public void setDoesMoved(boolean bool) { this.doesMoved = bool;}
        public ArrayList<String> getMoves() { return this.Moves; }
        public HashMap<Integer, String> getHits() {return this.Hits; }
        public boolean getDoseKingHit() { return doseKingHit; }
        private void setPieces() {
            pieces[0][0] = new Rook(false, 0, 0);
            pieces[7][0] = new Rook(true, 0, 7);
            pieces[0][1] = new Knight(false, 1, 0);
            pieces[7][1] = new Knight(true, 1, 7);
            pieces[0][2] = new Bishop(false, 2, 0);
            pieces[7][2] = new Bishop(true, 2, 7);
            pieces[0][3] = new Queen(false, 3, 0);
            pieces[7][3] = new Queen(true, 3, 7);
            pieces[0][4] = new King(false, 4, 0);
            pieces[7][4] = new King(true, 4, 7);
            pieces[0][5] = new Bishop(false, 5, 0);
            pieces[7][5] = new Bishop(true, 5, 7);
            pieces[0][6] = new Knight(false, 6, 0);
            pieces[7][6] = new Knight(true, 6, 7);
            pieces[0][7] = new Rook(false, 7, 0);
            pieces[7][7] = new Rook(true, 7, 7);
            for (int i = 0; i < 8; i++) {
                pieces[1][i] = new Pawn(false, i, 1);
                pieces[6][i] = new Pawn(true, i, 6);
            }
        }
        public void setAgainMove() {
            this.limits --;
            this.doesMoved = false;
            this.removedPiece = null;
            this.turn = - this.turn + 3;
            this.selectedPiece = null;
            this.doesAnyUndo = false;
        }
        public void writeMoveAndKill() {
            if (removedPiece == null) {
                this.Moves.add(selectedPiece.getName() + " " + (8 - selectedPiece.getY()) + "," + (selectedPiece.getX() + 1) + " to " + (8 - this.Y) + "," + (this.X + 1));
            } else {
                this.Moves.add(selectedPiece.getName() + " " + (8 - selectedPiece.getY()) + "," + (selectedPiece.getX() + 1) + " to " + (8 - this.Y) + "," + (this.X + 1)
                        + " destroyed " + removedPiece.getName());
                this.Hits.put(Hits.size() + 1, this.turn + " " + removedPiece.getName() + " killed in spot " + (8 - removedPiece.getY()) + "," + (removedPiece.getX() + 1));
                if (removedPiece instanceof King) {
                    this.Winner = (this.turn == 1 ? firstPlayer : secondPlayer);
                    this.Loser = (this.turn == 1 ? secondPlayer : firstPlayer);
                    this.doseKingHit = true;
                }
            }
        }
        public void doTurn() {
            this.pieces[selectedPiece.getY()][selectedPiece.getX()] = null;
            selectedPiece.setX(this.X);
            selectedPiece.setY(this.Y);
            this.pieces[this.Y][this.X] = selectedPiece;
        }
        public void undo() {
            this.Moves.remove(Moves.size() - 1);
            if (removedPiece != null)
                this.Hits.remove(Hits.size());
            this.doesMoved = false;
            if (Instance.turn == 1)
                this.firstUserUndoes--;
            else
                this.secondUserUndoes--;
            this.pieces[selectedPiece.getY()][selectedPiece.getX()] = selectedPiece;
            this.pieces[this.getY()][this.getX()] = removedPiece;
            this.doesAnyUndo = true;
            this.doseKingHit = false;
        }
    }

    public static class Bishop extends Piece {
        public Bishop(boolean IsWhite, int x, int y) {
            super("B", IsWhite, x, y);
        }
        public boolean canMoves(int x, int y) {
            if (Math.abs(x - this.getX()) == Math.abs(y - this.getY()))
                return this.canMovesInDiagonal(x, y) || this.canMoveInSubDiagonal(x, y);
            return false;
        }
    }

    public enum Message {
        REGISTER("register"),
        LOGIN("login"),
        REMOVE("remove"),
        SELECT("select"),
        USERNAME("username format is invalid"),
        PASSWORD("password format is invalid"),
        REGISTER_SUCCESSFUL("register successful"),
        LOGIN_SUCCESSFUL("login successful"),
        NO_USER_EXISTS("no user exists with this username"),
        USER_EXISTS("a user exists with this username"),
        INCORRECT_PASSWORD("incorrect password"),
        INVALID_LIMIT("number should be positive to have a limit or 0 for no limit"),
        SELF("you must choose another player to start a game"),
        REMOVED_SUCCESSFUL("removed [] successfully"),
        NOT_YOUR_OWN("you can only select one of your pieces"),
        NEW_GAME_STARTED("new game started successfully between first and second with limit [limit]"),
        LOGOUT_SUCCESSFUL("logout successful"),
        NO_PIECE_EXIST("no piece on this spot"),
        SELECTED("selected"),
        DESELECTED("deselected"),
        MOVE_YOUR_TURN("you must move then proceed to next turn"),
        TURNED("turn completed"),
        DRAW("draw"),
        Win("player [username] with color [black/white] won"),
        FORFEIT("you have forfeited\nplayer [username] with color [black/white] won"),
        SHOW_TURN("it is player [username] turn with color [white/black]"),
        NO_PIECE_SELECTED("no piece is selected"),
        WRONG_CONDITION("wrong coordination"),
        CANT_UNDO_ANYMORE("you cannot undo anymore"),
        DONT_MOVED("you must move before undo"),
        USED_UNDO("you have used your undo for this turn"),
        UNDO_COMPLETED("undo completed"),
        MOVED_ALREADY("already moved"),
        NOT_HAVE_SELECTED("do not have any selected piece"),
        CANT_MOVE_SPOT("cannot move to the spot"),
        PIECE_HIT("rival piece destroyed"),
        UNDO_NUMBER("you have [n] undo moves"),
        MOVED("moved"),
        INVALID_COMMAND("invalid command");
        private String message;
        Message (String message) {
            this.message = message;
        }
        public boolean UserMatch(String username) {
            return username.matches(USERNAME.message);
        }
        public boolean PassMatch(String password) {
            return password.matches(PASSWORD.message);
        }
        public String toString() { return this.message; }
    }

    public static class RegisterController extends Controller{
        private static RegisterController Instance = null;
        private RegisterController() {}
        public static RegisterController getInstance(){
            if (RegisterController.Instance == null)
                RegisterController.setInstance(new RegisterController());
            return RegisterController.Instance;
        }
        public static void setInstance(RegisterController registerController) {
            RegisterController.Instance = registerController;
        }
        private static boolean doesPasswordMatched(String username, String password) {
            return User.getUser(username).getPassword().equals(password);
        }
        protected static Message validateInput(Message message, String username, String password) {
            if (!isAlphaNumeric(username))
                return Message.USERNAME;
            if (!isAlphaNumeric(password))
                return Message.PASSWORD;
            if (message == Message.REGISTER && Controller.doesUserExists(username))
                return Message.USER_EXISTS;
            if (message == Message.REGISTER)
                return Message.REGISTER_SUCCESSFUL;
            if (!Controller.doesUserExists(username))
                return Message.NO_USER_EXISTS;
            if (!doesPasswordMatched(username, password))
                return Message.INCORRECT_PASSWORD;
            if (message == Message.LOGIN)
                return Message.LOGIN_SUCCESSFUL;
            return Message.REMOVED_SUCCESSFUL;
        }
        public Message handleRegister(String[] InputList) {
            if (InputList.length != 3)
                return Message.INVALID_COMMAND;
            Message message = RegisterController.validateInput(Message.REGISTER,InputList[1],InputList[2]);
            if (message == Message.REGISTER_SUCCESSFUL)
                new User(InputList[1],InputList[2]);
            return message;
        }
        public Message handleLogin(String[] InputList) {
            if (InputList.length != 3)
                return Message.INVALID_COMMAND;
            Message message = RegisterController.validateInput(Message.LOGIN, InputList[1],InputList[2]);
            if (message == Message.LOGIN_SUCCESSFUL)
                Controller.setFirstLoggedInUser(User.getUser(InputList[1]));
            return message;
        }
        public Message handleRemove(String[] InputList) {
            if (InputList.length != 3)
                return Message.INVALID_COMMAND;
            Message message = RegisterController.validateInput(Message.REMOVE,InputList[1],InputList[2]);
            if (message == Message.REMOVED_SUCCESSFUL)
                User.removeUser(InputList[1]);
            return message;
        }
    }

    public static class MainController extends Controller{
        private static MainController Instance = null;
        private MainController() {}
        public static MainController getInstance() {
            if (MainController.Instance == null)
                MainController.setInstance(new MainController());
            return MainController.Instance;
        }

        private static void setInstance(MainController mainController) {
            MainController.Instance = mainController;
        }
        public Message validateInput(String username, String limit) {
            if(!Controller.isDigitNumeric(limit))
                return Message.INVALID_COMMAND;
            if (!Controller.isAlphaNumeric(username))
                return Message.USERNAME;
            if (Integer.parseInt(limit) < 0)
                return Message.INVALID_LIMIT;
            if (username.equals(Controller.getFirstLoggedInUser().getUsername()))
                return Message.SELF;
            if (!doesUserExists(username))
                return Message.NO_USER_EXISTS;
            return Message.NEW_GAME_STARTED;
        }
        public Message handleStart(String[] InputList) {
            if (InputList.length != 3)
                return Message.INVALID_COMMAND;
            Message message = this.validateInput(InputList[1],InputList[2]);
            if (message == Message.NEW_GAME_STARTED) {
                Controller.setSecondLoggedInUser(User.getUser(InputList[1]));
                Controller.setGame(new Game((Integer.parseInt(InputList[2]) == 0 || InputList[2].length() >= 9 ? Integer.MAX_VALUE : Integer.parseInt(InputList[2])),
                        Controller.getFirstLoggedInUser(), Controller.getSecondLoggedInUser()));
            }
            return message;
        }

    }

    public static class GameController extends Controller{
        private static GameController Instance = null;
        public static GameController getInstance() {
            if (GameController.Instance == null)
                GameController.setInstance(new GameController());
            return GameController.Instance;
        }
        private static void setInstance(GameController gameController) { GameController.Instance = gameController; }
        private Message validateSelectInput (int x, int y) {
            if (x < 0 || x > 7 || y < 0 || y > 7)
                return Message.WRONG_CONDITION;
            if (Controller.getGame().getPieces()[y][x] == null)
                return Message.NO_PIECE_EXIST;
            if (Controller.getGame().getPieces()[y][x] != null && (Controller.getGame().getPieces()[y][x].getColor() && Controller.getGame().getTurn() == 2) ||
                    (!Controller.getGame().getPieces()[y][x].getColor() && Controller.getGame().getTurn() == 1))
                return Message.NOT_YOUR_OWN;
            return Message.SELECTED;
        }
        public Message validateMoveInput(int x, int y) {
            if (Controller.getGame().getDoesMoved())
                return Message.MOVED_ALREADY;
            if (x < 0 || x > 7 || y < 0 || y > 7)
                return Message.WRONG_CONDITION;
            if (Controller.getGame().getSelectedPiece() == null)
                return Message.NOT_HAVE_SELECTED;
            if (!Controller.getGame().getSelectedPiece().canMoves(x, y))
                return Message.CANT_MOVE_SPOT;
            boolean canHit = false;
            if (Controller.getGame().getSelectedPiece().canHit(x, y))
                canHit = true;
            Controller.getGame().getSelectedPiece().ApplyMAndH(x, y, canHit);
            if (canHit)
                return Message.PIECE_HIT;
            return Message.MOVED;
        }
        public Message validateUndo() {
            int turn = Controller.getGame().getTurn();
            if ((turn == 1 ? Controller.getGame().getFirstUserUndoes() : Controller.getGame().getSecondUserUndoes()) == 0)
                return Message.CANT_UNDO_ANYMORE;
            if (!Controller.getGame().getDoesMoved())
                return Message.DONT_MOVED;
            if (Controller.getGame().getDoesAnyUndo())
                return Message.USED_UNDO;
            return Message.UNDO_COMPLETED;
        }
        public Message handleSelect(String[] InputList) {
            if (InputList.length != 3 )
                return Message.INVALID_COMMAND;
            if (InputList[1].length() > 1 || InputList[2].length() > 1)
                return Message.WRONG_CONDITION;
            int x = Integer.parseInt(InputList[2]) - 1;
            int y = 8 - Integer.parseInt(InputList[1]);
            Message message = validateSelectInput(x, y);
            if (message == Message.SELECTED)
                Controller.getGame().setSelectedPiece(Controller.getGame().getPieces()[y][x]);
            return message;
        }
        public Message handleDeselect() {
            if (Controller.getGame().getSelectedPiece() == null)
                return Message.NO_PIECE_SELECTED;
            Controller.getGame().setSelectedPiece(null);
            return Message.DESELECTED;
        }
        public Message handleMove(String[] InputList) {
            if (InputList.length != 3 )
                return Message.INVALID_COMMAND;
            if (InputList[1].length() > 1 || InputList[2].length() > 1)
                return Message.WRONG_CONDITION;
            int x = Integer.parseInt(InputList[2]) - 1;
            int y = 8 - Integer.parseInt(InputList[1]);
            return validateMoveInput(x, y);
        }
        public Message nextTurn() {
            if (!Controller.getGame().getDoesMoved())
                return Message.MOVE_YOUR_TURN;
            Game.getGame().doTurn();
            Game.getGame().setAgainMove();
            if (Controller.getGame().getLimits() == 0) {
                User.getDraws(Controller.getGame().getFirstPlayer(), Controller.getGame().getSecondPlayer());
                return Message.DRAW;
            }
            if (Controller.getGame().getDoseKingHit()) {
                User.getWin(Controller.getGame().getWinner(), Controller.getGame().getLoser());
                return Message.Win;
            }
            return Message.TURNED;
         }
         public Message handleUndo() {
            Message message = validateUndo();
            if (message == Message.UNDO_COMPLETED)
                Game.getGame().undo();
            return message;
         }
         public String showTurn() {
            String message = Message.SHOW_TURN.toString().replaceAll("\\[username]",
                    (Controller.getGame().getTurn() == 1 ? Controller.getGame().getFirstPlayer().getUsername() : Controller.getGame().getSecondPlayer().getUsername()));
            message = message.replaceAll("\\[white/black]", (Controller.getGame().getTurn() == 1 ? "white" : "black"));
            return message;
         }
         public String showUndoNum() {
            int n = (Controller.getGame().getTurn() == 1 ? Controller.getGame().getFirstUserUndoes() : Controller.getGame().getSecondUserUndoes());
            return Message.UNDO_NUMBER.toString().replaceAll("\\[n]", String.valueOf(n));
         }
         public ArrayList<String> getMoves() { return Controller.getGame().getMoves(); }
        public HashMap<Integer, String> getKills() { return Controller.getGame().getHits(); }
        public Piece[][] getBoard() { return Controller.getGame().getPieces(); }

        public String forfeit() {
            User Winner = (Controller.getGame().getTurn() == 2 ? Controller.getGame().getFirstPlayer() : Controller.getGame().getSecondPlayer());
            User Loser = (Controller.getGame().getTurn() == 1 ? Controller.getGame().getFirstPlayer() : Controller.getGame().getSecondPlayer());
            String message = Message.FORFEIT.toString().replaceAll("\\[username]", Winner.getUsername());
            message = message.replaceAll("\\[black/white]", (Controller.getGame().getTurn() == 1 ? "black" : "white"));
            User.getForfeit(Winner, Loser);
            return message;
        }
    }

    public abstract static class Controller {
        private static Game game;
        private static User FirstLoggedInUser = null;
        private static User SecondLoggedInUser = null;
        public static User getFirstLoggedInUser() {
            return Controller.FirstLoggedInUser;
        }
        public static User getSecondLoggedInUser() { return Controller.SecondLoggedInUser; }
        public static void setFirstLoggedInUser(User user) {
            Controller.FirstLoggedInUser = user;
        }
        public static void setSecondLoggedInUser(User user) { Controller.SecondLoggedInUser = user; }
        public static ArrayList<User> getUsers() {
            return User.getUsers();
        }
        public static boolean doesUserExists(String username) {
            return User.getUser(username) != null;
        }
        public static boolean isAlphaNumeric(String Input) {
            return Input.matches("\\w+");
        }
        public static Game getGame() {
            return game;
        }
        public static boolean isDigitNumeric(String limit) {
            return limit.matches("-?\\d+");
        }
        public static void startNewGame() {
            Game.setGame(null);
            game = null;
            setSecondLoggedInUser(null);

        }
        public static ArrayList<User> getSortedUsers() { return User.getSortedUsers(); }
        public static void setGame(Game game) { Controller.game = game; }
    }
}
