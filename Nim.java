import java.awt.*;
import javax.swing.*;
import java.io.File;
import javax.imageio.ImageIO;
import java.awt.event.*;

public class Nim extends JPanel implements ActionListener{

	private static int WIN_WIDTH = 500, WIN_HEIGHT = 730;

	private static JPanel inputPanel = new JPanel();
	private final Font INPUT_PANEL_FONT = new Font("Times New Roman", Font.BOLD, 22);

	private JLabel promptLabel;
	private JTextField inputField;
	private JButton enter, enter2; //first enter get piles, enter2 gets amount
	private final Color BACKGROUND = Color.BLACK;
	private final Color FOREGROUND = Color.YELLOW;
	private int currentPlayer = 1;

	private Image coin;
	private final String IMAGE_PATH = "coin.jpg";
	private int[] piles;
	private int pileIndex;
	private final int TOTAL_PILES = 3;
	private int amount;
	
	public Nim()
	{
		setBackground(BACKGROUND);
		inputPanel.setLayout(new GridBagLayout() );
		inputPanel.setBackground(BACKGROUND);

		initGame();
	}

	public void initGame()
	{
		piles = new int[]{3,4,5};
		setUpInputPanel(); //the bottom panel
		loadImage();
	}

	public void setUpInputPanel()
	{
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(0,0,40,10);

		promptLabel = new JLabel("Player " + currentPlayer + " pick a pile:");
		promptLabel.setFont(INPUT_PANEL_FONT);
		promptLabel.setForeground(FOREGROUND);
		gbc.gridx = 0;
		gbc.gridy = 0;
		inputPanel.add(promptLabel, gbc);

		inputField = new JTextField(3);
		gbc.insets = new Insets(0,0,40,50);
		inputField.setFont(INPUT_PANEL_FONT);
		inputField.setForeground(FOREGROUND);
		inputField.setBackground(BACKGROUND);
		gbc.gridx = 1;
		gbc.gridy = 0;
		inputPanel.add(inputField, gbc);

		enter = new JButton("ENTER");
		enter.addActionListener(this);
		enter.setFont(INPUT_PANEL_FONT);
		enter.setBackground(BACKGROUND);
		enter.setForeground(FOREGROUND);
		gbc.gridx = 2;
		gbc.gridy = 0;
		inputPanel.add(enter, gbc);
	}

	public void changeInputPrompt()
	{
		String pileChosen = inputField.getText().toUpperCase();
		inputPanel.removeAll();
		setUpInputPanel();
		inputPanel.remove(enter);	
		
		promptLabel.setText("How much from pile " + pileChosen + ": ");
	
		inputPanel.repaint();

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(0,0,40,50);

		enter2 = new JButton("Enter");
		enter2.addActionListener(this);
		enter2.setFont(INPUT_PANEL_FONT);
		enter2.setBackground(BACKGROUND);
		enter2.setForeground(FOREGROUND);
		gbc.gridx = 2;
		gbc.gridy = 0;
		inputPanel.add(enter2, gbc);
	}

	public void loadImage()
	{
		try{
			coin = ImageIO.read(new File(IMAGE_PATH) );
		}
		catch(Exception ex){
			System.out.println("Image failed to load");
		}
	}

	public void actionPerformed(ActionEvent event)
	{

		if(event.getSource() == enter)
		{
			String pileChosen = inputField.getText().toUpperCase();

			if(isValidPile(pileChosen) )
				changeInputPrompt();
			else
				inputField.setForeground(Color.RED);
		}
		else if(event.getSource() == enter2)
		{
			String str = inputField.getText();
			if(isValidAmount(str) )
			{
				takeAwayCoins();
				if( !isGameOver())
					nextTurn();
			}
			else 
				inputField.setForeground(Color.RED);
		}
	}

	public void nextTurn()
	{
		currentPlayer = (currentPlayer == 1) ? 2 : 1;
		inputPanel.removeAll();
		setUpInputPanel();
		inputPanel.validate();
		inputPanel.repaint();
	}

	public void endGame(Graphics g2)
	{
		inputPanel.removeAll();
		inputPanel.repaint();
		g2.setFont(new Font("Times New Roman", Font.BOLD, 60) );
		g2.drawString("Player " + currentPlayer + " wins! ", 50, 300);
		g2.setFont(new Font("Times New Roman", Font.BOLD, 30) );
		g2.drawString("You took the last one", 100, 400);
	}

	public boolean isGameOver()
	{
		int total = 0;
		for(int i = 0; i < TOTAL_PILES; i++)
			total += piles[i];

		return (total == 0);
	}

	public void takeAwayCoins()
	{
		piles[pileIndex] -= amount;
		repaint();
	}
	public boolean isValidAmount(String str)
	{
		try{
			amount = Integer.parseInt(str);
		}
		catch(NumberFormatException ex){
			System.out.println("Not a number");
			return false;
		}

		boolean valid = true;

		if(amount <= 0)
			valid = false;

		if(piles[pileIndex] < amount)
			valid = false;

		return valid;
	}

	public boolean isValidPile(String pileChosen)
	{
		boolean valid = true;

		if(pileChosen.equals("A"))
		{
			if(piles[0] <= 0)
				valid = false;
			else
				pileIndex = 0;
		}
		else if(pileChosen.equals("B"))
		{
			if(piles[1] <= 0)
				valid = false;
			else
				pileIndex = 1;
		}
		else if(pileChosen.equals("C"))
		{
			if(piles[2] <= 0)
				valid = false;
			else
				pileIndex = 2;
		}
		else
			valid = false;

		return valid;
	}

	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
		
		g2.setColor(FOREGROUND);
		if(!isGameOver())
		{	
			g2.setFont(new Font("serif", Font.PLAIN, 30) );
			g2.drawString("A", 100, 50);
			g2.drawString("B", 250, 50);
			g2.drawString("C", 400, 50);

			drawCoins(g2);
		}
		else
			endGame(g2);
	}

	public void drawCoins(Graphics g2)
	{
		int pileXPos = 50;
		int pileYPos = 490;
		int seperationY = 110;
		int seperationX = 150;
		

		for(int i = 0; i < TOTAL_PILES; i++)
		{
			drawPile(g2, piles[i], pileXPos, pileYPos, seperationY);
			pileXPos += seperationX;
		}
	}

	public void drawPile(Graphics g2, int amount, int xPos, int yPos, int sepY)
	{
		for(int i = 0; i < amount; i++)
		{
			g2.drawImage(coin, xPos, yPos, this);
			yPos -= sepY;
		}
	}

	public static void main(String[] args)
	{
		JFrame win = new JFrame("NIM");
		win.setSize(WIN_WIDTH, WIN_HEIGHT);
		win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		win.add(inputPanel, BorderLayout.SOUTH);
		win.add(new Nim());
		win.setLocationRelativeTo(null);
		win.setResizable(false);
		win.setVisible(true);
	}
}