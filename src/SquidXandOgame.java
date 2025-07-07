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
