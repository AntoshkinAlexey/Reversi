package Game;

import java.util.ArrayList;

/**
 * Реализует игровую доску.
 */
public class Board {
    /**
     * История ходов на доске. Сохраняется всё поле.
     */
    public ArrayList<Enum[][]> movesHistory;

    /**
     * Тип хода или фишки на клетке:
     * First - первый игрок;
     * Second - второй игрок;
     * No - клетка пустая.
     */
    enum Type {
        First,
        Second,
        No
    }

    /**
     * Размер поля.
     */
    private static final int SIZE = 8;

    /**
     * Клетки поля.
     */
    private Enum[][] cells;

    /**
     * Конструктор поля. Создаёт поле SIZE * SIZE, сохраняет его в истории игры.
     */
    Board() {
        cells = new Enum[SIZE][SIZE];
        for (int i = 0; i < SIZE; ++i) {
            for (int j = 0; j < SIZE; ++j) {
                cells[i][j] = Type.No;
            }
        }
        cells[3][3] = Type.First;
        cells[4][4] = Type.First;
        cells[3][4] = Type.Second;
        cells[4][3] = Type.Second;
        movesHistory = new ArrayList<>();
        Enum[][] temp = new Enum[SIZE][SIZE];
        for (int i = 0; i < SIZE; ++i) {
            for (int j = 0; j < SIZE; ++j) {
                temp[i][j] = cells[i][j];
            }
        }
        movesHistory.add(temp);
    }


    /**
     * Оценка хода.
     * @param cell Клетка, в которую ходят.
     * @param turn Очередь хода.
     * @return Очки за ход.
     */
    public double SimpleEvaluationFunction(String cell, Type turn) {
        int[] position = convertToPair(cell);
        double score = 0;
        if (position[0] == 0 || position[0] + 1 == SIZE) {
            score += 0.4;
        }
        if (position[1] == 0 || position[1] + 1 == SIZE) {
            score += 0.4;
        }
        ArrayList<String> repaint = cellRepaint(position[0], position[1], turn);
        for (String move : repaint) {
            position = convertToPair(move);
            score += 1;
            if (position[0] == 0 || position[0] + 1 == SIZE || position[1] == 0 || position[1] + 1 == SIZE) {
                score += 1;
            }
        }
        return score;
    }


    /**
     * Вывод границы поля.
     */
    private void printBorder() {
        System.out.print("   ");
        for (int i = 0; i < SIZE * 6 + 1; ++i) {
            System.out.print('=');
        }
        System.out.println();
    }


    /**
     * Вывод всей доски на экран.
     * @param moves Возможные ходы.
     */
    public void printBoard(ArrayList<String> moves) {
        for (int i = 0; i < 20; ++i) {
            System.out.println();
        }
        for (int i = 0; i < SIZE; ++i) {
            printBorder();
            System.out.print(SIZE - i + "  |");
            for (int j = 0; j < SIZE; ++j) {
                if (cells[i][j] == Type.First) {
                    System.out.print("  ■");
                } else if (cells[i][j] == Type.Second) {
                    System.out.print("  ●");
                } else if (moves.contains(convertToStr(i, j))) {
                    System.out.print("  ◌");
                } else {
                    System.out.print("   ");
                }
                System.out.print("  |");
            }
            System.out.println();
        }
        printBorder();
        System.out.print("   ");
        for (char letter = 'a'; letter <= 'h'; ++letter) {
            System.out.print("   " + letter + "  ");
        }
        System.out.println();
    }


    /**
     * Вычисление возможных ходов.
     * @param turn Кто ходит.
     * @return Возможные ходы игрока turn.
     */
    public ArrayList<String> possibleMoves(Type turn) {
        ArrayList<String> moves = new ArrayList<>();
        for (int i = 0; i < SIZE; ++i) {
            for (int j = 0; j < SIZE; ++j) {
                if (cells[i][j] == Type.No && !cellRepaint(i, j, turn).isEmpty()) {
                    moves.add(convertToStr(i, j));
                }
            }
        }
        return moves;
    }


    /**
     * Совершает ход.
     * @param x Номер строки хода.
     * @param y Номер столбца хода.
     * @param turn Кто ходит.
     */
    public void putChip(int x, int y, Type turn) {
        ArrayList<String> repainting = cellRepaint(x, y, turn);
        for (String cell : repainting) {
            int[] position = convertToPair(cell);
            cells[position[0]][position[1]] = turn;
        }
        cells[x][y] = turn;
        Enum[][] temp = new Enum[SIZE][SIZE];
        for (int i = 0; i < SIZE; ++i) {
            for (int j = 0; j < SIZE; ++j) {
                temp[i][j] = cells[i][j];
            }
        }
        movesHistory.add(temp);
    }


    /**
     * Отмена последнего хода. Если ходов нет, ничего не отменяется.
     */
    public void CancelMove() {
        if (movesHistory.size() > 1) {
            movesHistory.remove(movesHistory.size() - 1);
            for (int i = 0; i < SIZE; ++i) {
                for (int j = 0; j < SIZE; ++j) {
                    cells[i][j] = movesHistory.get(movesHistory.size() - 1)[i][j];
                }
            }
        }
    }


    /**
     * Конвертация клетки из строкового представления в числа.
     * @param cell Строковое представление клетки в 1-нумерации.
     * @return Координаты клетки в 0-нумерации.
     */
    public static int[] convertToPair(String cell) {
        return new int[]{SIZE - ((int) cell.charAt(1) - '0'), cell.charAt(0) - 'a'};
    }


    /**
     * Конвертация клетки из числового представления в строковое.
     * @param x Номер строки клетки в 0-нумерации.
     * @param y Номер столбца клетки в 0-нумерации.
     * @return Строковое представление клетки в 1-нумерации.
     */
    public static String convertToStr(int x, int y) {
        return Character.toString('a' + y) + Character.toString('0' + SIZE - x);
    }


    /**
     * Проверка клетки на то, принадлежит ли она другому игроку.
     * @param x Номер строки клетки.
     * @param y Номер столбца клетки.
     * @param turn Кто ходит.
     * @return true, если в клетка принадлежит другому игроку, false иначе.
     */
    private boolean anotherCell(int x, int y, Type turn) {
        return cells[x][y] != Type.No && cells[x][y] != turn;
    }


    /**
     * Находит клетки, которые будут перекрашены в случае текущего хода.
     * @param x Номер строки клетки.
     * @param y Номер столбца клетки.
     * @param turn Кто ходит.
     * @return Список клеток, которые будут перекрашены.
     */
    private ArrayList<String> cellRepaint(int x, int y, Type turn) {
        ArrayList<String> repainting = new ArrayList<>();
        final int[] plusX = new int[]{0, 0, 1, -1, 1, 1, -1, -1};
        final int[] plusY = new int[]{1, -1, 0, 0, -1, 1, -1, 1};
        for (int i = 0; i < 8; ++i) {
            int curX = x + plusX[i];
            int curY = y + plusY[i];
            ArrayList<String> tempPaint = new ArrayList<>();
            while (curX >= 0 && curY >= 0 && curX < SIZE && curY < SIZE && anotherCell(curX, curY, turn)) {
                tempPaint.add(convertToStr(curX, curY));
                curX += plusX[i];
                curY += plusY[i];
            }
            if (curX >= 0 && curY >= 0 && curX < SIZE && curY < SIZE && cells[curX][curY] == turn) {
                repainting.addAll(tempPaint);
            }
        }
        return repainting;
    }


    /**
     * Подсчёт очков.
     * @return Количество очков первого игрока и второго игрока в виде массива из двух элементов.
     */
    public int[] getScore() {
        int first = 0;
        int second = 0;
        for (int i = 0; i < SIZE; ++i) {
            for (int j = 0; j < SIZE; ++j) {
                if (cells[i][j] == Type.First) {
                    ++first;
                } else if (cells[i][j] == Type.Second) {
                    ++second;
                }
            }
        }
        return new int[]{first, second};
    }
}
