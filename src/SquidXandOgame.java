import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.*;
import javax.sound.sampled.*;
import javax.swing.Timer;
import javazoom.jl.player.Player;
import java.io.FileInputStream;

public class SquidXandOgame extends JFrame {
    CardLayout layout = new CardLayout();
    JPanel mainPanel = new JPanel(layout);

    JPanel welcomePanel, inputPanel, gamePanel;

    JTextField player1Field, player2Field;
    JLabel player1NumLabel, player2NumLabel, turnLabel, timerLabel, scoreLabel;
    JButton[] buttons = new JButton[9];

    ImageIcon xImage, oImage;

    String player1Name = "", player2Name = "";
    int player1Num, player2Num;
    int player1Score = 0, player2Score = 0;
    boolean isPlayer1Turn = true;
    ArrayList<Integer> player1Moves = new ArrayList<>();
    ArrayList<Integer> player2Moves = new ArrayList<>();
    Stack<Integer> moveHistory = new Stack<>();

    Timer turnTimer;
    int timeRemaining = 10;

    Thread themeThread;

    public SquidXandOgame() {
        setTitle("Squid Game X and O");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        xImage = new ImageIcon(new ImageIcon("C:\\Users\\Hp\\Desktop\\icons8-squid-game-triangle-guard-96.png")
                .getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH));
        oImage = new ImageIcon(new ImageIcon("C:\\Users\\Hp\\Desktop\\icons8-squid-game-circle-guard-96.png")
                .getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH));

        setupWelcomeScreen();
        setupNameInputScreen();
        setupGameScreen();

        add(mainPanel);
        layout.show(mainPanel, "welcome");
        setVisible(true);
    }

    void setupWelcomeScreen() {
        welcomePanel = new JPanel(new BorderLayout());
        welcomePanel.setBackground(Color.BLACK);

        JLabel title = new JLabel("SQUID GAME: X AND O", SwingConstants.CENTER);
        title.setFont(new Font("Arial Black", Font.BOLD, 40));
        title.setForeground(Color.PINK);
        welcomePanel.add(title, BorderLayout.NORTH);

        try {
            ImageIcon welcomeIcon = new ImageIcon(new ImageIcon("C:\\Users\\Hp\\Downloads\\pngwing.com.png")
                    .getImage().getScaledInstance(600, 400, Image.SCALE_SMOOTH));
            JLabel img = new JLabel(welcomeIcon);
            img.setHorizontalAlignment(SwingConstants.CENTER);
            welcomePanel.add(img, BorderLayout.CENTER);
        } catch (Exception e) {
            welcomePanel.add(new JLabel("Image missing"), BorderLayout.CENTER);
        }

        JButton startBtn = new JButton("Start Game");
        startBtn.setFont(new Font("Arial Black", Font.BOLD, 20));
        startBtn.setBackground(Color.PINK);
        startBtn.setForeground(Color.BLACK);
        startBtn.addActionListener(e -> {
            layout.show(mainPanel, "input");
            playThemeSong();
        });

        welcomePanel.add(startBtn, BorderLayout.SOUTH);
        mainPanel.add(welcomePanel, "welcome");
    }

    
    void setupNameInputScreen() {
        inputPanel = new JPanel(new GridLayout(8, 1, 10, 10));
        inputPanel.setBackground(new Color(25, 25, 25));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel label1 = new JLabel("Enter Player X Name:");
        label1.setForeground(Color.WHITE);
        inputPanel.add(label1);

        player1Field = new JTextField();
        inputPanel.add(player1Field);

        player1NumLabel = new JLabel();
        player1NumLabel.setForeground(Color.pink);
        inputPanel.add(player1NumLabel);

        JLabel label2 = new JLabel("Enter Player O Name:");
        label2.setForeground(Color.WHITE);
        inputPanel.add(label2);

        player2Field = new JTextField();
        inputPanel.add(player2Field);

        player2NumLabel = new JLabel();
        player2NumLabel.setForeground(Color.pink);
        inputPanel.add(player2NumLabel);

        JButton continueBtn = new JButton("Continue");
        continueBtn.setFont(new Font("Arial Black", Font.BOLD, 16));
        continueBtn.setBackground(Color.PINK);
        continueBtn.setForeground(Color.BLACK);
        continueBtn.addActionListener(e -> {
            player1Name = player1Field.getText().trim().isEmpty() ? "Player 1" : player1Field.getText().trim();
            player2Name = player2Field.getText().trim().isEmpty() ? "Player 2" : player2Field.getText().trim();
            assignNumbers();
            layout.show(mainPanel, "game");
            startTimer();
        });

        inputPanel.add(continueBtn);
        mainPanel.add(inputPanel, "input");
    }

   void assignNumbers() {
        Random rand = new Random();
        do {
            player1Num = rand.nextInt(357) + 100;
            player2Num = rand.nextInt(357) + 100;
        } while (player1Num == player2Num);

        player1NumLabel.setText(player1Name + ", you are now Player " + player1Num);
        player2NumLabel.setText(player2Name + ", you are now Player " + player2Num);

        JOptionPane.showMessageDialog(this, player1Name + ", you are now Player " + player1Num + "\n" +
                player2Name + ", you are now Player " + player2Num);

        turnLabel.setText("Player " + player1Num + "'s Turn");
    }

void setupGameScreen() {
        gamePanel = new JPanel(new BorderLayout());
        gamePanel.setBackground(Color.BLACK);

        JPanel grid = new JPanel(new GridLayout(3, 3, 5, 5));
        grid.setBackground(Color.BLACK);
        grid.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        for (int i = 0; i < 9; i++) {
            final int pos = i + 1;
            buttons[i] = new JButton();
            buttons[i].setBackground(Color.DARK_GRAY);
            buttons[i].setFocusPainted(false);
            buttons[i].addActionListener(e -> handleMove((JButton) e.getSource(), pos));
            grid.add(buttons[i]);
        }

        JPanel topPanel = new JPanel(new GridLayout(3, 1));
        topPanel.setBackground(Color.BLACK);

        turnLabel = new JLabel("Turn", SwingConstants.CENTER);
        turnLabel.setFont(new Font("Arial black", Font.BOLD, 18));
        turnLabel.setForeground(Color.WHITE);

        timerLabel = new JLabel("Time left: 10s", SwingConstants.CENTER);
        timerLabel.setForeground(Color.PINK);

        scoreLabel = new JLabel("Score: 0 - 0", SwingConstants.CENTER);
        scoreLabel.setForeground(Color.LIGHT_GRAY);

        topPanel.add(turnLabel);
        topPanel.add(timerLabel);
        topPanel.add(scoreLabel);

        JButton undo = new JButton("Undo Move");
        undo.setBackground(Color.PINK);
        undo.setForeground(Color.BLACK);
        undo.setFont(new Font("Arial", Font.BOLD, 16));
        undo.addActionListener(e -> undoMove());

        gamePanel.add(topPanel, BorderLayout.NORTH);
        gamePanel.add(grid, BorderLayout.CENTER);
        gamePanel.add(undo, BorderLayout.SOUTH);

        mainPanel.add(gamePanel, "game");
    }
 void handleMove(JButton btn, int pos) {
        playSound("C:\\Users\\Hp\\Desktop\\sound\\click.wav");
        btn.setIcon(isPlayer1Turn ? xImage : oImage);
        btn.setEnabled(false);

        if (isPlayer1Turn) player1Moves.add(pos);
        else player2Moves.add(pos);

        moveHistory.push(pos);
        stopTimer();

        if (checkWin()) {
            playSound("C:\\Users\\Hp\\Desktop\\sound\\569847__danlucaz__gun-shot-1.wav");
            if (isPlayer1Turn) player1Score++; else player2Score++;
            updateScore();
            JOptionPane.showMessageDialog(this, (isPlayer1Turn ? player1Num : player2Num) + " wins!");
            resetBoard();
        } else if (player1Moves.size() + player2Moves.size() == 9) {
            JOptionPane.showMessageDialog(this, "It's a draw!");
            resetBoard();
        } else {
            isPlayer1Turn = !isPlayer1Turn;
            turnLabel.setText((isPlayer1Turn ? player1Name : player2Name) + "'s Turn (" + (isPlayer1Turn ? player1Num : player2Num) + ")");
            startTimer();
        }
    }

    void undoMove() {
        if (!moveHistory.isEmpty()) {
            int pos = moveHistory.pop();
            buttons[pos - 1].setIcon(null);
            buttons[pos - 1].setEnabled(true);
            if (isPlayer1Turn) player2Moves.remove((Integer) pos);
            else player1Moves.remove((Integer) pos);
            isPlayer1Turn = !isPlayer1Turn;
            turnLabel.setText((isPlayer1Turn ? player1Name : player2Name) + "'s Turn (" + (isPlayer1Turn ? player1Num : player2Num) + ")");
            startTimer();
        }
    }

    boolean checkWin() {
        int[][] wins = {
                {1, 2, 3}, {4, 5, 6}, {7, 8, 9},
                {1, 4, 7}, {2, 5, 8}, {3, 6, 9},
                {1, 5, 9}, {3, 5, 7}
        };
        ArrayList<Integer> moves = isPlayer1Turn ? player1Moves : player2Moves;
        for (int[] win : wins) {
            if (moves.contains(win[0]) && moves.contains(win[1]) && moves.contains(win[2])) return true;
        }
        return false;
    }

    void resetBoard() {
        for (JButton btn : buttons) {
            btn.setIcon(null);
            btn.setEnabled(true);
        }
        player1Moves.clear();
        player2Moves.clear();
        moveHistory.clear();
        isPlayer1Turn = true;
        assignNumbers();
    }

    void updateScore() {
        scoreLabel.setText("Score: " + player1Score + " - " + player2Score);
    }

    void startTimer() {
        timeRemaining = 10;
        timerLabel.setText("Time left: " + timeRemaining + "s");

        turnTimer = new Timer(1000, e -> {
            timeRemaining--;
            timerLabel.setText("Time left: " + timeRemaining + "s");
            if (timeRemaining <= 0) {
                stopTimer();
                JOptionPane.showMessageDialog(this, "Time's up! Turn skipped.");
                isPlayer1Turn = !isPlayer1Turn;
                turnLabel.setText((isPlayer1Turn ? player1Name : player2Name) + "'s Turn");
                startTimer();
            }
        });
        turnTimer.start();
    }

    void stopTimer() {
        if (turnTimer != null) turnTimer.stop();
    }
    void playSound(String path) {
        try {
            AudioInputStream audio = AudioSystem.getAudioInputStream(new File(path));
            Clip clip = AudioSystem.getClip();
            clip.open(audio);
            clip.start();
        } catch (Exception e) {
            System.out.println("Sound Error: " + e.getMessage());
        }
    }

    void playThemeSong() {
        themeThread = new Thread(() -> {
            try {
                FileInputStream fis = new FileInputStream("C:\\Users\\Hp\\Desktop\\200LVL vids\\Squid-Game-OST-Pink-Soldiers.mp3");
                Player player = new Player(fis);
                player.play();
            } catch (Exception e) {
                System.out.println("Theme MP3 Error: " + e.getMessage());
            }
        });
        themeThread.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SquidXandOgame::new);
    }
}




