import Game.Game;

public class Main {
    /**
     * Точка входа.
     * @param args Аргументы командной строки.
     */
    public static void main(String[] args) {
        Game game = new Game();
        System.out.println("Добро пожаловать в игру Реверси!");
        while (true) {
            System.out.println("""
                    Выберите тип игры:
                        1. Игра с компьютером (новичок)
                        2. Игра с компьютером (профи)
                        3. Игра вдвоём
                        4. Выход из игры
                    """);

            int input = Game.getInput(4);
            switch (input) {
                case 1:
                    System.out.println("Вы выбрали режим игры \"Игра с компьютером (новичок)\".");
                    game.startGame(true, false);
                    break;
                case 2:
                    System.out.println("Вы выбрали режим игры \"Игра с компьютером (профи)\".");
                    game.startGame(true, true);
                    break;
                case 3:
                    System.out.println("Вы выбрали режим игры \"Игра вдвоём\".");
                    game.startGame(false, false);
                    break;
                case 4:
                    return;
            }
        }
    }
}