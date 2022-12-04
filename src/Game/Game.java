package Game;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Реализует логику игры.
 */
public class Game {
    /**
     * Рекорд игрока за сессию.
     */
    public static int highScore = 0;

    /**
     * Игровая доска.
     */
    private Board board;

    /**
     * Считывает из консоли число от 1 до right.
     * @param right Правая граница диапазона.
     * @return Число, котороё ввёл пользователь.
     */
    public static int getInput(int right) {
        Scanner scanner = new Scanner(System.in);
        int input;
        while (true) {
            System.out.print("Введите одно число от 1 до " + right + ": ");
            try {
                input = scanner.nextInt();
                if (input < 1 || input > right) {
                    System.out.println("Вы ввели число не из нужного диапазона. Попробуйте ещё раз.");
                } else {
                    break;
                }
            } catch (Exception error) {
                scanner.nextLine();
                System.out.println("Вы ввели не число, попробуйте ещё раз.");
            }
        }
        return input;
    }


    /**
     * Логика игры простого компьютера.
     * @param moves Возможные ходы компьютера.
     * @return Позиция, на которую ходит компьютер.
     */
    private int[] simpleComputer(ArrayList<String> moves) {
        int[] position = new int[]{0, 0};
        double maxScore = 0;
        for (String move : moves) {
            double score = board.SimpleEvaluationFunction(move, Board.Type.Second);
            if (score > maxScore) {
                maxScore = score;
                position = Board.convertToPair(move);
            }
        }
        return position;
    }


    /**
     * Логика игры сложного компьютера.
     * @param moves Возможные ходы компьютера.
     * @return Позиция, на которую ходит компьютер.
     */
    private int[] hardComputer(ArrayList<String> moves) {
        int[] position = new int[]{0, 0};
        double maxScore = -1000;
        for (String move : moves) {
            double score = board.SimpleEvaluationFunction(move, Board.Type.Second);
            int[] currentPosition = Board.convertToPair(move);
            board.putChip(currentPosition[0], currentPosition[1], Board.Type.Second);

            ArrayList<String> playerMoves = board.possibleMoves(Board.Type.First);
            double maxPlayerScore = 0;
            for (String playerMove : playerMoves) {
                double playerScore = board.SimpleEvaluationFunction(playerMove, Board.Type.First);
                maxPlayerScore = Math.max(maxPlayerScore, playerScore);
            }

            if (score - maxPlayerScore > maxScore) {
                maxScore = score - maxPlayerScore;
                position = currentPosition;
            }

            board.CancelMove();
        }
        return position;
    }


    /**
     * Обрабатывает ходы в течение игры.
     * @param isComputer true, если играем с компьютером, false иначе.
     * @param isHard true, если компьютер сложный, false иначе.
     */
    private void gameLogic(boolean isComputer, boolean isHard) {
        Board.Type turn = Board.Type.First;
        int[] position = null;
        while (!board.possibleMoves(turn).isEmpty()) {
            ArrayList<String> moves = board.possibleMoves(turn);
            if (isComputer && turn == Board.Type.Second) {
                if (!isHard) {
                    position = simpleComputer(moves);
                } else {
                    position = hardComputer(moves);
                }
            } else {
                board.printBoard(moves);
                System.out.println("Очередь игрока: " + (turn == Board.Type.First ? "■" : "●"));
                System.out.println("Выберите ход: ");
                for (int i = 0; i < moves.size(); ++i) {
                    System.out.println(i + 1 + ": " + moves.get(i));
                }
                int input;
                if (board.movesHistory.size() > 1) {
                    System.out.println(moves.size() + 1 + ": отменить последний ход");
                    input = getInput(moves.size() + 1);
                } else {
                    input = getInput(moves.size());
                }
                if (input == moves.size() + 1) {
                    if (!isComputer) {
                        board.CancelMove();
                        if (turn == Board.Type.First) {
                            turn = Board.Type.Second;
                        } else {
                            turn = Board.Type.First;
                        }
                    } else {
                        board.CancelMove();
                        board.CancelMove();
                    }
                    continue;
                } else {
                    position = Board.convertToPair(moves.get(input - 1));
                }
            }
            board.putChip(position[0], position[1], turn);
            if (turn == Board.Type.First) {
                turn = Board.Type.Second;
            } else {
                turn = Board.Type.First;
            }
        }
        ArrayList<String> temp = new ArrayList<>();
        board.printBoard(temp);
    }


    /**
     * Стартует игру и обрабатывает окончание игры.
     * @param isComputer true, если играем с компьютером, false иначе.
     * @param isHard true, если компьютер сложный, false иначе.
     */
    public void startGame(boolean isComputer, boolean isHard) {
        board = new Board();
        gameLogic(isComputer, isHard);
        int[] score = board.getScore();
        System.out.println("Игра окончена!\nСчёт: " + score[0] + "\\" + score[1]);
        if (score[0] > score[1]) {
            System.out.println("Выиграл игрок ■.");
        } else if (score[1] > score[0]) {
            System.out.println("Выиграл игрок ●.");
        } else {
            System.out.println("Ничья.");
        }
        highScore = Math.max(highScore, score[0]);
        System.out.println("Рекорд игрока ■ за сессию: " + highScore + "\n\n");
    }
}
